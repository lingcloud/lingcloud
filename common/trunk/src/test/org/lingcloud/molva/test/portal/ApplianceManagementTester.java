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

package org.lingcloud.molva.test.portal;

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
		selenium.windowMaximize();
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
    public void testApplianceManagement() throws Exception{
    	selenium.open("/lingcloud/");
		selenium.click("link=Virtual Appliance");
		selenium.waitForPageToLoad("30000");
		// Add one appliance
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[3]/a/img");
		selenium.waitForPageToLoad("30000");
		selenium.click("css=a[title=Add Appliance] > img");
		selenium.type("name", "appliance1");
		selenium.select("location", "label=example.img");
		selenium.select("category", "label=No Category");
	
		selenium.select("os", "label=Windows");
		selenium.type("osversion", "xp");
		selenium.click("//input[@name='accessway' and @value='VNC']");
		selenium.select("cpuamount", "label=1");
		selenium.select("memsize", "label=512MB");
		selenium.click("//table[@id='newVirtualApplianceTable']/tbody/tr[11]/td[2]/a");
		selenium.type("description", "description1");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");	
		verifyTrue(selenium.isTextPresent("Windows xp"));
		verifyTrue(selenium.isTextPresent("appliance1"));
		selenium.click("css=a[title=Detail] > img");	
		selenium.click("popup_cancel");
		// Modify the appliance
		selenium.click("css=a[title=Update Appliance Information] > img");
		selenium.type("name", "appliance2");
		selenium.select("category", "label=No Category");
		selenium.select("os", "label=Linux");
		selenium.type("osversion", "ubuntu");
		selenium.click("VNC");
		selenium.click("SSH");
		
		selenium.select("cpuamount", "label=3");
		selenium.select("memsize", "label=256MB");
		selenium.select("lang", "label=Chinese(traditional)");
		selenium.click("//table[@id='modifyVirtualApplianceTable']/tbody/tr[11]/td[2]/a");
		selenium.click("//table[@id='modifyVirtualApplianceTable']/tbody/tr[10]/td[2]/a");
		selenium.type("description", "description2");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("appliance2"));
		verifyTrue(selenium.isTextPresent("Linux ubuntu"));
		selenium.click("css=a[title=Detail] > img");
		selenium.click("popup_cancel");
		selenium.click("css=a[title=Refresh] > img");
		selenium.click("css=a[title=Refresh] > img");
		verifyTrue(selenium.isTextPresent("Ready"));
		// Delete the appliance
		selenium.click("css=a[title=Delete Appliance] > img");
		selenium.click("popup_ok");
		verifyFalse(selenium.isTextPresent("appliance2"));
    }  
	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
