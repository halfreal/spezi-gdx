package de.halfreal.spezi.gdx.sector;


public class SectorHelper {

	private static float[] toPoint = new float[2];

	public static int activityReduction(long lastUpdate, long time,
			long activityReductionTime) {
		long diff = time / lastUpdate;
		int reduction = (int) (diff / activityReductionTime);
		return reduction;
	}

	public static int getMapExponent() {
		return 8;
	}

	public static int getMapX(int sectorX, int sectorZoom, int mapZoom) {
		return (sectorX << getRelativeMapExponent(sectorZoom, mapZoom));
	}

	public static int getMapX(SectorId id, int mapZoom) {
		return getMapX(id.x, id.z, mapZoom);
	}

	public static int getMapY(int sectorY, int sectorZoom, int mapZoom) {
		return (sectorY << getRelativeMapExponent(sectorZoom, mapZoom));
	}

	public static int getMapY(SectorId id, int mapZoom) {
		return getMapY(id.yInverse(), id.z, mapZoom);
	}

	public static int getMaxX(int zoomLevel) {
		return (1 << zoomLevel) - 1;
	}

	public static int getMaxY(int zoomLevel) {
		return (1 << zoomLevel) - 1;
	}

	public static int getRelativeMapExponent(int sectorZoom, int mapZoom) {
		return 8 + (mapZoom - sectorZoom);
	}

	public static int getRelativeSectorEdgeWidth(int sectorZoom, int mapZoom) {
		return 1 << getRelativeMapExponent(sectorZoom, mapZoom);
	}

	public static int getSectorEdgeWidth() {
		return 1 << getMapExponent();
	}

	public static boolean isValidId(int x, int y, int z) {
		return isValidX(x, z) && isValidY(y, z) && isValidZ(z);
	}

	public static boolean isValidId(SectorId sectorId) {
		if (sectorId == null) {
			return false;
		} else {
			return isValidId(sectorId.x, sectorId.y, sectorId.z);
		}
	}

	public static boolean isValidX(int x, int zoomLevel) {
		return x >= 0 && x <= getMaxX(zoomLevel);
	}

	public static boolean isValidY(int y, int zoomLevel) {
		return y >= 0 && y <= getMaxY(zoomLevel);
	}

	public static boolean isValidZ(int z) {
		return z >= 0 && z <= SectorId.MAX_ZOOM;
	}

	public static SectorId sectorIdForGeoCoordinates(float lat, float lng,
			int zoom) {
		GeoHelper.toPoint(lat, lng, zoom, toPoint);
		return sectorIdForScreenCoordinates(toPoint[0], toPoint[1], zoom);
	}

	public static SectorId sectorIdForScreenCoordinates(float x, float y, int z) {
		int sectorEdgeWidth = getSectorEdgeWidth();

		int max = getMaxX(z) + 1;
		int sectorX = ((int) (x / sectorEdgeWidth)) % max;
		int sectorY = ((int) (y / sectorEdgeWidth)) % max;

		SectorId id = new SectorId(sectorX, sectorY, z);
		id.y = id.yInverse();
		return isValidId(id) ? id : null;
	}

}
