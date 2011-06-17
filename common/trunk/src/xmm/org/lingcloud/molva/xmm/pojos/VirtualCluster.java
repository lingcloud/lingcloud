/*
 *  @(#)VirtualCluster.java  2010-5-27
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
import java.util.Iterator;

import org.lingcloud.molva.xmm.util.XmlUtil;
import org.lingcloud.molva.ocl.lease.Lease;
import org.lingcloud.molva.xmm.util.XMMConstants;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2009-9-15<br>
 * @author Xiaoyi Lu<br>
 */
public class VirtualCluster extends Lease {

	/**
	 * the serial ID.
	 */
	private static final long serialVersionUID = 8635928074327770992L;

	public static final String PARTITION_ID = "partitionId";

	/**
	 * the constructor.
	 */
	public VirtualCluster() {
		// To prohibit be set to other value. But can't prevent set value from
		// super class.
		init();
	}

	/**
	 * to construct a VirtualCluster from a right type of Lease.
	 * 
	 * @param gnode
	 *            the gnode object.
	 */
	public VirtualCluster(Lease lease) {
		super(lease);
		// FIXME the lease type should be determinated, but the lease controller
		// should be changable. It can support different kinds of virtual
		// cluster.
		if (lease.getType() == null
				|| !lease.getType().equals(
						XMMConstants.VIRTUAL_CLUSTER_TYPE)) {
			throw new RuntimeException(
					"The VirtualCluster's type should be setted as "
							+ XMMConstants.VIRTUAL_CLUSTER_TYPE + ".");
		}
	}

	private void init() {
		super.setType(XMMConstants.VIRTUAL_CLUSTER_TYPE);
		super.setAcl(XMMConstants.DEFAULT_ACL);
	}

	/**
	 * set the type of Lease. It must override the method that make sure the
	 * VirtualCluster has right type.
	 * 
	 * @see XMMConstants for more detailed info.
	 * @param type
	 *            the type of VirtualCluster.
	 */
	public void setType(int type) {
		// To prohibit be set to other value. But can't prevent set value from
		// super class.
		super.setType(XMMConstants.VIRTUAL_CLUSTER_TYPE);
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
	public VirtualCluster clone() throws CloneNotSupportedException {
		try {
			VirtualCluster vh = new VirtualCluster((Lease) super.clone());
			return vh;
		} catch (Exception e) {
			throw new CloneNotSupportedException("Clone failed due to " + e);
		}
	}

	/**
	 * convert this object to a Lease.
	 * 
	 * @return return the new created Lease object.
	 */
	public Lease toLease() {
		try {
			return (Lease) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(new Exception(
					"convert to Lease failed due to : " + e));
		}
	}

	public void setPartitionId(String parid) {
		this.getAdditionalTerms().put(PARTITION_ID, parid);
	}

	public String getPartitionId() {
		return this.getAdditionalTerms().get(PARTITION_ID);
	}

	public void setVirtualNetworkId(String vnid) {
		// bug fixed here, if vnid == null, then the hashmap will put "null" as
		// a valid string as the key.
		if (vnid == null || "".equals(vnid)) {
			return;
		}
		this.getAssetIdAndTypeMap().put(vnid, VirtualNetwork.class.getName());
	}

	public String getVirtualNetworkId() {
		HashMap<String, String> aitm = this.getAssetIdAndTypeMap();
		if (aitm.containsValue(VirtualNetwork.class.getName())) {
			Iterator<String> iterator = aitm.keySet().iterator();
			while (iterator.hasNext()) {
				String id = (String) iterator.next();
				String type = aitm.get(id);
				if (VirtualNetwork.class.getName().equals(type)) {
					return id;
				}
			}
		}
		return null;
	}

	/**
	 * Set Node IDs and Types.
	 * 
	 * @param nodeMap,
	 *            the key is the node id, the value is the class name of every
	 *            node.
	 */
	// TODO the value is class name of every node to support physical and
	// virtual node hybrid situation.
	public void setNodeIdsAndTypes(HashMap<String, String> nodeMap) {
		if (nodeMap == null || nodeMap.isEmpty()) {
			return;
		}
		if (nodeMap.containsKey("null")) {
			nodeMap.remove("null");
		}
		this.getAssetIdAndTypeMap().putAll(nodeMap);
	}

	/**
	 * Get Node IDs and Types.
	 * 
	 * @return nodeMap, the key is the node id, the value is the class name of
	 *         every node.
	 */
	public HashMap<String, String> getNodeIdsAndTypes() {
		HashMap<String, String> aitMap = this.getAssetIdAndTypeMap();
		if (aitMap == null || aitMap.isEmpty()) {
			return aitMap;
		}
		HashMap<String, String> nodeMap = new HashMap<String, String>();
		Iterator<String> it = aitMap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			String type = aitMap.get(key);
			if (PhysicalNode.class.getName().equals(type)
					|| VirtualNode.class.getName().equals(type)) {
				nodeMap.put(key, type);
			}
		}
		return nodeMap;
	}
	
	public String getHeadNode() {
		HashMap<String, NodeRequirement> map = this.getNodeRequirements();
		for (String it : map.keySet()) {
			if (map.get(it).isHeadNode()) {
				return it;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, NodeRequirement> getNodeRequirements() {
		String xml = this.getAdditionalTerms().get("nodeRequirements");
		if (xml == null || "".equals(xml)) {
			return null;
		}
		return (HashMap<String, NodeRequirement>) XmlUtil.fromXml(xml);
	}

	/**
	 * 
	 * @param nrmap :
	 *            the key is the node's ip address, the value is its
	 *            requirement. when the key is '*', it means all nodes have the
	 *            same requirement. So this map has one record at least.
	 */
	public void setNodeRequirements(HashMap<String, NodeRequirement> nrmap) {
		if (nrmap == null || nrmap.isEmpty()) {
			return;
		}
		if (nrmap.containsKey("null")) {
			nrmap.remove("null");
		}
		String nrmapxml = XmlUtil.toXml(nrmap);
		this.getAdditionalTerms().put("nodeRequirements", nrmapxml);
	}
}
