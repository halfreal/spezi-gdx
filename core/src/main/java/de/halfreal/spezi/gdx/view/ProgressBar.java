package de.halfreal.spezi.gdx.view;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;

public abstract class ProgressBar extends WidgetGroup {

	public static class ProgressBarStyle {

		public Drawable background;
		public Drawable foreground;
		public LabelStyle labelStyle;

	}

	private int amount;
	private Label amountLabel;
	private boolean animatable;
	private boolean animated;
	private float animationDuration = 1f;
	private Image backgroundImage;
	private int currentAmount;
	private float elapsedTime = 0;
	private Image image;
	private int maxAmount;
	private NumberFormat nf = new DecimalFormat("00");
	private float oldAmount = 0;
	private ProgressBarStyle progressBarStyle;
	private boolean showLabel = true;
	private boolean standardGrowth;

	public ProgressBar(ProgressBarStyle style, int amount, int maxAmount,
			float relevantSideLength, boolean standardGrowth) {
		this.amount = amount;
		this.maxAmount = maxAmount;
		progressBarStyle = style;
		this.standardGrowth = standardGrowth;
		init();
	}

	public ProgressBar(Skin skin, int amount, int maxAmount,
			float relevantSideLength, boolean standardGrowth) {
		this.standardGrowth = standardGrowth;
		ProgressBarStyle progressBarItemStyle = skin.get("default",
				ProgressBarStyle.class);
		this.amount = amount;
		this.maxAmount = maxAmount;
		progressBarStyle = progressBarItemStyle;
		init();
	}

	public ProgressBar(Skin skin, String styleName, int amount, int maxAmount,
			boolean showLabel, float relevantSideLength, boolean standardGrowth) {
		this.showLabel = showLabel;
		this.amount = amount;
		this.maxAmount = maxAmount;
		this.standardGrowth = standardGrowth;
		progressBarStyle = skin.get(styleName, ProgressBarStyle.class);
		init();
	}

	public ProgressBar(Skin skin, String styleName, int amount, int maxAmount,
			float relevantSideLength, boolean standardGrowth) {
		this.amount = amount;
		this.maxAmount = maxAmount;
		this.standardGrowth = standardGrowth;
		progressBarStyle = skin.get(styleName, ProgressBarStyle.class);
		init();
	}

	public ProgressBar(Skin skin, String styleName, int amount, int maxAmount,
			float relevantSideLength, boolean standardGrowth,
			String formatDigits) {
		this.standardGrowth = standardGrowth;
		nf = new DecimalFormat(formatDigits);
		this.amount = amount;
		this.maxAmount = maxAmount;
		progressBarStyle = skin.get(styleName, ProgressBarStyle.class);
		init();
	}

	@Override
	public void act(float delta) {

		super.act(delta);
	}

	protected abstract Image createAnimatedImage(Drawable foreground);

	public int getAmount() {
		return amount;
	}

	public Label getAmountLabel() {
		return amountLabel;
	}

	public float getAnimationDuration() {
		return animationDuration;
	}

	public Image getBackgroundImage() {
		return backgroundImage;
	}

	public int getCurrentAmount() {
		return currentAmount;
	}

	public float getElapsedTime() {
		return elapsedTime;
	}

	public Image getImage() {
		return image;
	}

	public int getMaxAmount() {
		return maxAmount;
	}

	public float getOldAmount() {
		return oldAmount;
	}

	private void init() {
		if (progressBarStyle == null) {
			throw new RuntimeException("ProgressBarItemStyle not set!");
		}

		amountLabel = new Label(nf.format(amount), progressBarStyle.labelStyle);
		amountLabel.pack();
		backgroundImage = new Image(progressBarStyle.background);
		image = createAnimatedImage(progressBarStyle.foreground);
		if (showLabel) {
			image.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					amountLabel.setText(nf.format(currentAmount));
				}
			});
		}
		backgroundImage.setScaling(Scaling.stretch);
		image.setScaling(Scaling.stretch);
		addActor(backgroundImage);
		addActor(image);

	}

	public boolean isAnimatable() {
		return animatable;
	}

	public boolean isAnimationRunning() {
		return animated;
	}

	public boolean isShowLabel() {
		return showLabel;
	}

	public boolean isStandardGrowth() {
		return standardGrowth;
	}

	public void setAmountLabel(Label amountLabel) {
		this.amountLabel = amountLabel;
	}

	public void setAnimatable(boolean animatable) {
		this.animatable = animatable;
	}

	public void setAnimationDuration(float animationDuration) {
		this.animationDuration = animationDuration;
	}

	public void setBackgroundImage(Image backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	@Override
	public void setColor(Color color) {
		super.setColor(color);
	}

	@Override
	public void setColor(float r, float g, float b, float a) {
		super.setColor(r, g, b, a);
	}

	public void setCurrentAmount(int currentAmount) {
		this.currentAmount = currentAmount;
	}

	public void setElapsedTime(float elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public void setMaxAmount(int maxAmount) {
		this.maxAmount = maxAmount;
	}

	public void setNewBounds(int newMin, int newDesired, int newMax) {
		amount = newDesired;
		setOldAmount(newMin);
		maxAmount = newMax;
		setAnimationDuration(getAnimationDuration() - getElapsedTime());
		setElapsedTime(0);
	}

	public void setOldAmount(float oldAmount) {
		this.oldAmount = oldAmount;
	}

	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
	}

	public void setstandardGrowth(boolean standardGrowth) {
		this.standardGrowth = standardGrowth;
	}

	public void setStyle(ProgressBarStyle progressBarItemStyle) {
		progressBarStyle = progressBarItemStyle;
		if (image != null) {
			image.setDrawable(progressBarItemStyle.foreground);
			backgroundImage.setDrawable(progressBarItemStyle.background);
			amountLabel.setStyle(progressBarItemStyle.labelStyle);
		}
	}

	public void setStyle(Skin skin, String styleName) {
		progressBarStyle = skin.get(styleName, ProgressBarStyle.class);
		if (image != null) {
			image.setDrawable(progressBarStyle.foreground);
			backgroundImage.setDrawable(progressBarStyle.background);
			amountLabel.setStyle(progressBarStyle.labelStyle);
		}
	}

	public void stopAnimation() {
		animated = false;
	}

	public boolean update(int newAmount) {
		animated = animatable;
		boolean changed = false;
		if (newAmount != amount) {
			changed = true;
		}
		if (!isAnimationRunning()) {
			amountLabel.setText(nf.format(newAmount));
		}
		setOldAmount(amount);
		amount = newAmount;
		return changed;
	}

}
