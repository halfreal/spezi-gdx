package de.halfreal.spezi.gdx.system;

import java.io.Serializable;

public class ProgramPhase implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean critical;
	private String program;
	private ProgramTarget target;
	private ProgramTarget type;

	public ProgramPhase() {
	}

	public ProgramPhase(boolean critical, String program, ProgramTarget target,
			ProgramTarget type) {
		this.critical = critical;
		this.program = program;
		this.target = target;
		this.type = type;
	}

	public String getProgram() {
		return program;
	}

	public ProgramTarget getTarget() {
		return target;
	}

	public ProgramTarget getType() {
		return type;
	}

	public boolean isCritical() {
		return critical;
	}

	public void setCritical(boolean critical) {
		this.critical = critical;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public void setTarget(ProgramTarget target) {
		this.target = target;
	}

	public void setType(ProgramTarget type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "[" + program + "." + type + " -> " + target + "]";
	}

}
