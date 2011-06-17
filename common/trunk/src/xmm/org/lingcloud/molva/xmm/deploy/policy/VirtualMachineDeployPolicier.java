/*
 *  @(#)VirtualMachineDeployPolicier.java  2010-5-27
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

package org.lingcloud.molva.xmm.deploy.policy;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.xmm.pojos.NodeRequirement;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.services.PartitionManager;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-12-5<br>
 * @author Xiaoyi Lu<br>
 */

public abstract class VirtualMachineDeployPolicier {

	/**
	 * the log object.
	 */
	private static Log log = LogFactory
			.getLog(VirtualMachineDeployPolicier.class);

	public static final String RANDOM_DEPLOY = "random";

	public static final String EFFECT_DEPLOY = "effect";

	public static final String PERFORMANCE_DEPLOY = "performance";

	public static final String LOADAWARE_DEPLOY = "loadaware";

	public static final String ASSIGN_DEPLOY = "assign";

	/**
	 * generate deploy policy for the virtual cluster.
	 * 
	 * @param parid
	 *            : indicates the virtual cluster will be deployed in which
	 *            partition;
	 * @param vmip
	 *            : indicates which virtual node will be deployed;
	 * @param nr
	 *            : node requirement.
	 * @return if the result is a hashmap, the key may be some key words, e.g.
	 *         RANK or REQUIREMENTS; the values may some result set of its key
	 *         word. For example of ONE, REQUIREMENTS = "CPUSPEED > 1000"
	 *         REQUIREMENTS = "HOSTNAME = \"aquila*\"" RANK = FREECPU; if the
	 *         result is null or empty, then it means no deploy policy, the
	 *         system will do by itself.
	 * @throws Exception
	 *             : if no required host to be deployed, then this method will
	 *             throw a exception.
	 */
	public HashMap<String, Vector<String>> generateDeployPolicy(String parid,
			String vmip, NodeRequirement nr) {
		if (parid == null || vmip == null || nr == null) {
			log.warn("When generate deploy policy for a virtual node,"
					+ " but some required parameters are null.");
			return null;
		}
		try {
			List<PhysicalNode> pnodes = PartitionManager.getInstance()
					.listPhysicalNode(parid);
			if (pnodes == null || pnodes.isEmpty()) {
				throw new Exception("The physical node num in partition "
						+ parid + " is zero, so can not generate "
						+ "deploy policy for virtual node " + vmip);
			}
			HashMap<String, Vector<String>> result = this
					.generateSpecificPolicy(pnodes, vmip, nr);
			log.info("Successfully to generate deploy policy for the virtual"
					+ " node " + vmip);
			return result;
		} catch (Exception e) {
			log.error("Error occurred when generate deploy policy"
					+ " for the virtual node " + vmip + ", due to "
					+ e.toString());
			return null;
		}
	}

	/**
	 * According to different deploy params, to select proper pnode from
	 * available pnodes to deploy virtual machine.
	 * 
	 * @param availablePNodes
	 *            : the available physical nodes;
	 * @param vmip
	 *            : which vm will be deployed;
	 * @param nr
	 *            : the virtual node requirement to be deployed;
	 * @return result is a hashmap, the key may be some key words, e.g. RANK or
	 *         REQUIREMENTS; the values may some result set of its key word. For
	 *         example of ONE, REQUIREMENTS = "CPUSPEED > 1000" REQUIREMENTS =
	 *         "HOSTNAME = \"aquila*\"" RANK = FREECPU
	 */
	public abstract HashMap<String, Vector<String>> generateSpecificPolicy(
			List<PhysicalNode> availablePNodes, String vmip, NodeRequirement nr)
			throws Exception;

}
