/*
 *  @(#)VirtualApplianceManagerTester.java  Jul 25, 2011
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
package org.lingcloud.molva.test.xmm.vam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.ocl.util.ConfigUtil;
import org.lingcloud.molva.test.util.TestConstants;
import org.lingcloud.molva.test.util.TestUtils;
import org.lingcloud.molva.xmm.vam.pojos.VACategory;
import org.lingcloud.molva.xmm.vam.pojos.VAFile;
import org.lingcloud.molva.xmm.vam.pojos.VirtualAppliance;
import org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager;
import org.lingcloud.molva.xmm.vam.util.VAMConfig;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2011-7-25<br>
 * @author Maosen Sun<br>
 * 
 */
public class VirtualApplianceManagerTester {
	private static Log log = LogFactory
			.getLog(VirtualApplianceManagerTester.class);
	private static VirtualApplianceManager vam = null;
	private VirtualAppliance vaNew = null;
	private VAFile vafile = null;

	@BeforeClass
	public static void initializeForAllTest() throws Exception {
		vam = VAMUtil.getVAManager();

	}

	@AfterClass
	public static void destroyForAllTest() throws Exception {
		vam = null;
	}

	@Before
	public void initialize() throws Exception {
		vaNew = null;
		vafile = null;
	}

	@After
	public void destroy() throws Exception {
		if (vaNew != null) {
			try {
				vam.removeAppliance(vaNew.getGuid());
				Thread.sleep(TimeUnit.SECONDS.toMillis(2));
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
		}
		if (vafile != null) {
			try {
				vam.removeFile(vafile.getGuid());
				Thread.sleep(TimeUnit.SECONDS.toMillis(2));
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
		}
	}
	
	public VAFile addExampleDisc() throws Exception {
		String imagePath = ConfigUtil.getHomePath()
		+ "/bin/images/example/example.iso";

		File imagetransfer = new File(VAMConfig.getDiscUploadDirLocation()
				+ "/example.iso");

			TestUtils.copyFile(imagePath, imagetransfer.getAbsolutePath());

		assertTrue(imagetransfer.exists());
		
		String discName = "testDisc";
		String type = VAMConstants.VAD_DISK_TYPE_OS;
		String format = VAMConstants.VAF_FORMAT_ISO;
		String location = "example.iso";
		String os = "Linux";
		String osversion = "";
		List<String> appl = new ArrayList<String>();
		appl.add("application");
		VAFile discFile = new VAFile();
		discFile.setId(discName);
		discFile.setLocation(location);
		discFile.setFormat(format);
		vafile = vam.addDisc(discFile, location, type, os, 
				osversion, appl);
		
		if (vafile == null) {
			return null;
		}
		
		boolean validated = false;
		for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++) {
			VAFile disc = vam.queryFile(vafile.getGuid());
			if (disc.getState() == VAMConstants.STATE_READY) {
				validated = true;
				break;
			}
			Thread.sleep(TimeUnit.SECONDS.toMillis(2));
		}
		
		assertTrue(validated);
		
		vafile = vam.queryFile(vafile.getGuid());
		return vafile;
	}

	@Test
	public void addDisc() {
		log.info("begin addDisc...");
		try {
			vafile = addExampleDisc();
			assertNotNull(vafile);
			
			log.info("addDisc Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}
	
	private VirtualAppliance addExampleAppliance(String guid) throws Exception {
		String imagePath = ConfigUtil.getHomePath()
					+ "/bin/images/example/example.img";
		String filename = "example." + VAMUtil.genGuid() + ".img";
		
		File imagetransfer = new File(VAMConfig.getDiskUploadDirLocation()
				+ "/" + filename);
		
		TestUtils.copyFile(imagePath, imagetransfer.getAbsolutePath());

		assertTrue(imagetransfer.exists());
		
		String format = VAMConfig.getImageFormat();
		String loader = VAMConstants.VA_BOOTLOAD_HVM;
		String name = "testAppliance";
		String location = filename;
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
		vaNew = vam.addAppliance(va, location);
		
		boolean validated = false;
		for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++) {
			VirtualAppliance appliance = vam.queryAppliance(vaNew.getGuid());
			if (appliance.getState() == VAMConstants.STATE_READY) {
				validated = true;
				break;
			}
			Thread.sleep(TimeUnit.SECONDS.toMillis(2));
		}
		assertTrue(validated);
		vaNew = vam.queryAppliance(vaNew.getGuid());
		
		return vaNew;
	}

	@Test
	public void addAppliance() {
		log.info("begin addAppliance...");
		try {
			String guid = TestConstants.TEST_EVN_GUID_APP;
			
			vaNew = addExampleAppliance(guid);
			assertNotNull(vaNew);
			
			log.info("addAppliance Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void updateAppliance() {
		log.info("begin updateAppliance...");
		
		try {
			String guid = TestConstants.TEST_EVN_GUID_APP;

			vaNew = addExampleAppliance(guid);
			assertNotNull(vaNew);
			
			VirtualAppliance va = vam.queryAppliance(vaNew.getGuid());
			va.setDescription("update");
			VirtualAppliance updated = vam.updateAppliance(va);
			assertNotNull(updated);
			assertTrue(updated.getDescription().equals("update"));
			
			log.info("updateAppliance Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void removeAppliance() {
		log.info("begin removeAppliance...");
		try {
			String guid = TestConstants.TEST_EVN_GUID_APP;

			VirtualAppliance va = addExampleAppliance(guid);
			assertNotNull(va);
			
			vaNew = null;
			assertTrue(vam.removeAppliance(va.getGuid()));
			log.info("removeAppliance Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}


	@Test
	public void queryAppliance() {
		log.info("begin queryAppliance...");
		
		try {
			String guid = TestConstants.TEST_EVN_GUID_APP;

			vaNew = addExampleAppliance(guid);
			assertNotNull(vaNew);
			
			assertNotNull(vam.queryAppliance(vaNew.getGuid()));
			log.info("queryAppliance Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void getAllAppliance() {
		log.info("begin getAllAppliance...");
		
		try {
			String guid = TestConstants.TEST_EVN_GUID_APP;

			vaNew = addExampleAppliance(guid);
			assertNotNull(vaNew);
			
			assertTrue(vam.getAllAppliance().size() > 0);
			log.info("getAllAppliance Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		} 
	}


	@Test
	public void getMakingAppliances() {
		log.info("begin getMakingAppliances...");
		
		try {
			String guid = TestConstants.TEST_EVN_GUID_APP;

			vaNew = addExampleAppliance(guid);
			assertNotNull(vaNew);
			
			VirtualAppliance vaMake = vam.makeAppliance(vaNew, vaNew.getGuid(),
					TestConstants.K / 2);
			assertNotNull(vaMake);
			vaMake = vam.queryAppliance(vaNew.getGuid());
			assertTrue(vaMake.getState() == VAMConstants.STATE_MAKING);
			assertTrue(vam.getMakingAppliances().size() > 0);
			
			log.info("getMakingAppliances Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		} 
	}

	@Test
	public void getAppliancesByCategory() {
		log.info("begin getAppliancesByCategory...");
		
		try {
			String guid = TestConstants.TEST_EVN_GUID_APP;

			vaNew = addExampleAppliance(guid);
			assertNotNull(vaNew);
			
		    assertTrue(vam.getAppliancesByCategory(
		    		vaNew.getCategory()).size() > 0);
			
			log.info("getAppliancesByCategory Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}


	@Test
	public void allocateAppliance() {
		log.info("begin allocateAppliance...");
		
		try {
			String guid = TestConstants.TEST_EVN_GUID_APP;

			vaNew = addExampleAppliance(guid);
			assertNotNull(vaNew);

            VirtualAppliance vaAllocate = vam.allocateAppliance(
            		vaNew.getGuid(), null, null);			
            assertNotNull(vaAllocate);
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            
            vam.revokeAppliance(vaAllocate.getGuid(), null, null);
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            
			log.info("allocateAppliance Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		} 
	}

	@Test
	public void revokeAppliance() {
		log.info("begin revokeAppliance...");
		
		try {
			String guid = TestConstants.TEST_EVN_GUID_APP;

			vaNew = addExampleAppliance(guid);
			assertNotNull(vaNew);

            VirtualAppliance vaAllocate = vam.allocateAppliance(
            		vaNew.getGuid(), null, null);
            assertNotNull(vaAllocate);
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            
            assertTrue(vam.revokeAppliance(vaAllocate.getGuid(), null, null));
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            
			log.info("revokeAppliance Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		} 
	}


	@Test
	public void makeAppliance() {
		log.info("begin makeAppliance...");
		
		try {
			String guid = TestConstants.TEST_EVN_GUID_APP;

			vaNew = addExampleAppliance(guid);
			assertNotNull(vaNew);
			
			VirtualAppliance vaMake = vam.makeAppliance(vaNew, vaNew.getGuid(),
					TestConstants.K / 2);
			assertNotNull(vaMake);
			vaMake = vam.queryAppliance(vaNew.getGuid());
			assertTrue(vaMake.getState() == VAMConstants.STATE_MAKING);
			
			log.info("makeAppliance Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		} 
	}

	@Test
	public void startAndStopAppliance() {
		log.info("begin startAndStopAppliance...");
		
		try {
			String guid = TestConstants.TEST_EVN_GUID_APP;

			vaNew = addExampleAppliance(guid);
			assertNotNull(vaNew);
			
			VirtualAppliance vaMake = vam.makeAppliance(vaNew, vaNew.getGuid(),
					TestConstants.K / 2);
			assertNotNull(vaMake);
			vaMake = vam.queryAppliance(vaNew.getGuid());
			assertTrue(vaMake.getState() == VAMConstants.STATE_MAKING);
			
			vam.startAppliance(vaMake.getGuid());
			List<String> listname = new ArrayList<String>();
			listname.add(vaMake.getVmName());
			boolean validated = false;
			for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++) {
				String status = vam.getApplianceRunningState(listname).get(0);
				if (status.equals(
						VAMConstants.MAKE_APPLIANCE_VM_STATE_RUNNING)) {
					validated = true;
					break;
				}
				
				Thread.sleep(TestConstants.RETRY_INTERVAL);
			}
			
			assertTrue(validated);
			
			vam.stopAppliance(vaMake.getGuid());
			validated = false;
			for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++) {
				String status = vam.getApplianceRunningState(listname).get(0);
				if (status.equals(
						VAMConstants.MAKE_APPLIANCE_VM_STATE_STOP)) {
					validated = true;
					break;
				}
				
				Thread.sleep(TestConstants.RETRY_INTERVAL);
			}
			
			assertTrue(validated);
			
			log.info("startAndStopAppliance Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		} 
	}


	@Test
	public void getApplianceRunningState() {
		log.info("begin getApplianceRunningState...");
		
		try {
			String guid = TestConstants.TEST_EVN_GUID_APP;

			vaNew = addExampleAppliance(guid);
			assertNotNull(vaNew);
			
			VirtualAppliance vaMake = vam.makeAppliance(vaNew, vaNew.getGuid(),
					TestConstants.K / 2);
			assertNotNull(vaMake);
			vaMake = vam.queryAppliance(vaNew.getGuid());
			assertTrue(vaMake.getState() == VAMConstants.STATE_MAKING);
			
			vam.startAppliance(vaMake.getGuid());
			List<String> listname = new ArrayList<String>();
			listname.add(vaMake.getVmName());
			boolean validated = false;
			for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++) {
				String status = vam.getApplianceRunningState(listname).get(0);
				if (status.equals(
						VAMConstants.MAKE_APPLIANCE_VM_STATE_RUNNING)) {
					validated = true;
					break;
				}
				
				Thread.sleep(TestConstants.RETRY_INTERVAL);
			}
			
			assertTrue(validated);

			assertTrue(vam.getApplianceRunningState(listname).get(0).
					equals(VAMConstants.MAKE_APPLIANCE_VM_STATE_RUNNING));
			
			log.info("getApplianceRunningState Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		} 
	}


	@Test
	public void saveAppliance() {
		log.info("begin saveAppliance...");
		
		try {
			String guid = TestConstants.TEST_EVN_GUID_APP;

			vaNew = addExampleAppliance(guid);
			assertNotNull(vaNew);
			
			VirtualAppliance vaMake = vam.makeAppliance(vaNew, vaNew.getGuid(),
					TestConstants.K / 2);
			assertNotNull(vaMake);
			vaMake = vam.queryAppliance(vaNew.getGuid());
			assertTrue(vaMake.getState() == VAMConstants.STATE_MAKING);
			
			VirtualAppliance vaSave = vam.saveAppliance(vaMake);
	     	assertNotNull(vaSave);
	     	assertTrue(vaSave.getState() == VAMConstants.STATE_READY);
			log.info("saveAppliance Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}


	@Test
	public void getApplianceConfig() {
		log.info("begin getApplianceConfig...");
		
		try {
			String guid = TestConstants.TEST_EVN_GUID_APP;

			vaNew = addExampleAppliance(guid);
			assertNotNull(vaNew);
			
			VirtualAppliance vaMake = vam.makeAppliance(vaNew, vaNew.getGuid(),
					TestConstants.K / 2);
			assertNotNull(vaMake);
			vaMake = vam.queryAppliance(vaNew.getGuid());
			assertTrue(vaMake.getState() == VAMConstants.STATE_MAKING);
			
	        assertNotNull(vam.getApplianceConfig(vaMake.getGuid()));
			log.info("getApplianceConfig Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		} 
	}


	@Test
	public void changeDisc() {
		log.info("begin changeDisc...");
		
		try {
			String guid = TestConstants.TEST_EVN_GUID_APP;

			vaNew = addExampleAppliance(guid);
			assertNotNull(vaNew);
			
			VirtualAppliance vaMake = vam.makeAppliance(vaNew, vaNew.getGuid(),
					TestConstants.K / 2);
			assertNotNull(vaMake);
			vaMake = vam.queryAppliance(vaNew.getGuid());
			assertTrue(vaMake.getState() == VAMConstants.STATE_MAKING);
			
			vafile = addExampleDisc();
			assertNotNull(vafile);
			
	        vam.changeDisc(vaNew.getGuid(), vafile.getGuid());
	        List<String> discs = vam.queryAppliance(vaNew.getGuid())
    		.getDiscs();
	        assertNotNull(discs);
	        assertTrue(discs.size() > 0);
	        assertTrue(discs.get(0).equals(vafile.getGuid()));
			
			log.info("changeDisc Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		} 
	}

	@Test
	public void addCategory() {
		log.info("begin addCategory...");
		try {
			String cat = "testCategory";
			VACategory vacat = vam.addCategory(cat);
			assertNotNull(vacat);
			
			vam.removeCategory(vacat.getGuid());
			log.info("addCategory Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void queryCategory() {
		log.info("begin queryCategory...");
		try {
			String cat = "testCategory";
			VACategory vacat = vam.addCategory(cat);
			assertNotNull(vacat);
			
			assertNotNull(vam.queryCategory(vacat.getGuid()));
			vam.removeCategory(vacat.getGuid());
			log.info("queryCategory Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}


	@Test
	public void getAllCategory() {
		log.info("begin getAllCategory...");
		try {
			String cat = "category";
			VACategory vacat = vam.addCategory(cat);	
			assertNotNull(vacat);
			
			assertTrue(vam.getAllCategory().size() > 0);
			vam.removeCategory(vacat.getGuid());
			log.info("getAllCategory Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void removeCategory() {
		log.info("begin removeCategory...");
		try {
			String cat = "category";
			VACategory vacat = vam.addCategory(cat);
			assertNotNull(vacat);
			
			assertTrue(vam.removeCategory(vacat.getGuid()));
			log.info("removeCategory Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}
}
