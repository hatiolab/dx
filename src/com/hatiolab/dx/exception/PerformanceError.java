package com.hatiolab.dx.exception;

public class PerformanceError extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3338220399923595242L;
	
	protected static final String message = "(이 메시지는 성능 문제가 있을 경우에 나타납니다.)";
	
	public PerformanceError() {
		super();
	}

	public PerformanceError(String detailMessage) {
		super(detailMessage);
	}

	public PerformanceError(Throwable throwable) {
		super(throwable);
	}

	public PerformanceError(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	@Override
	public String getMessage() {
		return super.getMessage() + message;
	}

}
