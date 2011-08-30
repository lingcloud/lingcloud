/*
 *  @(#)TestConfig.java  2011-08-23
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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import org.lingcloud.molva.ocl.util.ConfigUtil;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2011-08-23<br>
 * @author Ruijian Wang<br>
 * 
 */
public class TestConfig {
	private static final String TEST_LINGCLOUD_SERVER = "lingCloudServer";

	private static final String TEST_LINGCLOUD_SERVER_DEFAULT = "10.0.0.11";

	private static final String TEST_XEN_SERVER = "testCaseXenServer";

	private static final String TEST_XEN_SERVER_DEFAULT = "10.0.0.11";

	private static final String TEST_COMMON_SERVER = "testCaseCommonServer";

	private static final String TEST_COMMON_SERVER_DEFAULT = "10.0.0.12";

	private static final String SELENIUM_SERVER_HOST = "seleniumServerHost";
	
	private static final String SELENIUM_SERVER_HOST_DEFAULT = "localhost";

	private static final String SELENIUM_SERVER_PORT = "seleniumServerPort";
	
	private static final int SELENIUM_SERVER_PORT_DEFAULT = 4444;

	private static final String SELENIUM_SPEED = "seleniumSpeed";
	
	private static final String SELENIUM_SPEED_DEFAULT = "500";

	public static final String CONF_FILE = "molva.conf";

	private static Properties properties;

	private TestConfig() {

	}

	static {
		Properties defaults = new Properties();
		defaults.put(TEST_LINGCLOUD_SERVER, 
				TEST_LINGCLOUD_SERVER_DEFAULT);
		defaults.put(TEST_XEN_SERVER, TEST_XEN_SERVER_DEFAULT);
		defaults.put(TEST_COMMON_SERVER, TEST_COMMON_SERVER_DEFAULT);
		defaults.put(SELENIUM_SERVER_HOST, 
				SELENIUM_SERVER_HOST_DEFAULT);
		defaults.put(SELENIUM_SERVER_PORT, 
				"" + SELENIUM_SERVER_PORT_DEFAULT);
		defaults.put(SELENIUM_SPEED, SELENIUM_SPEED_DEFAULT);
		
		properties = new Properties(defaults);

		try {
			getConfig();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * get the LingCloud Molva configuration.
	 * 
	 * @throws Exception
	 */
	private static void getConfig() throws Exception {
		String confPath = ConfigUtil.getConfigFile(CONF_FILE).toString();
		
		if (confPath == null) {
			return;
		}

		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(confPath));
		} catch (FileNotFoundException e) {
			VAMUtil.errorLog("Can't find the molva configuration file.");
			return;
		}
		properties.load(in);

	}

	public static String getTestLingCloudServer() {
		return properties.getProperty(TEST_LINGCLOUD_SERVER);
	}

	public static String getTestXenServer() {
		return properties.getProperty(TEST_XEN_SERVER);
	}

	public static String getTestCommonServer() {
		return properties.getProperty(TEST_COMMON_SERVER);
	}

	public static String getTestLingCloudPortalURL() {
		return "http://" + getTestLingCloudServer() + ":8080/";
	}

	public static String getSeleniumServerHost() {
		return properties.getProperty(SELENIUM_SERVER_HOST);
	}

	public static int getSeleniumPort() {
		return Integer.parseInt((String) properties.get(SELENIUM_SERVER_PORT));
	}

	public static String getSeleniumSpeed() {
		return properties.getProperty(SELENIUM_SPEED);
	}
}
