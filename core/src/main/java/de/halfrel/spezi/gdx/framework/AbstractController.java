package de.halfrel.spezi.gdx.framework;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import de.halfreal.spezi.gdx.model.Pair;

public class AbstractController<M extends Model> implements Controller<M> {

	private static Logger log = LoggerFactory
			.getLogger(AbstractController.class);

	public static void mayRegister(String key, PropertyChangeListener listener,
			PropertyChangeSupport changes) {
		// TODO Auto-generated method stub

		if (changes.hasListeners(key)) {
			PropertyChangeListener[] propertyChangeListeners = changes
					.getPropertyChangeListeners(key);
			for (PropertyChangeListener propertyChangeListener : propertyChangeListeners) {
				if (propertyChangeListener == listener) {
					throw new RuntimeException(
							"Can not register the same listener twice for the same key: "
									+ key);
				}
			}

		}

	}

	private Preferences defaultPreferences;
	protected SpeziGame framework;
	private Boolean initListeners = false;
	private List<Pair<String, PropertyChangeListener>> listeners;
	protected M model;
	private ObjectMapper objectMappper;
	protected AbstractScreenModel screenModel;
	private Boolean updated = false;

	public AbstractController(M model, SpeziGame framework) {
		this.model = model;
		this.framework = framework;
		listeners = new ArrayList<Pair<String, PropertyChangeListener>>();
		objectMappper = ObjectMapperFactory.create();
	}

	protected void addPropertyChangeListener(String key,
			PropertyChangeListener listener) {
		model.getChanges().addPropertyChangeListener(key, listener);
	}

	public Preferences defaultPreferences() {
		if (defaultPreferences == null) {
			defaultPreferences = Gdx.app.getPreferences(this.getClass()
					.getSimpleName());

		}

		return defaultPreferences;
	}

	public boolean empty(String userName) {
		return userName == null || userName.length() == 0;
	}

	public SpeziGame getFramework() {
		return framework;
	}

	@Override
	public M getModel() {
		return model;
	}

	public Preferences getPreferences() {
		Preferences preferences = Gdx.app.getPreferences(this.getClass()
				.getSimpleName());
		return preferences;
	}

	public AbstractScreenModel getScreenModel() {
		return screenModel;
	}

	public void initModelListeners() {
	}

	protected void listen(String key, PropertyChangeListener listener) {
		mayRegister(key, listener, model.getChanges());
		listeners.add(new Pair<String, PropertyChangeListener>(key, listener));
		this.addPropertyChangeListener(key, listener);
	}

	public boolean loadBoolean(String key, boolean defaultValue) {
		if (defaultPreferences().contains(key)) {
			return defaultPreferences().getBoolean(key);
		} else {
			return defaultValue;
		}
	}

	public float loadFloat(String key, float defaultValue) {
		if (defaultPreferences().contains(key)) {
			float value = defaultPreferences().getFloat(key);
			return value != 0 ? value : defaultValue;
		} else {
			return defaultValue;
		}
	}

	public int loadInt(String key, int defaultValue) {
		if (defaultPreferences().contains(key)) {
			return defaultPreferences().getInteger(key, defaultValue);
		} else {
			return defaultValue;
		}
	}

	public long loadLong(String key, long defaultValue) {
		long value = defaultPreferences().getLong(key);
		return value != 0 ? value : defaultValue;
	}

	public List<Long> loadLongList(String key, List<Long> defaultValue) {
		Preferences preferences = getPreferences();
		String value = preferences.getString(key);
		if (value == null) {
			return defaultValue;
		}

		return toLongList(value);
	}

	private Object loadProperty(Object object, Class<?> clazz, Type type) {
		if (object == null) {
			return null;
		}

		if (object.getClass().equals(clazz)) {
			return object;
		}

		if (object instanceof String) {

			// if (clazz.equals(Map.class)) {
			// clazz = HashMap.class;
			// }
			//
			// if (clazz.equals(List.class)) {
			// clazz = ArrayList.class;
			// }
			//
			// if (clazz.equals(Set.class)) {
			// clazz = HashSet.class;
			// }

			JavaType constructType = objectMappper.constructType(type);
			try {
				return objectMappper.readValue((String) object, constructType);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		throw new RuntimeException("No mapping for "
				+ object.getClass().getSimpleName() + " to "
				+ clazz.getSimpleName());

	}

	public String loadString(String key, String defaultValue) {
		String value = defaultPreferences().getString(key);
		return value != null && value.length() > 0 ? value : defaultValue;
	}

	@Override
	public synchronized void performInitModelListeners() {
		if (!initListeners) {
			initListeners = true;
			initModelListeners();
		}
	}

	@Override
	public synchronized void performUpdate() {
		if (!updated) {
			updated = true;
			update();
		}
	}

	public void refreshPreferences() {
		defaultPreferences = null;
	}

	public void remove(String key) {
		defaultPreferences().remove(key);
		defaultPreferences().flush();
	}

	@Override
	public synchronized void removeModelListeners() {
		for (Pair<String, PropertyChangeListener> entry : listeners) {
			this.removePropertyChangeListener(entry.getFirst(),
					entry.getSecond());
		}

		listeners.clear();
		initListeners = false;
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		model.getChanges().removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String key,
			PropertyChangeListener listener) {
		model.getChanges().removePropertyChangeListener(key, listener);
	}

	/**
	 * just works for same KEY <-> property name bindings
	 * 
	 * @param key
	 */
	public boolean restore(String key) {

		if (key == null || !getPreferences().contains(key)) {
			log.debug("No Value with key: " + key);
			return false;
		}
		Method[] methods = getModel().getClass().getMethods();
		String setterName = "set" + key.substring(0, 1).toUpperCase()
				+ key.substring(1);

		Method setterMethod = null;

		for (Method method : methods) {
			if (method.getName().equals(setterName)) {
				setterMethod = method;
				break;
			}
		}

		if (setterMethod == null) {
			log.warn("No setter method  found for key "
					+ key
					+ " , did you refactored the propertyName but not the KEY_ ?");
			return false;
		}

		if (setterMethod.getParameterTypes() == null
				|| setterMethod.getParameterTypes().length != 1) {
			log.warn("Setter method "
					+ setterName
					+ " has a not valid amount of paameters, by convention setters have a single paramter");
			return false;
		}

		Class<?> clazz = setterMethod.getParameterTypes()[0];

		if (clazz.isPrimitive()) {

			if (float.class.equals(clazz)) {
				clazz = Float.class;
			}
			if (int.class.equals(clazz)) {
				clazz = Integer.class;
			}
			if (double.class.equals(clazz)) {
				clazz = Double.class;
			}
			if (long.class.equals(clazz)) {
				clazz = Long.class;
			}
			if (short.class.equals(clazz)) {
				clazz = Short.class;
			}
			if (char.class.equals(clazz)) {
				clazz = Character.class;
			}

		}

		Type type = setterMethod.getGenericParameterTypes()[0];

		Object value = loadProperty(getPreferences().get().get(key), clazz,
				type);
		try {
			setterMethod.invoke(getModel(), value);
			return true;
		} catch (IllegalArgumentException e) {
			log.error("Provided a bad argument" + setterName, e);
		} catch (IllegalAccessException e) {
			log.error("Can not access: " + setterName, e);
		} catch (InvocationTargetException e) {
			log.error("can not invocate the method " + setterName, e);
		}

		return false;

	}

	public void setScreenModel(AbstractScreenModel screenModel) {
		this.screenModel = screenModel;
	}

	protected void storeOnChange(final String key) {
		listen(key, new GdxPropertyChangeListener() {

			@Override
			public void changed(PropertyChangeEvent evt) {
				storeProperty(key, evt.getNewValue());
			}

		});
	}

	protected void storeProperty(String key, Collection<?> list) {

		Preferences preferences = getPreferences();
		preferences.putString(key, toString(list));
		preferences.flush();

	}

	public void storeProperty(String key, Object newValue) {
		if (newValue == null) {
			return;
		}
		if (newValue instanceof String) {
			storeProperty(key, (String) newValue);
		} else if (newValue instanceof Integer) {
			defaultPreferences().putInteger(key, (Integer) newValue);
			defaultPreferences.flush();
		} else if (newValue instanceof Float) {
			defaultPreferences().putFloat(key, (Float) newValue);
			defaultPreferences.flush();
		} else if (newValue instanceof Boolean) {
			defaultPreferences().putBoolean(key, (Boolean) newValue);
			defaultPreferences.flush();
		} else if (newValue instanceof Long) {
			defaultPreferences().putLong(key, (Long) newValue);
			defaultPreferences.flush();
		} else if (newValue instanceof Collection<?>) {
			storeProperty(key, (Collection<?>) newValue);
		} else {
			String outJson;
			try {
				outJson = objectMappper.writeValueAsString(newValue);
				storeProperty(key, outJson);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//
			// throw new RuntimeException("Cannot store object " + newValue
			// + " , with key" + key);
		}

	}

	public void storeProperty(String key, String value) {
		defaultPreferences().putString(key, value);
		defaultPreferences().flush();
	}

	protected List<Long> toLongList(String list) {
		ArrayList<Long> resultList = new ArrayList<Long>();

		String[] split = list.split(";");
		for (String value : split) {
			try {
				resultList.add(Long.parseLong(value));
			} catch (NumberFormatException e) {
			}
		}

		return resultList;
	}

	private <T> String toString(Collection<T> list) {
		StringBuilder sb = new StringBuilder();
		Iterator<T> iterator = list.iterator();
		while (iterator.hasNext()) {
			T value = iterator.next();
			sb.append(value);
			if (iterator.hasNext()) {
				sb.append(';');
			}
		}

		return sb.toString();
	}

	@Override
	public void update() {
	}

}
