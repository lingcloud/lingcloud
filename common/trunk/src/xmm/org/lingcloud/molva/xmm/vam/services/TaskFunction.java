/*
 *  @(#)ThreadFunction.java  2010-6-2
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

import org.lingcloud.molva.xmm.vam.pojos.VAObject;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-6-2<br>
 * @author Ruijian Wang<br>
 * 
 */
public interface TaskFunction {
	/**
	 * the call back function after update operation.
	 * @param obj
	 * 		the virtual appliance object
	 * @throws Exception
	 */
	void updateCallBack(VAObject obj) throws Exception;
	
	/**
	 * the call back function after create operation.
	 * @param obj
	 * 		the virtual appliance object
	 * @throws Exception
	 */
	void createCallBack(VAObject obj) throws Exception;
	/**
	 * the call back function after remove operation.
	 * @param obj
	 * 		the virtual appliance object
	 * @throws Exception
	 */
	void removeCallBack(VAObject obj) throws Exception;
	
	/**
	 * the call back function after copy operation.
	 * @param obj
	 * 		the virtual appliance object
	 * @throws Exception
	 */
	void copyCallBack(VAObject obj) throws Exception;
	
	/**
	 * the call back function after convert operation.
	 * @param obj
	 * 		the virtual appliance object
	 * @throws Exception
	 */
	void convertCallBack(VAObject obj) throws Exception;

	/**
	 * the call back function after move operation.
	 * @param obj
	 * 		the virtual appliance object
	 * @throws Exception
	 */
	void moveCallBack(VAObject obj) throws Exception;
	
	/**
	 * check the object.
	 * @param obj
	 * 		the basic object of virtual appliance
	 * @return 
	 * @throws Exception
	 */
	boolean check(VAObject obj) throws Exception;
}
