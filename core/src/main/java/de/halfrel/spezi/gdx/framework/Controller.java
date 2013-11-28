package de.halfrel.spezi.gdx.framework;

import de.halfrel.spezi.gdx.framework.Model;

/**
 * Placeholder to define common functionalities for later use
 * 
 * @author Simon Joecks halfreal.de (c)
 * 
 */
public interface Controller<M extends Model> {

	M getModel();

	void performInitModelListeners();

	void performUpdate();

	void removeModelListeners();

	/**
	 * Is called by the screen after Extras have been loaded and
	 */
	void update();

}
