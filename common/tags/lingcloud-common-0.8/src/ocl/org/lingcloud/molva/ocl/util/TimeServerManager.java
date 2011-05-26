/*
 *  @(#)TimeServerManager.java  2010-8-20
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

package org.lingcloud.molva.ocl.util;

import java.util.Date;
import java.util.TimeZone;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-8-20<br>
 * @author Xiaoyi Lu<br>
 */
public class TimeServerManager {
	
	private static TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8");

	static {
		String tzstr = System.getProperty("user.timezone");
		if (tzstr != null && !"".equals(tzstr)) {
			tz = TimeZone.getTimeZone(tzstr);
		}
		TimeZone.setDefault(tz);
	}

	public static Date getCurrentTime() {
		return new Date();
	}
	
	private TimeServerManager() {
		
	}
}
