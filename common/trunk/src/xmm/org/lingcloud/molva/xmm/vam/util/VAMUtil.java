/*
 *  @(#)VAMUtil.java  2010-5-27
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.ocl.util.HashFunction;
import org.lingcloud.molva.xmm.vam.daos.DaoFactory;
import org.lingcloud.molva.xmm.vam.daos.VirtualApplianceDao;
import org.lingcloud.molva.xmm.vam.pojos.VAFile;
import org.lingcloud.molva.xmm.vam.pojos.VADisk.DiskInfo;
import org.lingcloud.molva.xmm.vam.pojos.VMState;
import org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager;
import org.lingcloud.molva.xmm.vam.services.VirtualApplianceManagerImpl;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-27<br>
 * @author Ruijian Wang<br>
 * 
 */
public class VAMUtil {
	private static VAMUtil instance = new VAMUtil();

	private static VirtualApplianceManager vam = null;
	
	/**
	 * The logger for this class.
	 */
	private static Log logger = LogFactory.getFactory()
									.getInstance(VAMUtil.class);

	private VAMUtil() {

	}

	/**
	 * get the util instance.
	 * 
	 * @return VAMUtil object
	 */
	public static VAMUtil getInstance() {
		return instance;
	}

	/**
	 * generate GUID.
	 * 
	 * @return GUID
	 */
	public static String genGuid() {
		return HashFunction.createGUID().toHexString();
	}

	/**
	 * write log to the log file.
	 * 
	 * @param log
	 */
	public static void outputLog(String log) {
		logger.info(log);
	}

	/**
	 * get virtual appliance manager instance.
	 * 
	 * @return appliance manager object
	 */
	public static VirtualApplianceManager getVAManager() {
		if (vam == null) {
			vam = new VirtualApplianceManagerImpl();
		}
		return vam;
	}

	/**
	 * get the state string.
	 * 
	 * @param state
	 *            the file and appliance state
	 * @return state string
	 */
	public static String getStateString(int state) {
		switch (state) {
		case VAMConstants.STATE_READY:
			return "Ready";
		case VAMConstants.STATE_CONVERTING:
			return "Converting";
		case VAMConstants.STATE_COPING:
			return "Coping";
		case VAMConstants.STATE_DELETED:
			return "Deleted";
		case VAMConstants.STATE_DELETING:
			return "Deleting";
		case VAMConstants.STATE_ERROR:
			return "Error";
		case VAMConstants.STATE_MOVING:
			return "Moving";
		case VAMConstants.STATE_PREPARING:
			return "Preparing";
		case VAMConstants.STATE_MAKING:
			return "Making";
		case VAMConstants.STATE_PROCESSING:
			return "Processing";
		default:
			return "Unknown";
		}
	}

	/**
	 * convert capacity to string, according to the size, the unit will be B,
	 * KB, MB or GB.
	 * 
	 * @param capacity
	 *            the capacity size, the unit is B
	 * @return capacity string
	 */
	public static String getCapacityString(long capacity) {
		double cap = capacity;
		DecimalFormat currentNumberFormat = new DecimalFormat("0.00");
		String res = "";
		if (capacity >= VAMConstants.GB) {
			cap /= VAMConstants.GB;
			res = currentNumberFormat.format(cap) + " GB";
		} else if (capacity >= VAMConstants.MB) {
			cap /= VAMConstants.MB;
			res = currentNumberFormat.format(cap) + " MB";
		} else if (capacity >= VAMConstants.KB) {
			cap /= VAMConstants.KB;
			res = currentNumberFormat.format(cap) + " KB";
		} else {
			res = capacity + " B";
		}

		return res;
	}

	/**
	 * get the move file command in LINUX OS.
	 * 
	 * @param host
	 *            the remote host name or IP
	 * @param user
	 *            the remote host user name
	 * @param src
	 *            the source file path
	 * @param dst
	 *            the destination file path
	 * @return move file command
	 */
	public static String getMoveFileCommand(String host, String user,
			String src, String dst) {
		String cmd = "ssh " + user + "@" + host + " mv -f \"" + escapePath(src) 
			+ "\" \"" + escapePath(dst) + "\"";
		return cmd;
	}

	/**
	 * get the copy file command in LINUX OS.
	 * 
	 * @param host
	 *            the remote host name or IP
	 * @param user
	 *            the remote host user name
	 * @param src
	 *            the source file path
	 * @param dst
	 *            the destination file path
	 * @return copy file command
	 */
	public static String getCopyFileCommand(String host, String user,
			String src, String dst) {
		String cmd = "ssh " + user + "@" + host + " 'cp' \"" + escapePath(src) 
			+ "\" \"" + escapePath(dst) + "\"";
		return cmd;
	}

	/**
	 * get the remove remote file command in LINUX OS.
	 * 
	 * @param host
	 *            the remote host name or IP
	 * @param user
	 *            the remote host user name
	 * @param path
	 *            the file pa
	 * @return remove remote file command
	 */
	public static String getRemoveFileCommand(String host, String user,
			String path) {
		String cmd = "ssh " + user + "@" + host + " rm -f \"" + path + "\"";
		return cmd;
	}

	/**
	 * get the create virtual disk command.
	 * 
	 * @param host
	 *            the host name or IP
	 * @param user
	 *            the host user name
	 * @param format
	 *            the virtual disk format
	 * @param path
	 *            the virtual disk store path
	 * @param capacity
	 *            the virtual disk capacity
	 * @return create virtual disk command
	 */
	public static String getCreateDiskCommand(String host, String user,
			String format, String path, long capacity, String baseimage) {
		String cmd = "ssh " + user + "@" + host + " qemu-img create -f "
				+ format;
		if (baseimage != null) {
			cmd += " -b \"" + baseimage + "\" \"" + path + "\"";
		} else {
			cmd += " \"" + path + "\" " + capacity / VAMConstants.MB + "M";
		}

		return cmd;
	}

	/**
	 * get the convert virtual disk format command.
	 * 
	 * @param host
	 *            the host name or IP
	 * @param user
	 *            the host user name
	 * @param format
	 *            the virtual disk format
	 * @param src
	 *            the source virtual disk path
	 * @param dst
	 *            the destination virtual disk path
	 * @return convert virtual disk format command
	 */
	public static String getConvertDiskCommand(String host, String user,
			String format, String src, String dst) {
		String cmd = "ssh " + user + "@" + host + " qemu-img convert -O "
				+ format + " \"" + src + "\" \"" + dst + "\"";
		return cmd;
	}

	/**
	 * get the query virtual disk info command.
	 * 
	 * @param host
	 *            the host name or IP
	 * @param user
	 *            the host user name
	 * @param path
	 *            the virtual disk path
	 * @return query virtual disk info command.
	 */
	public static String getDiskInfoCommand(String host, String user,
			String path) {
		String cmd = "ssh " + user + "@" + host + " qemu-img info \"" + path
				+ "\"";
		return cmd;
	}

	/**
	 * get the query virtual disk info command.
	 * 
	 * @param path
	 *            the virtual disk path
	 * @return query virtual disk info command.
	 */
	public static String getDiskInfoCommand(String path) {
		String cmd = "qemu-img info \"" + path + "\"";
		return cmd;
	}

	/**
	 * get the create a disk's snapshot on a specific host command.
	 * 
	 * @param host
	 *            the host name or IP
	 * @param user
	 *            the host user name
	 * @param format
	 *            the format of the snapshot
	 * @param path
	 *            the snapshot path
	 * @param backingPath
	 *            the parent disk path
	 * @return create snapshot command
	 */
	public static String getCreateSnapshotCommand(String host, String user,
			String format, String path, String backingPath) {
		String cmd = "ssh " + user + "@" + host + " qemu-img create -f " 
				+ format + " -b \"" + backingPath + "\" \"" + path + "\"";
		return cmd;
	}

	/**
	 * get the start virtual machine command.
	 * 
	 * @param host
	 *            the name or IP of the host running virtual machines
	 * @param user
	 *            the user name of the host running virtual machines
	 * @param cfgPath
	 *            the disk configuration path
	 * @return start virtual machine command
	 */
	public static String getStartVirtualMachineCommand(String host,
			String user, String cfgPath) {
		String cmd = "ssh " + user + "@" + host + " xm create \"" + cfgPath
				+ "\"";
		return cmd;
	}

	/**
	 * get the stop virtual machine command.
	 * 
	 * @param host
	 *            the name or IP of the host running virtual machines
	 * @param user
	 *            the user name of the host running virtual machines
	 * @param vmName
	 *            the virtual machine name or id
	 * @return stop virtual machine command
	 */
	public static String getStopVirtualMachineCommand(String host, String user,
			String vmName) {
		String cmd = "ssh " + user + "@" + host + " xm destroy " + vmName;
		return cmd;
	}

	/**
	 * get the list virtual machines info command.
	 * 
	 * @param host
	 *            the name or IP of the host running virtual machines
	 * @param user
	 *            the user name of the host running virtual machines
	 * @return list virtual machines info command.
	 */
	public static String getVirtualMachineInfoCommand(String host, 
			String user) {
		String cmd = "ssh " + user + "@" + host + " xm list";
		return cmd;
	}

	/**
	 * create directory.
	 * 
	 * @param path
	 *            the directory path
	 * @return whether create directory successfully
	 * @throws Exception
	 */
	public static boolean createDirectory(String path) throws Exception {
		File file = new File(path);

		File dir = file.getParentFile();
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new Exception("Can't create the directory \""
						+ dir.getPath() + "\"");
			}
		}
		return true;
	}

	/**
	 * delete file.
	 * 
	 * @param path
	 *            the file path
	 * @return whether delete file successfully
	 * @throws Exception
	 */
	public static boolean deleteFile(String path) throws Exception {
		File file = new File(path);

		if (!file.exists()) {
			return false;
		}

		return file.delete();
	}

	/**
	 * check file path is correct.
	 * 
	 * @param dirPath
	 *            the directory path
	 * @param relativePath
	 *            the file path relative to the directory
	 * @return whether the file path is correct
	 * @throws Exception
	 */
	public static boolean checkFile(String dirPath, String relativePath)
			throws Exception {
		if (relativePath == null) {
			throw new Exception("File path can't be null");
		}
		if (relativePath.trim().startsWith("..")) {
			throw new Exception("Can't not access the file");
		}

		if (dirPath != null) {
			File file = new File(dirPath + relativePath);
			if (!file.exists()) {
				throw new Exception("The file \"" + relativePath
						+ "\" is not existed!");
			}
		}

		return true;
	}

	/**
	 * get the index of idle virtual machine on making appliance host.
	 * 
	 * @return the index of idle virtual machine
	 * @throws Exception
	 */
	public static int getIdleMakeApplianceVM() throws Exception {
		int selectVM = VAMConstants.NO_MAKE_APPLIANCE_VM;

		int count = VAMConfig.getMakeApplianceVMCount();

		VirtualApplianceDao appDao = DaoFactory.getVirtualApplianceDao();
		VMState vmState = appDao.getMakeApplianceVMState();

		for (int i = 1; i <= count; i++) {
			int state = vmState.getMakeApplianceVMState(i);
			if (state == VAMConstants.MAKE_APPLIANCE_VM_STATE_IDLE) {
				selectVM = i;
				break;
			}
		}

		return selectVM;
	}

	/**
	 * allocate the idle virtual machine on making appliance host.
	 * 
	 * @return the index of index virtual machine, if can't get get the idle
	 *         virtual machine return VAMConstants.NO_MAKE_APPLIANCE_VM
	 * @throws Exception
	 */
	public static int allocateMakeApplianceVM() throws Exception {
		int selectVM;

		VirtualApplianceDao appDao = DaoFactory.getVirtualApplianceDao();
		VMState vmState = appDao.getMakeApplianceVMState();

		selectVM = getIdleMakeApplianceVM();
		if (selectVM != VAMConstants.NO_MAKE_APPLIANCE_VM) {
			vmState.setMakeApplianceVMState(selectVM,
					VAMConstants.MAKE_APPLIANCE_VM_STATE_BUSY);
			appDao.udateMakeApplianceVMState(vmState);
		}
		return selectVM;
	}

	/**
	 * release the virtual machine on making appliance host.
	 * 
	 * @param index
	 *            the index of virtual machine
	 * @throws Exception
	 */
	public static void releaseMakeApplianceVM(int index) throws Exception {
		if (index <= VAMConstants.NO_MAKE_APPLIANCE_VM
				|| index > VAMConfig.getMakeApplianceVMCount()) {
			return;
		}

		VirtualApplianceDao appDao = DaoFactory.getVirtualApplianceDao();
		VMState vmState = appDao.getMakeApplianceVMState();

		vmState.setMakeApplianceVMState(index,
				VAMConstants.MAKE_APPLIANCE_VM_STATE_IDLE);
		appDao.udateMakeApplianceVMState(vmState);
	}

	/**
	 * check boot loader is correct.
	 * 
	 * @param loader
	 *            the boot loader of virtual machine, it can be
	 *            VAMConstants.VA_BOOTLOAD_HVM, VAMConstants.VA_BOOTLOAD_PYGRUB
	 *            now
	 * @return whether the boot loader is correct
	 * @throws Exception
	 */
	public static boolean checkBootLoader(String loader) throws Exception {
		if (!loader.equals(VAMConstants.VA_BOOTLOAD_HVM)
				&& !loader.equals(VAMConstants.VA_BOOTLOAD_PYGRUB)) {
			throw new Exception("Un supported virtual appliance bootloader, "
					+ "it should be " + VAMConstants.VA_BOOTLOAD_HVM + " or "
					+ VAMConstants.VA_BOOTLOAD_PYGRUB + ".");
		}
		return true;
	}

	/**
	 * check the access way is correct.
	 * 
	 * @param accessWay
	 *            the access way of appliance, it can be
	 *            VAMConstants.VA_ACCESS_WAY_SSH,
	 *            VAMConstants.VA_ACCESS_WAY_VNC, VAMConstants.VA_ACCESS_WAY_RDP
	 *            now
	 * @return whether access way is correct
	 * @throws Exception
	 */
	public static boolean checkAccessWay(String accessWay) throws Exception {
		if (!accessWay.equals(VAMConstants.VA_ACCESS_WAY_SSH)
				&& !accessWay.equals(VAMConstants.VA_ACCESS_WAY_VNC)
				&& !accessWay.equals(VAMConstants.VA_ACCESS_WAY_RDP)) {
			throw new Exception("Un supported virtual appliance accessway, "
					+ "it should be " + VAMConstants.VA_ACCESS_WAY_SSH + ", "
					+ VAMConstants.VA_ACCESS_WAY_VNC + " or "
					+ VAMConstants.VA_ACCESS_WAY_RDP + ".");
		}
		return true;
	}

	/**
	 * check the login style is correct.
	 * 
	 * @param loginStyle
	 *            the login style of appliance, it can be
	 *            VAMConstants.VA_LOGIN_STYLE_GLOBAL_USER,
	 *            VAMConstants.VA_LOGIN_STYLE_USER_PASS now
	 * @return whether login style is correct
	 * @throws Exception
	 */
	public static boolean checkLoginStyle(String loginStyle) throws Exception {
		if (!loginStyle.equals(VAMConstants.VA_LOGIN_STYLE_GLOBAL_USER)
				&& !loginStyle.equals(VAMConstants.VA_LOGIN_STYLE_USER_PASS)) {
			throw new Exception("Un supported virtual appliance login style, "
					+ "it should be " + VAMConstants.VA_LOGIN_STYLE_GLOBAL_USER
					+ " or " + VAMConstants.VA_LOGIN_STYLE_USER_PASS + ".");
		}
		return true;
	}

	/**
	 * check the disk format is correct.
	 * 
	 * @param format
	 *            the disk format, it can be VAMConstants.VAF_FORMAT_QCOW,
	 *            VAMConstants.VAF_FORMAT_QCOW_2, VAMConstants.VAF_FORMAT_RAW,
	 *        	  VAMConstants.VAF_FORMAT_VMDK now
	 * @return whether disk format is correct
	 * @throws Exception
	 */
	public static boolean checkDiskFormat(String format) throws Exception {
		if (!format.equals(VAMConstants.VAF_FORMAT_QCOW)
				&& !format.equals(VAMConstants.VAF_FORMAT_QCOW_2)
				&& !format.equals(VAMConstants.VAF_FORMAT_RAW)
				&& !format.equals(VAMConstants.VAF_FORMAT_VMDK)) {
			throw new Exception("Un supported virtual appliance format, "
					+ "it should be " + VAMConstants.VAF_FORMAT_RAW + ", "
					+ VAMConstants.VAF_FORMAT_QCOW_2 + ", "
					+ VAMConstants.VAF_FORMAT_QCOW + " or "
					+ VAMConstants.VAF_FORMAT_VMDK + ".");
		}
		return true;
	}

	/**
	 * list the file of one directory.
	 * 
	 * @param dirPath
	 *            the directory path
	 * @param fileType
	 *            the type of file
	 * @param format
	 *            the format of file
	 * @return file list
	 * @throws Exception
	 */
	
	public static List<String> listDirectory(String dirPath, String fileType,
			String format) throws Exception {
		List<String> resl = new ArrayList<String>();

		if (fileType == null) {
			return resl;
		}

		dirPath = null;
		if (fileType.equals(VAMConstants.VAF_FILE_TYPE_DISK)) {
			dirPath = VAMConfig.getDiskUploadDirLocation();
		} else if (fileType.equals(VAMConstants.VAF_FILE_TYPE_DISC)) {
			dirPath = VAMConfig.getDiscUploadDirLocation();
		}

		if (dirPath == null) {
			return resl;
		}

		Map<String, String> markMap = new HashMap<String, String>();
		// if file type is disc, get the existed discs
		if (fileType.equals(VAMConstants.VAF_FILE_TYPE_DISC)) {
			List<VAFile> fileList = getVAManager().getFilesByType(
					VAMConstants.VAF_FILE_TYPE_DISC);
			for (int i = 0; i < fileList.size(); i++) {
				VAFile file = (VAFile) fileList.get(i);
				if (file != null) {
					markMap.put(file.getLocation(), "mark");
				}
			}
		}

		File dir = new File(dirPath);
		// if the path is directory
		if (dir != null && dir.exists() && dir.isDirectory()) {
			File[] fileArray = dir.listFiles();
			for (int i = 0; i < fileArray.length; i++) {
				File file = fileArray[i];
				if (fileType.equals(VAMConstants.VAF_FILE_TYPE_DISC)) {
					// if (file.getName().endsWith("." + format)) {
					if (markMap.get(file.getName()) == null) {
						resl.add(file.getName());
					}
					// }
				} else if (fileType.equals(VAMConstants.VAF_FILE_TYPE_DISK)) {
					// if (!file.isDirectory() && !file.getName().endsWith("."
					// + VAMConstants.VAF_FORMAT_ISO)) {
					resl.add(file.getName());
					// }
				}
			}
		}

		return resl;
	}

	/**
	 * convert list to string.
	 * 
	 * @param list
	 *            the string list
	 * @return the format string
	 */
	public static String list2String(List<String> list) {
		String result = "";

		if (list != null && list.size() > 0) {
			result = list.get(0);
			for (int i = 1; i < list.size(); i++) {
				result += ", " + list.get(i);
			}
		}

		return result;
	}

	/**
	 * check disc type.
	 * 
	 * @param discType
	 *            the type of disc
	 * @return disc type
	 */
	public static String checkDiscType(String discType) {
		String type = VAMConstants.VAD_DISK_TYPE_UNKNOWN;
		if (discType != null
				&& (discType.equals(VAMConstants.VAD_DISK_TYPE_APP) || discType
						.equals(VAMConstants.VAD_DISK_TYPE_OS))) {
			type = discType;
		}
		return type;
	}

	/**
	 * get time stamp.
	 * 
	 * @return
	 */
	public static long getTimestamp() {
		return System.currentTimeMillis();
	}

	/**
	 * get time string.
	 * 
	 * @param date
	 *            date object
	 * @return
	 */
	public static String getTimeString(Date date) {
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return time.format(date);
	}

	/**
	 * get the operating system string.
	 * 
	 * @param os
	 *            operating system
	 * @param osVersion
	 *            operating system version
	 * @return
	 */
	public static String getOperatingSystemString(String os, String osVersion) {
		String res = "";
		if (!os.equals(VAMConstants.VA_OS_OTHER)) {
			res += os + " ";
		}
		res += osVersion;

		return res;
	}

	/**
	 * search string by regular expression.
	 * 
	 * @param str
	 *            string
	 * @param regex
	 *            search pattern
	 * @return the searched string
	 */
	public static String reSearch(String str, String regex) {
		String res = null;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			res = matcher.group(1);
		}
		return res;
	}

	/**
	 * execute command.
	 * 
	 * @param command
	 *            command
	 * @return execute result
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String executeCommand(String command) throws IOException,
			InterruptedException {
		// create a process input stream thread
		StreamGobbler errorGobbler = new StreamGobbler("ERROR");
		errorGobbler.setName("MainThread");
		errorGobbler.start();

		// execute command
		Runtime rt = Runtime.getRuntime();
		Process proc;
		String[] shell = new String[] { "/bin/sh", "-c", command };

		proc = rt.exec(shell);
		errorGobbler.end();
		errorGobbler.addStream(proc.getErrorStream());

		// get input stream
		InputStreamReader isr = new InputStreamReader(proc.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		String res = "";

		// read the output
		while ((line = br.readLine()) != null) {
			res += line + "\r\n";
		}

		proc.waitFor();

		if (proc.exitValue() != 0) {
			if (!res.equals("")) {
				VAMUtil.outputLog(res);
			}
			res = null;
		}

		return res;
	}

	/**
	 * get the information of disk.
	 * 
	 * @param filePath
	 *            absolute path of the file
	 * @return disk info object
	 */
	public static DiskInfo getDiskInfo(String filePath) {
		DiskInfo disk = null;

		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}

		String command = VAMUtil.getDiskInfoCommand(
				VAMConfig.getMakeApplianceHost(), 
				VAMConfig.getMakeApplianceUser(), filePath);

		try {
			String res = executeCommand(command);
			if (res != null) {
				disk = new DiskInfo();
				disk.init();

				disk.setFormat(VAMUtil.reSearch(res, "file format: (\\w+)\\s"));
				disk.setBackingFile(VAMUtil.reSearch(res,
						"backing file: (.+) \\(.+\\)\\s"));
				String vsize = VAMUtil.reSearch(res,
						"virtual size: .+ \\((\\d+) bytes\\)\\s");

				if (vsize != null) {
					disk.setVirtualSize(Long.parseLong(vsize));
				}
				disk.setDiskSize(file.length());

			}
		} catch (Exception e) {
			VAMUtil.outputLog(e.getMessage());
		}

		return disk;
	}

	/**
	 * <p>
	 * Checks if the field isn't null and length of the field is greater than
	 * zero not including whitespace.
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @return true if blank or null.
	 */
	public static boolean isBlankOrNull(String value) {
		return ((value == null) || (value.trim().length() == 0));
	}
	
	private static String escapePath(String path) {
		String res = path.replaceAll(" ", "\\\\ ");
		return res;
	}
}
