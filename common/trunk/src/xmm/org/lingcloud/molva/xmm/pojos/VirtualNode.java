/*
 *  @(#)VirtualNode.java  2010-5-27
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

import java.util.HashMap;
import java.util.Vector;

import org.lingcloud.molva.xmm.util.XmlUtil;
import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.xmm.util.XMMConstants;
import org.lingcloud.molva.xmm.vmc.one.VmTemplate;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-30<br>
 * @author Xiaoyi Lu<br>
 */
public class VirtualNode extends Node {
	/**
	 * the serial ID.
	 */
	private static final long serialVersionUID = 6692094600108137368L;

	public static final String APPLIANCE_ID = "applianceId";

	public static final String APPLIANCE_FILE_NAMES = "applianceFileNames";

	public static final String BRIDGE = "bridge";

	public static final String VMTEMPLATE = "vmTemplate";

	public static final String PARENT_PHYSICALNODE_NAME = 
		"parentPhysialNodeName";

	public static final String VNC_PORT_IN_PARENT_PHYSICALNODE = 
		"vncPortInParentPhysialNode";

	public static final String BOOTLOADER = "bootLoader";

	public static final String VM_DEPLOY_POLICER = "vmDeployPolicer";

	public static final String VM_DEPLOY_SCHEDULE_RESULT = 
		"vmDeployScheduleResult";

	public static final String APPLIANCE_NAME = "applianceName";

	// FIXME now left the disk info handled by virtual appliance.

	/**
	 * The default constructor.
	 * 
	 */
	public VirtualNode() {
		init();
	}

	/**
	 * to construct a VirtualNode from a right type of Asset.
	 * 
	 * @param Asset
	 *            the asset object.
	 */
	public VirtualNode(Asset asset) {
		super(asset);
		if (asset.getType() == null
				|| !asset.getType().equals(XMMConstants.VIRTUAL_NODE_TYPE)) {
			throw new RuntimeException(
					"The VirtualNode's type should be setted as "
							+ XMMConstants.VIRTUAL_NODE_TYPE + ".");
		}
	}

	private void init() {
		super.setType(XMMConstants.VIRTUAL_NODE_TYPE);
	}

	/**
	 * set the type of VirtualNode. It must override the method that make sure
	 * the VirtualNode has right type.
	 * 
	 * @see XMMConstants for more detailed info.
	 * @param type
	 *            the type of VirtualNode.
	 */
	public void setType(String type) {
		// To prohibit be set to other value. But can't prevent set value from
		// super class.
		super.setType(XMMConstants.VIRTUAL_NODE_TYPE);
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
	public VirtualNode clone() throws CloneNotSupportedException {
		try {
			VirtualNode vnode = new VirtualNode((Node) super.clone());
			return vnode;
		} catch (Exception e) {
			throw new CloneNotSupportedException("Clone failed due to " + e);
		}
	}

	public String getVmDeployPolicer() {
		return (String) this.getAttributes().get(VirtualNode.VM_DEPLOY_POLICER);
	}

	public void setVmDeployPolicer(String vmDeployPolicer) {
		this.getAttributes()
				.put(VirtualNode.VM_DEPLOY_POLICER, vmDeployPolicer);
	}

	public void setVmDeployScheduleResult(
			HashMap<String, Vector<String>> vmDeployParams) {
		this.getAttributes().put(VirtualNode.VM_DEPLOY_SCHEDULE_RESULT,
				XmlUtil.toXml(vmDeployParams));
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Vector<String>> getVmDeployScheduleResult() {
		String xml = this.getAttributes().get(
				VirtualNode.VM_DEPLOY_SCHEDULE_RESULT);
		if (xml == null || "".equals(xml)) {
			return null;
		}
		return (HashMap<String, Vector<String>>) XmlUtil.fromXml(xml);
	}

	public String getApplianceId() {
		return (String) this.getAttributes().get(VirtualNode.APPLIANCE_ID);
	}

	public void setApplianceId(String appliance) {
		this.getAttributes().put(VirtualNode.APPLIANCE_ID, appliance);
	}

	public void setApplianceFileNames(String[] location) {
		if (location == null || location.length <= 0) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < location.length; i++) {
			sb.append(location[i]);
			if (i < location.length - 1) {
				sb.append(",");
			}
		}
		this.getAttributes().put(VirtualNode.APPLIANCE_FILE_NAMES,
				sb.toString());
	}

	public String[] getApplianceFileNames() {
		String locations = (String) this.getAttributes().get(
				VirtualNode.APPLIANCE_FILE_NAMES);
		if (locations == null || "".equals(locations)) {
			return null;
		}
		return locations.split(",");
	}

	public String getApplianceName() {
		return (String) this.getAttributes().get(VirtualNode.APPLIANCE_NAME);
	}

	public void setApplianceName(String an) {
		this.getAttributes().put(VirtualNode.APPLIANCE_NAME, an);
	}

	public void setBridge(String bridge) {
		this.getAttributes().put(BRIDGE, bridge);
	}

	public String getBridge() {
		String bridge = this.getAttributes().get(BRIDGE);
		return bridge;
	}

	public void setVmTemplate(VmTemplate vmt) {
		String vmtstr = XmlUtil.toXml(vmt);
		this.getAttributes().put(VMTEMPLATE, vmtstr);
	}

	public VmTemplate getVmTemplate() {
		String vmtstr = this.getAttributes().get(VMTEMPLATE);
		return (VmTemplate) XmlUtil.fromXml(vmtstr);
	}

	public String getParentPhysialNodeName() {
		return this.getAttributes().get(VirtualNode.PARENT_PHYSICALNODE_NAME);
	}

	public void setParentPhysialNodeName(String name) {
		this.getAttributes().put(VirtualNode.PARENT_PHYSICALNODE_NAME, name);
	}

	public void setVncPortInParentPhysialNode(String portstr) {
		this.getAttributes().put(VNC_PORT_IN_PARENT_PHYSICALNODE, portstr);
	}

	public String getVncPortInParentPhysialNode() {
		return this.getAttributes().get(VNC_PORT_IN_PARENT_PHYSICALNODE);
	}

	public String getBootLoader() {
		return this.getAttributes().get(BOOTLOADER);
	}

	public void setBootLoader(String loader) {
		this.getAttributes().put(BOOTLOADER, loader);
	}

	/*
	 * Support opennebula info to be save.
	 */
	public void setVmInfo(String info) {
		this.getAttributes().put(XMMConstants.VMINFO, info);
	}

	public String getVmInfo() {
		return this.getAttributes().get(XMMConstants.VMINFO);
	}

}
