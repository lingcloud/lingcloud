/*
 *  @(#)XMMConstants.java  2010-5-27
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

package org.lingcloud.molva.xmm.util;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2009-9-17<br>
 * @author Xiaoyi Lu<br>
 * @email luxiaoyi@software.ict.ac.cn
 */
public class XMMConstants {
	
	/**
	 * lingcloud xmm home path.
	 */
	public static final String LINGCLOUD_HOME_PROPERTY = "lingcloud.home";

	/**
	 * config file name.
	 */
	public static final String CONFIG_FILE_NAME = "molva.conf";
	
	/**
	 *  URL item name for molva's web service, in conf file.
	 */
	public static final String  CONFIG_ITEM_SERVICEURL = "molvaWebServiceUrl";
	
	/**
	 * config dir name.
	 */
	public static final String CONFIG_DIR = "conf";

	/**
	 * The session name of OpenNebula.
	 */
	public static final String SESSION = "molva";

	public static final String ALL_NODE_SAME_REQUIREMENT_TAG = "*";

	public static final String HYPERVISOR_XEN = "xen";

	public static final String HYPERVISOR_KVM = "kvm";

	public static final String HYPERVISOR_VMWARE = "vmware";

	public static final String HYPERVISOR_EC2 = "ec2";

	public static final String FILE_TRANSFER_NFS = "nfs";

	public static final String FILE_TRANSFER_SSH = "ssh";

	public static final String IP_SECTION = "192.168.0.0-192.168.255.255";

	public static final String IP_PREFIX = "192.168.";

	public static final String NETWORK_SIZE = "NetworkSize";

	public static final String HEAD_NODE_IP = "HeadNodeIP";

	public static final String NETWORK_TYPE = "NETWORK_TYPE";

	public static final String BRIDGE = "BRIDGE";

	public static final String CLUSTER_ID = "CLUSTER_ID";

	public static final String NETWORK_TYPE_FIXED = "FIXED";

	public static final String DEFAULT_BRIDGE = "xenbr0";

	public static final String PRIVATE_IP_NICS = "PRIVATE_IP_NICS";

	public static final String PUBLIC_IP_NICS = "PUBLIC_IP_NICS";

	public static final String VIRTUAL_GW = "VirtualGateWay";

	public static final String CREATE_VIRTUAL_GW = "CREATE_VIRTUAL_GW";

	public static final String VA_BOOTHVMLOADER = "hvm";

	public static final String VA_BOOTPYGRUBLOADER = "pygrub";

	public static final String VA_APP = "app";

	public static final String VA_OS = "os";

	public static final String VA_LOCATION = "VA_LOCATION";

	public static final String VA_TYPE = "VA_TYPE";

	public static final String VIRTUAL_NETWORK = "VIRTUAL_NETWORK";

	public static final String VIRTUAL_APPLIANCE_OS = "VIRTUAL_APPLIANCE_OS";

	public static final String VIRTUAL_APPLIANCE_APP = "VIRTUAL_APPLIANCE_APP";

	public static final String DEFAULT_CPU_QUOTA = "1";

	public static final int DEFAULT_CPU_NUM = 1;

	public static final int DEFAULT_MEM_SIZE_MASTER = 1024;

	public static final int DEFAULT_MEM_SIZE_SLAVE = 1024;

	public static final String VIRTUAL_MACHINE = "VIRTUAL_MACHINE";

	public static final String VIRTUAL_CLUSTER_STATE = "VIRTUAL_CLUSTER_STATE";

	public static final String VA_SIZE = "VA_SIZE";

	public static final String VA_BOOTLOADER = "VA_BOOTLOADER";

	public static final String PUBLIC_IP = "PUBLIC_IP";

	/**
	 * Fixme this mac prefix can not be modified, due to many reasons. hehe!
	 */
	public static final String MAC_PREFIX = "ee:ee";

	public static final String QUOTA_CPU = "QUOTA_CPU";

	public static final String QUOTA_MEM = "QUOTA_MEM";

	public static final String QUOTA_DISK = "QUOTA_DISK";

	public static final String QUOTA_NETTRAFFIC = "QUOTA_NETTRAFFIC";

	public static final String PREFER_CPU = "PREFER_CPU";

	public static final String PREFER_MEM = "PREFER_MEM";

	public static final String PREFER_DISK = "PREFER_DISK";

	public static final String PREFER_NETTRAFFIC = "PREFER_NETTRAFFIC";

	public static final String USAGELEFT_CPU = "USAGELEFT_CPU";

	public static final String USAGELEFT_DISK = "USAGELEFT_DISK";

	public static final String USAGELEFT_MEM = "USAGELEFT_MEM";

	public static final String USAGELEFT_NETTRAFFIC = "USAGELEFT_NETTRAFFIC";

	public static final String PARTITION_TYPE = "PARTITION_TYPE";

	public static final String PHYSICAL_NODE_TYPE = "PHYSICAL_NODE_TYPE";

	public static final String VIRTUAL_NODE_TYPE = "VIRTUAL_NODE_TYPE";

	public static final String VIRTUAL_NETWORK_TYPE = "VIRTUAL_NETWORK_TYPE";

	public static final String HYPERVISOR = "HYPERVISOR";

	public static final String HOSTINFO = "HOSTINFO";

	public static final String VMINFO = "VMINFO";

	public static final int MAC_LENGTH = 6;

	public static final String VIRTUAL_CLUSTER_TYPE = "VIRTUAL_CLUSTER_TYPE";

	public static final String DEFAULT_ACL = "rwx------";

	public static enum MachineRunningState {
		WAIT_DEPLOY, BOOT, RUNNING, SUSPENDED, STOPPING, STOP, SHUTDOWN, ERROR
	}
}
