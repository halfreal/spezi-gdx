package de.halfreal.spezi.gdx.view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import de.halfreal.spezi.gdx.sector.SectorHelper;
import de.halfreal.spezi.gdx.sector.SectorId;

/**
 * An actor drawn on a particular sector. It is not compatible for moving or
 * scaling actions.
 * 
 * @author Simon Joecks halfreal.de (c)
 * 
 */
public class SectorActor extends Actor {

	private SectorId id;
	private TileMap map;
	private int oldZoom;
	private float percentX;
	private float percentY;
	private int sectorWidth;

	public SectorActor(TileMap map) {
		this.map = map;
		setTouchable(Touchable.disabled);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if (map.getZoom() != oldZoom) {
			onChangedScaling();
		}
		oldZoom = map.getZoom();

		if (id == null) {
			return;
		}

		sectorWidth = SectorHelper.getRelativeSectorEdgeWidth(id.z,
				map.getZoom());

		setX(map.getXOnScreen(getId(), getWidth(), percentX * getSectorWidth()));
		setY(map.getYOnScreen(getId()) + percentY * getSectorWidth());
	}

	public SectorId getId() {
		return id;
	}

	public TileMap getMap() {
		return map;
	}

	public float getPercentX() {
		return percentX;
	}

	public float getPercentY() {
		return percentY;
	}

	public int getSectorHeight() {
		return sectorWidth;
	}

	public int getSectorWidth() {
		return sectorWidth;
	}

	/**
	 * 
	 * @return the scaling factor in correspendence to a standart visible
	 *         sector, which is usaly id+1
	 */
	public double getZoomFactor() {
		return Math.pow(2, (getMap().getZoom() - getId().z - 1));
	}

	/**
	 * if the map changed it zoom factor notify the actor that it might need to
	 * rescale its elements
	 */
	protected void onChangedScaling() {
	}

	public void setId(SectorId id) {
		this.id = id;
	}

	public void setMap(TileMap map) {
		this.map = map;
	}

	public void setPercentX(float percentX) {
		this.percentX = percentX;
	}

	public void setPercentY(float percentY) {
		this.percentY = percentY;
	}

	public void setSectorId(SectorId id) {
		this.id = id;
	}

}
