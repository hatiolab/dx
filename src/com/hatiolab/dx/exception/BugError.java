package com.hatiolab.dx.exception;

public class BugError extends Error {

	protected static final String message = "(이 메시지는 버그가 있을 경우에 나타납니다.)";
	
	public BugError() {
		super();
	}

	public BugError(String detailMessage) {
		super(detailMessage);
	}

	public BugError(Throwable throwable) {
		super(throwable);
	}

	public BugError(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	@Override
	public String getMessage() {
		return super.getMessage() + message;
	}

}
