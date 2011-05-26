/*
 *  @(#)PartitionAC.java  2010-5-27
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetController;
import org.lingcloud.molva.xmm.pojos.Partition;
import org.lingcloud.molva.xmm.services.VirtualManager;
import org.lingcloud.molva.xmm.util.XMMUtil;
import org.lingcloud.molva.xmm.vmc.VirtualClient;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-28<br>
 * @author Xiaoyi Lu<br>
 * @email luxiaoyi@software.ict.ac.cn
 */
public class PartitionAC extends AssetController {
	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(PartitionAC.class);

	public static final String HPC = "HPC";

	public static final String VM = "VM";

	public static final String DC = "DC";

	public static final String STORAGE = "STORAGE";

	public static final String GENERAL = "GENERAL";

	public static final String INIT = "init";

	public static final String HPC_SOFT = "Torque";

	public static final String VM_SOFT = "Xen";

	public static final String MR_SOFT = "Hadoop";

	public static final String STORAGE_SOFT = "BlueWhale FS";

	public static final String REQUIRED_ATTR_NODETYPE = "nodeType";

	public static final String ATTR_NODE_PRE_INSTALLED_SOFT = "nodePreInstalledSoft";

	public static final String NODE_DEPLOY_STATUS_CLONED = "cloned";

	/**
	 * vaild partition type
	 */
	public static final String[] validTypeList = { HPC, VM, DC, INIT, STORAGE,
			GENERAL };

	private static String parDriverPath = null;

	private static String parImageServer = null;

	@Override
	public Asset create(Asset asset) throws Exception {
		Partition par = new Partition(asset);
		String nodeType = par.getNodeType();
		if (!isValidType(nodeType)) {
			throw new Exception(
					"The partition of "
							+ asset.getName()
							+ " should has a valid attribute with the name of nodeType.");
		}
		// For support multiple
		// virtual machine leasing partition.
		if (PartitionAC.VM.equals(nodeType)) {
			VirtualClient vc = VirtualManager.getInstance().getVirtualClient();
			Partition newpar = vc.allocateVMPartition(par);
			log.info("Partition (" + par.getName()
					+ ") with VM nodetype of PartitionAC is creating.");
			return newpar;
		}
		log.info("Partition (" + par.getName()
				+ ") of PartitionAC is creating.");
		return par;
	}

	public static boolean isValidType(String type) {
		if (type == null || type.trim().equals(""))
			return false;
		for (int i = 0; i < PartitionAC.validTypeList.length; i++) {
			if (PartitionAC.validTypeList[i].trim().equals(type.trim()))
				return true;
		}
		return false;
	}

	@Override
	public void destroy(Asset asset) throws Exception {
		Partition par = new Partition(asset);
		String nodeType = par.getNodeType();
		// For support multiple
		// virtual machine leasing partition.
		if (PartitionAC.VM.equals(nodeType)) {
			VirtualClient vc = VirtualManager.getInstance().getVirtualClient();
			vc.freeVMPartition(par);
			log.info("Partition (" + par.getName()
					+ ") with VM nodetype of PartitionAC is destroying.");
			return;
		}
		log.info("Partition (" + asset.getName()
				+ ") of PartitionAC is destroying.");
	}

	@Override
	public Asset poll(Asset asset) throws Exception {
		log.info("Partition (" + asset.getName()
				+ ") of PartitionAC is polling itself.");
		return asset;
	}

	@Override
	public double calculatePrice(Asset asset, HashMap params) throws Exception {
		return asset.getPrice();
	}

	@Override
	public Asset init(Asset asset) throws Exception {
		log.info("Partition (" + asset.getName()
				+ ") of PartitionAC is initing itself.");
		return asset;
	}

	@Override
	public Asset antiInit(Asset asset) throws Exception {
		log.info("Partition (" + asset.getName()
				+ ") of PartitionAC is antiIniting.");
		return asset;
	}

	@Override
	public Asset provision(Asset asset) throws Exception {
		log.info("Partition (" + asset.getName()
				+ ") of PartitionAC is provisioning itself.");
		return asset;
	}

	@Override
	public Asset revoke(Asset asset) throws Exception {
		log.info("Partition (" + asset.getName()
				+ ") of PartitionAC is revoking itself.");
		return asset;
	}

	private static boolean isValidPath(String imagePlace) {
		if (imagePlace == null || imagePlace.trim().equals(""))
			return false;
		else
			return true;
	}

	public static boolean addPNNode(String ip, String type) throws Exception {
		if (ip == null || ip.trim().equals("") || (!isValidType(type))) {
			throw new Exception("Illega argument.");
		}
		StringBuffer cmdSB = new StringBuffer();
		// command format: driver.sh ip type si_server_ip -p

		if (parImageServer == null || "".equals(parImageServer)) {
			parImageServer = XMMUtil.getPartitionServerAddressInCfgFile();
		}

		if (parDriverPath == null || "".equals(parDriverPath)) {
			parDriverPath = XMMUtil.getPartitionDriverPathInCfgFile();
		}

		cmdSB.append(parDriverPath).append(" ").append(ip.trim()).append(" ")
				.append(type.trim()).append(" ").append(parImageServer.trim())
				.append(" -p ");

		log.debug("command: \"" + cmdSB.toString() + "\"");

		String stdout = null;
		try {
			stdout = XMMUtil.runCommand(cmdSB.toString());

			if (stdout == null || !(stdout.trim().equals("true"))){
				
				return false;
			}else
				return true;
		} catch (Exception e) {
			log.error("Execute \"" + cmdSB.toString() + "\" fail, caused by "
					+ e.getMessage());
			throw new RemoteException(e.getMessage(), e);
		}
	}

	public static String getPhysicalNodeStatus(String ip) throws Exception {
		if (ip == null || ip.trim().equals(""))
			throw new Exception("Illega argument.");
		if (parImageServer == null || "".equals(parImageServer)) {
			parImageServer = XMMUtil.getPartitionServerAddressInCfgFile();
		}

		if (parDriverPath == null || "".equals(parDriverPath)) {
			parDriverPath = XMMUtil.getPartitionDriverPathInCfgFile();
		}
		// command format: driver.sh 10.0.0.10 -s
		String command = parDriverPath + "  " + ip + " -s";

		try {
			log.debug("command: \"" + command + "\"");
			String stdout = XMMUtil.runCommand(command);
			if (stdout == null || stdout.trim().equals(""))
				throw new RemoteException("unknown status");
			else
				return stdout.trim();
		} catch (Exception e) {
			log.error("Execute \"" + command + "\" fail, caused by "
					+ e.getMessage());
			throw new RemoteException(e.getMessage(), e);
		}

	}

}
