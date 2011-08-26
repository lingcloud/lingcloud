/*
 *  @(#)MonitorPoolTester.java  Jul 23, 2011
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.lingcloud.molva.test.util.TestConfig;
import org.lingcloud.molva.xmm.monitor.pool.*;
import org.lingcloud.molva.xmm.monitor.pojos.*;

/**
 * <strong>Purpose:</strong><br>
 * The tester for LingCloud MonitorPoolImpl.
 * 
 * @version 1.0.0 2011-8-24<br>
 * @author Liang Li<br>
 * 
 */
public class MonitorPoolTester {

	private static Log log = LogFactory.getLog(MonitorBridgeTester.class);

	private static MonitorPool mp = null;

	@BeforeClass
	public static void initializeForAllTest() {

		try {
			System.setProperty("lingcloud.home", "/opt/lingcloud");
			mp = MonitorPool.getInstanse();
			assertNotNull(mp);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Initialze failed. Reason: " + e);
			fail();
		}

	}

	@AfterClass
	public static void destroyForAllTest() {

		try {
			mp = null;
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
	public void update() {
		try {
			int size;
			long period = 0;
			long oldperiod = mp.getTimePeriod();
			mp.setTimePeriod(period);
			Thread.sleep(70000);
			size = mp.update();
			mp.setTimePeriod(oldperiod);
			assertTrue(size > 0);
			log.info("update Test success.");

		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void setTimePeriod() {
		try {
			long period = 3 * 60 * 1000;
			long oldperiod = mp.getTimePeriod();
			mp.setTimePeriod(period);
			assertTrue(mp.getTimePeriod() == period);
			log.info("setTimePeriod Test success.");
			mp.setTimePeriod(oldperiod);
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void getTimePeriod() {
		try {
			long period = 3 * 60 * 1000;
			long oldperiod = mp.getTimePeriod();
			mp.setTimePeriod(period);
			assertTrue(mp.getTimePeriod() == period);
			log.info("getTimePeriod Test success.");
			mp.setTimePeriod(oldperiod);
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Ignore
	@Test
	public void getAllHostMap() {
		try {
			Map<String, Host> all = mp.getAllHostMap();
			assertTrue(all.size() > 0);
			log.info("getAllHostMap Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void getMonitorConfByParName() {
		try {
			String parname = "parname";
			MonitorConf mconf = mp.getMonitorConfByParName(parname);

			assertNotNull(mconf);
			mconf = null;

			log.info("getMonitorConfByParName Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void setMonitorConf() {
		try {
			String parname = "parname";
			MonitorConf mcnew = mp.getMonitorConfByParName(parname);
			MonitorConf mc = mp.setMonitorConf(mcnew);
			assertNotNull(mc);
			mcnew = null;
			mc = null;
			log.info("setMonitorConf Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void getSrvInHost() {
		try {
			Service se = mp.getSrvInHost("cpu_num",
					TestConfig.getTestLingCloudServer());
			assertNotNull(se);
			log.info("getTimePeriod Test success.");

		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void getHostState() {
		try {
			String hoststate = mp.getHostState(TestConfig
					.getTestLingCloudServer());
			assertNotNull(hoststate);
			log.info("getHostState Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void getHost() {
		try {

			Host hostnew = mp.getHost(TestConfig.getTestXenServer());
			assertNotNull(hostnew);
			log.info("getHost Test success.");

		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void getSrvImgUri() {
		try {
			String name = "hostname";
			assertTrue("".equals(mp.getSrvImgUri(name, null, 0, 0)));
			log.info("getSrvImgUri Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

}
