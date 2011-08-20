/*
 *  @(#)VADisk.java  2010-5-20
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

package org.lingcloud.molva.xmm.vam.pojos;

import java.util.ArrayList;
import java.util.List;

import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-20<br>
 * @author Ruijian Wang<br>
 * 
 */
public class VADisk extends VAFile {
	/**
	 * <strong>Purpose:</strong><br>
	 * TODO.
	 * 
	 * @version 1.0.1 Dec 1, 2010<br>
	 * @author Ruijian Wang<br>
	 * 
	 */
	public static class DiskInfo {
		/**
		 * file format.
		 */
		private String format;
		/**
		 * file size.
		 */
		private long diskSize;
		/**
		 * virtual disk size.
		 */
		private long virtualSize;
		/**
		 * backing file.
		 */
		private String backingFile;

		public String getFormat() {
			return format;
		}

		public void setFormat(String format) {
			this.format = format;
		}

		public long getDiskSize() {
			return diskSize;
		}

		public void setDiskSize(long diskSize) {
			this.diskSize = diskSize;
		}

		public long getVirtualSize() {
			return virtualSize;
		}

		public void setVirtualSize(long virtualSize) {
			this.virtualSize = virtualSize;
		}

		public String getBackingFile() {
			return backingFile;
		}

		public void setBackingFile(String backingFile) {
			this.backingFile = backingFile;
		}
		public void init() {
			format = null;
			diskSize = 0;
			virtualSize = 0;
			backingFile = null;
		}
	}
	/**
	 * the serial ID.
	 */
	private static final long serialVersionUID = -5604860399568104578L;

	/**
	 * the constructor.
	 */
	public VADisk() {

	}

	public VADisk(VAObject vao) {
		super(vao);
	}

	public VADisk(VAFile vafile) {
		super(vafile);
	}

	/**
	 * get disk capacity.
	 * @return disk capacity
	 */
	public long getCapacity() {
		if (VAMUtil.isBlankOrNull((String) this.getAttributes().get(
				VAMConstants.VAD_CAPACITY))) {
			return 0;
		}
		return Long.parseLong((String) this.getAttributes().get(
				VAMConstants.VAD_CAPACITY));
	}
	
	/**
	 * set disk capacity.
	 * @param capacity
	 * 		disk capacity
	 */
	public void setCapacity(long capacity) {
		this.getAttributes().put(VAMConstants.VAD_CAPACITY, "" + capacity);
	}

	/**
	 * get disk type.
	 * @return disk type
	 */
	public String getDiskType() {
		return (String) this.getAttributes().get(VAMConstants.VAD_DISK_TYPE);
	}

	/**
	 * set disk type.
	 * @param diskType
	 * 		disk type
	 */
	public void setDiskType(String diskType) {
		this.getAttributes().put(VAMConstants.VAD_DISK_TYPE, diskType);
	}

	/**
	 * get operating system installed on the disk.
	 * @return operating system
	 */
	public String getOs() {
		String os = (String) this.getAttributes().get(VAMConstants.VAD_OS);
		if (os == null) {
			return "";
		}
		String[] osArray = os.split("\\|");
		if (osArray.length > 0) {
			return osArray[0];
		}
		return "";
	}

	/**
	 * get operating system version.
	 * @return operating system version
	 */
	public String getOsVersion() {
		String os = (String) this.getAttributes().get(VAMConstants.VAD_OS);
		if (os == null) {
			return "";
		}
		String[] osArray = os.split("\\|");
		if (osArray.length > 1) {
			return osArray[1];
		}
		return "";
	}

	/**
	 * set operating system installed on the disk.
	 * @param os
	 * 		operating system name
	 * @param osVersion
	 * 		operating system version
	 */
	public void setOs(String os, String osVersion) {
		this.getAttributes().put(VAMConstants.VAD_OS, os + "|" + osVersion);
	}

	/**
	 * get applications installed in the operating system.
	 * @return application list
	 */
	public List<String> getApplications() {
		List<String> appl = new ArrayList<String>();
		String apps = (String) this.getAttributes().get(
				VAMConstants.VAD_APPLICATIONS);
		if (apps == null) {
			return appl;
		}
		String[] appArray = apps.split("\\|");

		for (int i = 0; i < appArray.length; i++) {
			appl.add(appArray[i]);
		}
		return appl;
	}

	/**
	 * set applications installed in the operating system.
	 * @param applications
	 * 		applications installed in the operating system.
	 */
	public void setApplications(List<String> applications) {
		if (applications == null) {
			return;
		}
		String apps = "";
		int size = applications.size();
		if (applications.size() > 0) {
			apps = applications.get(0);
			for (int i = 1; i < size; i++) {
				if (applications.get(i) != null) {
					apps += "|" + applications.get(i);
				}

			}
		}

		this.getAttributes().put(VAMConstants.VAD_APPLICATIONS, apps);
	}
}
