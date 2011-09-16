/*
 *  @(#)TestConstants.java 2011-6-23
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

/**
 * <strong>Purpose:</strong><br>
 * The test suite for LingCloud Portal.
 * 
 * @version 1.0.0 2011-7-14<br>
 * @author Maosen Sun<br>
 * 
 */
public class AllUndeployTester extends SeleneseTestCase {

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
	public void testDeleteGeneralCluster() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualCluster.jsp");
		selenium.click("link=Infrastructure");
		selenium.waitForPageToLoad("30000");
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[10]/a/img");
		selenium.select("parguid", "label=partion2");
		selenium.select("vcguid", "label=cluster2");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("60000");
		selenium.click("//a[contains(text(),'partion2')]");
		verifyFalse(selenium.isTextPresent("partion2"));
	}

	@Test
	public void testDeleteGeneralPartitionNode() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualCluster.jsp");
		selenium.click("link=Infrastructure");
		selenium.waitForPageToLoad("30000");
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[6]/a/img");
		selenium.select("parguid", "label=partion2");
		selenium.select("pnguid", "label=" + TestConfig.getTestCommonServer());
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("60000");
		selenium.click("//a[contains(text(),'partion2')]");
		verifyFalse(selenium.isTextPresent(TestConfig.getTestCommonServer()));
	}

	@Test
	public void testDeleteGeneralPartition() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualCluster.jsp");
		selenium.click("link=Infrastructure");
		selenium.waitForPageToLoad("30000");
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[4]/a/h3");
		selenium.select("parguid", "label=partion2");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		verifyFalse(selenium.isTextPresent("partion2"));
	}

	@Test
	public void testDeleteVMCluster() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualCluster.jsp");
		selenium.click("link=Infrastructure");
		selenium.waitForPageToLoad("30000");
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[10]/a/img");
		selenium.select("parguid", "label=partion1");
		selenium.select("vcguid", "label=cluster1");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("//a[contains(text(),'partion1')]");
		verifyFalse(selenium.isTextPresent("cluster1"));
	}

	@Test
	public void testDeleteVMNode() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualCluster.jsp");
		selenium.click("link=Infrastructure");
		selenium.waitForPageToLoad("30000");
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[6]/a/img");
		selenium.select("parguid", "label=partion1");
		selenium.select("pnguid", "label=" + TestConfig.getTestXenServer());
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("//a[contains(text(),'partion1')]");
		verifyFalse(selenium.isTextPresent(TestConfig.getTestXenServer()));
	}

	@Test
	public void testDeleteVMPartition() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualCluster.jsp");
		selenium.click("link=Infrastructure");
		selenium.waitForPageToLoad("30000");
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[4]/a/h3");
		selenium.select("parguid", "label=partion1");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		verifyFalse(selenium.isTextPresent("partion1"));
	}

	@Test
	public void testDeleteAppliance() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualCluster.jsp");
		selenium.click("link=Virtual Appliance");
		selenium.waitForPageToLoad("30000");
		selenium.click("css=a[title=Delete Appliance] > img");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("css=li.current");
		verifyFalse(selenium.isTextPresent("appliance3"));
		selenium.open("/lingcloud/JSP/ViewVirtualAppliance.jsp");
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[4]/a/h3");
		selenium.waitForPageToLoad("30000");
		selenium.click("css=a[title=Delete Appliance] > img");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("css=a[title=Refresh] > img");
		selenium.waitForPageToLoad("30000");
		verifyFalse(selenium.isTextPresent("appliance1"));
	}

	@Test
	public void testDeleteCDImage() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualCluster.jsp");
		selenium.click("link=Virtual Appliance");
		selenium.waitForPageToLoad("30000");
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[5]/a/h3");
		selenium.waitForPageToLoad("30000");
		selenium.click("css=a[title=Delete CD Image] > img");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("css=a[title=Refresh] > img");
		selenium.waitForPageToLoad("30000");
		verifyFalse(selenium.isTextPresent("image1"));
	}

	@Test
	public void testDeleteCategory() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualDisc.jsp");
		selenium.click("link=Virtual Appliance");
		selenium.waitForPageToLoad("30000");
		selenium.click("css=li.current");
		selenium.click("link=-");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		verifyFalse(selenium.isTextPresent("category1"));
	}

	@Test
	public void testMonitor() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualCluster.jsp");
		selenium.click("link=Monitor");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("Sorry, no result!"));
	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}

}
