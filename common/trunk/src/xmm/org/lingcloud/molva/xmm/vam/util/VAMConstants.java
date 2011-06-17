/*
 *  @(#)VAMConstants.java  2010-5-26
 *
 *  Copyright (C) 2008-2011,
 *  Vega LingCloud Team,
 *  Institute of Computing Technology,
 *  Chinese Academy of Sciences.
 *  P.O.Box 2704, 100190, Beijing, China.
 *  
 *  http://lingcloud.org
 *  
 */

package org.lingcloud.molva.xmm.vam.util;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-26<br>
 * @author Ruijian Wang<br>
 * 
 */
public class VAMConstants {
	public static final String VIRTUAL_APPLIANCE_OBJECT
		= "virtual appliance object";

	public static final String VIRTUAL_APPLIANCE_FILE 
		= "virtual appliance file";

	public static final String VIRTUAL_APPLIANCE 
		= "virtual appliance";

	public static final String VIRTUAL_APPLIANCE_CATEGORY 
	= "virtual appliance category";
	
	public static final String VAO_STATE = "state";
	
	public static final String VAO_SRC_PATH = "srcPath";
	
	public static final String VAO_TIMESTAMP = "timestamp";
	
	public static final String VAO_OPERATION_TYPE = "operationType";
	
	public static final String VAO_DELETE_FILE = "deleteFile";
	
	public static final int VAO_OPERATION_TYPE_CONVERT_FORMAT = 1;
	
	public static final int VAO_OPERATION_TYPE_CREATE_DISK = 2;
	
	public static final int VAO_OPERATION_TYPE_CREATE_SNAPSHOT = 3;
	
	public static final int VAO_OPERATION_TYPE_COPY_FILE = 4;
	
	public static final int VAO_OPERATION_TYPE_MOVE_FILE = 5;
	
	public static final int VAO_OPERATION_TYPE_DELETE_FILE = 6;

	public static final String VAF_ID = "id";

	public static final String VAF_LOCATION = "href";

	public static final String VAF_STATE = "state";

	public static final String VAF_SIZE = "size";
	
	public static final String VAF_FILE_TYPE = "type";
	
	public static final String VAF_FILE_TYPE_DISC = "disc";
	
	public static final String VAF_FILE_TYPE_DISK = "disk";
	
	public static final String VAF_FILE_TYPE_APP = "app";
	
	public static final String VAF_FILE_TYPE_CONFIG = "config";
	
	public static final String VAF_FILE_TYPE_OTHER = "other";
	
	public static final String VAF_FORMAT = "format";
	
	public static final String VAF_FORMAT_ISO = "iso";
	
	public static final String VAF_FORMAT_RAW = "raw";
	
	public static final String VAF_FORMAT_QCOW = "qcow";
	
	public static final String VAF_FORMAT_VMDK = "vmdk";
	
	public static final String VAF_FORMAT_DEFAULT = "qcow";
	
	public static final String VAF_FORMAT_TEXT = "text";
	
	public static final String VAF_PARENT = "parent";
	
	public static final String VAF_REF = "ref";
	
	public static final String VAD_CAPACITY = "capacity";
	
	public static final String VAD_OS = "os";
	
	public static final String VAD_APPLICATIONS = "applications";
	
	public static final String VAD_DISK_TYPE = "diskType";
	
	public static final String VAD_DISK_TYPE_OS = "Operating System";
	
	public static final String VAD_DISK_TYPE_APP = "Application";
	
	public static final String VAD_DISK_TYPE_UNKNOWN = "Unknown";

	public static final String VA_NAME = "name";

	public static final String VA_DISKS = "disks";
	
	public static final String VA_DISCS = "discs";
	
	public static final String VA_CAPACITY = "capacity";
	
	public static final String VA_MEMORY = "memory";
	
	public static final String VA_BOOTLOAD = "bootLoader";
	
	public static final String VA_BOOTLOAD_HVM = "hvm";
	
	public static final String VA_BOOTLOAD_PYGRUB = "pygrub";
	
	public static final String VA_PARENT = "parent";
	
	public static final String VA_SIZE = "size";
	
	public static final String VA_CONFIG = "config";

	public static final String VA_CATEGORY = "category";
	
	public static final String VA_REF = "ref";
	
	public static final String VA_LOGIN_STYLE = "loginStyle";
	
	public static final String VA_LOGIN_STYLE_GLOBAL_USER = "Global User";
	
	public static final String VA_LOGIN_STYLE_USER_PASS = "User and Password";
	
	public static final String VA_USERNAME = "username";
	
	public static final String VA_PASSWORD = "password";
	
	public static final String VA_CPU_AMOUNT = "cpuAmount";
	
	public static final String VA_FORMAT = "format";
	
	public static final String VA_OS = "os";
	
	public static final String VA_OS_WINDOWS = "Windows";
	
	public static final String VA_OS_WINDOWS_SERVER
										= "Windows Server";
	
	public static final String VA_OS_CENTOS = "CentOS";
	
	public static final String VA_OS_SOLARIS = "Solaris";
	
	public static final String VA_OS_UBUNTU = "Ubuntu";
	
	public static final String VA_OS_RED_HAT = "Red Hat";
	
	public static final String VA_OS_SUSE_LINUX = "SUSE Linux";
	
	public static final String VA_OS_LINUX = "Linux";
	
	public static final String VA_OS_FREEBSD = "BSD";
	
	public static final String VA_OS_UNIX = "UNIX";
	
	public static final String VA_OS_DOS = "DOS";
	
	public static final String VA_OS_OTHER = "Other";
	
	public static final String[] VA_OS_LIST = new String[] { VA_OS_LINUX,
			VA_OS_FREEBSD, VA_OS_SOLARIS, VA_OS_WINDOWS, VA_OS_DOS, 
			VA_OS_OTHER };
	
	public static final String VA_ACCESS_WAY = "accessWay";
	
	public static final String VA_ACCESS_WAY_SSH = "SSH";
	
	public static final String VA_ACCESS_WAY_VNC = "VNC";

	public static final String VA_ACCESS_WAY_RDP = "RDP";
	
	public static final String VA_APPLICATIONS = "applications";
	
	public static final String VA_DESCRIPTION = "description";
	
	public static final String VA_LANGUAGES = "languages";
	
	public static final String VA_VNC_PORT = "vncPort";
	
	public static final String VA_VM_NAME = "vmName";
	
	public static final String VA_SELECT_VM = "selectVm";
	
	public static final String VAC_CATEGORY = "category";

	public static final int INCREASE = 1;

	public static final int DECREASE = 2;

	public static final int STATE_UNDEFINE = 0;

	public static final int STATE_MOVING = 1;

	public static final int STATE_COPING = 2;

	public static final int STATE_READY = 4;

	public static final int STATE_DELETING = 8;

	public static final int STATE_DELETED = 16;

	public static final int STATE_CONVERTING = 32;
	
	public static final int STATE_PREPARING = 64;

	public static final int STATE_ERROR = 128;

	public static final int STATE_MAKING = 256;
	
	public static final int TASK_TYPE_CREATE = 1;
	
	public static final int TASK_TYPE_UPDATE = 2;
	
	public static final int TASK_TYPE_COPY = 3;
	
	public static final int TASK_TYPE_CONVERT = 4;
	
	public static final int TASK_TYPE_REMOVE = 5;
	
	public static final int TASK_TYPE_MOVE = 6;
	
	public static final int THREAD_MAX = 20;
	
	public static final int KB = 1024;
	
	public static final int MB = 1048576;
	
	public static final int GB = 1073741824;
	
	public static final int INTERVAL = 50;
	
	public static final int RECONNECTION_TIMES = 5;
	
	public static final int MAX_BUSY_THREAD = 2;
	
	public static final int MAX_LIGHT_THREAD = 5;
	
	public static final int MAX_IDLE_THREAD = 1;
	
	public static final int THREAD_TYPE_BUSY = 1;
	
	public static final int THREAD_TYPE_LIGHT = 2;
	
	public static final int THREAD_TYPE_IDLE = 3;
	
	public static final int TYPE_EQUAL = 1;
	
	public static final int TYPE_SMALLER = 2;
	
	public static final int LOGIN_STYLE_GLOBAL_USER = 0;
	
	public static final int LOGIN_STYLE_USER_PASS = 1;
	
	public static final int HOUR = 3600000;
	
	public static final int MINUTE = 60000;
	
	public static final int SECOND = 1000;
	
	public static final int OPERATION_INTERVAL = 5 * MINUTE;
	
	public static final String NULL = "null";
	
	public static final String CONFIG_APPLIANCE = "VirtualAppliance";
	
	public static final String CONFIG_APPLIANCE_NAME = "Name";
	
	public static final String CONFIG_APPLIANCE_DESCRIPTION = "Description";
	
	public static final String CONFIG_APPLIANCE_MAKE_APPLIANCE_VM 
														= "MakeApplianceVM";
	
	public static final String CONFIG_APPLIANCE_HARDWARE = "Hardware";
	
	public static final String CONFIG_APPLIANCE_HARDWARE_DISK = "Disk";
	
	public static final String CONFIG_APPLIANCE_HARDWARE_DISK_NAME = "Name";
	
	public static final String CONFIG_APPLIANCE_HARDWARE_DISK_CAPACITY 
																= "Capacity";

	public static final String CONFIG_APPLIANCE_HARDWARE_DISK_LOCATION 
																= "Location";
	
	public static final String CONFIG_APPLIANCE_HARDWARE_DISK_FORMAT = "Format";
	
	public static final String CONFIG_APPLIANCE_HARDWARE_DISC = "Disc";
	
	public static final String CONFIG_APPLIANCE_HARDWARE_CPU = "CPU";
	
	public static final String CONFIG_APPLIANCE_HARDWARE_CPU_NUMBER = "Number";
	
	public static final String CONFIG_APPLIANCE_HARDWARE_MEMERY = "Memery";
	
	public static final String CONFIG_APPLIANCE_HARDWARE_MEMERY_SIZE = "Size";
	
	public static final String CONFIG_APPLIANCE_HARDWARE_NETWORK = "Network";
	
	public static final String CONFIG_APPLIANCE_HARDWARE_NETWORK_MACADDR
																= "MacAddress";
	
	public static final String CONFIG_APPLIANCE_HARDWARE_NETWORK_BRIDGE
																= "Bridge";
	
	public static final String CONFIG_APPLIANCE_HARDWARE_BOOT = "Boot";
	
	public static final String CONFIG_APPLIANCE_SOFTWARE = "Software";
	
	public static final String CONFIG_APPLIANCE_SOFTWARE_OS = "OperatingSystem";
	
	public static final String CONFIG_APPLIANCE_SOFTWARE_OS_NAME = "Name";
	
	public static final String CONFIG_APPLIANCE_SOFTWARE_APP = "Application";
	
	public static final String CONFIG_APPLIANCE_SOFTWARE_APP_NAME = "Name";
	
	public static final String CONFIG_APPLIANCE_SOFTWARE_BOOTLOADER 
																= "BootLoader";
	
	public static final String CONFIG_APPLIANCE_SOFTWARE_ACCESSWAY 
																= "AccessWay";
	
	public static final String CONFIG_APPLIANCE_SOFTWARE_LANGUAGE = "Language";
	
	public static final String CONFIG_APPLIANCE_SOFTWARE_LANGUAGE_NAME = "Name";
	
	public static final String CONFIG_APPLIANCE_SOFTWARE_VNCPORT = "VncPort";
	
	public static final String MAKE_APLIANCE_VM = "makeApplianceVM";
	
	public static final int MAKE_APLIANCE_VM_ARG_COUNT = 4;
	
	public static final String MAKE_APPLIANCE_VM_STATE = "state";
	
	public static final int MAKE_APPLIANCE_VM_STATE_BUSY = 1;
	
	public static final int MAKE_APPLIANCE_VM_STATE_IDLE = 2;
	
	public static final String MAKE_APPLIANCE_VM_STATE_RUNNING = "running";
	
	public static final String MAKE_APPLIANCE_VM_STATE_STOP = "stop";
	
	public static final int NO_MAKE_APPLIANCE_VM = 0;
	
	public static final int APPLIANCE_OPERATION_START = 1;
	
	public static final int APPLIANCE_OPERATION_STOP = 2;
	
	public static final int APPLIANCE_OPERATION_LIST = 4;
	
	public static final int APPLIANCE_OPERATION_SAVE = 8;
	
	public static final int FILE_CLEAR_FLAG_ERROR = 1;
	
	public static final int FILE_CLEAR_FLAG_NOT_READY = 2;
	
	public static final int FILE_CLEAR_FLAG_NOT_FIND_DATA = 4;
	
	public static final int FILE_CLEAR_FLAG_NOT_FIND_FILE = 8;
	
	public static final int FILE_CLEAR_FLAG_NOT_FIND_PARENT = 16;
	
	public static final int BOOT_CDROM = 1;
	
	public static final int BOOT_HARDDISK = 2;
	
	public static final String LINGCLOUD_AGENT = "LingcloudAgent";
	
	public static final int DISK_MOUNT = 1;
	
	public static final int DISK_UMOUNT = 2;
	
	public static final String DEFAULT_ACL = "rwx------";
	
	private VAMConstants() {
		
	}
}
