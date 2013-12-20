package de.halfreal.spezi.gdx.widget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import de.halfreal.spezi.gdx.framework.AbstractScreen;
import de.halfreal.spezi.gdx.view.VerticalWidgetGroup;

public abstract class VerticalListWidget<T> extends ListWidget<T> {

	private float bottomOffset;
	private Map<T, Actor> cacheMap;
	private int columns;
	private boolean firstTime;
	private ScrollPane scrollPane;
	private Skin skin;

	/**
	 * a vertical Scroll list, using a default scrollPane style. It is
	 * inefficient in the sense that it can not display many items and being
	 * still responsive.
	 * 
	 * TODO LazyList should cache views and reuse them while scrolling down
	 * 
	 * 
	 * @param controller
	 * @param screen
	 */
	public VerticalListWidget(ListController<T> controller,
			AbstractScreen<?, ?> screen) {
		this(controller, screen, 1);
	}

	public VerticalListWidget(ListController<T> controller,
			AbstractScreen<?, ?> screen, int columns) {
		super(controller, screen);
		this.columns = columns;
		cacheMap = new HashMap<T, Actor>();
		firstTime = true;
	}

	@Override
	public void dispose() {
		cacheMap.clear();
		super.dispose();
	}

	public float getBottomOffset() {
		return bottomOffset;
	}

	public int getColumns() {
		return columns;
	}

	@Override
	public Actor getItem(T data) {
		return cacheMap.get(data);
	}

	protected WidgetGroup getLayoutActor() {
		VerticalWidgetGroup verticalWidgetGroup = new VerticalWidgetGroup(
				columns);
		verticalWidgetGroup.setBottomOffset(bottomOffset);
		verticalWidgetGroup.setPadding(getItemPadding());
		return verticalWidgetGroup;
	}

	@Override
	public float getPrefWidth() {
		return getWidth();
	}

	public ScrollPane getScrollPane() {
		return scrollPane;
	}

	public Action inAnimation() {
		return null;
	}

	public boolean isFirstTime() {
		return firstTime;
	}

	@Override
	protected void onCreateView(Skin skin) {
		this.skin = skin;
		scrollPane = new ScrollPane(null, skin) {

			@Override
			public void scrollY(float pixels) {
				super.scrollY(pixels);
				model.setRelativePosition(scrollPane.getScrollPercentY());
			}

		};

		refresh();
		scrollPane.setWidth(getWidth());
		scrollPane.setHeight(getHeight());
		scrollPane.setScrollingDisabled(true, false);
		addActor(scrollPane);
	}

	@Override
	protected void refresh() {
		// TODO just generate new item, all others must be reused
		WidgetGroup scrollActor = getLayoutActor();

		Set<T> removeSet = new HashSet<T>();
		removeSet.addAll(cacheMap.keySet());

		T currentSelectedItem = model.getCurrentSelectedItem();

		for (int i = model.getCount() - 1; i >= 0; i--) {
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

	@Override
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

	public void setBottomOffset(float bottomOffset) {
		this.bottomOffset = bottomOffset;
	}

	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}

}
