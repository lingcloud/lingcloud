/*
 *  @(#)Placeable.java  2007-7-20
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
import java.util.Date;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.lingcloud.molva.ocl.util.GNodeValidator;

/**
 * 
 * <strong>Purpose:</strong><br>
 * The common part of two type of GNode.
 * 
 * @version 1.0.1 2007-7-20<br>
 * @author zouyongqiang<br>
 * 
 */
public class GNode implements Placeable, Serializable, Cloneable {
	/**
	 * add for Serializable.
	 */
	private static final long serialVersionUID = 4152864863185948508L;

	/**
	 * the guid of Object.
	 */
	private String guid;

	/**
	 * the DN of owner user.
	 */
	private String ownerDN;

	/**
	 * the guid of owner.
	 */
	private String ownerID;

	/**
	 * the ownerAgora's guid.
	 */
	private String ownerAgoraID;

	/**
	 * Who create the GNode? It's due to some security consideration.
	 */
	private String initUserID;

	/**
	 * The GNode is created in which agora?
	 */
	private String initAgoraID;

	/**
	 * the create time of the GNode.
	 */
	private Date addTime;

	/**
	 * Which site is responsible to the GNode?
	 */
	private String homeSiteID;

	/**
	 * the group's guid.
	 */
	private String groupID;

	/**
	 * the last update time.
	 */
	private Date updateTime;

	/**
	 * Access control information. It's "rwx rwx rwx" mode, represent read,
	 * write, execute permission for owner, group, others in the ownerAgora.
	 */
	private String acl;

	/**
	 * version number to indicate the new value.
	 */
	private int version;

	/**
	 * The orginal GNode. If it's guid is equals to this, then itself is an
	 * orgin GNode. If it's not equal, it's point to the orgin GNode of this
	 * GNodeLink.
	 */
	private GNodeInfo originGNodeInfo = new GNodeInfo();

	/**
	 * default constructor.
	 */
	public GNode() {
		setExport(GNodeConstants.DEFAULT_EXPORT);
		setAcl(GNodeConstants.DEFAULT_ACL);
	}

	/**
	 * constructor.
	 * 
	 * @param gnode
	 *            gNode
	 */
	public GNode(GNode gnode) {
		if (gnode == null) {
			return;
		}
		try {
			// FIXME It's not deep copy!
			BeanUtils.copyProperties(this, gnode);
			// clone the GNodeInfo.
			this.setOriginGNodeInfo((GNodeInfo) gnode.getOriginGNodeInfo()
					.clone());
		} catch (Exception e) {
			throw new RuntimeException(new CloneNotSupportedException(
					"Clone not support due to : " + e));
		}
	}

	/**
	 * Get the ID of the orginal GNode. If it's guid is equals to this, then
	 * itself is an orgin GNode. If it's not equal, it's point to the orgin
	 * GNode.
	 * 
	 * @return the GNode.
	 */
	public String getOriginGNodeID() {
		return this.originGNodeInfo.getGuid();
	}

	/**
	 * Set the ID of the orginal GNode. If it's guid is equals to this, then
	 * itself is an orgin GNode. If it's not equal, it's point to the orgin
	 * GNode.
	 * 
	 * @param originGNodeID
	 *            the of GNode object.
	 */
	public void setOriginGNodeID(String originGNodeID) {
		this.originGNodeInfo.setGuid(originGNodeID);
	}

	/**
	 * Get the group's guid.
	 * 
	 * @return the group's guid.
	 */
	public String getGroupID() {
		return groupID;
	}

	/**
	 * Set the group's guid.
	 * 
	 * @param groupID
	 *            the group's guid.
	 */
	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	/**
	 * Get the last update time.
	 * 
	 * @return the last update time.
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * Set the last update time.
	 * 
	 * @param updateTime
	 *            the last update time.
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * Get access control information. It's "rwx rwx rwx" mode, represent read,
	 * write, execute permission for owner, group, others in the ownerAgora.
	 * 
	 * @return access control information.
	 */
	public String getAcl() {
		return acl;
	}

	/**
	 * Set the access control information. It's "rwx rwx rwx" mode, represent
	 * read, write, execute permission for owner, group, others in the
	 * ownerAgora.
	 * 
	 * @param acl
	 *            access control information.
	 */
	public void setAcl(String acl) {
		this.acl = acl;
	}

	/**
	 * Get the version number.
	 * 
	 * @return the version number.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Set the version number.
	 * 
	 * @param version
	 *            the version number.
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * Get the linkCount. It indicate how many times are the GNode be link to
	 * other agora.
	 * 
	 * @return the linkCount.
	 */
	public int getLinkCount() {
		return originGNodeInfo.getLinkCount();
	}

	/**
	 * Set the linkCount. It indicate how many times are the GNode be link to
	 * other agora.
	 * 
	 * @param linkCount
	 *            the linkCount.
	 */
	public void setLinkCount(int linkCount) {
		originGNodeInfo.setLinkCount(linkCount);
	}

	/**
	 * Get the real object address. For service, it's url.
	 * 
	 * @return The real object address. For service, it's url.
	 */
	public String getObj() {
		return originGNodeInfo.getObj();
	}

	/**
	 * Set the real object address. For service, it's url.
	 * 
	 * @param object
	 *            The real object address. For service, it's url.
	 */
	public void setObj(String object) {
		originGNodeInfo.setObj(object);
	}

	/**
	 * Get export agora list sepreated by ',', "*" means all agoras.
	 * 
	 * @return export agora list.
	 */
	public String getExport() {
		return originGNodeInfo.getExport();
	}

	/**
	 * Set export agora list sepreated by ',', "*" means all agoras.
	 * 
	 * @param export
	 *            export agora list.
	 */
	public void setExport(String export) {
		this.originGNodeInfo.setExport(export);
	}

	/**
	 * Get the description.
	 * 
	 * @return the description.
	 */
	public String getDescription() {
		return originGNodeInfo.getDescription();
	}

	/**
	 * Set the description.
	 * 
	 * @param desc
	 *            the description.
	 */
	public void setDescription(String desc) {
		originGNodeInfo.setDescription(desc);
	}

	/**
	 * Get extension part. It's key-value pairs.
	 * 
	 * @return extension part.
	 */
	public Map getAttributes() {
		return originGNodeInfo.getAttributes();
	}

	/**
	 * Set extension part. It's key-value pairs.
	 * 
	 * @param attributes
	 *            extension part.
	 */
	public void setAttributes(Map attributes) {
		originGNodeInfo.setAttributes(attributes);
	}

	/**
	 * Get the user friendly name of the Object.
	 * 
	 * @return The user friendly name of the Object.
	 */
	public String getName() {
		return originGNodeInfo.getName();
	}

	/**
	 * Set the user friendly name of the Object.
	 * 
	 * @param name
	 *            The user friendly name of the Object.
	 */
	public void setName(String name) {
		originGNodeInfo.setName(name);
	}

	/**
	 * Get the RController's url.
	 * 
	 * @return The RController's url.
	 */
	public String getRControllerURL() {
		return originGNodeInfo.getRControllerURL();
	}

	/**
	 * Set the RController's url.
	 * 
	 * @param controllerURL
	 *            The RController's url.
	 */
	public void setRControllerURL(String controllerURL) {
		originGNodeInfo.setRControllerURL(controllerURL);
	}

	/**
	 * Get GNode type. It can be service, message, etc.
	 * 
	 * @return GNode type
	 */
	public int getType() {
		return originGNodeInfo.getType();
	}

	/**
	 * Set GNode type. It can be service, message, etc.
	 * 
	 * @param type
	 *            GNode type
	 */
	public void setType(int type) {
		originGNodeInfo.setType(type);
	}

	/**
	 * Get RController type. eg. org.gos.RController.ServiceController.
	 * 
	 * @return RController type.
	 */
	public String getRControllerType() {
		return originGNodeInfo.getRControllerType();
	}

	/**
	 * Set RController type. eg. org.gos.RController.ServiceController.
	 * 
	 * @param controllerType
	 *            RController type.
	 */
	public void setRControllerType(String controllerType) {
		originGNodeInfo.setRControllerType(controllerType);
	}

	/**
	 * convert this object to a GNode.
	 * 
	 * @return return the new created GNode object.
	 */
	public GNode toGNode() {
		// Force gnode to a GNode object, so that even gnode is a subclass of
		// GNode, we can use it in hibernate without define the mapping of the
		// subclass. 2007.9.2
		try {
			return (GNode) clone();
		} catch (Exception e) {
			throw new RuntimeException(new Exception(
					"convert to GNode failed due to : " + e));
		}
	}

	/**
	 * Get the create time of the GNode.
	 * 
	 * @return the create time of the GNode.
	 */
	public Date getAddTime() {
		return addTime;
	}

	/**
	 * Set the create time of the GNode.
	 * 
	 * @param addTime
	 *            the create time of the GNode.
	 */
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

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
	 * Get the initial agora id. It indicate in which agora the GNode is
	 * created.
	 * 
	 * @return initial agora id.
	 */
	public String getInitAgoraID() {
		return initAgoraID;
	}

	/**
	 * Set the initial agora id. It indicates in which agora the GNode is
	 * created.
	 * 
	 * @param initAgoraID
	 *            initial agora id.
	 */
	public void setInitAgoraID(String initAgoraID) {
		this.initAgoraID = initAgoraID;
	}

	/**
	 * Get the initiate user id. It indicates who create the GNode.
	 * 
	 * @return the initiate user id.
	 */
	public String getInitUserID() {
		return initUserID;
	}

	/**
	 * Set the initiate user id. It indicates who create the GNode.
	 * 
	 * @param initUserID
	 *            the initiate user id.
	 */
	public void setInitUserID(String initUserID) {
		this.initUserID = initUserID;
	}

	/**
	 * Get the ownerAgora's guid.
	 * 
	 * @return the ownerAgora's guid.
	 */
	public String getOwnerAgoraID() {
		return ownerAgoraID;
	}

	/**
	 * Set the ownerAgora's guid.
	 * 
	 * @param ownerAgoraID
	 *            the ownerAgora's guid.
	 */
	public void setOwnerAgoraID(String ownerAgoraID) {
		this.ownerAgoraID = ownerAgoraID;
	}

	/**
	 * get the DN of owner user.
	 * 
	 * @return the DN of owner user.
	 */
	public String getOwnerDN() {
		return ownerDN;
	}

	/**
	 * set the DN of owner user.
	 * 
	 * @param ownerDN
	 *            the DN of owner user.
	 */
	public void setOwnerDN(String ownerDN) {
		this.ownerDN = ownerDN;
	}

	/**
	 * get the guid of owner.
	 * 
	 * @return the guid of owner.
	 */
	public String getOwnerID() {
		return ownerID;
	}

	/**
	 * Set the guid of owner.
	 * 
	 * @param ownerID
	 *            the guid of owner.
	 */
	public void setOwnerID(String ownerID) {
		this.ownerID = ownerID;
	}

	/**
	 * Get home site ID. It indicates which site is responsible to the GNode.
	 * 
	 * @return home site ID
	 */
	public String getHomeSiteID() {
		return homeSiteID;
	}

	/**
	 * Set home site ID. It indicates which site is responsible to the GNode.
	 * 
	 * @param homeSiteID
	 *            home site ID.
	 */
	public void setHomeSiteID(String homeSiteID) {
		this.homeSiteID = homeSiteID;
	}

	/**
	 * Get the ID of the orginal GNode. If it's guid is equals to this, then
	 * itself is an orgin GNode. If it's not equal, it's point to the orgin
	 * GNode.
	 * 
	 * @return the GNode.
	 */
	public GNodeInfo getOriginGNodeInfo() {
		return originGNodeInfo;
	}

	/**
	 * Set the ID of the orginal GNode. If it's guid is equals to this, then
	 * itself is an orgin GNode. If it's not equal, it's point to the orgin
	 * GNode.
	 * 
	 * @param originGNodeInfo
	 *            the of GNode object.
	 */
	public void setOriginGNodeInfo(GNodeInfo originGNodeInfo) {
		this.originGNodeInfo = originGNodeInfo;
	}

	/**
	 * Clone an object.
	 * 
	 * @return the cloned object.
	 * @throws CloneNotSupportedException
	 *             Not support clone.
	 */
	public Object clone() throws CloneNotSupportedException {
		GNode newGNode = new GNode();
		try {
			// FIXME It's not deep copy!
			BeanUtils.copyProperties(newGNode, this);
			// clone the GNodeInfo.
			newGNode.setOriginGNodeInfo((GNodeInfo) originGNodeInfo.clone());
		} catch (Exception e) {
			throw new CloneNotSupportedException("Clone not support due to : "
					+ e);
		}

		return newGNode;
	}

	/**
	 * whether this GNode is a link GNode.
	 * 
	 * @return if is a link GNode, return true.
	 */
	public boolean isLink() {
		if (this.originGNodeInfo.getGuid() == null
				|| this.originGNodeInfo.getGuid().equals(guid)) {
			return false;
		}

		return true;
	}

	/**
	 * To get the string format of the GNode.
	 * 
	 * @return the String format of the GNode.
	 */
	public String toString() {
		return originGNodeInfo.getName() + "(" + guid + ")";
	}

	/**
	 * to validate a GNode.
	 * 
	 * @throws GosException
	 *             All kinds of invalidate exceptions.
	 */
	public void validate() throws GNodeException {
		// Simple validate.
		if (originGNodeInfo != null) {
			GNodeValidator.validateMaxLength(this.originGNodeInfo.getName(),
					GNodeConstants.NAMEMAXLEN);
			GNodeValidator.validateMaxLength(this.originGNodeInfo.getObj(),
					GNodeConstants.OBJMAXLEN);
			GNodeValidator.validateMaxLength(
					this.originGNodeInfo.getRControllerType(),
					GNodeConstants.RCONTROLLERTYPEMAXLEN);
			GNodeValidator.validateMaxLength(
					this.originGNodeInfo.getRControllerURL(),
					GNodeConstants.RCONTROLLERURLMAXLEN);
			GNodeValidator.validateMaxLength(this.originGNodeInfo.getExport(),
					GNodeConstants.EXPORTMAXLEN);
			GNodeValidator.validateMaxLength(
					this.originGNodeInfo.getDescription(),
					GNodeConstants.DESCMAXLEN);
		}

		GNodeValidator.validateMaxLength(this.ownerDN, GNodeConstants.DNMAXLEN);
	}
}
