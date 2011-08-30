/* 
 * @(#)MakeByExistedVirtualApplianceAction.java 2009-10-6 
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
import org.lingcloud.molva.xmm.vam.util.VAMConfig;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;

/**
 * <strong>Purpose:To make a virtual appliance using a existed
 * appliance.</strong><br>
 * 
 * @version 1.0.1 2009-10-6<br>
 * @author Xiaoyi Lu<br>
 */
public class MakeByExistedVirtualApplianceAction extends NeedLoginAction {
	/**
	 * The logger for this class.
	 */
	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	private String url;

	public ActionForward dowork(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			DynaValidatorForm newVirtualAppForm = (DynaValidatorForm) form;
			if (newVirtualAppForm == null) {
				throw new Exception(
						"The form is submitted failed, please retry!");
			}
			String appGuid = (String) newVirtualAppForm.get("appliance");
			String name = (String) newVirtualAppForm.get("appname");
			String memsize = (String) newVirtualAppForm.get("memsize");
			String format = (String) newVirtualAppForm.get("format");
			String action = (String) newVirtualAppForm.get("action");
			String thispage = (String) newVirtualAppForm.get("thispage");
			if (XMMPortalUtil.checkParamsBlankOrNull(new String[] { name,
					memsize, appGuid })) {
				throw new Exception("Please input the correct parameters of "
						+ "appliance, name,  memsize!");
			}

			format = VAMConfig.getSnapshotImageFormat();

			appGuid = appGuid.trim();
			name = name.trim();
			memsize = memsize.trim();
			int memery = Integer.parseInt(memsize);
			format = format.trim();
			VAMUtil.checkDiskFormat(format);

			if (thispage == null || "".equals(thispage)) {
				this.url = request.getContextPath()
						+ "/JSP/viewMakeVirtualAppliance.jsp";
			} else {
				if (!thispage.trim().startsWith("/")) {
					thispage = "/" + thispage.trim();
				}
				this.url = request.getContextPath() + thispage.trim();
			}
			log.info("User want to make a new virtual appliance from url : "
					+ url);
			VirtualApplianceManager vam = VAMUtil.getVAManager();
			VirtualAppliance va = new VirtualAppliance();
			if (action.equals("add")) {
				va.setVAName(name);
				va.setFormat(format);
			} else if (action.equals("modify")) {
				va.setGuid(appGuid);
			} else {
				throw new Exception("Please specify the action: "
						+ "add or modify!");
			}

			va = vam.makeAppliance(va, appGuid, memery);
			log.info("A virtual appliance with the name " + va.getVAName()
					+ " is being made successfully.");
			/*
			 * set object to request so that other pages can use.
			 */
			// request.setAttribute("thisPage", thisPage);
			request.getSession().removeAttribute(
					"useExistedVirtualApplianceForm");
			String forwardAction = request.getParameter("forwardAction");
			if (forwardAction != null && !("".equals(forwardAction))) {
				log.info("get a forwardAction : " + forwardAction);
				response.sendRedirect(forwardAction);
				return null;
			} else {
				return mapping.findForward("success");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.toString());
			// request.getSession().invalidate();
			super.addErrors(e.getMessage(), request);
			return mapping.findForward("failure");
		}
	}
}
