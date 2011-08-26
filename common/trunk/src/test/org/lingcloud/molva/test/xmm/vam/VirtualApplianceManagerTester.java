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
import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetManagerImpl;
import org.lingcloud.molva.ocl.util.ConfigUtil;
import org.lingcloud.molva.test.ocl.AssetManagerTester;
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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
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
	private static VirtualAppliance va = null;
	private static VirtualApplianceManager vam = null;

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
	}

	@After
	public void destroy() throws Exception {
      va = null;
	}

	@Test
	public void addDisc() {
		try {
			log.info("stopVirtualNode Test success.");
			String imagePath = ConfigUtil.getHomePath()
					+ "/bin/images/example/example.iso";
            System.out.println(imagePath);
			
			File image = new File(imagePath);
			File imagetransfer = new File(VAMConfig.getDiskUploadDirLocation()
					+ "/example.iso");
			if(!(imagetransfer.exists()))
			{
				TestUtils.copyFile(imagePath, VAMConfig.getDiscUploadDirLocation()
						+ "/example.iso");
			}
			assertTrue(image.exists());

			String discName = "disc";
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
			VAFile disFile = vam.addDisc(discFile, location, type, os, osversion,
					appl);
			assertNotNull(disFile);
			assertTrue(vam.removeFile(disFile.getGuid()));
			log.info("stopVirtualNode Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void addAppliance() {
		log.info("begin addAppliance...");
		try {
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

			File imagetransfer = new File(VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			if(!(imagetransfer.exists()))
			{
				TestUtils.copyFile(imagePath, VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			}
String format = VAMConstants.VAF_FORMAT_RAW;
			//String format = VAMConfig.getImageFormat();
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
			VirtualAppliance vaNew = vam.addAppliance(va, location);
			assertNotNull(vaNew);
			assertTrue(vam.removeAppliance(vaNew.getGuid()));
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

			File imagetransfer = new File(VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			if(!(imagetransfer.exists()))
			{
				TestUtils.copyFile(imagePath, VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			}

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
			VirtualAppliance vaNew = vam.addAppliance(va, location);
			assertNotNull(vam.updateAppliance(vaNew));
			assertTrue(vam.removeAppliance(vaNew.getGuid()));
			log.info("updateAppliance Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}
@Ignore
	@Test
	public void removeAppliance() {
		log.info("begin removeAppliance...");
		try {
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

			TestUtils.copyFile(imagePath, VAMConfig.getDiskUploadDirLocation()
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
		VirtualAppliance vaNew = vam.addAppliance(va, location);
			assertNotNull(va);
			assertTrue(vam.removeAppliance(vaNew.getGuid()));
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
			File imagetransfer = new File(VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			if(!(imagetransfer.exists()))
			{
				TestUtils.copyFile(imagePath, VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			}
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
			VirtualAppliance vaNew = vam.addAppliance(va, location);
			assertNotNull(vam.queryAppliance(vaNew.getGuid()));
			assertTrue(vam.removeAppliance(vaNew.getGuid()));
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
			File imagetransfer = new File(VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			if(!(imagetransfer.exists()))
			{
				TestUtils.copyFile(imagePath, VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			}
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
			VirtualAppliance vaNew = vam.addAppliance(va, location);
			assertTrue(vam.getAllAppliance().size()>0);
			assertTrue(vam.removeAppliance(vaNew.getGuid()));
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
			File imagetransfer = new File(VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			if(!(imagetransfer.exists()))
			{
				TestUtils.copyFile(imagePath, VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			}
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
			VirtualAppliance vaNew = vam.addAppliance(va, location);
			VirtualAppliance vaMake = vam.makeAppliance(vaNew,null , memsize);
			assertTrue(vam.getMakingAppliances().size()>0);
			assertTrue(vam.removeAppliance(vaMake.getGuid()));
			assertTrue(vam.removeAppliance(vaNew.getGuid()));
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
			File imagetransfer = new File(VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			if(!(imagetransfer.exists()))
			{
				TestUtils.copyFile(imagePath, VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			}
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
			VirtualAppliance vaNew = vam.addAppliance(va, location);
		    assertTrue(vam.getAppliancesByCategory(va.getCategory()).size()>0);
			
			assertTrue(vam.removeAppliance(vaNew.getGuid()));
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
			File imagetransfer = new File(VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			if(!(imagetransfer.exists()))
			{
				TestUtils.copyFile(imagePath, VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			}
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
			VirtualAppliance vaNew = vam.addAppliance(va, location);
            VirtualAppliance vaAllocate = vam.allocateAppliance(va.getGuid(), null, null);			
             assertNotNull(vaAllocate);
             vam.removeAppliance(vaAllocate.getGuid());
            assertTrue(vam.removeAppliance(vaNew.getGuid()));
			log.info("allocateAppliance Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Ignore
	@Test
	public void revokeAppliance() {

	}


	@Test
	public void makeAppliance() {
		log.info("begin makeAppliance...");
		try {
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
			File imagetransfer = new File(VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			if(!(imagetransfer.exists()))
			{
				TestUtils.copyFile(imagePath, VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			}
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
			VirtualAppliance vaNew = vam.addAppliance(va, location);
			VirtualAppliance vaMake = vam.makeAppliance(vaNew,null , 512);
			assertNotNull(vaMake);
			assertTrue(vam.removeAppliance(vaMake.getGuid()));
			assertTrue(vam.removeAppliance(vaNew.getGuid()));
			log.info("makeAppliance Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void startAppliance() {
		log.info("begin startAppliance...");
		try {
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
			File imagetransfer = new File(VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			if(!(imagetransfer.exists()))
			{
				TestUtils.copyFile(imagePath, VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			}
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
			VirtualAppliance vaNew = vam.addAppliance(va, location);
			VirtualAppliance vaMake = vam.makeAppliance(vaNew,null ,memsize);
			vam.startAppliance(vaMake.getGuid());
			Thread.sleep(5000);
			List<String> listname = new ArrayList<String>();
			listname.add(vaMake.getName());
			String status = vam.getApplianceRunningState(listname).get(0);
			assertTrue(status.equals(VAMConstants.MAKE_APPLIANCE_VM_STATE_RUNNING));
			vam.stopAppliance(vaMake.getGuid());
			Thread.sleep(8000);
		   status = vam.getApplianceRunningState(listname).get(0);
		   assertTrue(status.equals(VAMConstants.MAKE_APPLIANCE_VM_STATE_STOP));
			assertTrue(vam.removeAppliance(vaMake.getGuid()));
			assertTrue(vam.removeAppliance(vaNew.getGuid()));
			log.info("startAppliance Test success.");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Ignore
	@Test
	public void stopAppliance() {

	}


	@Test
	public void getApplianceRunningState() {
		log.info("begin getApplianceRunningState...");
		try {
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
			File imagetransfer = new File(VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			if(!(imagetransfer.exists()))
			{
				TestUtils.copyFile(imagePath, VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			}
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
			List<String> listname = new ArrayList<String>();
			listname.add(va.getName());
			VirtualAppliance vaNew = vam.addAppliance(va, location);
			assertNotNull(vam.getApplianceRunningState(listname).size() > 0);
			assertTrue(vam.removeAppliance(vaNew.getGuid()));
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
			File imagetransfer = new File(VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			if(!(imagetransfer.exists()))
			{
				TestUtils.copyFile(imagePath, VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			}
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
			VirtualAppliance vaMake = vam.makeAppliance(va, null, memsize);
	     	assertNotNull(vam.saveAppliance(vaMake));
	     	assertTrue(vam.removeAppliance(vaMake.getGuid()));
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
			File imagetransfer = new File(VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			if(!(imagetransfer.exists()))
			{
				TestUtils.copyFile(imagePath, VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			}
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
		
			VirtualAppliance vaNew = vam.addAppliance(va, location);
	        assertNotNull( vam.getApplianceConfig(vaNew.getGuid()));
			assertTrue(vam.removeAppliance(vaNew.getGuid()));
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
			File imagetransfer = new File(VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			if(!(imagetransfer.exists()))
			{
				TestUtils.copyFile(imagePath, VAMConfig.getDiskUploadDirLocation()
					+ "/example.img");
			}
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
		
			VirtualAppliance vaNew = vam.addAppliance(va, location);
	         vam.changeDisc(vaNew.getGuid(), "none");
	        assertTrue(vam.queryAppliance(vaNew.getGuid()).getDiscs().size() ==0);
			assertTrue(vam.removeAppliance(vaNew.getGuid()));
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
			String cat = "category";
			VACategory vacat = vam.addCategory(cat);
			assertNotNull(vacat);
			assertTrue(vam.removeCategory(vacat.getGuid()));
			log.info("begin addCategory...");
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
			String cat = "category";
			VACategory vacat = vam.addCategory(cat);
			
			assertNotNull(vam.queryCategory(vacat.getGuid()));
			assertTrue(vam.removeCategory(vacat.getGuid()));
			log.info("begin queryCategory...");
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
			assertTrue(vam.getAllCategory().size() > 0);
			assertTrue(vam.removeCategory(vacat.getGuid()));
			log.info("begin getAllCategory...");
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
			assertTrue(vam.removeCategory(vacat.getGuid()));
			log.info("begin removeCategory...");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}
}
