package de.halfreal.spezi.gdx.widget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.halfreal.spezi.gdx.framework.AbstractScreen;
import de.halfreal.spezi.gdx.view.HorizontalWidgetGroup;
import de.halfreal.spezi.mvc.UpdateListener;

public abstract class HorizontalListWidget<T> extends ListWidget<T> {

	private Map<T, Actor> cacheMap;
	private ScrollPane scrollPane;
	private Skin skin;

	public HorizontalListWidget(ListController<T> controller,
			AbstractScreen<?, ?> screen) {
		super(controller, screen);
		cacheMap = new HashMap<T, Actor>();
	}

	@Override
	public void dispose() {
		cacheMap.clear();
		super.dispose();
	}

	@Override
	public float getPrefHeight() {
		return getHeight();
	}

	@Override
	public float getPrefWidth() {
		return getWidth();
	}

	@Override
	public void onCreateModelListeners() {
		super.onCreateModelListeners();

		listen(ListModel.Keys.CURRENT_SELECTED_ITEM,
				new UpdateListener<Object>() {

					@Override
					public void onUpdate(Object newValue) {
						scrollToSelectedElement();
					}
				});
	}

	@Override
	protected void onCreateView(Skin skin) {
		this.skin = skin;
		scrollPane = new ScrollPane(null, skin) {
			@Override
			protected void scrollX(float pixelsX) {
				super.scrollX(pixelsX);
				model.setRelativePosition(scrollPane.getScrollPercentX());
			}
		};

		refresh();
		scrollPane.setWidth(getWidth());
		scrollPane.setHeight(getHeight());

		scrollPane.setScrollingDisabled(false, true);
		addActor(scrollPane);
	}

	@Override
	protected void onShow(Stage stage, Skin skin) {
		super.onShow(stage, skin);
		scrollToSelectedElement();
	}

	@Override
	protected void refresh() {
		HorizontalWidgetGroup scrollActor = new HorizontalWidgetGroup();
		scrollActor.setPadding(getItemPadding());

		Set<T> removeSet = new HashSet<T>();
		removeSet.addAll(cacheMap.keySet());
		T currentSelectedItem = model.getCurrentSelectedItem();
		for (int i = 0; i < model.getCount(); i++) {
			T item = model.getItem(i);
			boolean selected = currentSelectedItem == item;
			Actor actor = createItem(item, skin, cacheMap.get(item), selected);
			cacheMap.put(item, actor);
			removeSet.remove(item);
			scrollActor.addActor(actor);
		}
		for (T removeKey : removeSet) {
			cacheMap.remove(removeKey);
		}
		scrollActor.pack();
		scrollPane.setWidget(scrollActor);

	}

	public void scrollToSelectedElement() {
		T currentSelectedItem = model.getCurrentSelectedItem();
		if (currentSelectedItem != null) {
			Actor actor = cacheMap.get(currentSelectedItem);
			if (actor != null) {
				scrollPane.scrollToCenter(actor.getX(), actor.getY(),
						actor.getWidth(), actor.getHeight());
			}
		}
	}

}
