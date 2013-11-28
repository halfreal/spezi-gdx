package de.halfreal.spezi.gdx.sector;

import java.io.Serializable;

public class SectorId implements Serializable {

	public static final int MAX_ZOOM = 31;
	public static final SectorId root = new SectorId(0, 0, 0);

	private static final long serialVersionUID = 1L;

	public int x;
	public int y;
	public int z;

	public SectorId() {
	}

	public SectorId(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public SectorId(String id) {
		String[] array = id.split("\\.");
		x = Integer.parseInt(array[0]);
		y = Integer.parseInt(array[1]);
		z = Integer.parseInt(array[2]);
	}

	public SectorId child(int id) {
		if (z >= MAX_ZOOM) {
			return null;
		}
		switch (id) {
		case 0:
			return new SectorId(x * 2, y * 2, z + 1);
		case 1:
			return new SectorId(x * 2 + 1, y * 2, z + 1);
		case 2:
			return new SectorId(x * 2, y * 2 + 1, z + 1);
		case 3:
			return new SectorId(x * 2 + 1, y * 2 + 1, z + 1);
		default:
			return null;
		}
	}

	public SectorId[] children() {
		if (z < 0 || z >= MAX_ZOOM) {
			return null;
		}

		return new SectorId[] { child(0), child(1), child(2), child(3) };
	}

	public SectorId copy() {
		return new SectorId(x, y, z);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof SectorId) {
			return toString().equals(obj.toString());
		}

		return false;
	}

	/**
	 * Check if the given sector id is in contained in the subtree of any of the
	 * four children of this sector id. Returns the child that contains the
	 * given sector id or null, if it is not found.
	 * 
	 * @param id
	 *            the sector id
	 * @return the child id that contains the given sector id or null, if it is
	 *         not contained
	 */
	public SectorId findSubtree(SectorId id) {
		for (int i = 0; i < 4; i++) {
			SectorId childId = child(i);
			if (childId.inSubtree(id)) {
				return childId;
			}
		}
		return null;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Checks if the given sector id is contained in the tree that is defined by
	 * this sector id.
	 * 
	 * @param id
	 *            the sector id
	 * @return if the given sector id is contained in this tree
	 */
	public boolean inSubtree(SectorId id) {
		if (id.z < z) {
			return false;
		}

		int zDiff = id.z - z;
		int pow = (int) Math.pow(2, zDiff);
		int minX = x * pow;
		int maxX = (x + 1) * pow - 1;
		int minY = y * pow;
		int maxY = (y + 1) * pow - 1;

		return id.x >= minX && id.x <= maxX && id.y >= minY && id.y <= maxY;
	}

	public SectorId parent() {
		if (z > 0) {
			return new SectorId(x / 2, y / 2, z - 1);
		} else {
			return null;
		}
	}

	public int positionRelativeToParent() {
		return x % 2 + 2 * (y % 2);
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZ(int z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return x + "." + y + "." + z;
	}

	public int yInverse() {
		return (1 << z) - y - 1;
	}

}
