/*
 *  @(#)PartitionTester.java 2011-6-25
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

package org.lingcloud.molva.test.portal;

import com.thoughtworks.selenium.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lingcloud.molva.test.util.TestConfig;
import org.lingcloud.molva.test.util.TestConstants;
import org.lingcloud.molva.test.util.TestUtils;

import java.util.regex.Pattern;

/**
 * <strong>Purpose:</strong><br>
 * The tester class.
 * 
 * @version 1.0.0 2011-6-25<br>
 * @author Jian Lin<br>
 * 
 */
public class PartitionTester extends SeleneseTestCase {
	@Before
	public void setUp() throws Exception {
		selenium = new DefaultSelenium(TestConfig.getSeleniumServerHost(),
				TestConfig.getSeleniumPort(), TestUtils.getBrowser(),
				TestConfig.getTestLingCloudPortalURL());
		selenium.setSpeed(TestConfig.getSeleniumSpeed());
		selenium.start();
		selenium.windowMaximize();
	}

	@Test
	public void testVMPartitionAndNode() throws Exception {
		selenium.open("/lingcloud/");
		selenium.click("link=Infrastructure");
		selenium.waitForPageToLoad("30000");
		// Add a VM partition
		selenium.click("css=td:nth(2) > a > img");
		selenium.click("nodetype");
		selenium.type("name", "node1");
		selenium.type("preInstalledSoft", "soft1");
		selenium.type("description", "description1");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("node1"));
		// Add a node for VM partition
		selenium.click("css=td:nth(4) > a > img");
		selenium.type("privateip", TestConfig.getTestXenServer());
		selenium.type("description", "description2");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("sd1");
		Thread.sleep(TestConstants.RETRY_INTERVAL);
		verifyTrue(selenium.isTextPresent(TestConfig.getTestXenServer()));
		for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++)
		{
			selenium.click("css=#ptNodeRrd1 > td > span > img[title=Refresh]");
			Thread.sleep(TestConstants.RETRY_INTERVAL);
			if (selenium.isTextPresent("RUNNING"))
				break;
		}
		verifyTrue(selenium.isTextPresent("RUNNING"));
		// Delete the node for VM partition
		selenium.click("css=td:nth(5) > a > img");
		selenium.select("parguid", "label=node1");
		selenium.select("pnguid", "label=" + TestConfig.getTestXenServer());
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("sd1");
		Thread.sleep(TestConstants.RETRY_INTERVAL);
		verifyFalse(selenium.isTextPresent(TestConfig.getTestXenServer()));
		// Delete the VM partition
		selenium.click("css=td:nth(3) > a > img");
		selenium.select("parguid", "label=node1");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		verifyFalse(selenium.isTextPresent("node1"));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGeneralPartitionAndNode() throws Exception {
		selenium.open("/lingcloud/");
		selenium.click("link=Infrastructure");
		selenium.waitForPageToLoad("30000");
		// Add a General partition
		selenium.click("css=td:nth(2) > a > img");
		selenium.click("css=#addPartitionTable > tbody > tr:nth(3) > td > input[name=nodetype]");
		selenium.type("name", "partition1");
		selenium.type("preInstalledSoft", "soft2");
		selenium.type("description", "description3");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("partition1"));
		// Add a node for General partition
		selenium.click("css=td:nth(4) > a > img");
		selenium.type("privateip", TestConfig.getTestCommonServer());
		selenium.type("description", "description4");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("sd1");
		Thread.sleep(TestConstants.RETRY_INTERVAL);
		verifyTrue(selenium.isTextPresent(TestConfig.getTestCommonServer()));
		for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++)
		{
			selenium.click("css=#ptNodeRrd1 > td > span > img[title=Refresh]");
			Thread.sleep(TestConstants.RETRY_INTERVAL);
			if (selenium.isTextPresent("RUNNING"))
				break;
		}
		verifyTrue(selenium.isTextPresent("RUNNING"));
		// Delete the node for General partition
		selenium.click("css=td:nth(5) > a > img");
		selenium.select("parguid", "label=partition1");
		selenium.select("pnguid", "label=" + TestConfig.getTestCommonServer());
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("sd1");
		Thread.sleep(TestConstants.RETRY_INTERVAL);
		verifyFalse(selenium.isTextPresent(TestConfig.getTestCommonServer()));
		// Delete the General partition
		selenium.click("css=td:nth(3) > a > img");
		selenium.select("parguid", "label=partition1");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		verifyFalse(selenium.isTextPresent("partition1"));
	}
	
	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
