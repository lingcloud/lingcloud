/*
 *  @(#)LeaseLifeCycleMonitor.java  2010-5-11
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
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.ocl.lease.LeaseConstants.LeaseLifeCycleState;
import org.lingcloud.molva.ocl.util.TimeServerManager;
import org.lingcloud.molva.ocl.util.VoalUtil;

/**
 * <strong>Purpose:</strong><br>
 * Manage the whole lifecycles of all leases.
 * 
 * @version 1.0.1 2010-5-11<br>
 * @author Xiaoyi Lu<br>
 */

public class LeaseLifeCycleMonitor {
	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(LeaseLifeCycleMonitor.class);

	private static LeaseLifeCycleMonitor llcmSingleton 
		= new LeaseLifeCycleMonitor();

	private static final int MAX_THREADS = 40;

	public static final long IMMEDIATELY_DELAY = 0; // milliseconds

	public static final long DEFAULT_INIT_DELAY = 30 * 1000; // milliseconds

	public static final long DEFAULT_DELAY = 2 * 60 * 1000; // milliseconds

	private static final long DEFAULT_BE_INIT_DELAY = 5 * 1000;

	private static Map<String, ScheduledFuture<?>> futures;

	private ScheduledExecutorService threadPool = Executors
			.newScheduledThreadPool(MAX_THREADS);

	private static LeaseManagerImpl lmi = null;

	private LeaseLifeCycleMonitor() {
		try {
			lmi = new LeaseManagerImpl();
			futures = new HashMap<String, ScheduledFuture<?>>();
			load();
		} catch (Exception e) {
			log.error("Construct LeaseManagerImpl error : " + e.toString());
			throw new RuntimeException(e.toString());
		}
	}

	private void load() throws Exception {
		List<Lease> lelist = lmi.search(null, null, null);
		if (lelist == null || lelist.size() == 0) {
			return;
		}
		log.info("The system is started, and loads " + lelist.size()
				+ " leases.");
		for (int i = 0; i < lelist.size(); i++) {
			Lease le = lelist.get(i);
			if (this.addLeaseMonitoredOnce(le)) {
				log.debug("The lease with id : " + le.getGuid()
						+ " is added to be monitored.");
			} else {
				log.debug("The lease with id : " + le.getGuid()
						+ " is not added to be monitored.");
			}
		}
	}

	public static LeaseLifeCycleMonitor getInstance() {
		return LeaseLifeCycleMonitor.llcmSingleton;
	}

	public synchronized boolean addLeaseMonitoredOnce(Lease lease) {
		// FIXME the following state will be no monitored.
		if (lease == null) {
			return false;
		}
		if (checkIsNeededToBeMonitored(lease)) {
			String id = lease.getGuid();
			// FIXME make sure every lease has only one response task.
			if (futures.containsKey(id)) {
				ScheduledFuture<?> future = futures.get(id);
				if (future != null) {
					future.cancel(false);
				}
				futures.remove(id);
			}

			LeaseLifeCycleTracker llct = new LeaseLifeCycleTracker(
					lease.getGuid());
			Date now = TimeServerManager.getCurrentTime();
			long initialDelay = this.analyzeInitialDelayOfLease(lease, now);
			ScheduledFuture<?> future = threadPool.schedule(llct, initialDelay,
					TimeUnit.MILLISECONDS);
			futures.put(id, future);
			// lease.getEffectiveTime() and lease.getExpireTime() may be null,
			// so can not toString.
			log.info("A lease : ( " 
					+ lease.getGuid() + " state=" 
					+ lease.getLifecycleState() + " effectiveTime="
					+ lease.getEffectiveTime() + " expireTime="
					+ lease.getExpireTime() + " duration="
					+ lease.getDuration()
					+ " ) is monitored once : initialDelay=" + initialDelay
					+ " TimeUnit=MILLISECONDS.");
			return true;
		} else {
			return false;
		}
	}

	public synchronized boolean addLeaseMonitoredWithFixedDelay(Lease lease) {
		// FIXME the following state will be no monitored.
		if (lease == null) {
			return false;
		}
		if (checkIsNeededToBeMonitored(lease)) {
			String id = lease.getGuid();
			// FIXME make sure every lease has only one response task.
			if (futures.containsKey(id)) {
				ScheduledFuture<?> future = futures.get(id);
				if (future != null) {
					future.cancel(false);
				}
				futures.remove(id);
			}

			LeaseLifeCycleTracker llct = new LeaseLifeCycleTracker(
					lease.getGuid());
			Date now = TimeServerManager.getCurrentTime();
			long initDelay = this.analyzeInitialDelayOfLease(lease, now);
			long delay = this.analyzeDelayOfLease(lease, now);
			ScheduledFuture<?> future = threadPool.scheduleWithFixedDelay(llct,
					initDelay, delay, TimeUnit.MILLISECONDS);
			futures.put(id, future);
			log.info("A lease : ( " + lease.getGuid() + " effectiveTime="
					+ lease.getEffectiveTime().toString() + " expireTime="
					+ lease.getExpireTime().toString() + " duration="
					+ lease.getDuration()
					+ " ) is monitored by following rate : " + "initialDelay="
					+ initDelay + " delay=" + delay 
					+ " TimeUnit=MILLISECONDS.");
			return true;
		} else {
			return false;
		}
	}

	private boolean checkIsNeededToBeMonitored(Lease lease) {
		// FIXME now we only check the state.
		LeaseLifeCycleState llcs = lease.getLifecycleState();
		if (llcs.equals(LeaseLifeCycleState.CANCELLED)
				|| llcs.equals(LeaseLifeCycleState.EXPIRED)
				|| llcs.equals(LeaseLifeCycleState.FAIL)
				|| llcs.equals(LeaseLifeCycleState.REJECTED)
				|| llcs.equals(LeaseLifeCycleState.TERMINATION)) {
			return false;
		}
		return true;
	}

	public synchronized void removeMonitoredLease(String id) {
		ScheduledFuture<?> future = futures.get(id);
		if (future != null) {
			future.cancel(false);
		}
		futures.remove(id);
	}

	private long analyzeDelayOfLease(Lease lease, Date now) {
		// TODO we need to improve this method.
		return DEFAULT_DELAY;
	}

	protected long analyzeInitialDelayOfLease(Lease lease, Date now) {
		// FIXME here is the case of load when restart system.
		// TODO is need to reschedule? Different stages may have different init
		// delay time.
		Date effectiveTime = lease.getEffectiveTime();
		Date expireTime = lease.getExpireTime();
		long duration = lease.getDuration();

		if (lease.getLifecycleState().equals(LeaseLifeCycleState.READY)) {
			// FIXME READY Lease
			if (effectiveTime != null && effectiveTime.after(now)) {
				return VoalUtil.getGapMilliSecondsBetweenTimes(now,
						effectiveTime);
			} else if (effectiveTime != null
					&& effectiveTime.compareTo(now) <= 0) {
				return IMMEDIATELY_DELAY;
			} else { // effectiveTime == null
				// TODO need to be improved for Best Effort Lease.
				return IMMEDIATELY_DELAY;
			}
		} else if (lease.getLifecycleState().equals(
				LeaseLifeCycleState.EFFECTIVE)) {
			// FIXME EFFECTIVE Lease. Here is not DEFAULT_INIT_DELAY, but
			// DEFAULT_DELAY due to the LifeCycleTracker will track himself over
			// and over again.
			return DEFAULT_DELAY;
		} else if (lease.getLifecycleState()
				.equals(LeaseLifeCycleState.PENDING)) {
			// FIXME NEW lease.
			// FIXME due to all leases have been validated, so following lines
			// are safe.
			// BestEffort Lease.
			if (effectiveTime == null && expireTime == null) {
				return getBELeaseInitialDelay(duration, now);
			}
			// Deadline stoping Lease.
			if (effectiveTime == null && expireTime != null) {
				return getDStopLeaseInitialDelay(duration, expireTime, now);
			}
			// Deadline starting Lease.
			if (effectiveTime != null && expireTime == null) {
				return getDStartLeaseInitialDelay(duration, effectiveTime, now);
			}

			// AR Lease.
			// FIXME here we adopt AR Lease first policy if all of
			// effectiveTime,
			// expireTime, and duration are legal values.
			if (effectiveTime != null && expireTime != null) {
				return getARLeaseInitialDelay(effectiveTime, expireTime, now);
			}
			return DEFAULT_INIT_DELAY;
		} else {
			return IMMEDIATELY_DELAY;
		}
	}

	private long getDStartLeaseInitialDelay(long duration, Date effectiveTime,
			Date now) {
		// TODO should be improved.
		if (effectiveTime.after(now)) {
			long gap = VoalUtil.getGapMilliSecondsBetweenTimes(now,
					effectiveTime);
			// TODO we need to analyze a perfect time to lauch the lease.
			if (gap > DEFAULT_INIT_DELAY) {
				return (gap - DEFAULT_INIT_DELAY);
			} else {
				return IMMEDIATELY_DELAY;
			}
		} else {
			// FIXME Schedule immediately.
			return IMMEDIATELY_DELAY;
		}
	}

	private long getARLeaseInitialDelay(Date effectiveTime, Date expireTime,
			Date now) {
		// AR Lease.
		if (effectiveTime.after(now)) {
			long gap = VoalUtil.getGapMilliSecondsBetweenTimes(now,
					effectiveTime);
			// TODO we need to analyze a perfect time to lauch the lease.
			if (gap > DEFAULT_INIT_DELAY) {
				return (gap - DEFAULT_INIT_DELAY);
			} else {
				return IMMEDIATELY_DELAY;
			}
		} else {
			// FIXME Schedule immediately.
			return IMMEDIATELY_DELAY;
		}
	}

	private long getDStopLeaseInitialDelay(long duration, Date expireTime,
			Date now) {
		// TODO should be improved.
		if (expireTime.after(now)) {
			long gap = VoalUtil.getGapMilliSecondsBetweenTimes(now, expireTime);
			// TODO we need to analyze a perfect time to lauch the lease.
			// FIXME here we consider the lease is un-preemptible. Next time, we
			// should consider the preemptible lease.
			if ((gap - duration) > DEFAULT_INIT_DELAY) {
				return (gap - duration - DEFAULT_INIT_DELAY);
			} else {
				return IMMEDIATELY_DELAY;
			}
		} else {
			// FIXME Schedule immediately.
			return IMMEDIATELY_DELAY;
		}
	}

	private long getBELeaseInitialDelay(long duration, Date now) {
		// XXX should be improved.
		Random rand = new Random(System.currentTimeMillis());
		final int three = 3;
		if (Math.abs(rand.nextInt(Integer.MAX_VALUE)) % three == 1) {
			return IMMEDIATELY_DELAY;
		} else {
			return DEFAULT_BE_INIT_DELAY;
		}
	}
}
