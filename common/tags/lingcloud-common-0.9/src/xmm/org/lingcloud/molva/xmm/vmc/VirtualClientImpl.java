/*
 *  @(#)VirtualClientImpl.java  2010-5-27
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

package org.lingcloud.molva.xmm.vmc;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.opennebula.client.Client;
import org.opennebula.client.cluster.Cluster;
import org.opennebula.client.OneResponse;

import org.lingcloud.molva.xmm.ac.PVNPNController;
import org.lingcloud.molva.xmm.pojos.Nic;
import org.lingcloud.molva.xmm.pojos.Partition;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.pojos.VirtualNetwork;
import org.lingcloud.molva.xmm.pojos.VirtualNode;
import org.lingcloud.molva.xmm.services.PartitionManager;
import org.lingcloud.molva.xmm.util.XMMConstants;
import org.lingcloud.molva.xmm.util.XMMUtil;
import org.lingcloud.molva.xmm.vmc.one.Host;
import org.lingcloud.molva.xmm.vmc.one.VirtualMachine;
import org.lingcloud.molva.xmm.vmc.one.VmTemplate;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-9-23<br>
 * @author Xiaoyi Lu<br>
 */
public class VirtualClientImpl implements VirtualClient {

	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(VirtualClientImpl.class);

	private final Client client;

	private static final Map<URL, Client> CLIENTS = Collections
			.synchronizedMap(new WeakHashMap<URL, Client>());

	private static String vmKiller;

	public static final String IM_PRIFX = "im_";

	public static final String VMM_PRIFX = "vmm_";

	public static final String TM_PRIFX = "tm_";

	private static int currentClusterInPublicNode = 0;

	private static final String HVM_LOADER_LOCATION 
				= "/usr/lib/xen/boot/hvmloader";

	// user to index hard disk in hvm loader. e.g. hda,hdb,hdc...
	// TODO now we support seven disks.
	public static final String[] HD_ARRAY = new String[] { "hda", "hdb", "hdc",
			"hdd", "hde", "hdf", "hdg" };

	private static final String DEVICE_MODEL_LOCATION 
				= "'/usr/' + arch_libdir + '/xen/bin/qemu-dm'";

	private static final String PYGRUB_LOADER_LOCATION = "/usr/bin/pygrub";

	private static final int VNC_PORT = 5900;

	/**
	 * Constructs the client stub with a reference to the XMLRPC server, e.g.
	 * "http://localhost:8080/RPC2"
	 * 
	 * @param serverURL
	 *            XMLRPC server URL
	 * @throws Exception
	 */
	public VirtualClientImpl(String serverURL, String token) throws Exception {
		this(new URL(serverURL), token);
	}

	/**
	 * Constructs the client stub with a URL reference to the XMLRPC server,
	 * e.g. "http://localhost:8080/RPC2"
	 * 
	 * @param serverURL
	 *            XMLRPC server URL
	 * @throws Exception
	 */
	public VirtualClientImpl(URL serverURL, String token) throws Exception {
		if (serverURL == null) {
			throw new IllegalStateException("Server URL must be supplied");
		}

		if (CLIENTS.containsKey(serverURL)) {
			client = CLIENTS.get(serverURL);
			return;
		}

		synchronized (this) {
			client = new Client(token, serverURL.toString());
			CLIENTS.put(serverURL, client);
		}
	}

	public PhysicalNode allocateVmProvisionNode(PhysicalNode pn)
			throws Exception {
		String privateIp = pn.getPrivateIps()[0];
		if (privateIp == null || "".equals(privateIp)) {
			throw new Exception("The privateIp is null or blank, in "
					+ "allocateVmProvisionNode method.");
		}
		String hyper = pn.getAttributes().get(XMMConstants.HYPERVISOR);
		if (hyper == null || "".equals(hyper)) {
			throw new Exception("The hypervisor is null or blank, in "
					+ "allocateVmProvisionNode method.");
		}
		String im = "";
		String vmm = "";
		if (hyper.equalsIgnoreCase(XMMConstants.HYPERVISOR_XEN)) {
			im = IM_PRIFX + XMMConstants.HYPERVISOR_XEN;
			vmm = VMM_PRIFX + XMMConstants.HYPERVISOR_XEN;
		} else if (hyper.equalsIgnoreCase(XMMConstants.HYPERVISOR_KVM)) {
			im = IM_PRIFX + XMMConstants.HYPERVISOR_KVM;
			vmm = VMM_PRIFX + XMMConstants.HYPERVISOR_KVM;
		} else if (hyper.equalsIgnoreCase(XMMConstants.HYPERVISOR_VMWARE)) {
			im = IM_PRIFX + XMMConstants.HYPERVISOR_VMWARE;
			vmm = VMM_PRIFX + XMMConstants.HYPERVISOR_VMWARE;
		} else if (hyper.equalsIgnoreCase(XMMConstants.HYPERVISOR_EC2)) {
			im = IM_PRIFX + XMMConstants.HYPERVISOR_EC2;
			vmm = VMM_PRIFX + XMMConstants.HYPERVISOR_EC2;
		} else {
			throw new Exception(
					"Not support virtualization hypervisor type, and it "
							+ "should be xen, kvm, vmware, ec2.");
		}

		String trans = pn.getTransferway();
		if (trans == null || "".equals(trans)) {
			throw new Exception(
					"The file transfer way of String is null or blank, in"
							+ " allocateVmProvisionNode method.");
		}
		String tm = "";
		if (trans.equalsIgnoreCase(XMMConstants.FILE_TRANSFER_NFS)) {
			tm = TM_PRIFX + XMMConstants.FILE_TRANSFER_NFS;
		} else if (trans.equalsIgnoreCase(XMMConstants.FILE_TRANSFER_SSH)) {
			tm = TM_PRIFX + XMMConstants.FILE_TRANSFER_SSH;
		}
		try {
			int hostID = this.createHost(this.client, privateIp, im, vmm, tm);
			Host host = new Host(hostID, this.client);
			OneResponse onrs = host.info();
			if (onrs.isError()) {
				throw new Exception(onrs.getErrorMessage());
			}
			PhysicalNode newpn = host.mappingFieldForHostInfo(pn);
			return newpn;
		} catch (Exception e) {
			throw new Exception(
					"Allocate Vm Provision Node failed, the reason is : "
							+ e.toString());
		}
	}

	private int createHost(Client oneClient, String hostname, String im,
			String vmm, String tm) throws Exception {
		OneResponse rc = Host.allocate(oneClient, hostname, im, vmm, tm);
		if (rc.isError()) {
			throw new Exception(rc.getErrorMessage());
		}
		int newHostID = Integer.parseInt(rc.getMessage());
		return newHostID;
	}

	public void freeVmProvisionNode(PhysicalNode pn) throws Exception {
		/*
		 * A trick here, the hi.getHostInfo equals with the returned message by
		 * one, so we can use its constructor to parse message. Xiaoyi Lu added
		 * at 2010.09.20.
		 */
		Host his = new Host(pn.getHostInfo(), this.client);
		OneResponse onrs = his.delete();
		if (!onrs.isError()) {
			log.info("The host(" + his.getName() + " " + his.getId()
					+ ") is deleted successfully.");
		} else {
			// bug fixed, here his.getId return string type, so the Host
			// constructor may parse the id as xml string, it will error.
			// Host hi = new Host(his.getId(), this.client);
			// Here, we no need to new a Host object to check.
			OneResponse onrss = his.info();
			if (onrss.isError()) {
				// Modified by Xiaoyi Lu at 2010.09.20 for fault tolerance.
				// this case means the host is not normal case, so no need to
				// care about this case.
				log.warn("Error occurred when deleting the host("
						+ his.getName() + " " + his.getId()
						+ "), but view the info of this host is failed, "
						+ "ignore this error.");
				return;
			} else {
				// this case means the host is exist and normal, but can not
				// deleted, there will be some logic error. so throw this
				// exception.
				throw new Exception("Error occurred when deleting the host("
						+ his.getName() + " " + his.getId()
						+ "), the detail msg as: " + onrs.getErrorMessage());
			}
		}
	}

	public PhysicalNode getVMProvisionNode(PhysicalNode pn) throws Exception {
		Host his = new Host(pn.getHostInfo(), this.client);
		int hid = Integer.parseInt(his.getId());
		if (hid < 0) {
			return pn;
		}
		Host newhis = new Host(hid, this.client);
		OneResponse onrs = newhis.info();
		if (onrs.isError()) {
			log.error("Get VM Provision node info failed due to "
					+ onrs.getErrorMessage());
			pn.setRunningStatus(XMMConstants.MachineRunningState.ERROR
					.toString());
			return pn;
		}
		// XXX improved at 2010-12-25, we observe sometimes the one
		// will return error message, so we must check its return message.
		Host hisr = new Host(onrs.getMessage(), this.client);
		if (!("" + hid).equals(hisr.getId())) {
			log.warn("Some error may be returned by one, the hid in molva is "
					+ hid + ", but the id is " + hisr.getId()
					+ " in one response. Not update metainfo.");
			return pn;
		}
		return newhis.mappingFieldForHostInfo(pn);
	}

	public VirtualNetwork allocateVirtualNetwork(VirtualNetwork vn)
			throws Exception {
		// FIXME from 2010-07, we do not use opennebula to manage virtual
		// network.
		return vn;
	}

	public void freeVirtualNetwork(VirtualNetwork vn) throws Exception {
		// FIXME from 2010-07, we do not use opennebula to manage virtual
		// network.
		return;
	}

	public VmTemplate configureVMTemplate(boolean isHeadNode, String vmname,
			int vCpuNum, int memsize, String bootLoader, String[] vaFileNames,
			String bridge, List<Nic> privateNics, List<Nic> publicNics,
			HashMap<String, Vector<String>> vmdeployResults) throws Exception {
		VmTemplate vmTemplate = new VmTemplate();
		if ("hvm".equals(bootLoader)) {
			vmTemplate.setForHVM();
		}
		vmTemplate.newSimpleAttribute("NAME", vmname);
		vmTemplate.newSimpleAttribute("CPU", "" + vCpuNum);
		vmTemplate.newSimpleAttribute("MEMORY", "" + memsize);
		// XXX improved at 2010-12-10, due to one not set vcpu number, so when
		// the cpu number is bigger than 1, we must set by hand.
		if (vCpuNum > 1) {
			Map<String, String> attRAW4VCPU = new HashMap<String, String>();
			attRAW4VCPU.put("type", "xen");
			attRAW4VCPU.put("data", "vcpus = " + vCpuNum);
			vmTemplate.newVectorAttribute("RAW", attRAW4VCPU);
		}

		Map<String, String> attOS = new HashMap<String, String>();
		// Makesure the bootloader is setted.
		/*
		 * Two types of loader, one is hvm, the other is pygrub. The HVM
		 * loader's configure method is a litte complex as : builder = "hvm"
		 * kernel = "/usr/lib/xen/boot/hvmloader" boot = "c" device_model =
		 * "/usr/lib/xen/bin/qemu-dm"
		 * 
		 * The pygrub loader's configure method is easy as : bootloader =
		 * "/usr/bin/pygrub"
		 */
		if ("hvm".equals(bootLoader)) {
			attOS.put("kernel", HVM_LOADER_LOCATION);
			// The " attOS.put("root", "sda1")" way is to use kernel and initrd.
			vmTemplate.newVectorAttribute("OS", attOS);

			for (int m = 0; m < vaFileNames.length; m++) {
				Map<String, String> attDISK = new HashMap<String, String>();
				attDISK.put("source", vaFileNames[m]);
				// HVM loader need to use hda img.
				attDISK.put("target", HD_ARRAY[m]);
				attDISK.put("readonly", "no");
				vmTemplate.newVectorAttribute("DISK", attDISK);
			}

			// now we do not use app type va. Marked at 2010-07-27, Xiaoyi Lu.
			// // TODO Different loader of APP disk support in script.

			Map<String, String> attRAW4Buidler = new HashMap<String, String>();
			attRAW4Buidler.put("type", "xen");
			attRAW4Buidler.put("data", "builder = 'hvm'");
			vmTemplate.newVectorAttribute("RAW", attRAW4Buidler);

			Map<String, String> attRAW4Boot = new HashMap<String, String>();
			attRAW4Boot.put("type", "xen");
			attRAW4Boot.put("data", "boot = 'c'");
			vmTemplate.newVectorAttribute("RAW", attRAW4Boot);

			Map<String, String> attRAW4DeviceModel 
					= new HashMap<String, String>();
			attRAW4DeviceModel.put("type", "xen");

			// Bug fixed here, if the os is 64bit, the device_model is
			// /usr/lib64/xen/bin/qemu-dm\, and if 32-bit, it's
			// /usr/lib/xen/bin/qemu-dm\. Bug fixed by Xiaoyi Lu at 2009.10.14.
			attRAW4DeviceModel.put("data", "device_model = "
					+ DEVICE_MODEL_LOCATION);
			vmTemplate.newVectorAttribute("RAW", attRAW4DeviceModel);

			// pae = 1 Page Address Extension
			Map<String, String> attRAW4Pae = new HashMap<String, String>();
			attRAW4Pae.put("type", "xen");
			attRAW4Pae.put("data", "pae = 1");
			vmTemplate.newVectorAttribute("RAW", attRAW4Pae);
			// acpi = 1 Advanced Configuration and Power Management Interface
			Map<String, String> attRAW4Acpi = new HashMap<String, String>();
			attRAW4Acpi.put("type", "xen");
			attRAW4Acpi.put("data", "acpi = 1");
			vmTemplate.newVectorAttribute("RAW", attRAW4Acpi);
			// apic = 1 Advanced Programmable Interrupt Controller
			Map<String, String> attRAW4Apic = new HashMap<String, String>();
			attRAW4Apic.put("type", "xen");
			attRAW4Apic.put("data", "apic = 1");
			vmTemplate.newVectorAttribute("RAW", attRAW4Apic);
			// localtime = 0
			Map<String, String> attRAW4Localtime 
					= new HashMap<String, String>();
			attRAW4Localtime.put("type", "xen");
			attRAW4Localtime.put("data", "localtime = 0");
			vmTemplate.newVectorAttribute("RAW", attRAW4Localtime);
			// sdl = 0
			Map<String, String> attRAW4Sdl = new HashMap<String, String>();
			attRAW4Sdl.put("type", "xen");
			attRAW4Sdl.put("data", "sdl = 0");
			vmTemplate.newVectorAttribute("RAW", attRAW4Sdl);
		} else {
			// pygrub loader.
			attOS.put("bootloader", PYGRUB_LOADER_LOCATION);
			vmTemplate.newVectorAttribute("OS", attOS);

			for (int m = 1; m <= vaFileNames.length; m++) {
				Map<String, String> attDISK = new HashMap<String, String>();
				attDISK.put("source", vaFileNames[m - 1]);
				// pygrub loader need to use sda img.
				attDISK.put("target", "sda" + m);
				// FIXME if the "readonly" attribute is setted to yes, then the
				// vm
				// is created, but after a while, the vm is disappeared.
				attDISK.put("readonly", "no");
				vmTemplate.newVectorAttribute("DISK", attDISK);
			}

			// FIXME here we also must add swap disk to every pygrub vm.
			Map<String, String> attDISK4Swap = new HashMap<String, String>();
			attDISK4Swap.put("type", "swap");
			attDISK4Swap.put("size", "" + memsize * 2);
			attDISK4Swap.put("target", "sdb");
			vmTemplate.newVectorAttribute("DISK", attDISK4Swap);
		}

		if (privateNics != null) {
			for (int pri = 0; pri < privateNics.size(); pri++) {
				Nic nic = privateNics.get(pri);
				Map<String, String> attNIC = new HashMap<String, String>();
				attNIC.put("bridge", bridge);
				attNIC.put("ip", nic.getIp());
				attNIC.put("mac", nic.getMac());
				attNIC.put("script", "vif-bridge");
				// FIXME from 2010-07-27, we do not save the virtual network
				// info to
				// opennebula.
				vmTemplate.newVectorAttribute("NIC", attNIC);
			}
		}

		// FIXME the headnode could carry an additional ip address as a
		// public ip. Xiaoyi Lu added at 2009.12.22. From 2010-07-27, we support
		// every node has a public ip or more.
		if (publicNics != null) {
			for (int pub = 0; pub < publicNics.size(); pub++) {
				Nic nic = publicNics.get(pub);
				Map<String, String> attNIC = new HashMap<String, String>();
				attNIC.put("bridge", bridge);
				attNIC.put("ip", nic.getIp());
				attNIC.put("mac", nic.getMac());
				attNIC.put("script", "vif-bridge");
				// FIXME from 2010-07-27, we do not save the virtual network
				// info to
				// opennebula.
				vmTemplate.newVectorAttribute("NIC", attNIC);
			}
		}

		if (isHeadNode) {
			Map<String, String> attRAW4VNC = new HashMap<String, String>();
			attRAW4VNC.put("type", "xen");
			attRAW4VNC.put("data", "vnc=1");
			vmTemplate.newVectorAttribute("RAW", attRAW4VNC);

			// TODO VNCController and HeadNode structure
			String vncport = this.getNextVNCPort4Cluster();
			Map<String, String> attRAW4VNCDispaly 
					= new HashMap<String, String>();
			attRAW4VNCDispaly.put("type", "xen");
			attRAW4VNCDispaly.put("data", "vncdisplay=" + vncport);
			vmTemplate.newVectorAttribute("RAW", attRAW4VNCDispaly);

			Map<String, String> attRAW4VNCUnused 
					= new HashMap<String, String>();
			attRAW4VNCUnused.put("type", "xen");
			attRAW4VNCUnused.put("data", "vncunused=1");
			vmTemplate.newVectorAttribute("RAW", attRAW4VNCUnused);
		}

		// FIXME at 2009.11.26, Xiaoyi Lu marked below lines for usb mouse
		// At 2010-12-19, Xiaoyi Lu opened following lines to solve double
		// mouse pointer problem; Notice: the following line can be effected
		// only when the virtual appliance is installed in the
		// 'usbdevice=tablet' case.
		Map<String, String> attRAW4USB = new HashMap<String, String>();
		attRAW4USB.put("type", "xen");
		attRAW4USB.put("data", "usb=1");
		vmTemplate.newVectorAttribute("RAW", attRAW4USB);

		Map<String, String> attRAW4USBDevice = new HashMap<String, String>();
		attRAW4USBDevice.put("type", "xen");
		attRAW4USBDevice.put("data", "usbdevice = 'tablet'");
		vmTemplate.newVectorAttribute("RAW", attRAW4USBDevice);

		// For vm deploy policy.
		if (vmdeployResults != null && !vmdeployResults.isEmpty()) {
			Iterator<String> it = vmdeployResults.keySet().iterator();
			while (it.hasNext()) {
				String keyword = it.next();
				Vector<String> values = vmdeployResults.get(keyword);
				for (int d = 0; d < values.size(); d++) {
					String value = values.get(d);
					vmTemplate.newSimpleAttribute(keyword, value);
				}
			}
		}

		return vmTemplate;
	}

	// TODO the following method should be improved from two methods:
	// 1> use advanced search method;
	// 2> the same vncport can be used when they are deployed in different host,
	// but the one will schedule by itself.
	// may be we could throw this problem to opennebula1.4 above version.
	// Xiaoyi Lu marked at 2010.09.20.
	private String getNextVNCPort4Cluster() throws Exception {
		String[] fields = new String[] { "attributes['"
				+ VirtualNode.IS_HEAD_NODE + "']" };
		String[] opers = new String[] { "=" };
		Object[] values = new Object[] { "true" };
		List<VirtualNode> vnodels;
		try {
			vnodels = PartitionManager.getInstance().searchVirtualNode(fields,
					opers, values);
		} catch (Exception e) {
			log.error("Search all virtual head node error"
					+ " in allocating vm stage, the reason is " + e.toString()
					+ ".");
			throw e;
		}
		int port = 2;
		if (vnodels == null || vnodels.isEmpty()) {
			return "" + port;
		}
		HashMap<String, VirtualNode> portset 
				= new HashMap<String, VirtualNode>();
		for (int i = 0; i < vnodels.size(); i++) {
			VirtualNode vHeadNode = vnodels.get(i);
			// like 5902.
			String portstr = vHeadNode.getVncPortInParentPhysialNode();
			if (portstr != null && !"".equals(portstr)) {
				try {
					int existport = Integer.parseInt(portstr) - VNC_PORT;
					portset.put("" + existport, vHeadNode);
				} catch (Throwable t) {
					log.warn("Some head node (" + vHeadNode.getName()
							+ ") does not contain a valid"
							+ " vnc port in the parent host.");
				}
			}
		}
		while (true) {
			if (portset.containsKey("" + port)) {
				port++;
				continue;
			}
			break;
		}
		log.info("Success to find an appropriate vnc port : " + port
				+ " for a new cluster.");
		return "" + port;
	}

	private void deployHeadNode(String parid, int vid, String vmname,
			String vcid) throws Exception {
		// FIXME deploy the headnode to the host who has public ip.

		List<PhysicalNode> lhi = PartitionManager.getInstance()
				.listPhysicalNode(parid);
		List<PhysicalNode> phi = new ArrayList<PhysicalNode>();
		if (lhi != null && lhi.size() > 0) {
			for (int k = 0; k < lhi.size(); ++k) {
				PhysicalNode hi = lhi.get(k);
				String[] pip = hi.getPublicIps();
				if (pip == null || pip.length == 0) {
					continue;
				} else {
					phi.add(hi);
				}
			}
		}

		if (phi.size() > 0) {
			// Bug fixed here, it should use Math.abs to make sure the rand num
			// is bigger than 0. But notice that : Math.abs(Integer.MIN_VALUE) =
			// Integer.MIN_VALUE, that is a negative number. Xiaoyi Lu added at
			// 2009-12-22.
			// int whichhi = Math.abs(rand.nextInt() % phi.size());
			// FIXME, now we don't use random mechanism, but round robin.
			int whichhi = Math.abs(currentClusterInPublicNode % phi.size());
			currentClusterInPublicNode++;
			if (currentClusterInPublicNode == Integer.MAX_VALUE) {
				currentClusterInPublicNode = 0;
			}
			PhysicalNode targethi = phi.get(whichhi);
			// FIXME form 2010-07-28, we add a new method getHostInfo to get
			// host info result in opennebula.
			// HostInfoResult hir = new HostInfoResult(true, targethi
			// .getDescription());
			Host hir = new Host(targethi.getHostInfo(), this.client);
			VirtualMachine vm = new VirtualMachine(vid, this.client);
			OneResponse onr = vm.deploy(Integer.parseInt(hir.getId()));

			if (onr == null || onr.isError()) {
				// throw new Exception("Deploy Error...");
				// ignore this error.
				log.warn("Ignore an error when deploy a head node " + vmname
						+ " in a new virtual cluster " + vcid);
			}
		}
	}

	public VirtualNode allocateVirtualNode(VirtualNode vnode) throws Exception {
		int memsize = 0, vCpuNum = 0;
		boolean isGraphicEnabled = false;
		try {
			String vmname = vnode.getName();
			// TODO, the mem size should be estimated by user quota, free
			// mem and other parameters. FIXME, do not do this estimation, and
			// left this estimation to the pdp (to client side or the vegaxmm),
			// here is only pep of virtualization.
			vCpuNum = vnode.getCpuNum();
			if (vCpuNum <= 0) {
				vCpuNum = XMMConstants.DEFAULT_CPU_NUM;
			}

			if (vnode.isHeadNode()) {
				memsize = vnode.getMemorySize();
				if (memsize <= 0) {
					memsize = XMMConstants.DEFAULT_MEM_SIZE_MASTER;
				}
				isGraphicEnabled = true;
			} else {
				memsize = vnode.getMemorySize();
				if (memsize <= 0) {
					memsize = XMMConstants.DEFAULT_MEM_SIZE_SLAVE;
				}
				isGraphicEnabled = false;
			}

			String bridge = vnode.getBridge();
			if (bridge == null || "".equals(bridge)) {
				bridge = XMMUtil.getNetworkBridge();
			}

			List<Nic> privateNics = vnode.getPrivateIpNics();
			List<Nic> publicNics = vnode.getPublicIpNics();
			VmTemplate vmt = this.configureVMTemplate(vnode.isHeadNode(),
					vmname, vCpuNum, memsize, vnode.getBootLoader(),
					vnode.getApplianceFileNames(), bridge, privateNics,
					publicNics, vnode.getVmDeployScheduleResult());
			OneResponse onrs = VirtualMachine.allocate(this.client,
					vmt.toString());
			if (onrs.isError()) {
				throw new Exception("The vm allocate failed for the reason: "
						+ onrs.getErrorMessage());
			}
			int vid = Integer.parseInt(onrs.getMessage());
			if (vnode.isHeadNode()) {
				try {
					this.deployHeadNode(vnode.getPartitionId(), vid, vmname,
							vnode.getVirtualClusterID());
				} catch (Throwable t) {
					log.warn("Ignore error to deploy head node to a "
							+ "physicalnode who has a public ip, due to "
							+ t.toString());
				}
			}
			VirtualMachine vm = new VirtualMachine(vid, this.client);
			OneResponse vmor = vm.info();
			if (vmor.isError()) {
				throw new Exception("After allocate vm " + vnode.getName()
						+ " into one, but the view operation errored.");
			}
			VirtualNode newvi = vm.mappingFieldForVmInfo(vnode);
			newvi.setVmTemplate(vmt);
			if (isGraphicEnabled) {
				String vmtstr = vmt.toString();

				System.out.println("Langlee: " + vmtstr);

				int portpos = vmtstr.indexOf("vncdisplay=");
				String tmp = vmtstr.substring(portpos + "vncdisplay=".length(),
						vmtstr.length()).trim();
				String portstr = tmp.substring(0, tmp.indexOf("\"")).trim();
				if (portstr.equals("")) {
					portstr = "5900";
				} else {
					if (portstr.length() < 2) {
						portstr = "590" + portstr;
					} else if (portstr.length() >= 2) {
						int port = Integer.parseInt(portstr);
						port = VNC_PORT + port;
						portstr = "" + port;
					}
				}
				newvi.setVncPortInParentPhysialNode(portstr);
			}
			return newvi;
			// this.registerVMStatusCallback(vid, new VMStatusCallbackImpl(
			// newvi, this, OneConstants.VmState.ACTIVE.toString(),
			// NamingUtil.getOperateContext(true, true)));
		} catch (Exception e) {
			// TODO Transaction support later.
			log.error(e.toString());
			e.printStackTrace();
			if (e.getStackTrace().length > 1) {
				StackTraceElement element = e.getStackTrace()[0];
				log.error("Error Detail: " + element.getClassName() + " "
						+ element.getMethodName() + " "
						+ element.getLineNumber());
			}
			// e.printStackTrace();
			throw e;
		}
	}

	public VirtualNode startVirtualNode(VirtualNode vnode) throws Exception {
		String stat = vnode.getRunningStatus();
		if (!XMMConstants.MachineRunningState.STOP.toString().equals(stat)) {
			throw new Exception("Virtual machine " + vnode.getName()
					+ " CANNOT boot in state - " + stat + "!");
		}

		VirtualMachine vir = new VirtualMachine(vnode.getVmInfo(), this.client);
		int vid = Integer.parseInt(vir.getId());
		VirtualMachine newvir = new VirtualMachine(vid, this.client);
		OneResponse onrc = newvir.info();
		if (onrc.isError()) {
			throw new Exception("Error occurred when view the started VM("
					+ vnode.getName() + "), the detail msg as: "
					+ onrc.getErrorMessage());
		} else {
			if (newvir.stateStr().equals(
					VirtualMachine.VM_STATES[VirtualMachine.VM_STATE_ACTIVE])
					&& newvir
							.lcmStateStr()
							.equals(VirtualMachine.LCM_STATE[VirtualMachine
							                      .LCM_STATE_RUNNING])) {
				vnode.setRunningStatus(XMMConstants.MachineRunningState.RUNNING
						.toString());
				return vnode;
			}
			OneResponse sonrc = newvir.resume();

			if (sonrc.isError()) {
				throw new Exception("Error occurred when start the VM("
						+ vnode.getName() + "), the detail msg as: "
						+ onrc.getErrorMessage());
			}
			vnode.setRunningStatus(XMMConstants.MachineRunningState.BOOT
					.toString());
			log.info("The VM(" + vnode.getName() + ") is started.");
		}
		return vnode;
	}

	public VirtualNode stopVirtualNode(VirtualNode vnode) throws Exception {
		String stat = vnode.getRunningStatus();
		if (!XMMConstants.MachineRunningState.RUNNING.toString().equals(stat)) {
			throw new Exception("Virtual machine " + vnode.getName()
					+ " CANNOT stop in state - " + stat + "!");
		}

		VirtualMachine vir = new VirtualMachine(vnode.getVmInfo(), this.client);
		int vid = Integer.parseInt(vir.getId());
		VirtualMachine newvir = new VirtualMachine(vid, this.client);
		OneResponse onrc = newvir.info();
		if (onrc.isError()) {
			throw new Exception("Error occurred when view the stopped VM("
					+ vnode.getName() + "), the detail msg as: "
					+ onrc.getErrorMessage());
		} else {
			if (newvir.stateStr().equals(
					VirtualMachine.VM_STATES[VirtualMachine
					                         .VM_STATE_STOPPED])) {
				vnode.setRunningStatus(XMMConstants.MachineRunningState.STOP
						.toString());
				return vnode;
			} else if (newvir
					.lcmStateStr()
					.equals(VirtualMachine.LCM_STATE[VirtualMachine
					                                 .LCM_STATE_SAVE_STOP])) {
				vnode.setRunningStatus(XMMConstants.MachineRunningState.STOP
						.toString());
				return vnode;
			}
			OneResponse sonrc = newvir.stop();
			if (sonrc.isError()) {
				throw new Exception("Error occurred when stop the VM("
						+ vnode.getName() + "), the detail msg as: "
						+ onrc.getErrorMessage());
			}
			vnode.setRunningStatus(XMMConstants.MachineRunningState.STOPPING
					.toString());

			log.info("The VM(" + vnode.getName() + ") is stopped.");
		}
		return vnode;
	}

	public VirtualNode migrateVirtualNode(VirtualNode vnode, PhysicalNode host)
			throws Exception {
		String stat = vnode.getRunningStatus();
		if (!XMMConstants.MachineRunningState.RUNNING.toString().equals(stat)) {
			throw new Exception("Virtual machine " + vnode.getName()
					+ " CANNOT livemigrate in state - " + stat + "!");
		}

		VirtualMachine vir = new VirtualMachine(vnode.getVmInfo(), this.client);

		int vid = Integer.parseInt(vir.getId());
		VirtualMachine newvir = new VirtualMachine(vid, this.client);
		OneResponse onrc = newvir.info();

		int hostId = Integer.parseInt(host.getHostID());

		if (onrc.isError()) {
			throw new Exception("Error occurred when view the migrating VM("
					+ vnode.getName() + "), the detail msg as: "
					+ onrc.getErrorMessage());
		} else {
			OneResponse sonrc;

			sonrc = newvir.liveMigrate(hostId);

			if (sonrc.isError()) {
				log.info("Error occurred when live migrate the VM("
						+ vnode.getName() + "), the detail msg as: "
						+ sonrc.getErrorMessage());

				sonrc = newvir.migrate(hostId);
			}
			if (sonrc.isError()) {
				throw new Exception("Error occurred when migrate the VM("
						+ vnode.getName() + "), the detail msg as: "
						+ sonrc.getErrorMessage());
			}

			log.info("The VM(" + vnode.getName() + ") is migrated.");
		}
		return vnode;
	}

	public void freeVirtualNode(VirtualNode vnode, boolean isForcibly)
			throws Exception {
		VirtualMachine vir = new VirtualMachine(vnode.getVmInfo(), this.client);
		int vid = Integer.parseInt(vir.getId());
		if (vid < 0) {
			return;
		}
		VirtualMachine newvir = new VirtualMachine(vid, this.client);
		OneResponse onrc = newvir.info();
		if (onrc.isError()) {
			throw new Exception("Error occurred when view the freed VM("
					+ vnode.getName() + "), the detail msg as: "
					+ onrc.getErrorMessage());
		} else {
			OneResponse shonrs = newvir.shutdown();
			if (!shonrs.isError()) {
				log.info("The VM(" + vnode.getName() + ") is shutdowning.");
			} else {
				log.warn("Error occurred when shutdown the VM("
						+ vnode.getName() + "), the detail msg as: "
						+ shonrs.getErrorMessage() + ", Ignore this error.");
				// May be some errors occurred, for example: some pepole use
				// commands to shutdown vm in the hosts. Ignore this error.
				OneResponse deonrs = newvir.finalizeVM();
				if (deonrs.isError()) {
					log.warn("Error occurred when delete the VM("
							+ vnode.getName() + "), the detail msg as: "
							+ shonrs.getErrorMessage() + ", it may be lost.");
					if (isForcibly) {
						if (vmKiller == null || "".equals(vmKiller)) {
							try {
								vmKiller = XMMUtil.getVMKillerInCfgFile();
							} catch (Exception e) {
								String msg = "The virtual node "
										+ vnode.getName()
										+ " is shutdown failed, and the "
										+ "vmkiller is not defined.";
								log.warn(msg);
								return;
								// throw new Exception(msg);
							}
						}
						String parentHost = vnode.getParentPhysialNodeName();
						String[] vaFiles = vnode.getApplianceFileNames();
						StringBuilder sb = new StringBuilder();
						// command like vmkiller.sh parrentHostIp
						// deletedFileName1,deletedFileName2..
						sb.append(vmKiller);
						sb.append(" " + parentHost + " ");
						for (int i = 0; i < vaFiles.length - 1; i++) {
							sb.append(vaFiles[i] + ",");
						}
						sb.append(vaFiles[vaFiles.length - 1]);
						String command = sb.toString();
						log.info("Delete the virtual node " + vnode.getName()
								+ " forcibly by the command " + command + ".");
						try {
							// VForkUtil.execProcessbyJNI(cmdSB.toString());
							XMMUtil.runCommand(command);
						} catch (Exception e) {
							log.error("Execute \"" + command
									+ "\" fail, caused by " + e.getMessage());
							String msg = "The virtual node " + vnode.getName()
									+ " is shutdown failed, and the vmkiller"
									+ " executed failed.";
							log.warn(msg);
							return;
							// throw new Exception(msg);
						}
					}
				}
				log.info("The VM(" + vnode.getName() + ") is deleting.");
			}
		}
	}

	public VirtualNode refreshVirtualNode(VirtualNode vnode) throws Exception {
		VirtualMachine vir = new VirtualMachine(vnode.getVmInfo(), this.client);
		int vid = Integer.parseInt(vir.getId());
		if (vid < 0) {
			return vnode;
		}
		if (vnode.getRunningStatus().equals(
				XMMConstants.MachineRunningState.SHUTDOWN.toString())) {
			return vnode;
		}
		VirtualMachine newvir = new VirtualMachine(vid, this.client);
		OneResponse onrc = newvir.info();
		if (onrc.isError()) {
			log.error("Error occurred when view the refreshed VM("
					+ vnode.getName() + "), the detail msg as: "
					+ onrc.getErrorMessage());
			vnode.setRunningStatus(XMMConstants.MachineRunningState.ERROR
					.toString());
			return vnode;
		}
		// XXX improved at 2010-12-25, we observe sometimes the one
		// will return error message, so we must check its return message.
		VirtualMachine vmr = new VirtualMachine(onrc.getMessage(), this.client);
		if (!("" + vid).equals(vmr.getId())) {
			log.warn("Some error may be returned by one, the hid in molva is "
					+ vid + ", but the id is " + vmr.getId()
					+ " in one response. Not update metainfo.");
			return vnode;
		}
		VirtualNode newvi = newvir.mappingFieldForVmInfo(vnode);
		return newvi;
	}

	// TODO this method will be implemented later, use the restart operation in
	// opennebula. Usage, considering to handle the error case of configuraton,
	// user can modify the deployment confire file, and then use this method to
	// redeploy. This method also can be used to shuffle the node deploy status.
	public VirtualNode redeployVirtualNode(VirtualNode vnode) throws Exception {
		return vnode;
	}

	public Partition allocateVMPartition(Partition par) throws Exception {
		// Use guid as cluster name in opennebula to avoid Chinese name of
		// partition.
		OneResponse orc = Cluster.allocate(client, par.getGuid());
		int newClusterID = Integer.parseInt(orc.getMessage());
		par.getAttributes().put(PVNPNController.ONE_CLUSTER_ID,
				"" + newClusterID);
		return par;
	}

	public void freeVMPartition(Partition par) throws Exception {
		String ocid = par.getAttributes().get(PVNPNController.ONE_CLUSTER_ID);
		if (ocid == null || "".equals(ocid)) {
			log.warn("The partition " + par.getName()
					+ "'s info maintain errored, because its cluster id in"
					+ " opennebula is blank or null.");
			return;
		}
		int cid = -1;
		try {
			cid = Integer.parseInt(ocid);
		} catch (Exception e) {
			log.warn("The partition " + par.getName()
					+ "'s info maintain errored, because its cluster id in"
					+ " opennebula is not a valid integer.");
			return;
		}
		// cid = 0 is the default partition.
		if (cid > 0) {
			Cluster.delete(client, cid);
		}
	}

	public void addVmProvisionNode2Partiton(PhysicalNode newpn, Partition par)
			throws Exception {
		String hinfo = newpn.getHostInfo();
		Host host = new Host(hinfo, client);
		int hid = Integer.parseInt(host.getId());
		if (hid < 0) {
			return;
		}
		String ocid = par.getAttributes().get(PVNPNController.ONE_CLUSTER_ID);
		if (ocid == null || "".equals(ocid)) {
			log.warn("The partition " + par.getName()
					+ "'s info maintain errored, because its cluster id in"
					+ " opennebula is blank or null.");
			return;
		}
		int cid = -1;
		try {
			cid = Integer.parseInt(ocid);
		} catch (Exception e) {
			log.warn("The partition " + par.getName()
					+ "'s info maintain errored, because its cluster id in"
					+ " opennebula is not a valid integer.");
			return;
		}
		if (cid > 0) {
			Cluster.add(client, cid, hid);
		}
	}

	public VirtualNode bootVirtualNode(VirtualNode vnode) throws Exception {

		String stat = vnode.getRunningStatus();
		if (!XMMConstants.MachineRunningState.SHUTDOWN.toString().equals(stat)
				&& !XMMConstants.MachineRunningState.ERROR.toString().equals(
						stat)
				&& !XMMConstants.MachineRunningState.STOP.toString().equals(
						stat)
				&& !XMMConstants.MachineRunningState.BOOT.toString().equals(
						stat)
				&& !XMMConstants.MachineRunningState.SHUTTING.toString()
						.equals(stat)
				&& !XMMConstants.MachineRunningState.WAIT_DEPLOY.toString()
						.equals(stat)) {
			throw new Exception("Virtual machine " + vnode.getName()
					+ " CANNOT boot in state - " + stat + "!");
		}

		VirtualMachine vir = new VirtualMachine(vnode.getVmInfo(), this.client);
		int vid = Integer.parseInt(vir.getId());
		VirtualMachine newvir = new VirtualMachine(vid, this.client);
		OneResponse onrc = newvir.info();
		if (onrc.isError()) {
			throw new Exception("Error occurred when view the boot VM("
					+ vnode.getName() + "), the detail msg as: "
					+ onrc.getErrorMessage());
		} else {
			if (newvir.stateStr().equals(
					VirtualMachine.VM_STATES[VirtualMachine.VM_STATE_ACTIVE])) {
				OneResponse sonrc = newvir.restart();
				if (sonrc.isError()) {
					throw new Exception("Error occurred when boot the VM("
							+ vnode.getName() + "), the detail msg as: "
							+ onrc.getErrorMessage());
				}
				vnode.setRunningStatus(XMMConstants.MachineRunningState.RUNNING
						.toString());

				log.info("The VM(" + vnode.getName() + ") is boot.");
			}
		}
		return vnode;
	}

	public VirtualNode shutdownVirtualNode(VirtualNode vnode) throws Exception {

		String stat = vnode.getRunningStatus();
		if (!XMMConstants.MachineRunningState.RUNNING.toString().equals(stat)
				&& !XMMConstants.MachineRunningState.ERROR.toString().equals(
						stat)
				&& !XMMConstants.MachineRunningState.STOP.toString().equals(
						stat)
				&& !XMMConstants.MachineRunningState.SUSPENDED.toString()
						.equals(stat)) {
			throw new Exception("Virtual machine " + vnode.getName()
					+ " CANNOT shutdown in state - " + stat + "!");
		}

		VirtualMachine vir = new VirtualMachine(vnode.getVmInfo(), this.client);
		int vid = Integer.parseInt(vir.getId());
		VirtualMachine newvir = new VirtualMachine(vid, this.client);
		OneResponse onrc = newvir.info();
		if (onrc.isError()) {
			throw new Exception("Error occurred when view the shutdowned VM("
					+ vnode.getName() + "), the detail msg as: "
					+ onrc.getErrorMessage());
		} else {
			/**
			 * We shut down the VM by our own shell, instead of shutdown in
			 * one-oca, in order to start it again. Here, we introduce a
			 * SHUTTING state as mid-status.
			 */
			StringBuffer cmdSB = new StringBuffer();
			String cmd = XMMUtil.getOperateVirtualNodeCmdInCfgFile();
			if (cmd == null || "".equals(cmd)) {
				log.error("can't get operateVirtualNodeCmd in Cfg file.");
				throw new Exception("can't get operateVirtualNodeCmd "
						+ "in Cfg file.");
			}
			cmdSB.append(cmd).append(
					" " + vnode.getParentPhysialNodeName() + " " + "one-" + vid
							+ " shutdown");
			String stdout = XMMUtil.runCommand(cmdSB.toString());
			if (stdout.trim().equals("true")) {
				log.info("shutdown virtual node " + vnode.getName()
						+ " sucess.");
			} else {
				log.info("shutdown virtual node " + vnode.getName()
						+ " Failed: " + stdout);
				throw new Exception("shutdown virtual node " + vnode.getName()
						+ " Failed: " + stdout);
			}
			vnode.setRunningStatus(XMMConstants.MachineRunningState.SHUTTING
					.toString());

			newvir.stop();

			log.info("The VM(" + vnode.getName() + ") is shutting down.");
		}
		return vnode;
	}
}
