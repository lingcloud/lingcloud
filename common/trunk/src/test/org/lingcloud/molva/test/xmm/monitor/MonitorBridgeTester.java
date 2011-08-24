/*
 *  @(#)MonitorBridgeTester.java  Jul 23, 2011
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
package org.lingcloud.molva.test.xmm.monitor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.lingcloud.molva.xmm.monitor.pool.*;
import org.lingcloud.molva.xmm.monitor.pojos.*;

/**
 * <strong>Purpose:</strong><br>
 * The tester for LingCloud GangliaBridge.
 * 
 * @version 1.0.0 2011-8-24<br>
 * @author Liang Li<br>
 * 
 */
public class MonitorBridgeTester {
	
	private static Log log = LogFactory.getLog(MonitorBridgeTester.class);
	
	private static MonitorBridge mb = null;
	
	@BeforeClass
	public static void initializeForAllTest() {

		try {
			System.setProperty("lingcloud.home", "/opt/lingcloud");
			mb = MonitorBridge.getInstanse();
			assertNotNull(mb);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Initialze failed. Reason: " + e);
			fail();
		}

	}

	@AfterClass
	public static void destroyForAllTest() {

		try {
			mb = null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Destory failed. Reasion: " + e);
			fail();
		}
	}

	@Before
	public void initialze() {

	}

	@After
	public void destory() {
	}
	
	@Test
	public void test() {
		
	}

}
