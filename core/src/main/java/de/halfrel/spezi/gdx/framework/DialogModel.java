package de.halfrel.spezi.gdx.framework;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

public interface DialogModel extends Model {

	public enum DIALOG_CONTEXT {

		ENDFIGHT_FAILURE,
		FIGHT_NOT_A_POSSIBLE_ACTION,
		FIGHT_NOT_POSSIBLE_NO_SKILLS,
		MESSAGE_RECEIVED,
		MESSAGES_RECEIVED,
		REPEAT_OR_PREFIGHT,
		SECTOR_CHOOSE_GAME;

	}

	public static class RawDialog {

		public DIALOG_CONTEXT context;
		public ServiceError error;
		public DialogActionListener listener;
		public Message message;

		public RawDialog(DIALOG_CONTEXT context, DialogActionListener listener) {
			this(context, listener, null);
		}

		public RawDialog(DIALOG_CONTEXT context, DialogActionListener listener,
				Message message) {
			this.context = context;
			this.listener = listener;
			this.message = message;
		}

	}

	public static final String KEY_CLIENT_ERRORS = "clientError";
	public static final String KEY_DIALOGS = "dialogs";
	public static final String KEY_RAW_DIALOGS = "rawDialogs";
	public static final String KEY_SERVICE_ERRORS = "serviceError";
	public static final String KEY_TUTORIAL_FOR_CLASS = "tutorialForClass";
	public static final String KEY_WAITING = "waiting";

	/**
	 * High Priority Dialog
	 * 
	 * @param clientError
	 */
	void addClientError(UIClientError clientError);

	/**
	 * Low Priority Dialog
	 * 
	 * @param dialog
	 */
	void addDialog(Dialog dialog);

	/**
	 * Low Priority Dialog
	 * 
	 * @param dialog
	 */
	void addRawDialog(RawDialog rawDialog);

	/**
	 * Medium Priority Dialog
	 * 
	 * @param serviceError
	 */
	void addServiceError(UIServiceError serviceError);

	Class<?> getTutorialForClass();

	boolean isWaiting();

	UIClientError peakClientError();

	UIServiceError peakServiceError();

	Dialog peekDialog();

	RawDialog peekRawDialog();

	UIClientError pollClientError();

	Dialog pollDialog();

	RawDialog pollRawDialog();

	UIServiceError pollServiceError();

	void setTutorialForClass(Class<?> tutorialForClass);

	void setWaiting(boolean waiting);

}
