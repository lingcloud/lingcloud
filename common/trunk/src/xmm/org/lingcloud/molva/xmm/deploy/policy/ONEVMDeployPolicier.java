/*
 *  @(#)ONEVMDeployPolicier.java  2010-5-27
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.xmm.pojos.NodeRequirement;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.util.XMMConstants;
import org.lingcloud.molva.xmm.util.XMMUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO
 * 
 * @version 1.0.1 2010-12-15<br>
 * @author Xiaoyi Lu<br>
 * @email luxiaoyi@software.ict.ac.cn<br>
 */

public class ONEVMDeployPolicier extends VirtualMachineDeployPolicier {

	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(ONEVMDeployPolicier.class);

	private static Random rand = new Random(System.currentTimeMillis());

	public static final String REQUIREMENTS = "REQUIREMENTS";

	public static final String RANK = "RANK";

	public HashMap<String, Vector<String>> generateSpecificPolicy(
			List<PhysicalNode> availablePNodes, String vmip, NodeRequirement nr)
			throws Exception {
		if (availablePNodes == null || availablePNodes.isEmpty()) {
			return null;
		}
		if (nr == null || nr.getVmDeployParams() == null) {
			return null;
		}
		HashMap<String, String> deployParams = nr.getVmDeployParams();
		if (deployParams.containsKey(RANDOM_DEPLOY)) {
			return this.deployWithRandom(availablePNodes, vmip, nr);
		} else if (deployParams.containsKey(EFFECT_DEPLOY)) {
			return this.deployWithEffect(vmip, nr);
		} else if (deployParams.containsKey(PERFORMANCE_DEPLOY)) {
			return this.deployWithLoadBalance(availablePNodes, vmip, nr);
		} else if (deployParams.containsKey(LOADAWARE_DEPLOY)) {
			return this.deployWithLoadBalance(availablePNodes, vmip, nr);
		} else if (deployParams.containsKey(ASSIGN_DEPLOY)) {
			return this.deployWithAssign(deployParams.get(ASSIGN_DEPLOY),
					availablePNodes, vmip, nr);
		} else {
			return null;
		}
	}

	private HashMap<String, Vector<String>> deployWithAssign(String ip,
			List<PhysicalNode> availablePNodes, String vmip, NodeRequirement nr)
			throws Exception {
		if (!XMMUtil.ipIsValid(ip)) {
			throw new Exception("The target host ip address is not valid.");
		}
		log.info("Begin to deploy with assign policy for the virtual machine "
				+ vmip + ", the assigned host is " + ip);
		PhysicalNode tarpn = null;
		for (int i = 0; i < availablePNodes.size(); i++) {
			PhysicalNode pn = availablePNodes.get(i);
			if (XMMConstants.MachineRunningState.RUNNING.toString().equals(
					pn.getRunningStatus())
					&& pn.getFreeCpu() >= nr.getCpuNum()
					&& pn.getFreeMemory() >= nr.getMemorySize()
					&& ip.equals(pn.getPrivateIps()[0])) {
				tarpn = pn;
				break;
			}
		}
		// XXX may be we can change to random automatically.
		if (tarpn == null) {
			String msg = "The target assigned host " + ip
					+ " has not enough resource "
					+ "to deploy the virtual machine " + vmip + ".";
			log.error(msg);
			throw new Exception(msg);
		}
		Vector<String> target = new Vector<String>();
		target.add("\"HOSTNAME = " + tarpn.getHostName() + "\"");
		HashMap<String, Vector<String>> result = new HashMap<String, Vector<String>>();
		result.put(REQUIREMENTS, target);
		log.info("End to deploy with assign policy for the virtual machine "
				+ vmip + ", the assigned host is " + ip + ".");
		return result;
	}
	
	private HashMap<String, Vector<String>> deployWithLoadBalance(
			List<PhysicalNode> availablePNodes, String vmip, NodeRequirement nr)
			throws Exception {
		log.info("Begin to deploy with balance policy for the virtual machine "
				+ vmip + ".");
		List<PhysicalNode> pnodes = new ArrayList<PhysicalNode>();
		List<Integer> capList = new ArrayList<Integer>();
		int sum = 0;
		int cap = 0;
		for (int i = 0; i < availablePNodes.size(); i++) {
			PhysicalNode pn = availablePNodes.get(i);
			if (XMMConstants.MachineRunningState.RUNNING.toString().equals(
					pn.getRunningStatus())
					&& pn.getFreeCpu() > nr.getCpuNum()
					&& pn.getFreeMemory() > nr.getMemorySize()) {
				pnodes.add(pn);
				
				// capacity of the physical node
				cap = pn.getFreeCpu()/nr.getCpuNum() < pn.getFreeMemory()/nr.getMemorySize() ?
						pn.getFreeCpu()/nr.getCpuNum() : pn.getFreeMemory()/nr.getMemorySize() ;
				
				capList.add(cap);
				sum += cap;
				
			}
		}
		if (pnodes.isEmpty()) {
			String msg = "No enough resouces to deploy this virtual ndoe "
					+ vmip;
			log.error(msg);
			throw new Exception(msg);
		}
		int r = Math.abs(rand.nextInt() % sum);
		int index = 0;
		for (; r < 0 && index < capList.size() ; index++) {
			r -= capList.get(index);
		}
		index--;
		
		Vector<String> target = new Vector<String>();
		target.add("\"HOSTNAME = " + pnodes.get(index).getHostName() + "\"");
		HashMap<String, Vector<String>> result = new HashMap<String, Vector<String>>();
		result.put(REQUIREMENTS, target);
		log.info("End to deploy (" + vmip
				+ ") with balance policy, and choose the physical node "
				+ pnodes.get(index).getName() + " as the target.");
		return result;
	}

	private HashMap<String, Vector<String>> deployWithLoadAware(String vmip,
			NodeRequirement nr) throws Exception {
		log.info("Begin to deploy with load aware policy for the virtual machine "
				+ vmip + ".");		

		// support multiple partition by only one OpenNebula instance.
		Vector<String> rank_targets = new Vector<String>();
		rank_targets.add("FREECPU");
		Vector<String> req_targets = new Vector<String>();
		req_targets.add("\"CLUSTER = \\\"" + nr.getPartitionId() + "\\\"\"");
		HashMap<String, Vector<String>> result = new HashMap<String, Vector<String>>();
		result.put(RANK, rank_targets);
		result.put(REQUIREMENTS, req_targets);
		log.info("End to deploy with load aware policy for the virtual machine "
				+ vmip + ".");
		return result;
	}

	private HashMap<String, Vector<String>> deployWithPerformance(String vmip,
			NodeRequirement nr) throws Exception {
		log.info("Begin to deploy with performance policy for the virtual machine "
				+ vmip + ".");

		// support multiple partition by only one OpenNebula instance.
		Vector<String> rank_targets = new Vector<String>();
		rank_targets.add("\"- RUNNING_VMS\"");
		Vector<String> req_targets = new Vector<String>();
		req_targets.add("\"CLUSTER = \\\"" + nr.getPartitionId() + "\\\"\"");
		HashMap<String, Vector<String>> result = new HashMap<String, Vector<String>>();
		result.put(RANK, rank_targets);
		result.put(REQUIREMENTS, req_targets);
		log.info("End to deploy with performance policy for the virtual machine "
				+ vmip + ".");
		return result;
	}

	private HashMap<String, Vector<String>> deployWithEffect(String vmip,
			NodeRequirement nr) throws Exception {
		log.info("Begin to deploy with effect policy for the virtual machine "
				+ vmip + ".");

		// support multiple partition by only one OpenNebula instance.
		Vector<String> rank_targets = new Vector<String>();
		rank_targets.add("\"RUNNING_VMS\"");
		Vector<String> req_targets = new Vector<String>();
		req_targets.add("\"CLUSTER = \\\"" + nr.getPartitionId() + "\\\"\"");
		HashMap<String, Vector<String>> result = new HashMap<String, Vector<String>>();
		result.put(RANK, rank_targets);
		result.put(REQUIREMENTS, req_targets);
		log.info("End to deploy with effect policy for the virtual machine "
				+ vmip + ".");
		return result;
	}

	private HashMap<String, Vector<String>> deployWithRandom(
			List<PhysicalNode> availablePNodes, String vmip, NodeRequirement nr)
			throws Exception {
		log.info("Begin to deploy with random policy for the virtual machine "
				+ vmip + ".");
		List<PhysicalNode> pnodes = new ArrayList<PhysicalNode>();
		for (int i = 0; i < availablePNodes.size(); i++) {
			PhysicalNode pn = availablePNodes.get(i);
			if (XMMConstants.MachineRunningState.RUNNING.toString().equals(
					pn.getRunningStatus())
					&& pn.getFreeCpu() > nr.getCpuNum()
					&& pn.getFreeMemory() > nr.getMemorySize()) {
				pnodes.add(pn);
			}
		}
		if (pnodes.isEmpty()) {
			String msg = "No enough resouces to deploy this virtual ndoe "
					+ vmip;
			log.error(msg);
			throw new Exception(msg);
		}
		int index = Math.abs(rand.nextInt() % pnodes.size());
		Vector<String> target = new Vector<String>();
		target.add("\"HOSTNAME = " + pnodes.get(index).getHostName() + "\"");
		HashMap<String, Vector<String>> result = new HashMap<String, Vector<String>>();
		result.put(REQUIREMENTS, target);
		log.info("End to deploy (" + vmip
				+ ") with random policy, and choose the physical node "
				+ pnodes.get(index).getName() + " as the target.");
		return result;
	}
}
