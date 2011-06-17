/*
 *  @(#)AssetManagerImpl.java  2010-5-22
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.ocl.asset.AssetConstants.AssetState;
import org.lingcloud.molva.ocl.persistence.GNode;
import org.lingcloud.molva.ocl.persistence.GNodeConstants;
import org.lingcloud.molva.ocl.persistence.GNodeManager;
import org.lingcloud.molva.ocl.util.HashFunction;
import org.lingcloud.molva.ocl.poll.PollingTaskManager;
import org.lingcloud.molva.ocl.util.VoalUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-22<br>
 * @author Xiaoyi Lu<br>
 */

public class AssetManagerImpl {
	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(AssetManagerImpl.class);

	private GNodeManager gnm = null;

	public AssetManagerImpl() {
		try {
			gnm = new GNodeManager();
		} catch (Exception e) {
			log.error("Construct AssetManagerImpl error : " + e.toString());
			throw new RuntimeException(e.toString());
		}
	}

	public AssetManagerImpl(String namingUrl) {
		try {
			if (namingUrl == null || namingUrl.equals("")) {
				throw new Exception("the input url is null or blank.");
			}
			gnm = new GNodeManager();
		} catch (Exception e) {
			log.error("Construct AssetManagerImpl error : " + e.toString());
			throw new RuntimeException(e.toString());
		}
	}

	private Asset persistenceAsset(String method, Asset asset) 
		throws Exception {
		try {
			GNode gn = asset2GNode(asset);

			if (method.equals("add")) {
				GNode reg = gnm.register(gn);
				Asset newAsset = gnode2Asset(reg);
				return newAsset;
			} else if (method.equals("update")) {
				GNode newOne = gnm.update(gn);
				Asset newAsset = gnode2Asset(newOne);
				return newAsset;
			} else {
				String msg = "Unsupported method in Persistence Asset,"
						+ " Only support add and update.";
				throw new Exception(msg);
			}
		} catch (Exception e) {
			log.error("Persistence Asset Error : " + e.toString());
			throw e;
		}
	}
	
	private GNode asset2GNode(Asset asset) {
		GNode gn = new GNode();
		gn.setGuid(asset.getGuid());
		gn.setName(asset.getName());
		gn.setAcl(asset.getAcl());
		gn.setGroupID(asset.getGroupId());
		gn.setOwnerID(asset.getOwnerId());
		gn.setAddTime(asset.getAddTime());
		gn.setUpdateTime(asset.getUpdateTime());
		gn.setRControllerType(AssetConstants.ASSET_RCONTROLLER);
		gn.setType(GNodeConstants.GNODETYPE_RESOURCE);
		gn.setDescription(asset.getDescription());

		HashMap<String, String> attributes = asset.getAttributes();
		if (attributes != null && !attributes.isEmpty()) {
			Iterator<String> iterator = attributes.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				String value = attributes.get(key);
				gn.getAttributes().put("Asset.Attributes." + key, value);
			}
		}

		gn.getAttributes().put("Asset.AssetController",
				asset.getAssetController());

		gn.getAttributes().put("Asset.LeaseId", asset.getLeaseId());

		gn.getAttributes().put("Asset.Price",
				String.valueOf(asset.getPrice()));
		gn.getAttributes().put("Asset.Cai", asset.getCai());
		gn.getAttributes().put("Asset.AssetLeaserId",
				asset.getAssetLeaserId());
		gn.getAttributes().put("Asset.AssetState",
				asset.getAssetState().toString());
		gn.getAttributes().put("Asset.Type", asset.getType());
		gn.getAttributes().put("Asset.LastErrorMessage",
				asset.getLastErrorMessage());
		
		return gn;
	}

	private Asset gnode2Asset(GNode gn) {
		Asset ret = new Asset();
		/*
		 * FIXME Due to we use naming to save lease, so we leverage naming
		 * funcitons of guid, addtime, updatetime, acl, groupid, and ownerid
		 * generations. If we do not use naming later, these six fields should
		 * be maintained by ourselves. The methods of gnode2Asset and
		 * persistenceAsset should be overridden when we use other ways to save
		 * assets.
		 */
		ret.setGuid(gn.getGuid());
		ret.setAddTime(gn.getAddTime());
		ret.setUpdateTime(gn.getUpdateTime());
		ret.setAcl(gn.getAcl());
		ret.setGroupId(gn.getGroupID());
		ret.setOwnerId(gn.getOwnerID());
		ret.setDescription(gn.getDescription());

		ret.setName(gn.getName());

		HashMap<String, String> attributes = new HashMap<String, String>(
				gn.getAttributes());
		HashMap<String, String> assetAttributes = new HashMap<String, String>();
		
		if (attributes != null && !attributes.isEmpty()) {
			Iterator<String> iterator = attributes.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				String value = attributes.get(key);
				if (key.startsWith("Asset.Attributes.")) {
					String tag = key.substring(key.indexOf("Asset.Attributes.")
							+ "Asset.Attributes.".length());
					assetAttributes.put(tag, value);
				}
			}
		}
		ret.setAttributes(assetAttributes);
		ret.setAssetController((String) gn.getAttributes().get(
				"Asset.AssetController"));
		double price = Double.valueOf((String) gn.getAttributes().get(
				"Asset.Price"));
		ret.setPrice(price);
		ret.setLeaseId((String) gn.getAttributes().get("Asset.LeaseId"));
		ret.setCai((String) gn.getAttributes().get("Asset.Cai"));
		ret.setAssetLeaserId((String) gn.getAttributes().get(
				"Asset.AssetLeaserId"));
		ret.setAssetState(AssetState.valueOf((String) gn.getAttributes().get(
				"Asset.AssetState")));
		ret.setType((String) gn.getAttributes().get("Asset.Type"));
		ret.setLastErrorMessage((String) gn.getAttributes().get(
				"Asset.LastErrorMessage"));
		return ret;
	}

	/**
	 * add asset.
	 * @param initAsset
	 * 		the initial asset
	 * @param isOnlyAddMetaInfo
	 * 		whether only add the meta info
	 * @return the added asset
	 * @throws Exception
	 */
	public Asset add(Asset initAsset, boolean isOnlyAddMetaInfo)
			throws Exception {
		try {
			long begin = System.currentTimeMillis();
			if (initAsset == null) {
				String msg = "the input initAsset is null.";
				log.error(msg);
				throw new Exception(msg);
			}

			initAsset.validate();
			this.validateNameDuplicatedInSameType(initAsset);
			Asset clone = initAsset.clone();
			String assetLeaserId = clone.getAssetLeaserId();

			if (clone.getGuid() == null || "".equals(clone.getGuid())) {
				String guid = HashFunction.createGUID().toString();
				clone.setGuid(guid);
			}
			if (assetLeaserId == null || "".equals(assetLeaserId)) {
				clone.setAssetLeaserId(clone.getGuid());
			} else {
				Asset asset = this.view(assetLeaserId);
				if (asset == null) {
					throw new Exception("The leaser (" + assetLeaserId
							+ "( of the asset is not exist.");
				}
				clone.setAssetLeaserId(assetLeaserId);
			}
			clone.setCai("cloud://"
					+ VoalUtil.className2URI(clone.getAssetController()) + "//"
					+ clone.getName());
			Asset addedAsset = clone;
			AssetController ac = null;
			
			if (!isOnlyAddMetaInfo) {
				try {
					ac = (AssetController) Class.forName(
							clone.getAssetController()).newInstance();
					addedAsset = ac.add(clone);
				} catch (Exception e) {
					log.error("Add asset through " + clone.getAssetController()
							+ " failed.");
					throw e;
				}
			}

			addedAsset.setAssetState(AssetState.IDLE);

			Asset newAsset = persistenceAsset("add", addedAsset);
			long end = System.currentTimeMillis();
			log.info("add an asset " + newAsset.getGuid()
					+ " consuming time : " + (end - begin));
			return newAsset;
		} catch (CloneNotSupportedException e) {
			log.error("clone failed, due to : " + e);
			throw e;
		} catch (Exception e) {
			log.error("Error occured in AssetManagerImpl.add method, due to "
					+ e);
			throw e;
		}
	}

	private void validateNameDuplicatedInSameType(Asset initAsset)
			throws Exception {
		String[] searchFields = new String[] { "name", "type" };
		String[] operators = new String[] { "=", "=" };
		Object[] values = new Object[] { initAsset.getName(),
				initAsset.getType() };
		List<Asset> al = this.search(searchFields, operators, values);
		if (al != null && al.size() > 0) {
			throw new Exception("The name of asset " + initAsset.getName()
					+ " is used already, please change it.");
		}
	}

	/**
	 * remove the asset.
	 * @param assetId
	 * 		the id of asset
	 * @param isOnlyRemoveMetaInfo
	 * 		whether only remove the meta info
	 * @return the removed asset
	 * @throws Exception
	 */
	public Asset remove(String assetId, boolean isOnlyRemoveMetaInfo)
			throws Exception {
		if (assetId == null || "".equals(assetId)) {
			String msg = "the input assetId is null or blank.";
			log.error(msg);
			throw new Exception(msg);
		}
		try {
			long begin = System.currentTimeMillis();
			Asset asset = this.view(assetId);
			if (asset == null) {
				String msg = "The asset with id " + assetId + " is not exist.";
				log.error(msg);
				throw new Exception(msg);
			}
			// Bug fixed: when we remove any asset, we should check this asset
			// whether or not be referenced by other assets.
			String[] fields = new String[] { "assetLeaserId" };
			String[] opers = new String[] { "=" };
			Object[] values = new Object[] { asset.getGuid() };
			List<Asset> asli = this.search(fields, opers, values);
			// FIXME the root asset will reference itself, so we need to check
			// this.
			if (asli != null && !asli.isEmpty()) {
				for (int i = 0; i < asli.size(); i++) {
					Asset referasset = asli.get(i);
					if (asset.getGuid().equals(referasset.getGuid())) {
						continue;
					} else {
						String msg = "The asset with id " + assetId
								+ " is referenced by other assets.";
						log.error(msg);
						throw new Exception(msg);
					}
				}
			}
			if (!isOnlyRemoveMetaInfo) {
				try {
					AssetController ac = (AssetController) Class.forName(
							asset.getAssetController()).newInstance();
					ac.remove(asset);
				} catch (Exception e) {
					log.error("Remove asset through "
							+ asset.getAssetController() + " failed.");
					throw e;
				}
			}
			List<GNode> gnli = gnm.unregister(asset.getGuid());
			Asset removedAsset = this.gnode2Asset((GNode) gnli.get(0));
			long end = System.currentTimeMillis();
			log.info("remove an asset " + removedAsset.getGuid()
					+ " consuming time : " + (end - begin));
			return removedAsset;
		} catch (Exception e) {
			log.error("Error occured in AssetManagerImpl.remove method, due to "
					+ e);
			throw e;
		}
	}

	/**
	 * update the asset.
	 * @param assetId
	 * 		the id of asset
	 * @param newAsset
	 * 		the asset will be updated
	 * @return the updated asset
	 * @throws Exception
	 */
	public Asset update(String assetId, Asset newAsset) throws Exception {
		if (assetId == null || "".equals(assetId)) {
			String msg = "the input assetId is null or blank.";
			log.error(msg);
			throw new Exception(msg);
		}
		if (newAsset == null) {
			String msg = "the input new asset is null.";
			log.error(msg);
			throw new Exception(msg);
		}
		try {
			long begin = System.currentTimeMillis();
			newAsset.validate();
			Asset clone = newAsset.clone();
			Asset oldAsset = this.view(assetId);
			if (oldAsset == null) {
				String msg = "The asset with id " + assetId + " is not exist.";
				log.error(msg);
				throw new Exception(msg);
			}

			if (!oldAsset.getGuid().equals(clone.getGuid())) {
				String msg = "The guid should not be modified.";
				log.error(msg);
				throw new Exception(msg);
			}

			if (!oldAsset.getAssetLeaserId().equals(clone.getAssetLeaserId())) {
				String msg = "The asset leaser id should not be modified.";
				log.error(msg);
				throw new Exception(msg);
			}

			Asset asset = this.persistenceAsset("update", clone);
			long end = System.currentTimeMillis();
			log.info("update an asset " + asset.getGuid()
					+ " consuming time : " + (end - begin));
			return asset;
		} catch (CloneNotSupportedException e) {
			log.error("clone failed, due to : " + e);
			throw e;
		} catch (Exception e) {
			log.error("Error occured in AssetManagerImpl.update method, due to "
					+ e);
			throw e;
		}
	}

	/**
	 * get the asset by id.
	 * @param assetId
	 * 		the id of asset
	 * @return the asset whose id is assetId
	 * @throws Exception
	 */
	public Asset view(String assetId) throws Exception {
		if (assetId == null || "".equals(assetId)) {
			return null;
		}
		try {
			long begin = System.currentTimeMillis();
			GNode gn = gnm.locate(assetId);
			if (gn == null) {
				return null;
			}
			// for virtual appliance being not a valid asset.
			String acon = (String) gn.getAttributes().get(
					"Asset.AssetController");
			if (acon == null || "".equals(acon)) {
				return null;
			}
			Asset asset = this.gnode2Asset(gn);
			long end = System.currentTimeMillis();
			log.info("view an asset " + asset.getGuid() + " consuming time : "
					+ (end - begin));
			return asset;
		} catch (Exception e) {
			log.error("Error occured in AssetManagerImpl.view method, due to "
					+ e);
			throw e;
		}
	}

	public Asset refresh(Asset asset) throws Exception {
		if (asset == null) {
			String msg = "the input asset is null.";
			log.error(msg);
			throw new Exception(msg);
		}
		try {
			long begin = System.currentTimeMillis();
			asset.validate();
			Asset updatedAsset = null;
			try {
				AssetController ac = (AssetController) Class.forName(
						asset.getAssetController()).newInstance();
				Asset freshedAsset = ac.refresh(asset);
				if (freshedAsset == null) {
					long end = System.currentTimeMillis();
					log.info("fresh an asset " + asset.getGuid()
							+ " and it is disappeared, consuming time : "
							+ (end - begin));
					return null;
				}
				updatedAsset = this.update(asset.getGuid(), freshedAsset);
			} catch (Exception e) {
				log.error("Fresh asset through " + asset.getAssetController()
						+ " failed.");
				throw e;
			}
			long end = System.currentTimeMillis();
			log.info("fresh an asset " + updatedAsset.getGuid()
					+ " consuming time : " + (end - begin));
			return updatedAsset;
		} catch (Exception e) {
			log.error("Error occured in AssetManagerImpl.fresh method, due to "
					+ e);
			throw e;
		}
	}

	/**
	 * search assets, if the query condition is name<>'peter' then
	 * 		the searchFields is {"name"},
	 * 		the operators is {"<>"},
	 * 		the values is {"peter"}.
	 * @param searchFields
	 * 		the search Fields
	 * @param operators
	 * 		the operators between each pair of field and value
	 * @param values
	 * 		the search values
	 * @return the searched assets list
	 * @throws Exception
	 */
	public List<Asset> search(String[] searchFields, String[] operators,
			Object[] values) throws Exception {
		try {
			long begin = System.currentTimeMillis();
			String sCondition = this.parseSearchCondition(searchFields,
					operators);
			String newCondition = sCondition;
			Object[] newValues = null;
			if (sCondition == null || "".equals(sCondition)) {
				newCondition = "rControllerType = ? and type = ?";
				newValues = new Object[] { AssetConstants.ASSET_RCONTROLLER,
						GNodeConstants.GNODETYPE_RESOURCE };
			} else {
				newCondition = "(" + sCondition
						+ ") and rControllerType = ? and type = ?";
				newValues = new Object[values.length + 2];
				for (int i = 0; i < values.length; ++i) {
					newValues[i] = values[i];
				}
				newValues[newValues.length - 2] 
				          = AssetConstants.ASSET_RCONTROLLER;
				newValues[newValues.length - 1] 
				          = GNodeConstants.GNODETYPE_RESOURCE;
			}
			List<GNode> lst = gnm.search(newCondition, newValues);
			if (lst == null || lst.size() == 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("No assets satisfy the search condition "
						+ newCondition + " with the search values ");
				for (int k = 0; k < newValues.length; k++) {
					sb.append(newValues[k].toString() + ",");
				}
				log.info(sb.toString());
				return new ArrayList<Asset>();
			}
			List<Asset> lealist = new ArrayList<Asset>();
			for (int i = 0; i < lst.size(); i++) {
				lealist.add(this.gnode2Asset((GNode) lst.get(i)));
			}
			long end = System.currentTimeMillis();
			StringBuilder sb = new StringBuilder();
			sb.append("search " + lealist.size()
					+ "assets with the search condition " + newCondition
					+ " and search value ");
			for (int k = 0; k < newValues.length; k++) {
				sb.append(newValues[k].toString() + ",");
			}
			sb.append(" consuming time : " + (end - begin));
			log.info(sb.toString());
			return lealist;
		} catch (Exception e) {
			log.error("Error occured in AssetManagerImpl.search method, due to "
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
			
			if (s.indexOf("attributes") > -1) {
				try {
					String tag1 = s.substring(s.indexOf("'") + 1);
					String tag2 = tag1.substring(0, tag1.indexOf("'"));
					sb.append("attributes['Asset.Attributes." + tag2 + "']");
				} catch (Exception e) {
					throw new Exception("The search field of attributes" 
							+ " should be like attributes['key'].");
				}
				
			} else if (s.equals("price")) {
				sb.append("attributes['Asset.Price']");
			} else if (s.equals("cai")) {
				sb.append("attributes['Asset.Cai']");
			} else if (s.equals("assetLeaserId")) {
				sb.append("attributes['Asset.AssetLeaserId']");
			} else if (s.equals("assetState")) {
				sb.append("attributes['Asset.AssetState']");
			} else if (s.equals("assetController")) {
				sb.append("attributes['Asset.AssetController']");
			} else if (s.equals("leaseId")) {
				sb.append("attributes['Asset.LeaseId']");
			} else if (s.equals("type")) {
				sb.append("attributes['Asset.Type']");
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
	 * calculate the price of asset.
	 * @param asset
	 * @param params
	 * @return price
	 * @throws Exception
	 */
	public double calculatePrice(Asset asset, HashMap<String, String> params) 
		throws Exception {
		
		AssetController ac = (AssetController) Class.forName(
				asset.getAssetController()).newInstance();
		double price = ac.calculatePrice(asset, params);
		return price;
	}

	/**
	 * control the asset.
	 * @param asset
	 * @param operationName
	 * @param parameter
	 * @return
	 * @throws Exception
	 */
	public Object control(Asset asset, String operationName, Object[] parameter)
			throws Exception {
		try {
			long begin = System.currentTimeMillis();
			if (asset == null) {
				throw new Exception("The input asset is null.");
			}
			AssetController ac = (AssetController) Class.forName(
					asset.getAssetController()).newInstance();
			Object result = ac.control(asset, operationName, parameter);
			long end = System.currentTimeMillis();
			log.info("control asset " + asset.getGuid() + " consuming time : "
					+ (end - begin));
			return result;
		} catch (NoSuchMethodException e) {
			String msg = "No such method : " + operationName + " in "
					+ asset.getAssetController() + " class.";
			log.error(msg);
			throw new Exception(msg);
		} catch (InvocationTargetException e) {
			String msg = "Error occurred when invoke method : " + operationName
					+ " in " + asset.getAssetController() + " class, due to "
					+ e.getTargetException().toString();
			log.error(msg);
			throw new Exception(msg);
		} catch (Exception e) {
			log.error("Error occured in AssetManagerImpl." 
					+ "control method, due to " + e);
			throw e;
		}
	}

	/**
	 * handle pre-processing.
	 * @param assetId
	 * @param leaseId
	 * @return
	 * @throws Exception
	 */
	public Asset handlePreprocessing(String assetId, String leaseId)
			throws Exception {
		Asset asset = this.view(assetId);
		if (asset == null) {
			return null;
			
		}

		// XXX 1st, the ac of vm has to submit the virtual machine creation
		// requests to opennebula. 2nd, the ac of pm should begin to configure
		// cluster.
		if (asset.getAssetState().equals(AssetState.RESERVED)
				&& asset.getLeaseId().equals(leaseId)) {
			
			Asset initedAsset = (Asset) this.control(asset, "init",
					new Object[] { asset });
			return this.update(asset.getGuid(), initedAsset);
		} else {
			throw new Exception("The asset with id " + assetId
					+ " is not reserved for the lease " + leaseId);
		}
	}

	/**
	 * handle ready.
	 * @param assetId
	 * @param leaseId
	 * @return
	 * @throws Exception
	 */
	public Asset handleReady(String assetId, String leaseId) throws Exception {
		Asset asset = this.view(assetId);
		if (asset == null) {
			return null;
		}
		if (asset.getAssetState().equals(AssetState.RESERVED)
				&& asset.getLeaseId().equals(leaseId)) {
			Asset proAsset = (Asset) this.control(asset, "provision",
					new Object[] { asset });
			proAsset.setAssetState(AssetState.LEASED);
			return this.update(asset.getGuid(), proAsset);
		} else {
			throw new Exception("The asset with id " + assetId
					+ " is not reserved for the lease " + leaseId);
		}
	}

	/**
	 * 
	 * @param asset
	 * @param leaseId
	 * @param effectiveTime
	 *            : effectiveTime will be supported later.
	 * @return
	 * @throws Exception
	 */
	public Asset reserve(String assetId, String leaseId, 
			Date reserveEffectiveTime, Date reserveExpireTime) 
			throws Exception {
		// TODO effectiveTime will be handled later.
		try {
			long begin = System.currentTimeMillis();

			Asset reservedAsset = null;

			synchronized (AssetConstants.RESERVE_LOCK) {
				Asset viewedasset = this.view(assetId);
				if (viewedasset == null) {
					return null;
				}
				if (viewedasset.getAssetState().equals(AssetState.IDLE)) {
					viewedasset.setAssetState(AssetState.RESERVED);
					viewedasset.setLeaseId(leaseId);
					reservedAsset = this.update(viewedasset.getGuid(),
							viewedasset);
				} else if (viewedasset.getAssetState()
						.equals(AssetState.LEASED)
						&& !viewedasset.getLeaseId().equals(leaseId)) {
					throw new Exception("The asset " + viewedasset.getGuid()
							+ " is leased by lease " + viewedasset.getLeaseId()
							+ ".");
				} else if (viewedasset.getAssetState().equals(
						AssetState.RESERVED)
						&& !viewedasset.getLeaseId().equals(leaseId)) {
					throw new Exception("The asset " + viewedasset.getGuid()
							+ " is reserved by lease "
							+ viewedasset.getLeaseId() + ".");
				} else if (viewedasset.getAssetState().equals(
						AssetState.UNAVAILABLE)) {
					throw new Exception("The asset " + viewedasset.getGuid()
							+ " is unavailable.");
				}
			}

			long end = System.currentTimeMillis();
			if (reservedAsset != null) {
				log.info("Reserve an asset with id " + reservedAsset.getGuid()
						+ " and name " + reservedAsset.getName()
						+ " consuming time : " + (end - begin));
			}
			return reservedAsset;
		} catch (Exception e) {
			log.error("Error occured in AssetManagerImpl." 
					+ "reserve method, due to " + e);
			throw e;
		}
	}

	/**
	 * handle active.
	 * @param assetId
	 * @param leaseId
	 * @return
	 * @throws Exception
	 */
	public Asset handleActive(String assetId, String leaseId) throws Exception {
		Asset asset = this.view(assetId);
		if (asset == null) {
			return null;
			
		}

		if (asset.getAssetState().equals(AssetState.LEASED)
				&& asset.getLeaseId().equals(leaseId)) {
			// FIXME here due to phsical node type assets may
			// be refreshed by their own polling taskers. so we need to judge
			// the asset is or not polled.
			if (PollingTaskManager.isPolled(asset.getGuid())) {
				log.debug("The asset " + asset.getName()
						+ " is polled already.");
			} else {
				AssetController ac = (AssetController) Class.forName(
						asset.getAssetController()).newInstance();
				Asset freshedAsset = ac.refresh(asset);
				return this.update(asset.getGuid(), freshedAsset);
			}

			log.info("The asset " + asset.getName() + " of the lease "
					+ leaseId + " is running now.");
			return asset;
		} else {
			throw new Exception("The asset with id " + assetId
					+ " is not leased for the lease " + leaseId);
		}
	}

	/**
	 * unreserve.
	 * @param assetId
	 * @param leaseId
	 * @return
	 * @throws Exception
	 */
	public Asset unReserve(String assetId, String leaseId) throws Exception {
		try {
			long begin = System.currentTimeMillis();
			Asset unreservedAsset = null;

			synchronized (AssetConstants.RESERVE_LOCK) {
				Asset viewedasset = this.view(assetId);
				if (viewedasset == null) {
					return null;
				}
				if (viewedasset.getLeaseId() == null
						|| !viewedasset.getLeaseId().equals(leaseId)) {
					throw new Exception("Can not unReserve asset with id "
							+ assetId
							+ " due to the asset is not reserved by the lease "
							+ leaseId);
				}
				if (viewedasset.getAssetState().equals(AssetState.IDLE)) {
					return viewedasset;
				} else if (viewedasset.getAssetState()
						.equals(AssetState.LEASED)
						&& !viewedasset.getLeaseId().equals(leaseId)) {
					throw new Exception("The asset " + viewedasset.getGuid()
							+ " is leased by the lease " + leaseId + ".");
				} else if (viewedasset.getAssetState().equals(
						AssetState.RESERVED)
						&& !viewedasset.getLeaseId().equals(leaseId)) {
					throw new Exception("The asset " + viewedasset.getGuid()
							+ " is reserved by the lease " + leaseId + ".");
				} else if (viewedasset.getAssetState().equals(
						AssetState.UNAVAILABLE)) {
					throw new Exception("The asset " + viewedasset.getGuid()
							+ " is unavailable.");
				}
				viewedasset.setAssetState(AssetState.IDLE);
				viewedasset.setLeaseId(null);
				unreservedAsset = this.update(viewedasset.getGuid(),
						viewedasset);
			}
			long end = System.currentTimeMillis();
			log.info("unReserve an asset with id " + unreservedAsset.getGuid()
					+ " and name " + unreservedAsset.getName()
					+ " consuming time : " + (end - begin));
			return unreservedAsset;
		} catch (Exception e) {
			log.error("Error occured in AssetManagerImpl." 
					+ "unReserve method, due to " + e);
			throw e;
		}
	}

	/**
	 * handle anti pre-processing.
	 * @param assetid
	 * @param leaseId
	 * @throws Exception
	 */
	public void handleAntiPreprocessing(String assetid, String leaseId)
			throws Exception {
		try {
			long begin = System.currentTimeMillis();

			// FIXME we unreserve the assets in the lease manager logic.
			Asset viewedasset = this.view(assetid);
			if (viewedasset == null) {
				return;
			}
			if (viewedasset.getLeaseId() == null
					|| !viewedasset.getLeaseId().equals(leaseId)) {
				throw new Exception(
						"Can not handleAntiPreprocessing asset with id "
						+ assetid
						+ " due to the asset is not reserved by the lease "
						+ leaseId);
			}
			Asset uninitAsset = (Asset) this.control(viewedasset, "antiInit",
					new Object[] { viewedasset });
			this.update(viewedasset.getGuid(), uninitAsset);

			long end = System.currentTimeMillis();
			log.info("HandleAntiPreprocessing an asset with id "
					+ viewedasset.getGuid() + " and name "
					+ viewedasset.getName() + " consuming time : "
					+ (end - begin));
			return;
		} catch (Exception e) {
			log.error("Error occured in AssetManagerImpl."
					+ "handleAntiPreprocessing method, due to " + e);
			throw e;
		}
	}

	/**
	 * handle anti ready.
	 * @param assetid
	 * @param leaseId
	 * @throws Exception
	 */
	public void handleAntiReady(String assetid, String leaseId)
			throws Exception {
		try {
			long begin = System.currentTimeMillis();

			Asset viewedasset = this.view(assetid);
			if (viewedasset == null) {
				return;

			}
			if (viewedasset.getLeaseId() == null
					|| !viewedasset.getLeaseId().equals(leaseId)) {
				throw new Exception("Can not handleAntiReady asset with id "
						+ assetid
						+ " due to the asset is not reserved by the lease "
						+ leaseId);
			}
			
			Asset reasset = (Asset) this.control(viewedasset, "revoke",
					new Object[] { viewedasset });
			this.update(viewedasset.getGuid(), reasset);

			long end = System.currentTimeMillis();
			log.info("HandleAntiReady an asset with id "
					+ viewedasset.getGuid() + " and name "
					+ viewedasset.getName() + " consuming time : "
					+ (end - begin));
		} catch (Exception e) {
			log.error("Error occured in AssetManagerImpl." 
					+ "handleAntiReady method, due to " + e);
			throw e;
		}
	}
}
