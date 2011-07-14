package org.lingcloud.molva.test;

import com.thoughtworks.selenium.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lingcloud.molva.test.util.TestConstants;
import org.lingcloud.molva.test.util.TestUtils;


public class CDImageManagementTester extends SeleneseTestCase {	
	@Before
	public void setUp() throws Exception {
		selenium = new DefaultSelenium(
				TestConstants.SELENIUM_SERVER_HOST,
				TestConstants.SELENIUM_SERVER_PORT,
				TestUtils.getBrowser(),
				TestConstants.TEST_LINGCLOUD_PORTAL_URL);
		selenium.setSpeed(TestConstants.SELENIUM_SPEED);
		selenium.start();
	}
	@Test
	public void testCDImageManagement() throws  Exception{
		selenium.open("/lingcloud/JSP/ViewVirtualDisc.jsp#");
		selenium.click("link=Virtual Appliance");
		selenium.waitForPageToLoad("30000");
		selenium.click("css=li.current");
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[5]/a/img");
		selenium.waitForPageToLoad("30000");
		selenium.click("css=a[title=Add CD Image] > img");
		selenium.type("discname", "image1");
		selenium.select("location", "label=example.iso");
		selenium.select("format", "label=iso");
		selenium.select("type", "label=Applications");
		selenium.type("oneapp", "word");

		selenium.click("link=+");
		selenium.click("link=Clear");
		selenium.type("oneapp", "txt");
		selenium.click("link=+");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("image1"));
		verifyTrue(selenium.isTextPresent("Application"));
		verifyTrue(selenium.isTextPresent("txt"));
		
		for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++)
		{
			selenium.click("css=a[title=Refresh] > img");
			Thread.sleep(TestConstants.RETRY_INTERVAL);
			if(selenium.isTextPresent("Ready"))
				break;
		}
		verifyTrue(selenium.isTextPresent("Ready"));
		selenium.click("css=a[title=Update CD Image] > img");
		selenium.type("discname", "disk1");
		selenium.select("format", "label=iso");
		selenium.select("type", "label=Operating System");
		selenium.select("os", "label=Windows");
		selenium.type("osversion", "xp");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("disk1"));
		verifyTrue(selenium.isTextPresent("Operating System"));
		verifyTrue(selenium.isTextPresent("Windows xp"));
		verifyTrue(selenium.isTextPresent("Ready"));
		selenium.click("css=a[title=Delete CD Image] > img");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("Deleting"));
		selenium.click("css=a[title=Refresh] > img");
		selenium.waitForPageToLoad("30000");

	}
	
	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
