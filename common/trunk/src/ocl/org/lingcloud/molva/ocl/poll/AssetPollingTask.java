/*
 *  @(#)AssetPollingTask.java  2010-5-30
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

package org.lingcloud.molva.ocl.poll;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetManagerImpl;
import org.lingcloud.molva.ocl.asset.AssetConstants.AssetState;
import org.lingcloud.molva.ocl.lease.Lease;
import org.lingcloud.molva.ocl.lease.LeaseManagerImpl;
import org.lingcloud.molva.ocl.lease.LeaseConstants.LeaseLifeCycleState;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-30<br>
 * @author Xiaoyi Lu<br>
 */
public class AssetPollingTask implements PollingTask {

	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(AssetPollingTask.class);

	private int errorTimes = 0;

	private static final int MAX_ERROR_TIMES = 5;

	private AssetManagerImpl ami = null;

	private String assetId;

	public AssetPollingTask(String id) {
		this.assetId = id;
		if (ami == null) {
			ami = new AssetManagerImpl();
		}
	}

	public void run() {
		try {
			Asset asset = ami.view(assetId);
			if (asset == null) {
				PollingTaskManager.removePollingTask(this.getTaskGuid());
			}

			this.faultTolerant(asset);
			ami.refresh(asset);
			errorTimes = 0;
		} catch (Throwable t) {
			errorTimes++;
			log.error("Error occured when poll an asset " + this.assetId
					+ " at " + errorTimes + ", due to" + t.toString());
			if (errorTimes >= MAX_ERROR_TIMES) {
				log.error("The continous error times of polling asset "
						+ this.assetId + " exceeds the max number, "
						+ "so the task will be removed.");
				PollingTaskManager.removePollingTask(this.getTaskGuid());
			} else {
				log.error("The continous error times of polling asset "
						+ this.assetId + " does not exceed the max number, "
						+ "so ignore this error firstly.");
			}
		}
	}

	public String getTaskGuid() {
		return assetId;
	}

	public void faultTolerant(Asset asset) {
		// XXX fault tolerance, we check the lease is or not exist to fix
		// some errors. added at 2010-12-14.
		// FIXME we may implement any other fault tolerant policy
		// here.
		if (asset == null) {
			return;
		}
		String leaseId = asset.getLeaseId();
		try {
			if (leaseId != null && !"".equals(leaseId)) {
				Lease lease = new LeaseManagerImpl().view(leaseId);
				if (lease == null) {
					log.warn("Some error may be occurred before, "
							+ " and now the asset (" + asset.getName() + " "
							+ asset.getGuid()
							+ ") said it is leased by the lease (" + leaseId
							+ "), but the lease is not exist.");
					ami.handleAntiReady(asset.getGuid(), leaseId);
					asset.setAssetState(AssetState.IDLE);
					asset.setLeaseId(null);
					ami.update(assetId, asset);
				} else {
					if (LeaseLifeCycleState.EFFECTIVE == lease
							.getLifecycleState()
							&& AssetState.LEASED != asset.getAssetState()) {
						log.warn("Some error may be occurred before, "
								+ " and now the asset (" + asset.getName()
								+ " " + asset.getGuid()
								+ ") said it is leased by the lease ("
								+ lease.getName() + " " + leaseId
								+ "), but the asset's state is "
								+ asset.getAssetState() + ", not leased.");
						asset.setAssetState(AssetState.LEASED);
						ami.update(assetId, asset);
					}
				}
			}
		} catch (Exception e) {
			log.warn("Ignore occurred error when check fault for"
					+ " the polled asset (" + asset.getName() + " "
					+ asset.getGuid() + "), the reason is " + e.toString());
		}
	}
}
