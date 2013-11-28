package de.halfrel.spezi.gdx.framework;

import java.beans.PropertyChangeSupport;

public interface Model {

	void fire(String propertyName, Object newValue);

	PropertyChangeSupport getChanges();
}
