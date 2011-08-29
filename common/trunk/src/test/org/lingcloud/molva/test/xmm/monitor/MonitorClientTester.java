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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lingcloud.molva.test.util.TestConfig;
import org.lingcloud.molva.test.util.TestConstants;
import org.lingcloud.molva.xmm.ac.PPNPNController;
import org.lingcloud.molva.xmm.ac.PVNPNController;
import org.lingcloud.molva.xmm.ac.PartitionAC;
import org.lingcloud.molva.xmm.monitor.MonitorClient;
import org.lingcloud.molva.xmm.monitor.MonitorConstants;
import org.lingcloud.molva.xmm.monitor.pojos.Host;
import org.lingcloud.molva.xmm.monitor.pojos.VM;
import org.lingcloud.molva.xmm.monitor.pool.MonitorBridge;
import org.lingcloud.molva.xmm.pojos.Node;
import org.lingcloud.molva.xmm.pojos.Partition;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.pojos.VirtualCluster;
import org.lingcloud.molva.xmm.pojos.VirtualNode;
import org.lingcloud.molva.xmm.services.VirtualManager;
import org.lingcloud.molva.xmm.services.XMMImpl;
import org.lingcloud.molva.xmm.util.XmlUtil;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vmc.VirtualClient;


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

	private static MonitorClient mc = null;
	private static MonitorBridge mbnew = null;

	@BeforeClass
	public static void initializeForAllTest() {
		try {
			mc = MonitorClient.getInstanse();
			mbnew = MonitorBridge.getInstanse();
			xmmImpl = new XMMImpl();
			createPartion();
			addPhysicalNode();
			assertNotNull(mc);
			assertNotNull(mbnew);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Initialze failed. Reason: " + e);
			//fail();
		}
	}

	@AfterClass
	public static void destroyForAllTest() {

		try {
			mc = null;
//			removePhysicalNode();
//			destoryPartion();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Destory failed. Reasion: " + e);
			//fail();
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
			String stat = mc.getHosts4Srv(MonitorConstants.MONITOR_HOST_CPU, 0);
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
			String stat = mc.getNodesByState(MonitorConstants.MONITOR_HOST_CPU, 0);
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
					TestConfig.getTestLingCloudServer(), MonitorConstants.MONITOR_HOST_CPU);
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
			String vmname = null;
			String hostName = TestConfig.getTestLingCloudServer();
			vmname = "vmName";
			String stat = mc.getVMInfos(hostName, vmname);
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
	
	private static XMMImpl xmmImpl = null;
	private static Partition genPartition = null;
	private static PhysicalNode genPhyNode = null;
	
	private static Partition createPartion() throws Exception {
		Partition par = null;
		String name = null;
		String controller = null;
		String nodetype = null;
		String preInstalledSoft = null;
		String desc = null;
		HashMap<String, String> attr = new HashMap<String, String>();

		/**
		 * Create general partition
		 */
		name = TestConstants.TEST_EVN_NAME_GENPARTION;
		controller = PartitionAC.class.getName();
		nodetype = PartitionAC.GENERAL;
		preInstalledSoft = "No software.";
		desc = "General partition for xmmImpl test.";
		attr.put(PartitionAC.REQUIRED_ATTR_NODETYPE, nodetype);
		attr.put(PartitionAC.ATTR_NODE_PRE_INSTALLED_SOFT, preInstalledSoft);
		genPartition = xmmImpl.createPartition(name, controller, attr, desc);
		assertNotNull(genPartition);

		return par;
	}

	private static void destoryPartion() throws Exception {
		Thread.sleep(TestConstants.RETRY_INTERVAL);
		
		try {
			if (genPartition != null && genPartition.getGuid() != null) {
				xmmImpl.destroyPartition(genPartition.getGuid());
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Destory partion failed. Reasion: " + e);
		}
		
		Thread.sleep(TestConstants.RETRY_INTERVAL);
	}

	private static PhysicalNode addPhysicalNode() throws Exception {
		String privateip = null;
		String publicip = null;
		String controller = null;
		boolean redeploy = true;
		String description = null;
		HashMap<String, String> attr = new HashMap<String, String>();
		List<String> accessWay = new ArrayList<String>();
		accessWay.add(VAMConstants.VA_ACCESS_WAY_SSH);
		accessWay.add(VAMConstants.VA_ACCESS_WAY_VNC);
		attr.put(Node.ACCESSWAY, XmlUtil.toXml(accessWay));

		/**
		 * Add a physical node to the general partition.
		 */
		privateip = TestConfig.getTestLingCloudServer();
		controller = PPNPNController.class.getName();
		description = "Phyical Node for general partition test.";
		genPhyNode = xmmImpl.addPhysicalNode(genPartition.getGuid(), privateip,
				publicip, controller, redeploy, attr, description);
		assertNotNull(genPhyNode);

		Thread.sleep(TestConstants.RETRY_INTERVAL);
		log.info(description);

		return genPhyNode;
	}

	private static void removePhysicalNode() throws Exception {
		Thread.sleep(TestConstants.RETRY_INTERVAL);
		
		try {
			if (genPhyNode != null && genPhyNode.getGuid() != null) {
				xmmImpl.removePhysicalNode(genPhyNode.getGuid());
				log.info("Remove physical node " + genPhyNode.getName());

				for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++) {
					PhysicalNode pn = xmmImpl.viewPhysicalNode(genPhyNode
							.getGuid());
					if (pn == null) {
						break;
					}
					Thread.sleep(TestConstants.RETRY_INTERVAL);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Remove physical node failed. Reasion: " + e);
		}
		
	}

}
