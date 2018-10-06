package de.halfreal.spezi.gdx.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

import de.halfreal.spezi.gdx.framework.RelativeLayout;
import de.halfreal.spezi.gdx.system.Assets;

public class HorizontalProgressBar extends ProgressBar {

	public HorizontalProgressBar(ProgressBarStyle style, int amount,
			int maxAmount, boolean standardGrowth, float width) {
		super(style, amount, maxAmount, width, standardGrowth);
		computeDimensions(width);
	}

	public HorizontalProgressBar(Skin skin, int amount, int maxAmount,
			boolean standardGrowth, float width) {
		super(skin, amount, maxAmount, width, standardGrowth);
		computeDimensions(width);
	};

	public HorizontalProgressBar(Skin skin, String styleName, int amount,
			int maxAmount, boolean standardGrowth, boolean showLabel,
			float width) {
		super(skin, styleName, amount, maxAmount, showLabel, width,
				standardGrowth);
		computeDimensions(width);
	}

	public HorizontalProgressBar(Skin skin, String styleName, int amount,
			int maxAmount, boolean standardGrowth, float width) {
		super(skin, styleName, amount, maxAmount, width, standardGrowth);
		computeDimensions(width);
	}

	public HorizontalProgressBar(Skin skin, String styleName, int amount,
			int maxAmount, boolean standardGrowth, float width,
			String formatDigits) {
		super(skin, styleName, amount, maxAmount, width, standardGrowth,
				formatDigits);
		computeDimensions(width);
	}

	public void computeDimensions(float width) {
		if (isShowLabel()) {
			addActor(getAmountLabel());

			float imagePositionY = getAmountLabel().getHeight() / 2
					- getBackgroundImage().getHeight() / 2;
			getBackgroundImage().setY(imagePositionY);
			getImage().setY(imagePositionY);
		}
		float barWidth = width - getAmountLabel().getWidth();
		getBackgroundImage().setWidth(isShowLabel() ? barWidth : width);
		getImage().setWidth(isShowLabel() ? barWidth : width);

		if (isShowLabel()) {
			setHeight(getAmountLabel().getHeight());
			setWidth(getAmountLabel().getWidth()
					+ getBackgroundImage().getWidth() + Assets.dip(1));
		} else {
			setHeight(getBackgroundImage().getHeight());
			setWidth(getBackgroundImage().getWidth());
		}
		if (isStandardGrowth()) {
			RelativeLayout.alignRight(getAmountLabel(), this);
		} else {
			RelativeLayout.alignRight(getBackgroundImage(), this);
			RelativeLayout.alignRight(getImage(), this);
		}
	}

	@Override
	protected Image createAnimatedImage(Drawable foreground) {
		return new Image(foreground) {
			private int lastAmount;

			@Override
			public void draw(Batch batch, float parentAlpha) {
				batch.flush();
				float width;
				float clipStartPosition;
				if (isAnimationRunning()) {
					setElapsedTime(getElapsedTime()
							+ Gdx.graphics.getDeltaTime());
					float oldWidth = getWidth()
							* (getOldAmount() / getMaxAmount());
					float newWidth = getWidth()
							* ((float) getAmount() / getMaxAmount());
					float elapsedPercentage = (getElapsedTime() / getAnimationDuration());
					width = oldWidth + (newWidth - oldWidth)
							* elapsedPercentage;

					if (getElapsedTime() >= getAnimationDuration()) {
						stopAnimation();
						setCurrentAmount(getAmount());
						setOldAmount(getAmount());
						setElapsedTime(0);
						fire(Pools.obtain(ChangeEvent.class));
						width = newWidth;
					}

					setCurrentAmount((int) (getOldAmount() + (getAmount() - getOldAmount())
							* elapsedPercentage));
					if (Math.abs(lastAmount - getCurrentAmount()) >= 1) {
						fire(Pools.obtain(ChangeEvent.class));
						lastAmount = getCurrentAmount();
					}
					clipStartPosition = getX()
							+ (isStandardGrowth() ? 0 : getWidth() - width);
				} else {
					clipStartPosition = getX()
							+ (isStandardGrowth() ? 0
									: getWidth()
											* (1 - (float) getAmount()
													/ getMaxAmount()));
					width = getWidth() * ((float) getAmount() / getMaxAmount());
				}
				if (clipBegin(clipStartPosition, getY(), width, getHeight())) {
					super.draw(batch, parentAlpha);
					batch.flush();
					clipEnd();
				}

			}
		};
	}

}
