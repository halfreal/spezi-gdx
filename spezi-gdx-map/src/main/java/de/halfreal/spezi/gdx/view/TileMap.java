package de.halfreal.spezi.gdx.view;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.halfreal.spezi.gdx.actions.GeoActions;
import de.halfreal.spezi.gdx.math.MathHelper;
import de.halfreal.spezi.gdx.sector.GeoHelper;
import de.halfreal.spezi.gdx.sector.SectorHelper;
import de.halfreal.spezi.gdx.sector.SectorId;

public class TileMap extends Image {

	public interface MapController {

		boolean isMoving();
	}

	public interface TileProvider {

		void requestTiles(List<TileWrap> tiles);

		Texture tileTexture(String tileWrap);

		TileWrap tileWrap(String tileName);

	}

	public static class TileWrap {
		public float fadeIn;
		public FileHandle fileHandle;
		public Rectangle rectangle;
		public String tileKey;
		public int x;
		public int y;
		public int z;

		public TileWrap(int x, int y, int z, Rectangle rectangle, String tileKey) {
			super();
			this.x = x;
			this.y = y;
			this.z = z;
			this.rectangle = rectangle;
			this.tileKey = tileKey;
			fadeIn = 0;
		}

	}

	private static Logger log = LoggerFactory.getLogger(TileMap.class);

	public static final float MAP_FADE_IN_SPEED = 1f;

	public static final int MAX_ZOOM = 18;

	public static final int MIN_ZOOM = 2;

	public static final float ZOOM_STEP = 0.03f;

	public static float mod(float value, int range) {
		return value < 0 ? (range - (-value) % range) % range : value % range;
	}

	private Vector2 center;

	private Color color;

	private boolean debug;

	protected final BitmapFont font;

	private Matrix4 lastMapProjection;

	private Matrix4 lastStageProjection;
	private OrthographicCamera mapCam;
	private float progress;
	private float progressZoom = 1;
	private ArrayList<TileWrap> requestTilesList;
	private final TileProvider tileProvider;

	private float[] toGeo;

	private final float viewableHeight;

	private final float viewableWidth;

	private Rectangle viewport;

	private float ymargin;

	private int zoom;

	public TileMap(TileProvider tileProvider, MapController mapController,
			float viewableWidth, float viewableHeight) {
		this.tileProvider = tileProvider;
		this.viewableWidth = viewableWidth;
		this.viewableHeight = viewableHeight;
		font = new BitmapFont();
		viewport = new Rectangle();
		zoom = MIN_ZOOM;
		requestTilesList = new ArrayList<TileWrap>();
		center = new Vector2();
		toGeo = new float[2];
		setTouchable(Touchable.enabled);
		lastMapProjection = new Matrix4();
		lastStageProjection = new Matrix4();
		color = new Color(Color.WHITE);
		pack();
		setX(toMapX(getWidth() / 2));
		setY(toMapY(getHeight() / 2));
		updateViewport();

		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				log.debug("CLICKED ON MAP : " + x + " " + y);
				super.clicked(event, x, y);
			}
		});
	}

	@Override
	public void clearActions() {
		super.clearActions();
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if (getStage().getScrollFocus() != this) {
			getStage().setScrollFocus(this);
		}

		if (mapCam == null) {
			initCamara();
		}

		// wrapping coordinates

		// && MathHelper.floatEquals(Math.abs(newX), Math.abs(getX())
		// + Math.abs(getWidth()))
		float newX = getX() < 0 ? getX() % getWidth() : -getWidth();
		if (!MathHelper.floatEquals(newX, getX())) {
			setX(newX);
		}
		if (getY() > getYMargin()) {
			setY(getYMargin());
		}
		if (-getY() > getHeight() - viewableHeight + getYMargin()) {
			setY(-getHeight() + viewableHeight - getYMargin());
		}

		// Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		// mapCam.update();
		// tempProjectionMatrix = batch.getProjectionMatrix();
		// tempTransformationMatrix = batch.getTransformMatrix();
		// loadTextures();
		// batch.setProjectionMatrix(mapCam.projection);
		// batch.setTransformMatrix(mapCam.combined);
		Color oldBatchColor = batch.getColor();
		drawMapTiles(batch, parentAlpha);
		drawFixures(batch, parentAlpha);
		// TODO drawMapMarker(batch, parentAlpha);
		// TODO disposeTextures();
		// batch.setProjectionMatrix(tempProjectionMatrix);
		// batch.setTransformMatrix(tempTransformationMatrix);
		batch.setColor(oldBatchColor);
		super.draw(batch, parentAlpha);
	}

	private void drawDebugOutput(SpriteBatch batch, float parentAlpha) {
		font.setColor(Color.MAGENTA);

		GeoHelper.toLatLng(getCenter().x, getCenter().y, zoom, toGeo);

		font.draw(
				batch,
				String.format(
						"%1$df,z:%2$d,c:%3$.4f,%4$.4f,[%5$.0fx%6$.0f],t:%7$d,h:%8$d,x:%9$.0f",
						Gdx.graphics.getFramesPerSecond(), zoom, toGeo[0],
						toGeo[1], getPrefWidth(), getPrefHeight(), 999,
						Gdx.app.getJavaHeap(), getX()), 0, 50);
	}

	private void drawFixures(SpriteBatch batch, float parentAlpha) {
		// batch.setProjectionMatrix(fixedProjection);
		// drawDebugOutput(batch, parentAlpha);
	}

	private void drawMapTiles(SpriteBatch batch, float parentAlpha) {
		mapCam.zoom = progressZoom;
		mapCam.update();
		batch.setProjectionMatrix(mapCam.combined);
		lastMapProjection.set(mapCam.combined);

		// if (oldZoom != 1) {
		// mapCam.zoom = oldZoom;
		// }.0

		// if (oldZoom != 1) {
		// log.debug("zoom " + mapCam.zoom + " oldZoom " + oldZoom);
		// }

		updateViewport();
		drawTiles(batch);

		mapCam.zoom = 1;
		mapCam.update();
		batch.setProjectionMatrix(mapCam.combined);
		lastStageProjection.set(mapCam.combined);
	}

	private void drawTiles(SpriteBatch batch) {

		requestTilesList.clear();
		int edgeLenght = (int) Math.pow(2, zoom);
		int xCoordStart = (int) Math.floor(viewport.x / 256d);
		int xCoordEnd = (int) Math.ceil((viewport.x + viewport.width) / 256d);

		int yCoordStart = (int) Math.floor((viewport.y) / 256d);
		int yCoordEnd = (int) Math.ceil((viewport.y + viewport.height) / 256d);

		for (int i = xCoordStart; i <= xCoordEnd; i++) {
			for (int j = yCoordStart; j <= yCoordEnd; j++) {

				if (j >= edgeLenght || j < 0) {
					continue;
				}

				String tileKey = getTileKey((int) mod(i, edgeLenght), j, zoom);
				TileWrap tile = getTile(tileKey);
				// draw first any upper elements
				if (tile == null || (tile.fadeIn < 1)
						|| tileProvider.tileTexture(tile.tileKey) == null) {
					if (zoom > 0) {
						int upperI = i;
						int upperJ = j;
						int upperZoom = zoom;
						int upperEdge = 2 << upperZoom;
						int diffZoom = 0;
						TileWrap upperTile = null;
						// FIXME must extend the viewport
						while (upperTile == null && upperZoom > 0) {
							upperZoom--;
							diffZoom++;
							upperEdge = 2 << upperZoom;
							upperI = (int) Math.floor(upperI / 2d);
							upperJ = (int) Math.floor(upperJ / 2d);
							// just draw the top left corner
							// if (i % 2 == 0 && j % 2 == 0) {
							String newTileKey = getTileKey(
									(int) mod(upperI, upperEdge), upperJ,
									upperZoom);
							upperTile = getTile(newTileKey);
						}

						if (upperTile != null) {
							Texture upperTileTexture = tileProvider
									.tileTexture(upperTile.tileKey);
							if (upperTileTexture != null) {
								batch.setColor(color.r, color.g, color.b,
										color.a);

								batch.draw(upperTileTexture,
										(upperI << (8 + diffZoom)) + getX(),
										(upperJ << (8 + diffZoom)) + getY(),
										256 << diffZoom, 256 << diffZoom);

								upperTile.fadeIn = 1;
							}
						}

					}
				}
				// then the real one
				if (tile != null) {
					Texture tileTexture = tileProvider
							.tileTexture(tile.tileKey);
					if (tileTexture != null) {
						batch.setColor(color.r, color.g, color.b, tile.fadeIn
								* color.a);
						float tileX = (i << 8) + getX();
						float tileY = tile.rectangle.y + getY();
						batch.draw(tileTexture, tileX, tileY);
						tile.fadeIn = Math.min(
								1,
								tile.fadeIn + MAP_FADE_IN_SPEED
										* Gdx.graphics.getDeltaTime());
						if (debug) {
							int y1 = (edgeLenght - tile.y) - 1;

							font.draw(batch, String.format("%d.%d.%d", tile.x,
									y1, tile.z), tileX + 100, tileY + 100);
						}

					} else {
						requestTilesList.add(tile);
					}

				} else {
					int x = (int) mod(i, edgeLenght);
					int y = j;
					requestTilesList.add(new TileWrap(x, y, zoom,
							new Rectangle(x << 8, y << 8, 256, 256),
							getTileKey(x, y, zoom)));
				}

			}
		}

		getTiles(requestTilesList);

	}

	public Vector2 getCenter() {
		return center;
	}

	@Override
	public Color getColor() {
		return color;
	}

	public Matrix4 getLastMapProjection() {
		return lastMapProjection;
	}

	public Matrix4 getLastStageProjection() {
		return lastStageProjection;
	}

	public OrthographicCamera getMapCam() {
		if (mapCam == null && getStage() != null
				&& getStage().getCamera() != null) {
			initCamara();
		}
		return mapCam;
	}

	@Override
	public float getPrefHeight() {
		return (int) Math.pow(2, zoom) << 8;
	}

	@Override
	public float getPrefWidth() {
		return (int) Math.pow(2, zoom) << 8;
	}

	public float getProgress() {
		return progress;
	}

	private TileWrap getTile(String tileKey) {
		return tileProvider.tileWrap(tileKey);
	}

	private String getTileKey(int x, int y, int z) {
		return String.format("%1$d.%2$d.%3$d", x, y, z);
	}

	private void getTiles(ArrayList<TileWrap> requestTilesList) {

		tileProvider.requestTiles(requestTilesList);

	}

	public float getViewableHeight() {
		return viewableHeight;
	}

	public float getViewableWidth() {
		return viewableWidth;
	}

	public Rectangle getViewport() {
		return viewport;
	}

	public float getXOnScreen(SectorId id, float width) {
		int x = SectorHelper.getMapX(id, getZoom()) + (int) getWidth();
		return wrapX(getX() + x, width);
	}

	public float getXOnScreen(SectorId id, float width, float offsetX) {
		int x = SectorHelper.getMapX(id, getZoom()) + (int) getWidth();
		return wrapX(getX() + x + offsetX, width);
	}

	public float getYMargin() {
		return ymargin;
	}

	public float getYOnScreen(SectorId id) {
		int y = SectorHelper.getMapY(id, getZoom());
		return getY() + y;
	}

	public int getZoom() {
		return zoom;
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		return super.hit(x % getWidth(), y, touchable);
	}

	/**
	 * calculates the actors coordinate point based on the Image coordiante. The
	 * refernce coordinate is the absolute postion on the screen, which is 0 in
	 * the left bottom corner.
	 * 
	 * @param xy
	 * @param refernceXY
	 * @return
	 */
	public float imageToActorCoord(float xy, float refernceXY) {
		return -xy + refernceXY;
	}

	private void initCamara() {
		mapCam = (OrthographicCamera) getStage().getCamera();
	}

	private void moved() {
		fire(new ChangeListener.ChangeEvent());
	}

	public void resetZoom() {
		if (progressZoom != 1) {

			if (progressZoom < 1) {
				// zoomin
				zoomInAnimation(getCenter().x, getCenter().y);
			} else {
				// zoomout
				zoomOutAnimation(getCenter().x, getCenter().y);
			}

		}
	}

	public float screenToImageCoordX(float screenCoordX) {
		return -getX() + screenCoordX;
	}

	public float screenToImageCoordY(float screenCoordY) {
		return -getY() + screenCoordY;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public void setX(float x) {
		super.setX(x);
	}

	@Override
	public void setY(float y) {
		super.setY(y);
	}

	public void setYmargin(float ymargin) {
		this.ymargin = ymargin;
	}

	public void setZoom(int zoom) {
		float relativeX = getCenter().x / getWidth();
		float relativeY = getCenter().y / getHeight();
		if (mapCam == null) {
			initCamara();
		}
		mapCam.zoom = 1;
		this.zoom = zoom;
		pack();
		setX(toMapX(relativeX * getWidth()));
		setY(toMapY(relativeY * getHeight()));
		updateViewport();
	}

	/**
	 * a number between 0 and 1 which is describing the current progress towards
	 * the next zoom step
	 * 
	 * @param progress
	 */
	public void setZoomProgress(float progress, boolean zoomin) {
		if (zoomin) {
			getMapCam().zoom = 1 - 0.5f * progress;
		} else {
			getMapCam().zoom = 1 + 1f * progress;
		}

		if (progress >= 1f) {
			getMapCam().zoom = 1;
		}

		progressZoom = getMapCam().zoom;
	}

	/**
	 * the actor coordinate based on the image coord in refernc of the viewable
	 * middlepoint
	 * 
	 * @param x
	 * @return
	 */
	public float toMapX(float imageX) {
		return imageToActorCoord(imageX, getViewableWidth() / 2);
	}

	/**
	 * the actor coordinate based on the image coord in refernce of the viewable
	 * middlepoint
	 * 
	 * @param x
	 * @return
	 */
	public float toMapY(float imageY) {
		return imageToActorCoord(imageY, getViewableHeight() / 2);
	}

	private void updateViewport() {
		viewport.x = -getX();
		viewport.y = -getY();
		viewport.width = viewableWidth;
		viewport.height = viewableHeight;
		center.set(viewport.x + viewableWidth / 2, viewport.y + viewableHeight
				/ 2);
	}

	public float wrapX(float x) {
		return x % getWidth();
	}

	public float wrapX(float x, float width) {
		return (x + width) % getWidth() - width;
	}

	public int wrapX(int x) {
		return x % (int) getWidth();
	}

	/**
	 * Zooms in towards a middle point in image coordinates
	 * 
	 * @param x
	 * @param y
	 */
	public void zoomIn(float x, float y) {
		if (zoom == MAX_ZOOM) {
			return;
		}

		setZoom(zoom + 1);
		setPosition(-x + getX(), -y + getY());
		pack();
	}

	public void zoomInAnimation(float x, float y) {
		if (getZoom() == MAX_ZOOM) {
			return;
		}

		GeoHelper.toLatLng(x, y, getZoom(), toGeo);

		float lat = toGeo[0];
		float lng = toGeo[1];
		getActions().clear();
		addAction(Actions.parallel(GeoActions.zoomTo(getZoom() + 1, 0.5f),
				GeoActions.moveTo(lat, lng, 0.5f)));

		moved();
	}

	public void zoomInStep() {
		zoomStep(ZOOM_STEP);
	}

	/**
	 * Zooms out towards the middle point in image coordinate
	 * 
	 * @param x
	 * @param y
	 */
	public void zoomOut(float x, float y) {
		if (zoom <= MIN_ZOOM) {
			return;
		}

		setZoom(zoom - 1);
		setPosition(toMapX(x / 2), toMapY(y / 2));
		pack();
		moved();
	}

	public void zoomOutAnimation(float x, float y) {
		if (zoom <= MIN_ZOOM) {
			return;
		}

		GeoHelper.toLatLng(x, y, getZoom(), toGeo);

		float lat = toGeo[0];
		float lng = toGeo[1];
		getActions().clear();
		addAction(Actions.parallel(GeoActions.zoomTo(zoom - 1, 0.5f),
				GeoActions.moveTo(lat, lng, 0.5f)));
	}

	public void zoomOutStep() {
		zoomStep(-ZOOM_STEP);
	}

	public void zoomStep(float step) {

		boolean zoomIn = step > 0;
		if (zoomIn && getZoom() >= MAX_ZOOM || !zoomIn && getZoom() <= MIN_ZOOM) {
			return;
		}

		progress += step;
		setZoomProgress(Math.abs(progress), zoomIn);

		if (Math.abs(progress) >= 1) {

			GeoHelper.toLatLng(getCenter().x, getCenter().y, getZoom(), toGeo);
			float startLat = toGeo[0];
			float startLng = toGeo[1];

			float[] toPoint = new float[2];
			GeoHelper.toPoint(startLat, startLng,
					getZoom() + (zoomIn ? 1 : -1), toPoint);
			float startX = toPoint[0];
			float startY = toPoint[1];

			if (zoomIn) {
				zoomIn(startX, startY);
			} else {
				zoomOut(startX, startY);
			}
			setPosition(toMapX(startX), toMapY(startY));

			progress = 0;
		}

	}

}
