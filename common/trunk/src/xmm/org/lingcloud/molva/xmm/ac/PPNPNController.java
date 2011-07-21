/*
 *  @(#)PPNPNController.java  2010-5-27
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
import org.lingcloud.molva.ocl.asset.AssetConstants;
import org.lingcloud.molva.ocl.asset.AssetController;
import org.lingcloud.molva.ocl.poll.AssetPollingTask;
import org.lingcloud.molva.ocl.poll.PollingTaskManager;
import org.lingcloud.molva.xmm.ac.PartitionAC;
import org.lingcloud.molva.xmm.pojos.Nic;
import org.lingcloud.molva.xmm.pojos.Partition;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.pojos.VirtualCluster;
import org.lingcloud.molva.xmm.pojos.VirtualNetwork;
import org.lingcloud.molva.xmm.services.PartitionManager;
import org.lingcloud.molva.xmm.services.VirtualClusterManager;
import org.lingcloud.molva.xmm.services.VirtualNetworkManager;
import org.lingcloud.molva.xmm.util.PNStaticMetaInfoParser;
import org.lingcloud.molva.xmm.util.XMMConstants;
import org.lingcloud.molva.xmm.util.XMMUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-30<br>
 * @author Xiaoyi Lu<br>
 */
public class PPNPNController extends AssetController {

	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(PPNPNController.class);

	private static String metaInfoCollector = null;

	private static String metaInfoSender = null;

	private static String vcConfigDriverPath = null;

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
		
		AssetPollingTask apt = new AssetPollingTask(pn.getGuid());
		PollingTaskManager.addPollingTask(apt);
		
		log.info("The physical node with id " + pn.getGuid()
				+ " is added to polling task manager.");
		return pn;
	}

	public void destroy(Asset asset) throws Exception {
		if (asset.getAssetState() != null
				&& (asset.getAssetState() == AssetConstants.AssetState.RESERVED 
				|| asset.getAssetState() == AssetConstants.AssetState.LEASED)) {
			if (asset.getLeaseId() == null || "".equals(asset.getLeaseId())) {
				throw new Exception("The physical node (" + asset.getGuid()
						+ " " + asset.getName()
						+ ") is reserved or leased in virtual network, "
						+ "please destroy them firstly.");
			} else {
				throw new Exception("The physical node (" + asset.getGuid()
						+ " " + asset.getName()
						+ ") is reserved or leased in virtual cluster ("
						+ asset.getLeaseId() + "), "
						+ "please destroy them firstly.");
			}
		}
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
		if (pn.getRunningStatus().equals(
				XMMConstants.MachineRunningState.BOOT.toString())
				|| pn.getRunningStatus().equals(
						XMMConstants.MachineRunningState.ERROR.toString())) {
			try {
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
			}catch (RemoteException e) {
				log.info("The physical node " + pn.getName() + " is stil booting.");
				return pn;
			}
		}
		else if(pn.getRunningStatus().equals(
				XMMConstants.MachineRunningState.SHUTDOWN.toString())){
			log.info("The physical node " + pn.getName()
						+ "'s running status is " + pn.getRunningStatus());
		}
		else if (pn.getRunningStatus().equals(
				XMMConstants.MachineRunningState.RUNNING.toString())) {
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
			}
		} else {
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
		// FIXME Do configuration; Make sure this method should be complete
		// immedially.
		PhysicalNode pn = new PhysicalNode(asset);
		String clusterId = pn.getVirtualClusterID();
		if (clusterId == null || "".equals(clusterId)) {
			throw new Exception("No cluster info in the physical node,"
					+ " so it can not be init.");
		}
		VirtualCluster vc = VirtualClusterManager.getInstance().view(clusterId);
		String vnid = vc.getVirtualNetworkId();
		if (vnid == null || "".equals(vnid)) {
			throw new Exception(
					"No network info in the virtual cluster of physical node,"
							+ " so it can not be init.");
		}
		VirtualNetwork vn = VirtualNetworkManager.getInstance().view(vnid);
		if (pn.getPrivateIps()[0].equals(vn.getHeadNodeIp())) {
			// Bug fixed, we need to set this node as a head node. modified at
			// 2010-12-10.
			pn.setHeadNode(true);
			this.doConfiguration(pn, vn, true);
		} else {
			this.doConfiguration(pn, vn, false);
		}
		log.info("PhysicalNode (" + asset.getName() + ") of "
				+ this.getClass().getName() + " is init successfully.");
		return pn;
	}

	private boolean doConfiguration(PhysicalNode pn, VirtualNetwork vn,
			boolean isHeadNode) throws Exception {
		if (isHeadNode) {
			// TODO now the physical node does not implement appliance
			// management mechanism. so use the nodeType as the appliance.
			String appliance = pn.getApplianceId();
			String headNodeIp = pn.getPrivateIps()[0];
			// FIXME no need to do this check, it has been check before this
			// method invoked.

			if (headNodeIp == null || headNodeIp.trim().equals("")
					|| (!PartitionAC.isValidType(appliance))) {
				// throw new Exception("Illega argument.");
				log.warn("The physical node " + pn.getName()
						+ " does not have the proper type " + appliance
						+ " or ip address " + headNodeIp);
				return false;
			}
			StringBuffer cmdSB = new StringBuffer();
			// command format: vcConfig_driver.sh headnodeip
			// otherip1,otherip2,otherip3 type
			// the command return true or false.
			if (vcConfigDriverPath == null || "".equals(vcConfigDriverPath)) {
				vcConfigDriverPath = XMMUtil
						.getVirtualClusterConfigDriverPathInCfgFile();
			}

			cmdSB.append(vcConfigDriverPath).append(" ")
					.append(headNodeIp.trim()).append(" ");
			List<Nic> nics = vn.getPrivateIpNics();
			for (int i = 0; i < nics.size(); i++) {
				Nic nic = nics.get(i);
				if (nic.getIp().equals(headNodeIp)) {
					continue;
				}
				if (i == nics.size() - 1) {
					cmdSB.append(nic.getIp());
				} else {
					cmdSB.append(nic.getIp() + ",");
				}
			}

			cmdSB.append(appliance.trim());

			log.debug("command: \"" + cmdSB.toString() + "\"");

			String stdout = null;
			try {
				stdout = XMMUtil.runCommand(cmdSB.toString());
				if (stdout == null || !(stdout.trim().equals("true"))) {
					return false;
				}
			} catch (Exception e) {
				log.error("Execute \"" + cmdSB.toString()
						+ "\" fail, caused by " + e.getMessage());
				throw new RemoteException(e.getMessage(), e);
			}
		} 
		return true;
	}

	public Asset antiInit(Asset asset) throws Exception {
		// Bug fixed, we need to set this node as a general node. modified at
		// 2010-12-10.
		PhysicalNode pn = new PhysicalNode(asset);
		pn.setHeadNode(false);
		log.info("The physical node " + asset.getName()
				+ " is antiInit itself.");
		// Nothing to do for the provision pn physical nodes, because next time,
		// it will be inited again.
		return pn;
	}

	public Asset provision(Asset asset) throws Exception {
		PhysicalNode pn = new PhysicalNode(asset);
		String clusterId = pn.getVirtualClusterID();
		if (clusterId == null || "".equals(clusterId)) {
			throw new Exception("No cluster info in the physical node, "
					+ "so it can not be provisioned.");
		}
		VirtualCluster vc = VirtualClusterManager.getInstance().view(clusterId);
		String vnid = vc.getVirtualNetworkId();
		if (vnid == null || "".equals(vnid)) {
			throw new Exception(
					"No network info in the virtual cluster of physical node,"
							+ " so it can not be provision.");
		}
		VirtualNetwork vn = VirtualNetworkManager.getInstance().view(vnid);
		if (pn.getPrivateIps()[0].equals(vn.getHeadNodeIp())) {
			this.checkReady(pn, vn, true);
		} else {
			this.checkReady(pn, vn, false);
		}
		log.info("PhysicalNode (" + asset.getName() + ") of "
				+ this.getClass().getName() + " in virtual cluster ("
				+ vc.getName() + ") is provision successfully.");
		return pn;
	}

	private void checkReady(PhysicalNode pn, VirtualNetwork vn, boolean b) {
		// TODO may need check the configuration is completed or not.
		// now we choose lazy policy, do nothing.
		return;
	}

	public Asset revoke(Asset asset) throws Exception {
		// Bug fixed, we need to set this node as a general node. modified at
		// 2010-12-10.
		PhysicalNode pn = new PhysicalNode(asset);
		pn.setHeadNode(false);
		log.info("The physical node " + asset.getName() + " is revoke itself.");
		// Nothing to do for the revoked pn physical nodes, because next time,
		// it will be inited again.
		return pn;
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
