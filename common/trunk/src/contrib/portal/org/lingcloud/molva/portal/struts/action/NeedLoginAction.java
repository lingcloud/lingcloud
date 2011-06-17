/* 
 * @(#)NeedLoginAction.java 2009-10-6 
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

/**
 * <strong>Purpose:To check the authorization.</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2009-10-6<br>
 * @author Xiaoyi Lu<br>
 */
public abstract class NeedLoginAction extends BaseAction {

	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	public abstract ActionForward dowork(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String path = request.getContextPath();
		String hostPath = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort();
		String basePath = hostPath + path + "/";
		try {
			return this.dowork(mapping, form, request, response);
		} catch (Exception e) {
			log.error(e.getMessage() + ", go to " + basePath + " to login. "
					+ "Then the url will be redirected to " + hostPath
					+ request.getRequestURI());
			String thispage = (String) request.getParameter("thispage");
			request.getSession().invalidate();
			super.addErrors(e.getMessage(), request);
			request.getSession().setAttribute("forwardAction",
					basePath + thispage);
			return mapping.findForward("global-signon");
		}
	}
}
