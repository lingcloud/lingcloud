/*
 *  @(#)LeaseLifeCycleTracker.java  2010-5-21
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

package org.lingcloud.molva.ocl.lease;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.ocl.amm.AssetMatchMaker;
import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetManagerImpl;
import org.lingcloud.molva.ocl.lease.LeaseConstants.LeaseLifeCycleState;
import org.lingcloud.molva.ocl.util.VoalUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-21<br>
 * @author Xiaoyi Lu<br>
 */

public class LeaseLifeCycleTracker implements Runnable {

	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(LeaseLifeCycleTracker.class);

	private String leaseId;

	private LeaseManagerImpl lmi = null;

	public LeaseLifeCycleTracker(String leaseId) {
		this.leaseId = leaseId;
		lmi = new LeaseManagerImpl();
	}

	public void run() {
		Lease lease = null;
		try {
			try {
				lease = this.lmi.view(leaseId);
			} catch (Exception e1) {
				log.error("In LeaseLifeCycleTracker, error occurred"
						+ " when view lease with id " + leaseId + " due to "
						+ e1.toString());
				throw e1;
			}
			if (lease == null) {
				return;
			}

			if (VoalUtil.checkIsLeaseExpired(lease)) {
				log.info("The lease " + lease.getGuid()
						+ " is checked and expired in "
						+ "lifecycle tracker begining.");
				try {
					lmi.handleExpired(lease);
					LeaseLifeCycleMonitor.getInstance().removeMonitoredLease(
							lease.getGuid());
					log.info("The lease " + lease.getGuid()
							+ " is expired and removed from "
							+ "lifecycle monitor.");
					return;
				} catch (Exception e) {
					log.error("HandleExpiredLease error: " + e.toString());
				}
			}

			if (lease.getLifecycleState().equals(LeaseLifeCycleState.PENDING)) {
				// work for lease pending. only wait for the proper time to
				// negotiate.
				log.info("The lease " + lease.getGuid() + " is pending.");
				if (this.workForLeasePending(lease)) {
					LeaseLifeCycleMonitor.getInstance().addLeaseMonitoredOnce(
							lease);
				} else {
					lease.setLifecycleState(LeaseLifeCycleState.FAIL);
					try {
						this.lmi.update(this.leaseId, lease);
					} catch (Exception e) {
						log.error(e.toString());
					}
				}
			} else if (lease.getLifecycleState().equals(
					LeaseLifeCycleState.NEGOTIATING)) {
				// work for lease negotiating.
				log.info("The lease " + lease.getGuid() + " is negotiating.");
				if (this.workForLeaseNegotiating(lease)) {
					LeaseLifeCycleMonitor.getInstance().addLeaseMonitoredOnce(
							lease);
				} else {
					lease.setLifecycleState(LeaseLifeCycleState.FAIL);
					try {
						this.lmi.update(this.leaseId, lease);
						// FIXME we define an error handler method
						// in AssetMatchMaker class, which is invoked to clear
						// garbage nodes when partial error of many virtual
						// nodes creation.

					} catch (Exception e) {
						log.error(e.toString());
					}
				}
			} else if (lease.getLifecycleState().equals(
					LeaseLifeCycleState.PREPROCESSING)) {
				// work for lease preprocessing.
				log.info("The lease " + lease.getGuid() + " is preprocessing.");
				if (this.workForLeasePreprocessing(lease)) {
					LeaseLifeCycleMonitor.getInstance().addLeaseMonitoredOnce(
							lease);
				} else {
					lease.setLifecycleState(LeaseLifeCycleState.FAIL);
					try {
						// FIXME now, we only left the error handling to users
						// lazily, they can terminate or delete lease.
						this.lmi.update(this.leaseId, lease);
					} catch (Exception e) {
						log.error(e.toString());
					}
				}
			} else if (lease.getLifecycleState().equals(
					LeaseLifeCycleState.READY)) {
				// work for lease ready.
				log.info("The lease " + lease.getGuid() + " is ready.");
				if (this.workForLeaseReady(lease)) {
					LeaseLifeCycleMonitor.getInstance().addLeaseMonitoredOnce(
							lease);
				} else {
					lease.setLifecycleState(LeaseLifeCycleState.FAIL);
					try {
						// FIXME now, we only left the error handling to users
						// lazily, they can terminate or delete lease.
						this.lmi.update(this.leaseId, lease);
					} catch (Exception e) {
						log.error(e.toString());
						// throw e;
					}
				}
			} else if (lease.getLifecycleState().equals(
					LeaseLifeCycleState.EFFECTIVE)) {
				// work for lease active.
				log.info("The lease " + lease.getGuid() + " is active.");
				if (this.workForLeaseActive(lease)) {
					// FIXME Need to be checked and polling.
					// FIXME here we no need to use
					// addLeaseMonitoredWithFixedDelay
					// method, because every time the task will add himself to
					// be monitored. Xiaoyi Lu added at 2010-5-22.
					LeaseLifeCycleMonitor.getInstance().addLeaseMonitoredOnce(
							lease);
				} else {
					lease.setLifecycleState(LeaseLifeCycleState.FAIL);
					try {
						// FIXME now, we only left the error handling to users
						// lazily, they can terminate or delete lease.
						this.lmi.update(this.leaseId, lease);
					} catch (Exception e) {
						log.error(e.toString());
					}
				}
			} else {
				log.info("The lease " + lease.getGuid()
						+ " is in the state of "
						+ lease.getLifecycleState().toString()
						+ " and it is no need to be tracked.");
				LeaseLifeCycleMonitor.getInstance().removeMonitoredLease(
						lease.getGuid());
				return;
			}

		} catch (Throwable t) {
			// ignore the unexpected error.
			if (lease != null) {
				log.error("Error occurred when tracking the life cycle of "
						+ "the lease : " + lease.getGuid() + " due to : "
						+ t.toString());
				// TODO Error handle.
				lease.setLifecycleState(LeaseLifeCycleState.FAIL);
				try {
					this.lmi.update(this.leaseId, lease);
				} catch (Exception e) {
					log.error(e.toString());
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void cancelReservation(HashMap<String, String> assetIdAndTypeMap,
			String guid) {
		if (assetIdAndTypeMap == null || assetIdAndTypeMap.size() <= 0) {
			return;
		}

		AssetManagerImpl ami = new AssetManagerImpl();
		Iterator<String> it = assetIdAndTypeMap.keySet().iterator();
		while (it.hasNext()) {
			String assetid = it.next();
			// TODO could be improved by batch reservation for assets who have
			// same assetLeaser.
			try {
				Asset reserveAsset = ami.unReserve(assetid, leaseId);
				if (reserveAsset == null) {
					continue;
				}
				log.info("The asset " + reserveAsset.getName()
						+ " is reserved by the lease (" + leaseId + ").");
			} catch (Exception e) {
				log.error("Reservation error : " + e.toString());
				// Ignore this error.
				continue;
			}
		}
		return;
	}

	private boolean workForLeaseActive(Lease lease) {
		try {
			long begin = System.currentTimeMillis();

			HashMap<String, String> la = lease.getAssetIdAndTypeMap();
			if (la == null || la.isEmpty()) {
				String msg = "The lease " + lease.getGuid()
						+ " does not contain any assets," + " so terminate it.";
				
				this.lmi.terminate(lease.getGuid());
				VoalUtil.setLastErrorMessage4Lease(lmi, lease, msg);
				log.warn(msg);
				return true;
			}

			AssetManagerImpl ami = new AssetManagerImpl();
			Iterator<String> iterator = la.keySet().iterator();
			while (iterator != null && iterator.hasNext()) {
				String assetid = iterator.next();
				try {
					ami.handleActive(assetid, lease.getGuid());
				} catch (Exception e) {
					log.error("Ignore Error when handle active for the asset "
							+ assetid + " in the lease " + lease.getGuid()
							+ " " + lease.getName());
					// Ignore this error.
				}
			}
			// FIXME every loop, we will check expired, so here we do not check
			// termination condition. If user want to terminate a lease, he or
			// she should invoke LeaseManagerImpl.terminate method.

			long end = System.currentTimeMillis();
			log.info("workForLeaseActive lease " + lease.getGuid()
					+ " consuming time : " + (end - begin));
			return true;
		} catch (Exception e) {
			log.error("Error occurred in LeaseLifeCycleTracker"
					+ ".workForLeaseActive method, due to " + e);
			VoalUtil.setLastErrorMessage4Lease(lmi, lease, "Error "
					+ "occurred when the instance (" + lease.getGuid() + " "
					+ lease.getName() + ") in " + lease.getLifecycleState()
					+ " state, due to " + e.toString());
			return false;
		}
	}

	private boolean workForLeaseReady(Lease lease) {
		try {
			long begin = System.currentTimeMillis();
			synchronized (LeaseConstants.TERMINATE_LOCK) {
				Lease newest = this.lmi.view(lease.getGuid());
				if (newest.getLifecycleState() == LeaseLifeCycleState.READY) {
					HashMap<String, String> la = newest.getAssetIdAndTypeMap();
					if (la == null || la.isEmpty()) {
						String msg = "The lease " + lease.getGuid()
								+ " does not contain any assets,"
								+ " so terminate it.";
						this.lmi.terminate(newest.getGuid());
						VoalUtil.setLastErrorMessage4Lease(lmi, lease, msg);
						log.warn(msg);
						return true;
					}

					AssetManagerImpl ami = new AssetManagerImpl();
					Iterator<String> iterator = la.keySet().iterator();
					while (iterator != null && iterator.hasNext()) {
						String assetid = iterator.next();
						try {
							ami.handleReady(assetid, lease.getGuid());
						} catch (Exception e) {
							log.error("Ignore Error when handle ready for "
									+ "the asset " + assetid + " in the lease "
									+ lease.getGuid() + " " + lease.getName());
							// Ignore this error.
						}
					}
					newest.setLifecycleState(LeaseLifeCycleState.EFFECTIVE);
					this.lmi.update(this.leaseId, newest);
				} else {
					String msg = "The lease " + newest.getName()
							+ "'s state is "
							+ newest.getLifecycleState().toString()
							+ ", so it can not transfer to "
							+ LeaseLifeCycleState.EFFECTIVE.toString()
							+ " state.";
					VoalUtil.setLastErrorMessage4Lease(lmi, lease, msg);
					log.warn(msg);
					return false;
				}
			}
			long end = System.currentTimeMillis();
			log.info("workForLeaseReady lease " + lease.getGuid()
					+ " consuming time : " + (end - begin));
			return true;
		} catch (Exception e) {
			log.error("Error occurred in LeaseLifeCycleTracker"
					+ ".workForLeaseReady method, due to " + e);
			VoalUtil.setLastErrorMessage4Lease(lmi, lease, "Error "
					+ "occurred when the instance (" + lease.getGuid() + " "
					+ lease.getName() + ") in " + lease.getLifecycleState()
					+ " state, due to " + e.toString());
			return false;
		}
	}

	private boolean workForLeasePreprocessing(Lease lease) {
		try {
			long begin = System.currentTimeMillis();
			synchronized (LeaseConstants.TERMINATE_LOCK) {
				Lease newest = this.lmi.view(lease.getGuid());
				if (newest.getLifecycleState() 
						== LeaseLifeCycleState.PREPROCESSING) {
					HashMap<String, String> aidmap = newest
							.getAssetIdAndTypeMap();
					if (aidmap == null || aidmap.isEmpty()) {
						String msg = "The lease " + lease.getGuid()
								+ " does not contain any assets,"
								+ " so terminate it.";
						this.lmi.terminate(newest.getGuid());
						VoalUtil.setLastErrorMessage4Lease(lmi, lease, msg);
						log.warn(msg);
						return true;
					}
					
					AssetManagerImpl ami = new AssetManagerImpl();
					Iterator<String> it = aidmap.keySet().iterator();
					
					while (it.hasNext()) {
						
						String assetid = it.next();
						try {
							ami.handlePreprocessing(assetid, lease.getGuid());
						} catch (Exception e) {
							
							log.error("Error occurred when preprocessing "
									+ "the asset " + assetid
									+ " for the lease " + lease.getGuid() + " "
									+ lease.getName() + ". due to "
									+ e.toString());
							try {
								ami.handleAntiPreprocessing(assetid,
										lease.getGuid());
							} catch (Exception e2) {
								log.warn("Error occurred when "
										+ "anti-preprocessing the asset "
										+ assetid + " for the lease "
										+ lease.getGuid() + " "
										+ lease.getName() + ". due to "
										+ e2.toString());
								continue;
							}
							// Ignore this error.
						}
					}
					
					newest.setLifecycleState(LeaseLifeCycleState.READY);
					this.lmi.update(this.leaseId, newest);
				} else {
					String msg = "The lease " + newest.getName()
							+ "'s state is "
							+ newest.getLifecycleState().toString()
							+ ", so it can not transfer to "
							+ LeaseLifeCycleState.READY.toString() + " state.";
					VoalUtil.setLastErrorMessage4Lease(lmi, lease, msg);
					log.warn(msg);
					return false;
				}
			}
			long end = System.currentTimeMillis();
			log.info("workForLeasePreprocessing lease " + lease.getGuid()
					+ " consuming time : " + (end - begin));
			return true;
		} catch (Exception e) {
			log.error("Error occurred in LeaseLifeCycleTracker"
					+ ".workForLeasePreprocessing method, due to " + e);
			VoalUtil.setLastErrorMessage4Lease(lmi, lease, "Error "
					+ "occurred when the instance (" + lease.getGuid() + " "
					+ lease.getName() + ") in " + lease.getLifecycleState()
					+ " state, due to " + e.toString());
			return false;
		}
	}

	private boolean workForLeaseNegotiating(Lease lease) {
		try {
			long begin = System.currentTimeMillis();
			synchronized (LeaseConstants.TERMINATE_LOCK) {
				Lease newest = this.lmi.view(lease.getGuid());
				if (newest.getLifecycleState() 
						== LeaseLifeCycleState.NEGOTIATING) {
					// XXX in the vc, the inital assetIdAndType map only
					// contains the virtual network id or even null, and vc
					// require the assetMatchMaking to match the assets
					// satisfy the requirements. So we do not check the
					// assetIdAndType's size.
					
					try {
						String amm = newest.getAssetMatchMaker();

						AssetMatchMaker ammaker = (AssetMatchMaker) Class
								.forName(amm).newInstance();

						try {
							// XXX added "satiableDecision" function in asset
							// match maker at 2010-12-09 to decide the lease
							// whether or not can be satiable.
							ammaker.satiableDecision(newest);
							newest = ammaker.assetMatchMaking(newest);
							ammaker.validateMachedAssets(newest);

							Date et = newest.getEffectiveTime();
							Date ext = newest.getExpireTime();
		
							// TODO due to the virtual appliance is not
							// implemented
							// by ocl, so doReservation will throw exception.
							this.doReservation(newest.getAssetIdAndTypeMap(),
									newest.getGuid(), et, ext);
							newest.setLifecycleState(
									LeaseLifeCycleState.PREPROCESSING);
							this.lmi.update(this.leaseId, newest);
						} catch (Throwable t) {
							String msg = "Error occurred when match making"
									+ " assets for the lease ("
									+ newest.getGuid() + " " + newest.getName()
									+ "), due to " + t.toString();
							log.warn(msg);
							Lease erlease = ammaker.matchErrorHandler(newest);
							erlease.setLifecycleState(
									LeaseLifeCycleState.REJECTED);
							erlease.setLastErrorMessage(msg);
							this.lmi.update(lease.getGuid(), erlease);
							return true;
						}
					} catch (Exception e) {
						String msg = "AssetMatchMaking errored for the lease "
								+ lease.getGuid() + ", due to " + e.toString();
						VoalUtil.setLastErrorMessage4Lease(lmi, lease, msg);
						log.error(msg);
						return false;
					}
				} else {
					String msg = "The lease " + newest.getName()
							+ "'s state is "
							+ newest.getLifecycleState().toString()
							+ ", so it can not transfer to "
							+ LeaseLifeCycleState.PREPROCESSING.toString()
							+ " state.";
					VoalUtil.setLastErrorMessage4Lease(lmi, lease, msg);
					log.warn(msg);
					return false;
				}
			}
			long end = System.currentTimeMillis();
			log.info("workForLeaseNegotiating lease " + lease.getGuid()
					+ " consuming time : " + (end - begin));
			return true;
		} catch (Exception e) {
			log.error("Error occurred in LeaseLifeCycleTracker"
					+ ".workForLeaseNegotiating method, due to " + e);
			VoalUtil.setLastErrorMessage4Lease(lmi, lease, "Error "
					+ "occurred when the instance (" + lease.getGuid() + " "
					+ lease.getName() + ") in " + lease.getLifecycleState()
					+ " state, due to " + e.toString());
			return false;
		}
	}

	private void doReservation(HashMap<String, String> matchedAssets,
			String leaseId, Date reserveEffectiveTime, Date reserveExpireTime) {
		if (matchedAssets == null || matchedAssets.size() <= 0) {
			return;
		}

		AssetManagerImpl ami = new AssetManagerImpl();
		Iterator<String> it = matchedAssets.keySet().iterator();
		while (it.hasNext()) {
			String assetid = it.next();
			// TODO could be improved by batch reservation for assets who have
			// same assetLeaser.
			try {
				Asset reserveAsset = ami.reserve(assetid, leaseId, null, null);
				if (reserveAsset == null) {
					continue;
				}
				log.info("The asset " + reserveAsset.getName()
						+ " is reserved by the lease (" + leaseId + ").");
			} catch (Exception e) {
				log.error("Reservation error : " + e.toString());
				// Ignore this error.
				continue;
			}
		}
		return;
	}

	private boolean workForLeasePending(Lease lease) {
		try {
			long begin = System.currentTimeMillis();
			synchronized (LeaseConstants.CANCEL_LOCK) {
				// get the latest object from data base to avoid the cancel
				// operation update db currently. Xiaoyi Lu modified at
				// 2010-07-22.
				Lease newest = this.lmi.view(lease.getGuid());
				if (newest.getLifecycleState() == LeaseLifeCycleState.PENDING) {
					lease.setLifecycleState(LeaseLifeCycleState.NEGOTIATING);
					this.lmi.update(this.leaseId, lease);
				} else {
					String msg = "The lease " + newest.getName()
							+ "'s state is "
							+ newest.getLifecycleState().toString()
							+ ", so it can not transfer to "
							+ LeaseLifeCycleState.NEGOTIATING.toString()
							+ " state.";
					VoalUtil.setLastErrorMessage4Lease(lmi, lease, msg);
					log.warn(msg);
					return false;
				}
			}
			long end = System.currentTimeMillis();
			log.info("workForLeasePending lease " + lease.getGuid()
					+ " consuming time : " + (end - begin));
			return true;
		} catch (Exception e) {
			log.error("Error occurred in LeaseLifeCycleTracker"
					+ ".workForLeasePending method, due to " + e);
			VoalUtil.setLastErrorMessage4Lease(lmi, lease, "Error "
					+ "occurred when the instance (" + lease.getGuid() + " "
					+ lease.getName() + ") in " + lease.getLifecycleState()
					+ " state, due to " + e.toString());
			return false;
		}
	}
}
