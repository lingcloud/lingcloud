/*
 *  @(#)PollingTask.java  2010-5-30
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

package org.lingcloud.molva.ocl.poll;

import org.lingcloud.molva.ocl.asset.Asset;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-30<br>
 * @author Xiaoyi Lu<br>
 */
public interface PollingTask extends Runnable {

	String getTaskGuid();

	void faultTolerant(Asset asset);
}
