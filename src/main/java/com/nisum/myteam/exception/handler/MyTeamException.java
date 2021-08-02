package com.nisum.myteam.exception.handler;

public class MyTeamException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3489301441198508177L;

	public MyTeamException() {
	}

	public MyTeamException(String arg0) {
		super(arg0);
	}

	public MyTeamException(Throwable arg0) {
		super(arg0);
	}

	public MyTeamException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public MyTeamException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
