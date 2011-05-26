/*
 *  @(#)Node.java  2010-5-27
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

import java.util.ArrayList;
import java.util.List;

import org.lingcloud.molva.xmm.util.XmlUtil;
import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.xmm.util.XMMConstants;
import org.lingcloud.molva.xmm.util.XMMUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-8-8<br>
 * @author Xiaoyi Lu<br>
 * @email luxiaoyi@software.ict.ac.cn
 */
public class Node extends Asset {

	/**
	 * the serial ID.
	 */
	private static final long serialVersionUID = 8683250584233950295L;

	public static final String HOSTNAME = "hostname";

	public static final String PUBLIC_IPS = "publicips";

	public static final String NICS = "nics";

	public static final String PRIVATE_IPS = "privateips";

	public static final String RUNNING_STATUS = "runningStatus";

	public static final String ACCESSWAY = "accessWay";

	public static final String IS_HEAD_NODE = "headNodeTag";

	private static final String CPUARCH = "cpuArch";

	public static final String CPUMODAL = "cpuModal";

	public static final String CPUSPEED = "cpuSpeed";

	public static final String CPUNUM = "cpuNum";

	public static final String FREE_CPU = "freeCpu";

	public static final String MEMSIZE = "memSize";

	public static final String FREE_MEMORY = "freeMemory";

	/**
	 * The default constructor.
	 * 
	 */
	public Node() {
		init();
	}

	private void init() {
		super.setAcl(XMMConstants.DEFAULT_ACL);
	}

	/**
	 * to construct a Node from a right type of Asset.
	 * 
	 * @param Asset
	 *            the asset object.
	 */
	public Node(Asset asset) {
		super(asset);
		if (asset.getAssetController() == null) {
			String msg = "Node's asset controller should not be null.";
			throw new RuntimeException(msg);
		}
	}

	/**
	 * Clone an object.
	 * 
	 * @return the cloned object.
	 * @throws CloneNotSupportedException
	 *             Not support clone.
	 */
	public Node clone() throws CloneNotSupportedException {
		try {
			Node node = new Node((Asset) super.clone());
			return node;
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

	public int getCpuNum() {
		return XMMUtil.isBlankOrNull((String) this.getAttributes().get(
				CPUNUM)) ? 0 : Integer.parseInt((String) this.getAttributes()
				.get(CPUNUM));
	}

	public void setCpuNum(int cpuNum) {
		if (cpuNum <= 0) {
			cpuNum = 0;
		}
		this.getAttributes().put(CPUNUM, "" + cpuNum);
	}

	public int getMemorySize() {
		return XMMUtil.isBlankOrNull((String) this.getAttributes().get(
				MEMSIZE)) ? 0 : Integer.parseInt((String) this.getAttributes()
				.get(MEMSIZE));
	}

	public void setMemsize(int memSize) {
		if (memSize <= 0) {
			memSize = 0;
		}
		this.getAttributes().put(MEMSIZE, "" + memSize);
	}

	public int getFreeCpu() {
		return XMMUtil.isBlankOrNull((String) this.getAttributes().get(
				FREE_CPU)) ? 0 : Integer.parseInt((String) this.getAttributes()
				.get(FREE_CPU));
	}

	public void setFreeCpu(int freeCpu) {
		this.getAttributes().put(FREE_CPU, "" + freeCpu);
	}

	public int getFreeMemory() {
		return XMMUtil.isBlankOrNull((String) this.getAttributes().get(
				FREE_MEMORY)) ? 0 : Integer.parseInt((String) this
				.getAttributes().get(FREE_MEMORY));
	}

	public void setFreeMemory(int freeMemory) {
		this.getAttributes().put(FREE_MEMORY, "" + freeMemory);
	}

	public String getPartitionId() {
		return super.getAssetLeaserId();
	}

	public void setPartitionId(String parid) {
		super.setAssetLeaserId(parid);
	}

	public String getHostName() {
		return (String) super.getAttributes().get(Node.HOSTNAME);
	}

	public void setHostName(String hostName) {
		this.getAttributes().put(Node.HOSTNAME, hostName);
	}

	public String getVirtualClusterID() {
		return super.getLeaseId();
	}

	public void setVirtualClusterID(String virtualClusterID) {
		super.setLeaseId(virtualClusterID);
	}

	public String[] getPublicIps() {
		String pips = this.getAttributes().get(Node.PUBLIC_IPS);
		if (pips == null || "".equals(pips)) {
			// a minor bug.
			return new String[] {};
		}
		return pips.split(",");
	}

	public void setPublicIps(String[] ips) {
		if (ips == null || ips.length == 0) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ips.length; i++) {
			sb.append(ips[i]);
			if (i != ips.length - 1) {
				sb.append(",");
			}
		}
		this.getAttributes().put(Node.PUBLIC_IPS, sb.toString());
	}

	public List<Nic> getNics() {
		// it is hard to distinguish the private ip nic and public nic in the
		// total nics. so the private ip, public ip are get from user input. The
		// total nics are polled automatically.
		String liststr = (String) this.getAttributes().get(Node.NICS);
		return (List<Nic>) XmlUtil.fromXml(liststr);
	}

	public void setNics(List<Nic> nicli) {
		String liststr = XmlUtil.toXml(nicli);
		this.getAttributes().put(Node.NICS, "" + liststr);
	}

	public String[] getPrivateIps() {
		// it is hard to distinguish the private ip nic and public nic in the
		// total nics. so the private ip, public ip are get from user input. The
		// total nics are polled automatically, but the vm may not need to be
		// checked, here we keep coherent with physical node.
		String pips = this.getAttributes().get(Node.PRIVATE_IPS);
		if (pips == null || "".equals(pips)) {
			return new String[] { "" };
		}
		return pips.split(",");
	}

	public void setPrivateIps(String[] ips) {
		if (ips == null || ips.length == 0) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ips.length; i++) {
			sb.append(ips[i]);
			if (i != ips.length - 1) {
				sb.append(",");
			}
		}
		this.getAttributes().put(Node.PRIVATE_IPS, sb.toString());
	}

	public void setRunningStatus(String runningStatus) {
		this.getAttributes().put(RUNNING_STATUS, runningStatus);
	}

	public String getRunningStatus() {
		return this.getAttributes().get(RUNNING_STATUS);
	}

	public List<String> getAccessWay() {
		String awstr = (String) this.getAttributes().get(ACCESSWAY);
		return (List<String>) XmlUtil.fromXml(awstr);
	}

	public void setAccessWay(List<String> aws) {
		String awstr = XmlUtil.toXml(aws);
		this.getAttributes().put(ACCESSWAY, awstr);
	}

	public List<Nic> getPrivateIpNics() {
		List<Nic> privateNics = new ArrayList<Nic>();
		List<Nic> nics = this.getNics();
		if (nics == null || nics.size() <= 0) {
			return privateNics;
		}
		String[] priips = this.getPrivateIps();
		for (int i = 0; i < nics.size(); i++) {
			Nic nic = nics.get(i);
			if (nic == null) {
				continue;
			}
			String ip = nic.getIp();
			for (int j = 0; j < priips.length; j++) {
				if (ip.equals(priips[j])) {
					privateNics.add(nic);
					break;
				}
			}
		}
		return privateNics;
	}

	public List<Nic> getPublicIpNics() {
		List<Nic> publicNics = new ArrayList<Nic>();
		List<Nic> nics = this.getNics();
		if (nics == null || nics.size() <= 0) {
			return publicNics;
		}
		String[] pubips = this.getPublicIps();
		for (int i = 0; i < nics.size(); i++) {
			Nic nic = nics.get(i);
			if (nic == null) {
				continue;
			}
			String ip = nic.getIp();
			for (int j = 0; j < pubips.length; j++) {
				if (ip.equals(pubips[j])) {
					publicNics.add(nic);
					break;
				}
			}
		}
		return publicNics;
	}

	public void setHeadNode(boolean isHeadNode) {
		this.getAttributes().put(IS_HEAD_NODE, "" + isHeadNode);
	}

	public boolean isHeadNode() {
		boolean tag = false;
		try {
			tag = Boolean.valueOf(this.getAttributes().get(IS_HEAD_NODE));
		} catch (Exception e) {
			return tag;
		}
		return tag;
	}

	public String getCpuArch() {
		return this.getAttributes().get(CPUARCH);
	}

	public void setCpuArch(String cpuModal) {
		this.getAttributes().put(CPUARCH, cpuModal);
	}

	public String getCpuModal() {
		return this.getAttributes().get(CPUMODAL);
	}

	public void setCpuModal(String cpuModal) {
		this.getAttributes().put(CPUMODAL, cpuModal);
	}

	public int getCpuSpeed() {
		return XMMUtil.isBlankOrNull((String) this.getAttributes().get(
				CPUSPEED)) ? 0 : Integer.parseInt((String) this.getAttributes()
				.get(CPUSPEED));
	}

	public void setCpuSpeed(int cpuSpeed) {
		this.getAttributes().put(CPUSPEED, "" + cpuSpeed);
	}
}
