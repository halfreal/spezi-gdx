package de.halfreal.spezi.gdx.framework;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Disposable;

import de.halfreal.spezi.gdx.system.Assets;
import de.halfreal.spezi.mvc.AbstractController;
import de.halfreal.spezi.mvc.AbstractModel;
import de.halfreal.spezi.mvc.ChangeListener;
import de.halfreal.spezi.mvc.Key;
import de.halfreal.spezi.mvc.ListenerRegistry;

/**
 * TODO introduce a listenerFactory
 * 
 * 
 */
public class ScreenWidget<C extends AbstractController<MODEL>, MODEL extends AbstractModel>
		extends WidgetGroup implements Disposable {

	protected C controller;
	private boolean create;

	private boolean initListeners;
	private ListenerRegistry<MODEL> listenerRegistry;

	protected MODEL model;

	private float prefHeight;
	private float prefWidth;
	protected AbstractScreen<?, ?> screen;
	private Skin skin;

	public ScreenWidget(C controller, AbstractScreen<?, ?> screen) {
		if (controller != null) {
			this.model = controller.getModel();
		}

		this.controller = controller;
		this.screen = screen;
		listenerRegistry = new ListenerRegistry<MODEL>(model);
		create = false;
		initListeners = false;
	}

	public void collectAssets() {
	}

	@Override
	public void dispose() {
	}

	public C getController() {
		return controller;
	}

	public MODEL getModel() {
		return model;
	}

	@Override
	public float getPrefHeight() {
		return prefHeight;
	}

	@Override
	public float getPrefWidth() {
		return prefWidth;
	}

	public AbstractScreen<?, ?> getScreen() {
		return screen;
	}

	public Skin getSkin() {
		return skin;
	}

	public void initModelListeners() {

	}

	protected <T> void listen(Key<T> key, ChangeListener<T> listener) {
		listenerRegistry.registerListener(key, listener);
	}

	public Class<?> neededSkin() {
		return null;
	}

	/**
	 * is called right before shown on the stage
	 */
	protected void onCreateView(Skin skin) {
	}

	protected void onHide() {
		remove();
	}

	/**
	 * Created a View which is then added to the stage. Override this method for
	 * an individual starting Animation
	 * 
	 * @param stage
	 */
	protected void onShow(Stage stage, Skin skin) {
	}

	public void performCreate(Skin skin) {
		if (!create) {
			onCreateView(skin);
			create = true;
		}
	}

	public void performHide() {
		removeModelListeners();
		initListeners = false;
		onHide();
	}

	public void performInitListeners() {
		if (!initListeners) {
			initModelListeners();
			listenerRegistry.onResume();
			initListeners = true;
		}
	}

	public void performShow(Group group, Skin skin) {
		this.skin = skin;
		performCreate(skin);

		if (getParent() == null || getStage() == null) {
			group.addActor(this);
			performInitListeners();
			onShow(group.getStage(), skin);
		}
	}

	public void performShow(Stage stage) {
		performShow(stage, Assets.getSkin(neededSkin()));
	}

	private void performShow(Stage stage, Skin skin) {
		this.skin = skin;
		performCreate(skin);

		if (getParent() == null || getStage() == null) {
			stage.addActor(this);
			performInitListeners();
			onShow(stage, skin);
		}

		toFront();
	}

	public void removeModelListeners() {
		listenerRegistry.onPause();
	}

	public void setPrefHeight(float prefHeight) {
		this.prefHeight = prefHeight;
	}

	public void setPrefWidth(float prefWidth) {
		this.prefWidth = prefWidth;
	}

}
