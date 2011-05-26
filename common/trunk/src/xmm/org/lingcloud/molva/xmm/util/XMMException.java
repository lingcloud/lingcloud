/*
 *  @(#)XMMException.java  2010-5-27
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

package org.lingcloud.molva.xmm.util;

import org.lingcloud.molva.ocl.persistence.GNodeException;

public class XMMException extends GNodeException {
	
	public XMMException() {
		super("");
	}
	public XMMException(String val) {
		super(val);
	}
	public XMMException(Exception e) {
		super(e);
	}
}
