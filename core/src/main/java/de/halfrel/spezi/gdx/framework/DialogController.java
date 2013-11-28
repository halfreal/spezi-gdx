package de.halfrel.spezi.gdx.framework;

public interface DialogController<M extends DialogModel> extends Controller<M> {

	void handleClientError(ClientError error);

	void handleServiceError(ServiceError error, CONTEXT context);
}
