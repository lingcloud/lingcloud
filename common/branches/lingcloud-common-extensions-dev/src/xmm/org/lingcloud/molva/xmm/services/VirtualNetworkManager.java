/*
 *  @(#)VirtualNetworkManager.java  2010-5-27
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetConstants;
import org.lingcloud.molva.ocl.asset.AssetManagerImpl;
import org.lingcloud.molva.xmm.pojos.Nic;
import org.lingcloud.molva.xmm.pojos.Partition;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.pojos.VirtualNetwork;
import org.lingcloud.molva.xmm.util.XMMConstants;
import org.lingcloud.molva.xmm.util.XMMUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2009-9-20<br>
 * @author Xiaoyi Lu<br>
 */
public class VirtualNetworkManager {

	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(VirtualNetworkManager.class);

	private static VirtualNetworkManager vnm = new VirtualNetworkManager();

	private static boolean publicIpEnable = false;

	private static ArrayList<String> publicIpPool = null;

	/**
	 * key-value, ip-Nic.
	 */
	public static final Map<String, Nic> IP_POOL = Collections
			.synchronizedMap(new HashMap<String, Nic>());

	private static int headNodeIpC = 0;

	private static int headNodeIpD = 1;

	private static String validIpSec = "192.168.0.1-192.168.255.254";

	private static boolean isIpReverse = false;
	
	private static final int MAX_LAST_IP_FIELD = 254;
	
	private static final int MAX_IP_FIELD = 255;

	
	public static boolean isPublicIpEnable() {
		return publicIpEnable;
	}

	public static void setPublicIpEnable(boolean publicIpEnable) {
		VirtualNetworkManager.publicIpEnable = publicIpEnable;
	}
	
	public static ArrayList<String> getPublicIpPool() {
		return publicIpPool;
	}

	public static void setPublicIpPool(ArrayList<String> publicIpPool) {
		VirtualNetworkManager.publicIpPool = publicIpPool;
	}
	
	public static String getValidIpSec() {
		return validIpSec;
	}

	public static void setValidIpSec(String validIpSec) {
		VirtualNetworkManager.validIpSec = validIpSec;
	}
	
	/**
	 * key-value, mac-Nic.
	 */
	/*
	 * FIXME, ipPool and macPool are different for its number, because when some
	 * ips are illegal, but the macs are legal.
	 */
	private static final Map<String, Nic> MAX_POOL = Collections
			.synchronizedMap(new WeakHashMap<String, Nic>());

	static {
		try {
			load();
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}

	private VirtualNetworkManager() {

	}

	private static void load() throws Exception {
		// ALL vm provision hosts' ip and mac. Has been done by the
		// VmProvisonPoolManager's load method.
		// FIXME, but the consistency maintanence method is not enough strong,
		// it can't support the os reboot because the interface alias(eth0:0)
		// will disappear. Xiaoyi Lu added at 2009.09.28.
		// FIXME, solve above problem by virtual gateway field in the virutal
		// network struct and insert some lines to /etc/rc.local file. Xiaoyi Lu
		// added at 2009.09.29. Has fixed.

		// All virtualNetworks' ips and macs.
		try {
			AssetManagerImpl ami = new AssetManagerImpl();
			String[] fields = new String[] { "type" };
			String[] operators = new String[] { "=" };
			Object[] values = new Object[] { 
					XMMConstants.VIRTUAL_NETWORK_TYPE };
			List<Asset> vnls = ami.search(fields, operators, values);
			for (int i = 0; i < vnls.size(); ++i) {
				VirtualNetwork vn = new VirtualNetwork((Asset) vnls.get(i));
				// TODO should check with reality.
				List<Nic> nics = vn.getPrivateIpNics();
				if (nics != null) {
					for (int j = 0; j < nics.size(); j++) {
						Nic le = (Nic) nics.get(j);
						triggerIpPoolAdd(le);
						triggerMacPoolAdd(le);
					}
				}

				HashMap<String, Nic> punics = vn.getPublicIpNics();
				if (punics != null && !punics.isEmpty()) {
					Iterator<Nic> it = punics.values().iterator();
					while (it.hasNext()) {
						Nic le = (Nic) it.next();
						triggerIpPoolAdd(le);
						triggerMacPoolAdd(le);
					}
				}

				List<Nic> gws = vn.getVirtualGateWay();
				if (gws != null) {
					for (int j = 0; j < gws.size(); j++) {
						Nic le = (Nic) gws.get(j);
						// FIXME, here the ipPool.containsKey will return
						// false, because when the first time load, the hostinfo
						// will trigger ip pool, but the VNM's load method is
						// executed first. So, here, in order to make sure all
						// of
						// virtual gw are exist, we can run the commands again.
						HashMap<String, String> commands = vn
								.getCreateVirtualGateWayCommands();
						if (commands != null
								&& commands.containsKey(le.getIp())) {
							XMMUtil.runCommand(commands.get(le.getIp()));
							triggerIpPoolAdd(le);
						}
					}
				}
			}
			publicIpEnable = XMMUtil.getPublicIpEnableInCfgFile();
			if (publicIpEnable) {
				publicIpPool = XMMUtil.loadPublicIp();
				String msg = "Successfully load available public ip are : ";
				for (int k = 0; k < publicIpPool.size(); ++k) {
					if (IP_POOL.containsKey(publicIpPool.get(k))) {
						publicIpPool.remove(k);
					} else {
						msg += publicIpPool.get(k) + ";";
					}
				}
				log.info(msg);
			}
		} catch (Throwable t) {
			log.error(t.toString());
			// Ignore this error.
		}
	}

	public static synchronized VirtualNetworkManager getInstance() {
		return vnm;
	}

	/**
	 * VirtualNetwork creation method.
	 * 
	 * @param partitionId
	 *            the virtual network will be created in the partition with
	 *            partitionId, this parameter is required.
	 * @param name
	 *            the name of virtual network, this parameter is required.
	 * @param vnController
	 *            the controller of virtual network, the default one is
	 *            VirtualNetworkAC.class.getName(), this parameter is required.
	 * @param netSize
	 *            the network size of virtual network, this parameter is
	 *            required.
	 * @param headNodeIp
	 *            the head node ip of virtual network, this parameter is
	 *            optional. if this parameter is null or blank, then the system
	 *            will choose a proper ip address automatically.
	 * @param otherNodeIps
	 *            the slave nodes' ip addresses, this parameter is optional. if
	 *            this parameter is null, then the system will generate them
	 *            automatically.
	 * @param attributes
	 *            other advanced usage support can be setted in this parameter.
	 *            this parameter is optional.
	 * @param desc
	 *            the description of virtual network. this parameter is
	 *            optional.
	 * @return
	 * @throws Exception
	 */
	public synchronized VirtualNetwork createVirtualNetwork(String partitionId,
			String name, String vnController, int netSize, String headNodeIp,
			String[] otherNodeIps, HashMap<String, String> attributes,
			String desc) throws Exception {
		if (netSize <= 0) {
			throw new Exception("The network size should be bigger than zero.");
		}
		// The 1 indicates the headnode.
		if (netSize < otherNodeIps.length + 1) {
			throw new Exception("The network size is not valid, "
					+ "it can not be smaller than required ip num.");
		}
		if ((headNodeIp == null || "".equals(headNodeIp))
				&& otherNodeIps != null && otherNodeIps.length > 0) {
			throw new Exception("Headnode ip is blank or null, "
					+ "Please choose a head node ip before other node ips.");
		}
		AssetManagerImpl ami = new AssetManagerImpl();
		Asset apar = ami.view(partitionId);
		if (apar == null) {
			throw new Exception("The target partition with id " + partitionId
					+ " is not exist.");
		}
		Partition par = new Partition(apar);
		VirtualNetwork vn = new VirtualNetwork();
		vn.setPartitionId(partitionId);
		// FIXME the virtual network's owner is the leaser of itself.
		vn.setOwnerId(par.getOwnerId());
		vn.setName(name);
		vn.setAssetController(vnController);
		vn.setDescription(desc);
		vn.setNetworkSize(netSize);
		List<Nic> nicList = new ArrayList<Nic>();
		if (headNodeIp != null && !"".equals(headNodeIp)) {
			vn.setHeadNodeIp(headNodeIp);
			Nic hnic = new Nic();
			hnic.setIp(headNodeIp);
			nicList.add(0, hnic);
		}
		if (otherNodeIps != null && otherNodeIps.length > 0) {
			for (int i = 1; i <= otherNodeIps.length; i++) {
				Nic onic = new Nic();
				onic.setIp(otherNodeIps[i - 1]);
				nicList.add(i, onic);
			}
		}
		// FIXME make sure all of the private ip nics have the proper ip
		// address.
		vn.setPrivateIpNics(nicList);

		if (attributes != null && !attributes.isEmpty()) {
			vn.getAttributes().putAll((attributes));
		}
		// TODO need to implement the way of selecting bridge. Different host
		// may have different bridge.
		vn.setBridge(XMMConstants.DEFAULT_BRIDGE);

		VirtualNetwork newvn = new VirtualNetwork(ami.add(vn, false));
		return newvn;
	}

	public static synchronized boolean isIpUsed(String ip) {
		return IP_POOL.containsKey(ip);
	}

	public static synchronized boolean isMacUsed(String mac) {
		return MAX_POOL.containsKey(mac);
	}

	public static synchronized void triggerMacPoolAdd(Nic nic) {
		if (nic == null || nic.getMac() == null) {
			return; // Ignore this case.
		}
		if (MAX_POOL.containsKey(nic.getMac())) {
			MAX_POOL.remove(nic.getMac());
		}
		MAX_POOL.put(nic.getMac(), nic);
	}

	public static synchronized void triggerIpPoolAdd(Nic nic) {
		if (nic == null || nic.getIp() == null) {
			return; // Ignore this error.
		}
		if (IP_POOL.containsKey(nic.getIp())) {
			IP_POOL.remove(nic.getIp());
		}
		IP_POOL.put(nic.getIp(), nic);
	}

	public static synchronized void triggerMacPoolRemove(Nic nic) {
		if (nic == null || nic.getMac() == null) {
			return; // ignore this error.
		}
		if (MAX_POOL.containsKey(nic.getMac())) {
			MAX_POOL.remove(nic.getMac());
		}
	}

	public static synchronized void triggerIpPoolRemove(Nic nic) {
		if (nic == null || nic.getIp() == null) {
			return; // Ignore this error.
		}
		if (IP_POOL.containsKey(nic.getIp())) {
			IP_POOL.remove(nic.getIp());
		}
	}

	/**
	 * VirtualNetwork destroy method.
	 * 
	 * @param vnguid
	 *            the guid of virtual network.
	 * @return
	 * @throws Exception
	 */
	public synchronized VirtualNetwork destroyVirtualNetwork(String vnguid)
			throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		Asset asset = ami.view(vnguid);
		if (asset == null) {
			// Ignore this error.
			return null;
		}
		VirtualNetwork vn = new VirtualNetwork(asset);
		VirtualNetwork removedvn = new VirtualNetwork(ami.remove(vn.getGuid(),
				false));
		log.info("One virtual network (" + removedvn.getName() + " "
				+ removedvn.getGuid() + ")is removed.");
		return removedvn;
	}

	public synchronized VirtualNetwork addNode2VirtualNetwork(String vnid,
			String privateIp, HashMap<String, String> attributes)
			throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		Asset asset = ami.view(vnid);
		if (asset == null) {
			throw new Exception("The target virtual network " + vnid
					+ " is not exist.");
		}
		VirtualNetwork vn = new VirtualNetwork(asset);

		if (privateIp != null && !"".equals(privateIp)) {
			List<Nic> pips = vn.getPrivateIpNics();
			for (int i = 0; i < pips.size(); i++) {
				Nic nic = (Nic) pips.get(i);
				String ip = nic.getIp();
				if (ip != null && ip.equals(privateIp)) {
					throw new Exception("The node with ip " + privateIp
							+ " is already in the virtual network.");
				}
			}
		}

		VirtualNetwork convn = (VirtualNetwork) ami.control(vn, "addNode",
				new Object[] { vn, privateIp, attributes });
		log.info("One virtual network (" + convn.getName() + " "
				+ convn.getGuid() + ") add a new node with ip " + privateIp
				+ ".");
		return convn;
	}

	public synchronized VirtualNetwork removeNodeFromVirtualNetwork(
			String vnid, String privateIp, HashMap<String, String> attributes)
			throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		Asset asset = ami.view(vnid);
		if (asset == null) {
			throw new Exception("The target virtual network " + vnid
					+ " is not exist.");
		}
		VirtualNetwork vn = new VirtualNetwork(asset);
		if (privateIp != null && !"".equals(privateIp)) {
			List<Nic> pips = vn.getPrivateIpNics();
			boolean inNetworkFlag = false;
			for (int i = 0; i < pips.size(); i++) {
				Nic nic = (Nic) pips.get(i);
				String ip = nic.getIp();
				if (ip != null && ip.equals(privateIp)) {
					inNetworkFlag = true;
					break;
				}
			}
			if (!inNetworkFlag) {
				throw new Exception("The node with ip " + privateIp
						+ " is not in the virtual network.");
			}
		}
		VirtualNetwork convn = (VirtualNetwork) ami.control(vn, "removeNode",
				new Object[] { vn, privateIp, attributes });
		log.info("One virtual network (" + convn.getName() + " "
				+ convn.getGuid() + ") remove a node with ip " + privateIp
				+ ".");
		return convn;
	}

	public static void triggerNetworkInfoAdd(List<Nic> nicli) {
		if (nicli == null || nicli.isEmpty()) {
			return;
		}
		for (int j = 0; j < nicli.size(); j++) {
			Nic nic = (Nic) nicli.get(j);
			String mac = nic.getMac();
			String ip = nic.getIp();
			if (!"FE:FF:FF:FF:FF:FF".equals(mac)
					&& !"00:00:00:00:00:00".equals(mac) && !"".equals(mac)) {
				triggerMacPoolAdd(nic);
			}
			if (!"0.0.0.0".equals(ip)) {
				triggerIpPoolAdd(nic);
			}

		}
	}

	public static void triggerNetworkInfoRemove(List<Nic> nicli) {
		if (nicli == null || nicli.isEmpty()) {
			return;
		}
		for (int j = 0; j < nicli.size(); j++) {
			Nic nic = (Nic) nicli.get(j);
			String mac = nic.getMac();
			String ip = nic.getIp();
			if (!"FE:FF:FF:FF:FF:FF".equals(mac)
					&& !"00:00:00:00:00:00".equals(mac) && !"".equals(mac)) {
				triggerMacPoolRemove(nic);
			}
			if (!"0.0.0.0".equals(ip)) {
				triggerIpPoolRemove(nic);
			}
		}
	}

	public static boolean isIpInPhysicalNodeIdle(String ip) {
		if (ip == null || "".equals(ip)) {
			return false;
		}
		String[] searchConditions = new String[] { "name" };
		String[] operators = new String[] { "=" };
		Object[] values = new String[] { ip };
		try {
			List<PhysicalNode> pnlist = PartitionManager.getInstance()
					.searchPhysicalNode(searchConditions, operators, values);
			PhysicalNode pn = pnlist.get(0);
			return pn.getAssetState().equals(AssetConstants.AssetState.IDLE);
		} catch (Exception e) {
			log.error("Search PhysicalNode failed due to : " + e.toString());
			return false;
		}
	}

	public VirtualNetwork view(String vnid) throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		Asset asset = ami.view(vnid);
		if (asset == null) {
			return null;
		} else {
			return new VirtualNetwork(asset);
		}

	}

	public VirtualNetwork updateVirtualNetworkInfo(String guid,
			VirtualNetwork vn) throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		Asset upAsset = ami.update(guid, vn);
		if (upAsset == null) {
			return null;
		}
		return new VirtualNetwork(upAsset);
	}

	public List<VirtualNetwork> searchVirtualNetwork(String[] searchConditions,
			String[] operators, Object[] values) throws Exception {
		List<String> scon = new ArrayList<String>();
		List<String> opers = new ArrayList<String>();
		List<Object> vals = new ArrayList<Object>();

		if (searchConditions != null) {
			for (int i = 0; i < searchConditions.length; i++) {
				if (searchConditions[i].equals("type")) {
					continue;
				}
				scon.add(searchConditions[i]);
				opers.add(operators[i]);
				vals.add(values[i]);
			}
		}

		scon.add("type");
		opers.add("=");
		vals.add(XMMConstants.VIRTUAL_NETWORK_TYPE);

		AssetManagerImpl ami = new AssetManagerImpl();
		List<Asset> alist = ami.search(scon.toArray(new String[scon.size()]),
				opers.toArray(new String[scon.size()]), vals.toArray());
		if (alist == null || alist.isEmpty()) {
			return null;
		}
		List<VirtualNetwork> vnlist = new ArrayList<VirtualNetwork>();
		for (int k = 0; k < alist.size(); k++) {
			VirtualNetwork vn = new VirtualNetwork(alist.get(k));
			vnlist.add(vn);
		}
		return vnlist;
	}

	// XXX this method should be improved later.
	public static String findAvailableIp4VM() throws Exception {
		String ip = XMMConstants.IP_PREFIX + headNodeIpC + "."
				+ headNodeIpD;
		while (VirtualNetworkManager.isIpUsed(ip)) {
			headNodeIpD++;
			if (headNodeIpD == MAX_LAST_IP_FIELD - 2) {
				// a trick : this policy can make sure there is a 253 ip as the
				// first slave node.
				headNodeIpC++;
				headNodeIpD = 1;
			}
			if (headNodeIpC == MAX_IP_FIELD 
					&& headNodeIpD == MAX_LAST_IP_FIELD - 2) {
				if (isIpReverse) {
					// FIXME because when the system running, other ip
					// resource will be released.
					isIpReverse = false;
					throw new Exception(
							"There is not enough free ip address to allocate.");
				}
				isIpReverse = true;
				headNodeIpC = 0;
				headNodeIpD = 1;
			}
			ip = XMMConstants.IP_PREFIX + headNodeIpC + "." + headNodeIpD;
		}
		// FIXME because when the system running, other ip resource will be
		// released.
		isIpReverse = false;
		return ip;
	}

	public static String findAvailableIp4PM(String parid) throws Exception {
		List<String> ips = findAvailableIpList4PM(1, parid);
		if (ips == null || ips.isEmpty()) {
			throw new Exception(
					"There is not enough free ip address to allocate.");
		}
		return ips.get(0);
	}

	public static List<String> findAvailableIpList4PM(int num, String parid)
			throws Exception {
		String[] searchConditions = new String[] { "assetState",
				"assetLeaserId" };
		String[] operators = new String[] { "=", "=" };
		Object[] values = new Object[] {
				AssetConstants.AssetState.IDLE.toString(), parid };
		List<PhysicalNode> pnlist = PartitionManager.getInstance()
				.searchPhysicalNode(searchConditions, operators, values);
		if (pnlist == null || pnlist.size() <= 0) {
			throw new Exception("No enough physical node in partition " + parid
					+ "to allocate network.");
		}
		List<String> result = new ArrayList<String>();
		for (int k = 0; k < pnlist.size(); k++) {
			PhysicalNode pn = pnlist.get(k);
			result.add(pn.getPrivateIps()[0]);
			num--;
			if (num == 0) {
				return result;
			}
			// }
		}
		if (num > 0) {
			log.warn("No enough physical node in partition " + parid
					+ " to be allocated, left " + num
					+ " nodes are not allocated.");
		}
		return result;
	}
}
