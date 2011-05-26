/*
 *  @(#)Lease.java  2010-5-10
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

package org.lingcloud.molva.ocl.lease;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.beanutils.BeanUtils;
import org.lingcloud.molva.ocl.lease.LeaseConstants.LeaseLifeCycleState;

/**
 * <strong>Purpose:</strong><br>
 * The basic data structure of lease.
 * 
 * @version 1.0.1 2010-5-10<br>
 * @author Xiaoyi Lu<br>
 */

public class Lease implements Serializable, Cloneable {

	/**
	 * add for Serializable.
	 */
	private static final long serialVersionUID = -7217853825582281990L;

	/**
	 * The guid of lease.
	 */
	private String guid;

	/**
	 * The name of lease.
	 */
	private String name;

	/**
	 * The tenant id of lease.
	 */
	private String tenantId;

	/**
	 * The group id of lease.
	 */
	private String groupId;

	/**
	 * The acl of lease.
	 */
	private String acl;

	/**
	 * The type of lease.
	 */
	private String type;

	/**
	 * The list of all assets' ids in the lease.
	 */
	private HashMap<String, String> assetIdAndTypeMap 
		= new HashMap<String, String>();

	/**
	 * The asset match maker' class name.
	 */
	private String assetMatchMaker;

	/**
	 * The lifecycle state of lease.
	 */
	private LeaseLifeCycleState lifecycleState;

	/**
	 * the create time of the lease.
	 */
	private Date addTime;

	/**
	 * the last update time of the lease.
	 */
	private Date updateTime;

	/**
	 * the effective time of the lease.
	 */
	private Date effectiveTime;

	/**
	 * the expire time of the lease.
	 */
	private Date expireTime;

	/**
	 * The duraton of lease.
	 */
	private long duration = -1;

	/**
	 * Indicates whether or not the assets in the lease is preemptible.
	 */
	private boolean preemptible = false;

	/**
	 * The additional terms of leases.
	 */
	private HashMap<String, String> additionalTerms 
		= new HashMap<String, String>();

	/**
	 * the last error message of all operations done on the asset.
	 */
	private String lastErrorMessage;

	/**
	 * The description of lease.
	 */
	private String description;

	public Lease(Lease lease) {
		if (lease == null) {
			return;
		}
		try {
			// FIXME It's not deep copy!
			BeanUtils.copyProperties(this, lease);
			if (lease.assetIdAndTypeMap != null) {
				this.setAssetIdAndTypeMap(new HashMap<String, String>(
						lease.assetIdAndTypeMap));
			}
			if (lease.additionalTerms != null) {
				this.setAdditionalTerms(new HashMap<String, String>(lease
						.getAdditionalTerms()));
			}
		} catch (Exception e) {
			throw new RuntimeException(new CloneNotSupportedException(
					"Clone not support due to : " + e));
		}
	}

	public Lease() {

	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	/*
	 * The key is asset id, the value is the asset type, it always is the class
	 * name. Or other save way is also support, but the child class of lease
	 * should handle it by self.
	 */
	public HashMap<String, String> getAssetIdAndTypeMap() {
		return assetIdAndTypeMap;
	}

	public void setAssetIdAndTypeMap(HashMap<String, String> assetIdMap) {
		this.assetIdAndTypeMap = assetIdMap;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public Date getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(Date effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public LeaseLifeCycleState getLifecycleState() {
		return lifecycleState;
	}

	public void setLifecycleState(LeaseLifeCycleState lifecycleState) {
		this.lifecycleState = lifecycleState;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Lease clone() throws CloneNotSupportedException {
		Lease newLease = new Lease();
		try {
			// FIXME It's not deep copy!
			BeanUtils.copyProperties(newLease, this);
			if (this.assetIdAndTypeMap != null) {
				newLease.setAssetIdAndTypeMap(new HashMap<String, String>(
						this.assetIdAndTypeMap));
			}
			if (this.additionalTerms != null) {
				newLease.setAdditionalTerms(new HashMap<String, String>(this
						.getAdditionalTerms()));
			}
		} catch (Exception e) {
			throw new CloneNotSupportedException("Clone not support due to : "
					+ e);
		}
		return newLease;
	}

	public boolean isPreemptible() {
		return preemptible;
	}

	public void setPreemptible(boolean preemptible) {
		this.preemptible = preemptible;
	}

	public HashMap<String, String> getAdditionalTerms() {
		return additionalTerms;
	}

	public void setAdditionalTerms(HashMap<String, String> additionalTerms) {
		this.additionalTerms = additionalTerms;
	}

	public void validate() throws Exception {

		if (this.assetMatchMaker == null || "".equals(this.assetMatchMaker)) {
			throw new Exception(
					"The assetMatchMaker is blank or null in lease.");
		}

		// FIXME neither AR Lease, BestEffort Lease, DeadLine Lease.
		// BestEffort Lease check.
		if (this.effectiveTime == null && this.expireTime == null
				&& duration < 0) {
			throw new Exception("Three attributes of effectiveTime, "
					+ "expireTime, and duration should be init at least one.");
		}

		// AR Lease check.
		if (this.effectiveTime != null && this.expireTime != null
				&& this.effectiveTime.after(this.expireTime)) {
			throw new Exception("The effectiveTime of lease should be " 
					+ "earlier than expireTime.");
		}

		// Deadline Lease check.
		if (duration < 0 && this.effectiveTime == null) {
			// Deadline Stoping Lease should have a duration.
			throw new Exception(
					"Deadline Stoping Lease should have a duration.");
		}
		if (duration < 0 && this.expireTime == null) {
			// Deadline Starting Lease should have a duration.
			throw new Exception(
					"Deadline Starting Lease should have a duration.");
		}
	}

	public String getAcl() {
		return acl;
	}

	public void setAcl(String acl) {
		this.acl = acl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAssetMatchMaker() {
		return assetMatchMaker;
	}

	public void setAssetMatchMaker(String assetMatchMaker) {
		this.assetMatchMaker = assetMatchMaker;
	}

	public void setLastErrorMessage(String lastErrorMessage) {
		this.lastErrorMessage = lastErrorMessage;
	}

	public String getLastErrorMessage() {
		return lastErrorMessage;
	}
}
