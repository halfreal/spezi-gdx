package de.halfrel.spezi.gdx.framework;

import java.lang.reflect.Method;

public class ClientError {

	private Object[] args;
	private Throwable exception;
	private Method method;
	private CLIENT_ERROR type;

	/**
	 * A Client Error can be used to retry a request.
	 * 
	 * @param type
	 * @param method
	 * @param args
	 */
	public ClientError(CLIENT_ERROR type, Method method, Object[] args) {
		this.type = type;
		this.method = method;
		this.args = args;
	}

	public ClientError(CLIENT_ERROR type, Method method, Object[] args,
			Throwable e) {
		this.type = type;
		this.method = method;
		this.args = args;
		exception = e;
	}

	public Object[] getArgs() {
		return args;
	}

	public Throwable getException() {
		return exception;
	}

	public Method getMethod() {
		return method;
	}

	public CLIENT_ERROR getType() {
		return type;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public void setType(CLIENT_ERROR type) {
		this.type = type;
	}

}
