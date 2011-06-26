/*
 *  @(#)ApplianceManagementTester.java 2011-6-23
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

package org.lingcloud.molva.test;

import com.thoughtworks.selenium.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
public class ApplianceManagementTester extends SeleneseTestCase {
	@Before
	public void setUp() throws Exception {
		selenium = new DefaultSelenium(TestConstants.SELENIUM_SERVER_HOST,
				TestConstants.SELENIUM_SERVER_PORT,
				TestUtils.getBrowser(),
				TestConstants.TEST_LINGCLOUD_PORTAL_URL);
		selenium.setSpeed(TestConstants.SELENIUM_SPEED);
		selenium.start();
	}

	@Test
	public void testCategoryManagement() throws Exception {
		selenium.open("/lingcloud/");
		selenium.click("link=Virtual Appliance");
		selenium.waitForPageToLoad("30000");
		// Add a category
		selenium.click("link=+");
		selenium.type("categoryname", "category1");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("category1"));
		// Delete the category
		selenium.click("link=-");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		verifyFalse(selenium.isTextPresent("category1"));
	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
