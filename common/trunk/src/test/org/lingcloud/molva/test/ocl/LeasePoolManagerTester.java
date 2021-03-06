/*
 *  @(#)LeasePoolManagerTester.java  Jul 25, 2011
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
import static org.junit.Assert.fail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lingcloud.molva.ocl.lease.Lease;

import org.lingcloud.molva.ocl.lease.LeasePoolManager;
import org.lingcloud.molva.ocl.util.HashFunction;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2011-7-25<br>
 * @author Maosen Sun<br>
 * 
 */
public class LeasePoolManagerTester {
	private static Log log = LogFactory.getLog(LeasePoolManagerTester.class);
	private static Lease lease = null;

	@BeforeClass
	public static void initializeForAllTest() {

	}

	@AfterClass
	public static void destroyForAllTest() {
	}

	@Before
	public void initialize() throws Exception {
		lease = null;
	}

	@After
	public void destroy() throws Exception {
		if (lease != null) {
			LeasePoolManager.removeLease(lease);
		}
	}

	@Test
	public void getLease() {
		log.info("begin getLease...");
		try {
			Lease lease = new Lease();
			if (lease.getGuid() == null || "".equals(lease.getGuid())) {
				String guid = HashFunction.createGUID().toString();
				lease.setGuid(guid);
			}
			LeasePoolManager.putLease(lease);
			assertNotNull(LeasePoolManager.getLease(lease));
			log.info("getlease success");

		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void putLease() {
		log.info("begin putLease...");
		try {
			lease = new Lease();
			if (lease.getGuid() == null || "".equals(lease.getGuid())) {
				String guid = HashFunction.createGUID().toString();
				lease.setGuid(guid);
			}
			LeasePoolManager.putLease(lease);
			assertNotNull(LeasePoolManager.getLease(lease));
			log.info("putlease success");

		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void removeLease() {
		log.info("begin removeLease...");
		try {
			lease = new Lease();
			if (lease.getGuid() == null || "".equals(lease.getGuid())) {
				String guid = HashFunction.createGUID().toString();
				lease.setGuid(guid);
			}
			LeasePoolManager.putLease(lease);
			LeasePoolManager.removeLease(lease);
			assertNull(LeasePoolManager.getLease(lease));
			log.info("removelease success");

		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}
}
