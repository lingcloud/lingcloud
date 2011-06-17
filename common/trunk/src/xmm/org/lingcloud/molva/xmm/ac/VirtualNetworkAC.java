/*
 *  @(#)VirtualNetworkAC.java  2010-5-27
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

package org.lingcloud.molva.xmm.ac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetConstants;
import org.lingcloud.molva.ocl.asset.AssetController;
import org.lingcloud.molva.xmm.pojos.Nic;
import org.lingcloud.molva.xmm.pojos.Partition;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.pojos.VirtualCluster;
import org.lingcloud.molva.xmm.pojos.VirtualNetwork;
import org.lingcloud.molva.xmm.services.PartitionManager;
import org.lingcloud.molva.xmm.services.VirtualClusterManager;
import org.lingcloud.molva.xmm.services.VirtualNetworkManager;
import org.lingcloud.molva.xmm.util.XMMConstants;
import org.lingcloud.molva.xmm.util.XMMUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-07-05<br>
 * @author Xiaoyi Lu<br>
 */
public class VirtualNetworkAC extends AssetController {
	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(VirtualNetworkAC.class);

	public static final String OPTIONAL_ATTR_PUBLICIP_NUM = "publicIPNum";

	private static boolean isIpReverse = false;
	
	private static final int A = 0;
	private static final int B = 1;
	private static final int C = 2;
	private static final int D = 3;
	private static final int E = 4;
	private static final int F = 5;
	private static final int HEX = 16;
	private static final int MAX_LAST_IP_FIELD = 254;
	private static final int MAX_IP_FIELD = 255;

	@Override
	public Asset create(Asset asset) throws Exception {
		log.info("VirtualNetwork (" + asset.getName()
				+ ") of ElasticVirtualNetoworkAC is creating.");
		VirtualNetwork vn = new VirtualNetwork(asset);
		Partition par = PartitionManager.getInstance()
				.view(vn.getPartitionId());

		String nodeType = null;
		VirtualNetwork result = vn;
		if (par.getAssetController().equals(PartitionAC.class.getName())) {
			nodeType = par.getAttributes().get(
					PartitionAC.REQUIRED_ATTR_NODETYPE);
			if (PartitionAC.VM.equals(nodeType)) {
				result = this.createVN4VM(vn);
			} else {
				result = this.createVN4PM(vn);
			}
		} else {
			// nothing to do now.
			return result;
		}
		log.info("VirtualNetwork (" + result.getName()
				+ ") of VirtualNetworkAC is created successfully.");
		return result;
	}

	private VirtualNetwork createVN4PM(VirtualNetwork vn) throws Exception {
		if (vn.getHeadNodeIp() == null || "".equals(vn.getHeadNodeIp())) {
			// FIXME here, we generate a head node ip automatically.
			final int num = 1;
			List<String> ips = VirtualNetworkManager.findAvailableIpList4PM(
					num, vn.getPartitionId());
			vn.setHeadNodeIp(ips.get(0));
		} else {
			if (!VirtualNetworkManager.isIpInPhysicalNodeIdle(vn
					.getHeadNodeIp())) {
				throw new Exception("Sorry for this ip " + vn.getHeadNodeIp()
						+ " is used now, please change one.");
			}
		}

		List<Nic> nicList = vn.getPrivateIpNics();
		if (nicList != null && nicList.size() > 0) {
			// FIXME get the max number of nics for the user. In the normal
			// time, the network size is always bigger than nicList.size.
			if (vn.getNetworkSize() < nicList.size()) {
				vn.setNetworkSize(nicList.size());
			}
		}
		VirtualNetwork newvn = this.configVirtualNetwork4PM(vn);
		// XXX here we don't reserve physical node, because we do not know the
		// lease id (cluster id). In the future, we may use an option for user
		// to select whether or not to reserve the physical node.
		if (newvn.isNeedToReserveNode() && !newvn.isAutoCreate()) {
			this.reservePhysicalNodesByNicList(newvn.getPrivateIpNics());
		}
		return newvn;
	}

	private void reservePhysicalNodesByNicList(List<Nic> nics) 
		throws Exception {
		List<String> aips = new ArrayList<String>();
		for (int i = 0; i < nics.size(); i++) {
			Nic nic = (Nic) nics.get(i);
			aips.add(nic.getIp());
		}
		this.reservePhysicalNodesByIpList(aips);
	}

	private VirtualNetwork configVirtualNetwork4PM(VirtualNetwork vn)
			throws Exception {
		// FIXME From 2010-7-12, we decide to support user to choose some ips to
		// construct virtual network.
		// XXX firstly, we satisfy the user requirements;
		List<Nic> nics = vn.getPrivateIpNics();
		List<Nic> allLeases = new ArrayList<Nic>();
		for (int i = 0; i < nics.size(); i++) {
			Nic nic = (Nic) nics.get(i);
			if (nic.getIp().equals(vn.getHeadNodeIp())) {
				// HeadNode ip is checked before.
				allLeases.add(nic);
				continue;
			}
			if (VirtualNetworkManager.isIpInPhysicalNodeIdle(nic.getIp())) {
				allLeases.add(nic);
			}
		}

		// XXX secondly, we provide another ips automatically.
		if (allLeases.size() < vn.getNetworkSize()) {
			try {
				// FIXME Bug fixed, due to user required ips are not reserved
				// immediately, so findAvailableIp4PM also will choose them, we
				// must filter the result set, then we need to find networksize
				// number ip address to do filter.
				List<String> foundips = VirtualNetworkManager
						.findAvailableIpList4PM(vn.getNetworkSize(),
								vn.getPartitionId());
				if (foundips == null || foundips.isEmpty()) {
					log.info("No enough ips  to be generated automatically "
							+ "for the virtual network " + vn.getName()
							+ ". We ignore this situation, "
							+ "and go on to create network.");
					// Try best.
				} else {
					boolean filterTag = false;
					for (int i = 0; i < foundips.size(); i++) {
						filterTag = false;
						String tmip = foundips.get(i);
						for (int j = 0; j < allLeases.size(); j++) {
							String exip = allLeases.get(j).getIp();
							if (exip != null && exip.equals(tmip)) {
								filterTag = true;
								break;
							}
						}
						if (filterTag) {
							continue;
						}
						Nic nic = new Nic();
						nic.setIp(tmip);
						allLeases.add(nic);
					}
				}
			} catch (Exception e) {
				log.warn("Error occured when system find "
						+ "additonal ips for the virtual network "
						+ vn.getName() + " due to " + e.toString()
						+ " in findAvailableIp4PM method.");
				// Ignore this error.
			}
		}

		if (allLeases == null || allLeases.size() < vn.getNetworkSize()) {
			log.warn("Some ip are used, so there is not enough adresses, "
			+ "the system will try its best to create the virtual network.");
		}
		vn.setPrivateIpNics(allLeases);
		vn.setNetworkSize(allLeases.size());
		return vn;
	}

	private VirtualNetwork createVN4VM(VirtualNetwork vn) throws Exception {
		if (vn.getHeadNodeIp() == null || "".equals(vn.getHeadNodeIp())) {
			// FIXME here, we generate a head node ip automatically.
			String ip = VirtualNetworkManager.findAvailableIp4VM();
			vn.setHeadNodeIp(ip);
			List<Nic> nicList = new ArrayList<Nic>();
			Nic onic = new Nic();
			onic.setIp(ip);
			nicList.add(0, onic);
			nicList.addAll(vn.getPrivateIpNics());
			vn.setPrivateIpNics(nicList);
		} else {
			if (VirtualNetworkManager.isIpUsed(vn.getHeadNodeIp())) {
				throw new Exception("Sorry for this ip " + vn.getHeadNodeIp()
						+ " is used now, please change one.");
			}
		}
		// FIXME bug fixed, the vn.getPrivateIpNics may not contain all user
		// specific requriements. the network size is a standard value to
		// allocate nic.
		VirtualNetwork newvn = this.configVirtualNetwork4VM(vn);
		List<Nic> nics = newvn.getPrivateIpNics();
		if (nics != null) {
			VirtualNetworkManager.triggerNetworkInfoAdd(nics);
		}
		return newvn;
	}

	private VirtualNetwork configVirtualNetwork4VM(VirtualNetwork vn)
			throws Exception {
		// FIXME, two steps to construct virtual network. 1st, generate Ip and
		// Mac Pairs. 2nd, run command like "/sbin/ifconfig eth0:0
		// 10.0.0.10" to add a virtual gateway for every virtual ip
		// section; Comments added by Xiaoyi Lu, at 2010.07.05.
		try {
			// first step: generate private and public IpMacPairs.
			VirtualNetwork newvn0 = this.generateIpMacPairs(vn);
			// FIXME now, we don't add virtual network info to opennebula for
			// elastic requirement. 2010.07.05.
			VirtualNetwork newvn = this.allocatePublicIps(newvn0);

			// second step: createVirtualGateway
			this.createVirtualGateway(newvn);
			log.info("A new virtual network with the name of "
					+ newvn.getName() + " is prepared successfully.");

			return newvn;
		} catch (Throwable t) {
			// TODO Transaction support!
			log.error(t.toString());
			throw new Exception(t.toString());
		}
	}

	private VirtualNetwork allocatePublicIps(VirtualNetwork vn)
			throws Exception {
		// FIXME addtional elastic public ip support, Xiaoyi Lu added at
		// 2010-7-2.
		if (VirtualNetworkManager.isPublicIpEnable()) {
			if (VirtualNetworkManager.getPublicIpPool().size() > 0) {
				// FIXME an optional function to support every node has a
				// public ip.
				String tag = vn.getAttributes().get(OPTIONAL_ATTR_PUBLICIP_NUM);
				int value = 0;
				try {
					value = Integer.parseInt(tag);
				} catch (Exception e) {
					String msg = "The tag of " + OPTIONAL_ATTR_PUBLICIP_NUM
							+ " should be a valid int value.";
					log.error(msg);
					return vn;
				}
				HashMap<String, Nic> pipNics = new HashMap<String, Nic>();
				List<Nic> priNics = vn.getPrivateIpNics();
				if (value > priNics.size()) {
					value = priNics.size();
				}
				for (int i = 0; i < value; i++) {
					String tmpPip = (String) VirtualNetworkManager
						.getPublicIpPool().remove(0);
					while (VirtualNetworkManager.isIpUsed(tmpPip)) {
						log.warn("There is some error occured when "
								+ "maintaineces the publicIpPool, but"
								+ " ignore this.");
						// XXX tmpPip is null or blank will break this loop.
						tmpPip = (String) VirtualNetworkManager
							.getPublicIpPool().remove(0);
					}
					if (tmpPip != null && !"".equals(tmpPip)) {
						Nic pipnic = new Nic(tmpPip, ip2MacByRAW(tmpPip));
						String key = priNics.get(i).getIp();
						pipNics.put(key, pipnic);
					}
				}
				if (!pipNics.isEmpty()
						&& !pipNics.containsKey(vn.getHeadNodeIp())) {
					// Make sure the head node has a public ip.
					Nic nic = pipNics.remove(priNics.get(0).getIp());
					pipNics.put(vn.getHeadNodeIp(), nic);
				}
				log.info("Configure virtual network " + vn.getName()
						+ " a set of tmp public ips, number : "
						+ pipNics.size());
				vn.setPublicIpNics(pipNics);
			}
		}
		return vn;
	}

	private void createVirtualGateway(VirtualNetwork vn) throws Exception {
		if (vn == null) {
			return;
		}
		// TODO VirtualGateWay should be a data structure.
		// VirtualGateWay : nicname, ip, mac, host.
		// VirtualRouter : may also need to be supported.
		List<Nic> nics = vn.getPrivateIpNics();
		List<Nic> gw = new ArrayList<Nic>();
		HashMap<String, String> gwcm = new HashMap<String, String>();
		PhysicalNode thisHost = this.getLocalHostInfo();
		for (int i = 0; i < nics.size(); i++) {
			String gwip = XMMConstants.IP_PREFIX;
			Nic le = (Nic) nics.get(i);
			String ip = le.getIp().trim();
			gwip = gwip + ip.split("\\.")[2] + ".254";
			if (VirtualNetworkManager.isIpUsed(gwip)) {
				// FIXME trick: if the gw has been deployed, then it will in the
				// ipUsedMap.
				// Bug fixed here, some virtual networks may be in the same net
				// section, then they will share the virtual gate way.
				// TODO May be we can only get the last one to compare.
				boolean isAdded = false;
				for (int j = 0; j < gw.size(); j++) {
					Nic sharedgw = gw.get(j);
					if (sharedgw.getIp().equals(gwip)) {
						isAdded = true;
					}
				}
				if (!isAdded) {
					gw.add(VirtualNetworkManager.IP_POOL.get(gwip));
					if (!gwcm.containsKey(gwip)) {
						if (VirtualNetworkManager.IP_POOL.get(gwip)
								.getNicName() != null
								|| !"".equals(VirtualNetworkManager.IP_POOL.get(
										gwip).getNicName())) {
							gwcm.put(gwip, "/sbin/ifconfig "
									+ VirtualNetworkManager.IP_POOL.get(gwip)
											.getNicName() + " " + gwip);
						}
					}
				}
				continue;
			}
			try {
				// TODO this indicates the vecp server should be added to the
				// partition. Here should be improved. Xiaoyi Lu add at
				// 2010-06-04. we should modify getVirtualGWInterface method to
				// get nic info directly, but not
				if (thisHost == null) {
					continue;
				}
				String gwif = this.getVirtualGWInterface(thisHost);
				if (gwif == null) {
					String msg = "Can't get virtual gateway in the host : "
							+ thisHost.getHostName();
					log.error(msg);
					continue;
				}
				String command = "/sbin/ifconfig " + gwif + " " + gwip;
				XMMUtil.runCommand(command);
				Nic le1 = new Nic();
				le1.setIp(gwip);
				le1.setNicName(gwif);
				List<Nic> nicls = thisHost.getNics();
				for (int j = 0; j < nicls.size(); j++) {
					Nic ll = (Nic) nicls.get(j);
					String nicName = ll.getNicName();
					if (nicName != null && nicName.matches("eth\\d++")) {
						// The eth0:0 and eth0:1 will have the same mac address.
						if (gwif.startsWith(nicName)) {
							le1.setMac(ll.getMac());
							break;
						}
					}
				}
				// FIXME, here we can't Trigger later, because the reality has
				// create interface alias successfully. Xiaoyi Lu added at
				// 2009.10.08.
				VirtualNetworkManager.triggerIpPoolAdd(le1);
				// Bug fixed here, because we have run a command on the host,
				// then we should update the host info make sure the virtual
				// gateway info will be collected.

				nicls.add(le1);
				thisHost.setNics(nicls);
				PartitionManager.getInstance().updatePhysicalNodeInfo(
						thisHost.getGuid(), thisHost);

				gw.add(le1);
				gwcm.put(gwip, command);

			} catch (Exception e) {
				log.error(e.toString());
				throw e;
			}
		}

		vn.setVirtualGateWay(gw);
		vn.setCreateVirtualGateWayCommands(gwcm);
	}

	private PhysicalNode getLocalHostInfo() throws Exception {
		try {
			String hostname = XMMUtil.getLocalHostName();
			PhysicalNode pn = XMMUtil.getPhysicalNodeByHostName(hostname);
			return pn;
		} catch (Exception e) {
			log.error(e.toString());
			throw e;
		}
	}

	private String getVirtualGWInterface(PhysicalNode hi) throws Exception {
		try {
			List<Nic> nics = hi.getNics();
			List<String> vgwByAlias = new ArrayList<String>();
			List<String> ethif = new ArrayList<String>();
			for (int j = 0; j < nics.size(); j++) {
				Nic le = (Nic) nics.get(j);
				String nicName = le.getNicName();
				if (nicName.matches("eth\\d++\\:\\d++")) {
					// FIXME, this reg indicates pattern like "eth0:0".
					vgwByAlias.add(nicName);
				} else if (nicName.matches("eth\\d++")) {
					ethif.add(nicName);
				}
			}
			if (vgwByAlias.size() > 0) {
				String[] tt = vgwByAlias.get(0).split(":");
				String prefix = tt[0];
				int num = Integer.parseInt(tt[1]);
				for (int k = 0; k < vgwByAlias.size(); k++) {
					String alias = vgwByAlias.get(k).trim();
					if (alias.startsWith(prefix)) {
						String[] tp = alias.split(":");
						int nn = Integer.parseInt(tp[1]);
						if (nn > num) {
							num = nn;
						}
					}
				}
				num++;
				return prefix + ":" + num;
			}
			if (ethif.size() > 0) {
				// eth0, eth1...
				String[] tt = ethif.get(0).split("h");
				int num = Integer.parseInt(tt[1]);
				for (int m = 0; m < ethif.size(); m++) {
					String ethi = ethif.get(m).trim();
					String[] tp = ethi.split("h");
					int nn = Integer.parseInt(tp[1]);
					if (nn < num) {
						num = nn;
					}
				}
				// FIXME, the smallest interface.
				return "eth" + num + ":0";
			}
		} catch (Exception e) {
			log.error(e.toString());
			throw e;
		}
		return null;
	}

	private VirtualNetwork generateIpMacPairs(VirtualNetwork vn)
			throws Exception {
		// 10.0.0.10
		// Bug fixed here. ".""|""+""*" are escape character,when use them in
		// split(), we should add "\\" before them.
		// like split("\\."), split("\\|"), split("\\+"), split("\\*").
		// Fixed by Xiaoyi Lu at 2009.10.07.
		String[] ipsubField = vn.getHeadNodeIp().split("\\.");
		
		int subFieldc = Integer.parseInt(ipsubField[C]);
		int subFieldd = Integer.parseInt(ipsubField[D]);
		// FIXME, in the every ip section, the 254 num is reserved as the
		// virtual gateway of this section. And, another condition is there is
		// at least one node in the same section of headnode, so the last field
		// should less than 253.
		if (subFieldd >= MAX_LAST_IP_FIELD - 1) {
			throw new Exception("Please make sure the last field of head node"
					+ " ip less than 253.");
		}
		// TODO, now only support master-slave and vm mode, the secmaster mode
		// will be support later.
		StringBuilder macsb = new StringBuilder();
		// vm ip-mac pair.
		if (vn.getNetworkSize() == 1) {
			List<Nic> leaseList = new ArrayList<Nic>();
			int nextIpd = subFieldd;
			macsb.append(getValidMacField(Integer.toHexString(nextIpd)));
			// Master or vm tag.
			macsb.append(":00");
			// Net_num.
			macsb.append(":00:01");
			macsb.append(":" + getValidMacField(Integer.toHexString(subFieldc))
					+ ":" + getValidMacField(Integer.toHexString(subFieldd)));
			Nic ls = new Nic(vn.getHeadNodeIp(), macsb.toString());
			// sb.append(macsb.toString());
			// FIXME, in order to make sure all leases info in the memory are
			// the same with the reality and naming, we should trig them after
			// successfully allocation, and due to
			// createVirtualNetwork/removeVirtualNetwork are synchronized
			// methods, so no need to update lease pool too frequently. Later,
			// we should use transaction mechanism.Xiaoyi
			// LU added at 2009.10.08.
			leaseList.add(ls);
			vn.setPrivateIpNics(leaseList);
			vn.setNetworkSize(leaseList.size());
			return vn;
		}

		String firstSlaveNodeIp = this.generateFirstSlaveNodeIp(vn);
		if (firstSlaveNodeIp == null) {
			throw new Exception("There is no extra un-used ip address for the "
					+ "first next node assignment in the section of "
					+ ipsubField[0] + "." + ipsubField[1] + "." + ipsubField[2]
					+ ".0");
		}

		// Bug fixed here, for nicnum == 2.
		if (vn.getNetworkSize() == 2) {
			List<String> slaveIps = new ArrayList<String>();
			slaveIps.add(firstSlaveNodeIp);
			List<Nic> allLeases = this.generateNicListByIps(vn.getHeadNodeIp(),
					slaveIps);
			if (allLeases == null || allLeases.size() < vn.getNetworkSize()) {
				log.warn("Some ip or mac addresses are used, so there is not "
						+ "enough adresses, the system will try its best to "
						+ "create the virtual network.");
			}
			vn.setPrivateIpNics(allLeases);
			vn.setNetworkSize(allLeases.size());
			return vn;
		}

		List<String> slaveIps = this.generateAllSlaveIps(firstSlaveNodeIp, vn);
		// The associated mac address of every ip will be checked again, then if
		// the mac is used already, then the ip address will also be deleted.
		// TODO, some method to compensate this error. Comments added by Xiaoyi
		// Lu.
		List<Nic> allLeases = this.generateNicListByIps(vn.getHeadNodeIp(),
				slaveIps);
		if (allLeases == null || allLeases.size() < vn.getNetworkSize()) {
			log.warn("Some ip or mac addresses are used, so there is not "
					+ "enough adresses, the system will try its best to "
					+ "create the virtual network.");
		}
		vn.setPrivateIpNics(allLeases);

		vn.setNetworkSize(allLeases.size());
		return vn;

	}

	private List<String> generateAllSlaveIps(String firstSlaveNodeIp,
			VirtualNetwork vn) {
		String[] fsnips = firstSlaveNodeIp.split("\\.");
		int fsnipc = Integer.parseInt(fsnips[C]);
		int fsnipd = Integer.parseInt(fsnips[D]);
		List<String> slaveIps = new ArrayList<String>();
		slaveIps.add(firstSlaveNodeIp);
		// XXX, except headnode and firstslave node ip addresses.
		int leftSize = vn.getNetworkSize() - 2;
		// slaves ips generation.
		// FIXME, firstly, satify user requirement.
		List<Nic> nics = vn.getPrivateIpNics();
		for (int m = 0; m < nics.size(); m++) {
			Nic tmpnic = (Nic) nics.get(m);
			String tmpip = tmpnic.getIp();
			if (tmpip.equals(vn.getHeadNodeIp())
					|| tmpip.equals(firstSlaveNodeIp)) {
				continue;
			}
			if (VirtualNetworkManager.isIpUsed(tmpip)) {
				log.warn("The user wanted ip : " + tmpip
						+ " is used already, ignore this error.");
				continue;
			}
			slaveIps.add(tmpip);
			leftSize--;
		}
		if (leftSize == 0) {
			return slaveIps;
		}

		// FIXME, secondly, generate automatically.
		int nextTryNodeIpc = 0;
		int nextTryNodeIpd = 0;
		if (fsnipd >= MAX_LAST_IP_FIELD - 1) {
			nextTryNodeIpc = fsnipc + 1;
			if (nextTryNodeIpc > MAX_IP_FIELD) {
				nextTryNodeIpc = 0;
			}
			nextTryNodeIpd = 1;
		} else {
			nextTryNodeIpc = fsnipc;
			nextTryNodeIpd = fsnipd + 1;
		}
		// List<String> tmp_slaveIps = this.trySlaveIps(leftSize, fsnips[0] +
		// "."
		// + fsnips[1] + "." + nextTryNodeIpc + "." + nextTryNodeIpd);
		// FIXME bug fixed, due to we donot reserve the allocated ip immedially,
		// so the ip pool in memory doesnot contain the record of headnode and
		// previous allocated slave nodes ip addresses. So the try slave ips may
		// have the same ip of the exist ips, we must filter them.
		List<String> tmpSlaveIps = this.trySlaveIps(vn.getNetworkSize(),
				fsnips[0] + "." + fsnips[1] + "." + nextTryNodeIpc + "."
						+ nextTryNodeIpd);
		if (tmpSlaveIps == null || tmpSlaveIps.isEmpty()) {
			return slaveIps;
		} else {
			boolean filterTag = false;
			for (int i = 0; i < tmpSlaveIps.size(); i++) {
				filterTag = false;
				String tmip = tmpSlaveIps.get(i);
				if (vn.getHeadNodeIp().equals(tmip)) {
					continue;
				}
				for (int j = 0; j < slaveIps.size(); j++) {
					String exip = slaveIps.get(j);
					if (exip != null && exip.equals(tmip)) {
						filterTag = true;
						break;
					}
				}
				if (filterTag) {
					continue;
				}

				slaveIps.add(tmip);
				leftSize--;
				if (leftSize == 0) {
					break;
				}
			}
		}
		return slaveIps;
	}

	private String getMacFromIPInCandD(String nodeIp) {
		// a.b.c.d -> MacC:MacD
		String[] nips = nodeIp.split("\\.");
		int nipc = Integer.parseInt(nips[C]);
		int nipd = Integer.parseInt(nips[D]);
		String nipcMac = getValidMacField(Integer.toHexString(nipc));
		String nipdMac = getValidMacField(Integer.toHexString(nipd));
		return nipcMac + ":" + nipdMac;
	}

	private static String ip2MacByRAW(String ip) {
		String[] nips = ip.split("\\.");
		int nipa = Integer.parseInt(nips[A]);
		int nipb = Integer.parseInt(nips[B]);
		int nipc = Integer.parseInt(nips[D]);
		int nipd = Integer.parseInt(nips[D]);
		String nipaMac = getValidMacField(Integer.toHexString(nipa));
		String nipbMac = getValidMacField(Integer.toHexString(nipb));
		String nipcMac = getValidMacField(Integer.toHexString(nipc));
		String nipdMac = getValidMacField(Integer.toHexString(nipd));
		String mac = XMMConstants.MAC_PREFIX + ":" + nipaMac + ":" + nipbMac
				+ ":" + nipcMac + ":" + nipdMac;

		return mac;
	}

	private List<Nic> generateNicListByIps(String headnodeip,
			List<String> slaveIps) {
		List<Nic> allLeases = new ArrayList<Nic>();
		List<Nic> slaveLeases = new ArrayList<Nic>();
		String firstNodeIp = slaveIps.get(0);
		String headNodeIp2Mac = getMacFromIPInCandD(headnodeip);
		StringBuilder macsb = new StringBuilder();
		for (int slaveNum = 0; slaveNum < slaveIps.size() - 1; slaveNum++) {
			String slip = slaveIps.get(slaveNum);
			String slipNext = slaveIps.get(slaveNum + 1);
			// headNodeMac:nextHopMac:selfIpMac.
			macsb.append(headNodeIp2Mac + ":");
			macsb.append(getMacFromIPInCandD(slipNext) + ":"
					+ getMacFromIPInCandD(slip));
			String slMac = macsb.toString();
			macsb.delete(0, macsb.length());
			if (VirtualNetworkManager.isMacUsed(slMac)) {
				log.warn("The self ip : " + slip + " and the next ip "
						+ slipNext + " are available, but the mac of self : "
						+ slMac + " is used. Ignore this error.");
				slaveIps.remove(slaveNum + 1);
				// retry.
				slaveNum--;
				continue;
			}
			Nic les = new Nic(slip, slMac);
			slaveLeases.add(les);
		}
		// The last slave has two fields in the mac are pointing to the master
		// node. TODO, we can use this information for other things.
		String lastIp = slaveIps.get(slaveIps.size() - 1);
		macsb.append(headNodeIp2Mac + ":" + headNodeIp2Mac + ":");
		macsb.append(getMacFromIPInCandD(lastIp));
		String lastMac = macsb.toString();
		macsb.delete(0, macsb.length());
		if (VirtualNetworkManager.isMacUsed(lastMac)) {
			log.warn("The last slave ip : " + lastIp
					+ " are available, but the mac of it : " + lastMac
					+ " is used. Ignore this error.");
			// TODO, Solve this error, later.
		}
		Nic lastSlaveLease = new Nic(lastIp, lastMac);
		slaveLeases.add(lastSlaveLease);

		// master.
		String fsnip2mac = getMacFromIPInCandD(firstNodeIp);
		macsb.append(fsnip2mac.split(":")[1]);
		macsb.append(":00");
		String netNumMac = getValidMacField(Integer.toHexString(slaveLeases
				.size() + 1));
		// netNumMac may be only two chars. 00:E2
		final int minNetNumMac = 5;
		if (netNumMac.length() < minNetNumMac) {
			netNumMac = "00:" + netNumMac;
		}
		macsb.append(":" + netNumMac);
		macsb.append(":" + headNodeIp2Mac);
		Nic hnls = new Nic(headnodeip, macsb.toString());
		allLeases.add(hnls);
		allLeases.addAll(slaveLeases);
		slaveLeases = null;
		return allLeases;
	}

	private String generateFirstSlaveNodeIp(VirtualNetwork vn) {
		String headnodeip = vn.getHeadNodeIp();
		// FIXME, the first slave node should in the same section of the head
		// node.
		String[] hips = headnodeip.split("\\.");
		int ipc = Integer.parseInt(hips[2]);
		// FIXME, we first choose the user selected ip address.
		List<Nic> nics = vn.getPrivateIpNics();
		for (int k = 0; k < nics.size(); k++) {
			Nic nic = (Nic) nics.get(k);
			if (nic.getIp().equals(headnodeip)) {
				continue;
			}
			String[] sips = nic.getIp().split("\\.");
			int sipc = Integer.parseInt(sips[2]);
			if (ipc == sipc) {
				if (VirtualNetworkManager.isIpUsed(nic.getIp())) {
					continue;
				}
				return nic.getIp();
			}
		}

		// FIXME, secondly, we generate a proper ip for the user.
		int ipd = Integer.parseInt(hips[D]);
		int i = 0;

		for (i = ipd + 1; i < MAX_LAST_IP_FIELD; i++) {
			String fsnip = hips[0] + "." + hips[1] + "." + hips[2] + "." + i;
			if (VirtualNetworkManager.isIpUsed(fsnip)) {
				// ignore error, go to search the valid ip in the same section
				// of head node.
				continue;
			}
			return fsnip;
		}
		if (i == MAX_LAST_IP_FIELD) {
			// In the bigger end of the ip section, there is no any un-used ip
			// to assign, so search the smaller end.
			for (i = 1; i < ipd; i++) {
				String fsnip = hips[0] + "." + hips[1] + "." + hips[2] + "."
						+ i;
				if (VirtualNetworkManager.isIpUsed(fsnip)) {
					continue;
				}
				return fsnip;
			}
		}
		return null;
	}

	private List<String> trySlaveIps(int slaveNodeNum, String trySlaveNodeIp) {
		List<String> result = new ArrayList<String>();
		String[] fsnips = trySlaveNodeIp.split("\\.");
		int fsnipc = Integer.parseInt(fsnips[C]);
		int fsnipd = Integer.parseInt(fsnips[D]);
		int nextIpd = 0;
		
		for (nextIpd = fsnipd; nextIpd < MAX_LAST_IP_FIELD; nextIpd++) {
			// generate next ip address;
			String nextSlaveNodeip = XMMConstants.IP_PREFIX + fsnips[2] + "."
					+ nextIpd;
			// check ip is used?
			if (VirtualNetworkManager.isIpUsed(nextSlaveNodeip)) {
				// go on search.
				continue;
			}
			result.add(nextSlaveNodeip);
			slaveNodeNum--;
			if (slaveNodeNum == 0) {
				return result;
			}
		}
		if (nextIpd == MAX_LAST_IP_FIELD) {
			int nextIpc = fsnipc + 1;
			if (nextIpc > MAX_IP_FIELD) {
				// FIXME Bug fixed here, we need to check there isn't enough ip
				// space case. Xiaoyi Lu fixed at 2009.09.27.
				if (isIpReverse) {
					log.warn("There is not enough free ip address to "
							+ "allocate.");
					// FIXME because when the system running, other ip resource
					// will be released.
					isIpReverse = false;
					return result;
				}
				nextIpc = 0;
				isIpReverse = true;
			}
			nextIpd = 1;

			// TODO Test clearly.
			List<String> slip = this.trySlaveIps(slaveNodeNum,
					XMMConstants.IP_PREFIX + nextIpc + "." + nextIpd);
			result.addAll(slip);
		}
		// FIXME because when the system running, other ip resource will be
		// released.
		isIpReverse = false;
		// After the whole loop, this result is the try-best one.
		return result;
	}

	/*
	 * filed should be a hex string.
	 */
	private static String getValidMacField(String field) {
		if (field == null || field.length() == 0) {
			return "00";
		}
		StringBuilder sb = new StringBuilder();
		if (field.length() == 1) {
			sb.append("0" + field);
			return sb.toString();
		}
		if (field.length() == 2) {
			return field;
		}
		if (field.length() % 2 == 1) {
			field = "0" + field;
		}
		char[] tt = field.toCharArray();
		for (int i = 0; i < tt.length; i = i + 2) {
			sb.append(tt[i] + tt[i + 1] + ":");
		}
		// extra ':' should be cut.
		String ret = sb.toString();
		return ret.substring(0, sb.length() - 1);
	}

	public void destroy(Asset asset) throws Exception {
		log.info("VirtualNetwork (" + asset.getName()
				+ ") of VirtualNetoworkAC is destroying.");

		// Two steps, 1st, delete vn from opennebula; 2nd, delete it from
		// naming and update the ip and mac pools. Don't delete the virtual gw
		// for other clusters.
		// FIXME now we donot use opennebula to store virtual network
		// information.
		VirtualNetwork vn = new VirtualNetwork(asset);
		// FIXME, bug fixed, we should check the vn is used or not.
		String vcid = vn.getClusterID();
		if (vcid != null && !"".equals(vcid)) {
			VirtualCluster vc = VirtualClusterManager.getInstance().view(vcid);
			if (vc != null) {
				throw new Exception("The virtual network " + vn.getGuid()
						+ " is used by virtual cluster " + vc.getName()
						+ " now, please destroy it firstly.");
			} else {
				log.warn("Ignore error: the virtual network has been used"
						+ " by a cluster with id " + vcid
						+ ", but this cluster is not exist.");
				// Ignore this errror.
			}
		}

		Partition par = PartitionManager.getInstance()
				.view(vn.getPartitionId());
		String nodeType = null;
		if (par.getAssetController().equals(PartitionAC.class.getName())) {
			nodeType = par.getAttributes().get(
					PartitionAC.REQUIRED_ATTR_NODETYPE);
			if (PartitionAC.VM.equals(nodeType)) {
				this.destroyVN4VM(vn);
			} else {
				this.destroyVN4PM(vn);
			}
		} else {
			// nothing to do now.
			return;
		}
		log.info("VirtualNetwork (" + asset.getName()
				+ ") of ElasticVirtualNetoworkAC is destroyed successfully.");
	}

	private void destroyVN4PM(VirtualNetwork vn) throws Exception {
		// FIXME the physical nodes in vn4pm will be setted to IDLE state when
		// release the virtual cluster.
		if (vn.isNeedToReserveNode() && !vn.isAutoCreate()) {
			// FIXME this condition indicates the vn is created by mannual and
			// is reserved before, so we must unreserve them when destroy the
			// virtual network.
			this.unreservePhysicalNodesByNicList(vn.getPrivateIpNics());
		}
		log.info("Destroy virtual network for phsical cluster OK.");
	}

	private void unreservePhysicalNodesByNicList(List<Nic> nics)
			throws Exception {
		List<String> aips = new ArrayList<String>();
		for (int i = 0; i < nics.size(); i++) {
			Nic nic = (Nic) nics.get(i);
			aips.add(nic.getIp());
		}
		this.unreservePhysicalNodesByIpList(aips);
	}

	private void unreservePhysicalNodesByIpList(List<String> aips)
			throws Exception {
		String[] searchConditions = new String[] { "name" };
		String[] operators = new String[] { "=" };
		for (int i = 0; i < aips.size(); i++) {
			String nicIp = (String) aips.get(i);
			Object[] values = new Object[] { nicIp };
			List<PhysicalNode> pnlist = PartitionManager.getInstance()
					.searchPhysicalNode(searchConditions, operators, values);
			// TODO here we update physical node state immedially is not
			// very good, and we will add transaction support to make sure
			// after all physical nodes are chosen correctly,
			// then update the states. Marked at 2010.07-13.
			if (pnlist != null && !pnlist.isEmpty()) {
				PhysicalNode pn = pnlist.get(0);
				pn.setAssetState(AssetConstants.AssetState.IDLE);
				PartitionManager.getInstance().updatePhysicalNodeInfo(
						pn.getGuid(), pn);
			}
		}
	}

	private void destroyVN4VM(VirtualNetwork vn) {
		// VirtualClient vc = VirtualManager.getInstance().getVirtualClient();
		// /*
		// * If an error occurred, then the exception will be thrown.
		// */
		// vc.virtualNetworkFree(vn);
		List<Nic> nics = vn.getPrivateIpNics();
		for (int i = 0; i < nics.size(); i++) {
			Nic le = (Nic) nics.get(i);
			VirtualNetworkManager.triggerIpPoolRemove(le);
			VirtualNetworkManager.triggerMacPoolRemove(le);
		}
		// FIXME support addtional public ip mechanism, Xiaoyi Lu added at
		// 2009-12-22.
		if (VirtualNetworkManager.isPublicIpEnable()) {
			HashMap<String, Nic> pipnic = vn.getPublicIpNics();
			if (pipnic != null) {
				Iterator<Nic> it = pipnic.values().iterator();
				while (it.hasNext()) {
					Nic nic = (Nic) it.next();
					VirtualNetworkManager.triggerIpPoolRemove(nic);
					VirtualNetworkManager.triggerMacPoolRemove(nic);
					if (nic.getIp() != null && !"".equals(nic.getIp())) {
						VirtualNetworkManager.getPublicIpPool()
							.add(nic.getIp());
					}
				}
			}
		}
		log.info("Destroy virtual network for virtual cluster OK.");
	}

	public VirtualNetwork addNode(VirtualNetwork vn, String privateIp,
			HashMap<String, String> attributes) throws Exception {
		// FIXME 1st, according to the virtual network for vm or pm, we adopt
		// different method to add new node.
		// 2nd, if the private ip is null or blank, we select a proper ip
		// address automatically.
		// 3rd, if the virtual network is enlarged for vm, we update the ipPool
		// in memeory; if for pm, we update the physical node's state.
		// 4th, handle public ip information for vm's network.
		// 5th, modify head node mac for vm's network, due to size changed.
		Partition par = PartitionManager.getInstance()
				.view(vn.getPartitionId());
		String nodeType = null;
		VirtualNetwork result = vn;
		if (par.getAssetController().equals(PartitionAC.class.getName())) {
			nodeType = par.getAttributes().get(
					PartitionAC.REQUIRED_ATTR_NODETYPE);
			if (PartitionAC.VM.equals(nodeType)) {
				result = this.addVMNode2VN(vn, privateIp, attributes);
			} else {
				result = this.addPMNode2VN(vn, privateIp, attributes);
			}
		} else {
			// nothing to do now.
			return result;
		}
		return result;
	}

	private VirtualNetwork addPMNode2VN(VirtualNetwork vn, String privateIp,
			HashMap<String, String> attributes) throws Exception {
		VirtualNetwork result = vn.clone();
		List<String> aips = new ArrayList<String>();
		if (privateIp == null || "".equals(privateIp)) {
			final int num = 1;
			aips = VirtualNetworkManager.findAvailableIpList4PM(num,
					vn.getPartitionId());
			result = this.addIp2PrivateIpNics(aips.get(0), result, false);
			// Bug fixed here, the new ip nic should set to result.
			// result.getPrivateIpNics().addAll(aips);
		} else {
			if (VirtualNetworkManager.isIpInPhysicalNodeIdle(privateIp)) {
				aips.add(privateIp);
				result = this.addIp2PrivateIpNics(aips.get(0), result, false);
				// result.getPrivateIpNics().addAll(aips);
			} else {
				String msg = "The required ip " + privateIp
						+ " is used already.";
				log.error(msg);
				throw new Exception(msg);
			}
		}
		// XXX here we don't reserve physical node, because we do not know the
		// lease id (cluster id). In the future, we may use an option for user
		// to select whether or not to reserve the physical node.
		if (vn.isNeedToReserveNode() && !vn.isAutoCreate()) {
			this.reservePhysicalNodesByIpList(aips);
		}
		return result;
	}

	private void reservePhysicalNodesByIpList(List<String> aips) 
		throws Exception {
		String[] searchConditions = new String[] { "name" };
		String[] operators = new String[] { "=" };
		for (int i = 0; i < aips.size(); i++) {
			String nicIp = (String) aips.get(i);
			Object[] values = new Object[] { nicIp };
			List<PhysicalNode> pnlist = PartitionManager.getInstance()
					.searchPhysicalNode(searchConditions, operators, values);
			// TODO here we update physical node state immedially is not
			// very good, and we will add transaction support to make sure
			// after all physical nodes are chosen correctly,
			// then update the states. Marked at 2010.07-13.
			if (pnlist != null && !pnlist.isEmpty()) {
				PhysicalNode pn = pnlist.get(0);
				pn.setAssetState(AssetConstants.AssetState.RESERVED);
				PartitionManager.getInstance().updatePhysicalNodeInfo(
						pn.getGuid(), pn);
			}
		}
	}

	private VirtualNetwork addVMNode2VN(VirtualNetwork vn, String privateIp,
			HashMap<String, String> attributes) throws Exception {
		VirtualNetwork result;
		String fip = "";
		if (privateIp == null || "".equals(privateIp)) {
			final int num = 1;
			List<String> aips = this.findAvailableIpByHeadNodeIp4VM(num,
					vn.getHeadNodeIp());
			if (aips == null) {
				throw new Exception(
						"No available ip address to be allocated currently.");
			}
			fip = aips.get(0);
			result = this.addIp2PrivateIpNics(fip, vn, true);
		} else {
			if (!VirtualNetworkManager.isIpUsed(privateIp)) {
				fip = privateIp;
				result = this.addIp2PrivateIpNics(fip, vn, true);
			} else {
				String msg = "The required ip " + privateIp
						+ " is used already.";
				log.error(msg);
				throw new Exception(msg);
			}
		}
		result.setNetworkSize(result.getPrivateIpNics().size());

		VirtualNetwork revn = this.reconfigNewNic(result, fip);

		Nic newLastNic = (Nic) revn.getPrivateIpNics().get(
				revn.getPrivateIpNics().size() - 1);
		VirtualNetworkManager.triggerIpPoolAdd(newLastNic);
		VirtualNetworkManager.triggerMacPoolAdd(newLastNic);

		// handle public ip information.
		VirtualNetwork retvn = this.handlePublicIpForNewVM(revn, fip,
				attributes);
		VirtualNetwork returnvn = this.modifyHeadNodeMacByNetworkSize(retvn);
		log.info("success to add a new vm node (" + fip
				+ ") to virtual network (" + vn.getName() + ").");
		return returnvn;
	}

	private VirtualNetwork handlePublicIpForNewVM(VirtualNetwork vn,
			String newip, HashMap<String, String> attributes) {
		try {
			if (VirtualNetworkManager.isPublicIpEnable()) {
				String piptag = attributes
						.get(VirtualNetworkAC.OPTIONAL_ATTR_PUBLICIP_NUM);
				if (piptag == null || "".equals(piptag)) {
					return vn;
				}
				int num = Integer.parseInt(piptag);
				if (num > 0) {
					// see any num which is bigger than zero as 1;
					if (VirtualNetworkManager.getPublicIpPool().size() > 0) {
						String tmpPip = (String) VirtualNetworkManager
							.getPublicIpPool().remove(0);
						while (VirtualNetworkManager.isIpUsed(tmpPip)) {
							log.warn("There is some error occured when "
									+ "maintaineces the publicIpPool, "
									+ "but ignore this.");
							tmpPip = (String) VirtualNetworkManager
								.getPublicIpPool().remove(0);
							// XXX tmpPip is null or blank will break this
							// loop.
						}
						if (tmpPip != null && !"".equals(tmpPip)) {
							Nic pipnic = new Nic(tmpPip, ip2MacByRAW(tmpPip));
							HashMap<String, Nic> nics = vn.getPublicIpNics();
							nics.put(newip, pipnic);
							vn.setPublicIpNics(nics);
							VirtualNetworkManager.triggerIpPoolAdd(pipnic);
							VirtualNetworkManager.triggerMacPoolAdd(pipnic);
							log.info("Allocate a new public ip " + tmpPip
									+ " for the new node " + newip
									+ " in the virtual network " 
									+ vn.getName());
						}
					} else {
						return vn;
					}

					HashMap<String, String> map = vn.getAttributes();
					String tag = map
							.get(VirtualNetworkAC.OPTIONAL_ATTR_PUBLICIP_NUM);
					if (tag == null || "".equals(tag)) {
						map.put(VirtualNetworkAC.OPTIONAL_ATTR_PUBLICIP_NUM,
								"1");
					} else {
						int vnpipnumtag = Integer.parseInt(tag) + 1;
						map.put(VirtualNetworkAC.OPTIONAL_ATTR_PUBLICIP_NUM, ""
								+ vnpipnumtag);
					}
					vn.setAttributes(map);
				} else {
					// do nothing, if num equal or smaller than zero.
					log.debug("No need to allocate public ip"
							+ " address for the new node"
							+ " in the virtual network " + vn.getName());
				}
			}
		} catch (Exception e) {
			//String msg = "Error occurred when handle "
			//		+ "public ip for the new node,due to " + e.toString();
			log.warn(e.toString());
			// Ignore this error.
		}
		return vn;
	}

	private VirtualNetwork reconfigNewNic(VirtualNetwork vn, String newip)
			throws Exception {
		// FIXME 1st, use the last ip nic in vn to construct the overlay again,
		// 2nd, triggerMacPoolRemove the modified mac address.
		// 3rd, the new network info will be triggered to add in the
		// addVMNode2VN method.
		VirtualNetwork retvn = vn.clone();
		Nic lastNic = this.findLastNic(vn);
		String newmac = this.getMacFromIPInCandD(newip);
		String hnmac = this.getMacFromIPInCandD(vn.getHeadNodeIp());
		String lastNicNewMac = hnmac + ":" + newmac + ":"
				+ this.getMacFromIPInCandD(lastNic.getIp());
		if (VirtualNetworkManager.isMacUsed(lastNicNewMac)) {
			throw new Exception("The new node is added failed due to "
					+ "the allocated mac address is used already.");
		}
		// TODO if error occurred in the later execution, how to handle it? this
		// situation should be improved later. Introduce compensation mechanism.
		VirtualNetworkManager.triggerMacPoolRemove(lastNic);
		return this.modifyMacOfPrivateIpNic(lastNicNewMac, lastNic, retvn);
	}

	private VirtualNetwork modifyMacOfPrivateIpNic(String targetNicNewMac,
			Nic targetNic, VirtualNetwork retvn) {
		List<Nic> nics = retvn.getPrivateIpNics();
		for (int i = nics.size() - 1; i >= 0; i--) {
			Nic nic = (Nic) nics.get(i);
			if (nic.getIp().equals(targetNic.getIp())) {
				nic.setMac(targetNicNewMac);
				nics.remove(i);
				// FIXME Keep the order is preferably.
				nics.add(i, nic);
				retvn.setPrivateIpNics(nics);
				return retvn;
			}
		}
		return retvn;
	}

	private Nic findLastNic(VirtualNetwork vn) {
		List<Nic> alist = vn.getPrivateIpNics();
		String mac = this.getMacFromIPInCandD(vn.getHeadNodeIp());
		String[] macs = mac.split(":");
		for (int i = alist.size() - 1; i >= 0; i--) {
			// The last one is the newest one and has no mac.
			Nic nic = (Nic) alist.get(i);
			String lmac = nic.getMac();
			if (lmac == null || "".equals(lmac)) {
				continue;
			}
			String[] lmacs = lmac.split(":");
			if (lmacs.length == XMMConstants.MAC_LENGTH) {
				if (lmacs[A].equals(macs[A]) && lmacs[B].equals(macs[B])) {
					if (lmacs[C].equals(macs[A]) && lmacs[D].equals(macs[B])) {
						return nic;
					}
				}
			}
			continue;
		}
		return null;
	}

	private VirtualNetwork addIp2PrivateIpNics(String fip, VirtualNetwork vn,
			boolean isForVM) throws Exception {
		VirtualNetwork retvn = vn.clone();
		Nic nic = new Nic();
		nic.setIp(fip);
		if (isForVM) {
			String headMacSuffix = this.getMacFromIPInCandD(vn.getHeadNodeIp());
			String newmac = headMacSuffix + ":" + headMacSuffix + ":"
					+ this.getMacFromIPInCandD(fip);
			if (VirtualNetworkManager.isMacUsed(newmac)) {
				throw new Exception("This new node is added failed, "
						+ "due to the mac is used already.");
			}
			nic.setMac(newmac);
		}
		List<Nic> alist = vn.getPrivateIpNics();
		alist.add(nic);
		retvn.setPrivateIpNics(alist);
		retvn.setNetworkSize(retvn.getPrivateIpNics().size());
		return retvn;
	}

	private List<String> findAvailableIpByHeadNodeIp4VM(int num,
			String headNodeIp) throws Exception {
		// FIXME, the slave node should in the same section of the head
		// node preferably.
		String[] hips = headNodeIp.split("\\.");
		// int ipc = Integer.parseInt(hips[2]);
		// FIXME we generate a proper ip for the user.
		int ipd = Integer.parseInt(hips[D]);
		int i = 0;
		List<String> aips = new ArrayList<String>();
		// FIXME firstly, find the ip in the headnode ip same section.
		// Other policy will be implemented later.
		for (i = ipd + 1; i < MAX_LAST_IP_FIELD; i++) {
			String fsnip = hips[0] + "." + hips[1] + "." + hips[2] + "." + i;
			if (VirtualNetworkManager.isIpUsed(fsnip)) {
				// ignore error, go to search the valid ip in the same
				// section
				// of head node.
				continue;
			}
			aips.add(fsnip);
			if (aips.size() >= num) {
				for (int k = 0; k < (aips.size() - num); k++) {
					aips.remove(0);
				}
				return aips;
			}
		}
		if (i == MAX_LAST_IP_FIELD) {
			// In the bigger end of the ip section, there is no any un-used
			// ip to assign, so search the smaller end.
			for (i = 1; i < ipd; i++) {
				String fsnip = hips[0] + "." + hips[1] + "." + hips[2] + "."
						+ i;
				if (VirtualNetworkManager.isIpUsed(fsnip)) {
					continue;
				}
				aips.add(fsnip);
				if (aips.size() >= num) {
					for (int k = 0; k < (aips.size() - num); k++) {
						aips.remove(0);
					}
					return aips;
				}
			}
		}

		boolean isBroken = false;
		final int brokenTag = 255;
		int tryNum = 0;
		if (aips.size() < num) {
			Random rand = new Random();
			for (int j = 0; j < num - aips.size(); j++) {
				if (isBroken) {
					throw new Exception(
							"No available ip to be allocated for new node.");
				}
				int hc = Math.abs(rand.nextInt(
						(int) System.currentTimeMillis())) % MAX_IP_FIELD;
				// avoid zero.
				int hd = Math.abs(rand.nextInt((int) System
						.currentTimeMillis())) % (MAX_LAST_IP_FIELD - 2) + 1;
				String fsnip = hips[0] + "." + hips[1] + "." + hc + "." + hd;
				if (VirtualNetworkManager.isIpUsed(fsnip)) {
					j--;
					tryNum++;
					if (tryNum > brokenTag) {
						isBroken = true;
					}
					continue;
				}
				aips.add(fsnip);
				if (aips.size() == num) {
					return aips;
				}
			}
		}
		return null;
	}

	public VirtualNetwork removeNode(VirtualNetwork vn, String privateIp,
			HashMap<String, String> attributes) throws Exception {
		// FIXME 1st, according to the virtual network for vm or pm, we adopt
		// different method to remove a node.
		// 2nd, if the private ip is null or blank, we select a proper ip
		// address automatically, but not the headnode ip for both vm and pm,
		// and not the first slave node ip for vm if the vn.size > 2.
		// 3rd, if the virtual network is narrowed for vm, we update the ipPool
		// in memeory; if for pm, we do not update the physical node's state,
		// left this operation to the virtual cluster mananger.
		// 4th, handle public ip information for vm's network.
		// 5th, modify head node mac for vm's network, due to size changed.
		Partition par = PartitionManager.getInstance()
				.view(vn.getPartitionId());
		String nodeType = null;
		VirtualNetwork result = vn;
		if (par.getAssetController().equals(PartitionAC.class.getName())) {
			nodeType = par.getAttributes().get(
					PartitionAC.REQUIRED_ATTR_NODETYPE);
			if (PartitionAC.VM.equals(nodeType)) {
				result = this.removeVMNodeFromVN(vn, privateIp, attributes);
			} else {
				result = this.removePMNodeFromVN(vn, privateIp, attributes);
			}
		} else {
			// nothing to do now.
			return result;
		}
		return result;
	}

	private VirtualNetwork removePMNodeFromVN(VirtualNetwork vn,
			String privateIp, HashMap<String, String> attributes)
			throws Exception {
		VirtualNetwork result;
		List<Nic> anics = new ArrayList<Nic>();
		if (privateIp == null || "".equals(privateIp)) {
			// FIXME indicates we can choose any ip except head node ip.
			final int num = 1;
			anics = this.findNoHeadPMNicsInVN(num, vn);
			result = this.removeNicFromPrivateIpNics(anics.get(0), vn, false);
		} else {
			if (privateIp.equals(vn.getHeadNodeIp())) {
				if (vn.getNetworkSize() == 1) {
					throw new Exception(
							"There is only one node, please destroy it.");
				} else {
					throw new Exception("The head node can not be deleted.");
				}
			}
			Nic nic = this.getNicByIpFromVN(vn, privateIp);
			if (nic == null) {
				throw new Exception("The node " + privateIp
						+ " is not exist.");
			}
			anics.add(nic);
			result = this.removeNicFromPrivateIpNics(nic, vn, false);
		}

		if (vn.isNeedToReserveNode() && !vn.isAutoCreate()) {
			this.unreservePhysicalNodesByNicList(anics);
		}
		log.info("success to remove a pm node (" + anics.get(0).getIp()
				+ ") from virtual network (" + vn.getName() + ").");
		return result;
	}

	private VirtualNetwork removeNicFromPrivateIpNics(Nic nic,
			VirtualNetwork vn, boolean isForVM)
			throws CloneNotSupportedException {
		VirtualNetwork retvn = vn.clone();
		List<Nic> nics = vn.getPrivateIpNics();
		for (int i = 0; i < nics.size(); i++) {
			String tmpip = nics.get(i).getIp();
			if (nic.getIp().equals(tmpip)) {
				nics.remove(i);
				break;
			}
		}
		//if (isForVM) {
			// no thing to do. left the reconfig work in the reconfigV4RemoveNic
			// method.
		//}
		retvn.setPrivateIpNics(nics);
		retvn.setNetworkSize(retvn.getPrivateIpNics().size());
		return retvn;
	}

	private List<Nic> findNoHeadPMNicsInVN(int num, VirtualNetwork vn)
			throws Exception {
		List<Nic> result = new ArrayList<Nic>();
		List<Nic> nics = vn.getPrivateIpNics();
		for (int i = 0; i < nics.size(); i++) {
			String ip = nics.get(i).getIp();
			if (ip.equals(vn.getHeadNodeIp())) {
				continue;
			}
			result.add(nics.get(i));
			num--;
			if (num == 0) {
				return result;
			}
		}
		if (result.size() == 0) {
			throw new Exception("The virtual network " + vn.getName()
					+ " only has one node, please destroy it.");
		}
		return result;
	}

	private VirtualNetwork removeVMNodeFromVN(VirtualNetwork vn,
			String privateIp, HashMap<String, String> attributes)
			throws Exception {
		VirtualNetwork result;
		Nic renic;
		if (privateIp == null || "".equals(privateIp)) {
			final int num = 1;
			List<Nic> anics = this.findNics2Remove4VMInVN(num, vn);
			if (anics == null) {
				throw new Exception("No proper node to be removed currently.");
			}
			renic = anics.get(0);
			result = this.removeNicFromPrivateIpNics(renic, vn, true);
		} else {
			if (privateIp.equals(vn.getHeadNodeIp())) {
				if (vn.getNetworkSize() == 1) {
					throw new Exception(
							"There is only one node, please destroy it.");
				} else {
					throw new Exception("The head node can not be deleted.");
				}
			}
			renic = this.getNicByIpFromVN(vn, privateIp);
			if (renic == null) {
				throw new Exception("The node " + privateIp + " is not exist.");
			}
			result = this.removeNicFromPrivateIpNics(renic, vn, true);
		}

		VirtualNetwork revn = this.reconfigVN4RemovedVNNic(result, renic);
		VirtualNetworkManager.triggerIpPoolRemove(renic);
		VirtualNetworkManager.triggerMacPoolRemove(renic);

		// handle public ip information.
		VirtualNetwork retvn = this.handlePublicIpForRemoveVM(revn,
				renic.getIp(), attributes);
		VirtualNetwork returnvn = this.modifyHeadNodeMacByNetworkSize(retvn);
		log.info("success to remove a vm node (" + renic.getIp()
				+ ") from virtual network (" + vn.getName() + ").");
		return returnvn;
	}

	private VirtualNetwork handlePublicIpForRemoveVM(VirtualNetwork vn,
			String removeip, HashMap<String, String> attributes) {
		HashMap<String, Nic> pubnics = vn.getPublicIpNics();
		if (pubnics != null && pubnics.containsKey(removeip)) {
			Nic pubnic = pubnics.remove(removeip);
			VirtualNetworkManager.triggerIpPoolRemove(pubnic);
			VirtualNetworkManager.triggerMacPoolRemove(pubnic);
			VirtualNetworkManager.getPublicIpPool().add(pubnic.getIp());
			HashMap<String, String> map = vn.getAttributes();
			String tag = map.get(VirtualNetworkAC.OPTIONAL_ATTR_PUBLICIP_NUM);
			if (tag != null && !"".equals(tag)) {
				int vnpipnumtag = 0;
				try {
					vnpipnumtag = Integer.parseInt(tag) - 1;
					if (vnpipnumtag < 0) {
						vnpipnumtag = 0;
					}
					map.put(VirtualNetworkAC.OPTIONAL_ATTR_PUBLICIP_NUM, ""
							+ vnpipnumtag);
				} catch (Exception e) {
					log.error(e.getMessage());
					// ignore this error.
				}
			}
			vn.setAttributes(map);
			vn.setPublicIpNics(pubnics);
			log.info("Free a public ip " + pubnic.getIp() + " for the node "
					+ removeip + " in the virtual network " + vn.getName());
		}
		return vn;
	}

	private Nic getHeadNodeNic(VirtualNetwork vn) {
		return this.getNicByIpFromVN(vn, vn.getHeadNodeIp());
	}

	private VirtualNetwork modifyHeadNodeMacByNetworkSize(VirtualNetwork vn)
			throws Exception {
		VirtualNetwork ret = vn.clone();
		if (vn.getNetworkSize() != vn.getPrivateIpNics().size()) {
			ret.setNetworkSize(vn.getPrivateIpNics().size());
		}
		int size = ret.getNetworkSize();
		Nic hnnic = this.getHeadNodeNic(vn);
		String[] hnmacs = hnnic.getMac().split(":");
		String netNumMac = getValidMacField(Integer.toHexString(size));
		// net_num_mac may be only two chars. 00:E2
		final int minNetNumMac = 5;
		if (netNumMac.length() < minNetNumMac) {
			netNumMac = "00:" + netNumMac;
		}
		String hnmac = hnmacs[A] + ":" + hnmacs[B] + ":" + netNumMac + ":"
				+ hnmacs[E] + ":" + hnmacs[F];
		// As common case, the follow situation should not be occurred.
		if (VirtualNetworkManager.isMacUsed(hnmac)) {
			log.fatal("The head node mac address can not be modified now.");
			throw new Exception(
					"The head node network info can not be modified now.");
		}
		VirtualNetworkManager.triggerMacPoolRemove(hnnic);
		hnnic.setMac(hnmac);
		List<Nic> nics = ret.getPrivateIpNics();
		for (int i = 0; i < nics.size(); i++) {
			String tmpip = nics.get(i).getIp();
			if (hnnic.getIp().equals(tmpip)) {
				nics.remove(i);
				nics.add(i, hnnic);
				break;
			}
		}
		ret.setPrivateIpNics(nics);
		return ret;
	}

	private VirtualNetwork reconfigVN4RemovedVNNic(VirtualNetwork vn,
			Nic removedNic) throws Exception {
		// XXX Notice : the removedNic is chosen to be removed, and already
		// removed form the vn.
		VirtualNetwork ret = vn.clone();
		List<Nic> nics = vn.getPrivateIpNics();
		String fsnip = this.findFirstSlaveNodeIp(vn);
		if (fsnip.equals(removedNic.getIp())) {
			if (vn.getNetworkSize() == 1) {
				// indicates only left head node; modify it point to himself.
				Nic hnnic = nics.remove(0);
				String maccd = this.getMacFromIPInCandD(hnnic.getIp());
				String newmac = maccd.split(":")[1] + ":00:00:01:" + maccd;
				if (VirtualNetworkManager.isMacUsed(newmac)) {
					log.fatal("Due to the new mac address " + newmac
							+ " of head node is used, so the node "
							+ removedNic.getIp() + " can not be removed.");
					throw new Exception("The node " + removedNic.getIp()
							+ " can not be removed , "
							+ "because some network can not be modified.");
				}
				VirtualNetworkManager.triggerMacPoolRemove(hnnic);
				hnnic.setMac(newmac);
				VirtualNetworkManager.triggerMacPoolAdd(hnnic);
				nics.add(0, hnnic);
				ret.setPrivateIpNics(nics);
				return ret;
			} else {
				// FIXME here need to config in a special way: modify head
				// node's maca field, and point to another node who has the same
				// ip section of head node. If it can not find another ip, then
				// throw exception, that indicates the first slave node can not
				// be removed.
				Nic anafsn = this.findAnotherNodeAsFirstSlaveNodeInVN(vn);
				if (anafsn == null) {
					log.fatal("Due to the node " + fsnip
							+ " is the first slave node of virtual network "
							+ vn.getName()
							+ ", and no other node in the same ip section"
							+ " of head node, so it can not be removed.");
					throw new Exception("The node " + removedNic.getIp()
							+ " can not be removed , "
							+ "because some network can not be modified. "
							+ "Please remove another one.");
				}
				Nic hnic = null;
				int hnindex = 0;
				for (int i = 0; i < nics.size(); i++) {
					Nic nic = nics.get(i);
					if (nic.getIp().equals(vn.getHeadNodeIp())) {
						hnic = nic;
						hnindex = i;
						break;
					}
				}
				String[] hnmacs = hnic.getMac().split(":");
				String[] anafsnmacs = anafsn.getMac().split(":");
				String newmac = anafsnmacs[F] + ":" + hnmacs[B] + ":"
						+ hnmacs[C] + ":" + hnmacs[D] + ":" + hnmacs[E] + ":"
						+ hnmacs[F];
				if (VirtualNetworkManager.isMacUsed(newmac)) {
					log.fatal("Due to the new mac address " + newmac
							+ " of head node is used, so the node "
							+ removedNic.getIp() + " can not be removed.");
					throw new Exception("The node " + removedNic.getIp()
							+ " can not be removed , "
							+ "because some network can not be modified.");
				}
				VirtualNetworkManager.triggerMacPoolRemove(hnic);
				nics.remove(hnindex);
				hnic.setMac(newmac);
				VirtualNetworkManager.triggerMacPoolAdd(hnic);
				nics.add(hnindex, hnic);
				ret.setPrivateIpNics(nics);
				return ret;
			}
		}

		String maccd = this.getMacFromIPInCandD(removedNic.getIp());
		String[] nextmacs = removedNic.getMac().split(":");
		for (int i = 0; i < nics.size(); i++) {
			Nic tmpnic = nics.get(i);
			String tmpmac = tmpnic.getMac();
			String[] tmpmacs = tmpmac.split(":");
			String tmpmaccd = tmpmacs[C] + ":" + tmpmacs[D];
			// this nic is the removed nic's forerunner.
			// change the mac address for this nic to point the removed nic's
			// next nic.
			if (maccd.equals(tmpmaccd)) {
				tmpmacs[C] = nextmacs[C];
				tmpmacs[D] = nextmacs[D];
				VirtualNetworkManager.triggerMacPoolRemove(tmpnic);
				tmpmac = tmpmacs[A] + ":" + tmpmacs[B] + tmpmacs[C]
						+ tmpmacs[D] + tmpmacs[E] + tmpmacs[F];
				tmpnic.setMac(tmpmac);
				nics.remove(i);
				// keep the order is preferably.
				nics.add(i, tmpnic);
				ret.setPrivateIpNics(nics);
				break;
			}
		}
		return ret;
	}

	private Nic findAnotherNodeAsFirstSlaveNodeInVN(VirtualNetwork vn) {
		List<Nic> nics = vn.getPrivateIpNics();
		String hnip = vn.getHeadNodeIp();
		for (int i = 0; i < nics.size(); i++) {
			Nic nic = nics.get(i);
			String snip = nic.getIp();
			if (hnip.equals(snip)) {
				continue;
			}
			if (this.isIpInSameSection(snip, hnip)) {
				return nic;
			}
		}
		return null;
	}

	private boolean isIpInSameSection(String snip, String hnip) {
		String[] snips = snip.split(".");
		String[] hnips = hnip.split(".");
		if (snips[0].equals(hnips[0]) && snips[1].equals(hnips[1])
				&& snips[2].equals(hnips[2])) {
			return true;
		}
		return false;
	}

	private Nic getNicByIpFromVN(VirtualNetwork vn, String ip) {
		List<Nic> nics = vn.getPrivateIpNics();
		if (nics == null || nics.size() == 0) {
			return null;
		}
		for (int i = 0; i < nics.size(); i++) {
			Nic nic = nics.get(i);
			if (nic.getIp().equals(ip)) {
				return nic;
			}
		}
		return null;
	}

	private String findFirstSlaveNodeIp(VirtualNetwork vn) {
		String firstSlaveIp = "";
		List<Nic> nics = vn.getPrivateIpNics();
		if (nics.size() == 1) {
			return firstSlaveIp;
		}
		for (int i = 0; i < nics.size(); i++) {
			String ip = nics.get(i).getIp();
			if (ip.equals(vn.getHeadNodeIp())) {
				String headmac = nics.get(i).getMac();
				String ipdinmac = headmac.split(":")[0];
				String tmpip = Integer.toString(Integer.parseInt(
						ipdinmac, HEX));
				firstSlaveIp = vn.getHeadNodeIp().substring(0,
						vn.getHeadNodeIp().lastIndexOf("."))
						+ "." + tmpip;
			}
		}
		return firstSlaveIp;
	}

	private List<Nic> findNics2Remove4VMInVN(int num, VirtualNetwork vn)
			throws Exception {
		List<Nic> result = new ArrayList<Nic>();
		List<Nic> nics = vn.getPrivateIpNics();
		String firstSlaveIp = this.findFirstSlaveNodeIp(vn);
		for (int i = nics.size() - 1; i >= 0; i--) {
			String ip = nics.get(i).getIp();
			if (ip.equals(vn.getHeadNodeIp())) {
				continue;
			}
			if (vn.getNetworkSize() > 2 && ip.equals(firstSlaveIp)) {
				// FIXME if the network size is bigger than 2, the first slave
				// ip is not selected.
				continue;
			}
			result.add(nics.get(i));
			num--;
			if (num == 0) {
				return result;
			}
		}
		if (result.size() == 0) {
			throw new Exception("The virtual network " + vn.getName()
					+ " only has one node, please destroy it.");
		}
		return result;
	}

	public Asset poll(Asset asset) throws Exception {
		log.info("VirtualNetwork (" + asset.getName() + ") of "
				+ this.getClass().getName() + " is polling itself.");
		return asset;
	}

	public double calculatePrice(Asset asset, HashMap<String, String> params)
			throws Exception {
		return asset.getPrice();
	}

	public Asset init(Asset asset) throws Exception {
		log.info("VirtualNetwork (" + asset.getName() + ") of "
				+ this.getClass().getName() + " is init successfully.");
		return asset;
	}

	public Asset antiInit(Asset asset) throws Exception {
		log.info("VirtualNetwork (" + asset.getName() + ") of "
				+ this.getClass().getName() + " is antiIniting.");
		return asset;
	}

	public Asset provision(Asset asset) throws Exception {
		log.info("VirtualNetwork (" + asset.getName() + ") of "
				+ this.getClass().getName() + " is provisioning successfully.");
		return asset;
	}

	public Asset revoke(Asset asset) throws Exception {
		log.info("VirtualNetwork (" + asset.getName() + ") of "
				+ this.getClass().getName() + " is revoking itself.");
		return asset;
	}

}
