/*
 *  @(#)ShowConfigAction.java Oct 10, 2011
 *
 *  Copyright (C) 2008-2011,
 *  Vega LingCloud Team,
 *  Institute of Computing Technology,
 *  Chinese Academy of Sciences.
 *  P.O.Box 2704, 100190, Beijing, China.
 *
 *  http://lingcloud.org
 *
 */

package org.lingcloud.molva.extensions.example.portal.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;
import org.lingcloud.molva.portal.struts.action.NeedLoginAction;
import org.lingcloud.molva.extensions.example.util.ExampleUtil;
import org.lingcloud.molva.extensions.example.test.Util;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 Oct 10, 2011<br>
 * @author Ruijian Wang<br>
 * 
 */

public class ExampleAction extends NeedLoginAction {
	/**
	 * The logger for this class.
	 */
	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	public ActionForward dowork(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			DynaValidatorForm exampleForm = (DynaValidatorForm) form;
			if (exampleForm == null) {
				throw new Exception(
						"The form is submitted failed, please retry: ");
			}
			
			String action = (String) exampleForm.get("action");
			
			String extensionsDir = ExampleUtil.getExtensionsDir("");
			String exampleDir = ExampleUtil.getExampleDir("");
			String exampleConf = ExampleUtil.getExampleConf("");
			String message = "";
			
			if (action != null && action.equals("message")) {
				message = (String) exampleForm.get("message");
			} else {
				String command = exampleDir + "/bin/test.sh";
				message = Util.executeCommand(command);
			}
			
			request.setAttribute("message", message);
			request.setAttribute("extensionsDir", extensionsDir);
			request.setAttribute("exampleDir", exampleDir);
			request.setAttribute("exampleConf", exampleConf);
			request.getSession().removeAttribute("exampleForm");
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
