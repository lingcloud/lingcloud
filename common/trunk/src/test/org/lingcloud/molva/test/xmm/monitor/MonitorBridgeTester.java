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

import java.util.HashMap;
import java.util.Map;

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
	public void setTimePeriod() {
		try {
			long period = 3 * 60 * 1000;
			long oldperiod = mb.getTimePeriod();
			mb.setTimePeriod(period);
			assertTrue(mb.getTimePeriod() == period);
			log.info("setTimePeriod Test success.");
			mb.setTimePeriod(oldperiod);
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void getTimePeriod() {
		try {
			long period = 3 * 60 * 1000;
			long oldperiod = mb.getTimePeriod();
			mb.setTimePeriod(period);
			assertTrue(mb.getTimePeriod() == period);
			log.info("getTimePeriod Test success.");
			mb.setTimePeriod(oldperiod);
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void update() {
		try {
			Map<String, Host> hs = new HashMap<String, Host>();

			Map<String, Host> hsupdate = mb.update(hs);
			assertTrue(hsupdate.size() > 0);
			log.info("update Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void getHostMap() {

		try {
			Map<String, Host> hs = new HashMap<String, Host>();

			Map<String, Host> hsget = mb.getHostMap(hs);
			assertTrue(hsget.size() > 0);
			log.info("getHostMap Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void updateHost() {
		try {
			String name = "hostname";
			Host host = new Host(name);
			Host hostup = mb.updateHost(host);
			assertTrue(hostup.getHostName().equals(name));
			log.info("updateHost Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}

	}

	@Test
	public void getSrvImgUri() {
		try {
			String name = "hostname";
			assertTrue("".equals(mb.getSrvImgUri(name, null, 0, 0)));
			log.info("getSrvImgUri Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}
}
