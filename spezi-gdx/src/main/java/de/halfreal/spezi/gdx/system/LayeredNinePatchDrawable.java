package de.halfreal.spezi.gdx.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class LayeredNinePatchDrawable extends NinePatchDrawable {

	private Drawable corner;
	private int cornerId;

	public LayeredNinePatchDrawable(NinePatch drawable) {
		super(drawable);
	}

	/**
	 * cornerId enumerates the ninepatch corners from bottom-left clock-wise
	 * 
	 * @param ninepatch
	 * @param corner
	 * @param cornerId
	 */
	public LayeredNinePatchDrawable(NinePatch ninepatch, Drawable corner,
			int cornerId) {
		super(ninepatch);
		this.corner = corner;
		this.cornerId = cornerId;
	}

	public LayeredNinePatchDrawable(NinePatchDrawable ninepatch) {
		super(ninepatch);
	}

	public LayeredNinePatchDrawable(NinePatchDrawable ninepatch,
			Drawable corner, int cornerId) {
		super(ninepatch);
		this.corner = corner;
		this.cornerId = cornerId;
	}

	@Override
	public void draw(Batch batch, float x, float y, float width,
					 float height) {
		getPatch().draw(batch, x, y, width, height);

		if (corner != null) {
			float cornerX = x;
			float cornerY = y;

			switch (cornerId) {
			case 0:
				break;
			case 1:
				cornerY = y + height - corner.getMinHeight();
				break;
			case 2:
				cornerY = y + height - corner.getMinHeight();
				cornerX = x + width - corner.getMinWidth();
				break;
			case 3:
				cornerX = x + width - corner.getMinWidth();
				break;
			default:
				break;
			}

			corner.draw(batch, cornerX, cornerY, corner.getMinWidth(),
					corner.getMinHeight());

		}
	}

	public Drawable getCorner() {
		return corner;
	}

	public int getCornerId() {
		return cornerId;
	}

	public void setCorner(Drawable corner) {
		this.corner = corner;
	}

	public void setCornerId(int cornerId) {
		this.cornerId = cornerId;
	}

}
