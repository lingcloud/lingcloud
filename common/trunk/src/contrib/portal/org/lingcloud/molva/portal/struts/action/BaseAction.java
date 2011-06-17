/* 
 * @(#)BaseAction.java Jan 3, 2008 
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

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionServlet;

/**
 * <strong>Purpose:A base action of all actions.</strong><br>
 * 
 * @version 1.0.1 Jan 3, 2008<br>
 * @author Xiaoyi Lu<br>
 */
@SuppressWarnings("deprecation")
public abstract class BaseAction extends Action {

	public void setServlet(ActionServlet actionServlet) {
		super.setServlet(actionServlet);
		// ServletContext servletContext = actionServlet.getServletContext();
	}

	protected void addErrors(String msg, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		errors.add("GLOBAL_MESSAGE", new ActionError("global.errors",
				"Operation Failed: " + msg));
		this.saveErrors(request, errors);
	}

	protected void addSuccess(String msg, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		errors.add("GLOBAL_MESSAGE", new ActionError("global.errors",
				"Operation Success: " + msg));
		this.saveErrors(request, errors);
	}
}
