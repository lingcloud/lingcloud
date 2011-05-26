/*
 *  @(#)VAMConfig.java  2010-5-26
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

package org.lingcloud.molva.xmm.vam.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import org.lingcloud.molva.ocl.util.ConfigUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-26<br>
 * @author Ruijian Wang<br>
 * 
 */
public class VAMConfig {
	private static Properties properties;

	public static final String CONF_FILE = "molva.conf";

	/**
	 * get the Virtual Appliance Manager configuration.
	 * 
	 * @throws Exception
	 */
	private static void getConfig() throws Exception {
		String confPath = ConfigUtil.getConfigFile(CONF_FILE).toString();
		// String confPath = System.getProperty(CONF_FILE);
		if (confPath == null) {
			throw new Exception("Can't find the config file '" + CONF_FILE
					+ "'.");
		}

		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(confPath));
		} catch (FileNotFoundException e) {
			throw new Exception("Can't find the vam config.");
		}
		properties.load(in);

	}

	private VAMConfig() {
	}

	static {
		properties = new Properties();

		try {
			getConfig();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * add '/' after the directory path.
	 * 
	 * @param dir
	 *            directory path
	 * @return the format directory path
	 */
	private static String formatDir(String dir) {
		if (dir != null) {
			if (dir.equals("")) {
				dir = "/";
			} else if (!dir.endsWith("/")) {
				dir += "/";
			}
		}
		return dir;
	}

	/**
	 * get the store file directory path.
	 * 
	 * @return the path
	 */
	public static String getFileDirLocation() {
		return formatDir((String) properties.get("fileDirLocation"));
	}
	
	/**
	 * get the file upload directory path on none NFS host.
	 * @return the path
	 */
	public static String getUploadDirLocation() {
		return formatDir((String) properties.get("uploadDirLocation"));
	}
	
	/**
	 * get the disk upload directory path.
	 * @return the path
	 */
	public static String getDiskUploadDirLocation() {
		String uploadDir = getUploadDirLocation();
		if (uploadDir != null) {
			uploadDir += "/" + VAMConstants.VAF_FILE_TYPE_DISK;
		} 
		return formatDir(uploadDir);
	}
	
	/**
	 * get the disc upload directory path.
	 * @return the path
	 */
	public static String getDiscUploadDirLocation() {
		String uploadDir = getUploadDirLocation();
		if (uploadDir != null) {
			uploadDir += "/" + VAMConstants.VAF_FILE_TYPE_DISC;
		} 
		return formatDir(uploadDir);
	}
	
	/**
	 * get the application upload directory path on none NFS host.
	 * @return the path
	 */
	public static String getAppUploadDirLocation() {
		String uploadDir = getUploadDirLocation();
		if (uploadDir != null) {
			uploadDir += "/" + VAMConstants.VAF_FILE_TYPE_APP;
		} 
		return formatDir(uploadDir);
	}

	/**
	 * get the temporary directory path on none NFS host.
	 * 
	 * @return the path
	 */
	public static String getTempDirLocation() {
		return formatDir((String) properties.get("tempDirLocation"));
	}

	/**
	 * get the NFS host name or IP.
	 * 
	 * @return host name or IP
	 */
	public static String getNfsHost() {
		return (String) properties.get("nfsHost");
	}

	/**
	 * get the NFS host user name.
	 * 
	 * @return user name
	 */
	public static String getNfsUser() {
		return (String) properties.get("nfsUser");
	}

	/**
	 * get the log file path.
	 * 
	 * @return log file path
	 */
	public static String getLogLocation() {
		return (String) properties.get("logLocation");
	}

	/**
	 * get the max support number of snapshot.
	 * 
	 * @return max number
	 */
	public static int getSupportSnapshotNum() {
		return Integer.parseInt((String) properties.get("supportSnapshotNum"));
	}

	/**
	 * get the max number of redundant instance.
	 * 
	 * @return max number
	 */
	public static int getMaxRedundantInstance() {
		return Integer
				.parseInt((String) properties.get("maxRedundantInstance"));
	}

	/**
	 * get the minimum number of redundant instance.
	 * 
	 * @return minimum number
	 */
	public static int getMinRedundantInstance() {
		return Integer
				.parseInt((String) properties.get("minRedundantInstance"));
	}

	/**
	 * get the FTP host name or IP.
	 * 
	 * @return host name or IP
	 */
	public static String getFtpHost() {
		return (String) properties.get("ftpHost");
	}

	/**
	 * get the FTP user name.
	 * 
	 * @return user name
	 */
	public static String getFtpUser() {
		return (String) properties.get("ftpUser");
	}

	/**
	 * get the FTP password.
	 * 
	 * @return password
	 */
	public static String getFtpPassword() {
		return (String) properties.get("ftpPassword");
	}

	/**
	 * get the making appliance host user name.
	 * 
	 * @return user name
	 */
	public static String getMakeApplianceUser() {
		return (String) properties.get("makeApplianceUser");
	}

	/**
	 * get the making appliance host name or IP.
	 * 
	 * @return host name or IP
	 */
	public static String getMakeApplianceHost() {
		return (String) properties.get("makeApplianceHost");
	}

	/**
	 * get the number of making appliance host.
	 * 
	 * @return the number of host
	 */
	public static int getMakeApplianceVMCount() {
		return Integer
				.parseInt((String) properties.get("makeApplianceVMCount"));
	}

	/**
	 * get the specific virtual machine's MAC address on making appliance host.
	 * 
	 * @param index
	 *            the virtual machine index
	 * @return MAC address list
	 */
	public static String[] getMakeApplianceVMMacAddr(int index) {
		String[] macAddr = null;
		String strVm = (String) properties.get("makeApplianceVM" + index);
		if (strVm != null) {
			String[] args = strVm.split(",");
			if (args.length == VAMConstants.MAKE_APLIANCE_VM_ARG_COUNT) {
				macAddr = args[1].split("\\|");
			}
		}
		return macAddr;
	}

	/**
	 * get the specific virtual machine's bridge network interface on making
	 * appliance host.
	 * 
	 * @param index
	 * @return bridge network interface
	 */
	public static String getMakeApplianceVMBridge(int index) {
		String bridge = null;
		String strVm = (String) properties.get("makeApplianceVM" + index);
		if (strVm != null) {
			String[] args = strVm.split(",");
			if (args.length == VAMConstants.MAKE_APLIANCE_VM_ARG_COUNT) {
				bridge = args[2];
			}
		}
		return bridge;
	}

	/**
	 * get the specific virtual machine's VNC port on making appliance host.
	 * 
	 * @param index
	 *            the virtual machine index
	 * @return VNC port
	 */
	public static int getMakeApplianceVMVncPort(int index) {
		int vncPort = 0;
		String strVm = (String) properties.get("makeApplianceVM" + index);
		if (strVm != null) {
			String[] args = strVm.split(",");
			if (args.length == VAMConstants.MAKE_APLIANCE_VM_ARG_COUNT) {
				try {
					final int three = 3;
					vncPort = Integer.parseInt(args[three]);
				} catch (Exception e) {
					vncPort = 0;
				}

			}
		}
		return vncPort;
	}

	/**
	 * get the specific virtual machine's name on making appliance host.
	 * 
	 * @param index
	 *            the virtual machine index
	 * @return name
	 */
	public static String getMakeApplianceVMName(int index) {
		String name = null;
		String strVm = (String) properties.get("makeApplianceVM" + index);
		if (strVm != null) {
			String[] args = strVm.split(",");
			if (args.length == VAMConstants.MAKE_APLIANCE_VM_ARG_COUNT) {
				name = args[0];
			}
		}
		return name;
	}

	public static String getLingCloudAgent() {
		return (String) properties.get("LingCloudAgent");
	}
}
