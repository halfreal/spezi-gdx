package de.halfreal.spezi.gdx.framework;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import de.halfreal.spezi.gdx.system.Assets;
import de.halfreal.spezi.mvc.AbstractController;
import de.halfreal.spezi.mvc.AbstractModel;
import de.halfreal.spezi.mvc.Key;
import de.halfreal.spezi.mvc.ListenerRegistry;

/**
 * The base class for all game screens.
 */
public class AbstractScreen<C extends AbstractController<MODEL>, MODEL extends AbstractModel>
		implements ExtendedScreen {

	public static abstract class OnLoadedListener {
		void onAssetsLoaded() {
		};

		void onExtrasLoaded() {
		};

		void onShow() {
		};
	}

	public static class ScreenWidgetLoader extends OnLoadedListener {

		private boolean assetsLoaded;
		private boolean extrasLoaded;
		private boolean perfomedOnShow;
		private OnLoadedListener widgetListener;

		public ScreenWidgetLoader(OnLoadedListener widgetListener) {
			this.widgetListener = widgetListener;
		}

		private void checkOnShowComplete() {
			if (assetsLoaded && extrasLoaded && !perfomedOnShow) {
				perfomedOnShow = true;
				onShow();
			}
		}

		@Override
		public void onAssetsLoaded() {
			assetsLoaded = true;
			checkOnShowComplete();
			if (widgetListener != null) {
				widgetListener.onAssetsLoaded();
			}
		}

		@Override
		public void onExtrasLoaded() {
			extrasLoaded = true;
			checkOnShowComplete();
			if (widgetListener != null) {
				widgetListener.onExtrasLoaded();
			}
		}

		@Override
		public void onShow() {
			if (widgetListener != null) {
				widgetListener.onShow();
			}
		}

	}

	private static float DP_TRESHOLD;
	private static String GFX_FOLDER;
	private static final int GL_BLEND_DESTINATION_FACTOR = GL20.GL_ONE_MINUS_SRC_ALPHA;
	private static final int GL_BLEND_SOURCE_FACTOR = GL20.GL_SRC_ALPHA;
	public static int height;
	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractScreen.class);
	public static final float STANDARD_DENSITY = 1.5f;
	private static final String TAG = AbstractScreen.class.getSimpleName();
	public static int width;

	static {
		correctDimensions();
	}

	public static void calculateDP() {
		GFX_FOLDER = getGfxFolder();
		DP_TRESHOLD = getDensity();
		Gdx.app.log(TAG, "Current Density: " + DP_TRESHOLD + ",  " + GFX_FOLDER);
	}

	public static void correctDimensions() {
		if (Gdx.graphics != null) {
			calculateDP();
			width = Gdx.graphics.getWidth();
			height = Gdx.graphics.getHeight();
		}
	}

	/**
	 * Converts density independent pixels to on-screen point/pixel dimensions.
	 *
	 * @param dp
	 *            density independent pixels
	 * @return on-screen pixels
	 */
	public static float dip(float dp) {
		return dp * DP_TRESHOLD;
	}

	/**
	 * Converts density independent pixels to on-screen point/pixel dimensions.
	 *
	 * @param dp
	 *            density independent pixels
	 * @return on-screen pixels
	 */
	public static int dip(int dp) {
		return Math.round(dp * DP_TRESHOLD);
	}

	public static void disableBlending() {
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	public static void enableBlending() {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL_BLEND_SOURCE_FACTOR, GL_BLEND_DESTINATION_FACTOR);
	}

	public static float getDensity() {
		if (SpeziGame.getDeviceDensity() == 0f) {
			return Gdx.graphics.getDensity();
		} else {
			return SpeziGame.getDeviceDensity();
		}

	}

	public static String getEffectFolder() {
		return Assets.PARTICLE_EFFECT_FOLDER;
	}

	/**
	 * LDPI ~ 120dpi - 0.75
	 *
	 * MDPI ~ 160dpi - 1.0
	 *
	 * HDPI ~ 240dpi - 1.5
	 *
	 * XHDPI ~320dpi - 2
	 *
	 * @return
	 */
	public static String getGfxFolder() {

		String folder = null;
		if (getDensity() < 1f) {
			folder = Assets.DRAWABLE_LDPI;
		} else if (getDensity() >= 1f && getDensity() < 1.5f) {
			folder = Assets.DRAWABLE_MDPI;
		} else if (getDensity() >= 1.5 && getDensity() < 2) {
			folder = Assets.DRAWABLE_HDPI;
		} else {
			folder = Assets.DRAWABLE_XHDPI;
		}

		return folder;
	}

	/**
	 * LDPI ~ 120dpi - 0.75
	 *
	 * MDPI ~ 160dpi - 1.0
	 *
	 * HDPI ~ 240dpi - 1.5
	 *
	 * XHDPI ~320dpi - 2
	 *
	 * @return
	 */
	public static float getRoundDensity() {

		if (getDensity() < 1f) {
			return 0.75f;
		} else if (getDensity() >= 1f && getDensity() < 1.5f) {
			return 1f;
		} else if (getDensity() >= 1.5 && getDensity() < 2) {
			return 1.5f;
		} else {
			return 2.0f;
		}

	}

	public static boolean inViewport(float screenX, float screenY) {
		return screenX >= 0 && screenX <= width && screenY >= 0
				&& screenY <= height;
	}

	public static boolean isOnStage(Actor actor) {
		return actor != null
				&& (actor.getParent() != null || actor.getStage() != null);
	}

	private boolean afterShow = false;
	private boolean allExtrasLoaded;
	private boolean assetsLoaded = false;
	private Image background;
	protected final Batch batch;
	protected OrthographicCamera camera;
	protected C controller;
	protected final SpeziGame framework;
	private boolean initModelListeners;
	private ListenerRegistry<MODEL> listenerRegistry;
	private MODEL model;
	private Actor oldActor;
	private boolean resumed;
	private List<ScreenWidget<?, ?>> screenWidgets;
	private boolean show;
	protected final Stage stage;
	private ScreenStyle style;

	public AbstractScreen(SpeziGame framework, C controller) {
		this.framework = framework;
		this.controller = controller;
		this.model = controller.getModel();
		batch = new SpriteBatch();
		stage = new Stage(new StretchViewport(width, height)) {

			@Override
			public void addActor(Actor actor) {
				super.addActor(actor);
				putBarsInFront();
			}

			private void putBarsInFront() {
				Set<Actor> reaarangeList = new HashSet<Actor>();
				for (Actor child : getRoot().getChildren()) {
					// TODO change the assigned Order on the Screen

				}

				for (Actor child : reaarangeList) {
					child.toFront();
				}

			}

		};
		listenerRegistry = new ListenerRegistry<MODEL>(model);
		OrthographicCamera orthoCamara = new OrthographicCamera();
		orthoCamara.setToOrtho(false, width, height);
		stage.getViewport().setCamera(orthoCamara);
		Gdx.input.setInputProcessor(stage);
		Gdx.input.setCatchBackKey(false);
		calculateDP();
		assetsLoaded = false;
		screenWidgets = new ArrayList<ScreenWidget<?, ?>>();

		performCreate();
	}

	protected void addActor(Actor actor) {
		stage.addActor(actor);
	}

	private void applyStyle() {
		if (style != null && style.background != null) {
			boolean isDesktop = (Gdx.app.getType() == ApplicationType.Desktop);

			float imageHW = style.background.getMinHeight()
					/ style.background.getLeftWidth();
			float screenHW = height / width;

			if (background != null) {
				background.remove();
			}

			background = new Image(style.background) {
				@Override
				public void draw(Batch batch, float parentAlpha) {
					super.draw(batch, parentAlpha);
				};
			};
			if (imageHW > screenHW) {
				background = Assets.scaleImageH(background, height);
			} else {
				background = Assets.scaleImageW(background, width);
			}

			stage.addActor(background);
			background.toBack();

		}
	}

	private void checkDimensions() {
		if (width == 0 || height == 0) {
			correctDimensions();
		}
	}

	@Override
	public void collectAssets() {
	}

	@Override
	public void dispose() {
		Gdx.app.log(getName(), "Disposing screen: " + getName());
		for (ScreenWidget<?, ?> screenWidget : screenWidgets) {
			screenWidget.dispose();
		}
		stage.dispose();
		batch.dispose();
	}

	public Image getBackground() {
		return background;
	}

	public C getController() {
		return controller;
	}

	public SpeziGame getFramework() {
		return framework;
	}

	public MODEL getModel() {
		return model;
	}

	protected String getName() {
		return getClass().getSimpleName();
	}

	public Skin getSkin() {
		return Assets.getSkin(neededSkin());
	}

	@Override
	public void hide() {
		removeModelListeners();
	}

	public boolean isAfterShow() {
		return afterShow;
	}

	public boolean isAllExtrasLoaded() {
		return allExtrasLoaded;
	}

	public boolean isAssetsLoaded() {
		return assetsLoaded;
	}

	protected <T> void listen(Key<T> key,
			de.halfreal.spezi.mvc.ChangeListener<T> listener) {
		listenerRegistry.registerListener(key, listener);
	}

	protected Class<?> neededSkin() {
		return null;
	}

	@Override
	public void onAssetsLoaded() {
	}

	/**
	 * use {@link AbstractScreen.onCreateAfterConstructor }
	 */
	@Deprecated
	@Override
	public void onCreate() {
		Assets.resume();
	}

	protected void onCreateAfterConstructor() {
	}

	/**
	 * Initializes all model listeners after being attached to the GUI thread.
	 */
	protected void onCreateModelListeners() {
		Gdx.app.log(getName(), "INIT MODEL LISTENERS");
	}

	/**
	 * Finishes the screen, which is a helper method to implement fade-out
	 * animations.
	 */
	@Override
	public void onPauseScreen(TransitionListener listener) {
		listener.onTransitionComplete();
	}

	@Override
	public void onShow() {
	}

	@Override
	public void pause() {
		LOG.debug("PAUSE, not removing listeners");
	}

	private void performAssetsLoaded() {
		Gdx.app.log(getName(), "ASSETS LOADED");
		Skin skin = Assets.getSkin(neededSkin());
		if (style == null) {
			setStyle(skin.get("default", ScreenStyle.class));
		}

		onAssetsLoaded();
		performShowComplete();
	}

	private void performCollectAssets() {
		for (ScreenWidget<?, ?> screenWidget : screenWidgets) {
			screenWidget.collectAssets();
		}
		collectAssets();
	}

	private void performCreate() {
		LOG.debug("ON CREATE");
		// widgets get registered
		onCreate();
		performScreenAssetsLoading();
		performCollectAssets();
		assetsLoaded = !Assets.isLoading();
	}

	private synchronized void performInitModelListeners() {
		if (!initModelListeners) {
			initModelListeners = true;
			onCreateModelListeners();
		}
	}

	private void performScreenAssetsLoading() {
		assetsLoaded = false;
		// Skin loading should be done by each screen
		// Assets.loadSkin(neededSkin());
	}

	private synchronized void performShowComplete() {
		if (assetsLoaded && !afterShow && show) {
			for (ScreenWidget<? extends AbstractController<?>, ?> screenWidget : screenWidgets) {
				if (screenWidget.getController() != null) {
					AbstractController<?> screenController = screenWidget
							.getController();
					if (screenController instanceof ExtendedController) {
						((ExtendedController<?>) screenController)
								.performUpdate();
					}

				}
				screenWidget.performShow(stage);
			}
			afterShow = true;
			listenerRegistry.onResume();
			onShow();
		}

	}

	public void registerWidget(ScreenWidget<?, ?> widget) {
		screenWidgets.add(widget);
	}

	private void removeModelListeners() {
		listenerRegistry.onPause();

		for (ScreenWidget<?, ?> screenWidget : screenWidgets) {
			screenWidget.removeModelListeners();
		}

		initModelListeners = false;
	}

	@Override
	public void render(float delta) {
		checkDimensions();
		Assets.update();
		if (!assetsLoaded) {
			assetsLoaded = !Assets.isLoading();
			if (assetsLoaded) {
				performAssetsLoaded();
			}

		}

		stage.act(delta);

		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_ALPHA_BITS);

		if (!resumed || !Assets.isLoading()) {
			resumed = false;
			// update and draw the stage actors
			stage.draw();
		} else {
			Assets.getManager().finishLoading();
		}

	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log(getName(), "Resizing screen: " + getName() + " to: "
				+ width + " x " + height);
		// resize the stage
		// stage.setViewport(new ExtendViewport(width, height));
		camera = new OrthographicCamera(AbstractScreen.width,
				AbstractScreen.height);
		calculateDP();
		performShowComplete();
	}

	@Override
	public void resume() {
		resumed = true;
		Gdx.app.log(getName(), "Resuming screen, not restoring listeners: "
				+ getName());
		Assets.resume();
	}

	public void setStyle(ScreenStyle style) {
		this.style = style;
		applyStyle();
	}

	public void setStyle(String styleName) {
		this.style = Assets.getSkin(neededSkin()).get(styleName,
				ScreenStyle.class);
		applyStyle();
	}

	@Override
	public synchronized void show() {
		if (!show) {
			onCreateAfterConstructor();
		}
		Gdx.app.log(getName(), "SHOW, init listeners");
		Assets.resume();
		show = true;
		performInitModelListeners();
		checkDimensions();
		performShowComplete();
	}

	/**
	 * Two actors on a screen concept, which is moving the old actor out to the
	 * right and showing the new actor by moving it from left to right.
	 *
	 * @param actor
	 */
	protected void showWidget(Actor actor) {
		if (actor == oldActor && isOnStage(oldActor)) {
			return;
		}
		if (isOnStage(oldActor)) {
			oldActor.addAction(SpeziActions.moveRightAndLeave());
		}

		addActor(actor);
		actor.addAction(SpeziActions.startLeftMoveRight());
		oldActor = actor;
	}

	protected void updateCamera() {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
	}

}
