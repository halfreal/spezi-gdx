package de.halfreal.spezi.gdx.widget;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.halfreal.spezi.gdx.framework.ExtendedController;
import de.halfreal.spezi.gdx.framework.SpeziGame;
import de.halfreal.spezi.gdx.widget.ListModel.Keys;

public class ListController<T> extends ExtendedController<ListModel<T>> {

	public static interface ComparatorFactory<T> {

		Comparator<T>[] getComperator(String type, Class<T> clazz);

	}

	private Comparator<T> comparator;

	private String defaultSortType;

	public ListController(ListModel<T> model, SpeziGame framework) {
		super(model, framework);
		loadCurrentSortType();
		loadSortTypeStates();
		loadSortStates();
	}

	public ListController(ListModel<T> model, SpeziGame framework,
			Class<T> clazz, ComparatorFactory<T> factory, String... types) {
		super(model, framework);
		this.defaultSortType = types[0];
		for (String type : types) {
			loadComparators(clazz, type, factory);
		}
		loadCurrentSortType();
		loadSortTypeStates();
		loadSortStates();
	}

	public ListController(ListModel<T> model, SpeziGame framework,
			String defaultSortType) {
		super(model, framework);
		this.defaultSortType = defaultSortType;
		loadCurrentSortType();
		loadSortTypeStates();
		loadSortStates();
	}

	protected String getDefaultSortType() {
		return defaultSortType;
	}

	public void loadComparators(Class<T> clazz, String type,
			ComparatorFactory<T> factory) {
		Comparator<T>[] comperators = factory.getComperator(type, clazz);
		if (comperators != null) {
			Map<String, Comparator<T>[]> sortTypes = getModel().getSortTypes();
			if (sortTypes == null) {
				sortTypes = new HashMap<String, Comparator<T>[]>();
			}
			sortTypes.put(type, comperators);
			getModel().setSortTypes(sortTypes);
		}
	}

	private void loadCurrentSortType() {
		String loadedSortType = loadString(Keys.SORT_TYPE.getName(), "");
		if ("".equals(loadedSortType)) {
			// WTF is this initialy empty
			// TODO use some generic sorttype
			if (getDefaultSortType() != null) {
				loadedSortType = getDefaultSortType();
			}
		}
		String sortType;
		if (loadedSortType != null && loadedSortType.length() > 0) {
			sortType = loadedSortType;
		} else {
			sortType = "default";
		}

		model.setSortType(sortType);
	}

	public void loadSortStates() {
		if (model.getSortTypes() == null) {
			return;
		}
		Map<String, Integer> sortStates = model.getSortTypeStates();
		if (sortStates == null) {
			sortStates = new HashMap<String, Integer>();
		}

		for (String kind : model.getSortTypes().keySet()) {
			Comparator<T>[] comparators = model.getSortTypes().get(kind);
			sortStates.put(kind, loadInt(kind, 0) % comparators.length);
		}

		model.setSortTypeStates(sortStates);
		String currentSortType = model.getSortType();

		if (model.getSortTypes().get(currentSortType) == null) {
			currentSortType = getDefaultSortType();
			model.setSortType(currentSortType);
		}

		Integer currentSortTypeState = model.getSortTypeStates().get(
				currentSortType);
		if (currentSortTypeState == null) {
			currentSortTypeState = 0;
		}
		Comparator<T>[] currentSortStates = model.getSortTypes().get(
				currentSortType);
		setComparator(currentSortStates[currentSortTypeState]);
	}

	protected void loadSortTypeStates() {
	}

	public void setComparator(Comparator<T> comparator) {
		this.comparator = comparator;
	}

	/**
	 * sort the table based on the comparator
	 * 
	 * @param key
	 */
	public void sort(String key) {
		storeProperty(ListModel.Keys.SORT_TYPE.getName(), key);
		model.setSortType(key);
		Integer state = model.getSortTypeStates().get(key);
		Comparator<T>[] comparators = model.getSortTypes().get(key);
		if (comparator == null || comparators.length == 0) {
			throw new RuntimeException("An comparator is needed for sorting!  "
					+ key);
		}
		if (state == null) {
			state = 0;
		}

		int newState = (state + 1) % comparators.length;
		loadInt(key, newState);
		Map<String, Integer> sortTypeStates = model.getSortTypeStates();
		sortTypeStates.put(key, newState);
		model.setSortTypeStates(sortTypeStates);
		setComparator(comparators[newState]);
		updateItems(model.getItems());
	}

	public void sort(String key, Integer state) {
		Comparator<T>[] comparators = model.getSortTypes().get(key);
		setComparator(comparators[state]);
		updateItems(model.getItems());
	}

	public void updateItems() {
		model.setItems(model.getItems());
	}

	public void updateItems(List<T> newItems) {
		if (comparator != null) {
			Collections.sort(newItems, comparator);
		}
		model.setItems(newItems);
	}

}
