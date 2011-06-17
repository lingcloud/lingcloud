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
 */
public interface VirtualClient {

	PhysicalNode allocateVmProvisionNode(PhysicalNode pn)
			throws Exception;

	void freeVmProvisionNode(PhysicalNode pn) throws Exception;

	PhysicalNode getVMProvisionNode(PhysicalNode source)
			throws Exception;

	VirtualNetwork allocateVirtualNetwork(VirtualNetwork vn)
			throws Exception;

	void freeVirtualNetwork(VirtualNetwork vn) throws Exception;

	VirtualNode allocateVirtualNode(VirtualNode vnode) throws Exception;

	VirtualNode startVirtualNode(VirtualNode vnode) throws Exception;

	VirtualNode stopVirtualNode(VirtualNode vnode) throws Exception;

	void freeVirtualNode(VirtualNode vnode, boolean isForcibly)
			throws Exception;

	VirtualNode refreshVirtualNode(VirtualNode vnode) throws Exception;

	Partition allocateVMPartition(Partition par) throws Exception;

	void freeVMPartition(Partition par) throws Exception;

	void addVmProvisionNode2Partiton(PhysicalNode newpn, Partition par)
			throws Exception;
}
