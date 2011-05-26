/*
 *  @(#)GNodeException.java  2007-7-20
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

package org.lingcloud.molva.ocl.persistence;

/**
 * 
 * <strong>Purpose:</strong><br>
 * TODO.
 *
 * @version 1.0.1 2007-7-20<br>
 * @author zouyongqiang<br>
 *
 */
public class GNodeException extends Exception {
	private static final long serialVersionUID = -4706771345272481262L;

	public GNodeException(String eMsg) {
		super(eMsg);
	}

	public GNodeException(Exception clexp) {
		super(clexp);
	}

}
