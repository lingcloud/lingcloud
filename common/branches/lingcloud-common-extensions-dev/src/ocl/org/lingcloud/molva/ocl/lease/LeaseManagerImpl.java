/*
 *  @(#)LeaseManagerImpl.java  2010-5-11
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.ocl.amm.AssetMatchMaker;
import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetManagerImpl;
import org.lingcloud.molva.ocl.lease.LeaseConstants.LeaseLifeCycleState;
import org.lingcloud.molva.ocl.persistence.GNode;
import org.lingcloud.molva.ocl.persistence.GNodeConstants;
import org.lingcloud.molva.ocl.persistence.GNodeManager;
import org.lingcloud.molva.ocl.util.StringUtil;
import org.lingcloud.molva.ocl.util.VoalUtil;

/**
 * <strong>Purpose:</strong><br>
 * Lease Manager. Basic Operating APIs for lease.
 * 
 * @version 1.0.1 2010-5-11<br>
 * @author Xiaoyi Lu<br>
 */

public class LeaseManagerImpl {

	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(LeaseManagerImpl.class);
	/**
	 * the GNode manager.
	 */
	private GNodeManager gnm = null;

	/**
	 * the asset manager.
	 */
	private AssetManagerImpl ami = null;

	static {
		// a trick: to trigger the lease life cycle monitor to load all old
		// leases.
		LeaseLifeCycleMonitor.getInstance().addLeaseMonitoredOnce(null);
	}

	public LeaseManagerImpl() {
		try {
			gnm = new GNodeManager();
			ami = new AssetManagerImpl();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public LeaseManagerImpl(String namingUrl) {
		try {
			gnm = new GNodeManager();
			ami = new AssetManagerImpl(namingUrl);
		} catch (Exception e) {
			String msg = "Error occurred when construct LeaseManagerImpl " 
				+ "by url " + namingUrl + " due to : " + e.toString();
			log.error(msg);
			throw new RuntimeException(msg);
		}
	}

	private Lease persistenceLease(String method, Lease lease) 
		throws Exception {
		try {
			GNode gn = lease2GNode(lease);

			if (method.equals("add")) {
				GNode reg = gnm.register(gn);
				Lease newLease = gnode2Lease(reg);
				return newLease;
			} else if (method.equals("update")) {
				GNode newOne = gnm.update(gn);
				Lease newLease = gnode2Lease(newOne);
				return newLease;
			} else {
				String msg = "Unsupported method in Persistence Lease,"
						+ " Only support add and update.";
				throw new Exception(msg);
			}
		} catch (Exception e) {
			log.error("Persistence Lease Error : " + e.toString());
			throw e;
		}
	}
	
	private GNode lease2GNode(Lease lease) {
		GNode gn = new GNode();
		gn.setGuid(lease.getGuid());
		gn.setName(lease.getName());
		gn.setAcl(lease.getAcl());
		gn.setGroupID(lease.getGroupId());
		gn.setOwnerID(lease.getTenantId());
		gn.setAddTime(lease.getAddTime());
		gn.setUpdateTime(lease.getUpdateTime());
		gn.setRControllerType(LeaseConstants.LEASE_RCONTROLLER);
		gn.setType(GNodeConstants.GNODETYPE_RESOURCE);
		gn.setDescription(lease.getDescription());
		gn.getAttributes().put("Lease.AssetMatchMaker",
				lease.getAssetMatchMaker());

		gn.getAttributes().put("Lease.Type", lease.getType());
		gn.getAttributes().put("Lease.EffectiveTime",
				VoalUtil.dateToString(lease.getEffectiveTime()));
		gn.getAttributes().put("Lease.ExpireTime",
				VoalUtil.dateToString(lease.getExpireTime()));
		gn.getAttributes().put("Lease.LifecycleState",
				lease.getLifecycleState().toString());
		gn.getAttributes().put("Lease.Duration",
				String.valueOf(lease.getDuration()));
		String isPreemptible = "false";
		if (lease.isPreemptible()) {
			isPreemptible = "true";
		}
		gn.getAttributes().put("Lease.Preemptible", isPreemptible);

		// TODO here we must consider this problem later, in naming, the
		// attributes number can not exceed 30 in default, but the following
		// desin will cause the attr number will increase with the node
		// number, so it will be bigger than 30 easily. Now the solution is
		// change the default max length in naming to 512 to solve this
		// problem, it should be considered carefully later for two aspects:
		// 1> for the naming, we donot modify it as much as possible; 2> for
		// the LingCloud, we need to support advanced attribute-based
		// search. So as the situation of asset persistent method. Xiaoyi Lu
		// Marked at 2010-09-23.
		HashMap<String, String> additionalTerms = lease
				.getAdditionalTerms();
		if (additionalTerms != null && !additionalTerms.isEmpty()) {
			Iterator<String> iterator = additionalTerms.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				String value = (String) additionalTerms.get(key);
				gn.getAttributes().put("Lease.AdditionalTerms." + key,
						value);
			}
		}

		HashMap<String, String> assetIdMap = lease.getAssetIdAndTypeMap();
		if (assetIdMap != null && !assetIdMap.isEmpty()) {
			Iterator<String> iterator = assetIdMap.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				String value = (String) assetIdMap.get(key);
				gn.getAttributes().put("Lease.AssetIdMap." + key, value);
			}
		}

		gn.getAttributes().put("Lease.LastErrorMessage",
				lease.getLastErrorMessage());
		
		return gn;
	}

	private Lease gnode2Lease(GNode gn) {
		Lease ret = new Lease();
		/*
		 * FIXME Due to we use naming to save lease, so we leverage naming
		 * funcitons of guid, addtime, updatetime, acl, groupid, and ownerid
		 * generations. If we do not use naming later, these six fields should
		 * be maintained by ourselves. The methods of gnode2Lease and
		 * persistenceLease should be overridden when we use other ways to save
		 * leases.
		 */
		ret.setGuid(gn.getGuid());
		ret.setAddTime(gn.getAddTime());
		ret.setUpdateTime(gn.getUpdateTime());
		ret.setAcl(gn.getAcl());
		ret.setGroupId(gn.getGroupID());
		ret.setTenantId(gn.getOwnerID());

		ret.setDescription(gn.getDescription());
		ret.setName(gn.getName());

		String amm = (String) gn.getAttributes().get("Lease.AssetMatchMaker");
		ret.setAssetMatchMaker(amm);

		ret.setType((String) gn.getAttributes().get("Lease.Type"));

		long duration = Long.valueOf((String) gn.getAttributes().get(
				"Lease.Duration"));
		ret.setDuration(duration);

		String efstr = (String) gn.getAttributes().get("Lease.EffectiveTime");
		ret.setEffectiveTime(VoalUtil.dateFromString(efstr));

		String etstr = (String) gn.getAttributes().get("Lease.ExpireTime");
		ret.setExpireTime(VoalUtil.dateFromString(etstr));

		String lcstr = (String) gn.getAttributes().get("Lease.LifecycleState");
		ret.setLifecycleState(LeaseLifeCycleState.valueOf(lcstr));

		String prstr = (String) gn.getAttributes().get("Lease.Preemptible");
		ret.setPreemptible(Boolean.valueOf(prstr).booleanValue());

		HashMap<String, String> attributes = 
			new HashMap<String, String>(gn.getAttributes());
		HashMap<String, String> additionalTerms = new HashMap<String, String>();
		HashMap<String, String> asserIdMap = new HashMap<String, String>();
		if (attributes != null && !attributes.isEmpty()) {
			Iterator<String> iterator = attributes.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				String value = (String) attributes.get(key);
				if (key.startsWith("Lease.AdditionalTerms.")) {
					String tag = key.substring(key
							.indexOf("Lease.AdditionalTerms.")
							+ "Lease.AdditionalTerms.".length());
					additionalTerms.put(tag, value);
				} else if (key.startsWith("Lease.AssetIdMap.")) {
					String tag = key.substring(key.indexOf("Lease.AssetIdMap.")
							+ "Lease.AssetIdMap.".length());
					asserIdMap.put(tag, value);
				}
			}
		}
		ret.setAssetIdAndTypeMap(asserIdMap);
		ret.setAdditionalTerms(additionalTerms);
		ret.setLastErrorMessage((String) gn.getAttributes().get(
				"Lease.LastErrorMessage"));
		return ret;
	}

	/**
	 * add lease.
	 * @param initLease
	 * 		the initial lease.
	 * @return the added lease.
	 * @throws Exception
	 */
	public Lease add(Lease initLease) throws Exception {
		try {
			long begin = System.currentTimeMillis();
			if (initLease == null) {
				String msg = "the input initLease is null.";
				log.error(msg);
				throw new Exception(msg);
			}

			initLease.validate();
			this.validateNameDuplicatedInSameType(initLease);
			Lease clone = initLease.clone();

			clone.setLifecycleState(LeaseLifeCycleState.PENDING);
			if (clone.getAcl() == null || "".equals(clone.getAcl())) {
				clone.setAcl(LeaseConstants.DEFAULT_ACL);
			}
			Lease newLease = persistenceLease("add", clone);

			// FIXME here we add the new lease to lease life cycle monitor.
			LeaseLifeCycleMonitor.getInstance().addLeaseMonitoredOnce(newLease);

			long end = System.currentTimeMillis();
			log.info("add a lease " + newLease.getGuid() + " consuming time : "
					+ (end - begin));
			return newLease;
		} catch (CloneNotSupportedException e) {
			log.error("clone failed, due to : " + e);
			throw e;
		} catch (Exception e) {
			log.error("Error occured in LeaseManagerImpl.add method, due to "
					+ e);
			throw e;
		}
	}

	private void validateNameDuplicatedInSameType(Lease initLease)
			throws Exception {
		String[] searchFields = new String[] { "name", "type" };
		String[] operators = new String[] { "=", "=" };
		Object[] values = new Object[] { initLease.getName(),
				initLease.getType() };
		List<Lease> al = this.search(searchFields, operators, values);
		if (al != null && al.size() > 0) {
			throw new Exception("The name of lease " + initLease.getName()
					+ " is used already, please change it.");
		}
	}

	/**
	 * remove lease.
	 * @param leaseId
	 * 		the id of lease.
	 * @throws Exception
	 */
	public void remove(String leaseId) throws Exception {
		try {
			long begin = System.currentTimeMillis();
			if (leaseId == null || "".equals(leaseId)) {
				String msg = "the input leaseId is null or blank.";
				log.error(msg);
				throw new Exception(msg);
			}

			Lease lease = this.view(leaseId);
			if (lease == null) {
				String msg = "The lease with id " + leaseId + " is not exist.";
				log.error(msg);
				throw new Exception(msg);
			}

			LeaseLifeCycleState llcs = lease.getLifecycleState();
			if (llcs == null
					|| (!llcs.equals(LeaseLifeCycleState.CANCELLED)
							&& !llcs.equals(LeaseLifeCycleState.EXPIRED)
							&& !llcs.equals(LeaseLifeCycleState.FAIL)
							&& !llcs.equals(LeaseLifeCycleState.REJECTED) 
							&& !llcs.equals(LeaseLifeCycleState.TERMINATION))) {
				throw new Exception("Wrong state of lease, you can remove it "
						+ "when its state equals CANCELLED, EXPIRED,"
						+ " FAIL, REJECTED, and TERMINATION.");
			}

			gnm.unregister(lease.getGuid());

			// FIXME here we remove the lease from lease life cycle monitor.
			LeaseLifeCycleMonitor.getInstance().removeMonitoredLease(
					lease.getGuid());

			long end = System.currentTimeMillis();
			log.info("remove a lease " + lease.getGuid() + " consuming time : "
					+ (end - begin));
		} catch (Exception e) {
			log.error("Error occured in LeaseManagerImpl.remove method, due to "
					+ e);
			throw e;
		}
	}

	/**
	 * update lease.
	 * @param leaseId
	 * 		the id of lease.
	 * @param newLease
	 * 		the lease will be updated
	 * @return the updated lease
	 * @throws Exception
	 */
	public Lease update(String leaseId, Lease newLease) throws Exception {
		try {
			long begin = System.currentTimeMillis();
			if (leaseId == null || "".equals(leaseId)) {
				String msg = "the input leaseId is null or blank.";
				log.error(msg);
				throw new Exception(msg);
			}
			if (newLease == null) {
				String msg = "the input new lease is null.";
				log.error(msg);
				throw new Exception(msg);
			}

			newLease.validate();
			Lease clone = newLease.clone();
			Lease oldLease = this.view(leaseId);
			if (oldLease == null) {
				String msg = "The lease with id " + leaseId + " is not exist.";
				log.error(msg);
				throw new Exception(msg);
			}

			if (!oldLease.getGuid().equals(clone.getGuid())) {
				String msg = "The guid should not be modified.";
				log.error(msg);
				throw new Exception(msg);
			}
			Lease lease = this.persistenceLease("update", clone);
			long end = System.currentTimeMillis();
			log.info("update a lease " + lease.getGuid() + " consuming time : "
					+ (end - begin));
			return lease;
		} catch (CloneNotSupportedException e) {
			log.error("clone failed, due to : " + e);
			throw e;
		} catch (Exception e) {
			log.error("Error occured in LeaseManagerImpl.update method, due to "
					+ e);
			throw e;
		}
	}

	/**
	 * get the lease by id.
	 * @param leaseId
	 * 		the id of lease.
	 * @return the lease whose id is leaseId
	 * @throws Exception
	 */
	public Lease view(String leaseId) throws Exception {
		try {
			long begin = System.currentTimeMillis();
			if (leaseId == null || "".equals(leaseId)) {
				return null;
			}

			GNode gn = gnm.locate(leaseId);
			if (gn == null) {
				return null;
			}
			Lease lease = this.gnode2Lease(gn);
			long end = System.currentTimeMillis();
			log.info("view a lease " + lease.getGuid() + " consuming time : "
					+ (end - begin));
			return lease;
		} catch (Exception e) {
			log.error("Error occured in LeaseManagerImpl.view method, due to "
					+ e);
			throw e;
		}
	}

	/**
	 * search leases, if the query condition is name<>'peter' then
	 * 		the searchFields is {"name"},
	 * 		the operators is {"<>"},
	 * 		the values is {"peter"}.
	 * @param searchFields
	 * 		the search Fields
	 * @param operators
	 * 		the operators between each pair of field and value
	 * @param values
	 * 		the search values
	 * @return the searched leases list
	 * @throws Exception
	 */
	public List<Lease> search(String[] searchFields, String[] operators,
			Object[] values) throws Exception {
		try {
			long begin = System.currentTimeMillis();
			String sCondition = this.parseSearchCondition(searchFields,
					operators);
			String newCondition = sCondition;
			Object[] newValues = null;
			if (StringUtil.isEmpty(sCondition)) {
				newCondition = "rControllerType = ? and type = ?";
				newValues = new Object[] { LeaseConstants.LEASE_RCONTROLLER,
						GNodeConstants.GNODETYPE_RESOURCE };
			} else {
				newCondition = "(" + sCondition
						+ ") and rControllerType = ? and type = ?";
				newValues = new Object[values.length + 2];
				for (int i = 0; i < values.length; ++i) {
					newValues[i] = values[i];
				}
				newValues[newValues.length - 2] 
				          = LeaseConstants.LEASE_RCONTROLLER;
				newValues[newValues.length - 1] 
				          = GNodeConstants.GNODETYPE_RESOURCE;
			}
			List<GNode> lst = gnm.search(newCondition, newValues);
			// FIXME add rcontroller type to make a smaller scale search.
			if (lst == null || lst.size() == 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("No leases satisfy the search condition "
						+ newCondition + " with the search values ");
				for (int k = 0; k < newValues.length; k++) {
					sb.append(newValues[k].toString() + ",");
				}
				log.info(sb.toString());
				return new ArrayList<Lease>();
			}
			List<Lease> lealist = new ArrayList<Lease>();
			for (int i = 0; i < lst.size(); i++) {
				lealist.add(this.gnode2Lease((GNode) lst.get(i)));
			}
			long end = System.currentTimeMillis();
			StringBuilder sb = new StringBuilder();
			sb.append("search " + lealist.size()
					+ "leases with the search condition " + newCondition
					+ " and search value ");
			for (int k = 0; k < newValues.length; k++) {
				sb.append(newValues[k].toString() + ",");
			}
			sb.append(" consuming time : " + (end - begin));
			log.info(sb.toString());
			return lealist;
		} catch (Exception e) {
			log.error("Error occured in LeaseManagerImpl.search method, due to "
					+ e);
			throw e;
		}
	}

	private String parseSearchCondition(String[] searchFields,
			String[] operators) throws Exception {
		if (searchFields == null || searchFields.length <= 0) {
			return null;
		}
		if (operators == null || operators.length <= 0) {
			return null;
		}
		if (searchFields.length != operators.length) {
			throw new Exception("searchFields' length should be " 
					+ "equal with operators' length.");
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < searchFields.length; i++) {
			if (i > 0) {
				sb.append(" and ");
			}
			String s = searchFields[i].trim();
			String operator = operators[i].trim();
			if (s.indexOf("additionalTerms") > -1) {
				try {
					String tag1 = s.substring(s.indexOf("'") + 1);
					String tag2 = tag1.substring(0, tag1.indexOf("'"));
					sb.append("attributes['Lease.AdditionalTerms." + tag2
							+ "']");
				} catch (Exception e) {
					throw new Exception(
							"The search field of additionalTerms should be " 
							+ "like additionalTerms['key'].");
				}
			} else if (s.equals("tenantId")) {
				sb.append("ownerID");
			} else if (s.equals("assetIdList")) {
				sb.append("attributes['Lease.AssetIdMap']");
			} else if (s.equals("assetMatchMaker")) {
				sb.append("attributes['Lease.AssetMatchMaker']");
			} else if (s.equals("lifecycleState")) {
				sb.append("attributes['Lease.LifecycleState']");
			} else if (s.equals("effectiveTime")) {
				sb.append("attributes['Lease.EffectiveTime']");
			} else if (s.equals("expireTime")) {
				sb.append("attributes['Lease.ExpireTime']");
			} else if (s.equals("duration")) {
				sb.append("attributes['Lease.Duration']");
			} else if (s.equals("preemptible")) {
				sb.append("attributes['Lease.Preemptible']");
			} else if (s.equals("type")) {
				sb.append("attributes['Lease.Type']");
			} else {
				sb.append(s);
			}
			sb.append(" " + operator + " ?");
		}
		String ret = sb.toString();
		if (ret == null && "".equals(ret)) {
			return null;
		} else {
			return ret.trim();
		}
	}

	/**
	 * cancel a lease.
	 * @param leaseId
	 * 		the id of lease
	 * @throws Exception
	 */
	public void cancel(String leaseId) throws Exception {
		try {
			long begin = System.currentTimeMillis();
			if (leaseId == null || "".equals(leaseId)) {
				throw new Exception("The input leaseId is null or blank.");
			}
			Lease lease = null;
			synchronized (LeaseConstants.CANCEL_LOCK) {
				lease = this.view(leaseId);
				if (lease == null) {
					throw new Exception("The lease with id " + leaseId
							+ " is not exist.");
				}
				if (!lease.getLifecycleState().equals(
						LeaseLifeCycleState.PENDING)) {
					throw new Exception(
							"The lease with id "
									+ leaseId
									+ " is working, cannot be cancelled now."
									+ " Please wait until it's active, " 
									+ "then you can remove it.");
				}
				lease.setLifecycleState(LeaseLifeCycleState.CANCELLED);
				this.update(lease.getGuid(), lease);
				LeaseLifeCycleMonitor.getInstance().removeMonitoredLease(
						lease.getGuid());
			}
			long end = System.currentTimeMillis();
			log.info("cancel a lease with id " + lease.getGuid()
					+ " consuming time : " + (end - begin));
			return;
		} catch (Exception e) {
			log.error("Error occured in LeaseManagerImpl.cancel method, due to "
					+ e);
			throw e;
		}
	}

	/**
	 * This method is used to terminate the lease. It will release all leased
	 * assets and use asset controller to do some compensation operations for
	 * all assets go back to the available state. This method do not remove the
	 * lease. If you want to delete the lease, please invoke the remove method.
	 * 
	 * @param leaseId
	 * @throws Exception
	 */
	public void terminate(String leaseId) throws Exception {
		// three important steps.

		// 1st, according to different lease state, do different logic to
		// proccess termination.

		// 2nd, unreserve all assets in the lease.

		// 3rd, update the lease state to termination.
		try {
			long begin = System.currentTimeMillis();
			if (leaseId == null || "".equals(leaseId)) {
				throw new Exception("The input leaseId is null or blank.");
			}
			Lease lease = null;
			synchronized (LeaseConstants.TERMINATE_LOCK) {
				lease = this.view(leaseId);
				if (lease == null) {
					throw new Exception("The lease with id " + leaseId
							+ " is not exist.");
				}
				if (lease.getLifecycleState().equals(
						LeaseLifeCycleState.PREPROCESSING)) {
					// this state means the lease is just negotiation ok, due to
					// the LeaseConstants.TERMINATE_LOCK.

					try {
						String amm = lease.getAssetMatchMaker();
						AssetMatchMaker ammaker = (AssetMatchMaker) Class
								.forName(amm).newInstance();
						ammaker.handleTermination(lease);
					} catch (Exception e) {
						log.warn("Exception occurred when "
								+ "handle termination for the lease ("
								+ lease.getGuid() + lease.getName()
								+ "), due to" + e.toString());
					}
				} else if (lease.getLifecycleState().equals(
						LeaseLifeCycleState.READY)) {
					// this state means the lease is just preprocessing ok, due
					// to the LeaseConstants.TERMINATE_LOCK.
					HashMap<String, String> la = lease.getAssetIdAndTypeMap();
					if (la != null && !la.isEmpty()) {
						Iterator<String> iterator = la.keySet().iterator();
						while (iterator != null && iterator.hasNext()) {
							String assetid = (String) iterator.next();
							try {
								this.ami.handleAntiPreprocessing(assetid,
										lease.getGuid());
							} catch (Exception e) {
								log.warn("Ignore Exception occurred when "
										+ "handle AntiPreprocessing "
										+ " for the lease (" + lease.getGuid()
										+ lease.getName() + "), due to"
										+ e.toString());
								// Ignore error.
							}
						}
					}
				} else if (lease.getLifecycleState().equals(
						LeaseLifeCycleState.EFFECTIVE)) {
					// this state means the lease is just check status ok or ,
					// due
					// to the LeaseConstants.TERMINATE_LOCK.
					HashMap<String, String> la = lease.getAssetIdAndTypeMap();
					if (la != null && !la.isEmpty()) {
						Iterator<String> iterator = la.keySet().iterator();
						while (iterator != null && iterator.hasNext()) {
							String assetid = (String) iterator.next();
							try {
								this.ami.handleAntiReady(assetid,
										lease.getGuid());
							} catch (Exception e) {
								log.warn("Ignore Exception occurred when "
										+ "handle AntiReady for the lease ("
										+ lease.getGuid() + lease.getName()
										+ "), due to" + e.toString());
								// Ignore error.
							}
						}
					}
				}
				// FIXME, we can unreserve the assets at first, due to
				// LeaseConstants.TERMINATE_LOCK. Because in the negotiation
				// stage, it will also apply the LeaseConstants.TERMINATE_LOCK.
				this.unReservation(lease.getAssetIdAndTypeMap(),
						lease.getGuid());
				lease.setLifecycleState(LeaseLifeCycleState.TERMINATION);
				this.update(lease.getGuid(), lease);
				LeaseLifeCycleMonitor.getInstance().removeMonitoredLease(
						lease.getGuid());
			}
			long end = System.currentTimeMillis();
			log.info("terminate a lease with id " + lease.getGuid()
					+ " consuming time : " + (end - begin));
			return;
		} catch (Exception e) {
			log.error("Error occured in LeaseManagerImpl.cancel method, due to "
					+ e);
			throw e;
		}
	}

	private void unReservation(HashMap<String, String> assetIdAndTypeMap,
			String leaseId) {
		if (assetIdAndTypeMap == null || assetIdAndTypeMap.size() <= 0) {
			return;
		}

		AssetManagerImpl ami = new AssetManagerImpl();
		Iterator<String> it = assetIdAndTypeMap.keySet().iterator();
		while (it.hasNext()) {
			String assetid = (String) it.next();
			// TODO could be improved by batch unreservation for assets who have
			// same assetLeaser.
			try {
				Asset unreserveAsset = ami.unReserve(assetid, leaseId);
				if (unreserveAsset == null) {
					continue;
				}
				log.info("The asset " + unreserveAsset.getName()
						+ " is unreserved by the lease (" + leaseId + ").");
			} catch (Exception e) {
				log.error("UnReservation error : " + e.toString());
				// Ignore this error.
				continue;
			}
		}
		return;
	}

	/**
	 * handle expired lease.
	 * @param lease
	 * @return
	 * @throws Exception
	 */
	public Lease handleExpired(Lease lease) throws Exception {
		try {
			long begin = System.currentTimeMillis();
			Lease clone = lease.clone();

			// FIXME to handle all expired assests in the lease.
			// No need to handle PENDING, NEGOTIATING states, due to expired
			// check is before them. So only to set the state to expired is ok.
			// Also because expired check is before preprocessing, so when we
			// handle expired at preprocessing state, the assets are not
			// preprocessed yet.
			if (lease.getLifecycleState().equals(LeaseLifeCycleState.READY)) {
				HashMap<String, String> la = lease.getAssetIdAndTypeMap();
				if (la != null && !la.isEmpty()) {
					Iterator<String> iterator = la.keySet().iterator();
					while (iterator != null && iterator.hasNext()) {
						String assetid = (String) iterator.next();
						try {
							this.ami.handleAntiPreprocessing(assetid,
									lease.getGuid());
						} catch (Exception e) {
							log.warn("Ignore Exception occurred when "
									+ "handle expired for the lease ("
									+ lease.getGuid() + lease.getName()
									+ "), due to" + e.toString());
						}
					}
				}
			} else if (lease.getLifecycleState().equals(
					LeaseLifeCycleState.EFFECTIVE)) {
				HashMap<String, String> la = lease.getAssetIdAndTypeMap();
				if (la != null && !la.isEmpty()) {
					Iterator<String> iterator = la.keySet().iterator();
					while (iterator != null && iterator.hasNext()) {
						String assetid = (String) iterator.next();
						try {
							this.ami.handleAntiReady(assetid, lease.getGuid());
						} catch (Exception e) {
							log.warn("Ignore Exception occurred when "
									+ "handle expired for the lease ("
									+ lease.getGuid() + lease.getName()
									+ "), due to" + e.toString());
						}
					}
				}
			}
			HashMap<String, String> la = lease.getAssetIdAndTypeMap();
			this.unReservation(la, lease.getGuid());
			clone.setLifecycleState(LeaseLifeCycleState.EXPIRED);
			this.update(lease.getGuid(), clone);
			long end = System.currentTimeMillis();
			log.info("handle an expired lease with id " + lease.getGuid()
					+ " consuming time : " + (end - begin));
			return clone;
		} catch (Exception e) {
			log.error("Error occured in LeaseManagerImpl." 
					+ "handleExpired method, due to "
					+ e);
			throw e;
		}
	}
}
