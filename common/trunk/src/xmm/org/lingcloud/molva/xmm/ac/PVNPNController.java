/*
 *  @(#)PVNPNController.java  2010-5-27
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

package org.lingcloud.molva.xmm.ac;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetController;
import org.lingcloud.molva.ocl.poll.AssetPollingTask;
import org.lingcloud.molva.ocl.poll.PollingTaskManager;
import org.lingcloud.molva.xmm.ac.PartitionAC;
import org.lingcloud.molva.xmm.pojos.Nic;
import org.lingcloud.molva.xmm.pojos.Partition;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.services.PartitionManager;
import org.lingcloud.molva.xmm.services.VirtualManager;
import org.lingcloud.molva.xmm.services.VirtualNetworkManager;
import org.lingcloud.molva.xmm.util.XMMConstants;
import org.lingcloud.molva.xmm.util.XMMUtil;
import org.lingcloud.molva.xmm.vmc.VirtualClient;
import org.lingcloud.molva.xmm.util.PNStaticMetaInfoParser;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-30<br>
 * @author Xiaoyi Lu<br>
 */
public class PVNPNController extends AssetController {
	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(PVNPNController.class);

	private static String imageMgmtWay = null;

	private static String metaInfoCollector = null;
	
	private static String metaInfoSender = null;

	public static final String ONE_CLUSTER_ID = "ONE_CLUSTER_ID";

	@Override
	public Asset create(Asset asset) throws Exception {
		PhysicalNode pn = new PhysicalNode(asset);
		Partition par = PartitionManager.getInstance().view(
				pn.getAssetLeaserId());
		String nodeType = null;
		if (par.getAssetController().equals(PartitionAC.class.getName())) {
			nodeType = par.getAttributes().get(
					PartitionAC.REQUIRED_ATTR_NODETYPE);
			// TODO now the physical node does not implement appliance
			// management mechanism. so use the nodeType as the appliance.
			pn.setApplianceId(nodeType);
		}
		
		if (pn.getRedeployTagInCreate()) {
			try {
				if (!PartitionAC.addPNNode(pn.getPrivateIps()[0], nodeType)) {
					throw new Exception("A physical node (" + pn.getName()
							+ ") is failed to be added to the partition ("
							+ par.getName() + ").");
				}
			} catch (Exception e) {
				log.error(e.toString());
				throw e;
			}
		}
		pn.getAttributes().put(XMMConstants.HYPERVISOR,
				XMMConstants.HYPERVISOR_XEN);
		// XXX modified, at 2010-12-15, due to we find the io speed
		// is so slow in common PC Server with BlueWhale file system. Now we set
		// this by configuration.
		if (imageMgmtWay == null) {
			try {
				imageMgmtWay = XMMUtil.getImageMgmtWay();
				// Now we only support two cases;
				if (!XMMConstants.FILE_TRANSFER_SSH.equals(imageMgmtWay)
						&& !XMMConstants.FILE_TRANSFER_NFS
								.equals(imageMgmtWay)) {
					imageMgmtWay = XMMConstants.FILE_TRANSFER_SSH;
				}
			} catch (Exception e) {
				imageMgmtWay = XMMConstants.FILE_TRANSFER_SSH;
			}
		}
		pn.setTransferway(imageMgmtWay);
		PhysicalNode newpn = this.addVMProvisionNode(pn, par);
		AssetPollingTask apt = new AssetPollingTask(newpn.getGuid());
		PollingTaskManager.addPollingTask(apt);
		log.info("The physical node with id " + newpn.getGuid()
				+ " is added to polling task manager.");
		return newpn;
	}

	private PhysicalNode addVMProvisionNode(PhysicalNode pn, Partition par)
			throws Exception {
		VirtualClient vc = VirtualManager.getInstance().getVirtualClient();
		PhysicalNode newpn = vc.allocateVmProvisionNode(pn);
		if (!newpn.getGuid().equals(pn.getGuid())) {
			throw new Exception("The guid cannot be changed when allocate "
					+ "a vm provision node.");
		}
		try {
			vc.addVmProvisionNode2Partiton(newpn, par);
		} catch (Exception e) {
			String msg = "Add vm provision node (" + pn.getName()
					+ ") to partition (" + pn.getPartitionId()
					+ ") failed due to " + e.toString()
					+ ". Try to roll back to free vm provision node.";
			log.error(msg);
			// roll back;
			try {
				vc.freeVmProvisionNode(newpn);
			} catch (Exception e2) {
				String err = "Roll back failed to free vm provision node "
						+ newpn.getName() + ", due to " + e2.toString()
						+ ", please handle by mannual.";
				log.fatal(err);
				throw new Exception(err);
			}
			throw new Exception(msg);
		}
		log.info("A new vm provision node with the private ip "
				+ pn.getPrivateIps()[0] + " is added.");
		return newpn;
	}

	@Override
	public void destroy(Asset asset) throws Exception {
		PhysicalNode pn = new PhysicalNode(asset);
		VirtualClient vc = VirtualManager.getInstance().getVirtualClient();
		/*
		 * If error occurred, then the exception will be thrown.
		 */
		vc.freeVmProvisionNode(pn);
		PollingTaskManager.removePollingTask(asset.getGuid());
		log.info("The asset with id " + asset.getGuid()
				+ " is deleted from polling task manager.");
	}

	@Override
	public Asset poll(Asset asset) throws Exception {
		
		PhysicalNode pn = new PhysicalNode(asset.clone());
		
		
		if (pn.getRunningStatus() == null) {
			throw new Exception("The physical node has a null running status.");
		}
		/*if (pn.getRunningStatus().equals(
				XMMConstants.MachineRunningState.BOOT.toString())
				&& pn.getRedeployTagInCreate()) {
			try {
				if (PartitionAC.getPhysicalNodeStatus(pn.getPrivateIps()[0])
						.equals(PartitionAC.NODE_DEPLOY_STATUS_CLONED)) {
					pn.setRunningStatus(XMMConstants.MachineRunningState.RUNNING
							.toString());
				}
			}catch (RemoteException e) {
				log.info("The physical node " + pn.getName() + " is stil booting.");
				return pn;
			}
		}else */
		if (pn.getRunningStatus().equals(
				XMMConstants.MachineRunningState.BOOT.toString())
				|| pn.getRunningStatus().equals(
						XMMConstants.MachineRunningState.ERROR.toString())){
			//FIXME The property of the physical node "isRedeployInCreate" is not in use.
			// The former if block is not in function now.
			StringBuffer cmdSB = new StringBuffer();
			String cmd = XMMUtil.getOperatePhysicalNodeCmdInCfgFile();
			if (cmd == null || "".equals(cmd)) {
				log.error("can't get operatePhycialNodeCmd in Cfg file.");
				throw new Exception("can't get operatePhycialNodeCmd "
						+ "in Cfg file.");
			}
			cmdSB.append(cmd).append(" " + pn.getPrivateIps()[0] + " ping");
			String stdout = XMMUtil.runCommand(cmdSB.toString());
			if (stdout.trim().equals("true")) {
				pn.setRunningStatus(XMMConstants.MachineRunningState.RUNNING
						.toString());
				log.info("The phycial node " + pn.getName()
						+ " is booting sucess.");
			}
			else {
				log.info("The physical node " + pn.getName()
						+ " is still booting.");
			}
		}
		else if(pn.getRunningStatus().equals(
				XMMConstants.MachineRunningState.SHUTDOWN.toString())){
			log.info("The physical node " + pn.getName()
						+ "'s running status is " + pn.getRunningStatus());
		}

		if(pn.getRunningStatus().equals(
				XMMConstants.MachineRunningState.RUNNING.toString())){
			
			if (metaInfoSender == null || "".equals(metaInfoSender)) {
				metaInfoSender = XMMUtil.getStaticMetaInfoSenderInCfgFile();
			}
			if (metaInfoCollector == null || "".equals(metaInfoCollector)) {
				metaInfoCollector = XMMUtil
						.getStaticMetaInfoCollectorInCfgFile();
			}
			// XXX run command firstly. send when necessary.
			String command = metaInfoCollector.replaceAll("\\$host",
					pn.getPrivateIps()[0]);
			String stdout = XMMUtil.runCommand(command);
			if (stdout == null || "".equals(stdout)) {
				String scommand = metaInfoSender.replaceAll("\\$host",
						pn.getPrivateIps()[0]);
				XMMUtil.runCommand(scommand);
				log.debug("run command to send script : " + command);
				// Run command again.
				stdout = XMMUtil.runCommand(command);
			}
			if(stdout == null || "".equals(stdout))
			{
				
			}
			else {
				log.debug("run command to get meta info : " + command);
				PNStaticMetaInfoParser pnsmip = new PNStaticMetaInfoParser(stdout);
				pn.setCpuArch(pnsmip.getARCH());
				pn.setCpuModal(pnsmip.getMODELNAME());
				pn.setHostName(pnsmip.getHOSTNAME());
				
				pn.setCpuNum(pnsmip.getCpuNum());
				pn.setFreeCpu(pnsmip.getCpuNum());
				pn.setCpuSpeed((int) pnsmip.getCpuSpeed());
				
				pn.setMemsize(pnsmip.getMem());
				pn.setFreeMemory(pnsmip.getMem());
				
				// remove old ones.
				VirtualNetworkManager.triggerNetworkInfoRemove(pn.getNics());
				pn.setNics(pnsmip.getNics());
				// add new ones.
				VirtualNetworkManager.triggerNetworkInfoAdd(pn.getNics());
				VirtualClient vc = VirtualManager.getInstance().getVirtualClient();
				PhysicalNode newpn = vc.getVMProvisionNode(pn);
			
				log.info("The physical node " + pn.getName()
						+ "'s running status is " + newpn.getRunningStatus());
				return newpn;
			}
		}
		else {
			
			VirtualClient vc = VirtualManager.getInstance().getVirtualClient();
			PhysicalNode newpn = vc.getVMProvisionNode(pn);
			log.info("The physical node " + pn.getName()
					+ "'s running status is " + newpn.getRunningStatus());
			
			log.info("The physical node " + pn.getName()
						+ "'s running status is " + pn.getRunningStatus());
		}
		
		return pn;
	}

	@Override
	public double calculatePrice(Asset asset, 
			HashMap<String, String> params) throws Exception {
		return 0;
	}

	@Override
	public Asset init(Asset asset) throws Exception {
		log.info("PhysicalNode (" + asset.getName() + ") of "
				+ this.getClass().getName() + " is init successfully.");
		return asset;
	}

	@Override
	public Asset antiInit(Asset asset) throws Exception {
		log.info("PhysicalNode (" + asset.getName() + ") of "
				+ this.getClass().getName() + " is antiInit successfully.");
		return asset;
	}

	@Override
	public Asset provision(Asset asset) throws Exception {
		log.info("PhysicalNode (" + asset.getName() + ") of "
				+ this.getClass().getName() + " is provision successfully.");
		return asset;
	}

	@Override
	public Asset revoke(Asset asset) throws Exception {
		log.info("PhysicalNode (" + asset.getName() + ") of "
				+ this.getClass().getName() + " is revoke successfully.");
		return asset;
	}

	public void stop(Asset asset) throws Exception {
		PhysicalNode pn = new PhysicalNode(asset);
		String[] ips = pn.getPrivateIps();
		String name = pn.getName();
		
		try {
			StringBuffer cmdSB = new StringBuffer();
			String cmd = XMMUtil.getOperatePhysicalNodeCmdInCfgFile();
			if (cmd == null || "".equals(cmd)) {
				log.error("can't get operatePhycialNodeCmd in Cfg file.");
				throw new Exception("can't get operatePhycialNodeCmd "
						+ "in Cfg file.");
			}
			cmdSB.append(cmd).append(" " + ips[0] + " stop");
			String stdout = XMMUtil.runCommand(cmdSB.toString());
			if (stdout.trim().equals("true")) {
				log.info("shutdown phycial node " + name
						+ " sucess.");
			} else {
				log.info("shutdown phycial node " + name
						+ " Failed: " + stdout);
				throw new Exception("shutdown phycial node " + name + " Failed: " + stdout);
			}
			log.info("The physical node " + asset.getName() 
					+ " is shutdown itself.");
		} catch (Exception e) {
			throw e;
		}
	}

	public void start(Asset asset) throws Exception {
		PhysicalNode pn = new PhysicalNode(asset);
		String mac = null;
		String name = pn.getName();
		
		List<Nic> nicls  = pn.getNics();
		for (int j = 0; j < nicls.size(); j++) {
			Nic ll = (Nic) nicls.get(j);
			String nicName = ll.getNicName();
			if (nicName != null && nicName.matches("eth\\d++")) {
				// The eth0:0 and eth0:1 will have the same mac address.
				mac = ll.getMac();
				break;
			}
		}
		try {
			StringBuffer cmdSB = new StringBuffer();
			String cmd = XMMUtil.getOperatePhysicalNodeCmdInCfgFile();
			if (cmd == null || "".equals(cmd)) {
				log.error("can't get operatePhycialNodeCmd in Cfg file.");
				throw new Exception("can't get operatePhycialNodeCmd "
						+ "in Cfg file.");
			}
			cmdSB.append(cmd).append(" " + mac + " start");
			String stdout = XMMUtil.runCommand(cmdSB.toString());
			if (stdout.trim().equals("true")) {
				log.info("boot phycial node " + name
						+ " sucess.");
			} else {
				log.info("boot phycial node " + name
						+ " Failed: " + stdout);
				throw new Exception("boot phycial node " + name + " Failed: " + stdout);
			}
			log.info("The physical node " + name 
					+ " is booting itself.");
		} catch (Exception e) {
			throw e;
		}
	}

}
