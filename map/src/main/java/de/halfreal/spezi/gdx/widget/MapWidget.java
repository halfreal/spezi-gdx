package de.halfreal.spezi.gdx.widget;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.GdxRuntimeException;

import de.halfreal.spezi.gdx.actions.GeoActions;
import de.halfreal.spezi.gdx.framework.AbstractScreen;
import de.halfreal.spezi.gdx.framework.ExtendedController;
import de.halfreal.spezi.gdx.framework.GdxChangeListener;
import de.halfreal.spezi.gdx.framework.ScreenWidget;
import de.halfreal.spezi.gdx.framework.SpeziGame;
import de.halfreal.spezi.gdx.framework.TimingHelper;
import de.halfreal.spezi.gdx.sector.GeoHelper;
import de.halfreal.spezi.gdx.sector.SectorHelper;
import de.halfreal.spezi.gdx.sector.SectorId;
import de.halfreal.spezi.gdx.system.Assets;
import de.halfreal.spezi.gdx.system.CacheHelper;
import de.halfreal.spezi.gdx.view.TileMap;
import de.halfreal.spezi.gdx.view.TileMap.MapController;
import de.halfreal.spezi.gdx.view.TileMap.TileProvider;
import de.halfreal.spezi.gdx.view.TileMap.TileWrap;
import de.halfreal.spezi.gdx.widget.MapWidget.MapWidgetController;

public class MapWidget extends
		ScreenWidget<MapWidgetController, MapWidgetModel> {

	public static class MapWidgetController extends
			ExtendedController<MapWidgetModel> {

		private ThreadPoolExecutor executor;

		public MapWidgetController(MapWidgetModel model, SpeziGame framework) {
			super(model, framework);

			executor = (ThreadPoolExecutor) Executors
					.newFixedThreadPool(MAP_THREAD_POOL_SIZE);

			executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {

				@Override
				public void rejectedExecution(Runnable arg0,
						ThreadPoolExecutor arg1) {
					log.error("rejected task", arg1);
				}
			});

		}

		public void clean() {
			executor.purge();
			executor.shutdownNow();
		}

		public void enableDimmer(boolean dimmer) {
			getModel().setDimmer(dimmer);
		}

		public void execute(Runnable task) {
			executor.execute(wrapTask(task));
		}

		@Override
		public void update() {
		}

		private Runnable wrapTask(final Runnable task) {
			return new Runnable() {

				@Override
				public void run() {
					try {
						// framework.getNetworkModel().incrementCounter();
						task.run();
					} catch (Exception ignored) {
						log.error("Could not execute task: {} ", ignored);
					} finally {
						// framework.getNetworkModel().decrementCounter();
					}
				}
			};
		}

	}

	public static class SectorChangedEvent extends ChangeEvent {
		private SectorId id;

		public SectorChangedEvent(SectorId id) {
			this.id = id;
		}

		public SectorId getId() {
			return id;
		}
	}

	private static final int FPS_THRESHOLD = 15;

	private static Logger log = LoggerFactory.getLogger(MapWidget.class);
	public static final int MAP_THREAD_POOL_SIZE = 2;

	private static final long TILE_LOADING_TIME_OFFEST = 30000;
	private static String URL = "http://c.tile.openstreetmap.org/";
	private Queue<TileWrap> currentlyWatchedTiles;
	protected Map<String, Long> faildRequests;
	private float[] geoLock;
	private boolean lock;
	private int lockEdgeWidth;
	private SectorId lockId;
	private int lockZoom;
	private TileMap map;
	private ActorGestureListener moveListener;
	private boolean moving;
	private Matrix4 oldProjection;
	private Map<String, Boolean> oldRequests;
	private Rectangle scissor;
	private Map<String, SoftReference<Texture>> textureMap;
	private Map<String, TileWrap> tileWrapMap;
	private float[] toGeo;
	private boolean zoomEnabled;

	public MapWidget(MapWidgetController controller,
			AbstractScreen<?, ?> screen, String url, float width, float height) {
		super(controller, screen);
		setWidth(width);
		setHeight(height);

		setTouchable(Touchable.enabled);

		if (url != null && url.length() > 0) {
			URL = url;
		}

		// should be collections.newSetFromMap but this is only available since
		// Android-9
		oldRequests = new ConcurrentHashMap<String, Boolean>();
		faildRequests = new ConcurrentHashMap<String, Long>();
		textureMap = new ConcurrentHashMap<String, SoftReference<Texture>>();
		tileWrapMap = new ConcurrentHashMap<String, TileMap.TileWrap>();
		currentlyWatchedTiles = new ConcurrentLinkedQueue<TileWrap>();
		toGeo = new float[2];
		geoLock = new float[2];

	}

	protected boolean canZoom(boolean zoomIn) {

		if (!zoomEnabled) {
			return false;
		}

		if (lock) {
			if (zoomIn && map.getZoom() <= lockZoom) {
				return true;
			} else if (!zoomIn && map.getZoom() >= lockZoom) {
				return true;
			}
			return false;
		}

		return true;
	}

	private void checkTasks() {
		for (TileWrap tileWrap : currentlyWatchedTiles) {
			if (requestTile(tileWrap)) {
				return;
			}
		}
	}

	public void createMap() {
		TileProvider tileProvider = new TileProvider() {

			@Override
			public void requestTiles(java.util.List<TileWrap> tiles) {
				enqueInCurrentWatchedTiles(tiles);
			}

			@Override
			public Texture tileTexture(String tileKey) {
				return getTileTexture(tileKey);
			}

			@Override
			public TileWrap tileWrap(String tileKey) {
				return getTileWrap(tileKey);
			}
		};

		MapController mapController = new MapController() {

			@Override
			public boolean isMoving() {
				return MapWidget.this.isMoving();
			}

		};

		map = new TileMap(tileProvider, mapController, getWidth(), getHeight());
		map.setYmargin(0);
	}

	@Override
	public void dispose() {
		for (SoftReference<Texture> textureRef : textureMap.values()) {
			if (textureRef != null && textureRef.get() != null) {
				textureRef.get().dispose();
			}
		}
		textureMap.clear();
		controller.clean();

	}

	private void downloadTile(final String urlPath, final TileWrap tileWrap) {

		final FileHandle fileHandle = CacheHelper.cachedFileHandle(urlPath);

		log.debug("Downloading Tile: {} , file does not exist: {}", urlPath,
				fileHandle.path());

		HttpRequest request = new HttpRequest(HttpMethods.GET);
		request.setUrl(urlPath);
		HttpResponseListener httpResponseListener = new HttpResponseListener() {

			@Override
			public void failed(Throwable t) {

			}

			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				BufferedInputStream bufferedInputStream = null;
				FileOutputStream fileOutputStream = null;

				try {

					bufferedInputStream = new BufferedInputStream(
							httpResponse.getResultAsStream());

					File tempFile;
					tempFile = fileHandle.file();
					tempFile.createNewFile();

					fileOutputStream = new FileOutputStream(tempFile);
					byte data[] = new byte[1024];
					int count;

					while ((count = bufferedInputStream.read(data, 0, 1024)) != -1) {
						fileOutputStream.write(data, 0, count);
					}

					tileWrap.fileHandle = new FileHandle(tempFile);
					tileWrapMap.put(tileWrap.tileKey, tileWrap);
					// loadTexture(tileWrap.tileKey, fileHandle);
					faildRequests.remove(tileWrap.tileKey);

				} catch (FileNotFoundException e) {
					log.error("File not found! {} ", urlPath, e);
				} catch (IOException e) {
					oldRequests.remove(tileWrap.tileKey);
					faildRequests.put(tileWrap.tileKey,
							System.currentTimeMillis());
					log.error("Could not load: {} ", urlPath, e);
				} finally {
					try {
						if (fileOutputStream != null) {
							fileOutputStream.close();
						}
						if (bufferedInputStream != null) {
							bufferedInputStream.close();
						}
					} catch (IOException e) {
						log.error("Could not close streams! ", e);
					}
					getController().update();
				}
			}
		};
		Gdx.net.sendHttpRequest(request, httpResponseListener);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		checkTasks();

		if (oldProjection == null) {
			oldProjection = getStage().getCamera().combined;
		}
		ScissorStack.pushScissors(scissor);
		super.draw(batch, parentAlpha);
		ScissorStack.popScissors();
		batch.setProjectionMatrix(oldProjection);
	}

	protected void enqueInCurrentWatchedTiles(List<TileWrap> tiles) {
		currentlyWatchedTiles.clear();
		currentlyWatchedTiles.addAll(tiles);
	}

	public TileMap getMapView() {
		return map;
	}

	@Override
	public float getPrefHeight() {
		return getHeight();
	}

	@Override
	public float getPrefWidth() {
		return getWidth();
	}

	protected Texture getTileTexture(String tileKey) {
		SoftReference<Texture> softReference = textureMap.get(tileKey);
		if (softReference == null) {
			return null;
		} else if (softReference.get() == null) {
			textureMap.remove(tileKey);
			return null;
		} else {
			return softReference.get();
		}
	}

	protected TileWrap getTileWrap(String tileKey) {
		return tileWrapMap.get(tileKey);
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		if (touchable && getTouchable() != Touchable.enabled) {
			return null;
		}
		Actor hit = super.hit(x, y, touchable);
		if (hit != null && hit != this) {
			return hit;
		} else {
			return x >= 0 && x < getMapView().getWidth() && y >= 0
					&& y < getMapView().getHeight() ? this : null;
		}
	}

	public void initMap() {
		createMap();

		int zoom = getController().loadInt("zoom", Integer.MIN_VALUE);
		float x = getController().loadFloat("x", Float.MIN_VALUE);
		float y = getController().loadFloat("y", Float.MIN_VALUE);

		initMap(x, y, zoom);
	}

	/**
	 * After setting all Actor specific Values init the Map
	 */
	private void initMap(final float lastX, final float lastY,
			final int lastZoom) {

		map.addAction(Actions.run(new Runnable() {

			@Override
			public void run() {

				if (lastZoom != Integer.MIN_VALUE) {
					map.setZoom(lastZoom);
				}
				if (lastX != Integer.MIN_VALUE) {
					map.setX(lastX);
				}
				if (lastY != Integer.MIN_VALUE) {
					map.setY(lastY);
				}

			}
		}));

		addActor(map);
		scissor = new Rectangle(getX(), getY(), getWidth(), getHeight());
		moveListener = new ActorGestureListener() {

			private boolean direction;
			private boolean doProgressChange;
			private boolean first = true;
			private float oldMapX;
			private float oldMapY;
			private Vector2 screenMiddlePoint = new Vector2();
			private float STEP = Assets.dip(0.6f);
			private float steps;
			private Float tempInititialDistance;
			private float[] toGeo = new float[2];
			private float[] toPoint = new float[2];

			@Override
			public void pan(InputEvent event, float x, float y, float deltaX,
					float deltaY) {

				if (lock) {
					float xOnScreen = map.getXOnScreen(lockId, lockEdgeWidth)
							+ lockEdgeWidth / 2;
					float yOnScreen = map.getYOnScreen(lockId) + lockEdgeWidth
							/ 2;

					float newX = map.getX();
					float newY = map.getY();

					if ((xOnScreen > 0 && deltaX < 0)
							|| (xOnScreen < getWidth() && deltaX > 0)) {
						newX = map.getX() + deltaX;
					}

					if ((yOnScreen > 0 && deltaY < 0)
							|| (yOnScreen < getHeight() && deltaY > 0)) {
						newY = map.getY() + deltaY;
					}

					map.setX(newX);
					map.setY(newY);

				} else {
					map.setX(map.getX() + deltaX);
					map.setY(map.getY() + deltaY);

				}
			}

			@Override
			public void pinch(InputEvent event, Vector2 initialPointer1,
					Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {

				screenMiddlePoint.set(pointer1).add(pointer2).mul(0.5f);

			}

			@Override
			public void tap(InputEvent event, float x, float y, int count,
					int button) {

				if (count == 2 && canZoom(true)) {
					if (first) {
						first = false;
						return;
					}
					GeoHelper.toLatLng(map.screenToImageCoordX(x),
							map.screenToImageCoordY(y), map.getZoom(), toGeo);
					map.addAction(Actions.parallel(
							GeoActions.moveTo(toGeo[0], toGeo[1], 0.3f),
							GeoActions.zoomTo(map.getZoom() + 1, 0.3f)));

					event.cancel();
				}
				super.tap(event, x, y, count, button);
			}

			@Override
			public void touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				oldMapX = map.getX();
				oldMapY = map.getY();
				moving = true;
				steps = 0;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				moving = false;

				if (oldMapX != map.getX() || oldMapY != map.getY()) {
					// TODO Animations must fire a change Event as well
					fire(new ChangeEvent());
				}

				if (steps != 0) {
					getMapView().resetZoom();
				}
				steps = 0;
			}

			@Override
			public void zoom(InputEvent event, float initialDistance,
					float distance) {

				log.debug("initial: " + initialDistance + ", distance: "
						+ distance);

				boolean directionOut = initialDistance > distance;

				if (!canZoom(!directionOut)) {
					return;
				}

				if (directionOut != direction) {
					steps = 0;
					tempInititialDistance = distance;
				}

				if (tempInititialDistance != null) {
					initialDistance = tempInititialDistance;
				}

				if (Math.abs(initialDistance - distance) - (steps * STEP) > STEP) {
					steps++;
					doProgressChange = true;
				}

				if (doProgressChange) {
					doProgressChange = false;
					if (directionOut) {
						getMapView().zoomOutStep();
					} else {
						getMapView().zoomInStep();
					}
				}

				direction = directionOut;

			}
		};

		addCaptureListener(moveListener);
		addListener(new InputListener() {

			@Override
			public boolean scrolled(InputEvent event, float x, float y,
					int amount) {
				if (!canZoom(amount < 0)) {
					return false;
				}

				if (amount > 0) {
					getMapView().zoomOutStep();
				} else {
					getMapView().zoomInStep();
				}

				getMapView().resetZoom();

				return true;
			}

		});

		map.setYmargin(Assets.dip(200));
	}

	public void initMap(SectorId id) {

		createMap();

		float edgeWidth = SectorHelper.getRelativeSectorEdgeWidth(id.z,
				id.z + 1);
		Vector2 position = new Vector2(SectorHelper.getMapX(id, id.z + 1)
				+ edgeWidth / 2, SectorHelper.getMapY(id, id.z + 1) + edgeWidth
				/ 2);

		initMap(map.toMapX(position.x), map.toMapY(position.y), id.z + 1);
	}

	@Override
	public void initModelListeners() {
		super.initModelListeners();
		listen(MapWidgetModel.Keys.DIMMER, new GdxChangeListener<Boolean>() {

			@Override
			public void changed(Boolean oldValue, Boolean newValue) {
				if (getModel().getDimmer()) {
					map.addAction(Actions.color(new Color(1, 1, 1, 0.5f),
							TimingHelper.DIALOG_FADE));
				} else {
					map.addAction(Actions.color(new Color(1, 1, 1, 1),
							TimingHelper.DIALOG_FADE));
				}
			}
		});
	}

	public boolean isMoving() {
		return moving || map.getActions().iterator().hasNext();
	}

	public boolean isZoomEnabled() {
		return zoomEnabled;
	}

	public boolean loadindPossible() {
		return !isMoving() && Gdx.graphics.getFramesPerSecond() > FPS_THRESHOLD;
	}

	protected void loadTexture(final String tileKey, final FileHandle fileHandle) {

		log.debug("MapContainer", "Loading tile in soft map: " + tileKey);
		try {
			final Texture tileTexture = new Texture(fileHandle) {
				@Override
				protected void finalize() throws Throwable {

					log.debug("MapContainer", "Disposing unused Tile");
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							dispose();
							try {
								super.finalize();
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					});

				};
			};

			textureMap.put(tileKey, new SoftReference<Texture>(tileTexture));
		} catch (Throwable re) {
			log.error("MapContainer", "Could not load pixmap: " + tileKey, re);
			faildRequests.put(tileKey, System.currentTimeMillis());
		}
	}

	public void lockToSectorId(SectorId id, boolean noAnimationn) {
		if (id != null) {
			lockZoom = id.z + 1;
			lockEdgeWidth = SectorHelper.getRelativeSectorEdgeWidth(id.z,
					lockZoom);
			Vector2 lockPosition = new Vector2(SectorHelper.getMapX(id,
					lockZoom) + lockEdgeWidth / 2, SectorHelper.getMapY(id,
					lockZoom) + lockEdgeWidth / 2);

			GeoHelper.toLatLng(lockPosition.x, lockPosition.y, lockZoom,
					geoLock);

			lock = true;
			lockId = id;

			if (noAnimationn) {
				map.setZoom(lockZoom);
				map.setPosition(map.toMapX(lockPosition.x),
						map.toMapY(lockPosition.y));
			} else {
				zoomToSectorId(id);
			}
		} else {
			lock = false;
		}
	}

	public void moveToSector(SectorId id) {
		float edgeWidth = SectorHelper.getRelativeSectorEdgeWidth(id.z + 1,
				id.z + 1);
		Vector2 position = new Vector2(SectorHelper.getMapX(id, id.z + 1)
				+ edgeWidth / 2, SectorHelper.getMapY(id, id.z + 1) + edgeWidth
				/ 2);

		map.setZoom(id.z + 1);
		map.setPosition(map.toMapX(position.x), map.toMapY(position.y));
	}

	@Override
	protected void onHide() {
		super.onHide();
		storeCurrentLocation();
	}

	@Override
	protected void onShow(Stage stage, Skin skin) {
		stage.addTouchFocus(moveListener, this, this, 0, 0);
		super.onShow(stage, skin);
	}

	private void removeFile(String url) {
		FileHandle findFile = CacheHelper.findFile(url);
		findFile.delete();
	}

	/**
	 * 
	 * @param tile
	 * @return when started a loading action
	 */
	protected boolean requestTile(TileWrap tile) {
		if (tile == null) {
			return false;
		}

		Texture texture = getTileTexture(tile.tileKey);

		if (texture != null) {
			return false;
		}

		Long faildTime = faildRequests.get(tile.tileKey);
		if (faildTime == null) {
			faildTime = 0l;
		}
		if (System.currentTimeMillis() - faildTime < TILE_LOADING_TIME_OFFEST) {
			return false;
		} else {
			faildRequests.remove(tile.tileKey);
		}

		if (tile.fileHandle != null) {
			if (loadindPossible()) {
				loadTexture(tile.tileKey, tile.fileHandle);
				return true;
			} else {
				return false;
			}
		}

		if (oldRequests.containsKey(tile.tileKey)) {
			return false;
		}

		if (!loadindPossible()) {
			return true;
		}

		int edgeLenght = (int) Math.pow(2, tile.z);
		int y1 = (edgeLenght - tile.y) - 1;
		// "%1$s%2$d/%3$d/%4$d.png"
		// "%s%d/000/%03d/000/%03d.png"

		String url = String.format("%1$s%2$d/%3$d/%4$d.png", URL, tile.z,
				tile.x, y1);

		try {
			FileHandle file = tryLoadingFromCache(url);
			if (file != null) {
				loadTexture(tile.tileKey, file);
				tile.fileHandle = file;
				tileWrapMap.put(tile.tileKey, tile);
				return true;
			}
		} catch (IOException e) {
			removeFile(url);
			log.debug("removing file : ", e);
		}

		oldRequests.put(tile.tileKey, true);

		getController().execute(scheduleTileDownload(tile, url));

		tileWrapMap.put(tile.tileKey, tile);

		return true;
	}

	private Runnable scheduleTileDownload(final TileWrap tileWrap,
			final String url) {

		Runnable scheduledTileLoading = new Runnable() {

			@Override
			public void run() {
				downloadTile(url, tileWrap);
			}
		};

		return scheduledTileLoading;
	}

	public void setZoomEnabled(boolean zoomEnabled) {
		this.zoomEnabled = zoomEnabled;
	}

	public void storeCurrentLocation() {
		getController().storeProperty("zoom", map.getZoom());
		getController().storeProperty("x", map.getX());
		getController().storeProperty("y", map.getY());
	}

	private FileHandle tryLoadingFromCache(String url) throws IOException {
		try {
			if (CacheHelper.fileExists(url)) {
				return CacheHelper.cachedFileHandle(url);
			}
		} catch (GdxRuntimeException runtimeException) {
			throw new IOException(runtimeException);
		}

		return null;
	}

	public void zoomIn() {
		map.zoomInAnimation(map.getCenter().x, map.getCenter().y);
	}

	public void zoomOut() {
		map.zoomOutAnimation(map.getCenter().x, map.getCenter().y);
	}

	public void zoomToSectorId(SectorId id) {
		if (id != null) {
			int showZoom = id.z + 1;
			int edgeWidth = SectorHelper.getRelativeSectorEdgeWidth(id.z,
					showZoom);
			Vector2 position = new Vector2(SectorHelper.getMapX(id, showZoom)
					+ edgeWidth / 2, SectorHelper.getMapY(id, showZoom)
					+ edgeWidth / 2);
			GeoHelper.toLatLng(position.x, position.y, showZoom, toGeo);
			if (showZoom != map.getZoom()) {
				float timiningFactor = Math.abs(showZoom - map.getZoom());
				map.addAction(Actions.parallel(GeoActions.zoomTo(showZoom,
						TimingHelper.MAP_ZOOM * timiningFactor,
						Interpolation.pow3Out), GeoActions.moveTo(toGeo[0],
						toGeo[1], TimingHelper.MAP_MOVE * timiningFactor / 2,
						Interpolation.pow3Out)));
			} else {
				map.addAction(GeoActions.moveTo(toGeo[0], toGeo[1],
						TimingHelper.MAP_MOVE, Interpolation.pow3Out));
			}
		}
	}

}
