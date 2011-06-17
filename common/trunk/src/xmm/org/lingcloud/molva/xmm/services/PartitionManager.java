/*
 *  @(#)PartitionManager.java  2010-5-27
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

package org.lingcloud.molva.xmm.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetManagerImpl;
import org.lingcloud.molva.ocl.poll.AssetPollingTask;
import org.lingcloud.molva.ocl.poll.PollingTaskManager;
import org.lingcloud.molva.xmm.ac.PPNPNController;
import org.lingcloud.molva.xmm.ac.PVNPNController;
import org.lingcloud.molva.xmm.pojos.Nic;
import org.lingcloud.molva.xmm.pojos.Partition;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.pojos.VirtualCluster;
import org.lingcloud.molva.xmm.pojos.VirtualNode;
import org.lingcloud.molva.xmm.util.XMMConstants;
import org.lingcloud.molva.xmm.util.XMMUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-28<br>
 * @author Xiaoyi Lu<br>
 */
public class PartitionManager {
	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(PartitionManager.class);

	private static PartitionManager pm = new PartitionManager();

	static {
		try {
			load();
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}

	private PartitionManager() {

	}

	public static synchronized PartitionManager getInstance() {
		return pm;
	}

	public synchronized Partition createPartition(String name,
			String pController, HashMap<String, String> attributes, String desc)
			throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		// FIXME no need to do so, because the name can not be duplicated in
		// assets.
		Partition par = new Partition();
		par.setName(name);
		par.setAssetController(pController);
		par.setDescription(desc);

		if (attributes != null && !attributes.isEmpty()) {
			par.getAttributes().putAll((attributes));
		}

		Partition newpar = new Partition(ami.add(par, false));
		return newpar;
	}

	public synchronized Partition destroyPartition(String parGuid)
			throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		Partition par = new Partition(ami.view(parGuid));
		
		List<PhysicalNode> pnl = this.listPhysicalNode(par.getGuid());
		if (pnl != null && pnl.size() > 0) {
			throw new Exception(
				"There are physical node exist in the partition ("
					+ par.getName() + "), please destroy them firstly.");
		}
		String[] fields = new String[] { "additionalTerms['"
				+ VirtualCluster.PARTITION_ID + "']" };
		String[] operators = new String[] { "=" };
		Object[] values = new Object[] { parGuid };
		List<VirtualCluster> vcl = VirtualClusterManager.getInstance()
				.searchVirtualCluster(fields, operators, values);
		if (vcl != null && vcl.size() > 0) {
			throw new Exception(
				"There are virtual cluster exist in the partition ("
					+ par.getName() + "), please destroy them firstly.");
		}
		Partition removedPar = new Partition(ami.remove(par.getGuid(), false));
		log.info("One partition (" + removedPar.getName() + " "
				+ removedPar.getGuid() + ")is removed.");
		return removedPar;
	}

	public Partition view(String partitionId) throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		Asset asset = ami.view(partitionId);
		if (asset == null) {
			return null;
		} else {
			return new Partition(asset);
		}
	}

	public PhysicalNode viewPhysicalNode(String guid) throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		Asset asset = ami.view(guid);
		if (asset == null) {
			return null;
		} else {
			return new PhysicalNode(asset);
		}
	}

	public VirtualNode viewVirtualNode(String guid) throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		Asset asset = ami.view(guid);
		if (asset == null) {
			return null;
		} else {
			return new VirtualNode(asset);
		}
	}

	/**
	 * Refresh the physical node.
	 * @param pnid
	 * @return
	 * @throws Exception
	 */
	public synchronized PhysicalNode refreshPhysicalNode(String pnid)
			throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		Asset asset = ami.view(pnid);
		if (asset == null) {
			throw new Exception("The PhysicalNode " + pnid 
					+ " does not exist.");
		}
		PhysicalNode oldPn = new PhysicalNode(asset);
		Asset freshedAsset = null;
		try {
			freshedAsset = ami.refresh(asset);
		} catch (Exception e) {
			log.warn("Refresh physica node " + oldPn.getName()
					+ " failed when deleting it, ignore this error.");
		}
		if (freshedAsset == null) {
			log.error("The physical node " + pnid
					+ " in metainfo system, but not in the reality."
					+ " It will be deleted from metainfo system.");
			ami.remove(pnid, true);
			return null;
		}
		PhysicalNode newPn = new PhysicalNode(freshedAsset);
		if (newPn != null) {
			List<Nic> nicsource = oldPn.getNics();
			List<Nic> nicnew = newPn.getNics();
			if (nicsource != null && nicnew != null
					&& nicsource.size() != nicnew.size()) {
				VirtualNetworkManager.triggerNetworkInfoRemove(nicsource);
				VirtualNetworkManager.triggerNetworkInfoAdd(nicnew);
			}
		} 
//		else {
//			log.error("The physical node " + pnid
//					+ " in metainfo system, but not in the reality."
//					+ " It will be deleted from metainfo system.");
//			ami.remove(pnid, true);
//			return null;
//		}
		return newPn;
	}
	
	private boolean testPhysicalNode(String ip, String type) throws Exception {
		
		try {
			StringBuffer cmdSB = new StringBuffer();
			String cmd = XMMUtil.getTestPhysicalNodeCmdInCfgFile();
			if (cmd == null || "".equals(cmd)) {
				log.error("can't get testPhycialNodeCmd in Cfg file.");
				throw new Exception("can't get testPhycialNodeCmd "
						+ "in Cfg file.");
			}
			cmdSB.append(cmd).append(" " + ip + " " + type);
			String stdout = XMMUtil.runCommand(cmdSB.toString());
			if (stdout.trim().equals("true")) {
				log.info("test phycial node " + ip + " for " + type 
						+ " sucess.");
			} else {
				log.info("test phycial node " + ip + " for " + type 
						+ " Failed: " + stdout);
				throw new Exception("test phycial node " + ip + " for " 
						+ type + " Failed: " + stdout);
			}
		} catch (Exception e) {
			throw e;
		}
		
		return true;
	}

	public synchronized PhysicalNode addPhysicalNode(String partitionId,
			String privateIp, String publicIp, String pnController,
			boolean isRedeploy, HashMap<String, String> attributes, String desc)
			throws Exception {
		Partition par = this.view(partitionId);
		if (par == null) {
			throw new Exception("The partition with id " + partitionId
					+ " is not exist.");
		}

		AssetManagerImpl ami = new AssetManagerImpl();
		// FIXME we use private ip as the name.
		String[] searchConditions = new String[] { "name" };
		String[] operators = new String[] { "=" };
		Object[] values = new String[] { privateIp };
		List<Asset> alist = ami.search(searchConditions, operators, values);
		if (alist != null && !alist.isEmpty()) {
			throw new Exception("The physical node " + privateIp
					+ " exists already.");
		}
		
		// test the physical node
		String testType = "";
		if (pnController.contains(PVNPNController.class.getSimpleName())) {
			testType = "VM";
		} else if (pnController.contains(PPNPNController.class
				.getSimpleName())) {
			testType = "Common";
		}
		this.testPhysicalNode(privateIp, testType);

		PhysicalNode pn = new PhysicalNode();
		// FIXME, notice here, we use privateIp as the name is a trick.
		pn.setName(privateIp);
		pn.setAssetLeaserId(partitionId);
		pn.setPrivateIps(new String[] { privateIp });
		pn.setPublicIps(new String[] { publicIp });
		if (pnController.equals(PPNPNController.class.getName())) {
			pn.setVmProvisonerTag(false);
		} else if (pnController.equals(PVNPNController.class.getName())) {
			pn.setVmProvisonerTag(true);
		}
		pn.setAssetController(pnController);
		// pn.setPrice(price);
		pn.setDescription(desc);
		if (isRedeploy) {
			pn.setRunningStatus(XMMConstants.MachineRunningState.BOOT
					.toString());
		} else {
			pn.setRunningStatus(XMMConstants.MachineRunningState.RUNNING
					.toString());
		}
		if (attributes != null && !attributes.isEmpty()) {
			pn.getAttributes().putAll((attributes));
		}

		pn.setRedeployTagInCreate(isRedeploy);
		Asset addedAsset = ami.add(pn, false);
		if (addedAsset == null) {
			return null;
		}

		PhysicalNode newpn = new PhysicalNode(addedAsset);

		log.info("A new physical node with the privateIp " + privateIp
				+ " is added to the partition sucessfully.");
		return newpn;
	}

	public synchronized PhysicalNode removePhysicalNode(String pnGuid)
			throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		Asset asset = ami.view(pnGuid);
		if (asset == null) {
			return null;
		}
		// // XXX improved to remove physical node when the information is not
		// // consistency.

		PhysicalNode pn = new PhysicalNode(asset);
		// Bug fixed, before we check condition, we should refresh its latest
		// state.
		PhysicalNode refpn = this.refreshPhysicalNode(pn.getGuid());
		if (refpn == null) {
			return pn;
		}
		if (refpn.getRunningVms() > 0) {
			throw new Exception("The physical node (" + pn.getGuid() + " "
					+ pn.getName() + ") still has associated virtual clusters"
					+ ", please destroy them firstly.");
		}
		PhysicalNode repn = new PhysicalNode(ami.remove(pnGuid, false));
		// XXX the mac and ip is not changed easily, so keep them a longer time.
		// VirtualNetworkManager.triggerNetworkInfoRemove(pn.getNics());
		log.info("One PhysicalNode (" + repn.getName() + " " + repn.getGuid()
				+ ")is removed.");
		return repn;
	}

	public List<Partition> listAllPartition() throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		String[] fields = new String[] { "type" };
		String[] operators = new String[] { "=" };
		Object[] values = new Object[] { XMMConstants.PARTITION_TYPE };
		List<Asset> asli = ami.search(fields, operators, values);
		if (asli == null || asli.size() == 0) {
			return new ArrayList<Partition>();
		}
		List<Partition> lp = new ArrayList<Partition>();
		for (int i = 0; i < asli.size(); i++) {
			Partition par = new Partition(asli.get(i));
			lp.add(par);
		}
		return lp;
	}

	public List<Partition> listPartition(String partitionController)
			throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		String[] fields = new String[] { "type", "assetController" };
		String[] operators = new String[] { "=", "=" };
		Object[] values = new Object[] { XMMConstants.PARTITION_TYPE,
				partitionController };
		List<Asset> asli = ami.search(fields, operators, values);
		if (asli == null || asli.size() == 0) {
			return new ArrayList<Partition>();
		}
		List<Partition> lp = new ArrayList<Partition>();
		for (int i = 0; i < asli.size(); i++) {
			Partition par = new Partition(asli.get(i));
			lp.add(par);
		}
		return lp;
	}

	public List<PhysicalNode> listPhysicalNode(String partitionId)
			throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		String[] fields = new String[] { "type", "assetLeaserId" };
		String[] operators = new String[] { "=", "=" };
		Object[] values = new Object[] { XMMConstants.PHYSICAL_NODE_TYPE,
				partitionId };
		List<Asset> asli = ami.search(fields, operators, values);
		if (asli == null || asli.size() == 0) {
			return new ArrayList<PhysicalNode>();
		}
		List<PhysicalNode> lp = new ArrayList<PhysicalNode>();
		for (int i = 0; i < asli.size(); i++) {
			PhysicalNode par = new PhysicalNode(asli.get(i));
			lp.add(par);
		}
		return lp;
	}

	private static void load() {
		try {
			AssetManagerImpl ami = new AssetManagerImpl();
			String[] fields = new String[] { "type" };
			String[] operators = new String[] { "=" };
			Object[] values = new Object[] { XMMConstants.PARTITION_TYPE };
			List<Asset> asli = ami.search(fields, operators, values);
			if (asli == null || asli.size() == 0) {
				return;
			}
			for (int i = 0; i < asli.size(); i++) {
				String parid = asli.get(i).getGuid();
				String[] afields = new String[] { "type", "assetLeaserId" };
				String[] aoperators = new String[] { "=", "=" };
				Object[] avalues = new Object[] {
						XMMConstants.PHYSICAL_NODE_TYPE, parid };
				List<Asset> aseli = ami.search(afields, aoperators, avalues);
				if (aseli == null || aseli.size() == 0) {
					continue;
				}
				for (int j = 0; j < aseli.size(); j++) {
					PhysicalNode pn = new PhysicalNode(aseli.get(j));
					PhysicalNode refreshedPN = null;
					try {
						Asset asset = ami.refresh(pn);
						if (asset == null) {
							ami.remove(pn.getGuid(), true);
							continue;
						}
						refreshedPN = new PhysicalNode(asset);
						log.info("Successfully to load ("
								+ refreshedPN.getName() + ") physical node"
								+ " to the partition " + asli.get(i).getName()
								+ ".");
						VirtualNetworkManager.triggerNetworkInfoAdd(refreshedPN
								.getNics());
						AssetPollingTask apt = new AssetPollingTask(
								refreshedPN.getGuid());
						PollingTaskManager.addPollingTask(apt);
						log.info("The physical node with id "
								+ refreshedPN.getGuid()
								+ " is added to polling task manager.");
					} catch (Exception e) {
						log.error("Ignore an error occured when load a"
								+ " physical node info, due to " 
								+ e.toString());
						continue;
					}
				}
			}
		} catch (Throwable t) {
			log.error(t.toString());
			// Ignore this error.
		}
	}

	public Partition updatePartitionInfo(String guid, Partition par)
			throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		Asset upAsset = ami.update(guid, par);
		if (upAsset == null) {
			return null;
		}
		return new Partition(upAsset);
	}

	public PhysicalNode updatePhysicalNodeInfo(String guid, PhysicalNode pn)
			throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		Asset upAsset = ami.update(guid, pn);
		if (upAsset == null) {
			return null;
		}
		return new PhysicalNode(upAsset);
	}

	public VirtualNode updateVirtualNodeInfo(String guid, VirtualNode vn)
			throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		Asset upAsset = ami.update(guid, vn);
		if (upAsset == null) {
			return null;
		}
		return new VirtualNode(upAsset);
	}

	public List<PhysicalNode> searchPhysicalNode(String[] searchConditions,
			String[] operators, Object[] values) throws Exception {
		List<String> scon = new ArrayList<String>();
		List<String> opers = new ArrayList<String>();
		List<Object> vals = new ArrayList<Object>();
		if (searchConditions != null) {
			for (int i = 0; i < searchConditions.length; i++) {
				if (searchConditions[i].equals("type")) {
					continue;
				}
				scon.add(searchConditions[i]);
				opers.add(operators[i]);
				vals.add(values[i]);
			}
		}

		scon.add("type");
		opers.add("=");
		vals.add(XMMConstants.PHYSICAL_NODE_TYPE);

		AssetManagerImpl ami = new AssetManagerImpl();
		// FIXME we use private ip as the name.
		List<Asset> alist = ami.search(scon.toArray(new String[scon.size()]),
				opers.toArray(new String[scon.size()]), vals.toArray());
		// Bug fixed at 2010-07-31.
		if (alist == null || alist.isEmpty()) {
			return null;
		}
		List<PhysicalNode> pnlist = new ArrayList<PhysicalNode>();
		for (int k = 0; k < alist.size(); k++) {
			PhysicalNode pn = new PhysicalNode(alist.get(k));
			pnlist.add(pn);
		}
		return pnlist;
	}

	public List<VirtualNode> searchVirtualNode(String[] searchConditions,
			String[] operators, Object[] values) throws Exception {
		List<String> scon = new ArrayList<String>();
		List<String> opers = new ArrayList<String>();
		List<Object> vals = new ArrayList<Object>();

		if (searchConditions != null) {
			for (int i = 0; i < searchConditions.length; i++) {
				if (searchConditions[i].equals("type")) {
					continue;
				}
				scon.add(searchConditions[i]);
				opers.add(operators[i]);
				vals.add(values[i]);
			}
		}

		scon.add("type");
		opers.add("=");
		vals.add(XMMConstants.VIRTUAL_NODE_TYPE);

		AssetManagerImpl ami = new AssetManagerImpl();
		List<Asset> alist = ami.search(scon.toArray(new String[scon.size()]),
				opers.toArray(new String[scon.size()]), vals.toArray());
		// Bug fixed at 2010-07-31.
		if (alist == null || alist.isEmpty()) {
			return null;
		}
		List<VirtualNode> vnodelist = new ArrayList<VirtualNode>();
		for (int k = 0; k < alist.size(); k++) {
			VirtualNode vnode = new VirtualNode(alist.get(k));
			vnodelist.add(vnode);
		}
		return vnodelist;
	}

	public synchronized VirtualNode addVirtualNode(String partitionId,
			String privateIp, String publicIp, String vnController,
			String applianceId, boolean isDeploy,
			HashMap<String, String> attributes, String desc) throws Exception {
		Partition par = this.view(partitionId);
		if (par == null) {
			throw new Exception("The partition with id " + partitionId
					+ " is not exist.");
		}

		if (privateIp == null || "".equals(privateIp)) {
			throw new Exception(
					"The private ip of virtual node is blank or null.");
		}

		AssetManagerImpl ami = new AssetManagerImpl();

		// TODO the above case support later, because the network info will be
		// filtered by virtual network manager. we need to consider the same ip
		// nodes distribute in different partitions later.

		// FIXME notice here, we use privateIp as the name is a trick.
		String[] searchConditions = new String[] { "name" };
		String[] operators = new String[] { "=" };
		Object[] values = new String[] { privateIp };
		List<Asset> alist = ami.search(searchConditions, operators, values);
		if (alist != null && !alist.isEmpty()) {
			throw new Exception("The node " + privateIp + " exists already.");
		}

		VirtualNode vn = new VirtualNode();
		vn.setName(privateIp);
		vn.setAssetLeaserId(partitionId);
		vn.setApplianceId(applianceId);
		vn.setPrivateIps(new String[] { privateIp });
		if (publicIp != null && !"".equals(publicIp)) {
			vn.setPublicIps(new String[] { publicIp });
		}
		vn.setAssetController(vnController);
		vn.setDescription(desc);
		if (isDeploy) {
			vn.setRunningStatus(XMMConstants.MachineRunningState.BOOT
					.toString());
		} else {
			vn.setRunningStatus(XMMConstants.MachineRunningState.WAIT_DEPLOY
					.toString());
		}
		if (attributes != null && !attributes.isEmpty()) {
			vn.getAttributes().putAll((attributes));
		}

		Asset addedAsset = null;
		if (isDeploy) {
			addedAsset = ami.add(vn, false);
		} else {
			addedAsset = ami.add(vn, true);
		}

		if (addedAsset == null) {
			return null;
		}

		VirtualNode newvn = new VirtualNode(addedAsset);

		log.info("A new virtual node with the privateIp " + privateIp
				+ " is added to the partition (" + partitionId
				+ ") sucessfully.");
		return newvn;
	}

	public VirtualNode removeVirtualNode(String nodeid, boolean isUndeploy)
			throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		Asset asset = ami.view(nodeid);
		if (asset == null) {
			return null;
		}
		VirtualNode vn = null;
		if (isUndeploy) {
			vn = new VirtualNode(ami.remove(nodeid, false));
		} else {
			vn = new VirtualNode(ami.remove(nodeid, true));
		}

		log.info("One VirtualNode (" + vn.getName() + " " + vn.getGuid()
				+ ")is removed.");
		return vn;
	}
}
