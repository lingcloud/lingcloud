/*
 *  @(#)MonitorTestSuite.java  Jul 23, 2011
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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.lingcloud.molva.test.xmm.monitor.MonitorBridgeTester;
import org.lingcloud.molva.test.xmm.monitor.MonitorClientTester;
import org.lingcloud.molva.test.xmm.monitor.MonitorPoolTester;

/**
 * <strong>Purpose:</strong><br>
 * The test suite for LingCloud Monitor.
 * 
 * @version 1.0.0 2011-7-23<br>
 * @author Liang Li<br>
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ MonitorBridgeTester.class, MonitorPoolTester.class,
	MonitorClientTester.class })
public class MonitorTestSuite {

}
