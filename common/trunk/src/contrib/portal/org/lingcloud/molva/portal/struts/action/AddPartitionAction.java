/* 
 * @(#)AddPartitionAction.java Jan 3, 2008 
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
import org.lingcloud.molva.xmm.ac.PartitionAC;
import org.lingcloud.molva.xmm.client.XMMClient;
import org.lingcloud.molva.xmm.pojos.Partition;

/**
 * <strong>Purpose:Add a paritition.</strong><br>
 * 
 * @version 1.0.1 2010-6-1<br>
 * @author Xiaoyi Lu<br>
 * @email luxiaoyi@software.ict.ac.cn
 */
public class AddPartitionAction extends NeedLoginAction {
	/**
	 * The logger for this class.
	 */
	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	private String url;

	public ActionForward dowork(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			DynaValidatorForm addPartitionForm = (DynaValidatorForm) form;
			if (addPartitionForm == null) {
				throw new Exception(
						"The form is submitted failed, please retry: ");
			}
			String name = (String) addPartitionForm.get("name");
			String controller = (String) addPartitionForm.get("controller");
			String nodetype = (String) addPartitionForm.get("nodetype");
			String preInstalledSoft = (String) addPartitionForm
					.get("preInstalledSoft");
			String description = (String) addPartitionForm.get("description");
			String thisPage = (String) addPartitionForm.get("thispage");
			if (XMMPortalUtil.checkParamsBlankOrNull(new String[] { name,
					controller, nodetype })) {
				throw new Exception("Please input the correct parameters of "
						+ "name, controller, and type: ");
				// }
			}
			name = name.trim();
			controller = controller.trim();
			nodetype = nodetype.trim();

			if (controller.equalsIgnoreCase("MHV")) {
				controller = PartitionAC.class.getName();
			}

			// hpc, xen, hadoop
			if (nodetype.equals("VM")) {
				// XXX need to be improved.
				nodetype = PartitionAC.VM;
			} else if (nodetype.equals("HPC")) {
				nodetype = PartitionAC.HPC;
			} else if (nodetype.equals("DC")) {
				nodetype = PartitionAC.DC;
			} else if (nodetype.equals("STORAGE")) {
				nodetype = PartitionAC.STORAGE;
			} else if (nodetype.equals("GENERAL")) {
				nodetype = PartitionAC.GENERAL;
			} else {
				throw new Exception(
						"Unsupported node type of VM/MR/HPC/STORAGE/GENERAL "
								+ "Partition Controller.");
			}

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
			log.info("User want to add a partition from url : " + url);

			XMMClient vxc = XMMPortalUtil.getXMMClient();

			HashMap<String, String> attr = new HashMap<String, String>();
			attr.put(PartitionAC.REQUIRED_ATTR_NODETYPE, nodetype);
			attr
					.put(PartitionAC.ATTR_NODE_PRE_INSTALLED_SOFT,
							preInstalledSoft);
			Partition par = vxc.createPartition(name, controller, attr,
					description);
			log.info("A partition with the name " + par.getName()
					+ " is added successfully.");
			/*
			 * set object to request so that other pages can use.
			 */
			// request.setAttribute("thisPage", thisPage);
			request.getSession().removeAttribute("addPartitionForm");
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
}
