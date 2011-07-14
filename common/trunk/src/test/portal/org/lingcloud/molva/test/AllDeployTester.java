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

package org.lingcloud.molva.test;


import com.thoughtworks.selenium.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lingcloud.molva.test.util.TestConstants;
import org.lingcloud.molva.test.util.TestUtils;
/**
 * <strong>Purpose:</strong><br>
 * The test suite for LingCloud Portal.
 * 
 * @version 1.0.0 2011-7-14<br>
 * @author Maosen Sun <br>
 * 
 */
public class AllDeployTester extends SeleneseTestCase {
	@Before
	public void setUp() throws Exception {
		selenium = new DefaultSelenium(TestConstants.SELENIUM_SERVER_HOST,
				TestConstants.SELENIUM_SERVER_PORT, TestUtils.getBrowser(),
				TestConstants.TEST_LINGCLOUD_PORTAL_URL);
		selenium.setSpeed(TestConstants.SELENIUM_SPEED );
		
		selenium.start();
		selenium.windowMaximize();
	}

	@Test
	public void testAddCategory() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualAppliance.jsp");
		selenium.click("link=Virtual Appliance");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=+");
		selenium.type("categoryname", "category1");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("category1"));

	}

	public void testAddAndModifyCDImage() throws Exception {
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
	}

	public void testAddAndModifyAppliance() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualDisc.jsp#");
		selenium.click("link=Virtual Appliance");
		selenium.waitForPageToLoad("30000");
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[4]/a/h3");
		selenium.waitForPageToLoad("30000");
		selenium.click("css=a[title=Add Appliance] > img");
		selenium.type("appname", "appliance1");
		selenium.select("os", "label=Windows");
		selenium.type("osversion", "xp");
		selenium.select("vcd", "label=disk1");
		selenium.select("memsize", "label=256MB");
		selenium.select("cpuamount", "label=1");
		selenium.type("diskcapacity", "7");
		
		
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");


		selenium.click("//th[@id='actionlog_title']/a/img");
		selenium.waitForPageToLoad("100000");
		verifyTrue(selenium.isTextPresent("stop"));
		verifyTrue(selenium.isTextPresent("appliance1"));
		verifyTrue(selenium.isTextPresent("application1"));
		verifyTrue(selenium.isTextPresent("Windows xp"));
		selenium.click("css=h3");
		selenium.waitForPageToLoad("30000");
		selenium.click("//td[2]/div/ul/li");
		selenium.click("css=a[title=Add Appliance] > img");
		selenium.type("name", "appliance2");
		selenium.select("category", "label=category1");
		selenium.select("os", "label=Windows");
		selenium.type("osversion", "xp");
		selenium.type("oneapp", "application2");
		//selenium.click("//table[@id='newVirtualApplianceTable']/tbody/tr[6]/td[2]/a");
		selenium.click("//table[@id='newVirtualApplianceTable']/tbody/tr[6]/td[2]/a");
		selenium.click("//input[@name='accessway' and @value='VNC']");
		selenium.select("cpuamount", "label=2");
		selenium.select("memsize", "label=512MB");
		selenium.select("lang", "label=Chinese(traditional)");
		selenium.click("//table[@id='newVirtualApplianceTable']/tbody/tr[11]/td[2]/a");
		selenium.type("description", "description1");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("Ready"));
		verifyTrue(selenium.isTextPresent("Windows xp"));
		verifyTrue(selenium.isTextPresent("appliance2"));
		verifyTrue(selenium.isTextPresent("word"));
		verifyTrue(selenium.isTextPresent("application2"));
		selenium.click("css=a[title=Update Appliance Information] > img");
		selenium.type("name", "appliance3");
		selenium.select("os", "label=Linux");
		selenium.type("osversion", "ubuntu");
		selenium.click("link=Clear");
		selenium.type("oneapp", "application3");
		selenium.click("//table[@id='modifyVirtualApplianceTable']/tbody/tr[5]/td[2]/a");
		
		selenium.click("SSH");
		selenium.click("VNC");
		selenium.select("cpuamount", "label=1");
		selenium.select("memsize", "label=256MB");
		selenium.select("lang", "label=Chinese(traditional)");
		selenium.click("//table[@id='modifyVirtualApplianceTable']/tbody/tr[10]/td[2]/a");
		selenium.click("//table[@id='modifyVirtualApplianceTable']/tbody/tr[11]/td[2]/a");
		selenium.click("//table[@id='modifyVirtualApplianceTable']/tbody/tr[10]/td[2]/a");
		selenium.type("description", "description2");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("css=li.current");
		verifyTrue(selenium.isTextPresent("Linux ubuntu"));
		verifyTrue(selenium.isTextPresent("appliance3"));
		verifyTrue(selenium.isTextPresent("txt"));
		verifyTrue(selenium.isTextPresent("application3"));

	}

	public void testAddVMPartition() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualCluster.jsp");
		selenium.click("link=Infrastructure");
		selenium.waitForPageToLoad("30000");
		selenium.click("css=h3");
		selenium.type("name", "partion1");
		selenium.click("nodetype");
		selenium.type("preInstalledSoft", "software1");
		selenium.type("description", "description3");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("//a[contains(text(),'partion1')]");
		verifyTrue(selenium.isTextPresent("partion1"));
		verifyTrue(selenium.isTextPresent("description3"));
		verifyTrue(selenium.isTextPresent("software1"));
	}

	public void testAddVMNode() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualCluster.jsp");
		selenium.click("link=Infrastructure");
		selenium.waitForPageToLoad("30000");
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[5]/a/img");
		selenium.select("parguid", "label=partion1");
		selenium.type("privateip", TestConstants.TEST_XEN_SERVER);
		selenium.type("description", "description4");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("sd1");
		
		for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++)
		{
			selenium.click("css=#ptNodeRrd1 > td > span > img[title=Refresh]");
			Thread.sleep(TestConstants.RETRY_INTERVAL);
            if(selenium.isTextPresent("RUNNING"))		
			break;
		}
		verifyTrue(selenium.isTextPresent("RUNNING"));		
		verifyTrue(selenium.isTextPresent(TestConstants.TEST_XEN_SERVER));

	}

	public void testAddVMCluster() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualCluster.jsp");
		selenium.click("link=Infrastructure");
		selenium.waitForPageToLoad("30000");
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[7]/a/img");
		selenium.select("parguid", "label=partion1");
		selenium.type("clustername", "cluster1");
		selenium.type("nodenum", "1");
		selenium.click("publicIpSupport");
		selenium.click("nodeinfotype");
		selenium.select("vaguid0", "label=appliance3");

		selenium.select("memsize0", "label=512MB");
		selenium.select("cpunum0", "label=1");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("jd1");
		selenium.click("//a[contains(text(),'cluster1')]");
		verifyTrue(selenium.isTextPresent("cluster1"));
		
		for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++)
		{
			selenium.click("css=a[title=Refresh] > img");	
			Thread.sleep(TestConstants.RETRY_INTERVAL);
			if(selenium.isTextPresent("EFFECTIVE"))
			break;
		}
		
		verifyTrue(selenium.isTextPresent("EFFECTIVE"));

		for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++)
		{
			selenium.click("css=#vcNodeRrd1 > td > a[title=Refresh] > img");
			Thread.sleep(TestConstants.RETRY_INTERVAL);
			if(selenium.isTextPresent("RUNNING"))
			break;
		}
		verifyTrue(selenium.isTextPresent("RUNNING"));
		
	}

	public void testStopVMCluster() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualCluster.jsp");
		selenium.click("link=Infrastructure");
		selenium.waitForPageToLoad("30000");
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[9]/a/h3");
		selenium.select("parguid", "label=partion1");
		selenium.select("vcguid", "label=cluster1");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("jd2");
		selenium.click("//a[contains(text(),'cluster1')]");
	
		for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++)
		{
			selenium.click("css=#vcNodeRrd1 > td > a[title=Refresh] > img");
			Thread.sleep(TestConstants.RETRY_INTERVAL*2);
			if(selenium.isTextPresent("BOOT"))
			break;
		}
		verifyTrue(selenium.isTextPresent("BOOT"));
	}

	public void testStartVMCluster() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualCluster.jsp");
		selenium.click("link=Infrastructure");
		selenium.waitForPageToLoad("30000");
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[8]/a/img");
		selenium.select("parguid", "label=partion1");
		selenium.select("vcguid", "label=cluster1");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("//a[contains(text(),'partion1')]");
		selenium.click("//a[contains(text(),'cluster1')]");
		for (int i = 0; i < TestConstants.MAX_RETRY_TIMES; i++)
		{
			selenium.click("css=#vcNodeRrd1 > td > a[title=Refresh] > img");	
			Thread.sleep(TestConstants.RETRY_INTERVAL*2);
			if( selenium.isTextPresent("RUNNING") )
			break;
		}
		verifyTrue(selenium.isTextPresent("RUNNING"));

	}

	public void testAddGeneralPartition() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualCluster.jsp");	
		selenium.click("link=Infrastructure");
		selenium.waitForPageToLoad("30000");
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[3]/a/img");
		selenium.click("//input[@name='nodetype' and @value='GENERAL']");
		selenium.type("name", "partion2");
		selenium.type("preInstalledSoft", "software2");
		selenium.type("description", "description4");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("//a[contains(text(),'partion2')]");
		verifyTrue(selenium.isTextPresent("partion2"));
		verifyTrue(selenium.isTextPresent("software2"));
		verifyTrue(selenium.isTextPresent("description4"));

	}

	public void testAddGeneralNode() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualCluster.jsp");		
		selenium.click("link=Infrastructure");
		selenium.waitForPageToLoad("30000");
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[5]/a/img");
		selenium.select("parguid", "label=partion2");
		selenium.type("privateip", TestConstants.TEST_COMMON_SERVER);
		selenium.type("description", "description5");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("//a[contains(text(),'partion2')]");
		verifyTrue(selenium.isTextPresent(TestConstants.TEST_COMMON_SERVER));

	}

	public void testAddGeneralCluster() throws Exception {
		selenium.open("/lingcloud/JSP/ViewVirtualCluster.jsp");	
		selenium.click("link=Infrastructure");
		selenium.waitForPageToLoad("30000");
		selenium.click("//div[@id='middletext']/table/tbody/tr/td[7]/a/h3");
		selenium.select("parguid", "label=partion2");
		selenium.type("clustername", "cluster2");
		selenium.click("vntype");
		selenium.click("pnnodeip");
		selenium.click("popup_ok");
		selenium.waitForPageToLoad("30000");
		selenium.click("//a[contains(text(),'partion2')]");
		selenium.click("jd4");
		selenium.click("//a[contains(text(),'cluster2')]");
		verifyTrue(selenium.isTextPresent("cluster2"));

	}


	
	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}

}



