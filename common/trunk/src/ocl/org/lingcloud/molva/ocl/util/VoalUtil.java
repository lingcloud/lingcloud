/*
 *  @(#)VoalUtil.java  2010-5-12
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

import java.util.Calendar;
import java.util.Date;

import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetManagerImpl;
import org.lingcloud.molva.ocl.lease.Lease;
import org.lingcloud.molva.ocl.lease.LeaseManagerImpl;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-12<br>
 * @author Xiaoyi Lu<br>
 */

public class VoalUtil {
	private static final String CONFIG_FILE_NAME = "voal.conf";

	public static String className2URI(String name) {
		if (name == null || "".equals(name)) {
			return name;
		}
		String[] tmp = name.split("\\.");
		StringBuilder sb = new StringBuilder();
		for (int i = tmp.length - 1; i >= 0; i--) {
			sb.append(tmp[i]);
			if (i != 0) {
				sb.append(".");
			}
		}
		return sb.toString();
	}

	public static Date getTimeDelayFromNow(long delay) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(TimeServerManager.getCurrentTime());
		long l1 = calendar.getTimeInMillis();
		calendar.setTimeInMillis(l1 + delay);
		return calendar.getTime();
	}

	public static long getGapMilliSecondsBetweenTimes(Date baseTime,
			Date destTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(baseTime);
		long l1 = calendar.getTimeInMillis();
		calendar.setTime(destTime);
		long l2 = calendar.getTimeInMillis();
		return (l2 - l1); // milliseconds
	}

	public static Date dateFromString(String str) {
		if (str == null) {
			return null;
		}
		if (str.equalsIgnoreCase("")) {
			return null;
		}
		java.util.Date ret = null;
		try {
			java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(str);
			final int m = 1000000;
			long milliseconds = timestamp.getTime()
					+ (timestamp.getNanos() / m);
			ret = new Date(milliseconds);
		} catch (Exception e) {
			ret = null;
		}
		return ret;
	}

	public static String dateToString(Date date) {
		String ret = "";
		if (date != null) {
			java.sql.Timestamp timestamp = new java.sql.Timestamp(
					date.getTime());
			ret = timestamp.toString();
		}
		return ret;

	}

	public static String getPollIntervalInCfgFile() {
		ConfigUtil conf = new ConfigUtil(CONFIG_FILE_NAME);
		String ret = conf.getProperty("assetPollingInterval", "");

		if (StringUtil.isEmpty(ret)) {
			return null;
		}
		return ret.trim();
	}

	public static boolean checkIsLeaseExpired(Lease lease) {
		Date now = TimeServerManager.getCurrentTime();
		Date expireTime = lease.getExpireTime();
		if (expireTime != null) {
			return expireTime.before(now);
		}
		return false;
	}

	public static void setLastErrorMessage4Lease(LeaseManagerImpl lmi,
			Lease lease, String err) {
		if (lmi == null || lease == null || err == null
				|| "".equals(err.trim())) {
			return;
		}
		lease.setLastErrorMessage(err);
		try {
			lmi.update(lease.getGuid(), lease);
		} catch (Exception e) {
			
		}
	}

	public static void setLastErrorMessage4Asset(AssetManagerImpl ami,
			Asset asset, String err) {
		if (ami == null || asset == null || err == null
				|| "".equals(err.trim())) {
			return;
		}
		asset.setLastErrorMessage(err);
		try {
			ami.update(asset.getGuid(), asset);
		} catch (Exception e) {
			
		}
	}
	
	private VoalUtil() {
		
	}
}
