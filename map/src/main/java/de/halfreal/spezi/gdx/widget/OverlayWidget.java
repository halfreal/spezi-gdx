package de.halfreal.spezi.gdx.widget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import de.halfreal.spezi.gdx.sector.SectorId;
import de.halfreal.spezi.gdx.view.TileMap;
import de.halfrel.spezi.gdx.framework.AbstractScreen;
import de.halfrel.spezi.gdx.framework.Controller;
import de.halfrel.spezi.gdx.framework.Model;
import de.halfrel.spezi.gdx.framework.ScreenWidget;

public class OverlayWidget<C extends Controller<MODEL>, MODEL extends Model>
		extends ScreenWidget<C, MODEL> {

	private static Logger log = LoggerFactory.getLogger(OverlayWidget.class);

	private int blendDstFunc = GL11.GL_ONE_MINUS_SRC_ALPHA;
	protected boolean blending;
	private int blendSrcFunc = GL11.GL_SRC_ALPHA;
	protected TileMap map;

	private MapWidget mapWidget;

	public OverlayWidget(MapWidget mapWidget, C controller,
			AbstractScreen<?, ?> screen) {
		super(controller, screen);
		map = mapWidget.getMapView();
		this.mapWidget = mapWidget;

	}

	protected void disableBlending() {
		Gdx.gl.glDisable(GL10.GL_BLEND);
	}

	// @Override
	// public boolean addCaptureListener(EventListener listener) {
	// return mapWidget.addCaptureListener(listener);
	// }
	//
	// @Override
	// public boolean addListener(EventListener listener) {
	// return mapWidget.addListener(listener);
	// }

	@Override
	public void dispose() {
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		validate();
		batch.setProjectionMatrix(map.getLastMapProjection());
		// drawOnMap(batch, parentAlpha);
		for (Actor child : getChildren()) {
			child.draw(batch, parentAlpha);
		}
		batch.setProjectionMatrix(map.getLastStageProjection());
	}

	/**
	 * draw on the map with the correct projection
	 * 
	 * @param batch
	 * @param parentAlpha
	 */
	protected void drawOnMap(SpriteBatch batch, float parentAlpha) {

	}

	protected void enableBlending() {
		Gdx.gl.glEnable(GL10.GL_BLEND);
		Gdx.gl.glBlendFunc(blendSrcFunc, blendDstFunc);
	}

	@Override
	public float getHeight() {
		return map.getHeight();
	}

	public TileMap getMap() {
		return map;
	}

	public MapWidget getMapWidget() {
		return mapWidget;
	}

	@Override
	public float getPrefHeight() {
		return map.getHeight();
	}

	@Override
	public float getPrefWidth() {
		return map.getWidth();
	}

	@Override
	public float getWidth() {
		return map.getWidth();
	}

	@Override
	public float getX() {
		return map.getX();
	}

	protected float getXOnScreen(SectorId id, float width) {
		return map.getXOnScreen(id, width);
	}

	@Override
	public float getY() {
		return map.getY();
	}

	protected float getYOnScreen(SectorId id) {
		return map.getYOnScreen(id);
	}

}
