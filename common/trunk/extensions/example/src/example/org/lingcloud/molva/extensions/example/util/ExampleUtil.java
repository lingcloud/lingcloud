/*
 *  @(#)ExampleUtil.java Oct 10, 2011
 *
 *  Copyright (C) 2008-2011,
 *  Vega LingCloud Team,
 *  Institute of Computing Technology,
 *  Chinese Academy of Sciences.
 *  P.O.Box 2704, 100190, Beijing, China.
 *
 *  http://lingcloud.org
 *
 */

package org.lingcloud.molva.extensions.example.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import org.lingcloud.molva.ocl.util.ConfigUtil;
import org.lingcloud.molva.xmm.util.XMMConstants;

/**
 *  <strong>Purpose:</strong><br>
 *  TODO.
 *
 *  @version 1.0.1 Oct 10, 2011<br>
 *  @author Ruijian Wang<br>
 *
 */
public class ExampleUtil {
	private static Properties properties;

	public static final String CONF_FILE = "example.conf";
	
	/**
	 * get the example configuration.
	 * 
	 * @throws Exception
	 */
	private static void getConfig() throws Exception {
		ConfigUtil conf = new ConfigUtil(XMMConstants.CONFIG_FILE_NAME);
		String extensionsHome = conf.getProperty("extensionsHome", "");
		
		if (extensionsHome == null || extensionsHome.equals("")) {
			throw new Exception("Can't find the config file '" + CONF_FILE
					+ "'.");
		}
		
		String confPath = extensionsHome + "/example/conf/" + CONF_FILE;

		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(confPath));
		} catch (FileNotFoundException e) {
			throw new Exception("Can't find the example config.");
		}
		properties.load(in);

	}

	private ExampleUtil() {
	}

	static {
		properties = new Properties();

		try {
			getConfig();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getExtensionsDir(String defaultValue) {
		String extensionsDir = (String) properties.get("extensionsDir");
		if (extensionsDir == null) {
			extensionsDir = defaultValue;
		}
		return extensionsDir;
	}
	
	public static String getExampleDir(String defaultValue) {
		String exampleDir = (String) properties.get("exampleDir");
		if (exampleDir == null) {
			exampleDir = defaultValue;
		}
		return exampleDir;
	}
	
	public static String getExampleConf(String defaultValue) {
		String exampleConf = (String) properties.get("exampleConf");
		if (exampleConf == null) {
			exampleConf = defaultValue;
		}
		return exampleConf;
	}
}
