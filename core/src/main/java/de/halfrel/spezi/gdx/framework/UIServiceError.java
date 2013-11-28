package de.halfrel.spezi.gdx.framework;


public class UIServiceError extends ServiceError {

	private static final long serialVersionUID = 1L;
	private CONTEXT context;
	private DialogActionListener dialogActionListener;

	public UIServiceError(ServiceError error, CONTEXT context,
			DialogActionListener dialogActionListener) {
		super(error.getMessage());
		this.context = context;
		this.dialogActionListener = dialogActionListener;
		setDetails(error.getDetails());
	}

	public CONTEXT getContext() {
		return context;
	}

	public DialogActionListener getDialogActionListener() {
		return dialogActionListener;
	}

	public void setContext(CONTEXT context) {
		this.context = context;
	}

	public void setDialogActionListener(
			DialogActionListener dialogActionListener) {
		this.dialogActionListener = dialogActionListener;
	}

}
