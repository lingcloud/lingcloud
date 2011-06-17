/*
 *  @(#)Partition.java  2010-5-27
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

package org.lingcloud.molva.xmm.pojos;

import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.xmm.ac.PartitionAC;
import org.lingcloud.molva.xmm.util.XMMConstants;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-28<br>
 * @author Xiaoyi Lu<br>
 */
public class Partition extends Asset {

	/**
	 * the serial ID.
	 */
	private static final long serialVersionUID = -2647523087214687061L;

	public Partition() {
		init();
	}

	/**
	 * to construct a Partition from a right type of Asset.
	 * 
	 * @param Asset
	 *            the asset object.
	 */
	public Partition(Asset asset) {
		super(asset);
		if (asset.getAssetController() == null) {
			String msg = "Partition's asset controller should not be null.";
			throw new RuntimeException(msg);
		}
		if (asset.getType() == null
				|| !asset.getType().equals(XMMConstants.PARTITION_TYPE)) {
			throw new RuntimeException(
					"The partition's type should be setted as "
							+ XMMConstants.PARTITION_TYPE + ".");
		}
	}

	private void init() {
		super.setAcl(XMMConstants.DEFAULT_ACL);
		super.setType(XMMConstants.PARTITION_TYPE);
	}

	/**
	 * set the type of Partition. It must override the method that make sure the
	 * Partition has right type.
	 * 
	 * @see XMMConstants for more detailed info.
	 * @param type
	 *            the type of Partition.
	 */
	public void setType(int type) {
		// To prohibit be set to other value. But can't prevent set value from
		// super class.
		super.setType(XMMConstants.PARTITION_TYPE);
	}

	public String getType() {
		return super.getType();
	}

	/**
	 * Clone an object.
	 * 
	 * @return the cloned object.
	 * @throws CloneNotSupportedException
	 *             Not support clone.
	 */
	public Partition clone() throws CloneNotSupportedException {
		try {
			Partition par = new Partition((Asset) super.clone());
			return par;
		} catch (Exception e) {
			throw new CloneNotSupportedException("Clone failed due to " + e);
		}
	}

	/**
	 * convert this object to an Asset.
	 * 
	 * @return return the new created Asset object.
	 */
	public Asset toAsset() {
		try {
			return (Asset) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(new Exception(
					"convert to Asset failed due to : " + e));
		}
	}

	public String getNodeType() {
		return super.getAttributes().get(PartitionAC.REQUIRED_ATTR_NODETYPE);
	}

	public void setNodeType(String nodeType) {
		super.getAttributes().put(PartitionAC.REQUIRED_ATTR_NODETYPE, nodeType);
	}

	public String getNodePreInstalledSoft() {
		return super.getAttributes().get(
				PartitionAC.ATTR_NODE_PRE_INSTALLED_SOFT);
	}

	public void setNodePreInstalledSoft(String nodePreInSoft) {
		super.getAttributes().put(PartitionAC.ATTR_NODE_PRE_INSTALLED_SOFT,
				nodePreInSoft);
	}
}
