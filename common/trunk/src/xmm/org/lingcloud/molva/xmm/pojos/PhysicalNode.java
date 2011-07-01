/*
 *  @(#)PhysicalNode.java  2010-5-27
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

package org.lingcloud.molva.xmm.pojos;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.xmm.util.XMMConstants;
import org.lingcloud.molva.xmm.util.XMMUtil;
import org.w3c.dom.Document;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-30<br>
 * @author Xiaoyi Lu<br>
 */
public class PhysicalNode extends Node {
	/**
	 * the serial ID.
	 */
	private static final long serialVersionUID = -905527148043784165L;

	/**
	 * The default constructor.
	 * 
	 */
	public PhysicalNode() {
		super();
		init();
	}

	/**
	 * to construct a PhysicalNode from a right type of Asset.
	 * 
	 * @param Asset
	 *            the asset object.
	 */
	public PhysicalNode(Asset asset) {
		super(asset);
		if (asset.getType() == null
				|| !asset.getType().equals(XMMConstants.PHYSICAL_NODE_TYPE)) {
			throw new RuntimeException(
					"The PhysicalNode's type should be setted as "
							+ XMMConstants.PHYSICAL_NODE_TYPE + ".");
		}
	}

	private void init() {
		super.setType(XMMConstants.PHYSICAL_NODE_TYPE);
	}

	/**
	 * set the type of PhysicalNode. It must override the method that make sure
	 * the PhysicalNode has right type.
	 * 
	 * @see XMMConstants for more detailed info.
	 * @param type
	 *            the type of PhysicalNode.
	 */
	public void setType(String type) {
		// To prohibit be set to other value. But can't prevent set value from
		// super class.
		super.setType(XMMConstants.PHYSICAL_NODE_TYPE);
	}

	public String getType() {
		return super.getType();
	}

	/**
	 * Clone an object.
	 * 
	 * @return the cloned object.
	 * @throws CloneNotSupportedException
	 *             Not support clone.
	 */
	public PhysicalNode clone() throws CloneNotSupportedException {
		try {
			PhysicalNode pn = new PhysicalNode((Node) super.clone());
			return pn;
		} catch (Exception e) {
			throw new CloneNotSupportedException("Clone failed due to " + e);
		}
	}

	public String getApplianceId() {
		return (String) this.getAttributes().get("ApplianceId");
	}

	public void setApplianceId(String appliance) {
		this.getAttributes().put("ApplianceId", appliance);
	}

	public String getTransferway() {
		return (String) this.getAttributes().get("transferway");
	}

	public void setTransferway(String transferway) {
		this.getAttributes().put("transferway", transferway);
	}

	public int getRunningVms() {
		if (XMMUtil.isBlankOrNull((String) this.getAttributes().get(
				"runningVms"))) {
			return 0;
		}
		return Integer.parseInt((String) this.getAttributes()
				.get("runningVms"));
	}

	public void setRunningVms(int runningVms) {
		this.getAttributes().put("runningVms", "" + runningVms);
	}

	public boolean getVmProvisonerTag() {
		if (XMMUtil.isBlankOrNull((String) this.getAttributes().get(
				"vmProvisonerTag"))) {
			return true;
		}
		return Boolean.parseBoolean((String) this
				.getAttributes().get("vmProvisonerTag"));
	}

	public void setVmProvisonerTag(boolean isVmProvisoner) {
		this.getAttributes().put("vmProvisonerTag", "" + isVmProvisoner);
	}

	public void setRedeployTagInCreate(boolean isRedeploy) {
		this.getAttributes().put("isRedeployInCreate", "" + isRedeploy);
	}

	public boolean getRedeployTagInCreate() {
		boolean tag = true;
		try {
			tag = Boolean.valueOf(this.getAttributes()
					.get("isRedeployInCreate"));
		} catch (Exception e) {
			return tag;
		}
		return tag;
	}

	/*
	 * Support opennebula hostinfo structure.
	 */
	public void setHostInfo(String info) {
		this.getAttributes().put(XMMConstants.HOSTINFO, info);
	}

	public String getHostInfo() {
		return this.getAttributes().get(XMMConstants.HOSTINFO);
	}
	
	public String getHostID() {
		String result = null;

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(this.getHostInfo()
					.getBytes()));
			org.w3c.dom.Node xml = doc.getDocumentElement();
			result = xpath.evaluate("/HOST/ID".toUpperCase(), xml);
		} catch (Exception e) {
		}

		return result;
	}
	
	protected static XPath xpath ;
	static {
		XPathFactory factory = XPathFactory.newInstance();
		xpath = factory.newXPath();
	}
}
