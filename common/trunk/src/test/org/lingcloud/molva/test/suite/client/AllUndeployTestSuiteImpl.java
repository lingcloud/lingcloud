/*
 *  @(#)AllUndeployTestSuiteImpl.java 2011-6-26
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

package org.lingcloud.molva.test.suite.client;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestSuite;

import org.lingcloud.molva.test.portal.AllUndeployTester;
import org.lingcloud.molva.test.portal.ApplianceManagementTester;
import org.lingcloud.molva.test.portal.PartitionTester;
import org.lingcloud.molva.test.util.TestUtils;

/**
 * <strong>Purpose:</strong><br>
 * The test suite for LingCloud all undeploying.
 * 
 * @version 1.0.0 2011-6-26<br>
 * @author Jian Lin<br>
 * 
 */
public class AllUndeployTestSuiteImpl {
	
	private String browser;
	
	public AllUndeployTestSuiteImpl(String browser) {
		this.browser = browser;
	}
	
    public TestSuite suiteImpl() {
    	
    	TestUtils.setBrowser(browser);
        TestSuite suite = new TestSuite("The " + browser + " test suite for LingCloud all undeploying");
        
        suite.addTest(new JUnit4TestAdapter(AllUndeployTester.class));
        
        return suite;
    }
    
}
