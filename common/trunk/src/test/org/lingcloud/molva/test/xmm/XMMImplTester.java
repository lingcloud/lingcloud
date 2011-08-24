/*
 *  @(#)XMMImplTester.java  Jul 23, 2011
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
package org.lingcloud.molva.test.xmm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.lingcloud.molva.ocl.lease.LeaseConstants.LeaseLifeCycleState;
import org.lingcloud.molva.ocl.util.ConfigUtil;
import org.lingcloud.molva.test.util.TestConfig;
import org.lingcloud.molva.test.util.TestConstants;
import org.lingcloud.molva.xmm.pojos.Node;
import org.lingcloud.molva.xmm.pojos.NodeRequirement;
import org.lingcloud.molva.xmm.pojos.Partition;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.pojos.VirtualCluster;
import org.lingcloud.molva.xmm.pojos.VirtualNode;
import org.lingcloud.molva.xmm.services.VirtualManager;
import org.lingcloud.molva.xmm.services.XMMImpl;
import org.lingcloud.molva.xmm.util.XMMConstants;
import org.lingcloud.molva.xmm.util.XmlUtil;
import org.lingcloud.molva.xmm.vam.pojos.VirtualAppliance;
import org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager;
import org.lingcloud.molva.xmm.vam.util.VAMConfig;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;
import org.lingcloud.molva.xmm.vmc.VirtualClient;
import org.lingcloud.molva.xmm.ac.PPNPNController;
import org.lingcloud.molva.xmm.ac.PVNPNController;
import org.lingcloud.molva.xmm.ac.PartitionAC;
import org.lingcloud.molva.xmm.amm.VirtualClusterAMM;
import org.lingcloud.molva.xmm.deploy.policy.ONEVMDeployPolicier;
import org.lingcloud.molva.xmm.deploy.policy.VirtualMachineDeployPolicier;

/**
 * <strong>Purpose:</strong><br>
 * The tester for LingCloud XMMImpl.
 * 
 * @version 1.0.0 2011-7-24<br>
 * @author Liang Li<br>
 * 
 */
public class XMMImplTester {
	private static Log log = LogFactory.getLog(XMMClientTester.class);

	private static XMMImpl xmmImpl = null;

	private static Partition vmPartition = null;
	private static Partition genPartition = null;
	private static PhysicalNode vmPhyNode = null;
	private static PhysicalNode genPhyNode = null;
	private static VirtualCluster vmCluster = null;
	private static VirtualCluster genCluster = null;

	@BeforeClass
	public static void initializeForAllTest() {

		try {
			xmmImpl = new XMMImpl();
			addVirtualAppliance();
			createPartion();
			addPhysicalNode();
			createVirtualCluster();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Initialze failed. Reason: " + e);
			fail();
		}

	}

	@AfterClass
	public static void destroyForAllTest() {

		try {
			destroyVirtualCluster();
			removePhysicalNode();
			destoryPartion();
			removeVirtualAppliance();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Destory failed. Reasion: " + e);
			fail();
		}
		xmmImpl = null;
	}

	@Before
	public void initialze() {

	}

	@After
	public void destory() {
	}

	@Test
	public void getServerCurrentTime() {
		try {
			Date d = xmmImpl.getServerCurrentTime();
			assertNotNull(d);
			log.info("getServerCurrentTime Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void listAllPartition() {
		try {
			List<Partition> parList = xmmImpl.listAllPartition();
			assertTrue(parList.size() > 1);
			log.info("listAllPartition Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void listPartition() {
		try {
			List<Partition> parList = xmmImpl.listPartition(PartitionAC.class
					.getName());
			assertTrue(parList.size() > 1);
			log.info("listPartition Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void listPhysicalNode() {
		try {
			List<PhysicalNode> parList = xmmImpl.listPhysicalNode(genPartition
					.getGuid());
			assertTrue(parList.size() > 0);
			log.info("listPhysicalNode Test success.");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void refreshPhysicalNode() {
		try {
			PhysicalNode phyTmp = xmmImpl.refreshPhysicalNode(vmPhyNode
					.getGuid());
			assertNotNull(phyTmp);
			assertTrue(phyTmp.getGuid().equals(vmPhyNode.getGuid()));
			log.info("refreshPhysicalNode Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void refreshVirtualCluster() {
		try {
			VirtualCluster vml = xmmImpl.refreshVirtualCluster(vmCluster
					.getGuid());
			assertNotNull(vml);
			assertTrue(vml.getGuid().equals(vmCluster.getGuid()));
			log.info("refreshVirtualCluster Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void refreshVirtualNode() {
		try {
			List<VirtualNode> vmnList = null;
			vmnList = this.getVirtualNodeList4VC(vmCluster);

			assertNotNull(vmnList);
			assertTrue(vmnList.size() > 0);
			VirtualNode vn = vmnList.get(0);

			VirtualNode vnode = xmmImpl.refreshVirtualNode(vn.getGuid());
			assertNotNull(vnode);
			log.info("refreshVirtualNode Test success.");

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Test failed. Reason: " + e);
			fail();
		}

	}

	@Test
	public void searchVirtualCluster() {
		try {
			List<VirtualCluster> vcList = xmmImpl.searchVirtualCluster(
					new String[] { "name" }, new String[] { "=" },
					new Object[] { TestConstants.TEST_EVN_NAME_VMCLUSTER });
			assertTrue(vcList.size() > 0);
			log.info("searchVirtualCluster Test success.");

		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void searchVirtualNode() {
		try {
			List<VirtualNode> vmnList = getVirtualNodeList4VC(vmCluster);

			VirtualNode vn = vmnList.get(0);
			vn.setName("tom");
			xmmImpl.updateVirtualNodeInfo(vn.getGuid(), vn);
			List<VirtualNode> vnList = xmmImpl.searchVirtualNode(
					new String[] { "name" }, new String[] { "=" },
					new Object[] { "tom" });
			assertTrue(vnList.size() > 0);
			log.info("searchVirtualNode Test success.");

		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}

	}

	@Test
	public void searchPhysicalNode() {
		try {
			vmPhyNode.setName("sun");
			xmmImpl.updatePhysicalNodeInfo(vmPhyNode.getGuid(), vmPhyNode);
			List<PhysicalNode> phyList = xmmImpl.searchPhysicalNode(
					new String[] { "name" }, new String[] { "=" },
					new Object[] { "sun" });
			assertTrue(phyList.size() > 0);
			log.info("searchPhysicalNode Test success.");

		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}

	}
	
	private boolean stopVirtualCluster(String vcid) throws Exception {
		boolean validated = false;
		
		xmmImpl.stopVirtualCluster(vcid);
		for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++) {
			List<VirtualNode> vmnList = listVirtualNodeInVirtualCluster(
					vcid);
			validated = true;
			for (VirtualNode vn : vmnList) {
				if (!vn.getRunningStatus().equals(
						XMMConstants.MachineRunningState.STOP.toString())) {
					validated = false;
					break;
				}
			}
			if (validated) {
				break;
			}
			
			Thread.sleep(TestConstants.RETRY_INTERVAL);
		}
		
		return validated;
	}

	@Test
	public void stopAndStartVirtualCluster() {
		try {
			boolean validated = stopVirtualCluster(vmCluster.getGuid());
			assertTrue(validated);
			log.info("stopVirtualCluster Test success.");
			
			validated = startVirtualCluster(vmCluster.getGuid());
			assertTrue(validated);
			log.info("startVirtualCluster Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	private boolean startVirtualCluster(String vcid) throws Exception {
		boolean validated = false;
		
		xmmImpl.stopVirtualCluster(vcid);
		for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++) {
			List<VirtualNode> vmnList = listVirtualNodeInVirtualCluster(
					vcid);
			validated = true;
			for (VirtualNode vn : vmnList) {
				if (!vn.getRunningStatus().equals(
						XMMConstants.MachineRunningState.RUNNING.toString())) {
					validated = false;
					break;
				}
			}
			if (validated) {
				break;
			}
			
			Thread.sleep(TestConstants.RETRY_INTERVAL);
		}
		
		return validated;
	}
	
	@Ignore
	@Test
	public void startVirtualCluster() {
		try {
			boolean validated = startVirtualCluster(vmCluster.getGuid());
			assertTrue(validated);
			
			log.info("startVirtualCluster Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void viewVirtualNode() {
		try {
			List<VirtualNode> vmnList = getVirtualNodeList4VC(vmCluster);
			VirtualNode vn = vmnList.get(0);
			VirtualNode vnode = xmmImpl.viewVirtualNode(vn.getGuid());
			assertNotNull(vnode);
			assertTrue(vnode.getGuid().equals(vn.getGuid()));
			log.info("viewVirtualNode Test success.");

		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void viewVirtualCluster() {
		try {
			VirtualCluster vml = xmmImpl
					.viewVirtualCluster(vmCluster.getGuid());
			assertNotNull(vml);
			assertTrue(vml.getGuid().equals(vmCluster.getGuid()));
			log.info("viewVirtualCluster Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void viewPhysicalNode() {
		try {
			PhysicalNode phn = xmmImpl.viewPhysicalNode(vmPhyNode.getGuid());
			assertNotNull(phn);
			log.info("viewPhysicalNode Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void viewPartition() {
		try {
			Partition vmpart = xmmImpl.viewPartition(vmPartition.getGuid());
			assertNotNull(vmpart);
			assertTrue(vmpart.getGuid().equals(vmPartition.getGuid()));
			Partition phypart = xmmImpl.viewPartition(genPartition.getGuid());
			assertNotNull(phypart);
			assertTrue(phypart.getGuid().equals(genPartition.getGuid()));
			log.info("viewPartition Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void updateVirtualClusterInfo() {
		try {
			VirtualCluster vc = xmmImpl.viewVirtualCluster(vmCluster.getGuid());
			vc.setDescription("description1");
			vc = xmmImpl.updateVirtualClusterInfo(
					vmCluster.getGuid(), vc);
			assertNotNull(vc);
			assertTrue(vc.getDescription().equals("description1"));
			log.info("updateVirtualClusterInfo Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void updateVirtualNodeInfo() {
		try {
			PhysicalNode phy = xmmImpl.viewPhysicalNode(
					vmPhyNode.getGuid());
			phy.setDescription("description1");
			phy = xmmImpl.updatePhysicalNodeInfo(
					vmPhyNode.getGuid(), phy);
			assertNotNull(phy);
			assertNotNull(phy);
			// assertTrue(phyTmp.getDescription().equals("description1"));
			log.info("updateVirtualNodeInfo Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void updatePartitionInfo() {

		try {
			Partition par = xmmImpl.viewPartition(vmPartition.getGuid());
			par.setDescription("description1");
			par = xmmImpl.updatePartitionInfo(
					vmPartition.getGuid(), par);
			assertNotNull(par);
			assertTrue(par.getDescription().equals("description1"));

			log.info("updatePartitionInfo Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void updatePhysicalNodeInfo() {
		try {
			PhysicalNode phy = xmmImpl.viewPhysicalNode(vmPhyNode.getGuid());
			phy.setDescription("description1");
			phy = xmmImpl.updatePhysicalNodeInfo(
					vmPhyNode.getGuid(), phy);
			assertNotNull(phy);
			assertTrue(phy.getDescription().equals("description1"));
			
			log.info("updatePhysicalNodeInfo Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Ignore
	@Test
	public void stopVirtualNode() {
		try {
			VirtualNode vn = null;

			for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++) {
				List<VirtualNode> vmnList = listVirtualNodeInVirtualCluster(
						vmCluster.getGuid());
				vn = vmnList.get(0);
				if (vn.getRunningStatus().equals(
						XMMConstants.MachineRunningState.RUNNING.toString())) {
					break;
				}
				Thread.sleep(TestConstants.RETRY_INTERVAL);
				System.out.println("Waiting for VNode list for VC node- "
						+ vmCluster.getName());

			}

			VirtualNode vnode = xmmImpl.stopVirtualNode(vn.getGuid());
			assertTrue(vnode.getRunningStatus().equals(
					XMMConstants.MachineRunningState.STOP.toString()));
			log.info("stopVirtualNode Test success.");

		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	private static Partition createPartion() throws Exception {
		Partition par = null;
		String name = null;
		String controller = null;
		String nodetype = null;
		String preInstalledSoft = null;
		String desc = null;
		HashMap<String, String> attr = new HashMap<String, String>();
		/**
		 * Create VM partition.
		 */
		name = TestConstants.TEST_EVN_NAME_VMPARTION;
		controller = PartitionAC.class.getName();
		nodetype = PartitionAC.VM;
		preInstalledSoft = "No software.";
		desc = "VM partition for xmmImpl test.";
		attr.put(PartitionAC.REQUIRED_ATTR_NODETYPE, nodetype);
		attr.put(PartitionAC.ATTR_NODE_PRE_INSTALLED_SOFT, preInstalledSoft);
		vmPartition = xmmImpl.createPartition(name, controller, attr, desc);
		assertNotNull(vmPartition);

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

	private List<VirtualNode> getVirtualNodeList4VC(VirtualCluster vc)
			throws Exception {
		List<VirtualNode> vmnList = null;
		for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++) {
			vmnList = listVirtualNodeInVirtualCluster(vc
					.getGuid());
			
			if (vmnList != null && vmnList.size() > 0) {
				break;
			}
			Thread.sleep(TestConstants.RETRY_INTERVAL);
			System.out.println("Waiting for VNode list for VC - "
					+ vmCluster.getName());
		}
		return vmnList;
	}

	public List<VirtualNode> listVirtualNodeInVirtualCluster(String vcid)
			throws Exception {
		String[] fields = new String[] { "leaseId" };
		String[] operators = new String[] { "=" };
		Object[] values = new Object[] { vcid };
		return xmmImpl.searchVirtualNode(fields, operators, values);
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
		try {
			if (vmPartition != null && vmPartition.getGuid() != null) {
				xmmImpl.destroyPartition(vmPartition.getGuid());
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
		 * Add a physical node to the VM partition.
		 */
		privateip = TestConfig.getTestXenServer();
		publicip = privateip;
		controller = PVNPNController.class.getName();
		description = "Added Phyical Node for general partition test.";
		vmPhyNode = xmmImpl.addPhysicalNode(vmPartition.getGuid(), privateip,
				publicip, controller, redeploy, attr, description);
		assertNotNull(vmPhyNode);
		log.info(description);

		/**
		 * Add a physical node to the general partition.
		 */
		privateip = TestConfig.getTestCommonServer();
		publicip = privateip;
		controller = PPNPNController.class.getName();
		description = "Phyical Node for general partition test.";
		genPhyNode = xmmImpl.addPhysicalNode(genPartition.getGuid(), privateip,
				publicip, controller, redeploy, attr, description);
		assertNotNull(genPhyNode);

		xmmImpl.refreshPhysicalNode(vmPhyNode.getGuid());
		xmmImpl.refreshPhysicalNode(genPhyNode.getGuid());
		VirtualClient vc = null;
		PhysicalNode pn = null;
		vc = VirtualManager.getInstance().getVirtualClient();
		pn = vc.getVMProvisionNode(vmPhyNode);

		for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++) {
			if (pn.getFreeCpu() > 0) {
				break;
			}
			System.out
					.println("Wait for physical node in vm partition. " 
							+ "Free CPU in the node : "
							+ pn.getFreeCpu());
			Thread.sleep(TestConstants.RETRY_INTERVAL);
			pn = xmmImpl.refreshPhysicalNode(vmPhyNode.getGuid());
			pn = vc.getVMProvisionNode(vmPhyNode);
		}

		log.info(description);

		return pn;
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
		
		try {
			if (vmPhyNode != null && vmPhyNode.getGuid() != null) {
				xmmImpl.removePhysicalNode(vmPhyNode.getGuid());
				log.info("Remove physical node " + vmPhyNode.getName());

				for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++) {
					PhysicalNode pn = xmmImpl.viewPhysicalNode(vmPhyNode
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

	private static VirtualCluster createVirtualCluster() throws Exception {
		VirtualCluster vc = null;

		String parguid;
		String clustername;
		String nodeMatchMaker = VirtualClusterAMM.class.getName();
		
		String tenantId = "";
		String[] pnnodeip;
		HashMap<String, NodeRequirement> nrmap 
				= new HashMap<String, NodeRequirement>();
		Date effectiveTime = null;
		Date expireTime = null;
		String desc;

		/**
		 * Add a virtual cluster for a VM partition.
		 */
		parguid = vmPartition.getGuid();
		clustername = TestConstants.TEST_EVN_NAME_VMCLUSTER;

		NodeRequirement nr = new NodeRequirement();
		nr.setVirtualApplicanceID(TestConstants.TEST_EVN_GUID_APP);
		nr.setCpuNum(2);
		nr.setMemorySize(TestConstants.K / 2);
		nr.setNeedPublicIP(true);

		nr.setVmDeployPolicer(ONEVMDeployPolicier.class.getName());
		HashMap<String, String> deployParams = new HashMap<String, String>();
		deployParams.put(VirtualMachineDeployPolicier.RANDOM_DEPLOY, 
				TestConfig.getTestXenServer());
		nr.setVmDeployParams(deployParams);
		nrmap.put("0", nr);

		desc = "Added Virtual Cluster in VM partition for test";
		vmCluster = xmmImpl.createVirtualCluster(parguid, clustername,
				nodeMatchMaker, tenantId, null, nrmap, effectiveTime, 0,
				expireTime, null, desc);

		assertNotNull(vmCluster);
		log.info(desc);
		
		for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++) {
			VirtualCluster cluster = xmmImpl.viewVirtualCluster(
					vmCluster.getGuid());
			if (cluster.getLifecycleState() == LeaseLifeCycleState.EFFECTIVE) {
				break;
			}
			Thread.sleep(TestConstants.RETRY_INTERVAL);
		}

		/**
		 * Add a virtual cluster for a general partition.
		 */
		parguid = genPartition.getGuid();
		clustername = TestConstants.TEST_EVN_NAME_GENCLUSTER;
		pnnodeip = new String[] { TestConfig.getTestCommonServer() };
		nrmap.clear();
		for (int i = 0; i < pnnodeip.length; i++) {
			NodeRequirement tmpNr = new NodeRequirement();
			tmpNr.setPartitionId(parguid);
			tmpNr.setPrivateIP(pnnodeip[i]);
			nrmap.put(pnnodeip[i], tmpNr);
		}
		desc = "Added Virtual Cluster in general partition for test";
		genCluster = xmmImpl.createVirtualCluster(parguid, clustername,
				nodeMatchMaker, tenantId, null, nrmap, effectiveTime, 0,
				expireTime, null, desc);
		assertNotNull(genCluster);
		log.info(desc);
		
		for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++) {
			VirtualCluster cluster = xmmImpl.viewVirtualCluster(
					genCluster.getGuid());
			if (cluster.getLifecycleState() == LeaseLifeCycleState.EFFECTIVE) {
				break;
			}
			Thread.sleep(TestConstants.RETRY_INTERVAL);
		}

		return vc;
	}

	public static void destroyVirtualCluster() throws Exception {
		Thread.sleep(TestConstants.RETRY_INTERVAL);
		
		try {
			if (genCluster != null && genCluster.getGuid() != null) {
				xmmImpl.destroyVirtualCluster(genCluster.getGuid());
				log.info("Destory the test cluster in the general partition : "
						+ TestConstants.TEST_EVN_NAME_GENCLUSTER);

				for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++) {
					VirtualCluster cluster = xmmImpl
							.viewVirtualCluster(genCluster.getGuid());
					if (cluster == null) {
						break;
					}
					Thread.sleep(TestConstants.RETRY_INTERVAL);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Destroy virtual cluster failed. Reasion: " + e);
		}
		
		try {
			if (vmCluster != null && vmCluster.getGuid() != null) {
				xmmImpl.destroyVirtualCluster(vmCluster.getGuid());
				log.info("Destory the test cluster in the VM partition : "
						+ TestConstants.TEST_EVN_NAME_VMCLUSTER);

				for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++) {
					VirtualCluster cluster = xmmImpl
							.viewVirtualCluster(vmCluster.getGuid());
					if (cluster == null) {
						break;
					}
					Thread.sleep(TestConstants.RETRY_INTERVAL);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Destroy virtual cluster failed. Reasion: " + e);
		}
	}

	private static void copyFile(String srcPath, String dstPath) 
		throws Exception {
		int total = 0;
		int read = 0;
		File srcfile = new File(srcPath);
		if (srcfile.exists()) { 
			FileInputStream fis = new FileInputStream(srcPath); 
			FileOutputStream fos = new FileOutputStream(dstPath);
			byte[] buffer = new byte[TestConstants.K];
			
			while ((read = fis.read(buffer)) != -1) {
				total += read;
				fos.write(buffer, 0, read);
			}
			fis.close();
			fos.close();
		}
	}
	
	public static void addVirtualAppliance() throws Exception {
		VirtualApplianceManager vam = VAMUtil.getVAManager();
		String guid = TestConstants.TEST_EVN_GUID_APP;
		
		try {
			if (vam.queryAppliance(guid) != null) {
				return;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		String imagePath = ConfigUtil.getHomePath() 
				+ "/bin/images/example/example.img";
		File image = new File(imagePath);
	
		assertTrue(image.exists());
	
		copyFile(imagePath, VAMConfig.getDiskUploadDirLocation()
				+ "/example.img");
		
		
		String format = VAMConfig.getImageFormat();
		String loader = VAMConstants.VA_BOOTLOAD_HVM;
		String name = "testAppliance";
		String location = "example.img";
		String os = "Linux";
		String osversion = "";
		String category = "0";
		int cpuamount = 2;
		int memsize = TestConstants.K / 2;
		int loginstyle = VAMConstants.LOGIN_STYLE_GLOBAL_USER;
		List<String> accessWayList = new ArrayList<String>();
		accessWayList.add(VAMConstants.VA_ACCESS_WAY_VNC);
		List<String> langl = new ArrayList<String>();
		langl.add("English");
		String description = "test appliance.";
		
		VirtualAppliance va = new VirtualAppliance();
		va.setGuid(guid);
		va.setVAName(name);
		va.setAccessWay(accessWayList);
		va.setCpuAmount(cpuamount);
		va.setMemory(memsize);
		List<String> appl = new ArrayList<String>();
		va.setApplications(appl);
		va.setBootLoader(loader);
		va.setCategory(category);
		va.setDescription(description);
		va.setLanguages(langl);
		va.setOs(os, osversion);
		va.setFormat(format);
		va.setLoginStyle(loginstyle);
		va.setUsername("");
		va.setPassword("");
		va = vam.addAppliance(va, location);
		
		assertNotNull(va);
		
		for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++) {
			VirtualAppliance appliance = vam.queryAppliance(va.getGuid());
			if (appliance.getState() == VAMConstants.STATE_READY) {
				break;
			}
			Thread.sleep(TestConstants.RETRY_INTERVAL);
		}
	}
	
	public static void removeVirtualAppliance() throws Exception {
		try {
			String guid = TestConstants.TEST_EVN_GUID_APP;
			VirtualApplianceManager vam = VAMUtil.getVAManager();
			vam.removeAppliance(guid);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("remove virtual appliance failed. Reasion: " + e);
		}
		Thread.sleep(TestConstants.RETRY_INTERVAL);
	}
}
