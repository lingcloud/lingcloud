/*
 *  @(#)Placeable.java  2007-8-6
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

package org.lingcloud.molva.ocl.persistence;

/**
 * <strong>Purpose:</strong><br>
 * Define the interface of global object that can be store in the overlay
 * network.
 * 
 * @version 1.0.1 2007-8-6<br>
 * @author zouyongqiang<br>
 * 
 */
public interface Placeable {
	/**
	 * get the guid of the global object.
	 * 
	 * @return the global unique ID.
	 */
	String getGuid();

	/**
	 * get the version number.
	 * 
	 * @return the version number.
	 */
	int getVersion();


	/**
	 * home site is the original site that hold the object.
	 * 
	 * @return the home site id.
	 */
	String getHomeSiteID();
}
