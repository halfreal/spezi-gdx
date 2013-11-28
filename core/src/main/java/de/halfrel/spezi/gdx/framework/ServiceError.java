package de.halfrel.spezi.gdx.framework;

import java.io.Serializable;

public class ServiceError implements Serializable {

	private static final long serialVersionUID = 1L;

	private String details;
	private String message;

	public ServiceError() {
	}

	public ServiceError(String message) {
		this.message = message;
	}

	public ServiceError copy() {
		ServiceError clone = new ServiceError(message);
		return clone.setDetails(details);
	}

	public ServiceError copy(String details) {
		ServiceError clone = copy();
		clone.setDetails(details);
		return clone;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ServiceError) {
			return message.equals(((ServiceError) obj).message);
		}
		return false;
	}

	public String getDetails() {
		return details;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public int hashCode() {
		return message.hashCode();
	}

	public ServiceError setDetails(String details) {
		this.details = details;
		return this;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ServiceError[" + message + "," + details + "]";
	}

}
