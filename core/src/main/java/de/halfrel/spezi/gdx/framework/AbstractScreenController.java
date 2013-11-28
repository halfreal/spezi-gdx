package de.halfrel.spezi.gdx.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractScreenController<M extends DialogModel> extends
		AbstractController<M> implements DialogController<M> {

	private static Logger log = LoggerFactory
			.getLogger(AbstractScreenController.class);
	private M getModel;

	private boolean showInstantMessages;

	public AbstractScreenController(M model, SpeziGame framework) {
		super(model, framework);
		this.getModel = model;
		this.framework = framework;
		showInstantMessages = true;
	}

	@Override
	public void handleClientError(ClientError error) {
		getModel.addClientError(new UIClientError(error, null));
	}

	@Override
	public void handleServiceError(ServiceError error, CONTEXT context) {
		getModel.addServiceError(new UIServiceError(error, context, null));
	}

	@Override
	public void initModelListeners() {
		super.initModelListeners();

	}

	public boolean isShowInstantMessages() {
		return showInstantMessages;
	}

	public void setShowInstantMessages(boolean showInstantMessages) {
		this.showInstantMessages = showInstantMessages;
	}

}
