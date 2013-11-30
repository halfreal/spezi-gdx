package de.halfrel.spezi.gdx.framework;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public abstract class SpeziGame extends Game implements ClientErrorHandler {

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
		return (de.halfrel.spezi.gdx.framework.AbstractScreen<?, ?>) super
				.getScreen();
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
