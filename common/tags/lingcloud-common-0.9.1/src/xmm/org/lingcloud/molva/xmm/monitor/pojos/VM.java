/*
 *  @(#)VM.java  2011-7-24
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

/**
 * <strong>Purpose:</strong><br>
 * 
 * @version 1.0.1 2011-7-24<br>
 * @author Liang Li<br>
 */
public class VM {
	private String vmName;
	private String vmRunningState;
	private String vmTime;
	private String vmStat;
	private boolean flag = true;
	
	public static class CPUInfo {
		public int vcpu = 0;
		public int cpu = 0;
		public String cpuTime = "";
		public String stat = "";
	}
	
	public static class MemInfo {
		public double memory = 0;
		public double currMem = 0;
	}
	
	public static class ImageInfo {
		public String img = "";
		public String fmt = "";
		public String virSize = "";
		public String size = "";
		public String bak = "";
	}
	
	public static class DiskInfo {
		public List<ImageInfo> imgList = new ArrayList<ImageInfo>();
	}
	
	public static class VifInfo {
		public String dev = "";
		public String ip = "";
		public String mac = "";
		
		public String tx_bytes = "";
		public String rx_bytes = "";
	}
	
	public static class NetInfo {
		public List<VifInfo> vifList = new ArrayList<VifInfo>();
	}
	
	private CPUInfo vmCpu = new CPUInfo();
	private MemInfo vmMem = new MemInfo();
	private DiskInfo vmDisk = new DiskInfo();
	private NetInfo vmNet = new NetInfo();
	
	public CPUInfo getCPUInfo() {
		return vmCpu;
	}
	
	public void setCPUInfo(CPUInfo cpu) {
		vmCpu = cpu;
	}
	
	public MemInfo getMemInfo() {
		return vmMem;
	}
	
	public void setMemInfo(MemInfo mem) {
		vmMem = mem;
	}
	
	public DiskInfo getDiskInfo() {
		return vmDisk;
	}
	
	public void setDiskInfo(DiskInfo disk) {
		vmDisk = disk;
	}
	
	public NetInfo getNetInfo() {
		return vmNet;
	}
	
	public void setNetInfo(NetInfo net) {
		vmNet = net;
	}
	
	public VM(String name, String stat) {
		vmName = name;
		vmStat = stat;
	}
	
	public void setFlag(boolean t) {
		flag = t;
	}
	
	public boolean getFlag() {
		return flag;
	}
	
	public void setState(String stat) {
		this.vmStat = stat;
	}
	
	public String getStat() {
		return vmStat;
	}
	
	public void setRunStat(String running) {
		vmRunningState = running;
	}
	
	public String getRunStat() {
		return vmRunningState;
	}
	
	@Override
	public String toString() {
		if (vmName == null 
				|| vmTime == null
				|| vmStat == null) {
			return "";
		}
		return "{"
				+ "\tvmName:'" + vmName + "',\n"
				+ "\tvmTime:'" + vmTime + "',\n"
				+ "\tvmStat:'" + vmStat + "',\n" 
				+ "\tcpu:" + vmCpu
				+ "\tmem:" + vmMem
				+ "\tdisk:" + vmDisk
				+ "\tnet" + vmNet
				+ "}";
	}
}
