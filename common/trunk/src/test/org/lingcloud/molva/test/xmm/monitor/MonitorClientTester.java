/*
 *  @(#)MonitorClientTester.java  Jul 23, 2011
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
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lingcloud.molva.test.util.TestConfig;
import org.lingcloud.molva.xmm.client.XMMClient;
import org.lingcloud.molva.xmm.monitor.MonitorClient;
import org.lingcloud.molva.xmm.monitor.pojos.Host;
import org.lingcloud.molva.xmm.monitor.pojos.VM;
import org.lingcloud.molva.xmm.monitor.pool.MonitorBridge;
import org.lingcloud.molva.xmm.monitor.pool.MonitorPool;

import antlr.collections.List;

/**
 * <strong>Purpose:</strong><br>
 * The tester for LingCloud MonitorClient.
 * 
 * @version 1.0.0 2011-8-24<br>
 * @author Liang Li<br>
 * 
 */
public class MonitorClientTester {

	private static Log log = LogFactory.getLog(MonitorBridgeTester.class);
	private static MonitorPool mp = null;

	private static MonitorClient mc = null;
	private static MonitorBridge mbnew = null;

	@BeforeClass
	public static void initializeForAllTest() {

		try {
			System.setProperty("lingcloud.home", "/opt/lingcloud");
			mc = MonitorClient.getInstanse();
			mbnew = MonitorBridge.getInstanse();
			mp = MonitorPool.getInstanse();
			assertNotNull(mc);
			assertNotNull(mbnew);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Initialze failed. Reason: " + e);
			fail();
		}
	}

	@AfterClass
	public static void destroyForAllTest() {

		try {
			mc = null;
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
	public void getStaticsInJson() {
		try {
			String stat = mc.getStaticsInJson();
			assertNotNull(stat);
			log.info("getStaticsInJson Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void getHosts4Srv() {
		try {
			String stat = mc.getHosts4Srv("cpu_num", 0);
			assertNotNull(stat);
			log.info("getHosts4Srv Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void getMonitorConfInJson() {
		try {
			String stat = mc.getMonitorConfInJson(0);
			assertNotNull(stat);
			log.info("getMonitorConfInJson Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void getNodesByState() {

		try {
			String stat = mc.getNodesByState("cpu_num", 0);
			assertNotNull(stat);
			log.info("getNodesByState Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void getPartitionInJson() {
		try {
			String stat = mc.getPartitionInJson();
			assertNotNull(stat);
			log.info("getPartitionInJson Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void getSrvHistoryImg() {
		try {
			String stat = mc.getSrvHistoryImg(
					TestConfig.getTestLingCloudServer(), "cpu_num");
			assertNotNull(stat);
			log.info("getSrvHistoryImg Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}

	}

	@Test
	public void getVMInfos() {
		try {
			Map<String, Host> hs = new HashMap<String, Host>();
			Map<String, Host> hsupdate = mbnew.getHostMap(hs);
			Host hostnew = hsupdate.get(TestConfig.getTestXenServer());
			Map<String, VM> map = hostnew.getVMMap();

			String vmname = null;
			vmname = map.keySet().iterator().next();
			String stat = mc.getVMInfos(hostnew.getHostName(), vmname);
			assertNotNull(stat);
			log.info("getVMInfos Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void setMonitorConfByParName() {
		try {
			String setings = "setting";

			String stat = mc.setMonitorConfByParName(setings, 0);
			assertNotNull(stat);
			log.info("setMonitorConfByParName Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

}
