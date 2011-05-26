/* 
 * @(#)FastNewVirtualClusterAction.java 2009-10-6 
 *  
 * Copyright (C) 2008-2011, 
 * LingCloud Team, 
 * Institute of Computing Technology, 
 * Chinese Academy of Sciences. 
 * P.O.Box 2704, 100190, Beijing, China. 
 * 
 * http://lingcloud.org 
 *  
 */

package org.lingcloud.molva.portal.struts.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;
import org.lingcloud.molva.portal.util.XMMPortalUtil;
import org.lingcloud.molva.xmm.amm.VirtualClusterAMM;
import org.lingcloud.molva.xmm.client.XMMClient;
import org.lingcloud.molva.xmm.deploy.policy.ONEVMDeployPolicier;
import org.lingcloud.molva.xmm.pojos.NodeRequirement;
import org.lingcloud.molva.xmm.pojos.VirtualCluster;
import org.lingcloud.molva.ocl.util.ParaChecker;
import org.lingcloud.molva.xmm.util.XMMConstants;

/**
 * <strong>Purpose:To create a virtual cluster.</strong><br>
 * 
 * @version 1.0.1 2009-10-6<br>
 * @author Xiaoyi Lu<br>
 * @email luxiaoyi@software.ict.ac.cn
 */
public class FastNewVirtualClusterAction extends NeedLoginAction {
	/**
	 * The logger for this class.
	 */
	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	private String url;

	private Date effectiveTime, expireTime;

	public ActionForward dowork(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			DynaValidatorForm fastNewClusterForm = (DynaValidatorForm) form;
			if (fastNewClusterForm == null) {
				throw new Exception(
						"The form is submitted failed, please retry: ");
			}
			String parguid = (String) fastNewClusterForm.get("parguid");
			String clustername = (String) fastNewClusterForm.get("clustername");
			String nodeMatchMaker = (String) fastNewClusterForm.get("amm");
			String tenantId = (String) fastNewClusterForm.get("tenantId");
			String vntype = (String) fastNewClusterForm.get("vntype");
			String nodenum = (String) fastNewClusterForm.get("nodenum");
			String publicIpSupport = (String) fastNewClusterForm
					.get("publicIpSupport");
			String vnguid = (String) fastNewClusterForm.get("vnguid");
			// String osvaguid = (String) fastNewClusterForm.get("osvaguid");
			String[] pnnodeip = (String[]) fastNewClusterForm.get("pnnodeip");
			String nodeinfotype = (String) fastNewClusterForm
					.get("nodeinfotype");
			String[] nodeip = (String[]) fastNewClusterForm.get("nodeip");
			String[] vaguid = (String[]) fastNewClusterForm.get("vaguid");
			String[] cpunum = (String[]) fastNewClusterForm.get("cpunum");
			String[] memsize = (String[]) fastNewClusterForm.get("memsize");
			String[] deployPolicy = (String[]) fastNewClusterForm
					.get("deployPolicy");
			String[] deployPolicyParam = (String[]) fastNewClusterForm
					.get("deployPolicyParam");
			if (deployPolicyParam == null) {
				log.info("deployPolicyParam is null");
			} else {
				log.info(deployPolicyParam.length);
			}
			String rentStartTime = (String) fastNewClusterForm
					.get("effectiveTime");
			String rentEndTime = (String) fastNewClusterForm.get("expireTime");
			String desc = (String) fastNewClusterForm.get("desc");
			String thisPage = (String) fastNewClusterForm.get("thispage");
			String targetDiv = (String) fastNewClusterForm.get("targetdiv");
			effectiveTime = null;
			expireTime = null;
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			this
					.validateParameters(parguid, clustername, nodeMatchMaker,
							vntype, nodenum, publicIpSupport, vnguid, pnnodeip,
							nodeinfotype, nodeip, vaguid, cpunum, memsize,
							deployPolicy);
			if (thisPage == null || "".equals(thisPage)) {
				this.url = request.getContextPath()
						+ "/JSP/viewVirtualCluster.jsp";
			} else {
				if (!thisPage.trim().startsWith("/")) {
					thisPage = "/" + thisPage.trim();
				}
				this.url = request.getContextPath() + thisPage.trim();
			}
			log.info("User want to fast new a cluster from url : " + url);

			parguid = parguid.trim();
			clustername = clustername.trim();
			vntype = vntype.trim();
			nodeMatchMaker = nodeMatchMaker.trim();
			rentStartTime = rentStartTime.trim();
			rentEndTime = rentEndTime.trim();
			if ("".equals(rentStartTime)) {
				// todo nothing
			} else {
				effectiveTime = format.parse(rentStartTime);
			}
			if ("".equals(rentEndTime)) {
				// todo nothing
			} else {
				expireTime = format.parse(rentEndTime);
			}
			if (nodeMatchMaker.equals("VirtualClusterAMM")) {
				nodeMatchMaker = VirtualClusterAMM.class.getName();
			} else {
				log.warn("Not a default node match maker(" + nodeMatchMaker
						+ ") of new virtual cluster.");
			}
			VirtualCluster vc = null;
			if (publicIpSupport == null || "".equals(publicIpSupport.trim())) {
				// create vc for pm.
				if (vntype.equals(XMMPortalUtil.VN_AUTO_CREATE)) {
					if (pnnodeip == null || pnnodeip.length < 1) {
						throw new Exception("The physical node"
								+ " should choose one at least.");
					}
					vc = this.createVirtualCluster4PMWithAutoVN(parguid,
							clustername, nodeMatchMaker, tenantId, pnnodeip,
							desc);
				} else if (vntype.equals(XMMPortalUtil.VN_USE_EXIST)) {
					ParaChecker.checkGuidFormat(vnguid, "virtual network guid");
					vc = this
							.createVirtualCluster4PMWithExistVN(parguid,
									clustername, nodeMatchMaker, tenantId,
									vnguid, desc);
				}
			} else {
				// create vc for vm.
				if (vntype.equals(XMMPortalUtil.VN_AUTO_CREATE)) {
					int nodeNumInt = 0;
					if (nodenum != null) {
						try {
							nodeNumInt = Integer.parseInt(nodenum);
						} catch (Exception e) {
							throw new Exception("The node num parameter"
									+ " should be a valid digital value.");
						}
					} else {
						throw new Exception("The node num is not assigned.");
					}
					boolean onlyHeadNodePubIp = false;
					if (publicIpSupport
							.equals(XMMPortalUtil.PUBIP_HEADNODE)) {
						onlyHeadNodePubIp = true;
					} else if (publicIpSupport
							.equals(XMMPortalUtil.PUBIP_ALLNODE)) {
						onlyHeadNodePubIp = false;
					}
					vc = this.createVirtualCluster4VMWithAutoVN(parguid,
							clustername, nodeMatchMaker, tenantId, nodeNumInt,
							onlyHeadNodePubIp, nodeinfotype, nodeip, vaguid,
							cpunum, memsize, deployPolicy, deployPolicyParam,
							desc);
				} else if (vntype.equals(XMMPortalUtil.VN_USE_EXIST)) {
					ParaChecker.checkGuidFormat(vnguid, "virtual network guid");
					vc = this.createVirtualCluster4VMWithExistVN(parguid,
							clustername, nodeMatchMaker, tenantId, vnguid,
							nodeinfotype, nodeip, vaguid, cpunum, memsize,
							deployPolicy, deployPolicyParam, desc);
				}
			}

			log.info(" A cluster with the name " + vc.getName()
					+ " is added successfully and fastly.");
			/*
			 * set object to request so that other pages can use.
			 */
			// request.setAttribute("thisPage", thisPage);
			if (targetDiv == null || "".equals(targetDiv)) {
				targetDiv = "asset_info_div";
			}
			request.setAttribute("targetDiv", targetDiv.trim());
			request.getSession().removeAttribute("fastNewVirtualClusterForm");
			String forwardAction = request.getParameter("forwardAction");
			if (forwardAction != null && !("".equals(forwardAction))) {
				log.info("get a forwardAction : " + forwardAction);
				response.sendRedirect(forwardAction);
				return null;
			} else {
				return mapping.findForward("success");
			}
		} catch (Exception e) {
			log.error(e.toString());
			// request.getSession().invalidate();
			super.addErrors(e.getMessage(), request);
			return mapping.findForward("failure");
		}
	}

	private VirtualCluster createVirtualCluster4VMWithExistVN(String parguid,
			String clustername, String nodeMatchMaker, String tenantId,
			String vnguid, String nodeinfotype, String[] nodeip,
			String[] vaguid, String[] cpunum, String[] memsize,
			String[] deployPolicy, String[] deployPolicyParam, String desc)
			throws Exception {
		XMMClient vxc = XMMPortalUtil.getXMMClient();
		HashMap<String, NodeRequirement> nrmap = new HashMap<String, NodeRequirement>();
		int indextag = 0;
		for (int j = 0; j < nodeip.length; j++) {
			if (nodeip[j].equals(XMMConstants.ALL_NODE_SAME_REQUIREMENT_TAG)) {
				indextag = j;
			}
		}
		if (nodeinfotype.equals(XMMPortalUtil.NODE_INFO_TYPE_SIMPLE)) {
			NodeRequirement nr = new NodeRequirement();
			nr.setVirtualApplicanceID(vaguid[indextag]);
			nr.setCpuNum(Integer.parseInt(cpunum[indextag]));
			nr.setMemorySize(Integer.parseInt(memsize[indextag]));
			// Added at 2010-12-17 at Dongguan for vm deploy schedule.
			nr.setVmDeployPolicer(ONEVMDeployPolicier.class.getName());
			HashMap<String, String> deployParams = new HashMap<String, String>();
			deployParams.put(deployPolicy[indextag],
					deployPolicyParam[indextag]);
			nr.setVmDeployParams(deployParams);
			nrmap.put(XMMConstants.ALL_NODE_SAME_REQUIREMENT_TAG, nr);
		} else {
			for (int i = 0; i < nodeip.length; i++) {
				// FIXME the js submit form will contain some hidden messages.
				if (i == indextag) {
					continue;
				}
				NodeRequirement nr = new NodeRequirement();
				nr.setVirtualApplicanceID(vaguid[i]);
				nr.setCpuNum(Integer.parseInt(cpunum[i]));
				nr.setMemorySize(Integer.parseInt(memsize[i]));
				nr.setPrivateIP(nodeip[i]);
				// Added at 2010-12-17 at Dongguan for vm deploy schedule.
				nr.setVmDeployPolicer(ONEVMDeployPolicier.class.getName());
				HashMap<String, String> deployParams = new HashMap<String, String>();
				deployParams.put(deployPolicy[i], deployPolicyParam[i]);
				nr.setVmDeployParams(deployParams);
				nrmap.put(nodeip[i], nr);
			}
		}

		long begin = System.currentTimeMillis();
		VirtualCluster vc = vxc.createVirtualCluster(parguid, clustername,
				nodeMatchMaker, tenantId, vnguid, nrmap, effectiveTime, 0,
				expireTime, null, desc);
		long end = System.currentTimeMillis();
		log.info("Create Virtual Cluster (" + vc.getGuid() + ", "
				+ vc.getName() + ") successfully, consuming time "
				+ (end - begin));
		return vc;
	}

	private VirtualCluster createVirtualCluster4VMWithAutoVN(String parguid,
			String clustername, String nodeMatchMaker, String tenantId,
			int nodeNumInt, boolean onlyHeadNodePubIp, String nodeinfotype,
			String[] nodeip, String[] vaguid, String[] cpunum,
			String[] memsize, String[] deployPolicy,
			String[] deployPolicyParam, String desc) throws Exception {
		XMMClient vxc = XMMPortalUtil.getXMMClient();
		int indextag = 0, pubipnum = 0;
		// FIXME the js submit form will contain some hidden messages.
		for (int j = 0; j < nodeip.length; j++) {
			if (nodeip[j].equals(XMMConstants.ALL_NODE_SAME_REQUIREMENT_TAG)) {
				indextag = j;
			}
		}
		HashMap<String, NodeRequirement> nrmap = new HashMap<String, NodeRequirement>();
		if (onlyHeadNodePubIp) {
			pubipnum = 1;
		} else {
			pubipnum = nodeNumInt;
		}
		if (nodeinfotype.equals(XMMPortalUtil.NODE_INFO_TYPE_SIMPLE)) {
			for (int i = 0; i < nodeNumInt; i++) {
				NodeRequirement nr = new NodeRequirement();
				nr.setVirtualApplicanceID(vaguid[indextag]);
				nr.setCpuNum(Integer.parseInt(cpunum[indextag]));
				nr.setMemorySize(Integer.parseInt(memsize[indextag]));
				if (pubipnum > 0) {
					nr.setNeedPublicIP(true);
					pubipnum--;
				}
				// Added at 2010-12-17 at Dongguan for vm deploy schedule.
				nr.setVmDeployPolicer(ONEVMDeployPolicier.class.getName());
				HashMap<String, String> deployParams = new HashMap<String, String>();
				deployParams.put(deployPolicy[indextag],
						deployPolicyParam[indextag]);
				nr.setVmDeployParams(deployParams);
				nrmap.put("" + i, nr);
			}
		} else {
			for (int i = 0; i < nodeip.length; i++) {
				// FIXME the js submit form will contain some hidden messages.
				if (i == indextag) {
					continue;
				}
				NodeRequirement nr = new NodeRequirement();
				nr.setVirtualApplicanceID(vaguid[i]);
				nr.setCpuNum(Integer.parseInt(cpunum[i]));
				nr.setMemorySize(Integer.parseInt(memsize[i]));
				if (pubipnum > 0) {
					nr.setNeedPublicIP(true);
					pubipnum--;
				}
				// Added at 2010-12-17 at Dongguan for vm deploy schedule.
				nr.setVmDeployPolicer(ONEVMDeployPolicier.class.getName());
				HashMap<String, String> deployParams = new HashMap<String, String>();
				deployParams.put(deployPolicy[i], deployPolicyParam[i]);
				nr.setVmDeployParams(deployParams);
				nrmap.put(nodeip[i], nr);
			}
		}

		long begin = System.currentTimeMillis();
		VirtualCluster vc = vxc.createVirtualCluster(parguid, clustername,
				nodeMatchMaker, tenantId, null, nrmap, effectiveTime, 0,
				expireTime, null, desc);
		long end = System.currentTimeMillis();
		log.info("Create Virtual Cluster (" + vc.getGuid() + ", "
				+ vc.getName() + ") successfully, consuming time "
				+ (end - begin));
		return vc;
	}

	private VirtualCluster createVirtualCluster4PMWithExistVN(String parguid,
			String clustername, String nodeMatchMaker, String tenantId,
			String vnguid, String desc) throws Exception {
		XMMClient vxc = XMMPortalUtil.getXMMClient();
		// String parid
		// String name,
		// String nodeMatchMaker,
		// String vnid,
		// HashMap<String, NodeRequirement> nrmap,
		// Date effectiveTime,
		// long duration,
		// Date expireTime,
		// HashMap<String, String> attributes,
		// String desc
		long begin = System.currentTimeMillis();
		HashMap<String, NodeRequirement> nrmap = new HashMap<String, NodeRequirement>();
		NodeRequirement nr = new NodeRequirement();
		nr.setPartitionId(parguid);
		nrmap.put(XMMConstants.ALL_NODE_SAME_REQUIREMENT_TAG, nr);

		VirtualCluster vc = vxc.createVirtualCluster(parguid, clustername,
				nodeMatchMaker, tenantId, vnguid, nrmap, effectiveTime, 0,
				expireTime, null, desc);
		long end = System.currentTimeMillis();
		log.info("Create Virtual Cluster (" + vc.getGuid() + ", "
				+ vc.getName() + ") successfully, consuming time "
				+ (end - begin));
		return vc;
	}

	private VirtualCluster createVirtualCluster4PMWithAutoVN(String parguid,
			String clustername, String nodeMatchMaker, String tenantId,
			String[] pnnodeip, String desc) throws Exception {
		HashMap<String, NodeRequirement> nrmap = new HashMap<String, NodeRequirement>();
		for (int i = 0; i < pnnodeip.length; i++) {
			NodeRequirement nr = new NodeRequirement();
			nr.setPartitionId(parguid);
			nr.setPrivateIP(pnnodeip[i]);
			nrmap.put(pnnodeip[i], nr);
		}

		XMMClient vxc = XMMPortalUtil.getXMMClient();

		long begin = System.currentTimeMillis();
		VirtualCluster vc = vxc.createVirtualCluster(parguid, clustername,
				nodeMatchMaker, tenantId, null, nrmap, effectiveTime, 0,
				expireTime, null, desc);
		long end = System.currentTimeMillis();
		log.info("Create Virtual Cluster (" + vc.getGuid() + ", "
				+ vc.getName() + ") successfully, consuming time "
				+ (end - begin));
		return vc;
	}

	private void validateParameters(String parguid, String clustername,
			String nodeMatchMaker, String vntype, String nodenum,
			String publicIpSupport, String vnguid, String[] pnnode,
			String nodeinfotype, String[] nodeip, String[] vaguid,
			String[] cpunum, String[] memsize, String[] deployPolicy)
			throws Exception {
		if (XMMPortalUtil.checkParamsBlankOrNull(new String[] { parguid,
				clustername, nodeMatchMaker, vntype })) {
			throw new Exception("The parameters of "
					+ "parguid, clustername, nodeMatchMaker, "
					+ "and vntype are blank or null: ");
		}
		// Lazy check.
	}
}
