/*
 *  @(#)VirtualClusterAMM.java  2010-5-27
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

package org.lingcloud.molva.xmm.amm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.lingcloud.molva.ocl.amm.AssetMatchMaker;
import org.lingcloud.molva.ocl.asset.AssetConstants;
import org.lingcloud.molva.ocl.lease.Lease;
import org.lingcloud.molva.xmm.ac.PartitionAC;
import org.lingcloud.molva.xmm.ac.VirtualNetworkAC;
import org.lingcloud.molva.xmm.ac.VirtualNodeAC;
import org.lingcloud.molva.xmm.deploy.policy.VirtualMachineDeployPolicier;
import org.lingcloud.molva.xmm.pojos.Nic;
import org.lingcloud.molva.xmm.pojos.NodeRequirement;
import org.lingcloud.molva.xmm.pojos.Partition;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.pojos.VirtualCluster;
import org.lingcloud.molva.xmm.pojos.VirtualNetwork;
import org.lingcloud.molva.xmm.pojos.VirtualNode;
import org.lingcloud.molva.xmm.services.PartitionManager;
import org.lingcloud.molva.xmm.services.VirtualNetworkManager;
import org.lingcloud.molva.xmm.util.XMMConstants;
import org.lingcloud.molva.xmm.util.XMMUtil;
import org.lingcloud.molva.xmm.util.XmlUtil;

import org.lingcloud.molva.xmm.vam.pojos.VAFile;
import org.lingcloud.molva.xmm.vam.pojos.VirtualAppliance;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-7-22<br>
 * @author Xiaoyi Lu<br>
 */

public class VirtualClusterAMM implements AssetMatchMaker {

	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(VirtualClusterAMM.class);

	/**
	 * Used to handle error. It should be isolated with other instance.
	 */
	private HashMap<String, String> lastMatchedAssetsIdAndTypeMap = 
		new HashMap<String, String>();

	public Lease assetMatchMaking(Lease lease) throws Exception {
		if (lease == null) {
			return null;
		}

		// XXX Some important notices like follows:
		// 1st, check which partition (leaser) will be used to allocate virtual
		// cluster. in the different partition, it will adopt different policies
		// to create assets.
		//
		// 2nd, check virtual network is or not null; if vn is null, then create
		// one for the cluster.
		//
		// 3rd, if the node is vm type, then we must cteate its info including
		// all configuration information according to user requirements in
		// database, but not really provision, left the provision operation in
		// the ready stage to active stage. the vmnode should set the state to
		// reserve.
		//
		// 4th, if the node is pm type, we always need to create virtual network
		// for it, but it also can use the exist virtual network.
		//
		// 5th, if the virtual network is created automatically, then we should
		// destroy it after the cluster destroyed. Here is very Important.
		//
		// 6th, the vc.getNodeRequirements() will contain all requirements of
		// all nodes, and the special case is when the key is
		// 'VirtualClusterManager.ALL_NODE_SAME_REQUIREMENT_TAG', then it means
		// all nodes have the same requirements.
		//
		// Xiaoyi Lu marked at 2010-07-23.

		log.info("VirtualCluster (" + lease.getName()
				+ ") is doing asset match making.");
		VirtualCluster vc = new VirtualCluster(lease);
		Partition par = PartitionManager.getInstance()
				.view(vc.getPartitionId());

		String nodeType = null;
		VirtualCluster result = null;
		if (par.getAssetController().equals(PartitionAC.class.getName())) {
			nodeType = par.getAttributes().get(
					PartitionAC.REQUIRED_ATTR_NODETYPE);
			if (PartitionAC.VM.equals(nodeType)) {
				result = this.configVC4VM(vc);
			} else {
				result = this.configVC4PM(vc);
			}
		} else {
			// nothing to do now.
			return null;
		}
		HashMap<String, String> massets = result.getAssetIdAndTypeMap();
		log.info("VirtualCluster (" + lease.getName() + lease.getGuid()
				+ ") in the partition (" + par.getName() + par.getGuid()
				+ ") matches " + massets.size() + " assets successfully.");
		return result;
	}

	private VirtualCluster configVC4PM(VirtualCluster vc) throws Exception {
		String vnid = vc.getVirtualNetworkId();
		if (vnid != null && !"".equals(vnid)) {
			return this.matchPMNodesByExistVN(vc);
		} else {
			return this.matchNodesByCreateVN(vc, false);
		}
	}

	private VirtualCluster matchNodesByCreateVN(VirtualCluster vc,
			boolean isForVM) throws Exception {
		VirtualCluster clone = vc.clone();
		HashMap<String, NodeRequirement> nrmap = clone.getNodeRequirements();
		int pubipNum = 0;
		String headNodeIp = null, lastPriIpNeedPubIp = null;
		List<String> otherNodeIps = new ArrayList<String>();

		Iterator<String> it = nrmap.keySet().iterator();
		@SuppressWarnings("unchecked")
		HashMap<String, NodeRequirement> newnrmap = (HashMap<String, 
				NodeRequirement>) nrmap.clone();
		while (it.hasNext()) {
			String ip = (String) it.next();
			// A bug due to fail-fast iterator. When iterator all elements in
			// the map, we can't remove any element in the map.
			// NodeRequirement nr = nrmap.remove(ip);
			NodeRequirement nr = nrmap.get(ip);

			// we can not make sure the nrmap key is a proper ip. if not a valid
			// ip, we will find a proper one for it.
			if (isForVM) {
				// A bug, due to the key (ip) is changed here, so the nrmap'size
				// is also changed, then in the next loop, the iterator also
				// will throw java.util.ConcurrentModificationException.
				// so we introduce a new hashmap newnrmap to save the new keys
				// and values.

				// another problem of VirtualNetworkManager.findAvailableIp4VM()
				// method, this method will return an available ip, but it
				// doesn't trigger ip pool to save this ip, so in the next loop,
				// this method will return the same ip address. so as the
				// findAvailableIp4PM method. FIXME now we only filter the valid
				// ip, left other ip allocation to virtual network manager.
				// ip = VirtualNetworkManager.findAvailableIp4VM();
				// XXX but there is still a bug, the virtual network manager
				// allocate pub ip resources by their policy, it donot according
				// to
				// the user requirements. need to be improved. 2 method to do
				// this:
				// 1 is transfer the pub_ip detail requirement(which private ip
				// will
				// have a pub ip) to the vnmanager; 2nd, get the pub ips, and
				// assign
				// them to vm by amm itself.
				if (!XMMUtil
						.ipIsValid(ip, VirtualNetworkManager.getValidIpSec())) {
					if (nr.isNeedPublicIP()) {
						pubipNum++;
					}
					continue;
				} else {
					if (nr.isNeedPublicIP()) {
						pubipNum++;
						lastPriIpNeedPubIp = ip;
					}
				}
			} else if (!isForVM) {
				// ip = VirtualNetworkManager.findAvailableIp4PM(partitionId);
				if (!XMMUtil.ipIsValid(ip)) {
					if (nr.isNeedPublicIP()) {
						pubipNum++;
					}
					continue;
				} else {
					if (nr.isNeedPublicIP()) {
						pubipNum++;
						lastPriIpNeedPubIp = ip;
					}
				}
			}

			// bug fixed here, due to public ip resources are managed by the
			// system automatically, so the user can not assign a correct ip
			// address, they only can indicate their desire of pub ip. marked by
			// Xiaoyi Lu at 2010-08-05

			if (nr.isHeadNode()) {
				if (headNodeIp == null) {
					headNodeIp = ip;
				} else {
					// too many head node requirements.
					nr.setHeadNode(false);
					otherNodeIps.add(ip);
				}
			} else {
				otherNodeIps.add(ip);
			}
			newnrmap.put(ip, nr);
		}

		// no nr indicates itself as head node, then choose one who has the pub
		// ip.
		
		if (headNodeIp == null && otherNodeIps.size() > 0) {
			if (lastPriIpNeedPubIp != null) {
				NodeRequirement nr = newnrmap.get(lastPriIpNeedPubIp);
				headNodeIp = nr.getPrivateIP();
				nr.setHeadNode(true);
				newnrmap.put(lastPriIpNeedPubIp, nr);
				for (int i = 0; i < otherNodeIps.size(); i++) {
					String oip = otherNodeIps.get(i);
					if (oip.equals(headNodeIp)) {
						otherNodeIps.remove(i);
						break;
					}
				}
			} else {
				// no nr indicates itself has a pub ip, then we choose the first
				// one.
				headNodeIp = otherNodeIps.remove(0);
				NodeRequirement nr = newnrmap.get(headNodeIp);
				nr.setHeadNode(true);
				newnrmap.put(headNodeIp, nr);
			}
		}

		clone.setNodeRequirements(newnrmap);
		// advanced usage
		VirtualNetwork vn = createVirtualNetwork(clone, pubipNum, headNodeIp, 
				otherNodeIps);
		clone.setVirtualNetworkId(vn.getGuid());

		HashMap<String, String> itmap = new HashMap<String, String>();
		itmap.put(vn.getGuid(), VirtualNetwork.class.getName());
		// FIXME due to the exception will be thrown in any time, so we should
		// handle the transaction roll back.
		lastMatchedAssetsIdAndTypeMap.put(vn.getGuid(),
				VirtualNetwork.class.getName());
		if (isForVM) {
			// XXX left the error to error handler.
			itmap.putAll(this.matchVMNodesByVNAndRequirements(vn, newnrmap));
		} else {
			itmap.putAll(this.matchPMNodesByPrivateIpNics(vn));
		}
		clone.setNodeIdsAndTypes(itmap);
		return clone;
	}

	private VirtualNetwork createVirtualNetwork(VirtualCluster vc, 
			int pubipNum, String headNodeIp, 
			List<String> otherNodeIps) throws Exception {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put(VirtualNetwork.AUTO_CREATE_TAG, "" + true);
		attributes.put(VirtualNetwork.VN_RESERVE_NODE_TAG, "" + false);
		attributes.put(VirtualNetworkAC.OPTIONAL_ATTR_PUBLICIP_NUM, ""
				+ pubipNum);

		String partitionId = vc.getPartitionId();
		String name = vc.getName() + "_VN";
		String vnController = VirtualNetworkAC.class.getName();
		int netSize = vc.getNodeRequirements().size();
		String desc = "Automatically allocate virtual network for the "
			+ "virtual cluster " + vc.getName() + " in the partition " 
			+ partitionId;
		String[] otherips = new String[] {};
		if (otherNodeIps != null && !otherNodeIps.isEmpty()) {
			otherips = otherNodeIps.toArray(new String[otherNodeIps.size()]);
		}
		
		VirtualNetwork vn = VirtualNetworkManager.getInstance()
				.createVirtualNetwork(partitionId, name, vnController, netSize,
						headNodeIp, otherips, attributes, desc);
		return vn;
	}

	private VirtualCluster matchPMNodesByExistVN(VirtualCluster vc)
			throws Exception {
		// TODO simple solusion now, we only get the pn node info in virtual
		// network. In the future, we may need to do some deeply matching by the
		// users' requirements. For example, we may need to support change the
		// physial nodes' appliances.
		VirtualCluster clone = vc.clone();
		VirtualNetwork vn = VirtualNetworkManager.getInstance().view(
				clone.getVirtualNetworkId());
		HashMap<String, String> itmap = this.matchPMNodesByPrivateIpNics(vn);
		if (itmap != null && !itmap.isEmpty()) {
			clone.setNodeIdsAndTypes(itmap);
			// Bug fixed at 2010-12-14. Due to we support virtual network to
			// reserve physical node, so we must set the lease id for every
			// physical node here.
			if (vn.isNeedToReserveNode() && !vn.isAutoCreate()) {
				Iterator<String> it = itmap.keySet().iterator();
				while (it.hasNext()) {
					String pnid = (String) it.next();
					PhysicalNode pn = PartitionManager.getInstance()
							.viewPhysicalNode(pnid);
					pn.setAssetState(AssetConstants.AssetState.RESERVED);
					pn.setLeaseId(vc.getGuid());
					PartitionManager.getInstance().updatePhysicalNodeInfo(
							pn.getGuid(), pn);
				}
			}
		}
		return clone;
	}

	private HashMap<String, String> matchPMNodesByPrivateIpNics(
			VirtualNetwork vn) throws Exception {
		List<Nic> nics = vn.getPrivateIpNics();
		HashMap<String, String> itmap = new HashMap<String, String>();
		String[] fields = new String[] { "name" };
		String[] opers = new String[] { "=" };
		String[] values = new String[1];
				
		for (int i = 0; i < nics.size(); i++) {
			Nic nic = (Nic) nics.get(i);
			String ip = nic.getIp();
			values[0] = ip;
			List<PhysicalNode> pnlist = PartitionManager.getInstance()
					.searchPhysicalNode(fields, opers, values);
			PhysicalNode pn = pnlist.get(0);
			
			// left the state check to the doReservation and preprocessing.
			itmap.put(pn.getGuid(), PhysicalNode.class.getName());

		}
		
		return itmap;
	}

	private VirtualCluster configVC4VM(VirtualCluster vc) throws Exception {
		String vnid = vc.getVirtualNetworkId();
		if (vnid != null && !"".equals(vnid)) {
			return this.matchVMNodesByExistVN(vc);
		} else {
			return this.matchNodesByCreateVN(vc, true);
		}
	}

	private VirtualCluster matchVMNodesByExistVN(VirtualCluster vc)
			throws Exception {
		// FIXME two important notices.
		// 1st, we create the vm node info of virtual network in data base
		// according to the requirements, but not really deploy the vm.

		// 2nd, the requirements have two cases. one is all requirements are the
		// same, the other is detail requirements.

		// 3rd, if the requirement map has the smaller size than the network
		// size, it means the user specify some nodes, but other use default
		// value. This option is supported in client. The app or program can use
		// default value to create NodeRequirement.

		VirtualCluster clone = vc.clone();
		VirtualNetwork vn = VirtualNetworkManager.getInstance().view(
				clone.getVirtualNetworkId());
		HashMap<String, String> itmap = this.matchVMNodesByVNAndRequirements(
				vn, vc.getNodeRequirements());
		clone.setNodeIdsAndTypes(itmap);
		return clone;
	}

	private List<String> checkVNCAccessWay(List<String> accessWay) {
		boolean isNeedToAddVNC = true;
		if (accessWay != null && !accessWay.isEmpty()) {
			for (int k = 0; k < accessWay.size(); k++) {
				if (VAMConstants.VA_ACCESS_WAY_VNC.equals(accessWay.get(k))) {
					isNeedToAddVNC = false;
					break;
				}
			}
		}
		if (isNeedToAddVNC) {
			accessWay.add(VAMConstants.VA_ACCESS_WAY_VNC);
		}
		return accessWay;
	}

	private HashMap<String, String> createNodesBySameRequirement(
			VirtualNetwork vn, HashMap<String, NodeRequirement> nrmap) 
			throws Exception {
		HashMap<String, String> itmap = new HashMap<String, String>();
		
		NodeRequirement nr = nrmap.get(XMMConstants.
				ALL_NODE_SAME_REQUIREMENT_TAG);
		String partitionId = nr.getPartitionId();
		boolean isDeploy = false;
		String vnController = VirtualNodeAC.class.getName();
		
		String applianceId = nr.getVirtualApplicanceID();
		
		VirtualAppliance va = VAMUtil.getVAManager().queryAppliance(
				applianceId);
		if (va == null) {
			throw new Exception("The required virtual appliance ("
					+ applianceId + ") is not exist.");
		}
		VirtualAppliance snapshot = VAMUtil.getVAManager()
				.allocateAppliance(va.getGuid(), null, null);
		if (snapshot == null) {
			throw new Exception("The snapshot of virtual appliance ("
					+ va.getName() + ") is created failed.");
		}
		itmap.put(snapshot.getGuid(), VirtualAppliance.class.getName());
		lastMatchedAssetsIdAndTypeMap.put(snapshot.getGuid(),
				VirtualAppliance.class.getName());
		String vaFileNames = this.getVAFileNames(snapshot);
		String loader = snapshot.getBootLoader();
		List<String> accessWay = snapshot.getAccessWay();
		
		// advanced usage.
		// the following attributes are required.
		HashMap<String, String> attributes = new HashMap<String, String>();
		// attributes.put(VirtualNode.IS_HEAD_NODE, "" + nr.isHeadNode());
		attributes.put(VirtualNode.CPUNUM, "" + nr.getCpuNum());
		attributes.put(VirtualNode.MEMSIZE, "" + nr.getMemorySize());
		attributes.put(VirtualNode.APPLIANCE_FILE_NAMES, vaFileNames);
		attributes.put(VirtualNode.BOOTLOADER, loader);
		// XXX the head node can support vnc login always. move this line
		// backward.
		// attributes.put(VirtualNode.ACCESSWAY, XmlUtil.toXml(accessWay));
		attributes.put(VirtualNode.APPLIANCE_NAME, snapshot.getVAName());
		
		List<Nic> pips = vn.getPrivateIpNics();
		HashMap<String, Nic> puips = vn.getPublicIpNics();
		for (int i = 0; i < pips.size(); i++) {
			Nic privateIpNic = pips.get(i);
			String privateIp = privateIpNic.getIp();
			Nic publicIpNic = puips.get(privateIp);
			String publicIp = null;
			if (publicIpNic != null) {
				publicIp = publicIpNic.getIp();
			}
			String desc = privateIp + " Virtual Machine";
			if (privateIp.equals(vn.getHeadNodeIp())) {
				// the trustable level of virtual network is bigger than nr.
				attributes.put(VirtualNode.IS_HEAD_NODE, "" + true);
				accessWay = this.checkVNCAccessWay(accessWay);
			} else {
				attributes.put(VirtualNode.IS_HEAD_NODE, "" + false);
			}
		
			List<Nic> nodeNics = new ArrayList<Nic>();
			nodeNics.add(privateIpNic);
			if (publicIpNic != null) {
				nodeNics.add(publicIpNic);
			}
			attributes.put(VirtualNode.NICS, XmlUtil.toXml(nodeNics));
			attributes.put(VirtualNode.ACCESSWAY, XmlUtil.toXml(accessWay));
		
			// XXX added at 2010-12-15 for vm deploy.
			attributes.put(VirtualNode.VM_DEPLOY_POLICER,
					nr.getVmDeployPolicer());
			VirtualMachineDeployPolicier vmdp = 
				(VirtualMachineDeployPolicier) Class
					.forName(nr.getVmDeployPolicer()).newInstance();
			HashMap<String, Vector<String>> deployResult = vmdp
					.generateDeployPolicy(vn.getPartitionId(), privateIp,
							nr);
			if (deployResult != null) {
				// if deployResult == null, then we donot consider any
				// deploy schedule policy.
				attributes.put(VirtualNode.VM_DEPLOY_SCHEDULE_RESULT,
						XmlUtil.toXml(deployResult));
			}
		
			try {
				VirtualNode vnode = PartitionManager.getInstance()
						.addVirtualNode(partitionId, privateIp, publicIp,
								vnController, applianceId, isDeploy,
								attributes, desc);
				itmap.put(vnode.getGuid(), VirtualNode.class.getName());
				lastMatchedAssetsIdAndTypeMap.put(vnode.getGuid(),
						VirtualNode.class.getName());
			} catch (Exception e) {
				String msg = "Error occurred when create a virtual node"
					+ " info (" + privateIp + "), due to " + e.toString();
				log.error(msg);
				// XXX May cause garbage, we need to garbage collection.
				// left garbage collection to errorHandler.
			}
		}
		
		return itmap;
	}
	
	private HashMap<String, String> createNodesByDifferentRequirements(
			VirtualNetwork vn, HashMap<String, NodeRequirement> nrmap) 
			throws Exception {
		HashMap<String, String> itmap = new HashMap<String, String>();
		
		List<Nic> pips = vn.getPrivateIpNics();
		HashMap<String, Nic> pubnics = vn.getPublicIpNics();
		String partitionId = vn.getPartitionId();
		int tag = pips.size();
		for (int i = 0; i < pips.size(); i++) {
			Nic privateIpNic = pips.get(i);
			String privateIp = privateIpNic.getIp();
			if (!nrmap.containsKey(privateIp) && i < tag) {
				// the ip in virtual network may not be equals elements in
				// the nrmap, due to portal input difference and virtual
				// network manager try-best to allocate ip addresses.
				// handle user apply ip firstly.
				pips.add(privateIpNic);
				continue;
			}
			NodeRequirement nr = null;
			if (i >= tag) {
				Iterator<String> it = nrmap.keySet().iterator();
				while (it.hasNext()) {
					String key = (String) it.next();
					if (privateIp.equals(key)) {
						continue;
					}
					nr = nrmap.get(key);
					it.remove();
					nr.setPrivateIP(privateIp);
					// A bug, don't put the privateip as key again, then in
					// the next loop, the previous ip will be overwritten.
					break;
				}
			} else {
				nr = nrmap.get(privateIp);
			}

			Nic publicIpNic = pubnics.get(privateIp);
			String publicIp = null;
			if (publicIpNic != null) {
				publicIp = publicIpNic.getIp();
				nr.setNeedPublicIP(true);
			}
			String vnController = VirtualNodeAC.class.getName();
			boolean isDeploy = false;

			String applianceId = nr.getVirtualApplicanceID();
			VirtualAppliance va = VAMUtil.getVAManager().queryAppliance(
					applianceId);

			if (va == null) {
				throw new Exception("The required virtual appliance ("
						+ applianceId + ") is not exist.");
			}
			VirtualAppliance snapshot = VAMUtil.getVAManager()
					.allocateAppliance(va.getGuid(), null, null);
			if (snapshot == null) {
				throw new Exception("The snapshot of virtual appliance ("
						+ va.getName() + ") is created failed.");
			}
			itmap.put(snapshot.getGuid(), VirtualAppliance.class.getName());
			lastMatchedAssetsIdAndTypeMap.put(snapshot.getGuid(),
					VirtualAppliance.class.getName());
			String vaFileNames = this.getVAFileNames(snapshot);
			String loader = snapshot.getBootLoader();
			List<String> accessWay = snapshot.getAccessWay();
			// advanced usage.
			// the following attributes are required.
			HashMap<String, String> attributes = 
				new HashMap<String, String>();
			if (privateIp.equals(vn.getHeadNodeIp())) {
				// attributes.put(VirtualNode.IS_HEAD_NODE, "" +
				// nr.isHeadNode());
				// the trustable level of virtual network is bigger than nr.
				attributes.put(VirtualNode.IS_HEAD_NODE, "" + true);
				accessWay = this.checkVNCAccessWay(accessWay);
			} else {
				attributes.put(VirtualNode.IS_HEAD_NODE, "" + false);
			}
			attributes.put(VirtualNode.CPUNUM, "" + nr.getCpuNum());
			attributes.put(VirtualNode.MEMSIZE, "" + nr.getMemorySize());
			attributes.put(VirtualNode.APPLIANCE_FILE_NAMES, vaFileNames);
			attributes.put(VirtualNode.BOOTLOADER, loader);
			attributes.put(VirtualNode.ACCESSWAY, XmlUtil.toXml(accessWay));
			attributes
					.put(VirtualNode.APPLIANCE_NAME, snapshot.getVAName());
			List<Nic> nodeNics = new ArrayList<Nic>();
			nodeNics.add(privateIpNic);
			if (publicIpNic != null) {
				nodeNics.add(publicIpNic);
			}
			attributes.put(VirtualNode.NICS, XmlUtil.toXml(nodeNics));

			// XXX added at 2010-12-15 for vm deploy.
			attributes.put(VirtualNode.VM_DEPLOY_POLICER,
					nr.getVmDeployPolicer());
			VirtualMachineDeployPolicier vmdp = 
				(VirtualMachineDeployPolicier) Class
					.forName(nr.getVmDeployPolicer()).newInstance();
			HashMap<String, Vector<String>> deployResult = vmdp
					.generateDeployPolicy(vn.getPartitionId(), privateIp,
							nr);
			if (deployResult != null) {
				// if deployResult == null, then we donot consider any
				// deploy schedule policy.
				attributes.put(VirtualNode.VM_DEPLOY_SCHEDULE_RESULT,
						XmlUtil.toXml(deployResult));
			}

			String desc = nr.getPrivateIP() + " Virtual Machine";
			try {
				VirtualNode vnode = PartitionManager.getInstance()
						.addVirtualNode(partitionId, privateIp, publicIp,
								vnController, applianceId, isDeploy,
								attributes, desc);
				itmap.put(vnode.getGuid(), VirtualNode.class.getName());
				lastMatchedAssetsIdAndTypeMap.put(vnode.getGuid(),
						VirtualNode.class.getName());
			} catch (Exception e) {
				String msg = "Error occurred when create a virtual node"
					+ " info (" + privateIp + "), due to " + e.toString();
				log.error(msg);
				throw new Exception(msg);
				// // XXX May cause garbage, we need to garbage collection.
				// left garbage collection to errorHandler.
			}
		}
		
		return itmap;
	}
	
	private HashMap<String, String> matchVMNodesByVNAndRequirements(
			VirtualNetwork vn, HashMap<String, NodeRequirement> oldnrmap)
			throws Exception {
		// FIXME all the network info are according to virtual network obejct.
		@SuppressWarnings("unchecked")
		HashMap<String, NodeRequirement> nrmap = 
			(HashMap<String, NodeRequirement>) oldnrmap.clone();
		HashMap<String, String> itmap = new HashMap<String, String>();
		if (nrmap.containsKey(XMMConstants.ALL_NODE_SAME_REQUIREMENT_TAG)) {
			itmap = createNodesBySameRequirement(vn, nrmap);
		} else {
			itmap = createNodesByDifferentRequirements(vn, nrmap);
		}
		return itmap;
	}

	private String getVAFileNames(VirtualAppliance va) throws Exception {
		StringBuilder sb = new StringBuilder();
		List<String> disks = va.getDisks();
		List<String> fileNames = new ArrayList<String>();
		for (int i = 0; i < disks.size(); i++) {
			VAFile vafile = VAMUtil.getVAManager().queryFile(disks.get(i));
			if (vafile == null) {
				throw new Exception("The file object of virtual appliance ("
						+ va.getName() + " " + disks.get(i)
						+ ") is not found, please change a virtual appliance.");
			}
			// fileNames.add(vafile.getLocation());
			fileNames.add(vafile.getSavePath());
		}

		for (int i = 0; i < fileNames.size(); i++) {
			sb.append(fileNames.get(i));
			if (i < fileNames.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	private boolean garbageVirtualNodeClear(String nodeid) {
		if (nodeid == null || "".equals(nodeid)) {
			return false;
		} else {
			log.warn("due to error, so clear garbage node " + nodeid);
			try {
				PartitionManager.getInstance().removeVirtualNode(nodeid, false);
				log.warn("Clear success for the garbage node " + nodeid);
				return true;
			} catch (Throwable t) {
				log.fatal("Clear failed for the garbage node " + nodeid
						+ ", due to " + t.toString()
						+ " need to handle it by manual.");
			}
		}
		return false;
	}

	public void validateMachedAssets(Lease lease) throws Exception {
		HashMap<String, String> matchedAssets = lease.getAssetIdAndTypeMap();
		if (matchedAssets == null || matchedAssets.size() == 0) {
			String msg = "No matched assets for the VirtualCluster ("
					+ lease.getGuid() + lease.getName() + ").";
			log.error(msg);
			throw new Exception(msg);
		}
		if (!matchedAssets.containsValue(VirtualNetwork.class.getName())) {
			String msg = "Matched assets in the virtual cluster ("
					+ lease.getGuid() + lease.getName()
					+ ") should have one asset with virtual network type.";
			throw new Exception(msg);
		}
		if (!matchedAssets.containsValue(PhysicalNode.class.getName())
				&& !matchedAssets.containsValue(VirtualNode.class.getName())) {
			String msg = "Matched assets in the virtual cluster ("
					+ lease.getGuid() + lease.getName()
					+ ") should have assets with node type.";
			throw new Exception(msg);
		}

		// TODO many other validation can be done here. Some notice as follows:
		//
		// 1st, cluster in different partition should have different kinds of
		// node types;

		// 2nd, the matched nodes should have the same size with the virtual
		// network size.

		// 3rd, i think the validate policy should be adopted. In many case, we
		// no need to throw exceptions.

		// Xiaoyi Lu marked at 2010-07-24.
	}

	public Lease matchErrorHandler(Lease lease) {
		// When error occurred in the validateMachedAssets, this method will be
		// invoked to clear garbage.

		// Inorder to try best to clear garbage information, so ignore all
		// errors when clear garbages. Only log them.
		if (lease == null) {
			return null;
		}

		VirtualCluster vc = new VirtualCluster(lease);
		HashMap<String, String> itmap = vc.getAssetIdAndTypeMap();
		HashMap<String, String> itmapbak = new HashMap<String, String>(itmap);
		if (itmap != null && !itmap.isEmpty()) {
			Iterator<String> it = itmap.keySet().iterator();
			log.warn("due to system error or termination operation"
					+ ", it needs to clear all matched assets "
					+ "for the virtual cluster " + vc.getName());
			while (it.hasNext()) {
				String assetid = (String) it.next();
				String value = itmap.get(assetid);
				if (value.equals(VirtualNetwork.class.getName())) {
					if (this.garbageNetworkClear(assetid)) {
						itmapbak.remove(assetid);
					}
				} else if (value.equals(VirtualNode.class.getName())) {
					if (this.garbageVirtualNodeClear(assetid)) {
						itmapbak.remove(assetid);
					}
				} else if (value.equals(VirtualAppliance.class.getName())) {
					if (this.garbageApplianceClear(assetid)) {
						itmapbak.remove(assetid);
					}
				} else if (value.equals(PhysicalNode.class.getName())) {
					if (this.garbagePhysicalNodeClear(assetid)) {
						itmapbak.remove(assetid);
					}
				} else {
					log.warn("Unsupported asset type " + value
							+ " to roll back.");
				}
			}
		}

		// Important notice: the above cases are only work for the termination
		// operation, because when the error thrown, the above information may
		// be lost, so we need to use lastMatchedAssetsIdAndTypeMap to clear
		// again.
		if (lastMatchedAssetsIdAndTypeMap != null
				&& !lastMatchedAssetsIdAndTypeMap.isEmpty()) {
			Iterator<String> it = lastMatchedAssetsIdAndTypeMap.keySet()
				.iterator();
			log.warn("-------------------------"
					+ "Roll Back For Asset Match Making"
					+ "--------------------------------");
			while (it.hasNext()) {
				String assetid = (String) it.next();
				String value = lastMatchedAssetsIdAndTypeMap.get(assetid);
				if (value.equals(VirtualNetwork.class.getName())) {
					if (this.garbageNetworkClear(assetid)) {
						itmapbak.remove(assetid);
					}
				} else if (value.equals(VirtualNode.class.getName())) {
					if (this.garbageVirtualNodeClear(assetid)) {
						itmapbak.remove(assetid);
					}
				} else if (value.equals(VirtualAppliance.class.getName())) {
					if (this.garbageApplianceClear(assetid)) {
						itmapbak.remove(assetid);
					}
				} else if (value.equals(PhysicalNode.class.getName())) {
					if (this.garbagePhysicalNodeClear(assetid)) {
						itmapbak.remove(assetid);
					}
				} else {
					log.warn("Unsupported asset type " + value
							+ " to roll back.");
				}
			}
		}
		vc.setAssetIdAndTypeMap(itmapbak);
		return vc;
	}

	private boolean garbagePhysicalNodeClear(String assetid) {
		// TODO later.
		return true;
	}

	private boolean garbageApplianceClear(String assetid) {
		if (assetid == null || "".equals(assetid)) {
			return false;
		} else {
			log.warn("due to error, so clear garbage appliance " + assetid);
			try {
				VAMUtil.getVAManager().removeAppliance(assetid);
				log.warn("Clear success for the garbage appliance " + assetid);
				return true;
			} catch (Throwable t) {
				log.fatal("Clear failed for the garbage appliance " + assetid
						+ ", due to " + t.toString()
						+ " need to handle it by manual.");
			}
		}
		return false;
	}

	private boolean garbageNetworkClear(String assetid) {
		if (assetid == null || "".equals(assetid)) {
			return false;
		}
		VirtualNetwork removedvn = null;
		try {
			removedvn = VirtualNetworkManager.getInstance().view(assetid);
		} catch (Exception e) {
			String msg = "Error occurred when view virtual network " + assetid
					+ " for asset match making roll back. Due to "
					+ e.toString();
			log.error(msg);
		}
		if (removedvn != null
				&& removedvn.isAutoCreate()
				&& (removedvn.getClusterID() == null || "".equals(removedvn
						.getClusterID()))) {
			try {
				VirtualNetworkManager.getInstance().destroyVirtualNetwork(
						removedvn.getGuid());
				return true;
			} catch (Exception e) {
				String msg = "Error occurred when clear virtual network "
						+ removedvn.getGuid()
						+ " for asset match making roll back. Due to "
						+ e.toString();
				log.error(msg);
			}
		}
		return false;
	}

	public void handleTermination(Lease lease) {
		this.matchErrorHandler(lease);
	}

	// XXX added "satiableDecision" function in asset match maker at 2010-12-09
	// to decide the lease whether or not can be satiable.
	public void satiableDecision(Lease lease) throws Exception {
		if (lease == null) {
			throw new Exception("The input parameter lease is null.");
		}
		log.info("VirtualCluster (" + lease.getName()
				+ ") is doing satiableDecision.");
		VirtualCluster vc = new VirtualCluster(lease);
		Partition par = PartitionManager.getInstance()
				.view(vc.getPartitionId());
		String nodeType = null;
		if (par.getAssetController().equals(PartitionAC.class.getName())) {
			nodeType = par.getAttributes().get(
					PartitionAC.REQUIRED_ATTR_NODETYPE);
			if (PartitionAC.VM.equals(nodeType)) {
				this.checkSatiable4VMVC(vc);
			} else {
				// TODO
				log.debug("Nothing to do now for the physical node typed"
						+ " virtual cluster.");
			}
		}
		log.info("The lease of VirtualCluster (" + lease.getName()
				+ lease.getGuid() + ") can be satisfied in the partition ("
				+ par.getName() + par.getGuid() + ").");
		return;
	}

	private void checkSatiable4VMVC(VirtualCluster vc) throws Exception {
		// Notice
		// 1st> check satiable of the total vcpu number of all nodes;
		// 2nd> check satiable of the total memory of all nodes;
		// TODO
		// 3rd> check satiable of disk space and net bandwidth.
		String vnid = vc.getVirtualNetworkId();
		try {
			if (vnid == null || "".equals(vnid)) {
				checkSatiable4VMVCByCreateVN(vc);
			} else {
				checkSatiable4VMVCByExistVN(vc);
			}
		} catch (Exception e) {
			log.error("Can not satisfy the virtual cluster " + vc.getName()
					+ "'s reqirement, due to " + e.toString());
			throw e;
		}

	}

	private void checkSatiable4VMVCByCreateVN(VirtualCluster vc)
			throws Exception {
		HashMap<String, NodeRequirement> nrmap = vc.getNodeRequirements();
		if (nrmap == null) {
			throw new Exception("Both the virtual network id and the node"
					+ " requirement are null");
		} else if (nrmap.size() < 1
				|| (nrmap.size() == 1 && nrmap.containsKey(
						XMMConstants.ALL_NODE_SAME_REQUIREMENT_TAG))) {
			throw new Exception("When the virtual network id is null, "
					+ "the node requirements should have detail information.");
		}
		Iterator<NodeRequirement> iterator = nrmap.values().iterator();
		int totalCpu = 0, totalMem = 0;
		while (iterator.hasNext()) {
			NodeRequirement nr = iterator.next();
			totalCpu += nr.getCpuNum();
			totalMem += nr.getMemorySize();
		}
		this.checkSatiable4Resource(vc.getPartitionId(), totalCpu, totalMem);
	}

	private void checkSatiable4VMVCByExistVN(VirtualCluster vc)
			throws Exception {
		VirtualNetwork vn = VirtualNetworkManager.getInstance().view(
				vc.getVirtualNetworkId());
		HashMap<String, NodeRequirement> nrmap = 
			(HashMap<String, NodeRequirement>) vc.getNodeRequirements();
		if (nrmap.containsKey(XMMConstants.ALL_NODE_SAME_REQUIREMENT_TAG)) {
			NodeRequirement nr = nrmap
					.get(XMMConstants.ALL_NODE_SAME_REQUIREMENT_TAG);
			int cpuNum = nr.getCpuNum();
			int memSize = nr.getMemorySize();
			int nodeNum = vn.getNetworkSize();
			this.checkSatiable4Resource(vc.getPartitionId(), nodeNum * cpuNum,
					nodeNum * memSize);
		} else {
			if (nrmap.size() != vn.getNetworkSize()) {
				throw new Exception("Not all virtual nodes are configured.");
			}
			Iterator<NodeRequirement> iterator = nrmap.values().iterator();
			int totalCpu = 0, totalMem = 0;
			while (iterator.hasNext()) {
				NodeRequirement nr = iterator.next();
				totalCpu += nr.getCpuNum();
				totalMem += nr.getMemorySize();
			}
			this.checkSatiable4Resource(vc.getPartitionId(), totalCpu, 
					totalMem);
		}
	}

	private void checkSatiable4Resource(String parid, int totalCpu, 
			int totalMem) throws Exception {
		List<PhysicalNode> pnlist = PartitionManager.getInstance()
				.listPhysicalNode(parid);
		if (pnlist == null || pnlist.isEmpty()) {
			throw new Exception("No physical node in this partition " + parid);
		}
		int totalFreeCpu = 0, totalFreeMemory = 0;
		for (int i = 0; i < pnlist.size(); i++) {
			PhysicalNode pn = pnlist.get(i);
			totalFreeCpu += pn.getFreeCpu();
			totalFreeMemory += pn.getFreeMemory();
		}
		if ((totalFreeCpu < totalCpu) || (totalFreeMemory < totalMem)) {
			throw new Exception("No enough resource in the partition " + parid
					+ " is left to satisfy the requirement.");
		}
	}
}
