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
	private static FileService fileService = null;
	private static CategoryService cateService = null;
	private static ApplianceService vaService = null;
	
	private ServiceFactory() {
		
	}
	
	/**
	 * get a file service instance.
	 * @return file service instance
	 */
	public static FileService getFileService() {
		if (fileService == null) {
			fileService = FileService.getInstance();
		}
		return fileService;
	}
	
	/**
	 * get a category service instance.
	 * @return category service instance
	 */
	public static CategoryService getCategoryService() {
		if (cateService == null) {
			cateService = CategoryService.getInstance();
		}
		return cateService;
	}
	
	/**
	 * get a virtual appliance service instance.
	 * @return virtual appliance service instance
	 */
	public static ApplianceService getVirtualApplianceService() {
		if (vaService == null) {
			vaService = ApplianceService.getInstance();
		}
		return vaService;
	}
}
