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
	public static final String TEST_LINGCLOUD_SERVER = "10.0.0.10";

	public static final String TEST_XEN_SERVER = "10.0.0.10";

	public static final String TEST_COMMON_SERVER = "10.0.0.10";
	
	public static final String TEST_LINGCLOUD_PORTAL_URL = 
		"http://" + TEST_LINGCLOUD_SERVER + ":8080/";
	
	public static final String SELENIUM_SERVER_HOST = "localhost";
	
	public static final int SELENIUM_SERVER_PORT = 4444;
	
	public static final String SELENIUM_SPEED = "500";
	
	public static final int MAX_RETRY_TIMES = 10;

	public static final long RETRY_INTERVAL = 5000;
	
	/**
	 * For XMM test suite constants.
	 */
	/**
	 * VM and general partition Names for the test suite.
	 * They will be created in the test initialzation
	 * and will be destoryed after test.
	 */
	public static final String TEST_EVN_NAME_VMPARTION = "vm-partition-for-test";
	public static final String TEST_EVN_NAME_GENPARTION = "gen-partition-for-test";
	/**
	 * Host IP for test suite.
	 * They will be added to VM 
	 * and general partitions respectively. 
	 * They must be replaced by the existed host's IP.
	 */
	public static final String TEST_EVN_IPADD_VMHOST = "10.0.0.11";
	public static final String TEST_EVN_IPADD_GENHOST = "10.0.0.12";
	/**
	 * VM and general vitual cluster Names for the test suite.
	 * They will be created in the test initialzation
	 * and will be destoryed after test.
	 */
	public static final String TEST_EVN_NAME_VMCLUSTER = "vm-cluster-for-test";
	public static final String TEST_EVN_NAME_GENCLUSTER = "gen-cluster-for-test";
	/**
	 * Virtual appliance's GUID for the test suite.
	 * It must be replaced by the existed appliance's GUID.
	 */
	public static final String TEST_EVN_GUID_APP = "virtual appliance guid";
	
	private TestConstants() {
		
	}
	
	/**
	 * 
	 * <strong>Purpose:</strong><br>
	 * The browser for LingCloud Portal test.
	 *
	 * @version 1.0.0 2011-7-23<br>
	 * @author Ruijian Wang<br>
	 *
	 */
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
		
		private Browser() {
			
		}
	}
}
