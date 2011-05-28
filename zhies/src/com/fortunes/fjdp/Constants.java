package com.fortunes.fjdp;

public class Constants {
	
	public static enum Environment {
		DEVELOPMENT,DEBUG,PRODUCTION
	}
	
	public static final String PROJECT_CNAME = "FJDP";
	public static final String PROJECT_ENAME = "FJDP";
	public static final String MANUAL_DOC_PATH_NAME = "/doc/manual";
	public static final Environment ENVIRONMENT = Environment.DEVELOPMENT;
	
	public static final int USER_STATUS_LOGOUT = 0;//注销状态
	public static final int USER_STATUS_LOGIN = 1;//登陆状态
	

}
