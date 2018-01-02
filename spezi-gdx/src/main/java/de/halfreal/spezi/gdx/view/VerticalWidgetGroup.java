package de.halfreal.spezi.gdx.view;

import static de.halfreal.spezi.gdx.framework.RelativeLayout.*;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class VerticalWidgetGroup extends WidgetGroup {

	private float bottomOffset;
	private int columns;
	private float padding;

	public VerticalWidgetGroup(int columns) {
		this.columns = columns;
	}

	public float getBottomOffset() {
		return bottomOffset;
	}

	public int getColumns() {
		return columns;
	}

	public float getPadding() {
		return padding;
	}

	@Override
	public float getPrefHeight() {
		return super.getHeight();
	}

	public void setBottomOffset(float bottomOffset) {
		this.bottomOffset = bottomOffset;
		invalidate();
	}

	public void setPadding(float padding) {
		this.padding = padding;
	}

	@Override
	public void validate() {
		if (needsLayout()) {
			Actor lastActor = null;
			float height = 0;
			int i = 0;
			for (Actor actor : getChildren()) {
				if (lastActor != null) {
					alignSame(actor, lastActor);

					if (i % columns == 0) {
						alignBelow(actor, lastActor, padding);
						alignLeft(actor, this, padding);
					} else {
						alignRightOf(actor, lastActor, padding);
					}
				} else {
					alignTop(actor, this, padding);
					alignLeft(actor, this, padding);
				}
				if (i % columns == 0) {
					height += (actor.getHeight() + padding);
				}
				lastActor = actor;
				i++;
			}

			setHeight(height + bottomOffset);
		}
		super.validate();
	}
}
