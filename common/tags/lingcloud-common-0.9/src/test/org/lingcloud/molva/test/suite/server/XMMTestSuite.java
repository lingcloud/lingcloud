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
package org.lingcloud.molva.test.suite.server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.lingcloud.molva.test.xmm.XMMClientTester;
import org.lingcloud.molva.test.xmm.XMMImplTester;

/**
 * <strong>Purpose:</strong><br>
 * The test suite for LingCloud XMM.
 * 
 * @version 1.0.0 2011-7-23<br>
 * @author Ruijian Wang<br>
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ XMMImplTester.class, XMMClientTester.class })
public class XMMTestSuite {

}
