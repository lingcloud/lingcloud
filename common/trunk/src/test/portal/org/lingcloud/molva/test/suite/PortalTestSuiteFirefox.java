/*
 *  @(#)PortalTestSuiteFirefox.java 2011-6-26
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

package org.lingcloud.molva.test.suite;

import org.lingcloud.molva.test.util.TestConstants;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * <strong>Purpose:</strong><br>
 * The test suite for LingCloud Portal.
 * 
 * @version 1.0.0 2011-6-26<br>
 * @author Jian Lin<br>
 * 
 */
public class PortalTestSuiteFirefox {
	
    public static Test suite() {

    	PortalTestSuiteImpl ts = new PortalTestSuiteImpl(TestConstants.Browser.FIREFOX);
    	
        TestSuite suite = ts.suiteImpl();
        
        return suite;
    }

}
