package de.halfreal.spezi.gdx.sector;

public class GeoHelper {

	public static final float EAST_LNG_MAX_VALUE = 180f;
	public static final float NORTH_LAT_MAX_VALUE = 85.0511f;
	public static final float SOUTH_LAT_MAX_VALUE = -85.0511f;
	public static final float WEST_LNG_MAX_VALUE = -180f;

	public static boolean equalsInInterval(float value, float intervalBegin,
			float intervalEnd) {
		return value >= intervalBegin && value <= intervalEnd;
	}

	public static void main(String[] args) {

		GeoHelper helper = new GeoHelper();

		float[] coords = new float[2];
		long[] point = new long[2];

		point[0] = 10;
		point[1] = 10;

		helper.inverseMercator(point[0], point[1], coords);
		helper.toMercator(coords[0], coords[1], point);

		System.out.println(point[0] + ":" + point[1]);
		System.out.println(coords[0] + ":" + coords[1]);
	}

	/**
	 * TODO
	 * 
	 * @param x
	 * @param y
	 * @param zoom
	 * @param toPoint
	 */
	public static void toLatLng(float x, float y, int zoom, float[] toGeo) {
		double n = 1 << (8 + zoom);
		double lonDeg = (x / n) * 360.0f - 180.0f;
		double latRad = Math.atan(Math.sinh(Math.PI * (1 - 2 * (n - y) / n)));
		double latDeg = latRad * 180.0 / Math.PI;

		toGeo[0] = (float) latDeg;
		toGeo[1] = (float) lonDeg;
	}

	/**
	 * Returns pixel Coordinates based on the OSM scheme. Every zoom step double
	 * the edge of the map area.
	 * 
	 * @param lat
	 * @param lng
	 * @param zoom
	 * @param toPoint
	 *            , an float array which will hold the coordinates, must be an
	 *            array with length 2
	 */
	public static void toPoint(float lat, float lng, int zoom, float[] toPoint) {
		double n = 1 << (8 + zoom);
		double x = ((lng + 180) / 360) * n;
		double latRad = lat * (Math.PI / 180);
		// double y = (1 - Math.log(Math.tan(latRad) + (1/Math.cos(latRad))) /
		// Math.PI ) /(2*n) ;
		double y = Math.round((1 - Math.log(Math.tan(latRad)
				+ (1 / Math.cos(latRad)))
				/ Math.PI)
				/ 2 * n);

		toPoint[0] = (float) x;
		toPoint[1] = (float) (n - y);
	}

	public void inverseMercator(long x, long y, float[] coords) {
		float lng = (x / 20037508.34f) * 180;
		float lat = (float) (180 / Math.PI * (2 * Math.atan(Math
				.exp((y / 20037508.34f) * 180 * Math.PI / 180)) - Math.PI / 2));

		coords[0] = lat;
		coords[1] = lng;
	}

	public void toMercator(float lat, float lng, long[] point) {
		long x = Math.round(lng * 20037508.34f / 180);
		long y = Math
				.round((Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180)) * 20037508.34f / 180);

		point[0] = x;
		point[1] = y;
	}

}
