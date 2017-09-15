package com.szzc.spring.boot.starter.zookeeper.exception;

public class LockBusyException extends RuntimeException {

	private static final long serialVersionUID = -5875371379845226068L;

	protected String msg;
	protected String code;

	public LockBusyException() {
		super();
	}

	public LockBusyException(String message, Throwable cause) {
		super(message, cause);
	}

	public LockBusyException(Throwable cause) {
		super(cause);
	}

	public LockBusyException(String msg) {
		super(msg);
		this.code = "-1";
		this.msg = msg;
	}

	public LockBusyException(String code, String msg) {
		super(msg);
		this.code = code;
		this.msg = msg;
	}

	public LockBusyException(String code, String msgFormat, Object... args) {
		super(String.format(msgFormat, args));
		this.code = code;
		this.msg = String.format(msgFormat, args);
	}

	public String getMsg() {
		return msg;
	}

	public String getCode() {
		return code;
	}

}
