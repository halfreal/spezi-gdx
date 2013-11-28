package de.halfrel.spezi.gdx.framework;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Disposable;

import de.halfreal.spezi.gdx.math.Timer;
import de.halfreal.spezi.gdx.math.Timer.Task;
import de.halfreal.spezi.gdx.model.Pair;
import de.halfreal.spezi.gdx.system.Assets;

public class ScreenWidget<C extends Controller<MODEL>, MODEL extends Model>
		extends WidgetGroup implements Disposable {

	protected C controller;
	private boolean create;

	private boolean initListeners;

	private List<Pair<String, PropertyChangeListener>> listeners;

	protected MODEL model;

	private List<Pair<String, PropertyChangeListener>> parentListeners;
	private float prefHeight;
	private float prefWidth;
	protected AbstractScreen<?, ?> screen;
	private Skin skin;

	@SuppressWarnings("unchecked")
	public <CC extends Controller<MM>, MM extends MODEL> ScreenWidget(
			CC controller, AbstractScreen<?, ?> screen) {
		if (controller != null) {
			this.model = controller.getModel();
			if (controller instanceof AbstractController<?>) {
				((AbstractController<?>) controller)
						.setScreenModel((AbstractScreenModel) screen.getModel());
			}

		}

		this.controller = (C) controller;
		this.screen = screen;
		this.listeners = new ArrayList<Pair<String, PropertyChangeListener>>();
		this.parentListeners = new ArrayList<Pair<String, PropertyChangeListener>>();
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

	protected void listen(String key, PropertyChangeListener listener) {
		AbstractController.mayRegister(key, listener, model.getChanges());
		listeners.add(new Pair<String, PropertyChangeListener>(key, listener));
		model.getChanges().addPropertyChangeListener(key, listener);
	}

	protected void listenParent(String key, PropertyChangeListener listener) {
		AbstractController.mayRegister(key, listener, screen.getModel()
				.getChanges());
		parentListeners.add(new Pair<String, PropertyChangeListener>(key,
				listener));
		screen.getModel().getChanges().addPropertyChangeListener(key, listener);
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

	public void onShow(Stage stage) {
		onShow(stage, Assets.getSkin(neededSkin()));
	}

	/**
	 * Created a View which is then added to the stage. Override this method for
	 * an individual starting Animation
	 * 
	 * @param stage
	 */
	protected void onShow(Stage stage, Skin skin) {
		Timer.schedule(new Task() {

			@Override
			public void runUI() {

				if (getStage() == null) {
					return;
				}

			}
		}, 2f);
	}

	public void performCreate(Skin skin) {
		if (!create) {
			onCreateView(skin);
			create = true;
		}
	}

	public void performHide() {
		removeModelListeners();
		if (getController() != null) {
			getController().removeModelListeners();
		}
		initListeners = false;
		onHide();
	}

	public void performInitListeners() {
		if (!initListeners) {
			initModelListeners();
			initListeners = true;
		}
		if (getController() != null) {
			getController().performInitModelListeners();
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
		for (Pair<String, PropertyChangeListener> entry : listeners) {
			model.getChanges().removePropertyChangeListener(entry.getFirst(),
					entry.getSecond());
		}
		listeners.clear();
	}

	public void removeParentModelListeners() {
		for (Pair<String, PropertyChangeListener> entry : parentListeners) {
			screen.getModel()
					.getChanges()
					.removePropertyChangeListener(entry.getFirst(),
							entry.getSecond());
		}
		parentListeners.clear();
	}

	public void setPrefHeight(float prefHeight) {
		this.prefHeight = prefHeight;
	}

	public void setPrefWidth(float prefWidth) {
		this.prefWidth = prefWidth;
	}

}
