/*
 *  @(#)MonitorUtil.java  2011-7-24
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

package org.lingcloud.molva.xmm.monitor;

import java.text.SimpleDateFormat;
import java.util.*;

import org.lingcloud.molva.ocl.util.ConfigUtil;
import org.lingcloud.molva.xmm.util.XMMUtil;

/**
 * <strong>Purpose:</strong><br>
 * 
 * @version 1.0.1 2011-7-24<br>
 * @author Liang Li<br>
 */
public class MonitorUtil {
	
	public static String getMonitorTypeInCfgFile() throws Exception {
		return XMMUtil.getValueInCfgFile("monitorSystemType");
	}
	
	public static String getMondServerHostInCfgFile() throws Exception {
		return XMMUtil.getValueInCfgFile("monitorServerHost");
	}
	
	public static String getMondServerPortInCfgFile() throws Exception {
		return XMMUtil.getValueInCfgFile("monitorServerPort");
	}
	
	public static String getWebappLocationInCfgFile() throws Exception {
		return ConfigUtil.getHomePath() 
				+ "/dist/bin/tomcat/webapps/lingcloud";
	}
	
	public static String getMonitorDirInCfgFile() throws Exception {
		return XMMUtil.getValueInCfgFile("monitorDir");
	}
	
	public static String getRrds4GangliaInCfgFile() throws Exception {
		return MonitorUtil.getMonitorDirInCfgFile()
				+ "/ganglia/rrds";
	}
	
	public static String getRRDtoolCmdInCfgFile() throws Exception {
		return XMMUtil.getValueInCfgFile("cmdRRDtool");
	}
	
	public static String getGangliaMonitorCmd() throws Exception {
		return MonitorConstants.monitorCmdPrefix 
				+ " " + getMondServerHostInCfgFile()
				+ " " + getMondServerPortInCfgFile() ;
	}
	
	private static SimpleDateFormat formater = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss");
	public static String getTimeFromInt(String tm) {
		long t = 0;
		try {
			t = Long.parseLong(tm) * 1000l;
			return formater.format(new Date(t));
		}catch (Exception e) {
			return "";
		}
	}
	
	public static String getCurrTime() {
		return formater.format(new Date());
	}
	
	public static String getTimeFromInt(int tm) {
		return formater.format(new Date(tm*1000l));
	}
	
	public static String getMonitorConfGuidByPaName(String parName) {
		return MonitorConstants.MONITOR_CONF_PREFIX + parName;
	}
	
	public static String getImgNameByHostSrv(String host, String srv, String period) {
		return host 
				+ "-"
				+ srv
				+ "-"
				+ period
				+ ".png";
	}
	
	public static String getImgNameByHostSrv(String host, String srv, long beg, long end) {
		long dif = end -beg;
		if (beg == 0 
				|| dif < 0)
			dif = end;
		if (dif < MonitorConstants.MONITOR_IMG_PERIOD_MINS_S + 1) {
			return getImgNameByHostSrv(host, srv, MonitorConstants.MONITOR_IMG_PERIOD_MINS);
		}
		if (dif < MonitorConstants.MONITOR_IMG_PERIOD_HOURS_S + 1) {
			return getImgNameByHostSrv(host, srv, MonitorConstants.MONITOR_IMG_PERIOD_HOURS);
		}
		if (dif < MonitorConstants.MONITOR_IMG_PERIOD_DAYS_S + 1) {
			return getImgNameByHostSrv(host, srv, MonitorConstants.MONITOR_IMG_PERIOD_DAYS);
		}
		return getImgNameByHostSrv(host, srv, MonitorConstants.MONITOR_IMG_PERIOD_MONTHS);
	}
	
	public static String getImgDirInWebapp() {
		return "/images"
				+ "/monitor";
	}
	
	public static String getImgDirLocation() throws Exception {
		String res = getWebappLocationInCfgFile() 
						+ getImgDirInWebapp();
		return res;
	}
	
	public static long getSecendsByPeriod(String flag) {
		long res = MonitorConstants.MONITOR_IMG_PERIOD_HOURS_S;
		if (flag == null) {
			// One day
		}
		else if (flag.contains(MonitorConstants.MONITOR_IMG_PERIOD_MINS)) {
			// 60 mins
			res = MonitorConstants.MONITOR_IMG_PERIOD_MINS_S;
		}
		else if (flag.contains(MonitorConstants.MONITOR_IMG_PERIOD_HOURS)) {
			// 24 hours
			res = MonitorConstants.MONITOR_IMG_PERIOD_HOURS_S;
		}
		else if (flag.contains(MonitorConstants.MONITOR_IMG_PERIOD_DAYS)) {
			// 30 days
			res = MonitorConstants.MONITOR_IMG_PERIOD_DAYS_S;
		}
		else if (flag.contains(MonitorConstants.MONITOR_IMG_PERIOD_MONTHS)) {
			res = MonitorConstants.MONITOR_IMG_PERIOD_MONTHS_S;
		}
		
		return res;
	}
	
}
