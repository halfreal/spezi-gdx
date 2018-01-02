package de.halfreal.spezi.gdx.math;

public class MathHelper {

	private static final float DELTA = 0.0000001f;

	public static int cube(int value) {
		return value * value * value;
	}

	public static boolean floatEquals(float x, float y) {
		return Math.abs(x - y) < DELTA;
	}

	public static int fourth(int value) {
		return value * value * value * value;
	}

	public static boolean isBitSet(int value, int flag) {
		return (value & flag) == flag;
	}

	public static boolean isBitSetByIndex(int value, int bitindex) {
		return (value & (1 << bitindex)) != 0;
	}

	public static int max(int... values) {
		int max = Integer.MIN_VALUE;
		for (int value : values) {
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	public static int min(int... values) {
		int min = Integer.MAX_VALUE;
		for (int value : values) {
			if (value < min) {
				min = value;
			}
		}
		return min;
	}

	public static int minMax(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	public static int square(int value) {
		return value * value;
	}

}
