package de.halfreal.spezi.gdx.widget;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.halfreal.spezi.gdx.framework.AbstractScreen;
import de.halfreal.spezi.gdx.framework.ScreenWidget;
import de.halfreal.spezi.mvc.UpdateListener;

public abstract class ListWidget<T> extends
		ScreenWidget<ListController<T>, ListModel<T>> {

	private float itemPadding;

	public ListWidget(ListController<T> controller, AbstractScreen<?, ?> screen) {
		super(controller, screen);
	}

	/**
	 * creates a new item to display, this view needs to have set bounds after
	 * creation, otherwise it is not possible to position it correctly in the
	 * list creation
	 * 
	 * @param data
	 * @param skin
	 * @return
	 */
	public abstract Actor createItem(T data, Skin skin, Actor oldActor,
			boolean selected);

	public int getIndex(T data) {
		return getModel().getCount() - 1 - getModel().getItems().indexOf(data);
	}

	public float getItemPadding() {
		return itemPadding;
	}

	@Override
	public void onCreateModelListeners() {
		super.onCreateModelListeners();
		listen(ListModel.Keys.ITEMS, new UpdateListener<List<?>>() {

			@Override
			public void onUpdate(List<?> newValue) {
				refresh();
			}
		});
	}

	/**
	 * rebuilds the list
	 */
	protected abstract void refresh();

	public void setItemPadding(float itemPadding) {
		this.itemPadding = itemPadding;
	}

}
