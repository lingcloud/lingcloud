/*
 *  @(#)Host.java  2011-7-24
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

import java.util.*;

import org.lingcloud.molva.xmm.monitor.MonitorConstants;
import org.lingcloud.molva.xmm.monitor.MonitorUtil;

/**
 * <strong>Purpose:</strong><br>
 * 
 * @version 1.0.1 2011-7-24<br>
 * @author Liang Li<br>
 */
public class Host {
	
	private String			hostName;
	private String			hostIP;
	private String			hostTime;
	
	private Map<String, Service>	srvMap = new HashMap<String, Service>();
	private Map<String, VM>		vmMap = new HashMap<String, VM>();
	
	public Host(String name) {
		this(name, name, MonitorUtil.getCurrTime());
	}
	public Host(String name, String ip, String tm) {
		hostName = name;
		hostIP = ip;
		hostTime = tm;
		
		for (int i = 0 ; i < MonitorConstants.MONITOR_ITEM_LIST.length ; i++) {
			String srvName = MonitorConstants.MONITOR_ITEM_LIST[i];
			Service srv = new Service(srvName);
			srvMap.put(srvName, srv);
		}
	}
	
	@Override
	public String toString() {
		if (hostName == null
				|| hostIP == null
				|| hostTime == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("{\n"
					+ "\thostName:'" + hostName + "',\n"
					+ "\thostIP:'" + hostIP + "',\n"
					+ "\thostTime:'" + hostTime + "',\n"
					+ "\tsrvList:["
				);
		Service srv;
		VM vm;
		int size = srvMap.size();
		for (String key : srvMap.keySet()) {
			srv = srvMap.get(key);
			sb.append(srv);
			if (--size > 0) {
				sb.append(",");
			}
		}
		sb.append("],vmMap:[");
		for (int i = 0 ; i < vmMap.size() ; i++) {
			vm = vmMap.get(i);
			sb.append(vm);
			if (i < vmMap.size() - 1) {
				sb.append(",");
			}
		}
		sb.append("]\n\t}");
		
		return sb.toString();
	}
	
	public void setHostName(String name) {
		hostName = name;
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public String getHostState() {
		try {
			return srvMap.get(MonitorConstants.MONITOR_HOST_CPU)
					.getStat();
		}catch (Exception e) {
			return MonitorConstants.MONITOR_STAT_CRIT; 
		}
	}
	
	public String getHostInfo() {
		try {
			return srvMap.get(MonitorConstants.MONITOR_HOST_CPU)
					.getInfo();
		}catch (Exception e) {
			return MonitorConstants.MONITOR_INFO_UNKN; 
		}
	}
	
	public void setHostIP(String ip) {
		hostIP = ip;
	}
	
	public String getHostIP() {
		return hostIP;
	}
	
	public void setHostTime(String tm) {
		hostTime = tm;
	}
	
	public String getHostTime() {
		return hostTime;
	}
	
	public void clear() {
		srvMap.clear();
		vmMap.clear();
	}
	
	public void addSrv(String srvName, Service srv) {
		srvMap.put(srvName, srv);
	}
	
	public Map<String, Service> getSrvMap() {
		if (srvMap == null) {
			srvMap = new HashMap<String, Service>();
		}
		return srvMap;
	}
	
	public Service getSrv(String srvName) {
		return srvMap.get(srvName);
	}
	
	public void addVM(String name, VM vm) {
		vmMap.put(name,vm);
	}
	
	public int getVMSize() {
		return vmMap.size();
	}
	
	public Map<String, VM> getVMMap() {
		return vmMap;
	}
	
	public VM getVMIndexOf(int i) {
		return vmMap.get(i);
	}
}
