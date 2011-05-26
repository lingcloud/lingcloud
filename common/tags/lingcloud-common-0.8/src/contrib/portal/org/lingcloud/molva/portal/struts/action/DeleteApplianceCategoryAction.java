/* 
 * @(#)DeleteVirtualApplianceAction.java Oct 10, 2009 
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
import org.lingcloud.molva.xmm.vam.pojos.VACategory;
import org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;

/**
 * <strong>Purpose:Used to delete an appliance category.</strong><br>
 * 
 * @version 1.0.1 2009-10-6<br>
 * @author Xiaoyi Lu<br>
 * @email luxiaoyi@software.ict.ac.cn
 */
public class DeleteApplianceCategoryAction extends NeedLoginAction {
	/**
	 * The logger for this class.
	 */
	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	private String url;

	public ActionForward dowork(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			
			DynaValidatorForm deleteAppCateForm = (DynaValidatorForm) form;
			if (deleteAppCateForm == null) {
				throw new Exception(
						"The form is submitted failed, please retry: ");
			}
			String guid = (String) deleteAppCateForm.get("guid");
			String thisPage = (String) deleteAppCateForm.get("thispage");
			if (XMMPortalUtil
					.checkParamsBlankOrNull(new String[] { guid })) {
				throw new Exception("Please input the correct parameters of "
						+ "appliance category guid: ");
			}
			guid = guid.trim();

			if (thisPage == null || "".equals(thisPage)) {
				this.url = request.getContextPath()
						+ "/JSP/viewVirtualAppliance.jsp";
			} else {
				if (!thisPage.trim().startsWith("/")) {
					thisPage = "/" + thisPage.trim();
				}
				this.url = request.getContextPath() + thisPage.trim();
			}
			log.info("User want to delete a appliance category " + guid
					+ "from url : " + url);
			
			VirtualApplianceManager vam = VAMUtil.getVAManager();
			VACategory cate = vam.queryCategory(guid);
			vam.removeCategory(guid);
			log.info(" A appliance category with the name " + cate.getCategory() + " "
					+ cate.getGuid() + " is removed successfully.");
			/*
			 * set object to request so that other pages can use.
			 */
			request.getSession().removeAttribute("deleteApplianceCategoryForm");
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
		} finally {
		}
	}
}
