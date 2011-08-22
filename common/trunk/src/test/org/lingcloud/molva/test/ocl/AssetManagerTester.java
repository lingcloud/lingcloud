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
import org.lingcloud.molva.ocl.persistence.GNode;
import org.lingcloud.molva.ocl.persistence.GNodeConstants;
import org.lingcloud.molva.ocl.util.HashFunction;
import org.lingcloud.molva.xmm.ac.PartitionAC;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2011-7-25<br>
 * @author Ruijian Wang<br>
 * 
 */
public class AssetManagerTester {
	private static Log log = LogFactory.getLog(AssetManagerTester.class);
	private static AssetManagerImpl assetManager = null;
	private static Asset asset = null;

	@BeforeClass
	public static void initializeForAllTest() {
		assetManager = new AssetManagerImpl();
		asset = new Asset();
		 asset.setAssetController("lingcontrol");
		 asset.setName("lingname");
		 asset.setType("type");
		asset.setAcl("rwx------");
	}

	@AfterClass
	public static void destroyForAllTest() throws Exception {
		if (asset != null) {
			assetManager.remove(asset.getGuid(), true);
		}
	}

	@Before
	public void initialize() throws Exception {

	
	}

	@After
	public void destroy() throws Exception {
		
	}


	@Test
	public void add() {

		log.info("begin add...");
		try {

			Asset assetTmp = assetManager.add(asset, true);
			assertNotNull(assetTmp);
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

			List<Asset> list = assetManager.search(new String[] { "name" }, new String[] { "="}, new Object[] {
					"lingname"});
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

			Asset assetTmp = assetManager.remove(asset.getGuid(), true);
			assertNull(assetManager.view(assetTmp.getGuid()));
			log.info("remove success...");
		} catch (Exception e) {
			log.error("test failed. Reason: " + e);
			e.printStackTrace();
			fail();
		}
	}

}
