/* 
 * @(#)OperateVirtualNodeAction.java 2009-10-6 
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;
import org.lingcloud.molva.portal.util.XMMPortalUtil;
import org.lingcloud.molva.xmm.client.XMMClient;

/**
 * <strong>Purpose:To operate a virtual node.</strong><br>
 * Including start, stop, suspend, migrate and so on.
 * 
 * @version 1.0.1 2011-6-25<br>
 * @author Liang Li<br>
 * @email liliang@software.ict.ac.cn
 */

public class OperateVirtualNodeAction extends NeedLoginAction{
	/**
	 * The logger for this class.
	 */
	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	private String url;

	public ActionForward dowork(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			DynaValidatorForm operateVNodeForm = (DynaValidatorForm) form;
			if (operateVNodeForm == null) {
				throw new Exception(
						"The form is submitted failed, please retry: ");
			}
			String vNodeGuid = (String) operateVNodeForm.get("vNodeGuid");
			String action = (String) operateVNodeForm.get("action");
			
			String thisPage = (String) operateVNodeForm.get("thispage");

			if (XMMPortalUtil
					.checkParamsBlankOrNull(new String[] { vNodeGuid })) {
				throw new Exception("Please input the correct parameters of "
						+ "virtual cluster guid: ");
			}
			vNodeGuid = vNodeGuid.trim();

			if (thisPage == null || "".equals(thisPage)) {
				this.url = request.getContextPath()
						+ "/JSP/viewVirtualCluster.jsp";
			} else {
				if (!thisPage.trim().startsWith("/")) {
					thisPage = "/" + thisPage.trim();
				}
				this.url = request.getContextPath() + thisPage.trim();
			}
			log.info("User  want to operate a virtual node "
					+ vNodeGuid + " from url : " + url);

			XMMClient vxc = XMMPortalUtil.getXMMClient();
			if ("start".equals(action)) {
				vxc.startVirtualNode(vNodeGuid);
			}else if ("stop".equals(action)) {
				vxc.stopVirtualNode(vNodeGuid);
			}else if ("migrate".equals(action)) {
				String pNodeGuid = (String) operateVNodeForm.get("pnName");
				vxc.migrateVirtualNode(vNodeGuid, pNodeGuid);
			}

			log.info(" A virtual node with the id " + vNodeGuid
					+ " is operated(" + action + ") successfully.");
			/*
			 * set object to request so that other pages can use.
			 */
			// request.setAttribute("thisPage", thisPage);
			String targetDiv = "asset_info_div";
			request.setAttribute("targetDiv", targetDiv.trim());
			request.getSession().removeAttribute("operateVirtualVirtualNodeForm");
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
