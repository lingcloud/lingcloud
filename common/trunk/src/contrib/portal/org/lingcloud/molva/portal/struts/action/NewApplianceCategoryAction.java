/* 
 * @(#)NewVirtualApplianceAction.java 2009-10-6 
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
 * <strong>Purpose:To add a new appliance category.</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2009-10-6<br>
 * @author Xiaoyi Lu<br>
 */

public class NewApplianceCategoryAction extends NeedLoginAction {
	/**
	 * The logger for this class.
	 */
	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	private String url;

	public ActionForward dowork(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {

			DynaValidatorForm newAppCateForm = (DynaValidatorForm) form;
			if (newAppCateForm == null) {
				throw new Exception(
						"The form is submitted failed, please retry: ");
			}
			String categoryname = (String) newAppCateForm.get("categoryname");
			String thispage = (String) newAppCateForm.get("thispage");
			if (XMMPortalUtil
					.checkParamsBlankOrNull(new String[] { categoryname })) {
				throw new Exception("Please input the correct parameters of "
						+ "categoryname!");
			}
			categoryname = categoryname.trim();

			if (thispage == null || "".equals(thispage)) {
				this.url = request.getContextPath()
						+ "/JSP/viewVirtualAppliance.jsp";
			} else {
				if (!thispage.trim().startsWith("/")) {
					thispage = "/" + thispage.trim();
				}
				this.url = request.getContextPath() + thispage.trim();
			}
			log.info("User want to new a appliance category from url : " + url);

			VirtualApplianceManager vam = VAMUtil.getVAManager();
			VACategory cate = vam.addCategory(categoryname);

			log.info("A appliance category with the name " + cate.getCategory()
					+ " is added successfully.");
			/*
			 * set object to request so that other pages can use.
			 */
			// request.setAttribute("thisPage", thisPage);
			request.getSession().removeAttribute("newApplianceCategoryForm");
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
