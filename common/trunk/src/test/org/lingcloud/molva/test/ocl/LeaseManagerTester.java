/*
 *  @(#)LeaseManagerTester.java  Jul 25, 2011
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
package org.lingcloud.molva.test.ocl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetManagerImpl;
import org.lingcloud.molva.ocl.asset.AssetConstants.AssetState;
import org.lingcloud.molva.ocl.lease.Lease;
import org.lingcloud.molva.ocl.lease.LeaseConstants.LeaseLifeCycleState;
import org.lingcloud.molva.ocl.lease.LeaseManagerImpl;
import org.lingcloud.molva.test.ocl.example.Computer;
import org.lingcloud.molva.test.ocl.example.DeviceFactory;
import org.lingcloud.molva.test.ocl.example.Mainframe;
import org.lingcloud.molva.test.ocl.example.Monitor;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2011-7-25<br>
 * @author Ruijian Wang<br>
 * 
 */
public class LeaseManagerTester {
	private static Log log = LogFactory.getLog(LeaseManagerTester.class);
	private static final int MAX_TRY_TIMES = 40;
	private static final int SLEEP_TIME = 3000;
	private static final int DURATION = 1000000000;
	private static final int MINUTE = 60000;
	private static LeaseManagerImpl leaseManager = null;
	private static AssetManagerImpl assetManager = null;
	private static Lease lease = null;
	private static Lease tmpLease = null;
	private static Monitor monitor1 = null;
	private static Monitor monitor2 = null;
	private static Monitor monitor3 = null;
	private static Mainframe mainframe1 = null;
	private static Mainframe mainframe2 = null;
	private static Mainframe mainframe3 = null;

	@BeforeClass
	public static void initializeForAllTest() {
		log.info("initializeForAllTest...");
		if (System.getProperty("lingcloud.home") == null) {
			System.getProperties().put("lingcloud.home", ".");
		}

		leaseManager = new LeaseManagerImpl();
		assetManager = new AssetManagerImpl();

		monitor1 = new Monitor("monitor1", "BrandA", "14");
		monitor2 = new Monitor("monitor2", "BrandB", "17");
		monitor3 = new Monitor("monitor3", "BrandA", "17");
		mainframe1 = new Mainframe("mainframe1", "BrandA", "2G");
		mainframe2 = new Mainframe("mainframe2", "BrandA", "4G");
		mainframe3 = new Mainframe("mainframe3", "BrandB", "4G");

		try {
			monitor1 = new Monitor(assetManager.add(monitor1, false));
			monitor2 = new Monitor(assetManager.add(monitor2, false));
			monitor3 = new Monitor(assetManager.add(monitor3, false));
			mainframe1 = new Mainframe(assetManager.add(mainframe1, false));
			mainframe2 = new Mainframe(assetManager.add(mainframe2, false));
			mainframe3 = new Mainframe(assetManager.add(mainframe3, false));
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		DeviceFactory.getInstance().addDevice(monitor1);
		DeviceFactory.getInstance().addDevice(monitor2);
		DeviceFactory.getInstance().addDevice(monitor3);
		DeviceFactory.getInstance().addDevice(mainframe1);
		DeviceFactory.getInstance().addDevice(mainframe2);
		DeviceFactory.getInstance().addDevice(mainframe3);

	}

	@AfterClass
	public static void destroyForAllTest() {
		log.info("destroyForAllTest...");
		List<Asset> devices = DeviceFactory.getInstance().getAllDevices();
		for (Asset asset : devices) {
			try {
				if (asset.getGuid() != null) {
					assetManager.remove(asset.getGuid(), true);
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}

	@Before
	public void initialize() {
		log.info("initialize...");
		lease = null;
		tmpLease = null;
	}

	@After
	public void destroy() {
		log.info("destroy...");
		if (lease != null) {
			try {
				leaseManager.terminate(lease.getGuid());
				leaseManager.remove(lease.getGuid());
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}
		
		if (tmpLease != null) {
			try {
				leaseManager.terminate(tmpLease.getGuid());
				leaseManager.remove(tmpLease.getGuid());
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}

		List<Asset> devices = DeviceFactory.getInstance().getAllDevices();
		for (Asset asset : devices) {
			try {
				if (asset.getGuid() != null) {
					DeviceFactory.getInstance().setStateOfDevice(
							asset.getGuid(), AssetState.IDLE);
					Asset realAsset = assetManager.view(asset.getGuid());
					if (realAsset != null
							&& realAsset.getAssetState() != AssetState.IDLE) {
						realAsset.setAssetState(AssetState.IDLE);
						realAsset.setLeaseId(null);
						
						assetManager.update(realAsset.getGuid(), realAsset);
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}

	}

	private Computer createOneComputer() {
		Computer computer = new Computer();
		computer.setName("Computer1");
		computer.setMonitorBrand(monitor2.getBrand());
		computer.setMonitorSize(monitor2.getSize());
		computer.setMainframeBrand(mainframe1.getBrand());
		computer.setMainframeMemory(mainframe1.getMemory());
		return computer;
	}

	private boolean inStates(LeaseLifeCycleState[] states,
			LeaseLifeCycleState checkState) {
		for (LeaseLifeCycleState state : states) {
			if (state == checkState) {
				return true;
			}
		}
		return false;
	}

	private void checkState(Lease lease, int maxTryTimes, int unitTime,
			LeaseLifeCycleState[] success, LeaseLifeCycleState[] fail)
			throws Exception {
		int tryTimes = 0;
		while (tryTimes < maxTryTimes) {
			Lease l = leaseManager.view(lease.getGuid());
			LeaseLifeCycleState llfs = l.getLifecycleState();

			if (inStates(success, llfs)) {
				break;
			} else if (inStates(fail, llfs)) {
				fail();
			}
			tryTimes++;
			Thread.sleep(unitTime);
		}

		if (tryTimes >= maxTryTimes) {
			fail();
		}
	}

	@Test
	public void add() {
		log.info("begin add...");
		try {
			Computer computer = createOneComputer();
			computer.setDuration(DURATION);

			lease = leaseManager.add(computer);

			assertNotNull(lease);

			checkState(
					lease,
					MAX_TRY_TIMES,
					SLEEP_TIME,
					new LeaseLifeCycleState[] { LeaseLifeCycleState.EFFECTIVE },
					new LeaseLifeCycleState[] { LeaseLifeCycleState.FAIL,
							LeaseLifeCycleState.REJECTED,
							LeaseLifeCycleState.CANCELLED,
							LeaseLifeCycleState.EXPIRED,
							LeaseLifeCycleState.TERMINATION });

			computer = new Computer(leaseManager.view(lease.getGuid()));

			Monitor monitor = new Monitor(assetManager.view(computer
					.getMonitorId()));
			Mainframe mainframe = new Mainframe(assetManager.view(computer
					.getMainframeId()));

			if (monitor.getAssetState() != AssetState.LEASED
					|| mainframe.getAssetState() != AssetState.LEASED) {
				fail();
			}

		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void cancel() {
		log.info("begin cancel...");
		try {
			Computer computer = createOneComputer();
			GregorianCalendar now = new GregorianCalendar();
			now.add(Calendar.HOUR, 1);
			computer.setEffectiveTime(now.getTime());
			computer.setDuration(DURATION);

			lease = leaseManager.add(computer);

			assertNotNull(lease);

			leaseManager.cancel(lease.getGuid());

			Lease cancelledLease = leaseManager.view(lease.getGuid());

			LeaseLifeCycleState llfs = cancelledLease.getLifecycleState();
			if (llfs != LeaseLifeCycleState.CANCELLED) {
				fail();
			}
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void remove() {
		log.info("begin remove...");
		try {
			Computer computer = createOneComputer();
			computer.setDuration(DURATION);

			lease = leaseManager.add(computer);

			assertNotNull(lease);

			checkState(lease, MAX_TRY_TIMES, SLEEP_TIME,
					new LeaseLifeCycleState[] {
							LeaseLifeCycleState.PREPROCESSING,
							LeaseLifeCycleState.READY,
							LeaseLifeCycleState.EFFECTIVE },
					new LeaseLifeCycleState[] { LeaseLifeCycleState.FAIL,
							LeaseLifeCycleState.REJECTED,
							LeaseLifeCycleState.CANCELLED,
							LeaseLifeCycleState.EXPIRED,
							LeaseLifeCycleState.TERMINATION });

			computer = new Computer(leaseManager.view(lease.getGuid()));

			leaseManager.terminate(lease.getGuid());
			leaseManager.remove(lease.getGuid());

			Lease removedLease = leaseManager.view(lease.getGuid());
			assertNull(removedLease);

			lease = null;

			Monitor monitor = new Monitor(assetManager.view(computer
					.getMonitorId()));
			Mainframe mainframe = new Mainframe(assetManager.view(computer
					.getMainframeId()));

			if (monitor.getAssetState() != AssetState.IDLE
					|| mainframe.getAssetState() != AssetState.IDLE) {
				fail();
			}
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void search() {
		log.info("begin search...");
		try {
			Computer computer = createOneComputer();
			computer.setDescription("This is computer 1.");
			GregorianCalendar now = new GregorianCalendar();
			now.add(Calendar.HOUR, 1);
			computer.setEffectiveTime(now.getTime());
			computer.setDuration(DURATION);

			lease = leaseManager.add(computer);

			assertNotNull(lease);

			String[] fields = new String[] { "name", "description" };
			String[] operators = new String[] { "=", "=" };
			String[] values = new String[] { computer.getName(),
					computer.getDescription() };

			List<Lease> results = leaseManager
					.search(fields, operators, values);

			assertNotNull(results);
			assertTrue(results.size() >= 1);

			LeaseLifeCycleState llfs = results.get(0).getLifecycleState();
			if (llfs != LeaseLifeCycleState.PENDING) {
				fail();
			}
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void terminate() {
		log.info("begin terminate...");
		try {
			Computer computer = createOneComputer();
			GregorianCalendar now = new GregorianCalendar();
			now.add(Calendar.HOUR, 1);
			computer.setEffectiveTime(now.getTime());
			computer.setDuration(DURATION);

			lease = leaseManager.add(computer);

			assertNotNull(lease);

			leaseManager.terminate(lease.getGuid());

			Lease terminatedLease = leaseManager.view(lease.getGuid());

			LeaseLifeCycleState llfs = terminatedLease.getLifecycleState();
			if (llfs != LeaseLifeCycleState.TERMINATION) {
				fail();
			}
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void update() {
		log.info("begin update...");
		try {
			Computer computer = createOneComputer();
			GregorianCalendar now = new GregorianCalendar();
			now.add(Calendar.HOUR, 1);
			computer.setEffectiveTime(now.getTime());
			computer.setDuration(DURATION);

			lease = leaseManager.add(computer);

			assertNotNull(lease);

			lease.setName("computer2");
			lease.setDescription("This is computer 2.");

			Lease updatedLease = leaseManager.update(lease.getGuid(), lease);

			assertNotNull(updatedLease);
			assertTrue(updatedLease.getName().equals(lease.getName()));
			assertTrue(updatedLease.getDescription().equals(
					lease.getDescription()));

			LeaseLifeCycleState llfs = updatedLease.getLifecycleState();
			if (llfs != LeaseLifeCycleState.PENDING) {
				fail();
			}
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void view() {
		log.info("begin view...");
		try {
			Computer computer = createOneComputer();
			GregorianCalendar now = new GregorianCalendar();
			now.add(Calendar.HOUR, 1);
			computer.setEffectiveTime(now.getTime());
			computer.setDuration(DURATION);

			lease = leaseManager.add(computer);

			assertNotNull(lease);

			Lease viewedLease = leaseManager.view(lease.getGuid());

			assertNotNull(viewedLease);

			LeaseLifeCycleState llfs = viewedLease.getLifecycleState();
			if (llfs != LeaseLifeCycleState.PENDING) {
				fail();
			}
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testRejectLease() {
		log.info("begin testRejectLease...");
		try {
			Computer computer1 = createOneComputer();
			computer1.setDuration(DURATION);

			lease = leaseManager.add(computer1);
			computer1 = new Computer(lease);

			assertNotNull(lease);

			checkState(lease, MAX_TRY_TIMES, SLEEP_TIME,
					new LeaseLifeCycleState[] {
							LeaseLifeCycleState.PREPROCESSING,
							LeaseLifeCycleState.READY,
							LeaseLifeCycleState.EFFECTIVE },
					new LeaseLifeCycleState[] { LeaseLifeCycleState.FAIL,
							LeaseLifeCycleState.REJECTED,
							LeaseLifeCycleState.CANCELLED,
							LeaseLifeCycleState.EXPIRED,
							LeaseLifeCycleState.TERMINATION });

			computer1 = new Computer(leaseManager.view(lease.getGuid()));

			Monitor monitor = new Monitor(assetManager.view(computer1
					.getMonitorId()));
			Mainframe mainframe = new Mainframe(assetManager.view(computer1
					.getMainframeId()));

			DeviceFactory.getInstance().setStateOfDevice(monitor.getGuid(),
					monitor.getAssetState());
			DeviceFactory.getInstance().setStateOfDevice(mainframe.getGuid(),
					monitor.getAssetState());

			Computer computer2 = new Computer();
			computer2.setName("Computer2");
			computer2.setMonitorBrand(monitor2.getBrand());
			computer2.setMonitorSize(monitor2.getSize());
			computer2.setMainframeBrand(mainframe2.getBrand());
			computer2.setMainframeMemory(mainframe2.getMemory());
			computer2.setDuration(DURATION);

			Lease newLease = leaseManager.add(computer2);
			
			assertNotNull(newLease);
			tmpLease = newLease;

			checkState(newLease, MAX_TRY_TIMES, SLEEP_TIME,
					new LeaseLifeCycleState[] { LeaseLifeCycleState.REJECTED },
					new LeaseLifeCycleState[] { LeaseLifeCycleState.FAIL,
							LeaseLifeCycleState.CANCELLED,
							LeaseLifeCycleState.EXPIRED,
							LeaseLifeCycleState.TERMINATION,
							LeaseLifeCycleState.PREPROCESSING,
							LeaseLifeCycleState.READY,
							LeaseLifeCycleState.EFFECTIVE });

			Lease rejectedLease = leaseManager.view(newLease.getGuid());
			assertNotNull(rejectedLease);
			
			leaseManager.remove(newLease.getGuid());
			tmpLease = null;
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testEffectiveTime() {
		log.info("begin testEffectiveTime...");
		try {
			Computer computer = createOneComputer();
			GregorianCalendar now = new GregorianCalendar();
			now.add(Calendar.MINUTE, 1);
			computer.setEffectiveTime(now.getTime());
			computer.setDuration(DURATION);

			lease = leaseManager.add(computer);

			assertNotNull(lease);

			Thread.sleep(MINUTE / 2);

			Lease l = leaseManager.view(lease.getGuid());
			assertNotNull(l);
			assertTrue(l.getLifecycleState() == LeaseLifeCycleState.PENDING);

			Thread.sleep(MINUTE / 2);

			checkState(
					lease,
					MAX_TRY_TIMES,
					SLEEP_TIME,
					new LeaseLifeCycleState[] { LeaseLifeCycleState.EFFECTIVE },
					new LeaseLifeCycleState[] { LeaseLifeCycleState.FAIL,
							LeaseLifeCycleState.REJECTED,
							LeaseLifeCycleState.CANCELLED,
							LeaseLifeCycleState.EXPIRED,
							LeaseLifeCycleState.TERMINATION });

			l = leaseManager.view(lease.getGuid());
			assertNotNull(l);
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testExpireTime() {
		log.info("begin testExpireTime...");
		try {
			Computer computer = createOneComputer();
			GregorianCalendar now = new GregorianCalendar();
			now.add(Calendar.MINUTE, 1);
			computer.setExpireTime(now.getTime());
			computer.setDuration(MINUTE);

			lease = leaseManager.add(computer);

			assertNotNull(lease);

			checkState(lease, MAX_TRY_TIMES, SLEEP_TIME,
					new LeaseLifeCycleState[] { LeaseLifeCycleState.EFFECTIVE,
							LeaseLifeCycleState.EXPIRED },
					new LeaseLifeCycleState[] { LeaseLifeCycleState.FAIL,
							LeaseLifeCycleState.REJECTED,
							LeaseLifeCycleState.CANCELLED,
							LeaseLifeCycleState.TERMINATION });

			Lease l = leaseManager.view(lease.getGuid());
			assertNotNull(l);

			if (l.getLifecycleState() == LeaseLifeCycleState.EFFECTIVE) {
				checkState(
						lease,
						MAX_TRY_TIMES,
						SLEEP_TIME,
						new LeaseLifeCycleState[] { 
								LeaseLifeCycleState.EXPIRED },
						new LeaseLifeCycleState[] { LeaseLifeCycleState.FAIL,
								LeaseLifeCycleState.REJECTED,
								LeaseLifeCycleState.CANCELLED,
								LeaseLifeCycleState.TERMINATION });

				l = leaseManager.view(lease.getGuid());
				assertNotNull(l);
			}
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testEffectiveTimeAndExpireTime() {
		log.info("begin testEffectiveTimeAndExpireTime...");
		try {
			Computer computer = createOneComputer();
			GregorianCalendar now = new GregorianCalendar();
			now.add(Calendar.MINUTE, 1);
			computer.setEffectiveTime(now.getTime());
			now.add(Calendar.MINUTE, 1);
			computer.setExpireTime(now.getTime());

			lease = leaseManager.add(computer);

			assertNotNull(lease);

			Thread.sleep(MINUTE / 2);

			Lease l = leaseManager.view(lease.getGuid());
			assertNotNull(l);
			assertTrue(l.getLifecycleState() == LeaseLifeCycleState.PENDING);

			Thread.sleep(MINUTE / 2);

			checkState(
					lease,
					MAX_TRY_TIMES,
					SLEEP_TIME,
					new LeaseLifeCycleState[] { LeaseLifeCycleState.EFFECTIVE },
					new LeaseLifeCycleState[] { LeaseLifeCycleState.FAIL,
							LeaseLifeCycleState.REJECTED,
							LeaseLifeCycleState.CANCELLED,
							LeaseLifeCycleState.EXPIRED,
							LeaseLifeCycleState.TERMINATION });

			l = leaseManager.view(lease.getGuid());
			assertNotNull(l);

			checkState(lease, MAX_TRY_TIMES * 2, SLEEP_TIME,
					new LeaseLifeCycleState[] { LeaseLifeCycleState.EXPIRED },
					new LeaseLifeCycleState[] { LeaseLifeCycleState.FAIL,
							LeaseLifeCycleState.REJECTED,
							LeaseLifeCycleState.CANCELLED,
							LeaseLifeCycleState.TERMINATION });

			l = leaseManager.view(lease.getGuid());
			assertNotNull(l);
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}
}
