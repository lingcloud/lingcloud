/*
 *  @(#)ServiceFactory.java  2010-7-20
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

package org.lingcloud.molva.xmm.vam.services;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 *
 * @version 1.0.1 2010-7-20<br>
 * @author Ruijian Wang<br>
 *
 */
public class ServiceFactory {
	private static VAFileService fileService = null;
	private static VACategoryService cateService = null;
	private static VAService vaService = null;
	
	private ServiceFactory() {
		
	}
	
	/**
	 * get a file service instance.
	 * @return file service instance
	 */
	public static VAFileService getFileService() {
		if (fileService == null) {
			fileService = VAFileService.getInstance();
		}
		return fileService;
	}
	
	/**
	 * get a category service instance.
	 * @return category service instance
	 */
	public static VACategoryService getCategoryService() {
		if (cateService == null) {
			cateService = VACategoryService.getInstance();
		}
		return cateService;
	}
	
	/**
	 * get a virtual appliance service instance.
	 * @return virtual appliance service instance
	 */
	public static VAService getVirtualApplianceService() {
		if (vaService == null) {
			vaService = VAService.getInstance();
		}
		return vaService;
	}
}
