/*
 *  @(#)DaoFactory.java  2010-5-20
 *
 *  Copyright (C) 2008-2011,
 *  LingCloud Team,
 *  Chinese Academy of Sciences.
 *  P.O.Box 2704, 100190, Beijing, China.
 *
 *  http://lingcloud.org
 *  
 */

package org.lingcloud.molva.xmm.vam.daos;


/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-20<br>
 * @author Ruijian Wang<br>
 * 
 */
public class DaoFactory {

	/**
	 * the constructor.
	 *
	 */
	private DaoFactory() {
		
	}
	
	/**
	 * get the instance data access object.
	 * @return instance data access object
	 */
	public static VirtualApplianceDao getVirtualApplianceDao() {
		return VirtualApplianceDao.getInstance();
	}

	/**
	 * get the file data access object.
	 * @return file data access object
	 */
	public static VAFileDao getVAFileDao() {
		return VAFileDao.getInstance();
	}

	/**
	 * get the appliance category data access object.
	 * @return category data access object
	 */
	public static VACategoryDao getVACategoryDao() {
		return VACategoryDao.getInstance();
	}
}
