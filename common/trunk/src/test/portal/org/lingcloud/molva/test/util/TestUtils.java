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
 * The utils for LingCloud Portal test.
 * 
 * @version 1.0.0 2011-6-23<br>
 * @author Jian Lin<br>
 * 
 */
public class TestUtils {

	private static String browser = TestConstants.Browser.FIREFOX;

	public static void setBrowser(String browser) {
		TestUtils.browser = browser;
	}

	public static String getBrowser() {
		return browser;
	}

}
