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
import org.lingcloud.molva.xmm.util.XMMConstants;
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
	private static PhysicalNode vmmPhyNode = null;
	private static PhysicalNode genPhyNode = null;
	private static VirtualCluster vmCluster = null;
	private static VirtualCluster genCluster = null;

	@BeforeClass
	public static void initializeForAllTest() {
		try {
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
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Destory failed. Reasion: " + e);
			fail();
		}
		xmmClient = null;

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
			Date d = xmmClient.getServerCurrentTime();
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
			List<Partition> parList = xmmClient.listAllPartition();
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
			List<Partition> parList = xmmClient.listPartition(vmPartition
					.getAssetController());
			assertTrue(parList.size() > 0);
			log.info("listPartition Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}
	@Ignore
	@Test
	public void startVirtualCluster() {
		try {
			xmmClient.startVirtualCluster(vmCluster.getGuid());
			List<VirtualNode> vmnList = xmmClient
					.listVirtualNodeInVirtualCluster(vmCluster.getGuid());
			VirtualNode vn = vmnList.get(0);
	
		assertTrue(vn.getRunningStatus().equals(XMMConstants.MachineRunningState.RUNNING.toString()));
			log.info("startVirtualCluster Test success.");
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
			xmmClient.updateVirtualNodeInfo(vn.getGuid(),vn);
			List<VirtualNode> vnList = xmmClient.searchVirtualNode(new String[]{"name"}, new String[]{"="}, new Object[]{"tom"});
			assertTrue(vnList.size()>0);
			log.info("searchVirtualNode Test success.");

		} catch (Exception e) {
			log.error("Initialze failed. Reason: " + e);
			fail();
		}
		
	}

	@Test
	public void searchPhysicalNode() {
		try {
			vmPhyNode.setName("sun");
			xmmClient.updatePhysicalNodeInfo(vmPhyNode.getGuid(),vmPhyNode);
			List<PhysicalNode> phyList = xmmClient.searchPhysicalNode(new String[]{"name"}, new String[]{"="}, new Object[]{"sun"});
			assertTrue(phyList.size() > 0);
			log.info("searchPhysicalNode Test success.");

		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}

	}
@Ignore
	@Test
	public void stopVirtualCluster() {
		try {
			xmmClient.stopVirtualCluster(vmCluster.getGuid());
			List<VirtualNode> vmnList = xmmClient
					.listVirtualNodeInVirtualCluster(vmCluster.getGuid());
			VirtualNode vn = vmnList.get(0);
	
		assertTrue(vn.getRunningStatus().equals(XMMConstants.MachineRunningState.BOOT.toString()));
			log.info("stopVirtualCluster Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void viewPartition() {
		try {
			Partition vmpart = xmmClient.viewPartition(vmPartition.getGuid());
			assertTrue(vmpart.getGuid().equals(vmPartition.getGuid()));
			Partition phypart = xmmClient.viewPartition(genPartition.getGuid());
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
			vmCluster.setDescription("description1");
			VirtualCluster vmTmp = xmmClient.updateVirtualClusterInfo(
					vmCluster.getGuid(), vmCluster);
			assertTrue(vmTmp.getDescription().equals("description1"));
			log.info("updateVirtualClusterInfo Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}


	@Test
	public void updateVirtualNodeInfo() {
		try {
			vmPhyNode.setDescription("description1");
			PhysicalNode phyTmp = xmmClient.updatePhysicalNodeInfo(
					vmPhyNode.getGuid(), vmPhyNode);
			assertNotNull(phyTmp);
			//assertTrue(phyTmp.getDescription().equals("description1"));
			log.info("updateVirtualNodeInfo Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}
	
	@Test
	public void updatePartitionInfo() {

		try {
			vmPartition.setDescription("description1");
			Partition parTmp = xmmClient.updatePartitionInfo(
					vmPartition.getGuid(), vmPartition);
			assertTrue(parTmp.getDescription().equals("description1"));

			log.info("updatePartitionInfo Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}
	
	@Test
	public void updatePhysicalNodeInfo() {
		try {
			vmPhyNode.setDescription("description1");
			PhysicalNode phyTmp = xmmClient.updatePhysicalNodeInfo(
					vmPhyNode.getGuid(), vmPhyNode);
			assertTrue(phyTmp.getDescription().equals("description1"));
			log.info("updatePhysicalNodeInfo Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}
	
	
	@Test
	public void refreshPhysicalNode() {
		try {
			PhysicalNode phyTmp = xmmClient.refreshPhysicalNode(vmPhyNode
					.getGuid());
			assertTrue(phyTmp.getGuid().equals(vmPhyNode.getGuid()));
			log.info("refreshPhysicalNode Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}
	
	private List<VirtualNode> getVirtualNodeList4VC(VirtualCluster vc) throws Exception{
		List<VirtualNode> vmnList = xmmClient
							.listVirtualNodeInVirtualCluster(vc.getGuid());
		for (int i = 0 ; i < 10 ; i++) {
			if (vmnList != null ) 
				break;
			Thread.sleep(3000);
			System.out.println("Waiting for VNode list for VC - " + vmCluster.getName() );
			vmnList = xmmClient
				.listVirtualNodeInVirtualCluster(vc.getGuid());
		}
		return vmnList;
	}

	@Test
	public void refreshVirtualNode() { 
		try {
			List<VirtualNode> vmnList = null;
			vmnList = this.getVirtualNodeList4VC(vmCluster);
			
			assertNotNull(vmnList);
			VirtualNode vn = vmnList.get(0);
			
			
			VirtualNode vnode = xmmClient.refreshVirtualNode(vn.getGuid());
			assertNotNull(vnode);
			log.info("refreshVirtualNode Test success.");

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Initialze failed. Reason: " + e);
			fail();
		}
		
	}


	@Test
	public void listPhysicalNodeInPartition() {

		try {
			List<PhysicalNode> vmphyList = xmmClient
					.listPhysicalNodeInPartition(vmPartition.getGuid());
			assertTrue(vmphyList.size() > 0);

			log.info("listPhysicalNodeInPartition Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}

	}
	@Test
	public void listPhysicalNodeInVirtualCluster() {

		try {

			List<PhysicalNode> vmphyList = xmmClient
					.listPhysicalNodeInVirtualCluster(genCluster.getGuid());
	
			assertTrue(vmphyList.size() > 0);
			log.info("listPhysicalNodeInVirtualCluster Test success.");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}


	@Test
	public void listVirtualNodeInPartition() {
		try {
			List<VirtualNode> vmnList = xmmClient
					.listVirtualNodeInPartition(vmPartition.getGuid());
			assertTrue(vmnList.size() > 0);

			log.info("listVirtualNodeInPartition Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}

	}

	@Test
	public void listVirtualNodeInVirtualCluster() {
		try {
			List<VirtualNode> vmnList = getVirtualNodeList4VC(vmCluster);
			assertTrue(vmnList.size() > 0);

			log.info("listVirtualNodeInVirtualCluster Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}



	@Test
	public void viewVirtualCluster() {
		try {
			VirtualCluster vml = xmmClient.viewVirtualCluster(vmCluster
					.getGuid());
			assertTrue(vml.getGuid().equals(vmCluster.getGuid()));
			log.info("viewVirtualCluster Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void searchVirtualCluster() {
		try {
				vmCluster.setName("sun");
				xmmClient.updateVirtualClusterInfo(vmCluster.getGuid(),vmCluster);
				List<VirtualCluster> vcList = xmmClient.searchVirtualCluster(new String[]{"name"}, new String[]{"="}, new Object[]{"sun"});
				assertTrue(vcList.size() > 0);
				log.info("searchVirtualCluster Test success.");
			
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}
	

	
	@Test
	public void refreshVirtualCluster() {
		try {
			VirtualCluster vml = xmmClient.refreshVirtualCluster(vmCluster
					.getGuid());
			assertTrue(vml.getGuid().equals(vmCluster.getGuid()));
			log.info("refreshVirtualCluster Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}

	@Test
	public void listVirtualCluster() {
		try {
			List<VirtualCluster> vmlList = xmmClient
					.listVirtualCluster(vmPartition.getGuid());
			assertTrue(vmlList.size() > 0);
			log.info("listVirtualCluster Test success.");
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
			VirtualNode vnode = xmmClient.viewVirtualNode(vn.getGuid());
			assertTrue(vnode.getGuid().equals(vn.getGuid()));
			log.info("viewVirtualNode Test success.");

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
			for(int i = 0  ;i < 10; i++ ){
				List<VirtualNode> vmnList = xmmClient
						.listVirtualNodeInVirtualCluster(vmCluster.getGuid());
			 vn = vmnList.get(0);
			if (vn.getRunningStatus()==XMMConstants.MachineRunningState.RUNNING.toString())
			{
				break;
				}
			Thread.sleep(2000);
			}
		VirtualNode vnode =	xmmClient.stopVirtualNode(vn.getGuid());
		assertTrue(vnode.getRunningStatus().equals(XMMConstants.MachineRunningState.HALT.toString()));
			log.info("stopVirtualNode Test success.");

		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}
	@Ignore
	@Test
	public void startVirtualNode() {
		try {
			VirtualNode vn = null;
			for(int i = 0  ;i < 10; i++ ){
				List<VirtualNode> vmnList = getVirtualNodeList4VC(vmCluster);
			 vn = vmnList.get(0);
			 if (vn.getRunningStatus()==XMMConstants.MachineRunningState.RUNNING.toString())
				{
					break;
					}
		
			
			Thread.sleep(2000);
			}
		VirtualNode vnode =	xmmClient.stopVirtualNode(vn.getGuid());
		VirtualNode vnode1 =	xmmClient.startVirtualNode(vn.getGuid());
		
		for(int j = 0; j<10;j++){
		
			 if (vnode1.getRunningStatus()==XMMConstants.MachineRunningState.RUNNING.toString())
				{
					break;
					}
			 Thread.sleep(3000);
		}
		//assertTrue(vnode1.getRunningStatus().equals(XMMConstants.MachineRunningState.RUNNING.toString()));
			log.info("startVirtualNode Test success.");

		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}


	@Test
	public void viewPhysicalNode() {
		try {
			PhysicalNode phn = xmmClient.viewPhysicalNode(vmPhyNode.getGuid());
			assertNotNull(phn);
			log.info("viewPhysicalNode Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}
	@Ignore
	@Test
	public void startPhysicalNode() {
		try {
			PhysicalNode phn = xmmClient.startPhysicalNode(vmPhyNode.getGuid());
			assertTrue(phn.getRunningStatus().equals(XMMConstants.MachineRunningState.RUNNING.toString()));
			log.info("startPhysicalNode Test success.");
			
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}
	@Ignore
	@Test
	public void stopPhysicalNode() {
		try {
			PhysicalNode phn = xmmClient.stopPhysicalNode(vmPhyNode.getGuid());
			assertTrue(phn.getRunningStatus().equals(XMMConstants.MachineRunningState.STOP.toString()));
			log.info("stopPhysicalNode Test success.");
		} catch (Exception e) {
			log.error("Test failed. Reason: " + e);
			fail();
		}
	}
	@Ignore
	@Test
	public void migrateVirtualNode() {
		try {
			List<VirtualNode> vmnList =getVirtualNodeList4VC(vmCluster);
			VirtualNode vn = vmnList.get(0);
			VirtualNode vnode = xmmClient.migrateVirtualNode(vn.getGuid(), vmmPhyNode.getGuid());
			assertTrue(vnode.getGuid().equals(vn.getGuid()));
			log.info("migrateVirtualNode Test success.");
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
		name = TEST_EVN_NAME_VMPARTION;
		controller = PartitionAC.class.getName();
		nodetype = PartitionAC.VM;
		preInstalledSoft = "No software.";
		desc = "VM partition for XMMClient test.";
		attr.put(PartitionAC.REQUIRED_ATTR_NODETYPE, nodetype);
		attr.put(PartitionAC.ATTR_NODE_PRE_INSTALLED_SOFT, preInstalledSoft);
		vmPartition = xmmClient.createPartition(name, controller, attr, desc);
		//assertNotNull(vmPartition);

		/**
		 * Create general partition
		 */
		name = TEST_EVN_NAME_GENPARTION;
		controller = PartitionAC.class.getName();
		nodetype = PartitionAC.GENERAL;
		preInstalledSoft = "No software.";
		desc = "General partition for XMMClient test.";
		attr.put(PartitionAC.REQUIRED_ATTR_NODETYPE, nodetype);
		attr.put(PartitionAC.ATTR_NODE_PRE_INSTALLED_SOFT, preInstalledSoft);
		genPartition = xmmClient.createPartition(name, controller, attr, desc);
		assertNotNull(genPartition);

		return par;
	}

	private static void destoryPartion() throws Exception {

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
		vmPhyNode = xmmClient.addPhysicalNode(vmPartition.getGuid(), privateip,
				publicip, controller, redeploy, attr, description);
		assertNotNull(vmPhyNode);
		xmmClient.refreshPhysicalNode(vmPhyNode.getGuid());
		log.info(description);

		/**
		 * Add a physical node to the general partition.
		 */
		privateip = TestConstants.TEST_COMMON_SERVER;
		publicip = privateip;
		controller = PPNPNController.class.getName();
		description = "Phyical Node for general partition test.";
		genPhyNode = xmmClient.addPhysicalNode(genPartition.getGuid(),
				privateip, publicip, controller, redeploy, attr, description);
		//assertNotNull(genPhyNode);
		xmmClient.refreshPhysicalNode(genPhyNode.getGuid());
		
		VirtualClient vc = null;
		PhysicalNode pn = null;
		vc = VirtualManager.getInstance().getVirtualClient();
		pn = vc.getVMProvisionNode(vmPhyNode);
		xmmClient.refreshPhysicalNode(vmPhyNode.getGuid());
		xmmClient.refreshPhysicalNode(genPhyNode.getGuid());
		
		for (int i= 0 ; i < 100 ; i++) {
			if (pn.getFreeCpu() > 0) {
				break;
			}
			System.out.println("Wait for physical node in vm partition. Free CPU in the node : " + pn.getFreeCpu());
			Thread.sleep(3000);
			pn = vc.getVMProvisionNode(vmPhyNode);
			pn = xmmClient.refreshPhysicalNode(vmPhyNode.getGuid());
		}
		
		log.info(description);

		return vmPhyNode;
	}

	private static void removePhysicalNode() throws Exception {

		Thread.sleep(5000);
		xmmClient.removePhysicalNode(vmPhyNode.getGuid());
		log.info("Remove physical node " + vmPhyNode.getName());
		xmmClient.removePhysicalNode(genPhyNode.getGuid());
		log.info("Remove physical node " + genPhyNode.getName());
	}

	private static VirtualCluster createVirtualCluster() throws Exception {
		VirtualCluster vc = null;

		String parguid;
		String clustername;
		String nodeMatchMaker = VirtualClusterAMM.class.getName();
		
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
		HashMap<String, String> deployParams = new HashMap<String, String>();
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
		pnnodeip = new String[] { TestConstants.TEST_COMMON_SERVER };
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
		assertNotNull(genCluster);
		log.info(desc);

		return vc;
	}

	public static void destroyVirtualCluster() throws Exception {

		xmmClient.destroyVirtualCluster(vmCluster.getGuid());
		log.info("Destory the test cluster in the VM partition : "
				+ TEST_EVN_NAME_VMCLUSTER);

		xmmClient.destroyVirtualCluster(genCluster.getGuid());
		log.info("Destory the test cluster in the general partition : "
				+ TEST_EVN_NAME_GENCLUSTER);
	}
}
