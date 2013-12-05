package de.halfreal.spezi.gdx.widget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import de.halfreal.spezi.mvc.AbstractModel;
import de.halfreal.spezi.mvc.Model;

@Model
public class ListModelStub<T> extends AbstractModel {

	T currentSelectedItem;

	List<T> items;
	float relativePosition;
	String sortType;
	Map<String, Comparator<T>[]> sortTypes;
	Map<String, Integer> sortTypeStates;

	public ListModelStub() {
		items = new ArrayList<T>();
	}

	public int getCount() {
		return items.size();
	}

	public T getCurrentSelectedItem() {
		return currentSelectedItem;
	}

	public T getItem(int index) {
		if (index < 0 || index >= items.size()) {
			return null;
		}
		return items.get(index);
	}

	public List<T> getItems() {
		return items;
	}

}
