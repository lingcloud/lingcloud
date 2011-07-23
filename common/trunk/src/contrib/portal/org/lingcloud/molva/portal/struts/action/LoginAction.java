/* 
 * @(#)LoginAction.java Jul 18, 2011
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
import org.lingcloud.molva.portal.util.XMMPortalUtil;

import org.lingcloud.molva.portal.util.AccessControl;

/**
 * <strong>Purpose:To deal with the user's login action.</strong><br>
 * 
 * @version 1.0.1 2011-7-18<br>
 * @author Xuechao Liu<br>
 */

public class LoginAction extends NeedLoginAction {

	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	private String url;

	public ActionForward dowork(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		try {

			DynaValidatorForm loginForm = (DynaValidatorForm) form;
			if (loginForm == null) {
				throw new Exception(
						"The form is submitted failed, please retry");
			}
			String username = (String)loginForm.get("username");
			String password = (String)loginForm.get("password");
			if (XMMPortalUtil.checkParamsBlankOrNull(new String[] { username,
					password })) {

				throw new Exception("Please input the correct parameters of "
						+ "username, password");

			}
			username = username.trim();
			
			log.info("username:"+username+"\n"+"password:"+password);
			
			AccessControl ac = new AccessControl();
			ac.setUsername(username);
			boolean result = ac.Authenticate(username, password);
			HttpSession session = request.getSession();
			if (result == true)
			{
				result = ac.isAdmin(username);
				if (result == true)
				{
					log.info("the result is true");
					ac.setStatus(AccessControl.accessControlStatus.ADMIN);
					log.info("the ac status is set"+ac.getStatus());
					session.setAttribute("ACobject", ac);
					log.info("the acobject is insert into session");
					return mapping.findForward("success");
					
				}
				else 
				{
					return mapping.findForward("failure");
				}
			}
			else 
			{
				return mapping.findForward("failure");
			}
		
		}
		catch (Exception e) {
			log.error(e.toString());
			super.addErrors(e.getMessage(), request);
			return mapping.findForward("failure");
	}
	}
}

