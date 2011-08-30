/*
 *  @(#)NodeRequirement.java  2010-5-27
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

import java.io.Serializable;
import java.util.HashMap;

import org.lingcloud.molva.xmm.deploy.policy.VirtualMachineDeployPolicier;

/**
 * <strong>Purpose: Describe user requirement of node.</strong><br>
 * Notice: not every field will be supported now.
 * 
 * @version 1.0.1 2010-7-21<br>
 * @author Xiaoyi Lu<br>
 */
public class NodeRequirement implements Serializable {

	private static final long serialVersionUID = -365270509312805185L;

	// XXX this may be unused, only notice me to consider these requirements.
	public static final String X86_64 = "x86_64";

	public static final String X86_I386 = "x86_i386";

	// XXX we may support a virtual cluster cross different partitions.
	private String partitionId;

	private boolean isHeadNode;

	// XXX use PhysicalNode and VirtualNode classes' name to identify node type.
	private String nodeClassName;

	private String privateIP;

	private boolean isNeedPublicIP;

	private String bridge;

	private String virtualApplicanceID;

	private String cpuArch;

	private int cpuNum;

	private int cpuSpeed;

	private String cpuModal;

	private int memorySize;

	private int diskSize;

	private String vmDeployPolicer = VirtualMachineDeployPolicier.class
			.getName();

	// key: random/effect/performance/loadaware/assign
	// value: if key=assign, then value=ip address of physical node.
	private HashMap<String, String> vmDeployParams;

	private HashMap<String, String> otherTerms = new HashMap<String, String>();

	public NodeRequirement() {

	}

	// For detail requirements.
	public NodeRequirement(String partitionId, boolean isHeadNode,
			String privateIP, boolean isNeedPublicIP, String vaid, int cpuNum,
			int memorySize) {
		this.setPartitionId(partitionId);
		this.setHeadNode(isHeadNode);
		this.setPrivateIP(privateIP);
		this.setNeedPublicIP(isNeedPublicIP);
		this.setVirtualApplicanceID(vaid);
		this.setCpuNum(cpuNum);
		this.setMemorySize(memorySize);
	}

	// For all node have the same requirements.
	public NodeRequirement(String partitionId, boolean isNeedPublicIP,
			String vaid, int cpuNum, int memorySize) {
		this.setPartitionId(partitionId);
		this.setNeedPublicIP(isNeedPublicIP);
		this.setVirtualApplicanceID(vaid);
		this.setCpuNum(cpuNum);
		this.setMemorySize(memorySize);
	}

	public String getCpuArch() {
		return cpuArch;
	}

	public void setCpuArch(String cpuArch) {
		this.cpuArch = cpuArch;
	}

	public int getCpuNum() {
		return cpuNum;
	}

	public void setCpuNum(int cpuNum) {
		this.cpuNum = cpuNum;
	}

	public int getCpuSpeed() {
		return cpuSpeed;
	}

	public void setCpuSpeed(int cpuSpeed) {
		this.cpuSpeed = cpuSpeed;
	}

	public int getDiskSize() {
		return diskSize;
	}

	public void setDiskSize(int diskSize) {
		this.diskSize = diskSize;
	}

	public int getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(int memorySize) {
		this.memorySize = memorySize;
	}

	public String getNodeClassName() {
		return nodeClassName;
	}

	public void setNodeClassName(String nodeType) {
		this.nodeClassName = nodeType;
	}

	public HashMap<String, String> getOtherTerms() {
		return otherTerms;
	}

	public void setOtherTerms(HashMap<String, String> otherTerms) {
		this.otherTerms = otherTerms;
	}

	public String getVirtualApplicanceID() {
		return virtualApplicanceID;
	}

	public void setVirtualApplicanceID(String virtualApplianceID) {
		this.virtualApplicanceID = virtualApplianceID;
	}

	public String getPrivateIP() {
		return privateIP;
	}

	public void setPrivateIP(String privateIP) {
		this.privateIP = privateIP;
	}

	public String getPartitionId() {
		return partitionId;
	}

	public void setPartitionId(String partitionId) {
		this.partitionId = partitionId;
	}

	public boolean isHeadNode() {
		return isHeadNode;
	}

	public void setHeadNode(boolean isHeadNode) {
		this.isHeadNode = isHeadNode;
	}

	public String getBridge() {
		return bridge;
	}

	public void setBridge(String bridge) {
		this.bridge = bridge;
	}

	public String getCpuModal() {
		return cpuModal;
	}

	public void setCpuModal(String cpuModal) {
		this.cpuModal = cpuModal;
	}

	public boolean isNeedPublicIP() {
		return isNeedPublicIP;
	}

	public void setNeedPublicIP(boolean isNeedPublicIP) {
		this.isNeedPublicIP = isNeedPublicIP;
	}

	public void setVmDeployPolicer(String vmDeployPolicer) {
		this.vmDeployPolicer = vmDeployPolicer;
	}

	public String getVmDeployPolicer() {
		return vmDeployPolicer;
	}

	public void setVmDeployParams(HashMap<String, String> vmDeployParams) {
		this.vmDeployParams = vmDeployParams;
	}

	public HashMap<String, String> getVmDeployParams() {
		return vmDeployParams;
	}
}
