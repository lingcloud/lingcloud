/*
 *  @(#)MonitorConf.java  2011-7-24
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

import org.lingcloud.molva.ocl.asset.*;
import org.lingcloud.molva.ocl.util.HashFunction;
import org.lingcloud.molva.xmm.monitor.MonitorConstants;
import org.lingcloud.molva.xmm.monitor.MonitorUtil;

/**
 * <strong>Purpose:</strong><br>
 * 
 * @version 1.0.1 2011-7-24<br>
 * @author Liang Li<br>
 */
public class MonitorConf {
	
	private Asset asset = null;
	
	public MonitorConf(Asset asset) {
		this.asset = asset;
	}
	
	public MonitorConf(String parName) {
		asset = new Asset();
		asset.setGuid(
				HashFunction.createGUID().toString());
		
		asset.setAcl(MonitorConstants.MONITOR_DEFAULT_ACL);
		asset.setType(MonitorConstants.MONITOR_PART_CONF_OBJECT);
		asset.setAssetController("AssetController");
		
		setParName(parName);
		Map<String, String> arr = asset.getAttributes();
		for (int i= 0 ; i < MonitorConstants.MONITOR_ITEM_LIST.length ; i++) {
			arr.put(MonitorConstants.MONITOR_ITEM_LIST[i], "true");
		}
		setMonitorPeriod(MonitorConstants.MONITOR_PERIOD_DEF);
		setItemPerPage(MonitorConstants.MONITOR_ITEMPERPAGE_DEF);
	}
	
	public void setAsset(Asset as) throws Exception {
		if (!MonitorConstants.MONITOR_PART_CONF_OBJECT.equals(asset.getType())) {
			throw new Exception("The asset type is NOT " 
					+ MonitorConstants.MONITOR_PART_CONF_OBJECT);
		}
		asset = as;
	}
	
	public Asset getAsset() {
		return asset;
	}
	
	public void addMonitorSrv(String srvName) {
		asset.getAttributes().put(srvName, "true");
	}
	
	public void removeMonitorSrv(String srvName) {
		asset.getAttributes().put(srvName, "false");
	}
	
	public void removeAllMonitorSrv() {
		for (int i= 0 ; i < MonitorConstants.MONITOR_ITEM_LIST.length ; i++) {
			removeMonitorSrv(MonitorConstants.MONITOR_ITEM_LIST[i]);
		}
	}
	
	public boolean isMonitorSrv(String srvName) {
		if ("true".equals(
				asset.getAttributes().get(srvName))) {
			return true;
		}
		return false;
	}
	
	public void setParName(String name) {
		asset.setName(MonitorUtil.getMonitorConfGuidByPaName(name));
		asset.getAttributes().put("parName", name);
	}
	
	public String getParName() {
		return asset.getAttributes().get("parName");
	}
	
	public int getItemPerPage() {
		try {
			int ret = Integer.parseInt(
					asset.getAttributes().get(MonitorConstants.MONITOR_ITEMPERPAGE));
			return ret;
		}catch (Exception e) {
			setItemPerPage(MonitorConstants.MONITOR_ITEMPERPAGE_DEF);
			return MonitorConstants.MONITOR_ITEMPERPAGE_DEF;
		}
	}
	
	public void setItemPerPage(int num) {
		asset.getAttributes().put(MonitorConstants.MONITOR_ITEMPERPAGE, 
									Integer.toString(num));
	}
	
	public long getMonitorPeriod() {
		try {
			long ret = Long.parseLong(
					asset.getAttributes().get(MonitorConstants.MONITOR_PERIOD));
			return ret;
		}catch (Exception e) {
			setMonitorPeriod(MonitorConstants.MONITOR_PERIOD_DEF);
			return MonitorConstants.MONITOR_PERIOD_DEF;
		}
	}
	
	public void setMonitorPeriod(long p) {
		asset.getAttributes().put(MonitorConstants.MONITOR_PERIOD, Long.toString(p));
	}
}
