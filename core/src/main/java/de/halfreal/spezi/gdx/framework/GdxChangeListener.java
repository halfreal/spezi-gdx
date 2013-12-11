package de.halfreal.spezi.gdx.framework;

import com.badlogic.gdx.Gdx;

import de.halfreal.spezi.mvc.ChangeListener;

public abstract class GdxChangeListener<T> implements ChangeListener<T> {

	public abstract void changed(T oldValue, T newValue);

	@Override
	public void onChange(final T oldValue, final T newValue) {
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				changed(oldValue, newValue);
			}
		});
	}
}
