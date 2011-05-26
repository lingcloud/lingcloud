/*
 *  @(#)ConfigUtil.java  2005-3-25
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

package org.lingcloud.molva.ocl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author maybekolo
 * @version $$Id: ConfigUtil.java,v 1.1 2007/07/10 09:18:46 cbq Exp $$
 */
public class ConfigUtil {
	/**
	 * the vega suite's default config file.
	 */
	public static final String DEFAULT_CONFIG_FILE = "conf.properties";

	public static final String LINGCLOUD_HOME_PROPERTY = "lingcloud.home";

	private static final String CONFIG_DIR = "conf";
	
	private Properties proterty = new Properties();
	
	private static Log log = LogFactory.getLog(ConfigUtil.class);

	private String file;

	public String getFileName() {
		return this.file;
	}

	/**
	 * get the LingCloud home directory path.
	 * 
	 * @return path, or null if failed.
	 */
	public static String getHomePath() {
		// added by Yongqiang Zou. 2006.3.31
		return System.getProperty(LINGCLOUD_HOME_PROPERTY);
	}

	/**
	 * get the LingCloud home dir.
	 * 
	 * @return the home dir File.
	 * @throws if
	 *             failed, throw Exception to report the error reason.
	 */
	public static File getHomeDir() {
		// comments added by Yongqiang Zou. 2006.3.31
		String homePath = getHomePath();

		if (homePath == null) {
			throw new IllegalStateException("System property "
					+ LINGCLOUD_HOME_PROPERTY + " is not set");
		}
		File homeFile = new File(homePath);
		if (!homeFile.exists()) {
			throw new IllegalStateException("System property "
					+ LINGCLOUD_HOME_PROPERTY + " = " + homePath
					+ " is not path of an exist file");
		}
		if (!homeFile.isDirectory()) {
			throw new IllegalStateException("System property "
					+ LINGCLOUD_HOME_PROPERTY + " = " + homePath
					+ " is not path of a directory");
		}
		return homeFile;
	}

	/**
	 * get the full file path in home dir.
	 * 
	 * @param name
	 *            the file name (not include full path) in the home dir.
	 * @return
	 */
	private static File getDir(String name) {
		// comments added by Yongqiang Zou. 2006.3.31
		File res = new File(getHomeDir(), name);
		if (!res.exists() || !res.isDirectory()) {
			throw new IllegalArgumentException("@name = " + name
					+ " does not exist or is not a dir");
		}
		return res;
	}

	/**
	 * get the config file full path File object.
	 * 
	 * @param name
	 *            the config file name in home dir's config directory.
	 * @return
	 */
	public static File getConfigFile(String name) {
		// comments added by Yongqiang Zou. 2006.3.31
		if (StringUtil.isEmpty(name)) {
			return null;
		}
		File res = new File(getDir(CONFIG_DIR), name);
		if (res.exists() && !res.isFile()) {
			throw new IllegalStateException(res + " is not a file");
		}
		if (!res.exists()) {
			try {
				res.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		return res;
	}

	/**
	 * get the default config file.
	 */
	public ConfigUtil() {
		String gosConfFile = getConfigFile(DEFAULT_CONFIG_FILE).toString();
		file = DEFAULT_CONFIG_FILE;
		refresh(gosConfFile);
	}

	public ConfigUtil(String filename) {
		String gosConfFile = getConfigFile(filename).toString();
		file = filename;
		refresh(gosConfFile);
	}

	public ConfigUtil(InputStream in) {
		refresh(in);
	}
	
	/**
	 * refresh the content of property.
	 * 
	 */
	private void refresh(String fileName) {

		try {
			file = fileName;
			FileInputStream in = new FileInputStream(file);
			this.proterty.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			log.error(file + " not find !" + e.getMessage());
			e.printStackTrace();
		} catch (IOException ee) {
			log.error(ee.getMessage());
			ee.printStackTrace();
		}

	}

	/**
	 * refresh the content of property.
	 * 
	 * @param in
	 *            InputStream which to be load to the property
	 */
	private void refresh(InputStream in) {
		try {
			this.proterty.load(in);
		} catch (IOException e) {
			// here to log error
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * save the property to the file.
	 * 
	 * @return
	 */
	public boolean save() {
		try {
			String gosConfFile = getConfigFile(file).toString();
			FileOutputStream propFile = new FileOutputStream(gosConfFile);
			proterty.store(propFile, "");
			propFile.close();
		} catch (Exception e) {
			// log here
			log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * set a value to the properties.
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value) {
		this.proterty.setProperty(key, value);
	}

	/**
	 * get properteis from config file or inputSteam.
	 * 
	 * @param key
	 * @param defaulValue
	 * @return
	 */
	public String getProperty(String key, String defaultValue) {
		String result = this.proterty.getProperty(key);
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}

	/**
	 * get properteis from config file or inputSteam.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getProperty(String key, int defaultValue) {
		String temp = this.proterty.getProperty(key);
		int result = defaultValue;
		if (temp == null) {
			return result;
		} else {
			try {
				result = Integer.parseInt(temp.trim());
				return result;
			} catch (NumberFormatException nfe) {
				log.error(nfe.getMessage());
				nfe.printStackTrace();
				return defaultValue;
				// log
			}
		}
	}

}
