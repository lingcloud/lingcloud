/*
 *  @(#)XmmImplTestSuite.java  Jul 23, 2011
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
package org.lingcloud.molva.test.suite.server;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.lingcloud.molva.test.xmm.XMMImplTester;

/**
 * <strong>Purpose:</strong><br>
 * The test suite for LingCloud XMMImpl.
 *
 * @version 1.0.0 2011-7-24<br>
 * @author Liang Li<br>
 *
 */
public class XmmImplTestSuite {
	
	public static Test suite() {
		
		TestSuite suite = new TestSuite();
		suite.addTest(
				new JUnit4TestAdapter(
						XMMImplTester.class));
		
		return suite;
	}
}
