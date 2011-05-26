/*
 *  @(#)VirtualClient.java  2010-5-27
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

import org.lingcloud.molva.xmm.pojos.Partition;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.pojos.VirtualNetwork;
import org.lingcloud.molva.xmm.pojos.VirtualNode;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2009-9-19<br>
 * @author Xiaoyi Lu<br>
 * @email luxiaoyi@software.ict.ac.cn
 */
public interface VirtualClient {

	public PhysicalNode allocateVmProvisionNode(PhysicalNode pn)
			throws Exception;

	public void freeVmProvisionNode(PhysicalNode pn) throws Exception;

	public PhysicalNode getVMProvisionNode(PhysicalNode source)
			throws Exception;

	public VirtualNetwork allocateVirtualNetwork(VirtualNetwork vn)
			throws Exception;

	public void freeVirtualNetwork(VirtualNetwork vn) throws Exception;

	public VirtualNode allocateVirtualNode(VirtualNode vnode) throws Exception;

	public VirtualNode startVirtualNode(VirtualNode vnode) throws Exception;

	public VirtualNode stopVirtualNode(VirtualNode vnode) throws Exception;

	public void freeVirtualNode(VirtualNode vnode, boolean isForcibly)
			throws Exception;

	public VirtualNode refreshVirtualNode(VirtualNode vnode) throws Exception;

	public Partition allocateVMPartition(Partition par) throws Exception;

	public void freeVMPartition(Partition par) throws Exception;

	public void addVmProvisionNode2Partiton(PhysicalNode newpn, Partition par)
			throws Exception;
}
