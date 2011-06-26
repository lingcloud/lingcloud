/*
 *  @(#)TestConstants.java 2011-6-23
 *
 *  Copyright (C) 2008-2011,
 *  LingCloud Team,
 *  Institute of Computing Technology,
 *  Chinese Academy of Sciences.
 *  P.O.Box 2704, 100190, Beijing, China.
 *
 *  http://lingcloud.org
 *  
 */

package org.lingcloud.molva.test.util;

/**
 * <strong>Purpose:</strong><br>
 * The constants for LingCloud Portal test.
 * 
 * @version 1.0.0 2011-6-23<br>
 * @author Jian Lin<br>
 * 
 */
public final class TestConstants {

	public static final String TEST_LINGCLOUD_SERVER = "10.0.0.13";

	public static final String TEST_XEN_SERVER = "10.0.0.13";

	public static final String TEST_COMMON_SERVER = "10.0.0.12";
	
	public static final String TEST_LINGCLOUD_PORTAL_URL = 
		"http://" + TEST_LINGCLOUD_SERVER + ":8080/";
	
	public static final String SELENIUM_SERVER_HOST = "localhost";
	
	public static final int SELENIUM_SERVER_PORT = 4444;
	
	public static final String SELENIUM_SPEED = "500";
	
	public static final int MAX_RETRY_TIMES = 10;

	public static final long RETRY_INTERVAL = 5000;
	
	public final class Browser {
	/* Selenium supported browser list:
	  *firefox
	  *mock
	  *firefoxproxy
	  *pifirefox
	  *chrome
	  *iexploreproxy
	  *iexplore
	  *firefox3
	  *safariproxy
	  *googlechrome
	  *konqueror
	  *firefox2
	  *safari
	  *piiexplore
	  *firefoxchrome
	  *opera
	  *iehta
	  *custom
	*/
		public static final String FIREFOX = "*firefox";
		public static final String CHROME = "*googlechrome";
		public static final String IE = "*iexplore";
	}
}
