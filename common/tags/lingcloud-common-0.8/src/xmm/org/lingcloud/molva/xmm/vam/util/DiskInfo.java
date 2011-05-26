/*
 *  @(#)DiskInfo.java  Dec 1, 2010
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

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 Dec 1, 2010<br>
 * @author Ruijian Wang<br>
 * 
 */
public class DiskInfo {
	// file format
	private String format;
	// file size
	private long diskSize;
	// virtual disk size
	private long virtualSize;
	// backing file
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
