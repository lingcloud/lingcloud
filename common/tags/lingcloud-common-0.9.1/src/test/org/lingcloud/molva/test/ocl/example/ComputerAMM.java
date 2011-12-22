/*
 *  @(#)ComputerAMM.java  Jul 27, 2011
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
package org.lingcloud.molva.test.ocl.example;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.ocl.amm.AssetMatchMaker;
import org.lingcloud.molva.ocl.asset.AssetConstants.AssetState;
import org.lingcloud.molva.ocl.asset.AssetManagerImpl;
import org.lingcloud.molva.ocl.lease.Lease;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2011-7-27<br>
 * @author Ruijian Wang<br>
 * 
 */
public class ComputerAMM implements AssetMatchMaker {
	private static Log log = LogFactory.getLog(ComputerAMM.class);
	private AssetManagerImpl ami = null;

	public ComputerAMM() {
		ami = new AssetManagerImpl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.ocl.amm.AssetMatchMaker#assetMatchMaking(
	 * org.lingcloud.molva.ocl.lease.Lease)
	 */
	@Override
	public Lease assetMatchMaking(Lease lease) throws Exception {
		if (lease == null) {
			return null;
		}

		log.info("Computer (" + lease.getName()
				+ ") is doing assetMatchMaking.");
		Computer computer = new Computer(lease);

		String monitorBrand = computer.getMonitorBrand();
		String monitorSize = computer.getMonitorSize();
		String mainframeBrand = computer.getMainframeBrand();
		String mainframeMemory = computer.getMainframeMemory();

		if (monitorBrand == null || monitorSize == null
				|| mainframeBrand == null || mainframeMemory == null) {
			throw new Exception("Can't not match.");
		}

		Monitor monitor = new Monitor();
		monitor.setBrand(monitorBrand);
		monitor.setSize(monitorSize);
		Mainframe mainframe = new Mainframe();
		mainframe.setBrand(mainframeBrand);
		mainframe.setMemory(mainframeMemory);

		List<Monitor> monitors = DeviceFactory.getInstance().getMonitors(
				monitor);
		List<Mainframe> mainframes = DeviceFactory.getInstance().getMainframes(
				mainframe);

		Monitor matchMonitor = null;
		for (Monitor m : monitors) {
			if (m.getAssetState() == AssetState.IDLE) {
				matchMonitor = m;
				break;
			}
		}

		Mainframe matchMainframe = null;
		for (Mainframe m : mainframes) {
			if (m.getAssetState() == AssetState.IDLE) {
				matchMainframe = m;
				break;
			}
		}

		if (matchMainframe == null || matchMonitor == null) {
			throw new Exception("Can't not match.");
		}

		matchMonitor.setAssetState(AssetState.RESERVED);
		matchMonitor.setLeaseId(computer.getGuid());
		ami.update(matchMonitor.getGuid(), matchMonitor);

		matchMainframe.setAssetState(AssetState.RESERVED);
		matchMainframe.setLeaseId(computer.getGuid());
		ami.update(matchMainframe.getGuid(), matchMainframe);

		computer.setMonitorId(matchMonitor.getGuid());
		computer.setMainframeId(matchMainframe.getGuid());

		return computer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.ocl.amm.AssetMatchMaker#validateMachedAssets(
	 * org.lingcloud.molva.ocl.lease.Lease)
	 */
	@Override
	public void validateMachedAssets(Lease lease) throws Exception {
		HashMap<String, String> matchedAssets = lease.getAssetIdAndTypeMap();
		if (matchedAssets == null || matchedAssets.size() == 0) {
			String msg = "No matched assets for the Computer ("
					+ lease.getGuid() + lease.getName() + ").";
			log.error(msg);
			throw new Exception(msg);
		}
		if (!matchedAssets.containsValue(Monitor.class.getName())) {
			String msg = "Matched assets in the computer (" + lease.getGuid()
					+ lease.getName()
					+ ") should have one asset with monitor type.";
			throw new Exception(msg);
		}
		if (!matchedAssets.containsValue(Mainframe.class.getName())) {
			String msg = "Matched assets in the computer (" + lease.getGuid()
					+ lease.getName()
					+ ") should have assets with mainframe type.";
			throw new Exception(msg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.ocl.amm.AssetMatchMaker#matchErrorHandler(
	 * org.lingcloud.molva.ocl.lease.Lease)
	 */
	@Override
	public Lease matchErrorHandler(Lease lease) {
		if (lease == null) {
			return null;
		}
		return lease;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.ocl.amm.AssetMatchMaker#handleTermination(
	 * org.lingcloud.molva.ocl.lease.Lease)
	 */
	@Override
	public void handleTermination(Lease lease) {
		this.matchErrorHandler(lease);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.ocl.amm.AssetMatchMaker#satiableDecision(
	 * org.lingcloud.molva.ocl.lease.Lease)
	 */
	@Override
	public void satiableDecision(Lease lease) throws Exception {
		if (lease == null) {
			throw new Exception("The input parameter lease is null.");
		}

		log.info("Computer (" + lease.getName()
				+ ") is doing satiableDecision.");
		Computer computer = new Computer(lease);

		String monitorBrand = computer.getMonitorBrand();
		String monitorSize = computer.getMonitorSize();
		String mainframeBrand = computer.getMainframeBrand();
		String mainframeMemory = computer.getMainframeMemory();

		if (monitorBrand == null || monitorSize == null
				|| mainframeBrand == null || mainframeMemory == null) {
			String msg = "Can not satisfy. The monitor brand, monitor size , " 
				+ "mainframe brand and mainframe memory should not be empty.";
			log.error(msg);
			throw new Exception(msg);
		}

		Monitor monitor = new Monitor();
		monitor.setBrand(monitorBrand);
		monitor.setSize(monitorSize);
		Mainframe mainframe = new Mainframe();
		mainframe.setBrand(mainframeBrand);
		mainframe.setMemory(mainframeMemory);

		List<Monitor> monitors = DeviceFactory.getInstance().getMonitors(
				monitor);
		List<Mainframe> mainframes = DeviceFactory.getInstance().getMainframes(
				mainframe);

		boolean find = false;
		for (Monitor m : monitors) {
			if (m.getAssetState() == AssetState.IDLE) {
				find = true;
				break;
			}
		}

		if (!find) {
			String msg = "Can not find available monitor.";
			log.error(msg);
			throw new Exception(msg);
		}

		find = false;
		for (Mainframe m : mainframes) {
			if (m.getAssetState() == AssetState.IDLE) {
				find = true;
				break;
			}
		}

		if (!find) {
			String msg = "Can not find available mainframe.";
			log.error(msg);
			throw new Exception(msg);
		}

	}

}
