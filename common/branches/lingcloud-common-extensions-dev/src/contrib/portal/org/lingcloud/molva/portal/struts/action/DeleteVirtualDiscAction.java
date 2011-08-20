/* 
 * @(#)DeleteVirtualDiscAction.java 2009-10-6 
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
import org.lingcloud.molva.xmm.vam.pojos.VAFile;
import org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;

/**
 * <strong>Purpose:To delete a virtual disc.</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2009-10-6<br>
 * @author Xiaoyi Lu<br>
 */
public class DeleteVirtualDiscAction extends NeedLoginAction {
	/**
	 * The logger for this class.
	 */
	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	private String url;

	public ActionForward dowork(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		try {

			DynaValidatorForm deleteFileForm = (DynaValidatorForm) form;
			if (deleteFileForm == null) {
				throw new Exception(
						"The form is submitted failed, please retry: ");
			}
			String guid = (String) deleteFileForm.get("guid");
			String thisPage = (String) deleteFileForm.get("thispage");
			if (XMMPortalUtil.checkParamsBlankOrNull(new String[] { guid })) {
				// Get form from session, try again.
				throw new Exception("Please input the correct parameters of "
						+ "file guid: ");
				// }
			}
			guid = guid.trim();

			if (thisPage == null || "".equals(thisPage)) {
				this.url = request.getContextPath() + "/JSP/viewFile.jsp";
			} else {
				if (!thisPage.trim().startsWith("/")) {
					thisPage = "/" + thisPage.trim();
				}
				this.url = request.getContextPath() + thisPage.trim();
			}
			log
					.info("User want to delete a file " + guid + "from url : "
							+ url);

			VirtualApplianceManager vam = VAMUtil.getVAManager();
			VAFile vafile = vam.queryFile(guid);
			vam.removeFile(guid);
			log.info(" A file with the name " + vafile.getId() + " "
					+ vafile.getGuid() + " is removed successfully.");
			/*
			 * set object to request so that other pages can use.
			 */
			request.getSession().removeAttribute("deleteFileForm");
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
