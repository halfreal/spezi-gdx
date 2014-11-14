package de.halfreal.spezi.gdx.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

import de.halfreal.spezi.gdx.framework.RelativeLayout;

public class VerticalProgressBar extends ProgressBar {

	public VerticalProgressBar(Skin skin, String styleName, int amount,
			int maxAmount, float barWidth, float barHeight) {
		super(skin, styleName, amount, maxAmount, false, barHeight, false);
		computeDimensions(barWidth, barHeight);
	}

	public void computeDimensions(float width, float height) {
		getBackgroundImage().setHeight(height);
		getBackgroundImage().setWidth(width);
		getImage().setHeight(height);
		getImage().setWidth(width);

		setHeight(getBackgroundImage().getHeight());
		setWidth(getBackgroundImage().getWidth());

		if (!isStandardGrowth()) {
			RelativeLayout.alignTop(getImage(), this);
		}
	}

	@Override
	protected Image createAnimatedImage(Drawable foreground) {
		return new Image(foreground) {
			private int lastAmount;

			@Override
			public void draw(Batch batch, float parentAlpha) {
				batch.flush();
				if (isAnimationRunning()) {
					setElapsedTime(getElapsedTime()
							+ Gdx.graphics.getDeltaTime());
					float oldHeight = getHeight()
							* (getOldAmount() / getMaxAmount());
					float newHeight = getHeight()
							* ((float) getAmount() / getMaxAmount());
					float height = oldHeight + (newHeight - oldHeight)
							* (getElapsedTime() / getAnimationDuration());

					if (getElapsedTime() >= getAnimationDuration()) {
						stopAnimation();
						setCurrentAmount(getAmount());
						setOldAmount(getAmount());
						setElapsedTime(0);
						fire(Pools.obtain(ChangeEvent.class));
						height = newHeight;
					}

					setCurrentAmount((int) (getOldAmount() + (getAmount() - getOldAmount())
							* (getElapsedTime() / getAnimationDuration())));
					if (Math.abs(lastAmount - getCurrentAmount()) >= 1) {
						fire(Pools.obtain(ChangeEvent.class));
						lastAmount = getCurrentAmount();
					}

					if (clipBegin(getX(), getY(), getWidth(), height)) {
						super.draw(batch, parentAlpha);
						batch.flush();
						clipEnd();
					}
				} else {
					float height = getHeight()
							* ((float) getAmount() / getMaxAmount());
					if (clipBegin(getX(), getY(), getWidth(), height)) {
						super.draw(batch, parentAlpha);
						batch.flush();
						clipEnd();
					}
				}

			}
		};
	}

}
