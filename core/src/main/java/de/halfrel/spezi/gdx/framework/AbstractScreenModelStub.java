package de.halfrel.spezi.gdx.framework;

import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

import de.halfreal.spezi.mvc.AbstractModel;
import de.halfreal.spezi.mvc.Model;

@Model
public class AbstractScreenModelStub extends AbstractModel {

	Queue<UIClientError> clientErrors;

	Queue<Dialog> dialogs;

	Queue<UIServiceError> serviceErrors;

	Class<?> tutorialForClass;

	boolean waiting;

	public AbstractScreenModelStub() {
		clientErrors = new LinkedList<UIClientError>();
		serviceErrors = new LinkedList<UIServiceError>();
		dialogs = new LinkedList<Dialog>();
	}

}
