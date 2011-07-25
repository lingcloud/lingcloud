package org.lingcloud.molva.test.suite.server;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.lingcloud.molva.test.xmm.XMMImplTester;

public class XmmImplTestSuite {
	
	public static Test suite() {
		
		TestSuite suite = new TestSuite();
		suite.addTest(
				new JUnit4TestAdapter(
						XMMImplTester.class));
		
		return suite;
	}
}
