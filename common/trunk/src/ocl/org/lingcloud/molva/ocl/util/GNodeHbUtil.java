/*
 *  @(#)GNodeHbUtil.java  2007-8-30
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
/**
 * <strong>Purpose:</strong><br>
 * A Hibernate Util function for Naming.
 * 
 * @version 1.0.1 2007-8-30<br>
 * @author zouyongqiang<br>
 * 
 */
public class GNodeHbUtil extends HibernateUtil {
	/**
	 * Get the cfg.xml file name for the class so that to allow subclass to
	 * inherit from it.
	 * 
	 * @return the file name.
	 */
	protected String getCfgXmlFileName() {
		return "naming-hibernate.cfg.xml";
	}
	
	/**
	 * Get the cfg.xml file for the class so that to allow subclass to
	 * inherit from it.
	 * 
	 * @return the File.
	 */
	protected File getCfgXmlFile() {
		return ConfigUtil.getConfigFile("naming-hibernate.cfg.xml");
	}

	/**
	 * the singleton pattern.
	 * 
	 * @return
	 */
	private static GNodeHbUtil instance;

	/**
	 * Get the only instance. The singleton pattern.
	 * 
	 * @return the instance.
	 */
	public static GNodeHbUtil newInstance() {
		if (instance == null) {
			instance = new GNodeHbUtil();
		}

		return instance;
	}
}
