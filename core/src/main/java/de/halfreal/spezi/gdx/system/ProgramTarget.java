package de.halfreal.spezi.gdx.system;

public enum ProgramTarget {

	ENEMY,
	SELF;

	public ProgramTarget opponent() {
		switch (this) {
		case ENEMY:
			return ProgramTarget.SELF;
		case SELF:
			return ProgramTarget.ENEMY;
		default:
			throw new RuntimeException("Unknown program target: " + name());
		}
	}

}
