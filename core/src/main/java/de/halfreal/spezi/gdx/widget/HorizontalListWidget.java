package de.halfreal.spezi.gdx.widget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

import de.halfreal.spezi.gdx.framework.AbstractScreen;
import de.halfreal.spezi.gdx.view.HorizontalWidgetGroup;
import de.halfreal.spezi.mvc.UpdateListener;

public abstract class HorizontalListWidget<T> extends ListWidget<T> {

	private Map<T, Actor> cacheMap;
	private boolean firstTime;
	private int rows;
	private WidgetGroup scrollActor;
	private ScrollPane scrollPane;
	private float sideOffset;
	private Skin skin;

	public HorizontalListWidget(ListController<T> controller,
			AbstractScreen<?, ?> screen) {
		this(controller, screen, 1);
	}

	public HorizontalListWidget(ListController<T> controller,
			AbstractScreen<?, ?> screen, int rows) {
		super(controller, screen);
		this.rows = rows;
		cacheMap = new HashMap<T, Actor>();
		firstTime = true;
	}

	@Override
	public void dispose() {
		cacheMap.clear();
		super.dispose();
	}

	public Array<Actor> getActors() {
		if (scrollActor == null) {
			return new Array<Actor>();
		} else {
			return scrollActor.getChildren();
		}
	}

	protected WidgetGroup getLayoutActor() {
		HorizontalWidgetGroup horizontalWidgetGroup = new HorizontalWidgetGroup(
				rows);
		horizontalWidgetGroup.setSideOffset(sideOffset);
		horizontalWidgetGroup.setPadding(getItemPadding());
		return horizontalWidgetGroup;
	}

	@Override
	public float getPrefHeight() {
		return getHeight();
	}

	@Override
	public float getPrefWidth() {
		return getWidth();
	}

	public int getRows() {
		return rows;
	}

	public ScrollPane getScrollPane() {
		return scrollPane;
	}

	public Action inAnimation() {
		return null;
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
		scrollActor = getLayoutActor();

		Set<T> removeSet = new HashSet<T>();
		removeSet.addAll(cacheMap.keySet());

		T currentSelectedItem = model.getCurrentSelectedItem();

		for (int i = 0; i < model.getCount(); i++) {
			T item = model.getItem(i);
			boolean selected = currentSelectedItem == item;
			Actor actor = createItem(item, skin, cacheMap.get(item), selected);
			if (actor != null) {
				Action inAnimation = inAnimation();
				if (inAnimation != null && firstTime) {
					actor.addAction(inAnimation);
				}
				cacheMap.put(item, actor);
				removeSet.remove(item);
				scrollActor.addActor(actor);
			}
		}
		for (T removeKey : removeSet) {
			cacheMap.remove(removeKey);
		}

		scrollActor.pack();
		scrollPane.setWidget(scrollActor);
		firstTime = false;
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
