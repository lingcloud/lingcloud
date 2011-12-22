/*
 *  @(#)XMMImpl.java  2010-5-27
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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.lingcloud.molva.ocl.util.TimeServerManager;
import org.lingcloud.molva.ocl.util.ParaChecker;
import org.lingcloud.molva.xmm.pojos.NodeRequirement;
import org.lingcloud.molva.xmm.pojos.Partition;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.pojos.VirtualCluster;
import org.lingcloud.molva.xmm.pojos.VirtualNetwork;
import org.lingcloud.molva.xmm.pojos.VirtualNode;
import org.lingcloud.molva.xmm.util.XMMException;
import org.lingcloud.molva.xmm.util.XMMUtil;

/**
 * <strong>Purpose:</strong><br>
 * XMMImpl class, implement the services.
 * Support operations
 * for partitions, clusters, physical nodes, 
 * virtual nodes, networks and so on.
 * @version 1.0.1 2009-9-18<br>
 * @author Xiaoyi Lu<br>
 */
public class XMMImpl {

	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(XMMImpl.class);

	private static PartitionManager parm = PartitionManager.getInstance();

	private static VirtualNetworkManager vnm = VirtualNetworkManager
			.getInstance();

	private static VirtualClusterManager vcm = VirtualClusterManager
			.getInstance();

	private static PartitionManager pm = PartitionManager.getInstance();


	/**
	 * Construct a XMMImpl use server url from config file.
	 * 
	 * @throws Exception
	 */
	public XMMImpl() throws Exception {
	}

	/**
	 * Get server time.
	 * @return server current time.
	 * @throws XMMException
	 */
	public Date getServerCurrentTime() {
		return TimeServerManager.getCurrentTime();
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
			throws Exception {
		XMMUtil.validateBlankOrNull(name, "Partition name");
		XMMUtil.validateBlankOrNull(pController, "Partition controller");
		if (desc == null || "".equals(desc)) {
			desc = "Partition (" + name + ") with the controller "
					+ pController;
		}
		
		try {
			return pm.createPartition(name.trim(), pController.trim(),
					attributes, desc);
		} catch (Exception e) {
			log.error("Failed to create a new partition with the name : "
					+ name);
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		} 
	}

	/**
	 * Destroy a Partition.
	 * @param parGuid
	 * 			Partition global id.
	 * @throws XMMException
	 */
	public Partition destroyPartition(String parGuid) throws Exception {
		ParaChecker.checkGuidFormat(parGuid, "partition guid");
		Partition par = null;

		try {
			par = pm.destroyPartition(parGuid.trim());
			log.info("Successfully remove a partition with the guid : "
					+ parGuid + " and with name : " + par.getName());
			return par;
		} catch (Exception e) {
			if (par != null) {
				log.error("Failed to remove a partition with the guid : "
						+ par.getGuid() + " and with name : " + par.getName());
			} else {
				log.error("Failed to  remove a partition with the guid : "
						+ parGuid);
			}
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		} 
	}

	/**
	 * View a Partition, get a information from database.
	 * @param partitionId
	 * 			the partition id.
	 * @return 
	 * 			a instance of the partition.
	 * @throws XMMException
	 */
	public Partition viewPartition(String partitionId) throws Exception {
		ParaChecker.checkGuidFormat(partitionId, "partition guid");
		try {
			Partition par = pm.view(partitionId.trim());
			log.info("Successfully view a partition with the name : "
					+ par.getName());
			return par;
		} catch (Exception e) {
			log.error("Failed to view a partition with the id : " 
					+ partitionId);
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		} 
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
	public PhysicalNode refreshPhysicalNode(String pnid) throws Exception {
		ParaChecker.checkGuidFormat(pnid, "PhysicalNode guid");
		try {
			PhysicalNode pn = pm.refreshPhysicalNode(pnid.trim());
			log.info("Successfully refresh a physical node with the name : "
					+ pn.getName());
			return pn;
		} catch (Exception e) {
			log.error("Failed to refresh a physical node with the id : " 
					+ pnid);
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		} 
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
			String publicIp, String pnController, boolean isRedploy,
			HashMap<String, String> attributes, String desc)
			throws Exception {
		ParaChecker.checkGuidFormat(partitionId, "Partition guid");
		XMMUtil.validateBlankOrNull(privateIp, "privateIp");
		XMMUtil
				.validateBlankOrNull(pnController, "PhsicalNode controller");

		if (publicIp != null) {
			publicIp = publicIp.trim();
		}
		
		try {
			
			PhysicalNode pn = pm.addPhysicalNode(partitionId.trim(),
					privateIp.trim(), publicIp, pnController.trim(), isRedploy,
					attributes, desc);
			log.info("Successfully add a physical node with the name : "
					+ pn.getName() + " to the partition " 
					+ pn.getPartitionId());
			return pn;
		} catch (Exception e) {
			log.error("Failed to add a physical node with the ip : "
					+ privateIp);
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw e;
		} catch (Throwable t) {
			throw new Exception(t.toString());
		} 
	}

	/**
	 * Remove a physical node from the system.
	 * @param pnGuid
	 * 			the physical node global id
	 * @return
	 * 			the removed physical node
	 * @throws XMMException
	 */
	public PhysicalNode removePhysicalNode(String pnGuid) throws Exception {
		ParaChecker.checkGuidFormat(pnGuid, "PhysicalNode guid");
		
		try {

			PhysicalNode pn = pm.removePhysicalNode(pnGuid.trim());
			log.info("Successfully remove a physical node with the name : "
					+ pn.getName() + " from the partition "
					+ pn.getPartitionId());
			return pn;
		} catch (Exception e) {
			log.error("Failed to remove a physical node with the id : "
					+ pnGuid);
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		} 
	}

	/**
	 * List all partitions.
	 * @return
	 * 			the list of all partitions
	 * @throws XMMException
	 */
	public List<Partition> listAllPartition() throws Exception {
		try {
			List<Partition> pars = pm.listAllPartition();
			log.info("Successfully list all Partitions, number is "
					+ pars.size());
			return pars;
		} catch (Exception e) {
			log.error("Failed to list all Partitions, due to " + e.toString());
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		} 
	}

	/**
	 * List a kind of partitions.
	 * @param pController
	 * 			the partition operate controller
	 * @return
	 * 			the kind of Partition List
	 * @throws XMMException
	 */
	public List<Partition> listPartition(String pController)
			throws Exception {
		XMMUtil.validateBlankOrNull(pController, "partition controller");
		try {
			List<Partition> pars = pm.listPartition(pController.trim());
			log.info("Successfully list Partitions with the kind of "
					+ pController + ", and its size is " + pars.size());
			return pars;
		} catch (Exception e) {
			log.error("Failed to list Partitions with the kind of "
					+ pController + ", due to " + e.toString());
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		} 
	}

	/**
	 * List all physical nodes in a partition.
	 * @param parid
	 * 			the partition id
	 * @return
	 * 			all physical nodes in the partition
	 * @throws XMMException
	 */
	public List<PhysicalNode> listPhysicalNode(String pid) throws Exception {
		ParaChecker.checkGuidFormat(pid, "Partition guid");	
		try {

			List<PhysicalNode> pns = pm.listPhysicalNode(pid.trim());
			log.info("Successfully list PhysicalNodes of the partition " + pid);
			return pns;
		} catch (Exception e) {
			log.error("Failed to list PhysicalNodes, due to " + e.toString());
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		} 
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
	public VirtualNetwork createVirtualNetwork(String partitionId, String name,
			String vnController, int netSize, String headNodeIp,
			String[] otherNodeIps, HashMap<String, String> attributes,
			String desc) throws Exception {
		ParaChecker.checkGuidFormat(partitionId, "Partition ID");
		XMMUtil.validateBlankOrNull(name, "VirtualNetwork name");
		XMMUtil.validateBlankOrNull(vnController,
				"VirtualNetwork controller");
		VirtualNetwork vn;
		
		try {

			if (netSize <= 0) {
				throw new Exception("The network size should be bigger"
						+ " than 0.");
			}
			// if the head node ip is null, then the VirtualNetworkAC will
			// choose a proper node as the head node.
			if (headNodeIp != null && !"".equals(headNodeIp)) {
				XMMUtil.validateIp(headNodeIp, "head node ip");
			}

			if (otherNodeIps != null) {
				for (int i = 0; i < otherNodeIps.length; i++) {
					XMMUtil.validateIp(otherNodeIps[i], "slave node ip");
					otherNodeIps[i].trim();
				}
			}
			// advanced usage
			// attributes.put(VirtualNetwork.AUTO_CREATE_TAG, "" + true);
			// attributes.put(VirtualNetwork.VN_RESERVE_NODE_TAG, "" + false);
			if (desc == null || "".equals(desc)) {
				desc = "Virtual Network (" + name + " " + headNodeIp
						+ ") with the controller " + vnController;
			}
			vn = VirtualNetworkManager.getInstance().createVirtualNetwork(
					partitionId.trim(), name.trim(), vnController.trim(),
					netSize, headNodeIp.trim(), otherNodeIps, attributes, desc);
		} catch (Exception e) {
			log.error("Failed to create a virtual network with the nicnum : "
					+ netSize + " and the headnodeip : " + headNodeIp);
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		}
		return vn;
	}

	/**
	 * Destroy a virtual network.
	 * @param vnguid
	 * 			the virtual network id
	 * @throws XMMException
	 */
	public VirtualNetwork destroyVirtualNetwork(String vnguid)
			throws Exception {
		ParaChecker.checkGuidFormat(vnguid, "virtual network guid");
		VirtualNetwork vn = null;
		
		try {

			vn = vnm.destroyVirtualNetwork(vnguid.trim());
			log.info("Successfully remove a virtual network with the guid : "
					+ vnguid + " and with virtual network name : "
					+ vn.getName());
			return vn;
		} catch (Exception e) {
			if (vn != null) {
				log.error("Failed to  remove a virtual network "
						+ "with the guid : " + vnguid + " and with name : "
						+ vn.getName());
			} else {
				log.error("Failed to  remove a virtual network "
						+ "with the guid : " + vnguid);
			}
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		} 
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
	 *            one record at least. required parameter. finally {

		}
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
			String desc) throws Exception {
		// FIXME support cpu, memory, disk and net quota. left the quota and
		// preference check to the client side.
		ParaChecker.checkGuidFormat(parid, "partition guid");
		XMMUtil.validateBlankOrNull(name, "virtual cluster name");
		XMMUtil.validateBlankOrNull(nodeMatchMaker, "node match maker");
		if (vnid != null) {
			vnid = vnid.trim();
		}
		if (nrmap == null || nrmap.size() < 1) {
			throw new Exception(
					"The hashmap of node requirements should have one "
					+ "record at least.");
		}

		if (effectiveTime != null && expireTime != null
				&& !expireTime.after(effectiveTime)) {
			throw new Exception("The expire time (" + expireTime
					+ ") should be after effective time (" + effectiveTime
					+ ").");
		}

		if (expireTime != null
				&& expireTime.before(this.getServerCurrentTime())) {
			throw new Exception("The expire time (" + expireTime
					+ ") should be after server current time ("
					+ this.getServerCurrentTime() + ").");
		}

		VirtualCluster vc = null;

		try {
			
			vc = vcm.createVirtualCluster(parid.trim(), name.trim(),
					nodeMatchMaker.trim(), tenantId.trim(), vnid, nrmap,
					effectiveTime, duration, expireTime, attributes, desc);
		} catch (Exception e) {
			log.error("Failed to create a virtual cluster (" + name
					+ ") with the vnid : " + vnid + ".");
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		}
		return vc;
	}

	/**
	 * Update a virtual cluster information
	 * mainly meta information.
	 * @param vcid
	 * 			the virtual cluster id
	 * @param newvc
	 * 			the new virtual cluster instance.
	 * @return
	 * 			the updated virtual cluster.
	 * @throws XMMException finally {

		}
	 */
	public VirtualCluster updateVirtualClusterInfo(String vcid,
			VirtualCluster newvc) throws Exception {
		ParaChecker.checkGuidFormat(vcid, "Virtual Cluster id");
		VirtualCluster vc = null;
		try {

			// VirtualCluster auvc = 
			vcm.view(vcid.trim());
			vc = vcm.updateVirtualClusterInfo(vcid.trim(), newvc);
			return vc;
		} catch (Exception e) {
			log.error("Failed to update a virtual cluster with the guid : "
					+ vcid);
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		}
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
			throws Exception {
		ParaChecker.checkGuidFormat(vnid, "Virtual Node id");
		VirtualNode vn = null;
		
		try {

			vn = parm.updateVirtualNodeInfo(vnid.trim(), newvn);
			return vn;
		} catch (Exception e) {
			log.error("Failed to update a virtual node with the guid : " 
					+ vnid);
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		}
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
	public VirtualNetwork updateVirtualNetworkInfo(String vnid,
			VirtualNetwork newvn) throws Exception {
		ParaChecker.checkGuidFormat(vnid, "Virtual Network id");
		VirtualNetwork vn = null;
		
		try {
			vn = vnm.updateVirtualNetworkInfo(vnid.trim(), newvn);
			return vn;
		} catch (Exception e) {
			log.error("Failed to update a virtual network with the guid : "
					+ vnid);
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		}
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
	public Partition updatePartitionInfo(String parid, Partition newpar)
			throws Exception {
		ParaChecker.checkGuidFormat(parid, "Partition id");
		Partition par = null;

		try {
			par = parm.updatePartitionInfo(parid.trim(), newpar);
			return par;
		} catch (Exception e) {
			log.error("Failed to update a partition with the guid : " + parid);
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		}
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
	public PhysicalNode updatePhysicalNodeInfo(String pnid, PhysicalNode newpn)
			throws Exception {
		ParaChecker.checkGuidFormat(pnid, "PhysicalNode id");
		PhysicalNode pn = null;
		
		try {

			pn = parm.updatePhysicalNodeInfo(pnid.trim(), newpn);
			return pn;
		} catch (Exception e) {
			log.error("Failed to update a physicalnode with the guid : " 
					+ pnid);
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		}
	}

	/**
	 * Get a virtual cluster instance.
	 * @param guid
	 * 			the global id of the virtual cluster
	 * @return
	 * 			the virtual cluster instance
	 * @throws XMMException
	 */
	public VirtualCluster viewVirtualCluster(String vcid) throws Exception {
		ParaChecker.checkGuidFormat(vcid, "Virtual Cluster id");
		VirtualCluster vc = null;

		try {

			vc = vcm.view(vcid.trim());
			return vc;
		} catch (Exception e) {
			log.error("Failed to view a virtual cluster with the guid : "
					+ vcid);
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		}
	}

	/**
	 * Start virtual cluster.
	 * @param vcid
	 * 			the virtual cluster id
	 * @throws XMMException
	 */
	public void startVirtualCluster(String vcid) throws Exception {
		ParaChecker.checkGuidFormat(vcid, "Virtual Cluster id");

		try {

			VirtualCluster vc = vcm.view(vcid.trim());
			if (vc == null) {
				throw new Exception("The virtual cluster with id " + vcid
						+ " is not exist.");
			}
			vcm.startVirtualCluster(vc);
		} catch (Exception e) {
			log.error("Failed to start a virtual cluster : " + vcid);
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		}
	}

	/**
	 * Stop a Virtual Cluster.
	 * @param vcid
	 * 		virtual cluster id.
	 * @throws XMMException
	 */
	public void stopVirtualCluster(String vcid) throws Exception {
		ParaChecker.checkGuidFormat(vcid, "Virtual Cluster id");

		try {

			VirtualCluster vc = vcm.view(vcid.trim());
			if (vc == null) {
				throw new Exception("The virtual cluster with id " + vcid
						+ " is not exist.");
			}
			vcm.stopVirtualCluster(vc);
		} catch (Exception e) {
			log.error("Failed to stop a virtual cluster with the guid : "
					+ vcid);
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		}
	}

	/**
	 * Destroy a virtual cluster,
	 * the virtual cluster muster be empty.
	 * 
	 * @param vcguid
	 * 			the virtual cluster id
	 * @throws XMMException
	 */
	public void destroyVirtualCluster(String vcguid) throws Exception {
		ParaChecker.checkGuidFormat(vcguid, "Virtual Cluster id");
		
		try {

			vcm.destroyVirtualCluster(vcguid.trim());
			log.info("Success to destroy a virtual cluster with the guid : "
					+ vcguid);
			return;
		} catch (Exception e) {
			log.error("Failed to destroy a virtual cluster with the guid : "
					+ vcguid);
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		} 
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
			throws Exception {
		ParaChecker.checkGuidFormat(vcid, "Virtual Cluster id");
	
		try {

			VirtualCluster vc = vcm.refreshVirtualCluster(vcid.trim());
			log.info("Success to refresh a virtual cluster with the guid : "
					+ vcid);
			return vc;
		} catch (Exception e) {
			log.error("Failed to refresh a virtual cluster with the guid : "
					+ vcid);
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		}
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
			String[] operators, Object[] values) throws Exception {
		checkSearchCondition(searchConditions, operators, values);

		try {

			List<VirtualNetwork> vnlist = vnm.searchVirtualNetwork(
					searchConditions, operators, values);
			return vnlist;
		} catch (Exception e) {
			log.error("Failed to search virtual network, due to "
					+ e.toString());
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		} 
	}

	private void checkSearchCondition(String[] searchConditions,
			String[] operators, Object[] values) throws Exception {
		if (searchConditions != null && operators != null && values != null) {
			if (searchConditions.length != operators.length) {
				throw new Exception(
						"The length of search conditions should equal the"
						+ " length of operators.");
			}

			if (searchConditions.length != values.length) {
				throw new Exception(
						"The length of search conditions should equal the"
						+ " length of values.");
			}
		}
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
			String[] operators, Object[] values) throws Exception {
		checkSearchCondition(searchConditions, operators, values);

		try {

			List<VirtualCluster> vclist = vcm.searchVirtualCluster(
					searchConditions, operators, values);
			if (vclist != null) {
				List<VirtualCluster> newvclist = 
					new ArrayList<VirtualCluster>();
				for (int i = 0; i < vclist.size(); ++i) {
					VirtualCluster vc = (VirtualCluster) vclist.get(i);
					try {
						newvclist.add(vc);
					} catch (Exception e) {
						log.error(e.getMessage());
						// do nothing!
					}
				}
				return newvclist;
			}
			return vclist;
		} catch (Exception e) {
			log.error("Failed to search virtual cluster, due to "
					+ e.toString());
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		} 
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
			String[] operators, Object[] values) throws Exception {
		checkSearchCondition(searchConditions, operators, values);

		try {

			List<PhysicalNode> pnlist = parm.searchPhysicalNode(
					searchConditions, operators, values);
			return pnlist;
		} catch (Exception e) {
			log.error("Failed to search physical node, due to " + e.toString());
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		}
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
			String[] operators, Object[] values) throws Exception {
		checkSearchCondition(searchConditions, operators, values);

		try {

			List<VirtualNode> vnlist = parm.searchVirtualNode(searchConditions,
					operators, values);
			return vnlist;
		} catch (Exception e) {
			log.error("Failed to search virtual node, due to " + e.toString());
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				throw new Exception(new Exception(ite.getTargetException()));
			}
			throw new Exception(e);
		} catch (Throwable t) {
			throw new Exception(t.toString());
		}
	}

	/**
	 * Get a virtual network information.
	 * @param guid
	 * 			the global id of the network
	 * @return
	 * 			the virtual network
	 * @throws XMMException
	 */
	public VirtualNetwork viewVirtualNetwork(String vnid) throws Exception {
		ParaChecker.checkGuidFormat(vnid, "virtual network guid");
		try {
			return vnm.view(vnid.trim());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * Get physical node instance.
	 * @param guid
	 * 			the global id of physical node
	 * @return
	 * 			the physical node
	 * @throws XMMException
	 */
	public PhysicalNode viewPhysicalNode(String guid) throws Exception {
		ParaChecker.checkGuidFormat(guid, "physical node guid");


		try {

			return parm.viewPhysicalNode(guid.trim());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * Get virtual node instance.
	 * @param guid
	 * 			the global id of virtual node
	 * @return
	 * 			the virtual node
	 * @throws XMMException
	 */
	public VirtualNode viewVirtualNode(String guid) throws Exception {
		ParaChecker.checkGuidFormat(guid, "virtual node guid");
		try {

			return parm.viewVirtualNode(guid.trim());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	
	/**
	 * Refresh virtual node instance.
	 * @param guid
	 * 			the global id of virtual node
	 * @return
	 * 			the virtual node
	 * @throws Exception
	 */
	public VirtualNode refreshVirtualNode(String guid) throws Exception {
		ParaChecker.checkGuidFormat(guid, "virtual node guid");
		try {

			return parm.refreshVirtualNode(guid.trim());
		} catch (Exception e) {
			throw new Exception(e);
		} finally {

		}
	}
	
	/**
	 * Start the virtual node.
	 * @param guid the virtual node's guid
	 * @return the virtual node
	 * @throws Exception
	 */
	public VirtualNode startVirtualNode(String guid) throws Exception {
		ParaChecker.checkGuidFormat(guid, "virtual node guid");
		try {

			return parm.startVirtualNode(guid.trim());
		} catch (Exception e) {
			throw new Exception(e);
		} finally {

		}
	}
	
	/**
	 * Stop the virtual node.
	 * @param guid the virtual node's guid
	 * @return the virtual node
	 * @throws Exception
	 */
	public VirtualNode stopVirtualNode(String guid) throws Exception {
		ParaChecker.checkGuidFormat(guid, "virtual node guid");
		try {

			return parm.stopVirtualNode(guid.trim());
		} catch (Exception e) {
			throw new Exception(e);
		} finally {

		}
	}
	
	/**
	 * Boot the virtual node.
	 * @param guid the virtual node's guid
	 * @return the virtual node
	 * @throws Exception
	 */
	public VirtualNode bootVirtualNode(String guid) throws Exception {
		ParaChecker.checkGuidFormat(guid, "virtual node guid");
		try {

			return parm.bootVirtualNode(guid.trim());
		} catch (Exception e) {
			throw new Exception(e);
		} finally {

		}
	}
	
	/**
	 * Shutdown the virtual node.
	 * @param guid the virtual node's guid
	 * @return the virtual node
	 * @throws Exception
	 */
	public VirtualNode shutdownVirtualNode(String guid) throws Exception {
		ParaChecker.checkGuidFormat(guid, "virtual node guid");
		try {

			return parm.shutdownVirtualNode(guid.trim());
		} catch (Exception e) {
			throw new Exception(e);
		} finally {

		}
	}
	
	/**
	 * Migrate the virtual node, if it's running, will live-migrate.
	 * @param guid the virtual node's guid
	 * @param hostId the new host's id
	 * @return the virtual node
	 * @throws Exception
	 */
	public VirtualNode migrateVirtualNode(String vNodeGuid, String pNodeGuid) throws Exception {
		ParaChecker.checkGuidFormat(vNodeGuid, "virtual node guid");
		try {
			PhysicalNode pn = this.viewPhysicalNode(pNodeGuid.trim());
			return parm.migrateVirtualNode(vNodeGuid.trim(), pn);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {

		}
	}
	
	/**
	 * Start the physical node.
	 * @param pNodeGuid
	 * @return
	 * @throws Exception
	 */
	public PhysicalNode startPhysicalNode(String pNodeGuid) throws Exception {
		ParaChecker.checkGuidFormat(pNodeGuid, "physical node guid");
		try {
			return parm.startPhysicalNode(pNodeGuid);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {

		}
	}
	
	/**
	 * stop the physical node.
	 * @param pNodeGuid
	 * @return
	 * @throws Exception
	 */
	public PhysicalNode stopPhysicalNode(String pNodeGuid) throws Exception {
		ParaChecker.checkGuidFormat(pNodeGuid, "physical node guid");
		try {
			return parm.stopPhysicalNode(pNodeGuid);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {

		}
	}
	
}
