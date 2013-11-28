package de.halfreal.spezi.gdx.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import de.halfreal.spezi.gdx.sector.GeoHelper;
import de.halfreal.spezi.gdx.view.TileMap;

public class GeoActions {

	public static class GeoMoveToAction extends TemporalAction {

		private TileMap map;
		private float startLat, startLng, endLat, endLng;
		private float[] toGeo;
		private float[] toPoint;

		public GeoMoveToAction() {
			toGeo = new float[2];
			toPoint = new float[2];

		}

		@Override
		public void setActor(Actor actor) {
			if (actor != null && !(actor instanceof TileMap)) {
				throw new RuntimeException(
						"Must use Geo Actions on a MapWidget");
			}
			map = (TileMap) actor;

			if (map != null) {

				GeoHelper.toLatLng(map.getCenter().x, map.getCenter().y,
						map.getZoom(), toGeo);
				startLat = toGeo[0];
				startLng = toGeo[1];

			}
			super.setActor(actor);
		}

		public void setPosition(float lat, float lng) {
			endLat = lat;
			endLng = lng;
		}

		@Override
		protected void update(float percent) {

			GeoHelper.toPoint(startLat, startLng, map.getZoom(), toPoint);
			float startX = toPoint[0];
			float startY = toPoint[1];
			GeoHelper.toPoint(endLat, endLng, map.getZoom(), toPoint);
			float endX = toPoint[0];
			float endY = toPoint[1];

			boolean changeDirection = Math.abs(startX - endX) > map.getWidth() / 2;
			float nextX = startX + (changeDirection ? -1f : 1f)
					* (endX - startX) * percent;
			float nextY = startY + (changeDirection ? -1f : 1f)
					* (endY - startY) * percent;
			actor.setPosition(map.toMapX(nextX), map.toMapY(nextY));
			Gdx.graphics.requestRendering();
		}

	}

	public static class GeoZoomToAction extends TemporalAction {

		private int endZoom;
		private float lastPercent;
		private TileMap map;
		private float startProgress;
		float startZoom;
		private float totalProgress;

		@Override
		public void setActor(Actor actor) {
			if (actor != null && !(actor instanceof TileMap)) {
				throw new RuntimeException(
						"Must use Geo Actions on a MapWidget");
			}
			map = (TileMap) actor;

			if (map != null) {
				startZoom = map.getZoom();
				startProgress = map.getProgress();
				totalProgress = Math.abs(startZoom - endZoom)
						- Math.abs(startProgress);
				lastPercent = 0;
			}
			super.setActor(actor);
		}

		public void setZoom(int endZoom) {
			this.endZoom = endZoom;
		}

		@Override
		protected void update(float percent) {
			float step = (percent - lastPercent) * totalProgress;
			lastPercent = percent;
			if (endZoom > map.getZoom()) {
				map.zoomStep(step);
			} else {
				map.zoomStep(-step);
			}
			log.debug("step: " + step + ", z:" + map.getZoom() + ", zp:"
					+ map.getProgress());

			if (percent == 1 && map.getZoom() != endZoom) {
				float endStep = 1 - Math.abs(map.getProgress());
				if (endZoom > map.getZoom()) {
					map.zoomStep(endStep);
				} else {
					map.zoomStep(-endStep);
				}
				log.debug("ENDSTEP: " + endStep);
			}
			Gdx.graphics.requestRendering();
		}
	}

	private static Logger log = LoggerFactory.getLogger(GeoActions.class);

	public static GeoMoveToAction moveTo(float lat, float lng, float duration) {
		return moveTo(lat, lng, duration, Interpolation.linear);
	}

	public static GeoMoveToAction moveTo(float lat, float lng, float duration,
			Interpolation interpolation) {
		GeoMoveToAction geoMoveToAction = new GeoMoveToAction();
		geoMoveToAction.setDuration(duration);
		geoMoveToAction.setPosition(lat, lng);
		geoMoveToAction.setInterpolation(interpolation);
		return geoMoveToAction;
	}

	public static GeoZoomToAction zoomTo(int zoom, float duration) {
		return zoomTo(zoom, duration, Interpolation.linear);
	}

	public static GeoZoomToAction zoomTo(int zoom, float duration,
			Interpolation interpolation) {
		GeoZoomToAction action = new GeoZoomToAction();
		action.setDuration(duration);
		action.setZoom(zoom);
		action.setInterpolation(interpolation);
		return action;
	}

}
