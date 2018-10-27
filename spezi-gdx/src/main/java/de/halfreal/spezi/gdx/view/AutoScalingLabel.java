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
        float oldFontScale = getFontScaleX();
        float scaleFactorX = 1f;
        float scaleFactorY = 1f;

        float width = getPrefWidth();
        if (width > getWidth()) {
            scaleFactorX = getWidth() / width;
        }

        float height = getPrefHeight();
        if (height > getHeight()) {
            scaleFactorY = getHeight() / height;
        }

        float scaleFactor = Math.min(scaleFactorX, scaleFactorY);
        setFontScale(oldFontScale * scaleFactor);
    }
}
