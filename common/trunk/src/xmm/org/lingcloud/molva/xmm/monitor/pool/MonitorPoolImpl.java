/*
 *  @(#)MonitorPoolImpl.java  2011-7-24
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

import org.lingcloud.molva.xmm.monitor.MonitorConstants;
import org.lingcloud.molva.xmm.monitor.MonitorUtil;
import org.lingcloud.molva.xmm.monitor.pojos.*;

import org.lingcloud.molva.ocl.asset.*;

/**
 * <strong>Purpose:</strong><br>
 * 
 * @version 1.0.1 2011-7-24<br>
 * @author Liang Li<br>
 */
public class MonitorPoolImpl extends MonitorPool {
	
	public Map<String, Host> getAllHostMap() {
		try {
			update();
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return hostMap;
	}

	public int update() throws Exception {
		
		hostMap = mntBridge.update(hostMap);
		
		return hostMap.size();
	}
	
	List<MonitorConf> confList = null;	
	
	public MonitorConf getMonitorConfByParName(String parName) {
		MonitorConf ret = null;
		try {
			String[] searchConditions = new String[] { "name", "type" };
			String[] operators = new String[] { "=" , "=" };
			Object[] values = new String[] { MonitorUtil.getMonitorConfGuidByPaName(parName) , MonitorConstants.MONITOR_PART_CONF_OBJECT };
			List<Asset> atList =  assetMgt.search(searchConditions, operators, values);
			ret = new MonitorConf(atList.get(0));
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			try {
				ret = new MonitorConf(parName);
				assetMgt.add(ret.getAsset(), true);
			}catch (Exception e0) {
				log.error(e0);
			}
			
		}
		return ret;
	}
	
	public MonitorConf setMonitorConf(MonitorConf conf) throws Exception {
		try {
			assetMgt.update(conf.getAsset().getGuid(), conf.getAsset());
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			throw e;
		}
		
		return conf;
	}
	
	public Service getSrvInHost(String srvName,String host) {
		Service srv = null;
		try {
			srv = hostMap.get(host).getSrv(srvName);
			if (srv == null) {
				throw new Exception("Can NOT find monitor srv: " 
									+ srvName 
									+ " in " 
									+ host
									+ "!");
			}
		}catch(Exception e) {
			log.error(e);
			srv = new Service(srvName);
		}
		return srv;
	}
	
	public Host getHost(String hostName) {
		Host res = null;
		try {
			this.update();
			res = hostMap.get(hostName);
			if (res == null) {
				res = new Host(hostName);
				hostMap.put(hostName, res);
			}
		}catch (Exception e) {
			log.error(e);
		}
		return res;
	}
	
	public String getHostState(String hostName) {
		String res = null;
		try {
			res = hostMap.get(hostName).getHostState();
			if (res == null) {
				throw new Exception("Can NOT get state for host: " 
						+ hostName 
						+ "in the pool!");
			}
		}catch (Exception e) {
			log.error(e);
			res = MonitorConstants.MONITOR_STAT_CRIT;
		}
		return res;
	}
	
	public String getSrvImgUri(String host, String srvName, long beg, long end) {
		String res = "";
		try {
			res = mntBridge.getSrvImgUri(host, srvName, beg, end);
		}catch (Exception e) {
			log.error(e);
			log.error(res);
			res = "";
		}
		return res;
	}
}
