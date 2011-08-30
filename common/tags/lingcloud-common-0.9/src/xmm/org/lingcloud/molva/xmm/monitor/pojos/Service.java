/*
 *  @(#)Service.java  2011-7-24
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

package org.lingcloud.molva.xmm.monitor.pojos;

import org.lingcloud.molva.xmm.monitor.MonitorConstants;
import org.lingcloud.molva.xmm.monitor.MonitorUtil;

/**
 * <strong>Purpose:</strong><br>
 * 
 * @version 1.0.1 2011-7-24<br>
 * @author Liang Li<br>
 */
public class Service {
	private String srvName;
	private String srvStat;
	private String srvTime;
	private String srvInfo;
	
	public Service(String name) {
		this(name, 
				MonitorConstants.MONITOR_STAT_UNKN, 
				MonitorUtil.getCurrTime(), 
				MonitorConstants.MONITOR_INFO_UNKN );
	}
	
	public Service(String name, String stat, String tm, String info) {
		srvName = name;
		srvStat = stat;
		srvTime = tm;
		srvInfo = info;
	}
	
	public void setName(String name) {
		srvName = name;
	}
	
	public String getName() {
		return srvName;
	}
	
	public void setStat(String stat) {
		srvStat = stat;
	}
	
	public String getStat() {
		return srvStat;
	}
	
	public int getStatInt() {
		if (MonitorConstants.MONITOR_STAT_OK
				.equals(srvStat)) {
			return MonitorConstants.MONITOR_STAT_OK_INT;
		}
		if (MonitorConstants.MONITOR_STAT_WARN
				.equals(srvStat)) {
			return MonitorConstants.MONITOR_STAT_WARN_INT;
		}
		if (MonitorConstants.MONITOR_STAT_UNKN
				.equals(srvStat)) {
			return MonitorConstants.MONITOR_STAT_CRIT_INT;
		}
		return MonitorConstants.MONITOR_STAT_CRIT_INT;
	}
	
	public void setTime(String tm) {
		srvTime = tm;
	}
	
	public String getTime() {
		return srvTime;
	}
	
	public void setInfo(String info) {
		srvInfo = info;
	}
	
	public String getInfo() {
		return srvInfo;
	}
	
	@Override
	public String toString() {
		if (srvName == null 
				|| srvStat == null
				|| srvTime == null
				|| srvInfo == null) {
			return "";
		}
		return "{\n"
				+ "\tsrvName:'" + srvName + "',\n"
				+ "\tsrvState:'" + srvStat + "',\n"
				+ "\tsrvCheckTime:'" + srvTime + "',\n"
				+ "\tsrvStatInfor:'" + srvInfo + "'\n"
				+ "}";
	}
}
