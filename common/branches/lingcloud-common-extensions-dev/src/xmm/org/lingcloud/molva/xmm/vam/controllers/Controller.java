/*
 *  @(#)Controller.java  2011-07-28
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
package org.lingcloud.molva.xmm.vam.controllers;

import java.util.HashMap;

import org.lingcloud.molva.xmm.vam.pojos.VAObject;
import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetController;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 *
 * @version 1.0.1 2011-07-28<br>
 * @author Ruijian Wang<br>
 *
 */
public abstract class Controller extends AssetController {
	/**
	 * the call back function after update operation.
	 * @param obj
	 * 		the virtual appliance object
	 * @throws Exception
	 */
	public abstract void update(VAObject obj) throws Exception;
	
	/**
	 * the call back function after create operation.
	 * @param obj
	 * 		the virtual appliance object
	 * @throws Exception
	 */
	public abstract void create(VAObject obj) throws Exception;
	/**
	 * the call back function after remove operation.
	 * @param obj
	 * 		the virtual appliance object
	 * @throws Exception
	 */
	public abstract void remove(VAObject obj) throws Exception;
	
	/**
	 * the call back function after copy operation.
	 * @param obj
	 * 		the virtual appliance object
	 * @throws Exception
	 */
	public abstract void copy(VAObject obj) throws Exception;
	
	/**
	 * the call back function after convert operation.
	 * @param obj
	 * 		the virtual appliance object
	 * @throws Exception
	 */
	public abstract void convert(VAObject obj) throws Exception;

	/**
	 * the call back function after move operation.
	 * @param obj
	 * 		the virtual appliance object
	 * @throws Exception
	 */
	public abstract void move(VAObject obj) throws Exception;
	
	/**
	 * resume the file from incorrect state.
	 * @param obj
	 * 		the virtual appliance object
	 * @throws Exception
	 */
	public abstract void resume(VAObject obj) throws Exception;
	
	/**
	 * validate the object.
	 * @param obj
	 * 		the basic object of virtual appliance
	 * @return 
	 * @throws Exception
	 */
	public abstract boolean validate(VAObject obj) throws Exception;
	
	@Override
	public Asset create(Asset asset) throws Exception {
		return asset;
	}

	@Override
	public void destroy(Asset asset) throws Exception {
	}

	@Override
	public Asset poll(Asset asset) throws Exception {
		return asset;
	}

	@Override
	public double calculatePrice(Asset asset, HashMap<String, String> params)
			throws Exception {
		return 0;
	}

	@Override
	public Asset init(Asset asset) throws Exception {
		return asset;
	}

	@Override
	public Asset antiInit(Asset asset) throws Exception {
		return asset;
	}

	@Override
	public Asset provision(Asset asset) throws Exception {
		return asset;
	}

	@Override
	public Asset revoke(Asset asset) throws Exception {
		return asset;
	}
}
