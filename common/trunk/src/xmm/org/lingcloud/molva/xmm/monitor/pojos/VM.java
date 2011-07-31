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

/**
 * <strong>Purpose:</strong><br>
 * 
 * @version 1.0.1 2011-7-24<br>
 * @author Liang Li<br>
 */
public class VM {
	private String vmName;
	private String vmIP;
	private String vmTime;
	private String vmStat;
	
	public class CPU {
		
	}
	
	public class Mem {
		
	}
	
	public class Disk {
		
	}
	
	public class Net {
		
	}
	
	private CPU vmCpu;
	private Mem vmMem;
	private Disk vmDisk;
	private Net vmNet;
	
	public VM(String name, String ip, String tm, String stat) {
		vmName = name;
		vmIP = ip;
		vmTime = tm;
		vmStat = stat;
	}
	
	@Override
	public String toString() {
		if (vmName == null 
				|| vmIP == null
				|| vmTime == null
				|| vmStat == null) {
			return "";
		}
		return "{"
				+ "\tvmName:'" + vmName + "',\n"
				+ "\tvmIP:'" + vmIP + "',\n"
				+ "\tvmTime:'" + vmTime + "',\n"
				+ "\tvmStat:'" + vmStat + "',\n" 
				+ "\tcpu:" + vmCpu
				+ "\tmem:" + vmMem
				+ "\tdisk:" + vmDisk
				+ "\tnet" + vmNet
				+ "}";
	}
}
