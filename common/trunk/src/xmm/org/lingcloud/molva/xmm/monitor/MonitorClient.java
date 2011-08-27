/*
 *  @(#)MonitorClient.java  2011-7-24
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

import java.util.*;

import org.apache.commons.logging.*;

import org.lingcloud.molva.xmm.ac.*;
import org.lingcloud.molva.xmm.client.*;
import org.lingcloud.molva.xmm.pojos.*;
import org.lingcloud.molva.xmm.util.*;

import org.lingcloud.molva.xmm.monitor.pojos.*;
import org.lingcloud.molva.xmm.monitor.pool.*;

/**
 * <strong>Purpose:</strong><br>
 * 
 * @version 1.0.1 2011-7-24<br>
 * @author Liang Li<br>
 */
public class MonitorClient {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private static MonitorClient instanse = null;
	private MonitorClient() {
	}
	
	protected MonitorPool mntPool = MonitorPool.getInstanse();
	
	public static MonitorClient getInstanse() {
		if (instanse == null) {
			instanse = new MonitorClient();
		}
		return instanse;
	}
	
	protected long timePeriod = MonitorConstants.MONITOR_PERIOD_DEF;
	protected int itemsPerPage = MonitorConstants.MONITOR_ITEMPERPAGE_DEF;
	
	protected float getScoreByStats(int[][] stats) {
		float res = 0;
		try {
			float max = 0;
			for (int i = 0 ; i < stats.length ; i++) {
				max += ( stats[i][MonitorConstants.MONITOR_STAT_OK_INT] 
				                  + stats[i][MonitorConstants.MONITOR_STAT_WARN_INT]
				                  + stats[i][MonitorConstants.MONITOR_STAT_UNKN_INT]
				                  + stats[i][MonitorConstants.MONITOR_STAT_CRIT_INT] )*10;
				res += stats[i][MonitorConstants.MONITOR_STAT_OK_INT] * 10
				                  + stats[i][MonitorConstants.MONITOR_STAT_WARN_INT] * 7
				                  + stats[i][MonitorConstants.MONITOR_STAT_UNKN_INT] * 4;						
			}
			if (max > 0) {
				res = res/max * 100;
			}else
				res = 0;
		}catch (Exception e) {
			log.error(e);
			res = 0;
		}
		
		return res;
	}
	
	public String getStaticsInJson() {
		String res = "";
		try {
			XMMClient xmmClient = new XMMClient();
			List<Partition>  parList = xmmClient.listAllPartition();
			List<PhysicalNode> pnList = null;
			MonitorConf mntConf = null;
			String tmp = "";
			int[][] statics = new int[MonitorConstants.MONITOR_ITEM_LIST.length + 1][5];
			int size = 0;
			int count = 0;
			res += "{ flushHz:" + timePeriod
					+ ",partitionNum:"
					+ parList.size() 
					+ ",partitions:[";
			for (int i = 0 ; i < parList.size() ; i++) {
				for (int k = 0 ; k < MonitorConstants.MONITOR_ITEM_LIST.length + 1 ; k++) {
					statics[k][MonitorConstants.MONITOR_STAT_OK_INT] = 0;
					statics[k][MonitorConstants.MONITOR_STAT_WARN_INT] = 0;
					statics[k][MonitorConstants.MONITOR_STAT_UNKN_INT] = 0;
					statics[k][MonitorConstants.MONITOR_STAT_CRIT_INT] = 0;
				}
				try {
					String hostName = null;
					Host hostInfo = null;
					String srvName = null;
					Service srv = null;
					mntConf = mntPool.getMonitorConfByParName(parList.get(i).getName());
					pnList = xmmClient.listPhysicalNodeInPartition(parList.get(i).getGuid());
					for (int j = 0 ; j < pnList.size() ; j++) {
						hostName = pnList.get(j).getName();
						hostInfo = mntPool.getHost(hostName);
						if (hostInfo == null) {
							continue;
						}
						for (int k = 0 ; k < MonitorConstants.MONITOR_ITEM_LIST.length ; k++) {
							srvName = MonitorConstants.MONITOR_ITEM_LIST[k];
							if (!mntConf.isMonitorSrv(srvName)) {
								continue;
							}
							srv = hostInfo.getSrv(srvName);
							if (srv != null)
								statics[k][srv.getStatInt()]++;
							else 
								statics[k][MonitorConstants.MONITOR_STAT_CRIT_INT]++;
						}
						srvName = MonitorConstants.MONITOR_HOST_CPU;
						srv = hostInfo.getSrv(srvName);
						if (srv != null)
							statics[MonitorConstants.MONITOR_ITEM_LIST.length][srv.getStatInt()]++;
						else 
							statics[MonitorConstants.MONITOR_ITEM_LIST.length][MonitorConstants.MONITOR_STAT_CRIT_INT]++;
						
					}
					
					tmp += "{ partitionName:'" + parList.get(i).getName() + "',";
					size = MonitorConstants.MONITOR_ITEM_LIST.length;
					int kc = 0;
					int len = MonitorConstants.MONITOR_ITEM_LIST.length;
					tmp += "phyNode:[" 
								+ statics[len][MonitorConstants.MONITOR_STAT_OK_INT] + ","
								+ statics[len][MonitorConstants.MONITOR_STAT_WARN_INT] + ","
								+ statics[len][MonitorConstants.MONITOR_STAT_CRIT_INT] + ","
								+ statics[len][MonitorConstants.MONITOR_STAT_UNKN_INT] + "]";
					for (int k = 0 ; k < size ; k++) {
						srvName = MonitorConstants.MONITOR_ITEM_LIST[k];
						if (!mntConf.isMonitorSrv(srvName)) {
							continue;
						}
						tmp += ",";
						tmp += srvName + ":[";
						tmp += statics[k][MonitorConstants.MONITOR_STAT_OK_INT] + ","
								+ statics[k][MonitorConstants.MONITOR_STAT_WARN_INT] + ","
								+ statics[k][MonitorConstants.MONITOR_STAT_CRIT_INT] + ","
								+ statics[k][MonitorConstants.MONITOR_STAT_UNKN_INT] + "]";
						kc++;
					}
					tmp += ",healthScore:" + getScoreByStats(statics); 
					tmp += "}";
					
				}catch (Exception e) {
					log.error(e);
					tmp = "";
					continue;
				}
				if (count > 0) {
					res += ",";
				}
				count++;
				res += tmp;
				tmp = "";
			}
			res += "]}";
		}catch (Exception e) {
			log.error(e);
			res = "";
		}
		
		return res;
	}
	
	public String getPartitionInJson() {
		try {
			XMMClient vxc = new XMMClient();
			List<Partition> pl = vxc.listAllPartition();
			Json json = new Json();
			json.reSet();
			json.setSuccess(true);
			for (int i = 0; i < pl.size(); i++) {
				Partition par = pl.get(i);
				String ktag = par.getAttributes().get(
						PartitionAC.REQUIRED_ATTR_NODETYPE);
				String ktype = "Unknown";
				String kvalue = "Unknown";
				if (ktag == null || "".equals(ktag)) {
					//kvalue = "Unknown";
					kvalue = "vegaHPC.jpg";
					ktype = "HPC";
				} else if (ktag.equals(PartitionAC.HPC)) {
					//kvalue = PartitionAC.HPC_SOFT;
					kvalue = "vegaHPC.jpg";
					ktype = "HPC";
				} else if (ktag.equals(PartitionAC.VM)) {
					//kvalue = PartitionAC.VM_SOFT;
					kvalue = "vegaVM.jpg";
					ktype = "VM";
				} else if (ktag.equals(PartitionAC.DC)) {
					//kvalue = PartitionAC.MR_SOFT;
					kvalue = "vegaMapReduce.jpg";
					ktype = "MR";
				} else if (ktag.equals(PartitionAC.STORAGE)) {
					//kvalue = PartitionAC.STORAGE_SOFT;
					kvalue = "vegaStorage.jpg";
					ktype = "STORAGE";
				} else{
					kvalue = "vegaHPC.jpg";
					ktype = "GENERAL";
				}
				json.addItem("PartionName", par.getName());
				json.addItem("partionType", ktype);
				json.addItem("partionTypeImage", kvalue);
				json.addItemOk();

			}
			return json.toString();
		}catch (Exception e) {
			log.error(e);
			return "";
		}
	}
	
	public String getHost4PartitionInJson(int parId, int pageId) {
		String res = "";
		try {
			Map<String, Host> hostMap = mntPool.getAllHostMap();
			XMMClient vxc = new XMMClient();
			List<Partition> pl = vxc.listAllPartition();
			Partition par = pl.get(parId);
			List<PhysicalNode> pnList = vxc.listPhysicalNodeInPartition(par.getGuid());
			List<String> monitorItems = new ArrayList<String>();
			MonitorConf mntConf = mntPool.getMonitorConfByParName(par.getName());
			int totalPhysicalNodeNum = 0;
			totalPhysicalNodeNum = pnList.size();
			int left = totalPhysicalNodeNum % itemsPerPage;
			if (left > 0)
				left = 1;
			
			res += "{partitionName:'" + par.getName()
					+ "',phyNodeTotalNum:" + totalPhysicalNodeNum
					+ ",totalPage:"
					+ (totalPhysicalNodeNum / itemsPerPage + left)
					+ ",currPage:" + pageId;
			
			res += ",phyNodes:[";
			
			String tmp = "";
			int hostCount = 0;
			pageId--;
			for (int i = pageId * itemsPerPage ;
					i < totalPhysicalNodeNum && i < (pageId + 1) * itemsPerPage; i++) {
				try {
					String hostName = pnList.get(i).getName();
					Host hostInfo = hostMap.get(hostName);
					String srvName = null;
					String stat = null;
					Service srv = null;
					if (hostInfo == null) {
						hostInfo = new Host(hostName, hostName, MonitorUtil.getCurrTime());
					}
					stat = hostInfo.getHostState();
					tmp += "{";
					tmp += "hostName:'" + hostInfo.getHostIP();
					tmp += "',hostState:'" + stat;
					tmp += "',hostPic:'" + getHostPic(stat);
					tmp += "',monitorInfors:[";
					int count = 0;
					for (int k = 0 ; k < MonitorConstants.MONITOR_ITEM_LIST.length - 1 ; k++) {
						srvName = MonitorConstants.MONITOR_ITEM_LIST[k];
						if (!mntConf.isMonitorSrv(srvName)) {
							continue;
						}
						srv = hostInfo.getSrv(srvName);
						if (srv == null){
							srv = new Service(srvName);
						}
						if (count > 0) {
							tmp += ",";
						}
						count++;
						tmp += "{srvName:'" + srvName;
						tmp += "',srvState:'" + srv.getStat();
						tmp += "',srvCheckTime:'" + srv.getTime();
						tmp += "',srvStatInfor:'" + srv.getInfo() + "'}"; 
						monitorItems.add(srvName);
					}
					tmp += "]";
					
					tmp += ",MonitorItems:[";
					for (int j = 0; j < monitorItems.size(); j++) {
						tmp += "'" + monitorItems.get(j) + "'";
						if (j < monitorItems.size() - 1)
							tmp += ",";
					}
					tmp += "]";
					
					srvName = MonitorConstants.MONITOR_HOST_VMLIST;
					if ( mntConf.isMonitorSrv(srvName)) {
						tmp += ",vmMonitor:true";
						
						tmp += ",vmSrvName:'" + srvName + "'"; 
						tmp += ",VMList:[";

						Map<String, VM> vmMap = hostInfo.getVMMap();
						int c = 0;
						for (String vmName : vmMap.keySet()) {
							c++;
							if (c > 1) {
								tmp += ",";
							}
							tmp += "{ vmName:'" + vmName + "',";
							tmp += "vmPic:'" + getHostPic(vmMap.get(vmName).getStat()) + "' }";
						}
						tmp += "],";
						
						srv = hostInfo.getSrv(srvName);
						tmp += "vmStatus:'" 
								+ srv.getStat() + "',";
						
						tmp += "vmCheckTime:'" + MonitorUtil.getCurrTime() + "'";
						
					}else {
						tmp += ",vmMonitor:false";
					}
					

					tmp += "}";
					hostCount++;
				}catch (Exception e) {
					log.error(e);
					log.error(tmp);
					tmp = "";
				}
				if (hostCount > 1) {
					res += ",";
				}
				res += tmp;
				tmp = "";
			}
			
			res += "]}";
			
			log.info(res);
			
		}catch (Exception e) {
			log.error(e);
			log.error(res);
			res = "";
		}
		return res;
	}
	
	public String getVMInfos(String hostName, String vmName) {
		String res = "";
		Host host = mntPool.getHost(hostName);
		VM vm = host.getVMMap().get(vmName);
		VM.CPUInfo cpu = vm.getCPUInfo();
		VM.MemInfo mem = vm.getMemInfo();
		VM.DiskInfo disk = vm.getDiskInfo();
		VM.NetInfo net = vm.getNetInfo();
		
		res += "{vmName:'" + vmName + "',\n"
				+ "hostName:'" + hostName + "',"
				+ "chkTime:'" + host.getHostTime() + "',";
		res += "runStat:'" + vm.getRunStat() + "',\n";
		res += "hostPic:'" + this.getHostPic(vm.getStat()) + "',\n";
		res += "cpu:{"
				+ "vcpus:" + cpu.vcpu + ","
				+ "time:'" + cpu.cpuTime + "',"
				+ "stat:'" + cpu.stat + "',"
				+ "cpu:" + cpu.cpu + "\n},";
		
		res += "mem:{"
			+ "mem:" + mem.currMem + ","
			+ "usage:" + mem.currMem + ","
			+ "max:'" + mem.memory + "',"
			+ "maxPer:'" + (mem.memory > 0 ? ((int)mem.currMem*100/(int)mem.memory): 100) + "'"
			+ "\n},";
		
		res += "net:[";
		for (int i = 0 ; i < net.vifList.size() ; i++) {
			VM.VifInfo vif = net.vifList.get(i);
			if (i>0)
				res += ",";
			res += "{dev:'" + vif.dev + "',"
				+ "ip:'" + vif.ip + "',"
				+ "mac:'" + vif.mac + "',"
				+ "tx:'" + vif.tx_bytes + "',"
				+ "rx:'" + vif.rx_bytes + "'}";
		}
		res += "],";
		res += "disk:[";
		for (int i = 0 ; i < disk.imgList.size() ; i++) {
			VM.ImageInfo img = disk.imgList.get(i);
			if (i>0)
				res += ",";
			res += "{ img:'" + img.img + "',"
				+ "fmt:'" + img.fmt + "',"
				+ "virSize:'" + img.virSize + "',"
				+ "size:'" + img.size + "',"
				+ "bak:'" + img.bak + "'}";
		}
		res += "]";
		
		res += "}";
		return res;
	}
	
	public String getMonitorConfInJson(int parId) {
		String res = "";
		try {
			XMMClient vxc = new XMMClient();
			List<Partition> pl = vxc.listAllPartition();
			Partition par = pl.get(parId);
			MonitorConf mntConf = mntPool.getMonitorConfByParName(par.getName());
			res += "{flushHz:" + timePeriod / 1000;
			res += ",recordPerPage:" + itemsPerPage;
			res += ",partitionName:'" + mntConf.getParName() + "'";
			res += ",itemList:[";
			for (int i = 0; i < MonitorConstants.itemID.length; i++) {
				String item = MonitorConstants.MONITOR_ITEM_LIST[i];
				if (i > 0 )
					res += ",";
				res += "{itemName:'" + item + "',CtgID:"
						+ MonitorConstants.itemID[i];
				res += ",checked:";
				if (mntConf.isMonitorSrv(item)) {
					res += "1}";
				} else {
					res += "0}";
				}
			}
			res += "],";
			res += "CtgList:[";
			for (int i = 0; i < MonitorConstants.monitorItemCtg.length ; i++) {
				if (i > 0) 
					res += ",";
				res += "'" + MonitorConstants.monitorItemCtg[i] + "'";
			}
			res += "]}";
			
		}catch (Exception e) {
			log.error(e);
			log.error(res);
			res = "";
		}
		return res;
	}
	
	public String setMonitorConfByParName(String setings, int parId) {
		try {
			String[] strList = setings.split(";");
			
			if (strList.length > 1) {
				timePeriod = Long.parseLong(strList[0]) * 1000;
				mntPool.setTimePeriod(timePeriod);
				
				itemsPerPage = Integer.parseInt(strList[1]);
				if (itemsPerPage < 1)
					itemsPerPage = MonitorConstants.MONITOR_ITEMPERPAGE_DEF;
				
				XMMClient vxc = new XMMClient();
				List<Partition> pl = vxc.listAllPartition();
				Partition par = pl.get(parId);
				MonitorConf mntConf = mntPool.getMonitorConfByParName(par.getName());

				String item;
				mntConf.removeAllMonitorSrv();
				for (int j = 2; j < strList.length; j++) {
					item = strList[j];
					mntConf.addMonitorSrv(item);
				}
				mntConf.setItemPerPage(itemsPerPage);
				mntConf.setMonitorPeriod(timePeriod);
				mntPool.setMonitorConf(mntConf);
				return "OK";
			}
		}catch (Exception e) {
			log.error(e);
		}
		return "ERROR";
	}
	
	public String getHosts4Srv(String srvName, int parId) {
		String res = "";
		try {
			XMMClient vxc = new XMMClient();
			List<Partition> pl = vxc.listAllPartition();
			Partition par = pl.get(parId);
			List<PhysicalNode> hil = vxc.listPhysicalNodeInPartition(par.getGuid());
			Service srv;
			String hostName;
			int num = hil.size();
			res += "{partitionName:'" + par.getName() + "',";
			res += "hostNum:" + num ;
			res += ",tableHead:['" 
								+ "Host','"
								+ "Service', '"
								+ "Status', '"
								+ "Last Check Time', '"
								+ "Status Information" + "']";
			res += ",HostInfors:[";
			for (int i = 0 ; i < num ; i++) {
				hostName = hil.get(i).getName();
				srv = mntPool.getSrvInHost(srvName, hostName);
				if (i > 0) {
					res += ",";
				}
				res += "{hostName:'" + hostName;
				res += "',srvName:'" + srvName;
				res += "',srvState:'" + srv.getStat();
				res += "',srvCheckTime:'" + srv.getTime();
				res += "',srvStatInfor:'" + srv.getInfo() + "'}";
			}
			res += "]}";
			log.info(res);
			
		}catch (Exception e) {
			log.error(e);
			log.error(res);
			res = "false";
		}
		return res;
	}
	
	public String getNodesByState(String state, int parId) {
		String res = "";
		try {
			XMMClient vxc = new XMMClient();
			List<Partition> pl = vxc.listAllPartition();
			List<PhysicalNode> hil ;
			String s;
			String hostName;
			Host host;
			if (parId < 0) {
				List<PhysicalNode> hs ;
				hil = new ArrayList<PhysicalNode>();
				for (int i = 0 ; i < pl.size() ; i++) {
					hs = vxc.listPhysicalNodeInPartition(pl.get(i).getGuid());
					hil.addAll(hs);
				}
				s = "All Partitions";
			}else {
				hil = vxc.listPhysicalNodeInPartition(pl.get(parId).getGuid());
				s = pl.get(parId).getName();
			}
			int num = hil.size();
			res += "{partitionName:'" + s + "',";
			res += "hostNum:" + num ;
			res += ",tableHead:['" 
								+ "Host','"
								+ "Service', '"
								+ "Status', '"
								+ "Last Check Time', '"
								+ "Status Information" + "']";
			res += ",HostInfors:[";
			int count = 0;
			for (int i = 0 ; i < num ; i++) {
				hostName = hil.get(i).getName();
				s = mntPool.getHostState(hostName);
				if (!state.contains(s)) {
					continue;
				}
				if (count > 0) {
					res += ",";
				}
				host = mntPool.getHost(hostName);
				count++;
				res += "{hostName:'" + hostName;
				res += "',srvState:'" + state;
				res += "',srvCheckTime:'" + host.getHostTime();
				res += "',srvStatInfor:'" + host.getHostInfo() + "'}";
			}
			res += "]}";
			log.info(res);
			
		}catch (Exception e) {
			log.error(e);
			log.error(res);
			res = "false";
		}
		return res;
	}
	
	public String getSrvHistoryImg(String host, String srvName) {
		String res = "";
		long end;
		String imguri;
		try {
			res += "{Infors:[";
			
			end = MonitorUtil.getSecendsByPeriod(MonitorConstants.MONITOR_IMG_PERIOD_MINS);
			imguri = mntPool.getSrvImgUri(host, srvName, 0, end);
			if ("".equals(imguri)) {
				throw new Exception("Can't get History Image for " + host + " in Host " + host);
			}
			res += "{ Item:" 
						+ "'1 Hour'"
						+ ",url:'"
						+ MonitorUtil.getImgDirInWebapp()
						+ "/"
						+ imguri
						+ "'},";
			
			end = MonitorUtil.getSecendsByPeriod(MonitorConstants.MONITOR_IMG_PERIOD_HOURS);
			imguri = mntPool.getSrvImgUri(host, srvName, 0, end);
			res += "{ Item:" 
						+ "'1 Day'"
						+ ",url:'"
						+ MonitorUtil.getImgDirInWebapp()
						+ "/"
						+ imguri
						+ "'},";
			
			end = MonitorUtil.getSecendsByPeriod(MonitorConstants.MONITOR_IMG_PERIOD_DAYS);
			imguri = mntPool.getSrvImgUri(host, srvName, 0, end);
			res += "{ Item:" 
						+ "'1 Month'"
						+ ",url:'"
						+ MonitorUtil.getImgDirInWebapp()
						+ "/"
						+ imguri
						+ "'},";
			
			end = MonitorUtil.getSecendsByPeriod(MonitorConstants.MONITOR_IMG_PERIOD_MONTHS);
			imguri = mntPool.getSrvImgUri(host, srvName, 0, end);
			res += "{ Item:" 
						+ "'1 Year'"
						+ ",url:'"
						+ MonitorUtil.getImgDirInWebapp()
						+ "/"
						+ imguri
						+ "'}],";
			
			res += "hostName:'" + host + "',"
						+ "srvName:'" + srvName + "'"
						+ "}";
		}catch(Exception e) {
			log.error(e);
			log.error(res);
			res = "false";
		}
		return res;
	}
	
	protected String getHostPic(String stat) {
		if (MonitorConstants.MONITOR_STAT_OK.equals(stat)) {
			return MonitorConstants.MONITOR_PIC_OK;
		}else if (MonitorConstants.MONITOR_STAT_WARN.equals(stat)) {
			return MonitorConstants.MONITOR_PIC_WARN;
		}else {
			return MonitorConstants.MONITOR_PIC_CRIT;
		}
	}
	
}
