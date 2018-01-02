package de.halfreal.spezi.gdx.view;

import static de.halfreal.spezi.gdx.framework.RelativeLayout.*;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class HorizontalWidgetGroup extends WidgetGroup {

	private float padding;
	private int rows;

	private float sideOffset;

	public HorizontalWidgetGroup(int rows) {
		this.rows = rows;
	}

	public float getPadding() {
		return padding;
	}

	@Override
	public float getPrefWidth() {
		return super.getWidth();
	}

	public int getRows() {
		return rows;
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
			int i = 0;
			for (Actor actor : getChildren()) {
				if (lastActor != null) {
					alignSame(actor, lastActor);

					if (i % rows == 0) {
						alignRightOf(actor, lastActor, padding);
						alignTop(actor, this);
					} else {
						alignBelow(actor, lastActor, padding);
					}
				} else {
					alignTop(actor, this);
					alignLeft(actor, this);
				}
				if (i % rows == 0) {
					width += (actor.getWidth() + padding);
				}
				lastActor = actor;
				i++;
			}

			setWidth(width + sideOffset);
		}
		super.validate();
	}

}
