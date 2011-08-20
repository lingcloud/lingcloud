/*
 *  @(#)MonitorAC.java  Jul 27, 2011
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
package org.lingcloud.molva.test.ocl.example;

import java.util.HashMap;

import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetController;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2011-7-27<br>
 * @author Ruijian Wang<br>
 * 
 */
public class MonitorAC extends AssetController {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lingcloud.molva.ocl.asset.AssetController#create(org.lingcloud.molva
	 * .ocl.asset.Asset)
	 */
	@Override
	public Asset create(Asset asset) throws Exception {
		// TODO Auto-generated method stub
		return asset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lingcloud.molva.ocl.asset.AssetController#destroy(org.lingcloud.molva
	 * .ocl.asset.Asset)
	 */
	@Override
	public void destroy(Asset asset) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lingcloud.molva.ocl.asset.AssetController#poll(org.lingcloud.molva
	 * .ocl.asset.Asset)
	 */
	@Override
	public Asset poll(Asset asset) throws Exception {
		// TODO Auto-generated method stub
		return asset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lingcloud.molva.ocl.asset.AssetController#calculatePrice(
	 * org.lingcloud.molva.ocl.asset.Asset, java.util.HashMap)
	 */
	@Override
	public double calculatePrice(Asset asset, HashMap<String, String> params)
			throws Exception {
		// TODO Auto-generated method stub
		return asset.getPrice();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lingcloud.molva.ocl.asset.AssetController#init(org.lingcloud.molva
	 * .ocl.asset.Asset)
	 */
	@Override
	public Asset init(Asset asset) throws Exception {
		// TODO Auto-generated method stub
		return asset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lingcloud.molva.ocl.asset.AssetController#antiInit(org.lingcloud.
	 * molva.ocl.asset.Asset)
	 */
	@Override
	public Asset antiInit(Asset asset) throws Exception {
		// TODO Auto-generated method stub
		return asset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lingcloud.molva.ocl.asset.AssetController#provision(org.lingcloud
	 * .molva.ocl.asset.Asset)
	 */
	@Override
	public Asset provision(Asset asset) throws Exception {
		// TODO Auto-generated method stub
		return asset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lingcloud.molva.ocl.asset.AssetController#revoke(org.lingcloud.molva
	 * .ocl.asset.Asset)
	 */
	@Override
	public Asset revoke(Asset asset) throws Exception {
		// TODO Auto-generated method stub
		return asset;
	}

}
