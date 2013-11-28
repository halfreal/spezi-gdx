package de.halfreal.spezi.gdx.view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.halfreal.spezi.gdx.sector.SectorId;

public class ImageSector extends SectorActor {

	public enum ScaleType {
		fillSector,
		imageMinSize,
		percent
	}

	private boolean dynamicScaling = false;
	private Drawable image;

	private float percentHeight = 1;
	private float percentWidth = 1;
	private ScaleType scaleType;

	public ImageSector(Drawable image, TileMap map, SectorId id) {
		super(map);
		scaleType = ScaleType.fillSector;
		this.image = image;
		setSectorId(id);
	}

	// TODO add option for aligning images
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {

		super.draw(batch, parentAlpha);
		if (image == null) {
			return;
		}
		batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a
				* parentAlpha);
		TextureRegion region = null;
		if (image instanceof TextureRegionDrawable) {
			region = ((TextureRegionDrawable) image).getRegion();
		}

		float rotation = getRotation();
		if (scaleType == ScaleType.fillSector) {
			setWidth(getSectorWidth());
			setHeight(getSectorHeight());
			float originX = getOriginX() == 0 ? getWidth() / 2 : getOriginX();
			float originY = getOriginY() == 0 ? getHeight() / 2 : getOriginY();
			if (getRotation() == 0 || region == null) {
				image.draw(batch, getX(), getY(), getSectorWidth(),
						getSectorWidth());
			} else {
				batch.draw(region, getX() - originX, getY() - originY, originX,
						originY, getSectorWidth(), getSectorHeight(),
						getScaleX(), getScaleY(), rotation);
			}

		} else if (scaleType == ScaleType.imageMinSize) {
			setWidth(image.getMinWidth());
			setHeight(image.getMinHeight());

			float originX = getOriginX() == 0 ? getWidth() / 2 : getOriginX();
			float originY = getOriginY() == 0 ? getHeight() / 2 : getOriginY();

			if (rotation == 0 || region == null) {
				image.draw(batch, getX(), getY(), image.getMinWidth()
						* getScaleX(), image.getMinHeight() * getScaleY());
				// image.draw(batch, getX() - image.getMinWidth() / 2, getY()
				// - image.getMinHeight() / 2, image.getMinWidth(),
				// image.getMinHeight());
			} else {
				batch.draw(region, getX() - image.getMinWidth() / 2 - originX,
						getY() - image.getMinHeight() / 2 - originY, originX,
						originY, image.getMinWidth(), image.getMinHeight(),
						getScaleX(), getScaleY(), rotation);

			}

		} else if (scaleType == ScaleType.percent) {
			setWidth(getSectorWidth() * percentWidth);
			setHeight(getSectorWidth() * percentHeight);
			float originX = getOriginX() == 0 ? getWidth() / 2 : getOriginX();
			float originY = getOriginY() == 0 ? getHeight() / 2 : getOriginY();

			if (rotation == 0 || region == null) {
				image.draw(batch, getX(), getY(), getSectorWidth()
						* percentWidth, getSectorWidth() * percentHeight);
			} else {
				batch.draw(region, getX(), getY(), originX, originY,
						getSectorWidth() * percentWidth, getSectorWidth()
								* percentHeight, getScaleX(), getScaleY(),
						rotation);

			}

		}

	}

	public float getPercentHeight() {
		return percentHeight;
	}

	public float getPercentWidth() {
		return percentWidth;
	}

	@Override
	protected void onChangedScaling() {
		super.onChangedScaling();
		if (dynamicScaling) {
			setScale((float) getZoomFactor());
		}
	}

	public void setDynamicScaling(boolean dynamicScaling) {
		this.dynamicScaling = dynamicScaling;
	}

	public void setPercentHeight(float percentHeight) {
		this.percentHeight = percentHeight;
	}

	public void setPercentWidth(float percentWidth) {
		this.percentWidth = percentWidth;
	}

	public void setScaleType(ScaleType scaleType) {
		this.scaleType = scaleType;
	}

}
