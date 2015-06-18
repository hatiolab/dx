package com.hatiolab.dx.exception;

public class PreemptiveFunctionCallError extends BugError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8434445223325288576L;
	
	protected static final String message = "(이 메시지는 특정 쓰레드에 점유된 함수를 다른 쓰레드가 호출한 경우에 나타납니다.)";
	
	public PreemptiveFunctionCallError() {
		super();
	}

	@Override
	public String getMessage() {
		return super.getMessage() + message;
	}

}
