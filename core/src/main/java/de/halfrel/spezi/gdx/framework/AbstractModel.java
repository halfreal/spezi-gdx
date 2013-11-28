package de.halfrel.spezi.gdx.framework;

import java.beans.PropertyChangeSupport;

public class AbstractModel implements Model {

	protected PropertyChangeSupport changes;

	public AbstractModel() {
		changes = new PropertyChangeSupport(this);
	}

	@Override
	public void fire(String propertyName, Object newValue) {
		changes.firePropertyChange(propertyName, null, newValue);
	}

	public void fire(String propertyName, Object oldValue, Object newValue) {
		changes.firePropertyChange(propertyName, oldValue, newValue);
	}

	@Override
	public PropertyChangeSupport getChanges() {
		return changes;
	}

}
