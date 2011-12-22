/*
 *  @(#)XMMImplWrapper.java  2010-5-27
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

package org.lingcloud.molva.xmm.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.lingcloud.molva.ocl.util.ParaChecker;
import org.lingcloud.molva.xmm.util.XMMException;
import org.lingcloud.molva.xmm.util.XmlUtil;
import org.lingcloud.molva.xmm.pojos.NodeRequirement;
import org.lingcloud.molva.xmm.pojos.Partition;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.pojos.VirtualCluster;
import org.lingcloud.molva.xmm.pojos.VirtualNetwork;
import org.lingcloud.molva.xmm.pojos.VirtualNode;

/**
 * <strong>Purpose:</strong><br>
 * A Wrapper for XMMImpl class,
 * wrap the objects to xml string.
 * Support operations
 * for partitions, clusters, physical nodes, 
 * virtual nodes, networks and so on.
 * @version 1.0.1 2009-10-6<br>
 * @author Xiaoyi Lu<br>
 */
public class XMMImplWrapper {

	/**
	 * XMMImpl class instance.
	 */
	private XMMImpl impl = null;

	/**
	 * Default constructor.
	 * @throws Exception
	 */
	public XMMImplWrapper() throws Exception {
		impl = new XMMImpl();
	}

	/**
	 * Get server time.
	 * @return server current time.
	 * @throws XMMException
	 */
	public String getServerCurrentTime() {
		return XmlUtil.toXml(impl.getServerCurrentTime());
	}

	/**
	 * Create a Partition.
	 * @param name
	 * 			partition name.
	 * @param pController
	 * 			partition operate controller
	 * @param attributes
	 * 			partition attributes
	 * @param desc
	 * 			partition description.
	 * @return
	 * 			the created partition.
	 * @throws XMMException
	 */
	public String createPartition(String name, String pController,
			String attributes, String desc) throws Exception {
		@SuppressWarnings("unchecked")
		HashMap<String, String> attr = (HashMap<String, String>) XmlUtil
				.fromXml(attributes);
		Partition par = impl.createPartition(name, pController, attr, desc);
		return XmlUtil.toXml(par);
	}

	/**
	 * Destroy a Partition.
	 * @param parGuid
	 * 			Partition global id.
	 * @throws XMMException
	 */
	public void destroyPartition(String parGuid) throws Exception {
		impl.destroyPartition(parGuid);
	}

	/**
	 * View a Partition, get a information from database.
	 * @param partitionId
	 * 			the partition id.
	 * @return 
	 * 			a instance of the partition.
	 * @throws XMMException
	 */
	public String viewPartition(String partitionId) throws Exception {
		Partition par = impl.viewPartition(partitionId);
		return XmlUtil.toXml(par);
	}

	/**
	 * Refresh a physical node
	 * mainly the runtime information
	 * get the information and write to 
	 * the database.
	 * @param pnId
	 * 			the physical node id
	 * @return
	 * 			the refreshed physical node instance with new information
	 * @throws XMMException
	 */
	public String refreshPhysicalNode(String pnId) throws Exception {
		PhysicalNode pn = impl.refreshPhysicalNode(pnId);
		return XmlUtil.toXml(pn);
	}

	/**
	 * Remove a physical node from the system.
	 * @param pnGuid
	 * 			the physical node global id
	 * @return
	 * 			the removed physical node
	 * @throws XMMException
	 */
	public String removePhysicalNode(String pnGuid) throws Exception {
		PhysicalNode pn = impl.removePhysicalNode(pnGuid);
		return XmlUtil.toXml(pn);
	}

	/**
	 * Add a physical node to the system.
	 * The physical node must be testified,
	 * or will fail.
	 * @param partitionId
	 * 			the id of partition that the physical node will be added to
	 * @param privateIp
	 * 			private ip
	 * @param publicIp
	 * 			public ip
	 * @param pnController
	 * 			the physical node operate controller
	 * @param isRedeploy
	 * 			is redeployed or not
	 * @param attributes
	 * 			the physical node optional attributes
	 * @param desc
	 * 			the physical node's description
	 * @return 
	 * 			the added physical node instance.
	 * @throws XMMException
	 */
	public String addPhysicalNode(String partitionId, String privateIp,
			String publicIp, String pnController, boolean isRedeploy,
			String attributes, String desc) throws Exception {
		@SuppressWarnings("unchecked")
		HashMap<String, String> attr = (HashMap<String, String>) XmlUtil
				.fromXml(attributes);
		PhysicalNode pn = impl.addPhysicalNode(partitionId, privateIp,
				publicIp, pnController, isRedeploy, attr, desc);
		return XmlUtil.toXml(pn);
	}

	/**
	 * List all partitions.
	 * @return
	 * 			the list of all partitions
	 * @throws XMMException
	 */
	public String listAllPartition() throws Exception {
		return XmlUtil.toXml(impl.listAllPartition());
	}

	/**
	 * List a kind of partitions.
	 * @param pController
	 * 			the partition operate controller
	 * @return
	 * 			the kind of Partition List
	 * @throws XMMException
	 */
	public String listPartition(String pController) throws Exception {
		return XmlUtil.toXml(impl.listPartition(pController));
	}

	/**
	 * List all physical nodes in a partition.
	 * @param parid
	 * 			the partition id
	 * @return
	 * 			all physical nodes in the partition
	 * @throws XMMException
	 */
	public String listPhysicalNode(String pid) throws Exception {
		return XmlUtil.toXml(impl.listPhysicalNode(pid));
	}

	/**
	 * Create the virtual network for a partition.
	 * @param partitionId
	 * 			the partition id
	 * @param name
	 * 			the network name
	 * @param vnController
	 * 			the network operate controller
	 * @param netSize
	 * 			the size of network
	 * @param headNodeIp
	 * 			the head node ip
	 * @param otherNodeIps
	 * 			other ips
	 * @param attributes
	 * 			the optional attributes
	 * @param desc
	 * 			the description
	 * @return
	 * 			the created virtual network
	 * @throws XMMException
	 */
	public String createVirtualNetwork(String partitionId, String name,
			String vnController, int netSize, String headNodeIp,
			String[] otherNodeIps, String attributes, String desc)
			throws Exception {
		@SuppressWarnings("unchecked")
		HashMap<String, String> attr = (HashMap<String, String>) XmlUtil
				.fromXml(attributes);
		VirtualNetwork vn = impl.createVirtualNetwork(partitionId, name,
				vnController, netSize, headNodeIp, otherNodeIps, attr, desc);
		return XmlUtil.toXml(vn);
	}

	/**
	 * Destroy a virtual network.
	 * @param vnguid
	 * 			the virtual network id
	 * @throws XMMException
	 */
	public void destroyVirtualNetwork(String vnguid) throws Exception {
		impl.destroyVirtualNetwork(vnguid);
	}

	/**
	 * Get a virtual network information.
	 * @param guid
	 * 			the global id of the network
	 * @return
	 * 			the virtual network
	 * @throws XMMException
	 */
	public String viewVirtualNetwork(String vnid) throws Exception {
		VirtualNetwork vn = impl.viewVirtualNetwork(vnid);
		return XmlUtil.toXml(vn);
	}

	/**
	 * Get physical node instance.
	 * @param guid
	 * 			the global id of physical node
	 * @return
	 * 			the physical node
	 * @throws XMMException
	 */
	public String viewPhysicalNode(String guid) throws Exception {
		PhysicalNode pn = impl.viewPhysicalNode(guid);
		return XmlUtil.toXml(pn);
	}

	/**
	 * Get virtual node instance.
	 * @param guid
	 * 			the global id of virtual node
	 * @return
	 * 			the virtual node
	 * @throws XMMException
	 */
	public String viewVirtualNode(String guid) throws Exception {
		VirtualNode vn = impl.viewVirtualNode(guid);
		return XmlUtil.toXml(vn);
	}
	
	/**
	 * Refresh virtual node instance.
	 * @param guid
	 * 			the global id of virtual node
	 * @return
	 * 			the virtual node
	 * @throws Exception
	 */
	public String refreshVirtualNode(String guid) throws Exception {
		VirtualNode vn = impl.refreshVirtualNode(guid);
		return XmlUtil.toXml(vn);
	}
	
	/**
	 * Start the virtual node.
	 * @param guid the virtual node's guid
	 * @return the virtual node
	 * @throws Exception
	 */
	public String startVirtualNode(String guid) throws Exception {
		VirtualNode vn = impl.startVirtualNode(guid);
		return XmlUtil.toXml(vn);
	}
	
	/**
	 * Stop the virtual node.
	 * @param guid the virtual node's guid
	 * @return the virtual node
	 * @throws Exception
	 */
	public String stopVirtualNode(String guid) throws Exception {
		VirtualNode vn = impl.stopVirtualNode(guid);
		return XmlUtil.toXml(vn);
	}
	
	/**
	 * Boot the virtual node.
	 * @param guid the virtual node's guid
	 * @return the virtual node
	 * @throws Exception
	 */
	public String bootVirtualNode(String guid) throws Exception {
		VirtualNode vn = impl.bootVirtualNode(guid);
		return XmlUtil.toXml(vn);
	}
	
	/**
	 * Shutdown the virtual node.
	 * @param guid the virtual node's guid
	 * @return the virtual node
	 * @throws Exception
	 */
	public String shutdownVirtualNode(String guid) throws Exception {
		VirtualNode vn = impl.shutdownVirtualNode(guid);
		return XmlUtil.toXml(vn);
	}
	
	/**
	 * Migrate the virtual node, if it's running, will live-migrate.
	 * @param vNodeGuid the virtual node's guid
	 * @param pNodeGuid the new host's guid
	 * @return the virtual node
	 * @throws Exception
	 */
	public String migrateVirtualNode(String vNodeGuid, String pNodeGuid) throws Exception {
		VirtualNode vn = impl.migrateVirtualNode(vNodeGuid, pNodeGuid);
		return XmlUtil.toXml(vn);
	}

	/**
	 * Update a virtual cluster information.
	 * mainly meta information
	 * @param vcid
	 * 			the virtual cluster id
	 * @param newvc
	 * 			the new virtual cluster instance.
	 * @return
	 * 			the updated virtual cluster.
	 * @throws XMMException
	 */
	public String updateVirtualClusterInfo(String vcid, String newvcstr)
			throws Exception {
		VirtualCluster newvc = (VirtualCluster) XmlUtil.fromXml(newvcstr);
		return XmlUtil.toXml(impl.updateVirtualClusterInfo(vcid, newvc));
	}

	/**
	 * Update a virtual node information,
	 * mainly the meta information.
	 * @param vnid
	 * 			the virtual node id
	 * @param newvn
	 * 			the new virtual node instanse
	 * @return
	 * 			the updated virtual node.
	 * @throws XMMException
	 */
	public String updateVirtualNodeInfo(String vnid, String newvnstr)
			throws Exception {
		VirtualNode newvn = (VirtualNode) XmlUtil.fromXml(newvnstr);
		return XmlUtil.toXml(impl.updateVirtualNodeInfo(vnid, newvn));
	}

	/**
	 * Update a virtual network,
	 * mainly the meta information.
	 * @param vnid
	 * 			the virtual network id
	 * @param newvn
	 * 			the new virtual network instance.
	 * @return
	 * 			the updated virtual network.
	 * @throws XMMException
	 */
	public String updateVirtualNetworkInfo(String vnid, String newvnstr)
			throws Exception {
		VirtualNetwork newvn = (VirtualNetwork) XmlUtil.fromXml(newvnstr);
		return XmlUtil.toXml(impl.updateVirtualNetworkInfo(vnid, newvn));
	}

	/**
	 * Update a Partition,
	 * mainly the meta information.
	 * @param parid
	 * 			the partition id.
	 * @param newpar
	 * 			the new partition instance.
	 * @return
	 * 			the updated partition instance.
	 * @throws XMMException
	 */
	public String updatePartitionInfo(String parid, String newparstr)
			throws Exception {
		Partition newpar = (Partition) XmlUtil.fromXml(newparstr);
		return XmlUtil.toXml(impl.updatePartitionInfo(parid, newpar));
	}

	/**
	 * Update a physical node information
	 * mainly the meta information.
	 * @param pnid
	 * 			the physical node id
	 * @param newpn
	 * 			the new physical node instance.
	 * @return
	 * 			the updated physical node instance.
	 * @throws XMMException
	 */
	public String updatePhysicalNodeInfo(String pnid, String newpnstr)
			throws Exception {
		PhysicalNode newpn = (PhysicalNode) XmlUtil.fromXml(newpnstr);
		return XmlUtil.toXml(impl.updatePhysicalNodeInfo(pnid, newpn));
	}

	/**
	 * The method to create virtual cluster for phsical node or virtual node.
	 * 
	 * @param parid :
	 *            partition id. required parameter.
	 * @param name :
	 *            virtual cluster name. required parameter.
	 * @param assetMatchMaker :
	 *            asset match maker of virtual cluster. required parameter.
	 * 
	 * @tenantxId : the id of tenant. optional parameter.
	 * @param vnid :
	 *            virtual network id, it can be null or blank, then means the
	 *            virtual network generated by system automatically. if the vnid
	 *            is null or blank, the system will create a proper one
	 *            automatically. But if the nrmap contains the the key of
	 *            'ALL_NODE_SAME_REQUIREMENT_TAG', then the vnid is required.
	 *            conditional optional parameter.
	 * @param nrmap :
	 *            the key is the node's ip address, the value is its
	 *            requirement. when the key is 'ALL_NODE_SAME_REQUIREMENT_TAG',
	 *            it means all nodes have the same requirement. So this map has
	 *            one record at least. required parameter.
	 * @param effectiveTime :
	 *            the time of virtual cluster required to begin to run, but not
	 *            guarantee exactly. The gap between the expected time and the
	 *            real running time may gap in several seconds. optional
	 *            parameter.
	 * @param duration :
	 *            the expected running duration. its unit is ms. optional
	 *            parameter.
	 * @param expireTime :
	 *            the expected expire time, which means before this time, all of
	 *            the nodes in the virtual cluster should shutdown. it is also
	 *            hardly guaranteed. optional parameter.
	 * 
	 * There are four kinds of leases.
	 * 
	 * 1> BestEffort Lease : "this.effectiveTime == null && this.expireTime ==
	 * null && duration > 0"
	 * 
	 * 2> AR Lease : "this.effectiveTime != null && this.expireTime != null"
	 * 
	 * 3> Deadline Stoping Lease : "this.effectiveTime == null &&
	 * this.expireTime != null && duration > 0"
	 * 
	 * 4> Deadline Starting Lease : "this.effectiveTime != null &&
	 * this.expireTime == null && duration > 0"
	 * 
	 * @param attributes :
	 *            other attributes to be extended. optional parameter.
	 * @param desc :
	 *            the description of virtual cluster. optional parameter.
	 * @return
	 * @throws Exception
	 */
	public String createVirtualCluster(String parid, String name,
			String nodeMatchMaker, String tenantId, String vnid,
			String nrmapstr, String effectiveTimestr, long duration,
			String expireTimestr, String attributes, String desc)
			throws Exception {
		@SuppressWarnings("unchecked")
		HashMap<String, NodeRequirement> nrmap = 
			(HashMap<String, NodeRequirement>) XmlUtil.fromXml(nrmapstr);
		Date effectiveTime = (Date) XmlUtil.fromXml(effectiveTimestr);
		Date expireTime = (Date) XmlUtil.fromXml(expireTimestr);
		@SuppressWarnings("unchecked")
		HashMap<String, String> attr = (HashMap<String, String>) XmlUtil
				.fromXml(attributes);
		VirtualCluster vc = impl.createVirtualCluster(parid, name,
				nodeMatchMaker, tenantId, vnid, nrmap, effectiveTime, duration,
				expireTime, attr, desc);
		return XmlUtil.toXml(vc);
	}

	/**
	 * Start virtual cluster.
	 * @param vcid
	 * 			the virtual cluster id
	 * @throws XMMException
	 */
	public void startVirtualCluster(String vcid) throws Exception {
		impl.startVirtualCluster(vcid);
	}

	/**
	 * Stop a Virtual Cluster.
	 * @param vcid
	 * 		virtual cluster id.
	 * @throws XMMException
	 */
	public void stopVirtualCluster(String vcid) throws Exception {
		impl.stopVirtualCluster(vcid);
	}

	/**
	 * Destroy a virtual cluster,
	 * the virtual cluster muster be empty.
	 * @param vcguid
	 * 			the virtual cluster id
	 * @throws XMMException
	 */
	public void destroyVirtualCluster(String vcid) throws Exception {
		impl.destroyVirtualCluster(vcid);
	}

	/**
	 * Refresh a virtual cluster,
	 * mainly the runtime information.
	 * It will get the runtime information 
	 * and write to the database. 
	 * @param vcid
	 * 			the virtual cluster id
	 * @return
	 * 			the refreshed virtual cluster instance
	 * @throws XMMException
	 */
	public String refreshVirtualCluster(String vcid) throws Exception {
		VirtualCluster vc = impl.refreshVirtualCluster(vcid);
		return XmlUtil.toXml(vc);
	}

	/**
	 * Get a virtual cluster instance.
	 * @param guid
	 * 			the global id of the virtual cluster
	 * @return
	 * 			the virtual cluster instance
	 * @throws XMMException
	 */
	public String viewVirtualCluster(String vcid) throws Exception {
		VirtualCluster vc = impl.viewVirtualCluster(vcid);
		return XmlUtil.toXml(vc);
	}

	/**
	 * Search the virtual cluster 
	 * in some conditions.
	 * @param searchConditions
	 * 			search conditions, attributes
	 * @param operators
	 * 			the attributes operators
	 * @param values
	 * 			the values
	 * @return
	 * 			the search's results
	 * @throws XMMException
	 */
	public String searchVirtualCluster(String[] searchConditions,
			String[] operators, Object[] values) throws Exception {
		List<VirtualCluster> vclist = impl.searchVirtualCluster(
				searchConditions, operators, values);
		return XmlUtil.toXml(vclist);
	}

	/**
	 * Search the virtual Network 
	 * in some conditions.
	 * @param searchConditions
	 * 			search conditions, attributes
	 * @param operators
	 * 			the attributes operators
	 * @param values
	 * 			the values
	 * @return
	 * 			the search's results
	 * @throws XMMException
	 */
	public String searchVirtualNetwork(String[] searchConditions,
			String[] operators, Object[] values) throws Exception {
		List<VirtualNetwork> vnlist = impl.searchVirtualNetwork(
				searchConditions, operators, values);
		return XmlUtil.toXml(vnlist);
	}

	/**
	 * Search the physical node 
	 * in some conditions.
	 * @param searchConditions
	 * 			search conditions, attributes
	 * @param operators
	 * 			the attributes operators
	 * @param values
	 * 			the values
	 * @return
	 * 			the search's results
	 * @throws XMMException
	 */
	public String searchPhysicalNode(String[] searchConditions,
			String[] operators, Object[] values) throws Exception {
		List<PhysicalNode> pnlist = impl.searchPhysicalNode(searchConditions,
				operators, values);
		return XmlUtil.toXml(pnlist);
	}

	/**
	 * Search the virtual node 
	 * in some conditions.
	 * @param searchConditions
	 * 			search conditions, attributes
	 * @param operators
	 * 			the attributes operators
	 * @param values
	 * 			the values
	 * @return
	 * 			the search's results
	 * @throws XMMException
	 */
	public String searchVirtualNode(String[] searchConditions,
			String[] operators, Object[] values) throws Exception {
		List<VirtualNode> vnlist = impl.searchVirtualNode(searchConditions,
				operators, values);
		return XmlUtil.toXml(vnlist);
	}
	
	/**
	 * Start the physical node.
	 * @param pnGuid the physical node's guid
	 * @return the physical node
	 * @throws Exception
	 */
	public String startPhysicalNode(String pnGuid) throws Exception {
		PhysicalNode pn = impl.startPhysicalNode(pnGuid);
		return XmlUtil.toXml(pn);
	}
	
	/**
	 * Stop the physical node.
	 * @param pnGuid the physical node's guid
	 * @return the physical node
	 * @throws Exception
	 */
	public String stopPhysicalNode (String pnGuid) throws Exception {
		PhysicalNode pn = impl.stopPhysicalNode(pnGuid);
		return XmlUtil.toXml(pn);
	}

}
