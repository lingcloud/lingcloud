/* 
 * @(#)FreeVirtualClusterAction.java 2009-10-6 
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
 * <strong>Purpose:To free a virtual cluster.</strong><br>
 * 
 * @version 1.0.1 2009-10-6<br>
 * @author Xiaoyi Lu<br>
 * @email luxiaoyi@software.ict.ac.cn
 */
public class FreeVirtualClusterAction extends NeedLoginAction {
	/**
	 * The logger for this class.
	 */
	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	private String url;

	public ActionForward dowork(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {

			DynaValidatorForm freeClusterForm = (DynaValidatorForm) form;
			if (freeClusterForm == null) {
				throw new Exception(
						"The form is submitted failed, please retry: ");
			}
			String vcguid = (String) freeClusterForm.get("vcguid");
			String thisPage = (String) freeClusterForm.get("thispage");
			// String targetDiv = (String) freeClusterForm.get("targetdiv");
			if (XMMPortalUtil
					.checkParamsBlankOrNull(new String[] { vcguid })) {
				// Get form from session, try again.
				throw new Exception("Please input the correct parameters of "
						+ "virtual cluster guid: ");
				// }
			}
			vcguid = vcguid.trim();

			if (thisPage == null || "".equals(thisPage)) {
				this.url = request.getContextPath()
						+ "/JSP/viewVirtualCluster.jsp";
			} else {
				if (!thisPage.trim().startsWith("/")) {
					thisPage = "/" + thisPage.trim();
				}
				this.url = request.getContextPath() + thisPage.trim();
			}
			log.info("User want to delete a cluster "
					+ vcguid + " from url : " + url);
			XMMClient vxc = XMMPortalUtil.getXMMClient();
			long begin = System.currentTimeMillis();
			vxc.destroyVirtualCluster(vcguid);
			long end = System.currentTimeMillis();
			log.info("Destroy Virtual Cluster (" + vcguid
					+ ") successfully, consuming time " + (end - begin));
			/*
			 * set object to request so that other pages can use.
			 */
			// request.setAttribute("thisPage", thisPage);
			String targetDiv = "asset_info_div";
			request.setAttribute("targetDiv", targetDiv.trim());
			request.getSession().removeAttribute("freeClusterForm");
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
