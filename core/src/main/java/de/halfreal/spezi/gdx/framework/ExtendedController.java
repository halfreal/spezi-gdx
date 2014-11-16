package de.halfreal.spezi.gdx.framework;

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

import de.halfreal.spezi.mvc.AbstractController;
import de.halfreal.spezi.mvc.AbstractModel;

// TODO Extract preference management to PreferenceKeys and a generic way to
// approach preference storing in the spezi-mvc framework.
public class ExtendedController<M extends AbstractModel> extends
		AbstractController<M> {

	private static Logger log = LoggerFactory
			.getLogger(ExtendedController.class);

	private Preferences defaultPreferences;
	protected SpeziGame framework;
	protected M model;
	private ObjectMapper objectMapper;
	protected AbstractModel screenModel;
	private Boolean updated = false;

	public ExtendedController(M model, SpeziGame framework) {
		super(model);
		this.model = model;
		this.framework = framework;
		objectMapper = ObjectMapperFactory.create();
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

	public AbstractModel getScreenModel() {
		return screenModel;
	}

	public void initModelListeners() {
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

			JavaType constructType = objectMapper.constructType(type);
			try {
				return objectMapper.readValue((String) object, constructType);
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

	/**
	 * Only works for same KEY <-> property name bindings.
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
			log.warn("No setter method  found for key " + key
					+ " , did you refactor the propertyName but not the KEY_ ?");
			return false;
		}

		if (setterMethod.getParameterTypes() == null
				|| setterMethod.getParameterTypes().length != 1) {
			log.warn("Setter method "
					+ setterName
					+ " has a not valid amount of parameters, by convention setters have a single paramter.");
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
			log.error("Provided a bad argument '" + setterName + "'", e);
		} catch (IllegalAccessException e) {
			log.error("Can not access '" + setterName + "'", e);
		} catch (InvocationTargetException e) {
			log.error("Can not invocate the method '" + setterName + "'", e);
		}

		return false;
	}

	public void setScreenModel(AbstractModel screenModel) {
		this.screenModel = screenModel;
	}

	// TODO move to SpeziScreen
	protected void storeOnChange(final String key) {
		// listen(key, new GdxPropertyChangeListener() {
		//
		// @Override
		// public void changed(PropertyChangeEvent evt) {
		// storeProperty(key, evt.getNewValue());
		// }
		//
		// });
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
				outJson = objectMapper.writeValueAsString(newValue);
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

	public void update() {
	}

}
