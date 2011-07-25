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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.lingcloud.molva.ocl.asset.AssetManagerImpl;
import org.lingcloud.molva.ocl.lease.Lease;
import org.lingcloud.molva.ocl.lease.LeaseManagerImpl;

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
	private static LeaseManagerImpl leaseManager = null;
	private static Lease lease = null;
	
	@BeforeClass
	public static void initializeForAllTest() {
		leaseManager = new LeaseManagerImpl();
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
			leaseManager.remove(lease.getGuid());
		}
	}
	
	@Ignore
	@Test
	public void add() {
		
	}
	
	@Ignore
	@Test
	public void cancel() {
		
	}
	
	@Ignore
	@Test
	public void remove() {
		
	}
	
	@Ignore
	@Test
	public void search() {
		
	}
	
	@Ignore
	@Test
	public void terminate() {
		
	}
	
	@Ignore
	@Test
	public void update() {
		
	}
	
	@Ignore
	@Test
	public void view() {
		
	}
}
