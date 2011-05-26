/*
 *  @(#)VirtualNetwork.java  2010-5-27
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

import java.util.HashMap;
import java.util.List;

import org.lingcloud.molva.xmm.util.XmlUtil;
import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.xmm.util.XMMConstants;
import org.lingcloud.molva.xmm.util.XMMUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2009-9-15<br>
 * @author Xiaoyi Lu<br>
 * @email luxiaoyi@software.ict.ac.cn
 */
public class VirtualNetwork extends Asset {

	/**
	 * the serial ID.
	 */
	private static final long serialVersionUID = 8635928074327770992L;

	public static final String AUTO_CREATE_TAG = "AUTO_CREATE_TAG";

	public static final String VN_RESERVE_NODE_TAG = "VN_RESERVE_NODE_TAG";

	/**
	 * the constructor.
	 */
	public VirtualNetwork() {
		// To prohibit be set to other value. But can't prevent set value from
		// super class.
		init();
	}

	/**
	 * to construct a VirtualNetwork from a right type of Asset.
	 * 
	 * @param asset
	 *            the asset object.
	 */
	public VirtualNetwork(Asset asset) {
		super(asset);
		if (asset.getAssetController() == null) {
			String msg = "VirtualNetwork's asset controller should not be null.";
			throw new RuntimeException(msg);
		}
		if (asset.getType() == null
				|| !asset.getType().equals(
						XMMConstants.VIRTUAL_NETWORK_TYPE)) {
			throw new RuntimeException(
					"The VirtualNetwork's type should be setted as "
							+ XMMConstants.VIRTUAL_NETWORK_TYPE + ".");
			// asset.getAttributes().put("assetType", "Partition");
		}
	}

	private void init() {
		// FIXME the asset controller can be assigned and implemented by anyone.
		// So we can't set it.
		super.setAcl(XMMConstants.DEFAULT_ACL);
		super.setType(XMMConstants.VIRTUAL_NETWORK_TYPE);
	}

	/**
	 * set the type of VirtualNetwork. It must override the method that make
	 * sure the VirtualNetwork has right type.
	 * 
	 * @see XMMConstants for more detailed info.
	 * @param type
	 *            the type of VirtualNetwork.
	 */
	public void setType(int type) {
		// To prohibit be set to other value. But can't prevent set value from
		// super class.
		super.setType(XMMConstants.VIRTUAL_NETWORK_TYPE);
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
	public VirtualNetwork clone() throws CloneNotSupportedException {
		try {
			VirtualNetwork vh = new VirtualNetwork((Asset) super.clone());
			return vh;
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

	public String getPartitionId() {
		return super.getAssetLeaserId();
	}

	public void setPartitionId(String parid) {
		super.setAssetLeaserId(parid);
	}

	public int getNetworkSize() {
		return XMMUtil.isBlankOrNull((String) this.getAttributes().get(
				XMMConstants.NETWORK_SIZE)) ? 0 : Integer
				.parseInt((String) this.getAttributes().get(
						XMMConstants.NETWORK_SIZE));
	}

	@SuppressWarnings("unchecked")
	public void setNetworkSize(int size) {
		this.getAttributes().put(XMMConstants.NETWORK_SIZE, "" + size);
	}

	public List<Nic> getPrivateIpNics() {
		String listr = (String) this.getAttributes().get(
				XMMConstants.PRIVATE_IP_NICS);
		List li = (List) XmlUtil.fromXml(listr);
		return li;
	}

	@SuppressWarnings("unchecked")
	public void setPrivateIpNics(List<Nic> nics) {
		String listr = XmlUtil.toXml(nics);
		this.getAttributes().put(XMMConstants.PRIVATE_IP_NICS, listr);
	}

	public HashMap<String, Nic> getPublicIpNics() {
		// The key is the private ip address, and the Nic is public nic.
		String listr = (String) this.getAttributes().get(
				XMMConstants.PUBLIC_IP_NICS);
		HashMap<String, Nic> li = (HashMap<String, Nic>) XmlUtil.fromXml(listr);
		return li;
	}

	public void setPublicIpNics(HashMap<String, Nic> nics) {
		// The key is private ip address, the Nic is the public nic.
		String listr = XmlUtil.toXml(nics);
		this.getAttributes().put(XMMConstants.PUBLIC_IP_NICS, listr);
	}

	public String getHeadNodeIp() {
		return (String) this.getAttributes().get(XMMConstants.HEAD_NODE_IP);
	}

	public void setHeadNodeIp(String hnip) {
		this.getAttributes().put(XMMConstants.HEAD_NODE_IP, hnip);
	}

	public String getBridge() {
		return (String) this.getAttributes().get(XMMConstants.BRIDGE);
	}

	public void setBridge(String bridge) {
		this.getAttributes().put(XMMConstants.BRIDGE, bridge);
	}

	public List getVirtualGateWay() {
		String listr = (String) this.getAttributes().get(
				XMMConstants.VIRTUAL_GW);
		List li = (List) XmlUtil.fromXml(listr);
		return li;
	}

	public void setVirtualGateWay(List<Nic> gw) {
		String listr = XmlUtil.toXml(gw);
		this.getAttributes().put(XMMConstants.VIRTUAL_GW, listr);
	}

	public String getClusterID() {
		return super.getLeaseId();
	}

	public void setClusterID(String vcid) {
		super.setLeaseId(vcid);
	}

	public HashMap<String, String> getCreateVirtualGateWayCommands() {
		String mapstr = (String) this.getAttributes().get(
				XMMConstants.CREATE_VIRTUAL_GW);
		HashMap<String, String> map = (HashMap<String, String>) XmlUtil
				.fromXml(mapstr);
		return map;
	}

	public void setCreateVirtualGateWayCommands(HashMap<String, String> gwcm) {
		String mapstr = XmlUtil.toXml(gwcm);
		this.getAttributes().put(XMMConstants.CREATE_VIRTUAL_GW, mapstr);
	}

	public boolean isAutoCreate() {
		return XMMUtil.isBlankOrNull((String) this.getAttributes().get(
				VirtualNetwork.AUTO_CREATE_TAG)) ? false : Boolean
				.parseBoolean((String) this.getAttributes().get(
						VirtualNetwork.AUTO_CREATE_TAG));
	}

	public void setAutoCreate(boolean tag) {
		this.getAttributes().put(VirtualNetwork.AUTO_CREATE_TAG, "" + tag);
	}

	public boolean isNeedToReserveNode() {
		return XMMUtil.isBlankOrNull((String) this.getAttributes().get(
				VirtualNetwork.VN_RESERVE_NODE_TAG)) ? false : Boolean
				.parseBoolean((String) this.getAttributes().get(
						VirtualNetwork.VN_RESERVE_NODE_TAG));
	}

	public void setNeedToReserveNode(boolean tag) {
		this.getAttributes().put(VirtualNetwork.VN_RESERVE_NODE_TAG, "" + tag);
	}

}
