package de.halfreal.spezi.gdx.view;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AutoScalingLabel extends Label {

	public AutoScalingLabel(CharSequence text, LabelStyle style) {
		super(text, style);
		setText(text);
	}

	public AutoScalingLabel(CharSequence text, Skin skin, String styleName) {
		super(text, skin, styleName);
		setText(text);
	}

	@Override
	public void setText(CharSequence newText) {
		super.setText(newText);
		float oldFontScale = getStyle().font.getScaleX();
		float width = getPrefWidth();
		if (width > getWidth()) {
			float scaleFactor = getWidth() / width;
			setFontScale(scaleFactor * oldFontScale);
		}
	}

}
