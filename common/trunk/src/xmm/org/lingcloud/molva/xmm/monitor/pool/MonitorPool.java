/*
 *  @(#)MonitorPool.java  2011-7-24
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

import org.lingcloud.molva.xmm.monitor.pojos.*;
import org.lingcloud.molva.ocl.asset.*;

/**
 * <strong>Purpose:</strong><br>
 * 
 * @version 1.0.1 2011-7-24<br>
 * @author Liang Li<br>
 */
public abstract class MonitorPool {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	private static MonitorPool mntPool = null;
	public static MonitorPool getInstanse() {
		if (mntPool == null) {
			mntPool = new MonitorPoolImpl();	
		}
		return mntPool;
	}
	
	protected MonitorBridge				mntBridge = MonitorBridge.getInstanse();
	protected Map<String, Host>	hostMap = new HashMap<String, Host>();
	
	protected AssetManagerImpl		assetMgt = new AssetManagerImpl();
	
	public abstract Map<String, Host> getAllHostMap() ;
	
	public abstract MonitorConf getMonitorConfByParName(String parName);
	
	public abstract MonitorConf setMonitorConf(MonitorConf conf) throws Exception;
	
	public abstract Service getSrvInHost(String srvName,String host);
	
	public abstract String getHostState(String hostName);
	
	public abstract Host getHost(String hostName);
	
	public abstract String getSrvImgUri(String host, String srvName, long beg, long end);
	
	public abstract int update() throws Exception;
	
}
