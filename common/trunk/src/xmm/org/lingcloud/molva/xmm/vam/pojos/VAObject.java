/*
 *  @(#)VAObject.java  2010-5-26
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

import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;


/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-26<br>
 * @author Ruijian Wang<br>
 * 
 */
public class VAObject extends Asset {

	/**
	 * the serial ID.
	 */
	private static final long serialVersionUID = -6587596957486246443L;

	/**
	 * the constructor.
	 */
	public VAObject() {
		init();
	}

	/**
	 * to construct a VAObject from a right name.
	 * 
	 * @param asset
	 *            the asset object.
	 */
	public VAObject(Asset asset) {
		super(asset);
		if (asset.getAssetController() == null) {
			String msg = "VAObject's asset controller should not be null.";
			throw new RuntimeException(msg);
		}
	}
	
	/**
	 * to construct a VAObject from a right name.
	 * @param vao
	 * 		the basic object of virtual appliance
	 */
	public VAObject(VAObject vao) {
		this(vao.toAsset());
	}

	private void init() {
		super.setAcl(VAMConstants.DEFAULT_ACL);
		super.setName(VAMConstants.VIRTUAL_APPLIANCE_OBJECT);
		super.setType(VAMConstants.VIRTUAL_APPLIANCE_OBJECT);
		super.setAssetController("AssetController");
	}

	public void setGuid(String guid) {
		super.setName(guid);
		super.setGuid(guid);
	}
	
	/**
	 * get the state of object.
	 * @return state
	 */
	public int getState() {
		if (VAMUtil.isBlankOrNull((String) this.getAttributes().get(
				VAMConstants.VAO_STATE))) {
			return 0;
		}
		return Integer.parseInt((String) this.getAttributes().get(
				VAMConstants.VAO_STATE));
	}

	/**
	 * set the state of object.
	 * @param state
	 * 		the state of object
	 */
	public void setState(int state) {
		this.getAttributes().put(VAMConstants.VAO_STATE, "" + state);
	}

	/**
	 * convert this object to a Asset.
	 * 
	 * @return return the new created Asset object.
	 */
	public Asset toAsset() {
		try {
			return (Asset) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(new Exception(
					"convert to GNode failed due to : " + e));
		}
	}
	
	/**
	 * get the source location.
	 * @return
	 */
	public String getSrcPath() {
		return (String) this.getAttributes().get(VAMConstants.VAO_SRC_PATH);
	}
	
	/**
	 * set the source location.
	 * @param srcPath
	 * 		source location
	 */
	public void setSrcPath(String srcPath) {
		this.getAttributes().put(VAMConstants.VAO_SRC_PATH, srcPath);
	}
	
	/**
	 * get the type of operation.
	 * @return the type of operation
	 */
	public int getOperationType() {
		if (VAMUtil.isBlankOrNull((String) this.getAttributes().get(
				VAMConstants.VAO_OPERATION_TYPE))) {
			return 0;
		}
		return Integer.parseInt((String) this.getAttributes().get(
				VAMConstants.VAO_OPERATION_TYPE));
	}

	/**
	 * set the type of operation.
	 * @param operationType
	 * 		the type of operation
	 */
	public void setOperationType(int operationType) {
		this.getAttributes().put(VAMConstants.VAO_OPERATION_TYPE, 
				"" + operationType);
	}
	
	/**
	 * get the time stamp.
	 * @return time stamp
	 */
	public long getTimestamp() {
		if (VAMUtil.isBlankOrNull((String) this.getAttributes().get(
				VAMConstants.VAO_TIMESTAMP))) {
			return 0;
		}
		return Long.parseLong((String) this.getAttributes().get(
				VAMConstants.VAO_TIMESTAMP));
	}

	/**
	 * set the time stamp.
	 * @param timestamp
	 * 		time stamp
	 */
	public void setTimestamp(long timestamp) {
		this.getAttributes().put(VAMConstants.VAO_TIMESTAMP, 
				"" + timestamp);
	}
	
	/**
	 * whether delete the file after operation.
	 * @return true if delete file after operation, else false
	 */
	public boolean isDeleteFileAfterOperation() {
		if (VAMUtil.isBlankOrNull((String) this.getAttributes().get(
				VAMConstants.VAO_DELETE_FILE))) {
			return false;
		}
		return Boolean.parseBoolean((String) this.getAttributes().get(
				VAMConstants.VAO_DELETE_FILE));
	}
	
	/**
	 * set whether delete the file after operation.
	 * @param deleteFile
	 * 		delete file
	 */
	public void setDeletefileAfterOperation(boolean deleteFile) {
		this.getAttributes().put(VAMConstants.VAO_DELETE_FILE, 
				"" + deleteFile);
	}
	
	/**
	 * get the controller.
	 * @return controller name
	 */
	public String getController() {
		return (String) super.getAssetController();
	}
	
	/**
	 * set the controller.
	 * @param controller
	 * 		controller name
	 */
	public void setController(String controller) {
		super.setAssetController(controller);
	}
}
