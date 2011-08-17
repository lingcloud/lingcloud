/*
 *  @(#)XMMClientTester.java  Jul 23, 2011
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

import java.util.*;

import static org.junit.Assert.*;

import org.apache.commons.logging.*;

import org.junit.*;
import org.lingcloud.molva.test.util.TestConstants;
import org.lingcloud.molva.xmm.services.VirtualManager;
import org.lingcloud.molva.xmm.util.XmlUtil;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vmc.VirtualClient;
import org.lingcloud.molva.xmm.pojos.*;
import org.lingcloud.molva.xmm.ac.*;
import org.lingcloud.molva.xmm.amm.VirtualClusterAMM;
import org.lingcloud.molva.xmm.client.*;
import org.lingcloud.molva.xmm.deploy.policy.ONEVMDeployPolicier;

import static org.lingcloud.molva.test.xmm.XMMImplTester.*;

/**
 * <strong>Purpose:</strong><br>
 * The tester for LingCloud XMMClient.
 *
 * @version 1.0.0 2011-7-24<br>
 * @author Liang Li<br>
 *
 */
public class XMMClientTester {
	private static Log log = LogFactory.getLog(XMMClientTester.class);
	
	private static XMMClient xmmClient = new XMMClient();
	
	private static Partition vmPartition = null;
	private static Partition genPartition = null;
	private static PhysicalNode vmPhyNode = null;
	private static PhysicalNode genPhyNode = null;
	private static VirtualCluster vmCluster = null;
	private static VirtualCluster genCluster = null;
	
	@BeforeClass
	public static void initializeForAllTest() {
		try {
			createPartion();
			addPhysicalNode();
			createVirtualCluster();
		}catch (Exception e) {
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
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Destory failed. Reasion: " + e);
			fail();
		}
		xmmClient = null;
	}
	
	@Before
	public void initialze() {
		try {
			
		}catch (Exception e) {
			log.error("Initialze failed. Reason: " + e);
			fail();
		}
	}
	
	@After
	public void destory() {
	}
	
	@Test
	public void getServerCurrentTime() {
		try {
			Date d = xmmClient.getServerCurrentTime();
			assertNotNull(d);
			log.info("getServerCurrentTime Test success.");
		}catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}
	
	@Test
	public void listAllPartition() {
		try {
			List<Partition> parList = xmmClient.listAllPartition();
			assertTrue(parList.size() > 1);
			log.info("getServerCurrentTime Test success.");
		}catch (Exception e) {
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
		name = TEST_EVN_NAME_VMPARTION;
		controller = PartitionAC.class.getName();
		nodetype = PartitionAC.VM;
		preInstalledSoft = "No software.";
		desc = "VM partition for XMMClient test.";
		attr.put(PartitionAC.REQUIRED_ATTR_NODETYPE, nodetype);
		attr.put(PartitionAC.ATTR_NODE_PRE_INSTALLED_SOFT,
						preInstalledSoft);
		vmPartition = xmmClient.createPartition(name, controller, attr, desc);
		assertNotNull(vmPartition);
		
		/**
		 * Create general partition
		 */
		name = TEST_EVN_NAME_GENPARTION;
		controller = PartitionAC.class.getName();
		nodetype = PartitionAC.GENERAL;
		preInstalledSoft = "No software.";
		desc = "General partition for XMMClient test.";
		attr.put(PartitionAC.REQUIRED_ATTR_NODETYPE, nodetype);
		attr.put(PartitionAC.ATTR_NODE_PRE_INSTALLED_SOFT,
						preInstalledSoft);
		genPartition = xmmClient.createPartition(name, controller, attr, desc);
		assertNotNull(genPartition);
		
		return par;
	}
	
	private static void destoryPartion() throws Exception{
		
		xmmClient.destroyPartition(vmPartition.getGuid());
		xmmClient.destroyPartition(genPartition.getGuid());
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
		privateip = TestConstants.TEST_XEN_SERVER;
		publicip = privateip;
		controller = PVNPNController.class.getName();
		description = "Added Phyical Node for general partition test.";
		vmPhyNode = xmmClient.addPhysicalNode(vmPartition.getGuid(), privateip, publicip,
				controller, redeploy, attr, description);
		assertNotNull(vmPhyNode);
		log.info(description);
		
		/**
		 * Add a physical node to the general partition.
		 */
		privateip = TestConstants.TEST_COMMON_SERVER;
		publicip = privateip;
		controller = PPNPNController.class.getName();
		description = "Phyical Node for general partition test.";
		genPhyNode = xmmClient.addPhysicalNode(genPartition.getGuid(), privateip, publicip,
				controller, redeploy, attr, description);
		assertNotNull(genPhyNode);
		
		xmmClient.refreshPhysicalNode(vmPhyNode.getGuid());
		xmmClient.refreshPhysicalNode(genPhyNode.getGuid());
		VirtualClient vc = null;
		PhysicalNode pn = null;
		vc = VirtualManager.getInstance().getVirtualClient();
		pn = vc.getVMProvisionNode(vmPhyNode);
		
		for (int i= 0 ; i < 100 ; i++) {
			if (pn.getFreeCpu() > 0) {
				break;
			}
			System.out.println("Wait for physical node in vm partition. Free CPU in the node : " + pn.getFreeCpu());
			Thread.sleep(3000);
			pn = xmmClient.refreshPhysicalNode(vmPhyNode.getGuid());
			pn = vc.getVMProvisionNode(vmPhyNode);
		}
		
		log.info(description);
		
		return vmPhyNode;
	}
	
	private static void removePhysicalNode() throws Exception {
		
		xmmClient.removePhysicalNode(vmPhyNode.getGuid());
		log.info("Remove physical node " + vmPhyNode.getName());
		xmmClient.removePhysicalNode(genPhyNode.getGuid());
		log.info("Remove physical node " + genPhyNode.getName());
	}
	
	private static VirtualCluster createVirtualCluster() throws Exception {
		VirtualCluster vc = null;

		String parguid;
		String clustername;
		String nodeMatchMaker = VirtualClusterAMM.class.getName();;
		String tenantId = "";
		String[] pnnodeip;
		HashMap<String, NodeRequirement> nrmap = new HashMap<String, NodeRequirement>();
		Date effectiveTime = null;
		Date expireTime = null;
		String desc;
		
		/**
		 * Add a virtual cluster for a VM partition.
		 */
		parguid = vmPartition.getGuid();
		clustername = TEST_EVN_NAME_VMCLUSTER;

		NodeRequirement nr = new NodeRequirement();
		nr.setVirtualApplicanceID(TEST_EVN_GUID_APP);
		nr.setCpuNum(2);
		nr.setMemorySize(512);
		nr.setNeedPublicIP(true);
		
		nr.setVmDeployPolicer(ONEVMDeployPolicier.class.getName());
		HashMap<String, String> deployParams = 
			new HashMap<String, String>();
		deployParams.put("random", "");
		nr.setVmDeployParams(deployParams);
		nrmap.put("0", nr);
		
		desc = "Added Virtual Cluster in VM partition for test";
		vmCluster = xmmClient.createVirtualCluster(parguid, clustername,
				nodeMatchMaker, tenantId, null, nrmap, effectiveTime, 0,
				expireTime, null, desc);
		assertNotNull(vmCluster);
		log.info(desc);
		
		/**
		 * Add a virtual cluster for a general partition.
		 */
		parguid = genPartition.getGuid();
		clustername = TEST_EVN_NAME_GENCLUSTER;
		pnnodeip = new String[] { TestConstants.TEST_COMMON_SERVER 
							};
		nrmap.clear();
		for (int i = 0; i < pnnodeip.length; i++) {
			NodeRequirement tmpNr = new NodeRequirement();
			tmpNr.setPartitionId(parguid);
			tmpNr.setPrivateIP(pnnodeip[i]);
			nrmap.put(pnnodeip[i], tmpNr);
		}
		desc = "Added Virtual Cluster in general partition for test";
		genCluster = xmmClient.createVirtualCluster(parguid, clustername,
				nodeMatchMaker, tenantId, null, nrmap, effectiveTime, 0,
				expireTime, null, desc);
		assertNotNull(vmCluster);
		log.info(desc);
		
		return vc;
	}
	public static void destroyVirtualCluster() throws Exception{
		
		xmmClient.destroyVirtualCluster(vmCluster.getGuid());
		log.info("Destory the test cluster in the VM partition : "
				+ TEST_EVN_NAME_VMCLUSTER);
		
		xmmClient.destroyVirtualCluster(genCluster.getGuid());
		log.info("Destory the test cluster in the general partition : "
				+ TEST_EVN_NAME_GENCLUSTER);
	}
}
