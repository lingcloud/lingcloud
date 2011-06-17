/*
 *  @(#)AssetController.java  2010-5-10
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

package org.lingcloud.molva.ocl.asset;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.lingcloud.molva.ocl.util.ReflectionUtils;
import org.lingcloud.molva.ocl.util.VoalUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-10<br>
 * @author Xiaoyi Lu<br>
 */

public abstract class AssetController {

	private boolean checkValidity(Asset newAsset, Asset asset) {
		if (newAsset == null || asset == null) {
			return false;
		}

		return ((newAsset.getAddTime() == null || newAsset.getAddTime().equals(
				asset.getAddTime()))
				&& (newAsset.getAssetController() == null || newAsset
						.getAssetController()
						.equals(asset.getAssetController()))
				&& (newAsset.getGuid() == null || newAsset.getGuid().equals(
						asset.getGuid()))
				&& newAsset.getCai().equals(asset.getCai())
				&& (newAsset.getName() == null || newAsset.getName().equals(
						asset.getName()))
				&& (newAsset.getAssetLeaserId() == null || newAsset
						.getAssetLeaserId().equals(asset.getAssetLeaserId()))
				&& (newAsset.getOwnerId() == null || newAsset.getOwnerId()
						.equals(asset.getOwnerId())) 
				&& newAsset.getPrice() == asset.getPrice());
	}

	public Asset add(Asset asset) throws Exception {
		Asset clone = asset.clone();
		Asset createdAsset = this.create(clone);
		if (checkValidity(createdAsset, asset)) {
			return createdAsset;
		} else {
			throw new Exception("The fields of acl, assetController, guid, "
					+ "groupid, cai, name, assetLeaserId, ownerId,"
					+ " and addTime can be modified when create "
					+ "asset in controller.");
		}
	}

	public void remove(Asset asset) throws Exception {
		Asset clone = asset.clone();
		this.destroy(clone);
	}

	public Asset refresh(Asset asset) throws Exception {
		try {
			Asset clone = asset.clone();
			Asset polledAsset = this.poll(clone);
			if (checkValidity(polledAsset, asset)) {
				return polledAsset;
			} else {
				throw new Exception(
					"The fields of acl, assetController, guid, "
					+ "groupid, cai, name, assetLeaserId, ownerId, price,"
					+ " and addTime can be modified when poll asset.");
			}
		} catch (Throwable t) {
			VoalUtil.setLastErrorMessage4Asset(
					new AssetManagerImpl(),
					asset,
					"Error " + "occurred when the instance (" + asset.getGuid()
							+ " " + asset.getName() + ") in "
							+ asset.getAssetState() + " state, due to "
							+ t.toString());
			throw new Exception(t);
		}
	}

	public Object control(Asset asset, String operationName, Object[] parameter)
			throws Exception {
		try {
			return ReflectionUtils.invoke(this, operationName, parameter);
		} catch (InvocationTargetException e) {
			VoalUtil.setLastErrorMessage4Asset(
					new AssetManagerImpl(),
					asset,
					"Error " + "occurred when the instance (" + asset.getGuid()
							+ " " + asset.getName() + ") in "
							+ asset.getAssetState() + " state, due to "
							+ e.getTargetException().toString());
			throw new Exception(e.getTargetException());
		} catch (Throwable t) {
			VoalUtil.setLastErrorMessage4Asset(
					new AssetManagerImpl(),
					asset,
					"Error " + "occurred when the instance (" + asset.getGuid()
							+ " " + asset.getName() + ") in "
							+ asset.getAssetState() + " state, due to "
							+ t.toString());
			throw new Exception(t);
		}
	}

	public abstract Asset create(Asset asset) throws Exception;

	public abstract void destroy(Asset asset) throws Exception;

	public abstract Asset poll(Asset asset) throws Exception;

	public abstract double calculatePrice(Asset asset,
			HashMap<String, String> params) throws Exception;

	public abstract Asset init(Asset asset) throws Exception;

	public abstract Asset antiInit(Asset asset) throws Exception;

	public abstract Asset provision(Asset asset) throws Exception;

	public abstract Asset revoke(Asset asset) throws Exception;

}
