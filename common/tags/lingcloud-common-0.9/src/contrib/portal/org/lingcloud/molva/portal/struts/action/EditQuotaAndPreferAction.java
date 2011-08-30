/* 
 * @(#)EditQuotaAndPreferAction.java 2009-10-6 
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
import org.lingcloud.molva.xmm.util.XMMConstants;

/**
 * <strong>Purpose:To edit the quota and prefer.</strong><br>
 * 
 * @version 1.0.1 2009-10-6<br>
 * @author Xiaoyi Lu<br>
 */
public class EditQuotaAndPreferAction extends NeedLoginAction {
	/**
	 * The logger for this class.
	 */
	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	private String url;

	@SuppressWarnings("deprecation")
	public ActionForward dowork(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			DynaValidatorForm editQuotaAndPreferForm = (DynaValidatorForm) form;
			if (editQuotaAndPreferForm == null) {
				throw new Exception(
						"The form is submitted failed, please retry: ");
			}
			String userGuid = (String) editQuotaAndPreferForm.get("userGuid");
			String thispage = (String) editQuotaAndPreferForm.get("thispage");
			String memQuota = (String) editQuotaAndPreferForm.get("memQuota");
			String memPrefer = (String) editQuotaAndPreferForm.get("memPrefer");
			if (XMMPortalUtil.checkParamsBlankOrNull(new String[] { userGuid,
					memQuota, memPrefer })) {
				throw new Exception(
						"Please input the correct parameters of userGuid, "
						+ "memQuota, memPrefer: ");
			}
			userGuid = userGuid.trim();
			memQuota = memQuota.trim();
			memPrefer = memPrefer.trim();

			int mqi = -1;
			int mpi = -1;
			try {
				mqi = Integer.parseInt(memQuota);
				mpi = Integer.parseInt(memPrefer);
			} catch (Exception e) {
				throw new Exception(
						"Please input valid digital number for quota "
								+ "and prefer setting.");
			}

			if (thispage == null || "".equals(thispage)) {
				this.url = request.getContextPath()
						+ "/JSP/quotaPreferMgmt.jsp";
			} else {
				if (!thispage.trim().startsWith("/")) {
					thispage = "/" + thispage.trim();
				}
				this.url = request.getContextPath() + thispage.trim();
			}
			log.info("User want to edit quota and prefer info " + userGuid
					+ " from url : " + url);

			XMMClient vmc = XMMPortalUtil.getXMMClient();
			vmc.setUserQuota(userGuid, XMMConstants.QUOTA_MEM, mqi);
			vmc.setUserPreference(userGuid, XMMConstants.PREFER_MEM, mpi);
			log.info("A quota [" + XMMConstants.QUOTA_MEM + " " + mqi
					+ "]and prefer [" + XMMConstants.PREFER_MEM + " " + mpi
					+ "] info is edited successfully.");
			/*
			 * set object to request so that other pages can use.
			 */
			request.getSession().removeAttribute("editQuotaAndPreferForm");
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
