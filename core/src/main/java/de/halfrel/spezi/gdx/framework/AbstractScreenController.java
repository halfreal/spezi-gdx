package de.halfrel.spezi.gdx.framework;

import de.halfreal.spezi.mvc.AbstractModel;

/**
 * FIXME remove the controller
 * 
 * @author Simon Joecks halfreal.de (c)
 * 
 * @param <M>
 */
public class AbstractScreenController<M extends AbstractModel> extends
		AbstractSpeziController<M> {

	public AbstractScreenController(M model, SpeziGame framework) {
		super(model, framework);
		this.framework = framework;
	}

}
