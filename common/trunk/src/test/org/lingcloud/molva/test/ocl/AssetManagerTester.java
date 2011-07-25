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
	}

	@AfterClass
	public static void destroyForAllTest() {
	}

	@Before
	public void initialize() throws Exception {
		asset = null;
	}

	@After
	public void destroy() throws Exception {
		if (asset != null) {
			assetManager.remove(asset.getGuid(), true);
		}
	}
	
	@Ignore
	@Test
	public void add() {
		
	}
	
	@Ignore
	@Test
	public void remove() {
		
	}
	
	@Ignore
	@Test
	public void search() {
		
	}
	
	@Ignore
	@Test
	public void update() {
		
	}
	
	@Ignore
	@Test
	public void reserve() {
		
	}
	
	@Ignore
	@Test
	public void unReserve() {
		
	}
	
	@Ignore
	@Test
	public void view() {
		
	}
	
}
