/*
 *  @(#)AssetManagerTester.java  Jul 25, 2011
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
package org.lingcloud.molva.test.ocl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetManagerImpl;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
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
public class AssetManagerTester {
	private static Log log = LogFactory.getLog(AssetManagerTester.class);
	private static AssetManagerImpl assetManager = null;
	private static Asset asset = null;

	@BeforeClass
	public static void initializeForAllTest() {
		assetManager = new AssetManagerImpl();
	}

	@AfterClass
	public static void destroyForAllTest() throws Exception {
	}

	@Before
	public void initialize() throws Exception {
		asset = new Asset();
		asset.setAssetController("lingcontrol");
		asset.setName("test asset");
		asset.setType("test asset");
		asset.setAcl("rwx------");
	}

	@After
	public void destroy() throws Exception {
		if (asset != null && asset.getGuid() != null) {
			assetManager.remove(asset.getGuid(), true);
		}
	}

	@Test
	public void add() {
		log.info("begin add...");
		try {
			asset = assetManager.add(asset, true);
			assertNotNull(asset);
			log.info("add success...");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void view() {
		log.info("begin view...");

		try {
			asset = assetManager.add(asset, true);
			assertNotNull(assetManager.view(asset.getGuid()));
			log.info("view success...");
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
			asset = assetManager.add(asset, true);
			List<Asset> list = assetManager.search(new String[] { "name" },
					new String[] { "=" }, new Object[] { "test asset" });
			assertTrue(list.size() > 0);
			log.info("search success...");
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
			asset = assetManager.add(asset, true);
			asset.setDescription("test");
			Asset assetTmp = assetManager.update(asset.getGuid(), asset);
			assertNotNull(assetTmp);
			log.info("update success...");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void remove() {
		log.info("begin remove...");
		try {
			asset = assetManager.add(asset, true);
			Asset assetTmp = assetManager.remove(asset.getGuid(), true);
			assertNull(assetManager.view(assetTmp.getGuid()));
			asset = null;
			log.info("remove success...");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

}
