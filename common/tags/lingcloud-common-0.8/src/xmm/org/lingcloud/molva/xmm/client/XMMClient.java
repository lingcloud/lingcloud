/*
 *  @(#)XMMClient.java  2010-5-27
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

package org.lingcloud.molva.xmm.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.lingcloud.molva.xmm.pojos.NodeRequirement;
import org.lingcloud.molva.xmm.pojos.Partition;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.pojos.VirtualCluster;
import org.lingcloud.molva.xmm.pojos.VirtualNetwork;
import org.lingcloud.molva.xmm.pojos.VirtualNode;
import org.lingcloud.molva.xmm.util.XMMException;
import org.lingcloud.molva.xmm.util.XMMUtil;
import org.lingcloud.molva.xmm.util.XmlUtil;
import org.lingcloud.molva.ocl.util.ParaChecker;

/**
 * <strong>Purpose:</strong><br>
 * Client class for other component to invoke,
 * including partition, cluster, physical host, 
 * virtual host and so on.  
 * @version 1.0.1 2009-10-6<br>
 * @author Xiaoyi Lu<br>
 * @email luxiaoyi@software.ict.ac.cn
 */
public class XMMClient {

	/**
	 * XMM service Url.
	 */
	private String xmmServerUrl;

	/**
	 * Default constructor.
	 * Get the xmmServerUrl from default conf file.
	 */
	public XMMClient() {
		xmmServerUrl = XMMUtil.getMolvaServiceUrl(); 
	}
	/**
	 * To construct an AgoraClient.
	 * 
	 * @param serverUrl
	 *            the naming URL of the GOS site. The specified GOS site will
	 *            serve as the entry point of the grid.
	 * @throws XMMException
	 *             All kinds of exceptions.
	 */
	public XMMClient(String serverUrl) throws XMMException {
		xmmServerUrl = XMMUtil.formVirtualClusterServiceUrl(serverUrl);
		if (xmmServerUrl == null || xmmServerUrl.equals("")) {
			xmmServerUrl = XMMUtil.getMolvaServiceUrl(); 
		}
	}

	/**
	 * Get server time.
	 * @return server current time.
	 * @throws XMMException
	 */
	public Date getServerCurrentTime() throws XMMException {
		String str = (String) XMMUtil.callService(xmmServerUrl,
				"getServerCurrentTime", new Object[] {});
		
		return (Date) XmlUtil.fromXml(str);
	}

	/**
	 * Start virtual cluster
	 * @param vcid
	 * 			the virtual cluster id
	 * @throws XMMException
	 */
	public void startVirtualCluster(String vcid) throws XMMException {
		XMMUtil.callService(xmmServerUrl, "startVirtualCluster",
				new Object[] { vcid });
		return;
	}

	/**
	 * Stop a Virtual Cluster.
	 * @param vcid
	 * 		virtual cluster id.
	 * @throws XMMException
	 */
	public void stopVirtualCluster(String vcid) throws XMMException {
		XMMUtil.callService(xmmServerUrl, "stopVirtualCluster",
				new Object[] { vcid });
		return;
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
	public Partition createPartition(String name, String pController,
			HashMap<String, String> attributes, String desc)
			throws XMMException {
		String attr = XmlUtil.toXml(attributes);
		String parstr = (String) XMMUtil.callService(xmmServerUrl,
				"createPartition",
				new Object[] { name, pController, attr, desc });
		return (Partition) XmlUtil.fromXml(parstr);
	}

	/**
	 * Destroy a Partition.
	 * @param parGuid
	 * 			Partition global id.
	 * @throws XMMException
	 */
	public void destroyPartition(String parGuid) throws XMMException {
		try {
			ParaChecker.checkGuidFormat(parGuid, "Partition Guid");
		}catch (Exception e) {
			throw new XMMException(e.getMessage());
		}
		
		XMMUtil.callService(xmmServerUrl, "destroyPartition",
				new Object[] { parGuid });
	}

	/**
	 * View a Partition, get a information from database.
	 * @param partitionId
	 * 			the partition id.
	 * @return 
	 * 			a instance of the partition.
	 * @throws XMMException
	 */
	public Partition viewPartition(String partitionId) throws XMMException {
		String parstr = (String) XMMUtil.callService(xmmServerUrl,
				"viewPartition", new Object[] { partitionId });
		return (Partition) XmlUtil.fromXml(parstr);
	}

	/**
	 * Update a virtual cluster information
	 * mainly meta information
	 * @param vcid
	 * 			the virtual cluster id
	 * @param newvc
	 * 			the new virtual cluster instance.
	 * @return
	 * 			the updated virtual cluster.
	 * @throws XMMException
	 */
	public VirtualCluster updateVirtualClusterInfo(String vcid,
			VirtualCluster newvc) throws XMMException {
		try {
			ParaChecker.checkGuidFormat(vcid, "VirtualCluster Guid");
		}catch (Exception e) {
			throw new XMMException(e.getMessage());
		}
		
		if (newvc == null) {
			throw new XMMException("The input virtualcluster is null.");
		}

		String ostr = XmlUtil.toXml(newvc);
		String str = (String) XMMUtil.callService(xmmServerUrl,
				"updateVirtualClusterInfo", new Object[] { vcid, ostr });
		return (VirtualCluster) XmlUtil.fromXml(str);
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
	public VirtualNode updateVirtualNodeInfo(String vnid, VirtualNode newvn)
			throws XMMException {
		try {
			ParaChecker.checkGuidFormat(vnid, "VirtualNode Guid");
		}catch (Exception e) {
			throw new XMMException(e.getMessage());
		}
		
		if (newvn == null) {
			throw new XMMException("The input VirtualNode is null.");
		}
		String ostr = XmlUtil.toXml(newvn);
		String str = (String) XMMUtil.callService(xmmServerUrl,
				"updateVirtualNodeInfo", new Object[] { vnid, ostr });
		return (VirtualNode) XmlUtil.fromXml(str);
	}

	/**
	 * Update a virtual network,
	 * mainly the meta information
	 * @param vnid
	 * 			the virtual network id
	 * @param newvn
	 * 			the new virtual network instance.
	 * @return
	 * 			the updated virtual network.
	 * @throws XMMException
	 */
	public VirtualNetwork updateVirtualNetworkInfo(String vnid,
			VirtualNetwork newvn) throws XMMException {
		try {
			ParaChecker.checkGuidFormat(vnid, "VirtualNetwork Guid");
		}catch (Exception e) {
			throw new XMMException(e.getMessage());
		}
		
		if (newvn == null) {
			throw new XMMException("The input VirtualNetwork is null.");
		}
		String ostr = XmlUtil.toXml(newvn);
		String str = (String) XMMUtil.callService(xmmServerUrl,
				"updateVirtualNetworkInfo", new Object[] { vnid, ostr });
		return (VirtualNetwork) XmlUtil.fromXml(str);
	}

	/**
	 * Update a Partition,
	 * mainly the meta information
	 * @param parid
	 * 			the partition id.
	 * @param newpar
	 * 			the new partition instance.
	 * @return
	 * 			the updated partition instance.
	 * @throws XMMException
	 */
	public Partition updatePartitionInfo(String parid, Partition newpar)
			throws XMMException {
		try {
			ParaChecker.checkGuidFormat(parid, "Partition Guid");
		}catch (Exception e) {
			throw new XMMException(e.getMessage());
		}
		
		if (newpar == null) {
			throw new XMMException("The input Partition is null.");
		}
		String ostr = XmlUtil.toXml(newpar);
		String str = (String) XMMUtil.callService(xmmServerUrl,
				"updatePartitionInfo", new Object[] { parid, ostr });
		return (Partition) XmlUtil.fromXml(str);
	}

	/**
	 * Update a physical node information
	 * mainly the meta information
	 * @param pnid
	 * 			the physical node id
	 * @param newpn
	 * 			the new physical node instance.
	 * @return
	 * 			the updated physical node instance.
	 * @throws XMMException
	 */
	public PhysicalNode updatePhysicalNodeInfo(String pnid, PhysicalNode newpn)
			throws XMMException {
		try {
			ParaChecker.checkGuidFormat(pnid, "PhysicalNode Guid");
		}catch (Exception e) {
			throw new XMMException(e.getMessage());
		}
		
		if (newpn == null) {
			throw new XMMException("The input PhysicalNode is null.");
		}

		String ostr = XmlUtil.toXml(newpn);
		String str = (String) XMMUtil.callService(xmmServerUrl,
				"updatePhysicalNodeInfo", new Object[] { pnid, ostr });
		return (PhysicalNode) XmlUtil.fromXml(str);
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
	public PhysicalNode refreshPhysicalNode(String pnId) throws XMMException {
		String pnstr = (String) XMMUtil.callService(xmmServerUrl,
				"refreshPhysicalNode", new Object[] { pnId });
		return (PhysicalNode) XmlUtil.fromXml(pnstr);
	}

	/**
	 * Remove a physical node from the system.
	 * @param pnGuid
	 * 			the physical node global id
	 * @return
	 * 			the removed physical node
	 * @throws XMMException
	 */
	public PhysicalNode removePhysicalNode(String pnGuid) throws XMMException {
		String pnstr = (String) XMMUtil.callService(xmmServerUrl,
				"removePhysicalNode", new Object[] { pnGuid });
		return (PhysicalNode) XmlUtil.fromXml(pnstr);
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
	public PhysicalNode addPhysicalNode(String partitionId, String privateIp,
			String publicIp, String pnController, boolean isRedeploy,
			HashMap<String, String> attributes, String desc)
			throws XMMException {
		String attr = XmlUtil.toXml(attributes);
		String pnstr = (String) XMMUtil.callService(xmmServerUrl,
				"addPhysicalNode", new Object[] { partitionId, privateIp,
						publicIp, pnController, isRedeploy, attr, desc });
		return (PhysicalNode) XmlUtil.fromXml(pnstr);
	}

	/**
	 * List all partitions
	 * @return
	 * 			the list of all partitions
	 * @throws XMMException
	 */
	public List<Partition> listAllPartition() throws XMMException {

		String parsstr = (String) XMMUtil.callService(xmmServerUrl,
				"listAllPartition", null);
		return (List<Partition>) XmlUtil.fromXml(parsstr);
	}

	/**
	 * List a kind of partitions
	 * @param pController
	 * 			the partition operate controller
	 * @return
	 * 			the kind of Partition List
	 * @throws XMMException
	 */
	public List<Partition> listPartition(String pController)
			throws XMMException {

		String parsstr = (String) XMMUtil.callService(xmmServerUrl,
				"listPartition", new Object[] { pController });
		return (List<Partition>) XmlUtil.fromXml(parsstr);
	}

	/**
	 * List all physical nodes in a partition
	 * @param parid
	 * 			the partition id
	 * @return
	 * 			all physical nodes in the partition
	 * @throws XMMException
	 */
	public List<PhysicalNode> listPhysicalNodeInPartition(String parid)
			throws XMMException {
		String parsstr = (String) XMMUtil.callService(xmmServerUrl,
				"listPhysicalNode", new Object[] { parid });
		return (List<PhysicalNode>) XmlUtil.fromXml(parsstr);
	}

	/**
	 * List all virtual node in a partition
	 * @param parid
	 * 			the partition id
	 * @return
	 * 			All virtual nodes in the partition
	 * @throws XMMException
	 */
	public List<VirtualNode> listVirtualNodeInPartition(String parid)
			throws XMMException {
		String[] fields = new String[] { "assetLeaserId" };
		String[] operators = new String[] { "=" };
		Object[] values = new Object[] { parid };
		return this.searchVirtualNode(fields, operators, values);
	}

	/**
	 * List all physical node in a virtual cluster
	 * @param vcid
	 * 			the virtual cluster id
	 * @return
	 * 			all the physical nodes in the partition 
	 * @throws XMMException
	 */
	public List<PhysicalNode> listPhysicalNodeInVirtualCluster(String vcid)
			throws XMMException {
		String[] fields = new String[] { "leaseId" };
		String[] operators = new String[] { "=" };
		Object[] values = new Object[] { vcid };
		return this.searchPhysicalNode(fields, operators, values);
	}

	/**
	 * List all virtual node in a virtual cluster
	 * @param vcid
	 * 			the virtual cluster id
	 * @return
	 * 			all the virtual nodes in the virtual cluster
	 * @throws XMMException
	 */
	public List<VirtualNode> listVirtualNodeInVirtualCluster(String vcid)
			throws XMMException {
		String[] fields = new String[] { "leaseId" };
		String[] operators = new String[] { "=" };
		Object[] values = new Object[] { vcid };
		return this.searchVirtualNode(fields, operators, values);
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
	public VirtualCluster createVirtualCluster(String parid, String name,
			String nodeMatchMaker, String tenantId, String vnid,
			HashMap<String, NodeRequirement> nrmap, Date effectiveTime,
			long duration, Date expireTime, HashMap<String, String> attributes,
			String desc) throws XMMException {
		String vcstr = (String) XMMUtil.callService(xmmServerUrl,
				"createVirtualCluster", new Object[] { parid, name,
						nodeMatchMaker, tenantId, vnid, XmlUtil.toXml(nrmap),
						XmlUtil.toXml(effectiveTime), duration,
						XmlUtil.toXml(expireTime), XmlUtil.toXml(attributes),
						desc });
		return (VirtualCluster) XmlUtil.fromXml(vcstr);
	}

	/**
	 * Destroy a virtual cluster,
	 * the virtual cluster muster be empty.
	 * @param vcguid
	 * 			the virtual cluster id
	 * @throws XMMException
	 */
	public void destroyVirtualCluster(String vcguid) throws XMMException {
		XMMUtil.callService(xmmServerUrl, "destroyVirtualCluster",
				new Object[] { vcguid });
		return;
	}

	/**
	 * Get a virtual cluster instance
	 * @param guid
	 * 			the global id of the virtual cluster
	 * @return
	 * 			the virtual cluster instance
	 * @throws XMMException
	 */
	public VirtualCluster viewVirtualCluster(String guid) throws XMMException {
		String vcstr = (String) XMMUtil.callService(xmmServerUrl,
				"viewVirtualCluster", new Object[] { guid });
		return (VirtualCluster) XmlUtil.fromXml(vcstr);
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
	public List<VirtualCluster> searchVirtualCluster(String[] searchConditions,
			String[] operators, Object[] values) throws XMMException {
		String vcliststr = (String) XMMUtil.callService(xmmServerUrl,
				"searchVirtualCluster", new Object[] { searchConditions,
						operators, values });
		return (List<VirtualCluster>) XmlUtil.fromXml(vcliststr);
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
	public List<VirtualNetwork> searchVirtualNetwork(String[] searchConditions,
			String[] operators, Object[] values) throws XMMException {
		String vnliststr = (String) XMMUtil.callService(xmmServerUrl,
				"searchVirtualNetwork", new Object[] { searchConditions,
						operators, values });
		return (List<VirtualNetwork>) XmlUtil.fromXml(vnliststr);
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
	public List<PhysicalNode> searchPhysicalNode(String[] searchConditions,
			String[] operators, Object[] values) throws XMMException {
		String pnliststr = (String) XMMUtil.callService(xmmServerUrl,
				"searchPhysicalNode", new Object[] { searchConditions,
						operators, values });
		return (List<PhysicalNode>) XmlUtil.fromXml(pnliststr);
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
	public List<VirtualNode> searchVirtualNode(String[] searchConditions,
			String[] operators, Object[] values) throws XMMException {
		String vnliststr = (String) XMMUtil.callService(xmmServerUrl,
				"searchVirtualNode", new Object[] { searchConditions,
						operators, values });
		return (List<VirtualNode>) XmlUtil.fromXml(vnliststr);
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
	public VirtualCluster refreshVirtualCluster(String vcid)
			throws XMMException {
		String vcstr = (String) XMMUtil.callService(xmmServerUrl,
				"refreshVirtualCluster", new Object[] { vcid });
		VirtualCluster vcluster = (VirtualCluster) XmlUtil.fromXml(vcstr);
		return vcluster;
	}

	/**
	 * List all virtual cluster in a partition
	 * @param parid
	 * 			the partition id
	 * @return
	 * 			the virtual clusters in the partition
	 * @throws XMMException
	 */
	public List<VirtualCluster> listVirtualCluster(String parid)
			throws XMMException {
		String[] fields = new String[] { "additionalTerms['"
				+ VirtualCluster.PARTITION_ID + "']" };
		String[] operators = new String[] { "=" };
		Object[] values = new Object[] { parid };
		return this.searchVirtualCluster(fields, operators, values);
	}

	/**
	 * List all networks in a partition
	 * @param parid
	 * 			the partition id
	 * @return
	 * 			the networks in the partition
	 * @throws XMMException
	 */
	public List<VirtualNetwork> listVirtualNetwork(String parid)
			throws XMMException {
		String[] fields = new String[] { "assetLeaserId" };
		String[] operators = new String[] { "=" };
		Object[] values = new Object[] { parid };
		return this.searchVirtualNetwork(fields, operators, values);
	}

	/**
	 * Create the virtual network for a partition
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
	public VirtualNetwork createVirtualNetwork(String partitionId, String name,
			String vnController, int netSize, String headNodeIp,
			String[] otherNodeIps, HashMap<String, String> attributes,
			String desc) throws XMMException {
		String vnstr = (String) XMMUtil.callService(xmmServerUrl,
				"createVirtualNetwork", new Object[] { partitionId, name,
						vnController, netSize, headNodeIp, otherNodeIps,
						XmlUtil.toXml(attributes), desc });
		return (VirtualNetwork) XmlUtil.fromXml(vnstr);
	}

	/**
	 * Destroy a virtual network
	 * @param vnguid
	 * 			the virtual network id
	 * @throws XMMException
	 */
	public void destroyVirtualNetwork(String vnguid) throws XMMException {
		XMMUtil.callService(xmmServerUrl, "destroyVirtualNetwork",
				new Object[] { vnguid });
	}

	/**
	 * Get a virtual network information
	 * @param guid
	 * 			the global id of the network
	 * @return
	 * 			the virtual network
	 * @throws XMMException
	 */
	public VirtualNetwork viewVirtualNetwork(String guid) throws XMMException {
		String vnstr = (String) XMMUtil.callService(xmmServerUrl,
				"viewVirtualNetwork", new Object[] { guid });
		return (VirtualNetwork) XmlUtil.fromXml(vnstr);
	}

	/**
	 * Set User Quota.
	 * @param userId
	 * @param quotaKey
	 * @param quotaValue
	 * @throws XMMException
	 */
	@Deprecated
	public void setUserQuota(String userId, String quotaKey, int quotaValue)
			throws XMMException {
		XMMUtil.validateQuotaKey(quotaKey);
		XMMUtil.validateQuotaValue(quotaKey, quotaValue);
	}

	/**
	 * Get user quota.
	 * @param userId
	 * @param quotaKey
	 * @return
	 * @throws XMMException
	 */
	@Deprecated
	private int getUserQuota(String userId, String quotaKey) 
					throws XMMException {		
		return 0;
	}

	/**
	 * Get user quota.
	 * @param quotaKey
	 * @return
	 * @throws XMMException
	 */
	@Deprecated
	public int getUserQuota(String quotaKey) throws XMMException {
		return getUserQuota("", quotaKey);
	}

	/**
	 * Set user preference.
	 * @param userId
	 * @param preferKey
	 * @param preferValue
	 * @throws XMMException
	 */
	@Deprecated
	public void setUserPreference(String userId, String preferKey,
			int preferValue) throws XMMException {
		XMMUtil.validatePreferKey(preferKey);
		// FIXME if user's preference is -1, then it means no limited.
		XMMUtil.validatePreferValue(preferKey, preferValue);

	}

	/**
	 * Get user preference.
	 * @param userId
	 * @param preferKey
	 * @return
	 * @throws XMMException
	 */
	@Deprecated
	private int getUserPreference(String userId, String preferKey) 
					throws XMMException {

		throw new XMMException("getUserPreference");
		
	}

	/**
	 * Get user preference.
	 * @param preferKey
	 * @return
	 * @throws XMMException
	 */
	@Deprecated
	public int getUserPreference(String preferKey) throws XMMException {

		return getUserPreference("", preferKey);
	}

	/**
	 * Get virtual node instance.
	 * @param guid
	 * 			the global id of virtual node
	 * @return
	 * 			the virtual node
	 * @throws XMMException
	 */
	public VirtualNode viewVirtualNode(String guid) throws XMMException {
		try {
			ParaChecker.checkGuidFormat(guid, "virtual node guid");
		}catch (Exception e) {
			throw new XMMException(e.getMessage());
		}
		
		String vnstr = (String) XMMUtil.callService(xmmServerUrl,
				"viewVirtualNode", new Object[] { guid });
		return (VirtualNode) XmlUtil.fromXml(vnstr);
	}
	
	/**
	 * Get physical node instance.
	 * @param guid
	 * 			the global id of physical node
	 * @return
	 * 			the physical node
	 * @throws XMMException
	 */
	public PhysicalNode viewPhysicalNode(String guid) throws XMMException {
		try {
			ParaChecker.checkGuidFormat(guid, "physical node guid");
		}catch (Exception e) {
			throw new XMMException(e.getMessage());
		}
		
		String pnstr = (String) XMMUtil.callService(xmmServerUrl,
				"viewPhysicalNode", new Object[] { guid });
		return (PhysicalNode) XmlUtil.fromXml(pnstr);
	}
}
