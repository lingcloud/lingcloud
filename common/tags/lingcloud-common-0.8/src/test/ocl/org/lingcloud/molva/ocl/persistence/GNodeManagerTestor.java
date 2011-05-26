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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <strong>Purpose:</strong><br>
 * Test GNodeManager.
 * 
 * @version 1.0.0 2011-4-7<br>
 * @author Ruijian Wang<br>
 * 
 */
public class GNodeManagerTestor extends TestCase {
	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(GNodeManagerTestor.class);

	public GNodeManagerTestor(String name) {
		super(name);
	}

	/**
	 * to determine which functions should be test.
	 * 
	 * @return the Test object.
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite();
		 suite.addTest(new GNodeManagerTestor("testRegisterGNode"));
		 suite.addTest(new GNodeManagerTestor("testUpdateGNode"));
		 suite.addTest(new GNodeManagerTestor("testSearchGNode"));
		 suite.addTest(new GNodeManagerTestor("testUnregisterGNode"));
		

		return suite;
	}

	public void testRegisterGNode() {
		log.info("begin testRegisterGNode...");
		try {
			GNodeManager gnm = new GNodeManager();
			GNode gn = new GNode();
			gn.setType(GNodeConstants.GNODETYPE_SERVICE);
			GNode rst = gnm.register(gn);
			assertTrue(rst.getGuid() != null);
			assertTrue(gn.getGuid() == null);
			GNode gnDb = gnm.locate(rst.getGuid());
			assertEquals(rst.getGuid(), gnDb.getGuid());
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	public void testUpdateGNode() {
		log.info("begin testUpdateGNode...");
		try {
			GNodeManager gnm = new GNodeManager();

			String query = "type=?";
			Object[] values = new Object[] { GNodeConstants.GNODETYPE_SERVICE };

			List rst = gnm.search(query, values);
			log.info("the init size: " + rst.size());

			for (int i = 0; i < rst.size(); ++i) {
				GNode gn = (GNode) rst.get(i);

				gn.setOwnerDN("wrj@software" + i);

				gn.setDescription(gn.getDescription() + "-" + i);

				if (gn.getAttributes().size() > 0) {
					// add new one.
					gn.getAttributes().put("attr1", "value1-" + i);
					gn.getAttributes().put("attr2", "value2-" + i);
					// modify old one
					gn.getAttributes().put("testattr1",
							"xxxx--modified at " + new Date() + "-" + i);
					gn.getAttributes().remove("testattr2");
				}

				gnm.update(gn);

				GNode tmp = gnm.locate(gn.getGuid());
				// These are the GNodeInfo fields.

				assertTrue(HashFunction.isObjEqual(tmp.getDescription(),
						gn.getDescription()));
				assertTrue(HashFunction.isObjEqual(tmp.getExport(),
						gn.getExport()));
				if (gn.getAttributes().size() > 0) {
					assertTrue(HashFunction.isObjEqual(
							tmp.getAttributes().get("attr1"), gn
									.getAttributes().get("attr1")));
					assertTrue(HashFunction.isObjEqual(
							tmp.getAttributes().get("attr2"), gn
									.getAttributes().get("attr2")));
					assertTrue(HashFunction.isObjEqual(
							tmp.getAttributes().get("testattr1"), gn
									.getAttributes().get("testattr1")));
					assertTrue(tmp.getAttributes().get("testattr2") == null);
				}

				// It's the GNode part.
				assertTrue(tmp.getVersion() == (gn.getVersion() + 1));
				assertTrue(HashFunction.isObjEqual(tmp.getOwnerDN(),
						gn.getOwnerDN()));
				log.info("update success : i = " + i);
			}

		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	public void testSearchGNode() {
		log.info("begin testSearchGNode...");
		try {
			GNodeManager gnm = new GNodeManager();

			String query = "type=?";
			Object[] values = new Object[] { GNodeConstants.GNODETYPE_SERVICE };

			log.info("before search ...");
			List rst = gnm.search(query, values);
			log.info("after search ...");

			log.info("the search result is : \n");
			for (int i = 0; i < rst.size(); ++i) {
				showGNode((GNode) rst.get(i));
			}

			assertTrue(rst.size() >= 0);

		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	public void testUnregisterGNode() {
		log.info("begin testUnregisterGNode...");
		try {
			GNodeManager gnm = new GNodeManager();

			String query = "type=?";
			Object[] values = new Object[] { GNodeConstants.GNODETYPE_SERVICE };

			List rst = gnm.search(query, values);
			log.info("the init size: " + rst.size());

			for (int i = 0; i < rst.size(); ++i) {
				GNode gn = (GNode) rst.get(i);
				log.info("delete item " + i + " id = " + gn.getGuid());
				List removed = gnm.unregister(gn.getGuid());
				log.info("delete count : " + removed.size());
			}

			List finalRst = gnm.search(query, values);
			assertEquals(finalRst.size(), 0);

		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	private void showGNode(GNode gn) {
		log.info("GNode id " + gn.getGuid());
		log.info("OwnerDN " + gn.getOwnerDN());
	}
}
