/*
 *  @(#)GangliaBridge.java  2011-7-24
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

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.lingcloud.molva.xmm.monitor.pojos.*;
import org.lingcloud.molva.xmm.monitor.*;
import org.lingcloud.molva.xmm.util.*;
import org.w3c.dom.*;

/**
 * <strong>Purpose:</strong><br>
 * 
 * @version 1.0.1 2011-7-24<br>
 * @author Liang Li<br>
 */
class GangliaBridge extends MonitorBridge {
	
	public static void main(String args[]) {
		try {
			System.setProperty("lingcloud.home", "/opt/lingcloud");
			MonitorBridge b = new GangliaBridge();
			Map<String, Host> hs = new HashMap<String, Host>(); 
			
			hs = b.getHostMap(hs);
			
			System.out.println(hs.get("10.0.0.10"));
		}catch(Exception e) {
			log.error(e);
		}
	}
	
	private XPath xpath = XPathFactory.newInstance().newXPath();
	
	private Map<String, Host> hostMap = null;
	
	protected Document getGangliaMonitorData() throws Exception {
		
		Process child = null;
		String command = MonitorUtil.getGangliaMonitorCmd();
		String sep = System.getProperty("line.separator");
		
		child = Runtime.getRuntime().exec(command);

		// Get the input stream and read from it
		BufferedReader in = new BufferedReader(new InputStreamReader(
				child.getInputStream()));
		String c = null;
		StringBuilder sb = new StringBuilder();
		while ((c = in.readLine()) != null) {
			if (c.startsWith("<"))
				break;
		}
		sb.append(c + sep);
		while ((c = in.readLine()) != null) {
			sb.append(c + sep);
		}
		in.close();
		child.destroy();
		//System.out.println(sb);
		return XmlUtil.build(sb.toString());
	}
	
	protected Map<String, Service> getSrvFromNode(Map<String, Service> srvMap, Node hostNode) {
		String[] fields = MonitorConstants.MONITOR_ITEM_LIST;
		Method[] meths = this.getClass().getMethods();

		String methName = "";
		Method meth = null;
		Class[] args = new Class[]{Map.class, Node.class };
		for (int i = 0 ; i < fields.length ; i++) {
			try {
				methName = "getSrv_" + fields[i];
				meth = GangliaBridge.class.getDeclaredMethod(methName, args);
				
				meth.invoke(this, srvMap, hostNode);
			}catch(NoSuchMethodException e) {
				log.warn("Can't find method in GangliaBridge: " + methName + " - " + e);
			}catch (Exception e) {
				log.warn("Invoke method in GangliaBridge : " + methName + " - " + e);
			}
		}
		
		return srvMap;
	}
	
	protected Service getSrv_CPU_Load(Map<String, Service> srvMap, Node hostNode) throws Exception {
		String key = MonitorConstants.MONITOR_HOST_CPU;
		String name = key;
		String stat = MonitorConstants.MONITOR_STAT_UNKN;
		String tm = xpath.evaluate("./@REPORTED", hostNode);
		String info ;
		
		tm = MonitorUtil.getTimeFromInt(tm);
		String avg1 = xpath.evaluate("./METRIC[@NAME='load_one']/@VAL", hostNode);
		String avg5 = xpath.evaluate("./METRIC[@NAME='load_five']/@VAL", hostNode);
		String avg15 = xpath.evaluate("./METRIC[@NAME='load_fifteen']/@VAL", hostNode);
		float f = Float.parseFloat(avg15);
		if (f > 0.90f) {
			stat = MonitorConstants.MONITOR_STAT_CRIT;
		}else if (f > 0.70f) {
			stat = MonitorConstants.MONITOR_STAT_WARN;
		}else {
			stat = MonitorConstants.MONITOR_STAT_OK;
		}
		info = "CPU "
				+ stat 
				+ " - load average: " 
				+ "one_min "
				+ avg1 
				+ "; " 
				+ "five_min "
				+ avg5 
				+ "; "
				+ "fifteen_min "
				+ avg15;
		
		Service srv = srvMap.get(key);
		if (srv == null) {
			srv = new Service(name, stat, tm, info);
			srvMap.put(key, srv);
		}else {
			srv.setStat(stat);
			srv.setTime(tm);
			srv.setInfo(info);
		}
		
		return srv;
	}
	
	protected Service getSrv_Swap_Usage(Map<String, Service> srvMap, Node hostNode) throws Exception {
		
		String key = MonitorConstants.MONITOR_HOST_SWAP;
		String name = key;
		String stat = MonitorConstants.MONITOR_STAT_UNKN;
		String tm = xpath.evaluate("./@REPORTED", hostNode);
		String info ;
		
		tm = MonitorUtil.getTimeFromInt(tm);
		String total = xpath.evaluate("./METRIC[@NAME='swap_total']/@VAL", hostNode);
		String free = xpath.evaluate("./METRIC[@NAME='swap_free']/@VAL", hostNode);
		String unit1 = xpath.evaluate("./METRIC[@NAME='swap_total']/@UNITS", hostNode);
		String unit2 = xpath.evaluate("./METRIC[@NAME='swap_free']/@UNITS", hostNode);
		float f1 = Float.parseFloat(total);
		float f2 = Float.parseFloat(free);
		float f = f2/f1;
		if (1 - f > 0.90f) {
			stat = MonitorConstants.MONITOR_STAT_CRIT;
		}else if ( 1 - f > 0.70f ) {
			stat = MonitorConstants.MONITOR_STAT_WARN;
		}else {
			stat = MonitorConstants.MONITOR_STAT_OK;
		}
		info = "SWAP " 
				+ stat 
				+ " - " 
				+ String.format("%.1f", f*100) 
				+ "% free ( " 
				+ free + " "
				+ unit2
				+ " out of "
				+ total + " "
				+ unit1
				+ " )";
		
		Service srv = srvMap.get(key);
		if (srv == null) {
			srv = new Service(name, stat, tm, info);
			srvMap.put(key, srv);
		}else {
			srv.setStat(stat);
			srv.setTime(tm);
			srv.setInfo(info);
		}
		
		return srv;
	}
	
	protected Service getSrv_Memory_Usage(Map<String, Service> srvMap, Node hostNode) throws Exception {
		
		String key = MonitorConstants.MONITOR_HOST_MEMORY;
		String name = key;
		String stat = MonitorConstants.MONITOR_STAT_UNKN;
		String tm = xpath.evaluate("./@REPORTED", hostNode);
		String info ;
		
		tm = MonitorUtil.getTimeFromInt(tm);
		String total = xpath.evaluate("./METRIC[@NAME='mem_total']/@VAL", hostNode);
		String free = xpath.evaluate("./METRIC[@NAME='mem_free']/@VAL", hostNode);
		String cach = xpath.evaluate("./METRIC[@NAME='mem_cached']/@VAL", hostNode);
		String unit1 = xpath.evaluate("./METRIC[@NAME='mem_total']/@UNITS", hostNode);
		String unit2 = xpath.evaluate("./METRIC[@NAME='mem_free']/@UNITS", hostNode);
		String unit3 = xpath.evaluate("./METRIC[@NAME='mem_cached']/@UNITS", hostNode);
		float f1 = Float.parseFloat(total);
		float f2 = Float.parseFloat(free);
		float f = f2/f1;
		if (1 - f > 0.90f) {
			stat = MonitorConstants.MONITOR_STAT_CRIT;
		}else if ( 1 - f > 0.70f ) {
			stat = MonitorConstants.MONITOR_STAT_WARN;
		}else {
			stat = MonitorConstants.MONITOR_STAT_OK;
		}
		info = "MEMORY " 
				+ stat 
				+ " - " 
				+ String.format("%.1f", f*100) 
				+ "% free ( " 
				+ free + " "
				+ unit1
				+ " out of "
				+ total
				+ unit2
				+ " ), "
				+ "cached: "
				+ cach + " "
				+ unit3 ;
		
		Service srv = srvMap.get(key);
		if (srv == null) {
			srv = new Service(name, stat, tm, info);
			srvMap.put(key, srv);
		}else {
			srv.setStat(stat);
			srv.setTime(tm);
			srv.setInfo(info);
		}
		
		return srv;
	}
	
	protected Service getSrv_Disk_Usage(Map<String, Service> srvMap, Node hostNode) throws Exception {
		String key = MonitorConstants.MONITOR_HOST_DISK;
		String name = key;
		String stat = MonitorConstants.MONITOR_STAT_UNKN;
		String tm = xpath.evaluate("./@REPORTED", hostNode);
		String info ;
		
		tm = MonitorUtil.getTimeFromInt(tm);
		String total = xpath.evaluate("./METRIC[@NAME='disk_total']/@VAL", hostNode);
		String free = xpath.evaluate("./METRIC[@NAME='disk_free']/@VAL", hostNode);
		String unit1 = xpath.evaluate("./METRIC[@NAME='disk_total']/@UNITS", hostNode);
		String unit2 = xpath.evaluate("./METRIC[@NAME='disk_free']/@UNITS", hostNode);
		float f1 = Float.parseFloat(total);
		float f2 = Float.parseFloat(free);
		float f = f2/f1;
		if (1 - f > 0.90f) {
			stat = MonitorConstants.MONITOR_STAT_CRIT;
		}else if ( 1 - f > 0.70f ) {
			stat = MonitorConstants.MONITOR_STAT_WARN;
		}else {
			stat = MonitorConstants.MONITOR_STAT_OK;
		}
		info = "DISK " 
				+ stat 
				+ " - " 
				+ String.format("%.1f", f*100) 
				+ "% free ( " 
				+ free + " "
				+ unit2
				+ " out of "
				+ total + " "
				+ unit1
				+ " )";
		
		Service srv = srvMap.get(key);
		if (srv == null) {
			srv = new Service(name, stat, tm, info);
			srvMap.put(key, srv);
		}else {
			srv.setStat(stat);
			srv.setTime(tm);
			srv.setInfo(info);
		}
		
		return srv;
	}
	
	protected Service getSrv_Net_Traffic(Map<String, Service> srvMap, Node hostNode) throws Exception {
		String key = MonitorConstants.MONITOR_HOST_NET;
		String name = key;
		String stat = MonitorConstants.MONITOR_STAT_UNKN;
		String tm = xpath.evaluate("./@REPORTED", hostNode);
		String info ;
		
		tm = MonitorUtil.getTimeFromInt(tm);
		String bytes_in = xpath.evaluate("./METRIC[@NAME='bytes_in']/@VAL", hostNode);
		String bytes_out = xpath.evaluate("./METRIC[@NAME='bytes_out']/@VAL", hostNode);
		String unit1 = xpath.evaluate("./METRIC[@NAME='bytes_in']/@UNITS", hostNode);
		String unit2 = xpath.evaluate("./METRIC[@NAME='bytes_out']/@UNITS", hostNode);
		float f1 = Float.parseFloat(bytes_in);
		float f2 = Float.parseFloat(bytes_out);
		double f = (f2+f1)/1e8;
		if (f > 0.90f) {
			stat = MonitorConstants.MONITOR_STAT_CRIT;
		}else if (f > 0.70f ) {
			stat = MonitorConstants.MONITOR_STAT_WARN;
		}else {
			stat = MonitorConstants.MONITOR_STAT_OK;
		}
		info = "NET " 
				+ stat 
				+ " - " 
				+ "Traffic in: "
				+ bytes_in + " "
				+ unit1
				+ ", Traffic out:"
				+ bytes_in + " " 
				+ unit2;
		
		Service srv = srvMap.get(key);
		if (srv == null) {
			srv = new Service(name, stat, tm, info);
			srvMap.put(key, srv);
		}else {
			srv.setStat(stat);
			srv.setTime(tm);
			srv.setInfo(info);
		}
		
		return srv;
	}
	
	protected Service getSrv_Current_Users(Map<String, Service> srvMap, Node hostNode) throws Exception {
		String key = MonitorConstants.MONITOR_HOST_USER;
		String name = key;
		String stat = MonitorConstants.MONITOR_STAT_UNKN;
		String tm = xpath.evaluate("./@REPORTED", hostNode);
		String info ;
		
		tm = MonitorUtil.getTimeFromInt(tm);
		String total = xpath.evaluate("./METRIC[@NAME='current_user']/@VAL", hostNode);

		float f1 = Float.parseFloat(total);
		float f = f1/20.0f;
		if ( f > 0.90f) {
			stat = MonitorConstants.MONITOR_STAT_CRIT;
		}else if ( f > 0.70f ) {
			stat = MonitorConstants.MONITOR_STAT_WARN;
		}else {
			stat = MonitorConstants.MONITOR_STAT_OK;
		}
		info = "USERS " 
				+ stat 
				+ " - " 
				+ total 
				+ " users currently logged in";
		
		Service srv = srvMap.get(key);
		if (srv == null) {
			srv = new Service(name, stat, tm, info);
			srvMap.put(key, srv);
		}else {
			srv.setStat(stat);
			srv.setTime(tm);
			srv.setInfo(info);
		}
		
		return srv;
	}
	
	protected Service getSrv_Total_Processes(Map<String, Service> srvMap, Node hostNode) throws Exception {
		String key = MonitorConstants.MONITOR_HOST_TPROCESS;
		String name = key;
		String stat = MonitorConstants.MONITOR_STAT_UNKN;
		String tm = xpath.evaluate("./@REPORTED", hostNode);
		String info ;
		
		tm = MonitorUtil.getTimeFromInt(tm);
		String total = xpath.evaluate("./METRIC[@NAME='total_process']/@VAL", hostNode);

		float f1 = Float.parseFloat(total);
		float f = f1/400.0f;
		if ( f > 0.90f) {
			stat = MonitorConstants.MONITOR_STAT_CRIT;
		}else if ( f > 0.70f ) {
			stat = MonitorConstants.MONITOR_STAT_WARN;
		}else {
			stat = MonitorConstants.MONITOR_STAT_OK;
		}
		info = "PROCS " 
				+ stat 
				+ " - " 
				+ total 
				+ " processes are running";
		
		Service srv = srvMap.get(key);
		if (srv == null) {
			srv = new Service(name, stat, tm, info);
			srvMap.put(key, srv);
		}else {
			srv.setStat(stat);
			srv.setTime(tm);
			srv.setInfo(info);
		}
		
		return srv;
	}
	
	protected Service getSrv_Zombie_Processes(Map<String, Service> srvMap, Node hostNode) throws Exception {
		String key = MonitorConstants.MONITOR_HOST_ZPROCESS;
		String name = key;
		String stat = MonitorConstants.MONITOR_STAT_UNKN;
		String tm = xpath.evaluate("./@REPORTED", hostNode);
		String info ;
		
		tm = MonitorUtil.getTimeFromInt(tm);
		String total = xpath.evaluate("./METRIC[@NAME='zombie_process']/@VAL", hostNode);

		float f1 = Float.parseFloat(total);
		float f = f1/50.0f;
		if ( f > 0.90f) {
			stat = MonitorConstants.MONITOR_STAT_CRIT;
		}else if ( f > 0.70f ) {
			stat = MonitorConstants.MONITOR_STAT_WARN;
		}else {
			stat = MonitorConstants.MONITOR_STAT_OK;
		}
		info = "PROCS " 
				+ stat 
				+ " - " 
				+ total 
				+ " processes with STATE = Z";
		
		Service srv = srvMap.get(key);
		if (srv == null) {
			srv = new Service(name, stat, tm, info);
			srvMap.put(key, srv);
		}else {
			srv.setStat(stat);
			srv.setTime(tm);
			srv.setInfo(info);
		}
		
		return srv;
	}
	
	protected Service getSrv_HTTP(Map<String, Service> srvMap, Node hostNode) throws Exception {
		String key = MonitorConstants.MONITOR_HOST_HTTP;
		String name = key;
		String stat = MonitorConstants.MONITOR_STAT_UNKN;
		String tm = xpath.evaluate("./@REPORTED", hostNode);
		String info ;
		
		tm = MonitorUtil.getTimeFromInt(tm);
		String total = xpath.evaluate("./METRIC[@NAME='httpd']/@VAL", hostNode);

		float f1 = Float.parseFloat(total);
		if (f1 < 1) {
			stat = MonitorConstants.MONITOR_STAT_CRIT;
		}else {
			stat = MonitorConstants.MONITOR_STAT_OK;
		}
		info = "HTTP " 
				+ stat
				+ " - service is " 
				+ stat ;
		
		Service srv = srvMap.get(key);
		if (srv == null) {
			srv = new Service(name, stat, tm, info);
			srvMap.put(key, srv);
		}else {
			srv.setStat(stat);
			srv.setTime(tm);
			srv.setInfo(info);
		}
		
		return srv;
	}
	
	protected Service getSrv_Xend(Map<String, Service> srvMap, Node hostNode) throws Exception {
		String key = MonitorConstants.MONITOR_HOST_XEND;
		String name = key;
		String stat = MonitorConstants.MONITOR_STAT_UNKN;
		String tm = xpath.evaluate("./@REPORTED", hostNode);
		String info ;
		
		tm = MonitorUtil.getTimeFromInt(tm);
		String total = xpath.evaluate("./METRIC[@NAME='xend']/@VAL", hostNode);

		float f1 = Float.parseFloat(total);
		if (f1 < 1) {
			stat = MonitorConstants.MONITOR_STAT_CRIT;
		}else {
			stat = MonitorConstants.MONITOR_STAT_OK;
		}
		info = "XEND " 
				+ stat
				+ " - Xend service is " 
				+ stat ;
		
		Service srv = srvMap.get(key);
		if (srv == null) {
			srv = new Service(name, stat, tm, info);
			srvMap.put(key, srv);
		}else {
			srv.setStat(stat);
			srv.setTime(tm);
			srv.setInfo(info);
		}
		
		return srv;
	}
	
	protected Service getSrv_VECP(Map<String, Service> srvMap, Node hostNode) throws Exception {
		String key = MonitorConstants.MONITOR_HOST_VECP;
		String name = key;
		String stat = MonitorConstants.MONITOR_STAT_UNKN;
		String tm = xpath.evaluate("./@REPORTED", hostNode);
		String info ;
		
		tm = MonitorUtil.getTimeFromInt(tm);
		String total = xpath.evaluate("./METRIC[@NAME='lingcloud']/@VAL", hostNode);

		float f1 = Float.parseFloat(total);
		if (f1 < 1) {
			stat = MonitorConstants.MONITOR_STAT_CRIT;
		}else {
			stat = MonitorConstants.MONITOR_STAT_OK;
		}
		info = "LINGCLOUD " 
				+ stat
				+ " - LingCloud service is " 
				+ stat ;
		
		Service srv = srvMap.get(key);
		if (srv == null) {
			srv = new Service(name, stat, tm, info);
			srvMap.put(key, srv);
		}else {
			srv.setStat(stat);
			srv.setTime(tm);
			srv.setInfo(info);
		}
		
		return srv;
	}
	
	protected Service getSrv_Mysql(Map<String, Service> srvMap, Node hostNode) throws Exception {
		String key = MonitorConstants.MONITOR_HOST_MYSQL;
		String name = key;
		String stat = MonitorConstants.MONITOR_STAT_UNKN;
		String tm = xpath.evaluate("./@REPORTED", hostNode);
		String info ;
		
		tm = MonitorUtil.getTimeFromInt(tm);
		String total = xpath.evaluate("./METRIC[@NAME='mysql']/@VAL", hostNode);

		float f1 = Float.parseFloat(total);
		if (f1 < 1) {
			stat = MonitorConstants.MONITOR_STAT_CRIT;
		}else {
			stat = MonitorConstants.MONITOR_STAT_OK;
		}
		info = "MYSQL "
				+ stat
				+ " - MySQL service is " 
				+ stat ;
		
		Service srv = srvMap.get(key);
		if (srv == null) {
			srv = new Service(name, stat, tm, info);
			srvMap.put(key, srv);
		}else {
			srv.setStat(stat);
			srv.setTime(tm);
			srv.setInfo(info);
		}
		
		return srv;
	}
	
	protected String getVMState(String stat) {
		String ret = MonitorConstants.MONITOR_STAT_CRIT;
		if (ret == null) {
			return ret;
		}
		if (stat.contains("run") ||
				stat.contains("idle") ||
				stat.contains("block")) {
			ret = MonitorConstants.MONITOR_STAT_OK;
		}else if (stat.contains("paused")) {
			ret = MonitorConstants.MONITOR_STAT_WARN;
		}
		return ret;
	}
	
	protected int getVMInfos(Map<String,VM> vmMap, Node hostNode) throws Exception {
		VM vm = null;
		for (String key : vmMap.keySet()) {
			vm = vmMap.get(key);
			vm.setFlag(false);
		}
		String names = xpath.evaluate("./METRIC[@NAME='vm_name_infos']/@VAL", hostNode);
		String cpus = xpath.evaluate("./METRIC[@NAME='vm_cpu_infos']/@VAL", hostNode);
		String mems = xpath.evaluate("./METRIC[@NAME='vm_mem_infos']/@VAL", hostNode);
		String disks = xpath.evaluate("./METRIC[@NAME='vm_disk_infos']/@VAL", hostNode);
		String nets = xpath.evaluate("./METRIC[@NAME='vm_net_infos']/@VAL", hostNode);
		String[] bufs ;
		String sep = "\\|";
		
		log.error(names);
		if (null == names || "".equals(names)) {
			vmMap.clear();
			return 0;
		}
		
		/**
		 * VM name and state list
		 * eg:
		 *  one-x:state |
		 *  one-xx:state
		 */
		bufs = names.split(sep);
		String stat;
		for (int i = 0 ; i < bufs.length ; i++) {
			String[] tmp = bufs[i].split(":");
			if (tmp.length == 2){
				vm = vmMap.get(tmp[0]);
				stat = this.getVMState(tmp[1]);
				if (vm == null) {
					vm = new VM(tmp[0], stat);
					vmMap.put(tmp[0], vm);
				}else {
					vm.setState(stat);
				}
				vm.setRunStat(tmp[1]);
			}else {
				log.warn("GangliaBridge: VM name and stat format Error: " 
						+ bufs[i]);
			}
		}
		for (String key : vmMap.keySet()) {
			vm = vmMap.get(key);
			if (!vm.getFlag()) {
				vmMap.remove(key);
			}
		}
		
		/**
		 * VM CPU infos
		 * eg:
		 *  vmName:one-x;vcpu:2,VCPU: 0,CPU: 0,State:running,CPU time: 84018.3s,CPU Affinity: y------- |
		 *  vmName:one-xx;vcpu:2,VCPU: 0,CPU: 0,State:running,CPU time: 84018.3s,CPU Affinity: y-------
		 */
		VM.CPUInfo cpu;
		bufs = cpus.split(sep);
		for (int i = 0 ; i < bufs.length ; i++) {
			try {
				String[] tmp = bufs[i].split(";");
				vm = vmMap.get(tmp[0].split(":",2)[1]);
				tmp = tmp[1].split(",");
				cpu = vm.getCPUInfo();
				cpu.vcpu = Integer.parseInt(tmp[0].split(":",2)[1].trim());
				cpu.cpu = Integer.parseInt(tmp[2].split(":",2)[1].trim());
				cpu.cpuTime = tmp[4].split(":",2)[1].trim();
				cpu.stat = tmp[3].split(":",2)[1].trim();
			}catch(Exception e) {
				log.warn(e);
				log.warn(bufs[i]);
			}
		}
		
		/**
		 * VM memory infos
		 * eg:
		 *  vmName:one-x;memory:524288,currMem:524288 | 
		 *  vmName:one-xx;memory:524288,currMem:524288
		 */
		VM.MemInfo mem;
		bufs = mems.split(sep);
		for (int i = 0 ; i < bufs.length ; i++) {
			try {
				String[] tmp = bufs[i].split(";");
				vm = vmMap.get(tmp[0].split(":",2)[1]);
				tmp = tmp[1].split(",");
				mem = vm.getMemInfo();
				mem.memory = Double.parseDouble(tmp[0].split(":",2)[1].trim());
				mem.currMem = Double.parseDouble(tmp[1].split(":",2)[1].trim());
			}catch(Exception e) {
				log.warn(e);
				log.warn(bufs[i]);
			}
		}
		
		/**
		 * VM disk infos
		 * eg:
		 * vmName:one-x;
		 * ***ula/var//x/images/disk.0,qcow,10M(10485760bytes),4.0K |
		 * vmName:one-xx;
		 * ***ula/var//xx/images/disk.0,qcow,10M(10485760bytes),4.0K 
		 */
		VM.DiskInfo ds;
		VM.ImageInfo img;
		bufs = disks.split(sep);
		for (int i = 0 ; i < bufs.length ; i++) {
			try {
				String[] tmp = bufs[i].split(";");
				vm = vmMap.get(tmp[0].split(":",2)[1]);
				ds = vm.getDiskInfo();
				if (ds.imgList.size() != tmp.length - 1) {
					ds.imgList.clear();
					for (int j = 1 ; j < tmp.length ; j++)
						ds.imgList.add(new VM.ImageInfo());
				}
				for (int j = 1 ; j < tmp.length ; j++) {
					String[] t = tmp[j].split(",");
					img = ds.imgList.get(j-1);
					img.img = t[0].trim();
					img.fmt = t[1].trim();
					img.virSize = t[2].trim();
					img.size = t[3].trim();
					img.bak = "";
				}
			}catch(Exception e) {
				log.warn(e);
				log.warn(bufs[i]);
			}
		}
		
		/**
		 * VM network infos
		 * eg: dev,ip,mac,tx,rx
		 *  vmName:one-x;
		 *  vifx.0,192.168.0.1,01:00:00:01:00:01, 0,0;
		 *  vifx.1,10.0.0.10,ee:ee:ac:16:01:c9, 0,0 |
		 *  vmName:one-xx;
		 *  vifxx.0,192.168.0.2,00:01:00:03:00:02, 0,0
		 */
		VM.NetInfo ns;
		VM.VifInfo vif;
		bufs = nets.split(sep);
		for (int i = 0 ; i < bufs.length ; i++) {
			try {
				String[] tmp = bufs[i].split(";");
				vm = vmMap.get(tmp[0].split(":",2)[1]);
				ns = vm.getNetInfo();
				if (ns.vifList.size() != tmp.length - 1) {
					ns.vifList.clear();
					for (int j = 1 ; j < tmp.length ; j++)
						ns.vifList.add(new VM.VifInfo());
				}
				for (int j = 1 ; j < tmp.length ; j++) {
					String[] t = tmp[j].split(",");
					vif = ns.vifList.get(j-1);
					vif.dev = t[0].trim();
					vif.ip = t[1].trim();
					vif.mac = t[2].trim();
					vif.tx_bytes = t[3].trim();
					vif.rx_bytes = t[4].trim();
				}
			}catch(Exception e) {
				log.warn(e);
				log.warn(bufs[i]);
			}
		}
		
		return 0;
	}
	
	protected Service getSrv_VMList(Map<String, Service> srvMap, Node hostNode) throws Exception {
		String key = MonitorConstants.MONITOR_HOST_VMLIST;
		String name = key;
		String stat = MonitorConstants.MONITOR_STAT_UNKN;
		String tm = xpath.evaluate("./@REPORTED", hostNode);
		String info ;
		
		tm = MonitorUtil.getTimeFromInt(tm);
		String total = xpath.evaluate("./METRIC[@NAME='vm_num']/@VAL", hostNode);

		int f1 = Integer.parseInt(total);
		if (f1 > 20) {
			stat = MonitorConstants.MONITOR_STAT_CRIT;
		}else if (f1 > 12){
			stat = MonitorConstants.MONITOR_STAT_WARN;
		}else {
			stat = MonitorConstants.MONITOR_STAT_OK;
		}
		info = "VMLIST "
				+ stat
				+ " - VMs: " 
				+ f1 ;
		
		Service srv = srvMap.get(key);
		if (srv == null) {
			srv = new Service(name, stat, tm, info);
			srvMap.put(key, srv);
		}else {
			srv.setStat(stat);
			srv.setTime(tm);
			srv.setInfo(info);
		}
		
		return srv;
	}
	
	public Map<String, Host> getHostMap(Map<String, Host> hostMap) throws Exception {
		
		if (hostMap == null) {
			hostMap = new HashMap<String, Host>();
		}
		this.hostMap = hostMap;
		Document doc = getGangliaMonitorData();
		
		NodeList nl = doc.getElementsByTagName("HOST");
		if (nl == null) {
			return hostMap;
		}
		hostMap.clear();
		for (int i = 0 ; i < nl.getLength() ; i++) {
			Node hn = nl.item(i);
			NamedNodeMap attMap = hn.getAttributes();
			String name = attMap.getNamedItem("NAME").getNodeValue();
			String ip = attMap.getNamedItem("IP").getNodeValue();
			String tm = attMap.getNamedItem("REPORTED").getNodeValue();
			Host host = hostMap.get(ip);
			tm = MonitorUtil.getTimeFromInt(tm);
			if (host == null) {
				host = new Host(name, ip, tm);
				hostMap.put(ip, host);
			}else {
				host.setHostName(name);
				host.setHostIP(ip);
				host.setHostTime(tm);
			}
			try {
				getSrvFromNode(host.getSrvMap(), hn);
				getVMInfos(host.getVMMap(), hn);
			}catch (Exception e) {
				log.error(e);
			}
			
		}
		
		return hostMap;
	}
	
	@Override
	public Host updateHost(Host host) throws Exception {
		return host;
	}
	
	public String getSrvImgUri(String host, String srvName, long beg, long end) throws Exception {
		String imgName = "";
		String output;
		String cmd ;
		String imgFile;
		String timeRange;
		String opt;
		String subDir = host;
		try {
			if (this.hostMap != null) {
				subDir = this.hostMap.get(host).getHostName();
			}
			imgName = MonitorUtil.getImgNameByHostSrv(host, srvName, beg, end);
			imgFile = MonitorUtil.getImgDirLocation()
								+ "/"
								+ imgName;
			timeRange = getTimeRangeForRRDtool(beg, end);
			opt = getImgCreatorOptionForRRDtool(subDir, host, srvName);
			cmd = MonitorUtil.getRRDtoolCmdInCfgFile();
			cmd += " graph ";
			cmd += " " + imgFile;
			cmd += " " + timeRange;
			cmd += " " + opt;
			output = XMMUtil.runCommand(cmd);
			if (!output.matches("[0-9]*x[0-9]*\n")) {
				throw new Exception ("Create img for " 
						+ host + " : "
						+ srvName
						+ " cmd Error: " 
						+ cmd
						+ ". Output: "
						+ output) ;
			}
		}catch (Exception e) {
			log.error(e);
			log.error(imgName);
			imgName = "";
		}
		return imgName;
	}
	
	protected String getImgCreatorOptionForRRDtool(String subDir,String host, String srvName) throws Exception {
		String dsDir = MonitorUtil.getRrds4GangliaInCfgFile() 
							+ "/"
							+ MonitorConstants.ganglia_cluster
							+ "/" 
							+ subDir;
		String dsName;
		// title
		String opt = " -t '"
						+ host
						+ "--"
						+ srvName
						+ "'";
		// options
		if (MonitorConstants.MONITOR_HOST_CPU.equals(srvName)) {
			dsName = dsDir + "/load_one.rrd";
			opt += " DEF:line0=" 
					+ dsName
					+ ":sum:AVERAGE " 
					+ " AREA:line0#AFAF32:'CPU_Load-One_Minite' "
					+ " LINE1:line0#000000 ";
		}
		else if (MonitorConstants.MONITOR_HOST_MEMORY.equals(srvName)) {
			dsName = dsDir + "/mem_free.rrd";
			opt += " DEF:line0=" 
					+ dsName
					+ ":sum:AVERAGE "
					+ " AREA:line0#AFAF32:'Free_memory' "
					+ " LINE1:line0#000000 ";
		}
		else if (MonitorConstants.MONITOR_HOST_SWAP.equals(srvName)) {
			dsName = dsDir + "/swap_free.rrd";
			opt += " DEF:line0=" 
					+ dsName
					+ ":sum:AVERAGE "
					+ " AREA:line0#AFAF32:'Free_swap' "
					+ " LINE1:line0#000000 ";
		}
		else if (MonitorConstants.MONITOR_HOST_DISK.equals(srvName)) {
			dsName = dsDir + "/disk_free.rrd";
			opt += " DEF:line0=" 
					+ dsName
					+ ":sum:AVERAGE "
					+ " AREA:line0#AFAF32:'Free_disk' "
					+ " LINE1:line0#000000 ";
		}
		else if (MonitorConstants.MONITOR_HOST_NET.equals(srvName)) {
			dsName = dsDir + "/bytes_in.rrd";
			opt += " DEF:line0=" 
					+ dsName
					+ ":sum:AVERAGE "
					+ " AREA:line0#AFAF32 "
					+ " LINE1:line0#00FF00:'Bytes_in' ";
			
			dsName = dsDir + "/bytes_out.rrd";
			opt += " DEF:line1=" 
					+ dsName
					+ ":sum:AVERAGE "
					+ " AREA:line1#AFAF32 "
					+ " LINE1:line1#FF0000:'Bytes_out' ";
		}
		else if (MonitorConstants.MONITOR_HOST_USER.equals(srvName)) {
			dsName = dsDir + "/current_user.rrd";
			opt += " DEF:line0=" 
					+ dsName
					+ ":sum:AVERAGE "
					+ " AREA:line0#AFAF32:'Users_login' "
					+ " LINE1:line0#000000 ";
		}
		else if (MonitorConstants.MONITOR_HOST_TPROCESS.equals(srvName)) {
			dsName = dsDir + "/total_process.rrd";
			opt += " DEF:line0=" 
					+ dsName
					+ ":sum:AVERAGE "
					+ " AREA:line0#AFAF32:'Total_processes' "
					+ " LINE1:line0#000000 ";
		}
		else if (MonitorConstants.MONITOR_HOST_ZPROCESS.equals(srvName)) {
			dsName = dsDir + "/zombie_process.rrd";
			opt += " DEF:line0=" 
					+ dsName
					+ ":sum:AVERAGE "
					+ " AREA:line0#AFAF32:'Zombie_process' "
					+ " LINE1:line0#000000 ";
		}
		else if (MonitorConstants.MONITOR_HOST_HTTP.equals(srvName)) {
			dsName = dsDir + "/httpd.rrd";
			opt += " DEF:line0=" 
					+ dsName
					+ ":sum:AVERAGE "
					+ " AREA:line0#AFAF32:'HTTP_service' "
					+ " LINE1:line0#000000 ";
		}
//		else if (MonitorConstants.MONITOR_HOST_SSH.equals(srvName)) {
//
//		}
		else if (MonitorConstants.MONITOR_HOST_XEND.equals(srvName)) {
			dsName = dsDir + "/xend.rrd";
			opt += " DEF:line0=" 
					+ dsName
					+ ":sum:AVERAGE "
					+ " AREA:line0#AFAF32:'Xen' "
					+ " LINE1:line0#000000 ";
		}
		else if (MonitorConstants.MONITOR_HOST_VECP.equals(srvName)) {
			dsName = dsDir + "/lingcloud.rrd";
			opt += " DEF:line0=" 
					+ dsName
					+ ":sum:AVERAGE "
					+ " AREA:line0#AFAF32:'LingCloud' "
					+ " LINE1:line0#000000 ";
		}
		else if (MonitorConstants.MONITOR_HOST_MYSQL.equals(srvName)) {
			dsName = dsDir + "/mysql.rrd";
			opt += " DEF:line0=" 
					+ dsName
					+ ":sum:AVERAGE "
					+ " AREA:line0#AFAF32:'Mysql' "
					+ " LINE1:line0#000000 ";
		}
		else if (MonitorConstants.MONITOR_HOST_VMLIST.equals(srvName)) {
			dsName = dsDir + "/vm_num.rrd";
			opt += " DEF:line0=" 
					+ dsName
					+ ":sum:AVERAGE "
					+ " AREA:line0#AFAF32:'VM_number' "
					+ " LINE1:line0#000000 ";
		}
		else {
			throw new Exception("No RRDtool option defined for host:"
									+ host
									+ " srv:"
									+ srvName
									+ ". Opt: "
									+ opt);
		}
		
		return opt;
	}
	
	protected String getTimeRangeForRRDtool(long beg, long end) {
		String res ;
		if (beg == 0 
				|| beg >= end) {
			res = " --start "
					+ "-"
					+ end
					+ " ";
		}else {
			res = " --start "
					+ beg
					+ " --end "
					+ end
					+ " ";
		}
		return res;
	}
}
