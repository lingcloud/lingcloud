/*
 *  @(#)XMMClientTestSuite.java  Jul 23, 2011
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

import org.lingcloud.molva.test.xmm.*;

import junit.framework.*;

/**
 * <strong>Purpose:</strong><br>
 * The test suite for LingCloud XMMClient.
 *
 * @version 1.0.0 2011-7-24<br>
 * @author Liang Li<br>
 *
 */
public class XMMClientTestSuite {

	public static Test suite() {
		
		TestSuite suite = new TestSuite();
		suite.addTest(
				new JUnit4TestAdapter(
						XMMClientTester.class));
		
		return suite;
	}
}
