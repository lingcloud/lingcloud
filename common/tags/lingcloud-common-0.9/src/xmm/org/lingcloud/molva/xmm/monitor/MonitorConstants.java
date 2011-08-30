/*
 *  @(#)MonitorConstants.java  2011-7-24
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

/**
 * <strong>Purpose:</strong><br>
 * 
 * @version 1.0.1 2011-7-24<br>
 * @author Liang Li<br>
 */
public interface MonitorConstants {
	
	String ganglia			= "ganglia" ;
	
	String ganglia_cluster = "lingcloud" ;
	
	String monitorCmdPrefix = "telnet" ;
	
	String MONITOR_CONF_PREFIX		= "CONF_MONITOR_PARTION_";
	String MONITOR_DEFAULT_ACL		= "rwx------";
	String MONITOR_PART_CONF_OBJECT	= "monitor partition conf object";
	String MONITOR_ITEMPERPAGE		= "itemPerPage";
	int MONITOR_ITEMPERPAGE_DEF		= 10;
	String MONITOR_PERIOD			= "period" ;
	long MONITOR_PERIOD_DEF			= 10 * 60 * 1000 ;
	
	/**
	 * Monitor items
	 */
	String MONITOR_HOST_CPU 		= "CPU_Load";
	String MONITOR_HOST_MEMORY 		= "Memory_Usage";
	String MONITOR_HOST_SWAP 		= "Swap_Usage";
	String MONITOR_HOST_DISK 		= "Disk_Usage";
	String MONITOR_HOST_NET 		= "Net_Traffic";
	//String MONITOR_HOST_PING 		= "PING";
	String MONITOR_HOST_USER 		= "Current_Users";
	String MONITOR_HOST_TPROCESS 	= "Total_Processes";
	String MONITOR_HOST_ZPROCESS 	= "Zombie_Processes";
	String MONITOR_HOST_HTTP 		= "HTTP";
	//String MONITOR_HOST_SSH 		= "SSH";
	String MONITOR_HOST_XEND 		= "Xend";
	String MONITOR_HOST_VECP 		= "VECP";
	String MONITOR_HOST_MYSQL 		= "Mysql";
	String MONITOR_HOST_VMLIST	 	= "VMList";
	
	String[] MONITOR_ITEM_LIST		= new String[] {
			MONITOR_HOST_CPU,		// 0
			MONITOR_HOST_MEMORY,	// 0
			MONITOR_HOST_SWAP,		// 0
			MONITOR_HOST_DISK,		// 0
			MONITOR_HOST_NET,		// 2
			//MONITOR_HOST_PING,	// 2
			MONITOR_HOST_USER,		// 4
			MONITOR_HOST_TPROCESS,	// 1
			MONITOR_HOST_ZPROCESS,	// 1
			MONITOR_HOST_HTTP,		// 3
			//MONITOR_HOST_SSH,		// 2
			MONITOR_HOST_XEND,		// 3
			MONITOR_HOST_VECP,		// 3
			MONITOR_HOST_MYSQL,		// 3
			MONITOR_HOST_VMLIST
	};
	
	int[] itemID = { 0, 0, 0, 0, 2, 4, 1, 1, 3, 3, 3, 3, 3};

	String[] monitorItemCtg = { 
							"Performace",
							"Process", 
							"Network", 
							"Applicance", 
							"Users"
							};
	
	String MONITOR_VM_VMCPU 		= "VM_Cpu";
	String MONITOR_VM_VMDISK 		= "VM_Disk";
	String MONITOR_VM_VMNET 		= "VM_Net";
	
	String MONITOR_STAT_OK			= "OK";
	String MONITOR_STAT_WARN		= "WARNING";
	String MONITOR_STAT_UNKN		= "UNKNOWN";
	String MONITOR_STAT_CRIT		= "CRITICAL";
	
	String MONITOR_INFO_UNKN		= "Can NOT get information from monitor server!";
	
	int MONITOR_STAT_OK_INT			= 0;
	int MONITOR_STAT_WARN_INT		= 1;
	int MONITOR_STAT_CRIT_INT		= 2;
	int MONITOR_STAT_UNKN_INT		= 3;
	
	/*
	 * 0-1.0
	 */
	double MONITOR_CRIT_VAL			= 0.95 ;
	double MONITOR_WARN_VAL			= 0.70 ;
	double MONITOR_OK_VAL			= 0.00 ;
	
	String MONITOR_PIC_CRIT			= "server-red.png";
	String MONITOR_PIC_OK			= "server-green.png";
	String MONITOR_PIC_WARN			= "server-yellow.png";
	
	String MONITOR_IMG_PERIOD_MINS	= "mins";
	long MONITOR_IMG_PERIOD_MINS_S	= 60 * 60;
	String MONITOR_IMG_PERIOD_HOURS	= "hours";
	long MONITOR_IMG_PERIOD_HOURS_S	= 60 * 60 * 24;
	String MONITOR_IMG_PERIOD_DAYS	= "days";
	long MONITOR_IMG_PERIOD_DAYS_S	= 60 * 60 * 24 * 30;
	String MONITOR_IMG_PERIOD_MONTHS	= "months";
	long MONITOR_IMG_PERIOD_MONTHS_S	= 60 * 60 * 24 * 30 * 12;
	
}
