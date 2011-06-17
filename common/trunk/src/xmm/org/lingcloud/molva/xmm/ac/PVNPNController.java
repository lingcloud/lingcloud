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

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetController;
import org.lingcloud.molva.ocl.poll.AssetPollingTask;
import org.lingcloud.molva.ocl.poll.PollingTaskManager;
import org.lingcloud.molva.xmm.pojos.Partition;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.services.PartitionManager;
import org.lingcloud.molva.xmm.services.VirtualManager;
import org.lingcloud.molva.xmm.util.XMMConstants;
import org.lingcloud.molva.xmm.util.XMMUtil;
import org.lingcloud.molva.xmm.vmc.VirtualClient;

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
		if (pn.getRunningStatus().equals(
				XMMConstants.MachineRunningState.BOOT.toString())
				&& pn.getRedeployTagInCreate()) {
			if (PartitionAC.getPhysicalNodeStatus(pn.getPrivateIps()[0])
					.equals("cloned")) {
				pn.setRunningStatus(XMMConstants.MachineRunningState.RUNNING
						.toString());
			}
		} else {
			VirtualClient vc = VirtualManager.getInstance().getVirtualClient();
			PhysicalNode newpn = vc.getVMProvisionNode(pn);
			log.info("The physical node " + pn.getName()
					+ "'s running status is " + newpn.getRunningStatus());
			return newpn;
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

	public void start(Asset asset) {
		log.info("The physical node " + asset.getName() 
				+ " is started itself.");
	}

	public void stop(Asset asset) {
		log.info("The physical node " + asset.getName() 
				+ " is stopped itself.");
	}

}
