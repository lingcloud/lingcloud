/*
 *  @(#)OperatePhysicalNodeAction.java  2011-7-11
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
package org.lingcloud.molva.portal.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;
import org.lingcloud.molva.portal.util.XMMPortalUtil;
import org.lingcloud.molva.xmm.client.XMMClient;

/**
 * <strong>Purpose:To operate a physical node.</strong><br>
 * Including start, stop.
 * TODO
 *
 * @version 1.0.1 2011-7-11<br>
 * @author Xiaofan Zhang<br>
 *
 */
public class OperatePhysicalNodeAction extends NeedLoginAction {
	/**
	 * The logger for this class.
	 */
	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	
	private String url;
	
	public ActionForward dowork(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try{
			DynaValidatorForm operatePNodeForm = (DynaValidatorForm) form;
			if (operatePNodeForm == null) {
				throw new Exception(
						"The form is submitted failed, please retry: ");
			}
			String pNodeGuid = (String) operatePNodeForm.get("pNodeGuid");
			
			String action = (String) operatePNodeForm.get("action");
			
			String thisPage = (String) operatePNodeForm.get("thispage");
			
			if (XMMPortalUtil
					.checkParamsBlankOrNull(new String[] { pNodeGuid })) {
				throw new Exception("Please input the correct parameters of "
						+ "physical node guid: ");
			}
			pNodeGuid = pNodeGuid.trim();
			
			if (thisPage == null || "".equals(thisPage)) {
				this.url = request.getContextPath()
						+ "/JSP/viewVirtualCluster.jsp";
			} else {
				if (!thisPage.trim().startsWith("/")) {
					thisPage = "/" + thisPage.trim();
				}
				this.url = request.getContextPath() + thisPage.trim();
			}
			log.info("User  want to operate a physical node "
					+ pNodeGuid + " from url : " + url);
			
			XMMClient vxc = XMMPortalUtil.getXMMClient();
			
			if ("start".equals(action)) {
				vxc.startPhysicalNode(pNodeGuid);
			}else if ("stop".equals(action)) {
				vxc.stopPhysicalNode(pNodeGuid);
			}
			log.info(" A physical node with the id " + pNodeGuid
					+ " is operated(" + action + ") successfully.");
			
			request.getSession().removeAttribute("operatePhysicalNodeForm");
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
			super.addErrors(e.getMessage(), request);
			return mapping.findForward("failure");
		}
	}

}
