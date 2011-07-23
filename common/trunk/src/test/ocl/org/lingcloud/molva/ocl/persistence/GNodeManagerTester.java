/* 
 * @(#)GNodeManagerTestor.java 2011-5-1
 * 
 * Copyright (C) 2008-2011, 
 * LingCloud Team, 
 * Institute of Computing Technology, 
 * Chinese Academy of Sciences. 
 * P.O.Box 2704, 100190, Beijing, China. 
 * 
 * http://lingcloud.org 
 * 
 */
package org.lingcloud.molva.ocl.persistence;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.ocl.util.HashFunction;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import static org.lingcloud.molva.ocl.util.ConfigUtil.LINGCLOUD_HOME_PROPERTY;
import static org.lingcloud.molva.test.util.TestConstants.LINGCLOUD_HOME;

/**
 * <strong>Purpose:</strong><br>
 * Test GNodeManager.
 * 
 * @version 1.0.0 2011-4-7<br>
 * @author Ruijian Wang<br>
 * 
 */
public class GNodeManagerTester {
	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(GNodeManagerTester.class);
	private static GNodeManager gnodeManager = null;
	private static String homeProperty = null;
	private static GNode gnode = null;

	@BeforeClass
	public static void initializeForAllTest() {
		gnodeManager = new GNodeManager();
		homeProperty = System.getProperty(LINGCLOUD_HOME_PROPERTY);
		System.getProperties().put(LINGCLOUD_HOME_PROPERTY, LINGCLOUD_HOME);

		try {
			// initialize database connection
			gnode = gnodeManager
					.locate("B4D264602943EDA931FD4DC102F6D161E3E52CEB");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void destroyForAllTest() {
		if (homeProperty != null) {
			System.setProperty(LINGCLOUD_HOME_PROPERTY, homeProperty);
		}
	}

	@Before
	public void initialize() throws Exception {
		gnode = null;
	}

	@After
	public void destroy() throws Exception {
		if (gnode != null) {
			gnodeManager.unregister(gnode.getGuid());
		}
	}

	@Test
	public void register() {
		log.info("begin register...");
		try {
			GNode gn = new GNode();
			gn.setType(GNodeConstants.GNODETYPE_SERVICE);
			gnode = gnodeManager.register(gn);
			assertTrue(gnode.getGuid() != null);
			assertTrue(gn.getGuid() == null);
			GNode gnDb = gnodeManager.locate(gnode.getGuid());
			assertNotNull(gnDb);
			// assertTrue(gnode.getGuid().equals(gnDb.getGuid()));
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void update() {
		log.info("begin update...");
		try {
			GNode gn = new GNode();
			gn.setType(GNodeConstants.GNODETYPE_SERVICE);
			gnode = gnodeManager.register(gn);
			gn = gnode;

			gn.setOwnerDN("Ruijian Wang");

			gn.setDescription("Test.");

			if (gn.getAttributes().size() > 0) {
				// add new one.
				gn.getAttributes().put("attr1", "value1");
				gn.getAttributes().put("attr2", "value2");
				// modify old one
				gn.getAttributes().put("testattr1",
						"xxxx--modified at " + new Date());
				gn.getAttributes().remove("testattr2");
			}

			gnodeManager.update(gn);

			GNode tmp = gnodeManager.locate(gn.getGuid());

			assertTrue(HashFunction.isObjEqual(tmp.getDescription(),
					gn.getDescription()));
			assertTrue(HashFunction.isObjEqual(tmp.getExport(), 
					gn.getExport()));
			if (gn.getAttributes().size() > 0) {
				assertTrue(HashFunction.isObjEqual(
						tmp.getAttributes().get("attr1"), gn.getAttributes()
								.get("attr1")));
				assertTrue(HashFunction.isObjEqual(
						tmp.getAttributes().get("attr2"), gn.getAttributes()
								.get("attr2")));
				assertTrue(HashFunction.isObjEqual(
						tmp.getAttributes().get("testattr1"), gn
								.getAttributes().get("testattr1")));
				assertTrue(tmp.getAttributes().get("testattr2") == null);
			}

			// It's the GNode part.
			assertTrue(tmp.getVersion() == (gn.getVersion() + 1));
			assertTrue(HashFunction.isObjEqual(tmp.getOwnerDN(),
					gn.getOwnerDN()));
			log.info("update success");

		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void search() {
		log.info("begin search...");
		try {
			GNode gn = new GNode();
			gn.setType(GNodeConstants.GNODETYPE_SERVICE);
			gnode = gnodeManager.register(gn);

			String query = "type=?";
			Object[] values = new Object[] { GNodeConstants.GNODETYPE_SERVICE };

			log.info("before search ...");
			List<GNode> rst = gnodeManager.search(query, values);
			log.info("after search ...");

			log.info("the search result is : \n");
			for (int i = 0; i < rst.size(); ++i) {
				showGNode((GNode) rst.get(i));
			}

			assertTrue(rst.size() > 0);

		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void unregister() {
		log.info("begin unregister...");
		try {
			GNode gn = new GNode();
			gn.setType(GNodeConstants.GNODETYPE_SERVICE);
			gnode = gnodeManager.register(gn);
			gn = gnode;

			log.info("delete item id = " + gn.getGuid());
			List<GNode> removed = gnodeManager.unregister(gn.getGuid());
			log.info("delete count : " + removed.size());

			GNode tmp = gnodeManager.locate(gn.getGuid());
			assertNull(tmp);
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	private void showGNode(GNode gn) {
		log.info("GNode id: " + gn.getGuid());
		log.info("OwnerDN: " + gn.getOwnerDN());
	}
}
