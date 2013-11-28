package de.halfrel.spezi.gdx.framework;

import java.lang.reflect.Method;

public class UIClientError extends ClientError {

	private DialogActionListener dialogActionListener;

	public UIClientError(CLIENT_ERROR type, Method method, Object[] args,
			DialogActionListener listener) {
		super(type, method, args);
		dialogActionListener = listener;
	}

	public UIClientError(CLIENT_ERROR type, Method method, Object[] args,
			Exception e, DialogActionListener listener) {
		super(type, method, args, e);
		dialogActionListener = listener;
	}

	public UIClientError(ClientError clientError, DialogActionListener listener) {
		super(clientError.getType(), clientError.getMethod(), clientError
				.getArgs(), clientError.getException());
		dialogActionListener = listener;
	}

	public DialogActionListener getDialogActionListener() {
		return dialogActionListener;
	}

}
