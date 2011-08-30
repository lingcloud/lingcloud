/* 
 * @(#)LogoutAction.java Jul 18, 2011
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
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;

/**
 * <strong>Purpose:To deal with the user's login action.</strong><br>
 * 
 * @version 1.0.1 2011-7-18<br>
 * @author Xuechao Liu<br>
 */

public class LogoutAction extends NeedLoginAction {

	/**
	 * get the common log factory. 
	 */
	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	public ActionForward dowork(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		try {

			DynaValidatorForm logoutForm = (DynaValidatorForm) form;
			if (logoutForm == null) {
				throw new Exception(
						"The form is submitted failed, please retry");
			}

			HttpSession session = request.getSession();
			session.removeAttribute("ACobject");
			return mapping.findForward("success");
		} catch (Exception e) {
			log.error(e.toString());
			super.addErrors(e.getMessage(), request);
			return mapping.findForward("failure");
	}
	}
}

