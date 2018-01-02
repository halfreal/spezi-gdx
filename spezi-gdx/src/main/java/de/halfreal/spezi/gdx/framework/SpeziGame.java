package de.halfreal.spezi.gdx.framework;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public abstract class SpeziGame extends Game {

	public static interface DensityAdapter {
		float calculateDensity(float screenWdth, float screenHeight,
				float systemDensity);
	}

	private static DensityAdapter densityAdapter = new DensityAdapter() {

		@Override
		public float calculateDensity(float screenWdth, float screenHeight,
				float systemDensity) {
			return systemDensity;
		}
	};

	public static float getDeviceDensity() {
		if (Gdx.graphics != null) {
			return densityAdapter.calculateDensity(Gdx.graphics.getWidth(),
					Gdx.graphics.getHeight(), Gdx.graphics.getDensity());
		} else {
			return 2.0f;
		}
	}

	public static void setDensityAdapter(DensityAdapter densityAdapter) {
		SpeziGame.densityAdapter = densityAdapter;
	}

	@Override
	public AbstractScreen<?, ?> getScreen() {
		return (de.halfreal.spezi.gdx.framework.AbstractScreen<?, ?>) super
				.getScreen();
	}

	/**
	 * load an AbstractScreen from the defined Package and from the System
	 * Property -Dscreen=<YourScreenName>
	 * 
	 * @return C extends AbstractScreen<?,?> or null if no class found or if no
	 *         one argument Constructor exists
	 */
	public AbstractScreen<?, ?> loadScreenFromProperty(String basePackageName) {
		try {
			String property = System.getProperty("screen");
			if (property != null) {
				String className = basePackageName + "." + property;
				Class<?> clazz = Class.forName(className);
				Constructor<?> constructor = clazz
						.getConstructor(SpeziGame.class);

				return (AbstractScreen<?, ?>) constructor.newInstance(this);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void startScreen(final AbstractScreen<?, ?> screen, boolean restart) {

		if (!restart && getScreen() != null && screen != null
				&& screen.getClass().equals(getScreen().getClass())) {
			return;
		}

		if (getScreen() != null) {
			final AbstractScreen<?, ?> oldScreen = getScreen();

			oldScreen.onPauseScreen(new TransitionListener() {

				@Override
				public void complete() {
					setScreen(screen);
				}

			});
		} else {
			setScreen(screen);
		}
	}

}
