/* 
 * @(#)DeleteVirtualApplianceAction.java Jan 3, 2008 
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
import org.lingcloud.molva.xmm.vam.pojos.VirtualAppliance;
import org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;

/**
 * <strong>Purpose:When making appliance.Change the mounted disc.</strong><br>
 * 
 * @version 1.0.1 2009-10-6<br>
 * @author Xiaoyi Lu<br>
 */
public class ChangeDiscAction extends NeedLoginAction {
	/**
	 * The logger for this class.
	 */
	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	private String url;

	public ActionForward dowork(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		try {

			DynaValidatorForm changeDiscForm = (DynaValidatorForm) form;
			if (changeDiscForm == null) {
				throw new Exception(
						"The form is submitted failed, please retry");
			}
			String vaguid = (String) changeDiscForm.get("guid");
			String discGuid = (String) changeDiscForm.get("discguid");
			String thisPage = (String) changeDiscForm.get("thispage");
			if (XMMPortalUtil.checkParamsBlankOrNull(new String[] { vaguid,
					discGuid })) {

				throw new Exception("Please input the correct parameters of "
						+ "virtual appliance guid, disc guid");

			}
			vaguid = vaguid.trim();
			discGuid = discGuid.trim();

			if (thisPage == null || "".equals(thisPage)) {
				this.url = request.getContextPath()
						+ "/JSP/viewMakeVirtualAppliance.jsp";
			} else {
				if (!thisPage.trim().startsWith("/")) {
					thisPage = "/" + thisPage.trim();
				}
				this.url = request.getContextPath() + thisPage.trim();
			}
			log.info("User want to change the disc of a appliance " + discGuid
					+ "from url : " + url);

			VirtualApplianceManager vam = VAMUtil.getVAManager();
			VirtualAppliance va = vam.queryAppliance(vaguid);
			if (discGuid.equals("none")) {
				vam.changeDisc(vaguid, null);
			} else {
				vam.changeDisc(vaguid, discGuid);
			}

			log.info(" A virtual appliance with the name " + va.getVAName()
					+ " " + va.getGuid() + " changes disc successfully.");
			/*
			 * set object to request so that other pages can use.
			 */
			request.getSession().removeAttribute("changeDiscForm");
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
