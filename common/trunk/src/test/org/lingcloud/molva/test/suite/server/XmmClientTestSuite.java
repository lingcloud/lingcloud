package org.lingcloud.molva.test.suite.server;

import org.lingcloud.molva.test.xmm.*;

import junit.framework.*;

public class XmmClientTestSuite {

	public static Test suite() {
		
		TestSuite suite = new TestSuite();
		suite.addTest(
				new JUnit4TestAdapter(
						XMMClientTester.class));
		
		return suite;
	}
}
