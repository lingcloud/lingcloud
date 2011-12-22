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
	public static final int MAX_RETRY_TIMES = 10;

	public static final long RETRY_INTERVAL = 5000;

	/**
	 * VM and general partition Names for the test suite. They will be created
	 * in the test initialzation and will be destoryed after test.
	 */
	public static final String TEST_EVN_NAME_VMPARTION 
									= "vm-partition-for-test";
	public static final String TEST_EVN_NAME_GENPARTION 
									= "gen-partition-for-test";
	/**
	 * VM and general vitual cluster Names for the test suite. They will be
	 * created in the test initialzation and will be destoryed after test.
	 */
	public static final String TEST_EVN_NAME_VMCLUSTER 
									= "vm-cluster-for-test";
	public static final String TEST_EVN_NAME_GENCLUSTER 
									= "gen-cluster-for-test";
	/**
	 * Virtual appliance's GUID for the test suite. It must be replaced by the
	 * existed appliance's GUID.
	 */
	public static final String TEST_EVN_GUID_APP 
						= "7A53AAEF7819528A8435395556CC3FB49A100BC8";

	public static final int K = 1024;
	
	private TestConstants() {

	}

	/**
	 * 
	 * <strong>Purpose:</strong><br>
	 * The browser for LingCloud Portal test.
	 * 
	 * @version 1.0.0 2011-7-23<br>
	 * @author Jian Lin<br>
	 * 
	 */
	public final class Browser {
		/*
		 * Selenium supported browser list:firefoxmockfirefoxproxypifirefox
		 * chromeiexploreproxyiexplorefirefox3safariproxygooglechromekonqueror
		 * firefox2safaripiiexplorefirefoxchromeoperaiehtacustom
		 */
		public static final String FIREFOX = "*firefox";
		public static final String CHROME = "*googlechrome";
		public static final String IE = "*iexplore";

		private Browser() {

		}
	}
}
