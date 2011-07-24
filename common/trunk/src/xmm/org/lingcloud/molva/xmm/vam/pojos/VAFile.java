/*
 *  @(#)VAFile.java  2010-5-20
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

import org.lingcloud.molva.xmm.vam.util.VAMConfig;
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
public class VAFile extends VAObject {

	/**
	 * the serial ID.
	 */
	private static final long serialVersionUID = 315478735593568825L;
	private String loc;

	/**
	 * the constructor.
	 */
	public VAFile() {
		super.setType(VAMConstants.VIRTUAL_APPLIANCE_FILE);
	}

	/**
	 * the constructor.
	 * @param guid
	 * 		the file's GUID
	 * @param fileType
	 * 		the file's type
	 * @param format
	 * 		the file's format
	 * @param location
	 * 		the file's location
	 * @param id
	 * 		the file's id
	 * @param size
	 * 		the file's size
	 * @param state
	 * 		the file's state
	 */
	public VAFile(String guid, String fileType, String format, String location,
			String id, long size, int state) {
		setGuid(guid);
		setFileType(fileType);
		setFormat(format);
		setLocation(location);
		setId(id);
		setSize(size);
		setState(state);

		super.setType(VAMConstants.VIRTUAL_APPLIANCE_FILE);
	}

	/**
	 * to construct a VAFile from a right type.
	 * 
	 * @param vao
	 *            the basic object of virtual appliance
	 */
	public VAFile(VAObject vao) {
		super(vao);
		if (vao != null
				&& !(vao.getType().equals(
						VAMConstants.VIRTUAL_APPLIANCE_FILE))) {
			String msg = "Wrong virtual appliance object type. It should be "
					+ VAMConstants.VIRTUAL_APPLIANCE_FILE + " but get "
					+ vao.getType();
			throw new RuntimeException(msg);
		}
	}

	/**
	 * Clone an object.
	 * 
	 * @return the cloned object.
	 * @throws CloneNotSupportedException
	 *             Not support clone.
	 */
	public VAFile clone() throws CloneNotSupportedException {
		try {
			VAFile vafile = new VAFile(this);
			return vafile;
		} catch (Exception e) {
			throw new CloneNotSupportedException("Clone failed due to " + e);
		}
	}

	/**
	 * set the name. It must override the method that make sure the
	 * object has right name.
	 * 
	 * @param name
	 *            the name of file.
	 */
	public void setName(String name) {
		super.setType(VAMConstants.VIRTUAL_APPLIANCE_FILE);
	}
	
	/**
	 * set the type. It must override the method that make sure the
	 * object has right type.
	 * 
	 * @param type
	 *            the type of file.
	 */
	public void setType(String type) {
		super.setType(VAMConstants.VIRTUAL_APPLIANCE_FILE);
	}

	/**
	 * get the location of file.
	 * @return location
	 */
	public String getLocation() {
		return (String) this.getAttributes().get(VAMConstants.VAF_LOCATION);
	}

	/**
	 * set the location of file.
	 * @param location
	 * 		the location of file
	 */
	public void setLocation(String location) {
		this.getAttributes().put(VAMConstants.VAF_LOCATION, location);
	}

	/**
	 * get the name of file.
	 * @return the name of file
	 */
	public String getId() {
		return (String) this.getAttributes().get(VAMConstants.VAF_ID);
	}

	/**
	 * set the name of file.
	 * @param id
	 * 		the name of file
	 */
	public void setId(String id) {
		this.getAttributes().put(VAMConstants.VAF_ID, id);
	}

	/**
	 * get the size of file.
	 * @return size of file
	 */
	public long getSize() {
		if (VAMUtil.isBlankOrNull((String) this.getAttributes().get(
				VAMConstants.VAF_SIZE))) {
			return 0;
		}
		return Long.parseLong((String) this.getAttributes().get(
				VAMConstants.VAF_SIZE));
	}

	/**
	 * set the size of file.
	 * @param size
	 * 		size of file.
	 */
	public void setSize(long size) {
		this.getAttributes().put(VAMConstants.VAF_SIZE, "" + size);
	}

	/**
	 * get the type of file.
	 * @return type of file
	 */
	public String getFileType() {
		return (String) this.getAttributes().get(VAMConstants.VAF_FILE_TYPE);
	}

	/**
	 * set the type of file.
	 * @param type
	 * 		the type of file
	 */
	public void setFileType(String type) {
		this.getAttributes().put(VAMConstants.VAF_FILE_TYPE, type);
	}

	/**
	 * get the format of file.
	 * @return the format of file
	 */
	public String getFormat() {
		return (String) this.getAttributes().get(VAMConstants.VAF_FORMAT);
	}

	/**
	 * set the format of file.
	 * @param format
	 * 		the format of file
	 */
	public void setFormat(String format) {
		this.getAttributes().put(VAMConstants.VAF_FORMAT, format);
	}

	/**
	 * get the parent file GUID.
	 * @return parent file GUID
	 */
	public String getParent() {
		return (String) this.getAttributes().get(VAMConstants.VAF_PARENT);
	}

	/**
	 * set the parent file GUID.
	 * @param parent
	 * 		the parent file GUID
	 */
	public void setParent(String parent) {
		this.getAttributes().put(VAMConstants.VAF_PARENT, parent);
	}

	/**
	 * get reference.
	 * @return reference
	 */
	public int getRef() {
		if (VAMUtil.isBlankOrNull((String) this.getAttributes().get(
				VAMConstants.VAF_REF))) {
			return 0;
		}
		return Integer.parseInt((String) this.getAttributes().get(
				VAMConstants.VAF_REF));
	}

	/**
	 * set reference.
	 * @param ref
	 * 		reference
	 */
	public void setRef(int ref) {
		if (ref < 0) {
			ref = 0;
		}
		this.getAttributes().put(VAMConstants.VAF_REF, "" + ref);
	}
	
	public boolean isReplica() {
		if (VAMUtil.isBlankOrNull((String) this.getAttributes().get(
				VAMConstants.VAF_REPLICA))) {
			return false;
		}
		return Boolean.parseBoolean((String) this.getAttributes().get(
				VAMConstants.VAF_REPLICA));
	}
	
	public void setReplica(boolean replica) {
		this.getAttributes().put(VAMConstants.VAF_REPLICA, "" + replica);
	}

	/**
	 * get the local save location.
	 * @return the local save location
	 */
	public String getSavePath() {
		String path = null;
		String fileType = getFileType();
		if (fileType != null) {
			String dir = VAMConfig.getFileDirLocation() + fileType + "/";
			loc = getLocation();
			if (dir != null && loc != null) {
				path = dir + loc;
			}
		}

		return path;
	}
}
