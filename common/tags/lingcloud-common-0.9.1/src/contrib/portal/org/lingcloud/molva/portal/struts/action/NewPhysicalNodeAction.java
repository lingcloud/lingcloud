/* 
 * @(#)NewPhysicalNodeAction.java 2009-10-6 
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;
import org.lingcloud.molva.portal.util.XMMPortalUtil;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.ac.PPNPNController;
import org.lingcloud.molva.xmm.ac.PVNPNController;
import org.lingcloud.molva.xmm.ac.PartitionAC;
import org.lingcloud.molva.xmm.client.XMMClient;
import org.lingcloud.molva.xmm.pojos.Node;
import org.lingcloud.molva.xmm.pojos.Partition;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.util.XmlUtil;

/**
 * <strong>Purpose:To add a new physical node.</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2009-10-6<br>
 * @author Xiaoyi Lu<br>
 */
public class NewPhysicalNodeAction extends NeedLoginAction {
	/**
	 * The logger for this class.
	 */
	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	private String url;

	public ActionForward dowork(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			DynaValidatorForm newHostNodeForm = (DynaValidatorForm) form;
			if (newHostNodeForm == null) {
				throw new Exception(
						"The form is submitted failed, please retry: ");
			}
			String parguid = (String) newHostNodeForm.get("parguid");
			String privateip = (String) newHostNodeForm.get("privateip");
			String publicip = (String) newHostNodeForm.get("publicip");
			String redeploy = (String) newHostNodeForm.get("redeploy");
			String description = (String) newHostNodeForm.get("description");
			String thisPage = (String) newHostNodeForm.get("thispage");
			if (XMMPortalUtil.checkParamsBlankOrNull(new String[] { parguid,
					privateip, redeploy })) {
				throw new Exception("Please input the correct parameters of "
						+ "target partition, redeploy, and privateip: ");
				// }
			}
			parguid = parguid.trim();
			privateip = privateip.trim();
			if (publicip != null && !publicip.equals("")) {
				publicip = publicip.trim();
			}
			redeploy = redeploy.trim();
			boolean red = Boolean.valueOf(redeploy).booleanValue();
			if (description != null && !description.equals("")) {
				description = description.trim();
			}

			if (thisPage == null || "".equals(thisPage)) {
				this.url = request.getContextPath()
						+ "/JSP/viewVirtualCluster.jsp";
			} else {
				if (!thisPage.trim().startsWith("/")) {
					thisPage = "/" + thisPage.trim();
				}
				this.url = request.getContextPath() + thisPage.trim();
			}
			log.info("User want to add a physical node to partition " + parguid
					+ " from url : " + url);
			XMMClient vxc = XMMPortalUtil.getXMMClient();
			Partition par = vxc.viewPartition(parguid);
			if (par == null) {
				throw new Exception("The target partition is not exist.");
			}
			String pnController = null;
			if (par.getAssetController().equals(PartitionAC.class.getName())) {
				if (par.getAttributes() != null
						&& par.getAttributes().containsKey(
								PartitionAC.REQUIRED_ATTR_NODETYPE)
						&& par.getAttributes()
								.get(PartitionAC.REQUIRED_ATTR_NODETYPE)
								.equals(PartitionAC.VM)) {
					pnController = PVNPNController.class.getName();
				} else {
					pnController = PPNPNController.class.getName();
				}

			}
			List<String> accessWay = new ArrayList<String>();

			accessWay.add(VAMConstants.VA_ACCESS_WAY_SSH);

			accessWay.add(VAMConstants.VA_ACCESS_WAY_VNC);

			HashMap<String, String> attr = new HashMap<String, String>();

			attr.put(Node.ACCESSWAY, XmlUtil.toXml(accessWay));

			PhysicalNode pn = vxc.addPhysicalNode(parguid, privateip, publicip,
					pnController, red, attr, description);
			for (int i = 0; i < pn.getAccessWay().size(); i++) {
				log.info(pn.getAccessWay().get(i));
			}
			log.info("A physical node with the private ip "
					+ pn.getPrivateIps()[0] + " is added successfully.");
			/*
			 * set object to request so that other pages can use.
			 */
			request.getSession().removeAttribute("newHostNodeForm");
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
			request.setAttribute("errormsg", e.getMessage().replace("\n", ""));
			// super.addErrors(e.getMessage(), request);
			return mapping.findForward("failure");
		}
	}
}
