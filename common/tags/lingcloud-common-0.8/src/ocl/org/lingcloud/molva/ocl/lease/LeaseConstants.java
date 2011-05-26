/*
 *  @(#)LeaseConstants.java  2010-5-12
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

package org.lingcloud.molva.ocl.lease;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-12<br>
 * @author Xiaoyi Lu<br>
 */

public class LeaseConstants {
	private LeaseConstants() {
		
	}

	/**
	 * 
	 * <strong>Purpose:</strong><br>
	 * TODO.
	 *
	 * @version 1.0.1 2010-5-12<br>
	 * @author Xiaoyi Lu<br>
	 *
	 */
	public enum LeaseLifeCycleState {
		PENDING, CANCELLED, NEGOTIATING, REJECTED, 
		PREPROCESSING, READY, EFFECTIVE, EXPIRED, TERMINATION, FAIL;
	};

	public static final String LEASE_RCONTROLLER = "LEASE_RCONTROLLER";

	public static final Object CANCEL_LOCK = new Object();

	public static final Object TERMINATE_LOCK = new Object();

	public static final String DEFAULT_ACL = "rwx------";
}
