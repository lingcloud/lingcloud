/*
 *  @(#)Asset.java  2010-5-10
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

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.beanutils.BeanUtils;
import org.lingcloud.molva.ocl.asset.AssetConstants.AssetState;

/**
 * <strong>Purpose:</strong><br>
 * The basic data structure of all types of managed resources.
 * 
 * @version 1.0.1 2010-5-10<br>
 * @author Xiaoyi Lu<br>
 */

public class Asset implements Serializable, Cloneable {
	/**
	 * add for Serializable.
	 */
	private static final long serialVersionUID = -3980879863450022480L;

	/**
	 * The name of asset.
	 */
	private String name;

	/**
	 * The global uniform id of asset.
	 */
	private String guid;

	/**
	 * The cloud asset identifier.
	 */
	private String cai;

	/**
	 * The type of asset.
	 */
	private String type;

	/**
	 * Asset leaser id.
	 */
	private String assetLeaserId;

	/**
	 * The owner id of asset.
	 */
	private String ownerId;

	/**
	 * The group id of asset.
	 */
	private String groupId;

	/**
	 * The access control list, e.g. rwxr-xr-x.
	 */
	private String acl;

	/**
	 * Asset state, idle, reserved, or leased or unavailable.
	 */
	private AssetState assetState;

	/**
	 * The price of asset.
	 */
	private double price = 0;

	/**
	 * Get extension part. It's key-value pairs.
	 */
	private HashMap<String, String> attributes = new HashMap<String, String>();

	/**
	 * AssetController indicates different types of assets.
	 */
	private String assetController;

	/**
	 * the create time of the Asset.
	 */
	private Date addTime;

	/**
	 * the last update time of the Asset.
	 */
	private Date updateTime;

	/**
	 * the id of lease.
	 */

	private String leaseId;

	/**
	 * the last error message of all operations done on the asset.
	 */
	private String lastErrorMessage;

	/**
	 * The description of asset.
	 * 
	 */
	private String description;

	public Asset() {

	}

	public Asset(Asset asset) {
		try {
			// FIXME It's not deep copy!
			BeanUtils.copyProperties(this, asset);
			if (this.attributes != null) {
				this.setAttributes(
						new HashMap<String, String>(asset.attributes));
			}
		} catch (Exception e) {
			throw new RuntimeException("Clone not support due to : " + e);
		}
	}

	public String getAcl() {
		return acl;
	}

	public void setAcl(String acl) {
		this.acl = acl;
	}

	public String getAssetController() {
		return assetController;
	}

	public void setAssetController(String controller) {
		assetController = controller;
	}

	public String getCai() {
		return cai;
	}

	public void setCai(String address) {
		this.cai = address;
	}

	public HashMap<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Asset clone() throws CloneNotSupportedException {
		Asset newAsset = new Asset();
		try {
			// FIXME It's not deep copy!
			BeanUtils.copyProperties(newAsset, this);
			if (this.attributes != null) {
				newAsset.setAttributes(new HashMap<String, String>(
						this.attributes));
			}
		} catch (Exception e) {
			throw new CloneNotSupportedException("Clone not support due to : "
					+ e);
		}
		return newAsset;
	}

	public void validate() throws Exception {
		if (this.assetController == null || "".equals(this.assetController)) {
			throw new Exception("The asset controller is blank or null.");
		}
		if (this.name == null || "".equals(this.name)) {
			throw new Exception("The name of asset is blank or null.");
		}
		if (this.type == null || "".equals(this.type)) {
			throw new Exception("The type of asset is blank or null.");
		}
	}

	public String getAssetLeaserId() {
		return assetLeaserId;
	}

	public void setAssetLeaserId(String assetLeaserId) {
		this.assetLeaserId = assetLeaserId;
	}

	public AssetState getAssetState() {
		return assetState;
	}

	public void setAssetState(AssetState assetState) {
		this.assetState = assetState;
	}

	public void setLeaseId(String leaseId) {
		this.leaseId = leaseId;
	}

	public String getLeaseId() {
		return leaseId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setLastErrorMessage(String lastErrorMessage) {
		this.lastErrorMessage = lastErrorMessage;
	}

	public String getLastErrorMessage() {
		return lastErrorMessage;
	}
}
