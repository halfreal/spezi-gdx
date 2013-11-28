package de.halfrel.spezi.gdx.framework;

import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

public class AbstractScreenModel extends AbstractModel implements DialogModel {

	private Queue<UIClientError> clientErrors;

	private Queue<Dialog> dialogs;

	private Queue<RawDialog> rawDialogs;

	private Queue<UIServiceError> serviceErrors;

	private Class<?> tutorialForClass;

	boolean waiting;

	public AbstractScreenModel() {
		clientErrors = new LinkedList<UIClientError>();
		serviceErrors = new LinkedList<UIServiceError>();
		dialogs = new LinkedList<Dialog>();
		rawDialogs = new LinkedList<RawDialog>();
	}

	@Override
	public void addClientError(UIClientError clientError) {
		clientErrors.add(clientError);
		fire(DialogModel.KEY_CLIENT_ERRORS, clientErrors);
	}

	@Override
	public void addDialog(Dialog dialog) {
		dialogs.add(dialog);
		fire(DialogModel.KEY_DIALOGS, serviceErrors);
	}

	@Override
	public void addRawDialog(RawDialog rawDialog) {
		rawDialogs.add(rawDialog);
		fire(DialogModel.KEY_RAW_DIALOGS, rawDialogs);
	}

	@Override
	public void addServiceError(UIServiceError serviceError) {
		serviceErrors.add(serviceError);
		fire(DialogModel.KEY_SERVICE_ERRORS, serviceErrors);
	}

	@Override
	public Class<?> getTutorialForClass() {
		return tutorialForClass;
	}

	@Override
	public boolean isWaiting() {
		return waiting;
	}

	@Override
	public UIClientError peakClientError() {
		return clientErrors.peek();
	}

	@Override
	public UIServiceError peakServiceError() {
		return serviceErrors.peek();
	}

	@Override
	public Dialog peekDialog() {
		return dialogs.peek();
	}

	@Override
	public RawDialog peekRawDialog() {
		return rawDialogs.peek();
	}

	@Override
	public UIClientError pollClientError() {
		return clientErrors.poll();
	}

	@Override
	public Dialog pollDialog() {
		return dialogs.poll();
	}

	@Override
	public RawDialog pollRawDialog() {
		return rawDialogs.poll();
	}

	@Override
	public UIServiceError pollServiceError() {
		return serviceErrors.poll();
	}

	@Override
	public void setTutorialForClass(Class<?> tutorialForClass) {
		this.tutorialForClass = tutorialForClass;
		fire(KEY_TUTORIAL_FOR_CLASS, tutorialForClass);
	}

	@Override
	public void setWaiting(boolean waiting) {
		this.waiting = waiting;
		fire(KEY_WAITING, waiting);
	}

}
