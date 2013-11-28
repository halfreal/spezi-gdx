package de.halfreal.spezi.gdx.model;

public class Pair<FIRST, SECOND> {

	private FIRST first;
	private SECOND second;

	public Pair() {
	}

	public Pair(FIRST first, SECOND second) {
		super();
		this.first = first;
		this.second = second;
	}

	public FIRST getFirst() {
		return first;
	}

	public SECOND getSecond() {
		return second;
	}

	public void setFirst(FIRST first) {
		this.first = first;
	}

	public void setSecond(SECOND second) {
		this.second = second;
	}

}
