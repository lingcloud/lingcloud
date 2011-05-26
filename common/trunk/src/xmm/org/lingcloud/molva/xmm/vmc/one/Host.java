/*******************************************************************************
 * Copyright 2002-2010, OpenNebula Project Leads (OpenNebula.org)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.lingcloud.molva.xmm.vmc.one;

import org.opennebula.client.*;
import org.opennebula.client.host.HostPool;

import java.util.ArrayList;
import javax.xml.xpath.XPathExpressionException;
import org.lingcloud.molva.xmm.util.XMMUtil;
import org.lingcloud.molva.xmm.pojos.Nic;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.util.XMMConstants;
import org.w3c.dom.Node;

public class Host extends PoolElement {
	
	private static final String METHOD_PREFIX = "host.";

	private static final String ALLOCATE = METHOD_PREFIX + "allocate";

	private static final String INFO = METHOD_PREFIX + "info";

	private static final String DELETE = METHOD_PREFIX + "delete";

	private static final String ENABLE = METHOD_PREFIX + "enable";

	private static final String[] HOST_STATES = { "INIT", "MONITORING",
			"MONITORED", "ERROR", "DISABLED" };
	
	/**
	 * Creates a new Host representation.
	 * 
	 * @param id
	 *            The host id (hid) of the machine.
	 * @param client
	 *            XML-RPC Client.
	 */
	public Host(int id, Client client) {
		super(id, client);
	}

	/**
	 * Creates a Host representatio by a older oneresponse.
	 * 
	 * @param hostInfo
	 *            : the message of oneresponse.
	 * @throws XPathExpressionException
	 * @throws NumberFormatException
	 */
	// Added by Xiaoyi Lu for get hid.
	public Host(String oneresponse, Client client)
			throws NumberFormatException, XPathExpressionException {
		super(oneresponse, client);
	}

	/**
	 * @see PoolElement
	 */
	protected Host(Node xmlElement, Client client) {
		super(xmlElement, client);
	}

	// =================================
	// Static XML-RPC methods
	// =================================

	/**
	 * Allocates a new host in OpenNebula
	 * 
	 * @param client
	 *            XML-RPC Client.
	 * @param hostname
	 *            Hostname of the machine we want to add
	 * @param im
	 *            The name of the information manager (im_mad_name), this values
	 *            are taken from the oned.conf with the tag name IM_MAD (name)
	 * @param vmm
	 *            The name of the virtual machine manager mad name
	 *            (vmm_mad_name), this values are taken from the oned.conf with
	 *            the tag name VM_MAD (name)
	 * @param tm
	 *            The transfer manager mad name to be used with this host
	 * @return If successful the message contains the associated id generated
	 *         for this host
	 */
	public static OneResponse allocate(Client client, String hostname,
			String im, String vmm, String tm) {
		return client.call(ALLOCATE, hostname, im, vmm, tm);
	}

	/**
	 * Retrieves the information of the given host.
	 * 
	 * @param client
	 *            XML-RPC Client.
	 * @param id
	 *            The host id (hid) of the target machine.
	 * @return If successful the message contains the string with the
	 *         information returned by OpenNebula.
	 */
	public synchronized static OneResponse info(Client client, int id) {
		return client.call(INFO, id);
	}

	/**
	 * Deletes a host from OpenNebula.
	 * 
	 * @param client
	 *            XML-RPC Client.
	 * @param id
	 *            The host id (hid) of the target machine.
	 * @return A encapsulated response.
	 */
	public static OneResponse delete(Client client, int id) {
		return client.call(DELETE, id);
	}

	/**
	 * Enables or disables a given host.
	 * 
	 * @param client
	 *            XML-RPC Client.
	 * @param id
	 *            The host id (hid) of the target machine.
	 * @param enable
	 *            If set true OpenNebula will enable the target host, if set
	 *            false it will disable it.
	 * @return A encapsulated response.
	 */
	public static OneResponse enable(Client client, int id, boolean enable) {
		return client.call(ENABLE, id, enable);
	}

	// =================================
	// Instanced object XML-RPC methods
	// =================================

	/**
	 * Loads the xml representation of the host. The info is also stored
	 * internally.
	 * 
	 * @see Host#info(Client, int)
	 */
	public synchronized OneResponse info() {
		OneResponse response = info(client, id);
		super.processInfo(response);
		return response;
	}

	/**
	 * Deletes the host from OpenNebula.
	 * 
	 * @see Host#delete(Client, int)
	 */
	public OneResponse delete() {
		return delete(client, id);
	}

	/**
	 * Enables or disables the host.
	 * 
	 * @see Host#enable(Client, int, boolean)
	 */
	public OneResponse enable(boolean enable) {
		return enable(client, id, enable);
	}

	// =================================
	// Helpers
	// =================================

	/**
	 * Returns the state of the Host. <br/>
	 * The method {@link Host#info()} must be called before.
	 * 
	 * @return The state of the Host.
	 */
	public String stateStr() {
		int state = state();
		return state != -1 ? HOST_STATES[state()] : null;
	}

	/**
	 * Returns the short length string state of the Host. <br/>
	 * The method {@link Host#info()} must be called before.
	 * 
	 * @return The short length string state of the Host.
	 */
	public String shortStateStr() {
		String st = stateStr();
		if (st == null)
			return null;
		else if (st.equals("ERROR"))
			return "err";
		else if (st.equals("DISABLED"))
			return "off";
		else
			return "on";
	}
	
	// Add by Xiaoyi Lu for info mapping between one and molva. Marked at
	// 2010-09-20.
	public synchronized PhysicalNode mappingFieldForHostInfo(PhysicalNode pn)
			throws CloneNotSupportedException {
		PhysicalNode newpn = pn.clone();
		newpn.setCpuArch(this.getArch());
		newpn.setCpuSpeed(this.getCpuSpeed());
		newpn.setHostInfo(this.getInfo());
		newpn.setHostName(this.getHostName());
		String nicstr = this.getNics();
		if (nicstr != null && !nicstr.equals("")) {
			String[] nics = nicstr.trim().split(" ");
			ArrayList<Nic> niclist = new ArrayList<Nic>();
			for (int j = 0; j < nics.length; j++) {
				String[] nic = nics[j].split("-");
				if (nic.length != 3) {
					// Ignore this error.
					continue;
				} else {
					String name = nic[0].trim();
					String mac = nic[1].trim();
					String ip = nic[2].trim();
					Nic lease = new Nic();
					lease.setIp(ip);
					lease.setMac(mac);
					lease.setNicName(name);
					niclist.add(lease);
				}
			}
			newpn.setNics(niclist);
		}
		newpn.setCpuModal(this.getProcessorModel());
		newpn.setRunningVms(this.getRunningVMs());
		newpn.setCpuNum(this.getActualTotalCPU() / 100);
		newpn.setMemsize((int) this.getActualTotalMemory() / 1024);
		int acpu = this.getAllocatedCPU() / 100;
		int amem = this.getAllocatedMemory() / 1024;
		if (newpn.getCpuNum() > acpu) {
			newpn.setFreeCpu(newpn.getCpuNum() - acpu);
		} else {
			newpn.setFreeCpu(0);
		}

		if (newpn.getMemorySize() > amem) {
			newpn.setFreeMemory(newpn.getMemorySize() - amem);
		} else {
			newpn.setFreeMemory(0);
		}

		String state = this.stateStr();
		if (state.equals(HOST_STATES[0]) || state.equals(HOST_STATES[1])) {
			newpn.setRunningStatus(XMMConstants.MachineRunningState.BOOT
					.toString());
		} else if (state.equals(HOST_STATES[2])) {
			newpn.setRunningStatus(XMMConstants.MachineRunningState.RUNNING
					.toString());
		}
		// Other state will handled by outside.
		return newpn;
	}

	// <HOST>
	// <ID>3</ID>
	// <NAME>10.61.0.164</NAME>
	// <STATE>2</STATE>
	// <IM_MAD>im_xen</IM_MAD>
	// <VM_MAD>vmm_xen</VM_MAD>
	// <TM_MAD>tm_nfs</TM_MAD>
	// <LAST_MON_TIME>1284977487</LAST_MON_TIME>
	// <CLUSTER>default</CLUSTER>
	// <HOST_SHARE>
	// <HID>3</HID>
	// <DISK_USAGE>0</DISK_USAGE>
	// <MEM_USAGE>0</MEM_USAGE>
	// <CPU_USAGE>0</CPU_USAGE>
	// <MAX_DISK>0</MAX_DISK>
	// <MAX_MEM>8382464</MAX_MEM>
	// <MAX_CPU>800</MAX_CPU>
	// <FREE_DISK>0</FREE_DISK>
	// <FREE_MEM>7181312</FREE_MEM>
	// <FREE_CPU>798</FREE_CPU>
	// <USED_DISK>0</USED_DISK>
	// <USED_MEM>1201152</USED_MEM>
	// <USED_CPU>2</USED_CPU>
	// <RUNNING_VMS>0</RUNNING_VMS>
	// </HOST_SHARE>
	// <TEMPLATE>
	// <ARCH><![CDATA[x86_64]]></ARCH>
	// <CPUSPEED><![CDATA[1995]]></CPUSPEED>
	// <FREECPU><![CDATA[798]]></FREECPU>
	// <FREEMEMORY><![CDATA[7181312]]></FREEMEMORY>
	// <HOSTNAME><![CDATA[node164]]></HOSTNAME>
	// <HYPERVISOR><![CDATA[xen]]></HYPERVISOR>
	// <MODELNAME><![CDATA[Intel(R) Xeon(R) CPU E5405 @2.00GHz]]></MODELNAME>
	// <NETRX><![CDATA[17805145]]></NETRX>
	// <NETTX><![CDATA[2230779]]></NETTX>
	// <TOTALCPU><![CDATA[800]]></TOTALCPU>
	// <TOTALMEMORY><![CDATA[8382464]]></TOTALMEMORY>
	// <USEDCPU><![CDATA[2]]></USEDCPU>
	// <USEDMEMORY><![CDATA[1201152]]></USEDMEMORY>
	// </TEMPLATE>
	// </HOST>

	private int getActualTotalMemory() {
		String atms = xpath("/HOST/TEMPLATE/TOTALMEMORY");
		return XMMUtil.isBlankOrNull(atms) ? 0 : Integer.parseInt(atms);
	}

	private int getActualTotalCPU() {
		String atms = xpath("/HOST/TEMPLATE/TOTALCPU");
		return XMMUtil.isBlankOrNull(atms) ? 0 : Integer.parseInt(atms);
	}

	private int getRunningVMs() {
		String atms = xpath("/HOST/HOST_SHARE/RUNNING_VMS");
		return XMMUtil.isBlankOrNull(atms) ? 0 : Integer.parseInt(atms);
	}

	private String getProcessorModel() {
		return this.xpath("/HOST/TEMPLATE/MODELNAME");
	}

	private String getNics() {
		return this.xpath("/HOST/TEMPLATE/NICS");
	}

	private String getHostName() {
		return this.xpath("/HOST/TEMPLATE/HOSTNAME");
	}

	// modification for ONE 2.0 version. marked at 2010-12-14.
	private int getAllocatedCPU() {
		String atms = xpath("/HOST/HOST_SHARE/CPU_USAGE");
		return XMMUtil.isBlankOrNull(atms) ? 0 : Integer.parseInt(atms);
	}

	private int getAllocatedMemory() {
		String atms = xpath("/HOST/HOST_SHARE/MEM_USAGE");
		return XMMUtil.isBlankOrNull(atms) ? 0 : Integer.parseInt(atms);
	}

	private int getActualFreeCPU() {
		String atms = xpath("/HOST/TEMPLATE/FREECPU");
		return XMMUtil.isBlankOrNull(atms) ? 0 : Integer.parseInt(atms);
	}

	private int getActualFreeMemory() {
		String atms = xpath("/HOST/TEMPLATE/FREEMEMORY");
		return XMMUtil.isBlankOrNull(atms) ? 0 : Integer.parseInt(atms);
	}

	private int getCpuSpeed() {
		String atms = xpath("/HOST/TEMPLATE/CPUSPEED");
		return XMMUtil.isBlankOrNull(atms) ? 0 : Integer.parseInt(atms);
	}

	private String getArch() {
		return this.xpath("/HOST/TEMPLATE/ARCH");
	}
}
