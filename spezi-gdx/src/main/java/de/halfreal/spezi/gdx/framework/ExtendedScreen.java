package de.halfreal.spezi.gdx.framework;

import com.badlogic.gdx.Screen;

/**
 * An interface to describe a more extened screen cycle, considering asset
 * loading and network dependence. Those methodes should be implemented rather
 * then the original Screen methods.
 * 
 * @author Simon Joecks halfreal.de (c)
 * 
 */
public interface ExtendedScreen extends Screen {

	/**
	 * {@link #collectAssets()} is always called after {@link #onCreate()} but
	 * before {@link Screen#show()} and {@link #onShow()} was called. This is
	 * the phase to load Assest.
	 */
	void collectAssets();

	/**
	 * All assets previously loaded in {@link ExtendedScreen#collectAssets()}
	 * are loaded. This is always called after {@link Screen#show()} but before
	 * {@link ExtendedScreen#onShow()}
	 */
	void onAssetsLoaded();

	/**
	 * The screen is created but not shown, this method is always called before
	 * the screen is attached to the GUI Thread, and also before collecting any
	 * assets or network requests.
	 * 
	 * Here you can initialize Objects needed for the collection phases, and
	 * also register needed Widget
	 */
	void onCreate();

	/**
	 * When the Screen should be detached, {@link TransitionListener#complete()}
	 * should be called in order to trigger a screen change. An out going
	 * Animation can be realized here
	 * 
	 * @param listener
	 */
	void onPauseScreen(TransitionListener listener);

	/**
	 * After the screen is attached to the GUI Thread and all Assest are
	 * collected, here you can construct all Asset dependent Layouts and show
	 * then on the GUI Thread. #onShow() is always called after
	 * {@link Screen#show()}
	 */
	void onShow();
}
