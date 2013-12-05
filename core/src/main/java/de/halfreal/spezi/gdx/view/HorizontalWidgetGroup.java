package de.halfreal.spezi.gdx.view;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import de.halfreal.spezi.gdx.framework.RelativeLayout;

public class HorizontalWidgetGroup extends WidgetGroup {

	private float padding;

	private float sideOffset;

	public float getPadding() {
		return padding;
	}

	@Override
	public float getPrefWidth() {
		return super.getWidth();
	}

	public float getSideOffset() {
		return sideOffset;
	}

	public void setPadding(float padding) {
		this.padding = padding;
	}

	public void setSideOffset(float sideOffset) {
		this.sideOffset = sideOffset;
		invalidate();
	}

	@Override
	public void validate() {
		if (needsLayout()) {
			Actor lastActor = null;
			float width = 0;

			for (Actor actor : getChildren()) {
				if (lastActor != null) {
					RelativeLayout.alignRightOf(actor, lastActor, padding);
				} else {
					actor.setX(sideOffset);
				}
				width += (actor.getWidth() + padding);
				lastActor = actor;
			}
			setWidth(width + 2 * sideOffset);
		}
		super.validate();
	}
}
