/*
 *  @(#)GNodeInfo.java  2007-8-22
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

package org.lingcloud.molva.ocl.persistence;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

/**
 * 
 * <strong>Purpose:</strong><br>
 * The infomation part of a GNode, only the orignal GNode has this part, Link
 * GNode only has a link to it.
 * 
 * @version 1.0.1 2007-8-22<br>
 * @author zouyongqiang<br>
 * 
 */
public class GNodeInfo implements Serializable, Cloneable {
	/**
	 * add for Serializable.
	 */
	private static final long serialVersionUID = 2944888598139857836L;

	/**
	 * the guid of Object.
	 */
	private String guid;

	// These are one in GNode.
	/**
	 * The user friendly name of the Object.
	 */
	private String name;

	/**
	 * GNode type. It can be service, message, etc.
	 */
	private int type;

	/**
	 * RController type. eg. org.gos.RController.ServiceController.
	 */
	private String rControllerType;

	/**
	 * How many times are the GNode be link to other agora?
	 */
	private int linkCount;

	/**
	 * The real obj address/name. For service, it's url.
	 */
	private String obj;

	/**
	 * The RController's url.
	 */
	private String rControllerURL;

	/**
	 * export agora list sepreated by ',', "*" means all agoras.
	 */
	private String export;

	/**
	 * the description.
	 */
	private String description;

	/**
	 * extension part. It's key-value pairs. Default is a empty map.
	 */
	private Map attributes = new HashMap();

	/**
	 * get the guid of Object.
	 * 
	 * @return the guid of Object.
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * the guid of Object.
	 * 
	 * @param guid
	 *            the guid.
	 */
	public void setGuid(String guid) {
		this.guid = guid;
	}

	/**
	 * Get the linkCount. It indicate how many times are the GNode be link to
	 * other agora.
	 * 
	 * @return the linkCount.
	 */
	public int getLinkCount() {
		return linkCount;
	}

	/**
	 * Set the linkCount. It indicate how many times are the GNode be link to
	 * other agora.
	 * 
	 * @param linkCount
	 *            the linkCount.
	 */
	public void setLinkCount(int linkCount) {
		this.linkCount = linkCount;
	}

	/**
	 * Get the real obj address. For service, it's url.
	 * 
	 * @return The real obj address. For service, it's url.
	 */
	public String getObj() {
		return obj;
	}

	/**
	 * Set the real object address. For service, it's url.
	 * 
	 * @param object
	 *            The real object address. For service, it's url.
	 */
	public void setObj(String object) {
		this.obj = object;
	}

	/**
	 * Get export agora list sepreated by ',', "*" means all agoras.
	 * 
	 * @return export agora list.
	 */
	public String getExport() {
		return export;
	}

	/**
	 * Set export agora list sepreated by ',', "*" means all agoras.
	 * 
	 * @param export
	 *            export agora list.
	 */
	public void setExport(String export) {
		this.export = export;
	}

	/**
	 * Get the description.
	 * 
	 * @return the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description.
	 * 
	 * @param desc
	 *            the description.
	 */
	public void setDescription(String desc) {
		this.description = desc;
	}

	/**
	 * Get extension part. It's key-value pairs.
	 * 
	 * @return extension part.
	 */
	public Map getAttributes() {
		return attributes;
	}

	/**
	 * Set extension part. It's key-value pairs.
	 * 
	 * @param attributes
	 *            extension part.
	 */
	public void setAttributes(Map attributes) {
		this.attributes = attributes;
	}

	/**
	 * Get the user friendly name of the Object.
	 * 
	 * @return The user friendly name of the Object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the user friendly name of the Object.
	 * 
	 * @param name
	 *            The user friendly name of the Object.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the RController's url.
	 * 
	 * @return The RController's url.
	 */
	public String getRControllerURL() {
		return rControllerURL;
	}

	/**
	 * Set the RController's url.
	 * 
	 * @param controllerURL
	 *            The RController's url.
	 */
	public void setRControllerURL(String controllerURL) {
		rControllerURL = controllerURL;
	}

	/**
	 * Get GNode type. It can be service, message, etc.
	 * 
	 * @return GNode type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Set GNode type. It can be service, message, etc.
	 * 
	 * @param type
	 *            GNode type
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Get RController type. eg. org.gos.RController.ServiceController.
	 * 
	 * @return RController type.
	 */
	public String getRControllerType() {
		return rControllerType;
	}

	/**
	 * Set RController type. eg. org.gos.RController.ServiceController.
	 * 
	 * @param controllerType
	 *            RController type.
	 */
	public void setRControllerType(String controllerType) {
		this.rControllerType = controllerType;
	}

	/**
	 * Clone an object.
	 * 
	 * @return the cloned object.
	 * @throws CloneNotSupportedException
	 *             Not support clone.
	 */
	public Object clone() throws CloneNotSupportedException {
		GNodeInfo newGNode = new GNodeInfo();
		try {
			// FIXME It's not deep copy!
			BeanUtils.copyProperties(newGNode, this);
			// clone attributes. 2007.9.13 with the help of Wang Xiaoning.
			// Caution!!! Should consider String and other type's deep copy
			// later.
			if (this.attributes != null) {
				newGNode.setAttributes(new HashMap(this.attributes));
			}
		} catch (Exception e) {
			throw new CloneNotSupportedException("Clone not support due to : "
					+ e);
		}

		return newGNode;
	}
}
