package de.halfrel.spezi.gdx.framework;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.badlogic.gdx.Gdx;

public abstract class GdxPropertyChangeListener implements
		PropertyChangeListener {

	public abstract void changed(PropertyChangeEvent evt);

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				changed(evt);
			}
		});
	}

}
