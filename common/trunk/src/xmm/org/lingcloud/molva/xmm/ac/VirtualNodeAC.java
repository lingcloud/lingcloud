/*
 *  @(#)VirtualNodeAC.java  2010-5-27
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
import org.lingcloud.molva.ocl.asset.AssetManagerImpl;
import org.lingcloud.molva.ocl.util.VoalUtil;
import org.lingcloud.molva.xmm.pojos.VirtualNode;
import org.lingcloud.molva.xmm.services.VirtualManager;
import org.lingcloud.molva.xmm.util.XMMConstants;
import org.lingcloud.molva.xmm.vam.pojos.VirtualAppliance;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;
import org.lingcloud.molva.xmm.vmc.VirtualClient;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-7-25<br>
 * @author Xiaoyi Lu<br>
 * @email luxiaoyi@software.ict.ac.cn
 */
public class VirtualNodeAC extends AssetController {

	private static final int MAX_TRY_TIMES = 10;

	private static final long SLEEP_TIME = 1000 * 3;

	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(VirtualNodeAC.class);

	public Asset create(Asset asset) throws Exception {
		VirtualNode vn = new VirtualNode(asset);
		if (vn.getApplianceId() == null || "".equals(vn.getApplianceId())) {
			throw new Exception("The appliance id of virtual node "
					+ vn.getName() + " is null or blank.");
		}
		// vn.isHeadNode();
		// vn.getCPUNum();
		// vn.getMemSize();
		// if (vn.getDeployTagInCreate()) {
		// this.provision(asset);
		// }
		log.info("The virtual node " + vn.getGuid() + vn.getName()
				+ " is creating in VirtualNodeAC.");
		return vn;
	}

	public void destroy(Asset asset) throws Exception {
		log.info("The virtual node " + asset.getGuid() + asset.getName()
				+ " is destroying in VirtualNodeAC.");
	}

	public Asset poll(Asset asset) throws Exception {
		VirtualNode vnode = new VirtualNode(asset);
		String clusterId = vnode.getVirtualClusterID();
		if (clusterId == null || "".equals(clusterId)) {
			throw new Exception(
					"No cluster info in the virtual node, so it can not be polled.");
		}

		try {
			VirtualClient vclient = VirtualManager.getInstance()
					.getVirtualClient();
			VirtualNode vnodeNew = vclient.refreshVirtualNode(vnode);
			log.info("VirtualNode (" + asset.getName() + ") of "
					+ this.getClass().getName() + " is polled successfully.");
			return vnodeNew;
		} catch (Exception e) {
			String err = "Pool VirtualNode (" + asset.getName()
					+ ") errored : " + e.toString();
			log.error(err);
			asset.setLastErrorMessage(err);
		}
		return asset;
	}

	public void start(Asset asset) {
		try {
			VirtualClient vclient = VirtualManager.getInstance()
					.getVirtualClient();
			VirtualNode vn = new VirtualNode(asset);
			VirtualNode vnodeNew = vclient.startVirtualNode(vn);
			log.info("VirtualNode (" + vnodeNew.getName() + ") of "
					+ this.getClass().getName() + " is started successfully.");
			return;
		} catch (Exception e) {
			String err = "Start VirtualNode (" + asset.getName()
					+ ") errored : " + e.toString();
			VoalUtil.setLastErrorMessage4Asset(new AssetManagerImpl(), asset,
					err);
			log.error(err);
		}
		return;
	}

	public void stop(Asset asset) {
		try {
			VirtualClient vclient = VirtualManager.getInstance()
					.getVirtualClient();
			VirtualNode vn = new VirtualNode(asset);
			VirtualNode vnodeNew = vclient.stopVirtualNode(vn);
			log.info("VirtualNode (" + vnodeNew.getName() + ") of "
					+ this.getClass().getName() + " is stopped successfully.");
			return;
		} catch (Exception e) {
			String err = "Stop VirtualNode (" + asset.getName()
					+ ") errored : " + e.toString();
			VoalUtil.setLastErrorMessage4Asset(new AssetManagerImpl(), asset,
					err);
			log.error(err);
		}
		return;
	}

	public double calculatePrice(Asset asset, HashMap params) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Asset init(Asset asset) throws Exception {
		// FIXME every node submit its creation to opennebula or only head node
		// submit virtual cluster creation; now we choose the first method.

		// FIXME due to the opennebula process a request may have a period of
		// time, so we do provision operation in the init method, and left
		// check status in provision method.

		VirtualNode vnode = new VirtualNode(asset);
		String apid = vnode.getApplianceId();
		int tryTimes = 0;
		boolean isReady = false;
		VirtualAppliance va = null;
		while (tryTimes < VirtualNodeAC.MAX_TRY_TIMES) {
			va = VAMUtil.getVAManager().queryAppliance(apid);
			if (va == null) {
				throw new Exception("The virtual appliance (" + apid + " "
						+ vnode.getApplianceName() + ") is not exist.");
			}
			// FIXME wait for the disk ready.
			if (VAMConstants.STATE_READY != va.getState()) {
				tryTimes++;
				Thread.currentThread().sleep(VirtualNodeAC.SLEEP_TIME);
			} else {
				isReady = true;
				break;
			}
		}

		if (!isReady) {
			throw new Exception("The virtual appliance (" + apid + " "
					+ vnode.getApplianceName() + ") is not ready after "
					+ VirtualNodeAC.MAX_TRY_TIMES + " times checking.");
		}

		String clusterId = vnode.getVirtualClusterID();
		if (clusterId == null || "".equals(clusterId)) {
			throw new Exception(
					"No cluster info in the virtual node, so it can not be init.");
		}

		try {
			VirtualClient vclient = VirtualManager.getInstance()
					.getVirtualClient();
			VirtualNode vnodeNew = vclient.allocateVirtualNode(vnode);
			log.info("VirtualNode (" + asset.getName() + ") of "
					+ this.getClass().getName() + " is init successfully.");
			return vnodeNew;
		} catch (Exception e) {
			String err = "Allocate VirtualNode errored : " + e.toString();
			log.error(err);
			throw new Exception(err);
		}
	}

	public Asset antiInit(Asset asset) throws Exception {
		// FIXME due to we do provision operation in the init method, so we must
		// cancel it here.
		VirtualNode vnode = new VirtualNode(asset);
		String clusterId = vnode.getVirtualClusterID();
		if (clusterId == null || "".equals(clusterId)) {
			throw new Exception(
					"No cluster info in the virtual node, so it can not be antiinit.");
		}

		try {
			VirtualClient vclient = VirtualManager.getInstance()
					.getVirtualClient();
			vclient.freeVirtualNode(vnode, true);
			vnode.setRunningStatus(XMMConstants.MachineRunningState.SHUTDOWN
					.toString());
			log.info("VirtualNode (" + asset.getName() + ") of "
					+ this.getClass().getName() + " is antiinit successfully.");
			return vnode;
		} catch (Exception e) {
			String err = "Free VirtualNode errored in anti-init method : "
					+ e.toString();
			vnode.setLastErrorMessage(err);
			vnode.setRunningStatus(XMMConstants.MachineRunningState.ERROR
					.toString());
			log.error(err);
			return vnode;
		}
	}

	public Asset provision(Asset asset) throws Exception {
		VirtualNode vnode = new VirtualNode(asset);
		String clusterId = vnode.getVirtualClusterID();
		if (clusterId == null || "".equals(clusterId)) {
			throw new Exception(
					"No cluster info in the virtual node, so it can not be provision.");
		}

		int tryTimes = 0;
		try {
			VirtualClient vclient = VirtualManager.getInstance()
					.getVirtualClient();
			while (tryTimes < VirtualNodeAC.MAX_TRY_TIMES) {
				VirtualNode vnodeNew = vclient.refreshVirtualNode(vnode);
				// try best to make sure all nodes are running when the state of
				// lease transferring to running.
				if (vnodeNew.getRunningStatus()
						.equals(XMMConstants.MachineRunningState.RUNNING
								.toString())) {
					log.info("VirtualNode (" + asset.getName() + ") of "
							+ this.getClass().getName()
							+ " is provision successfully.");
					return vnodeNew;
				} else {
					tryTimes++;
					Thread.currentThread().sleep(VirtualNodeAC.SLEEP_TIME);
				}
			}
		} catch (Exception e) {
			String err = "Provision VirtualNode errored : " + e.toString();
			asset.setLastErrorMessage(err);
			log.error(err);
		}
		return asset;
	}

	public Asset revoke(Asset asset) throws Exception {
		return this.antiInit(asset);
	}

}
