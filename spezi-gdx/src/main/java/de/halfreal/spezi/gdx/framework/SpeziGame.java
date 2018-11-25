package de.halfreal.spezi.gdx.framework;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class SpeziGame implements ApplicationListener {

    public interface DensityAdapter {
        float calculateDensity(float screenWidth, float screenHeight, float systemDensity);
    }

    private static DensityAdapter densityAdapter = new DensityAdapter() {
        @Override
        public float calculateDensity(float screenWidth, float screenHeight, float systemDensity) {
            return systemDensity;
        }
    };

    public static float getDeviceDensity() {
        if (Gdx.graphics != null) {
            return densityAdapter.calculateDensity(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                    Gdx.graphics.getDensity());
        } else {
            return 2.0f;
        }
    }

    public static void setDensityAdapter(DensityAdapter densityAdapter) {
        SpeziGame.densityAdapter = densityAdapter;
    }

    protected SpeziScreen screen;
    protected SpeziScreen overlayScreen;

    @Override
    public void resize(int width, int height) {
        if (screen != null) screen.resize(width, height);
        if (overlayScreen != null) overlayScreen.resize(width, height);
    }

    @Override
    public void render() {
        if (screen != null) screen.render(Gdx.graphics.getDeltaTime());
        if (overlayScreen != null) overlayScreen.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void pause() {
        if (screen != null) screen.pause();
        if (overlayScreen != null) overlayScreen.pause();
    }

    @Override
    public void resume() {
        if (screen != null) screen.resume();
        if (overlayScreen != null) overlayScreen.resume();
    }

    @Override
    public void dispose() {
        if (screen != null) screen.dispose();
        if (overlayScreen != null) overlayScreen.dispose();
    }

    public SpeziScreen getScreen() {
        return screen;
    }

    public SpeziScreen getOverlayScreen() {
        return overlayScreen;
    }

    public void setScreen(SpeziScreen screen) {
        if (this.screen != null) this.screen.hide();
        this.screen = screen;
        if (this.screen != null) {
            this.screen.show();
            this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    public void setOverlayScreen(SpeziScreen overlayScreen) {
        if (this.overlayScreen != null) this.overlayScreen.hide();
        this.overlayScreen = overlayScreen;
        if (this.overlayScreen != null) {
            this.overlayScreen.show();
            this.overlayScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    public void removeOverlayScreen() {
        if (this.overlayScreen != null) this.overlayScreen.hide();
        this.overlayScreen = null;
    }

    public void startScreen(final SpeziScreen screen, boolean restart) {
        if (!restart && getScreen() != null && screen != null && screen.getClass().equals(getScreen().getClass())) {
            return;
        }

        if (getScreen() != null) {
            final SpeziScreen oldScreen = getScreen();
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

    /**
     * Load a SpeziScreen from the defined Package and from the system property -Dscreen=<YourScreenName>.
     *
     * @return The SpeziScreen or null if no class is found or if no single argument Constructor exists.
     */
    public SpeziScreen loadScreenFromProperty(String basePackageName) {
        try {
            String property = System.getProperty("screen");
            if (property != null) {
                String className = basePackageName + "." + property;
                Class<?> clazz = Class.forName(className);
                Constructor<?> constructor = clazz
                        .getConstructor(SpeziGame.class);

                return (SpeziScreen) constructor.newInstance(this);
            }

        } catch (ClassNotFoundException | SecurityException | NoSuchMethodException | IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
