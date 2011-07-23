/*
 *  @(#)OclTestSuite.java  Jul 23, 2011
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

import org.lingcloud.molva.test.ocl.GNodeManagerTester;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <strong>Purpose:</strong><br>
 * The test suite for LingCloud Ocl.
 *
 * @version 1.0.0 2011-7-23<br>
 * @author Ruijian Wang<br>
 *
 */
public class OclTestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite("The test suite for LingCloud Ocl");
        suite.addTest(new JUnit4TestAdapter(GNodeManagerTester.class));
        
        return suite;
	}
}
