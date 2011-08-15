/*
 *  @(#)MonitorBridge.java  2011-7-24
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

package org.lingcloud.molva.xmm.monitor.pool;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.xmm.monitor.*;
import org.lingcloud.molva.xmm.monitor.pojos.*;

/**
 * <strong>Purpose:</strong><br>
 * 
 * @version 1.0.1 2011-7-24<br>
 * @author Liang Li<br>
 */
public abstract class MonitorBridge {
	
	protected static Log log = LogFactory.getLog(MonitorBridge.class);
	
	private static MonitorBridge instanse = null;
	
	protected long timeStamp;
	protected long timePeriod = 5 * 60 * 1000;
	
	public static final MonitorBridge getInstanse() {
		if (instanse == null) {
			try {
				String server = MonitorUtil.getMonitorTypeInCfgFile();
				if (MonitorConstants.ganglia.equals(server)) {
					
					instanse = new GangliaBridge();
					
				}else {
					throw new Exception("The Monitor Server is Not Defined or Suported in Configure File!");
				}
			} catch (Exception e) {
				log.error(e);
			}
			
		}
		return instanse;
	}
	
	public void setTimePeriod(long period) {
		timePeriod = period;
	}
	
	public long getTimePeriod() {
		return timePeriod;
	}
	
	public Map<String, Host> update(Map<String, Host> hostMap) throws Exception {
		
		long curTime = System.currentTimeMillis();
		if (curTime - timeStamp > timePeriod) {
			try {
				hostMap = getHostMap(hostMap);
				timeStamp = curTime;
			}catch (Exception e) {
				timeStamp = curTime;
				throw e;
			}
		}
		
		return hostMap;
	}
	
	public abstract Map<String, Host> getHostMap(Map<String, Host> hostMap) throws Exception ;
	
	public abstract Host updateHost(Host host) throws Exception ;
	
	public abstract String getSrvImgUri(String host, String srvName, long beg, long end) throws Exception;
	
}
