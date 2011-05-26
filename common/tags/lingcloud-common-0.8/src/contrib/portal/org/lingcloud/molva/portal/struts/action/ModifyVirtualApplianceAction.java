/* 
 * @(#)ModifyVirtualApplianceAction.java 2009-10-6 
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

import java.util.ArrayList;
import java.util.List;

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
import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;

/**
 * <strong>Purpose:To modify a properties of a virtual appliance.</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2009-10-6<br>
 * @author Xiaoyi Lu<br>
 * @email luxiaoyi@software.ict.ac.cn
 */
public class ModifyVirtualApplianceAction extends NeedLoginAction {
	/**
	 * The logger for this class.
	 */
	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	private String url;

	public ActionForward dowork(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			DynaValidatorForm newVirtualAppForm = (DynaValidatorForm) form;
			if (newVirtualAppForm == null) {
				throw new Exception(
						"The form is submitted failed, please retry");
			}
			String guid = (String) newVirtualAppForm.get("guid");
			String name = (String) newVirtualAppForm.get("name");
			String os = (String) newVirtualAppForm.get("os");
			String osversion = (String) newVirtualAppForm.get("osversion");
			String format = (String) newVirtualAppForm.get("format");
			String app = (String) newVirtualAppForm.get("app");
			String category = (String) newVirtualAppForm.get("category");
			String loader = (String) newVirtualAppForm.get("loader");
			String[] accessway = (String[]) newVirtualAppForm.get("accessway");
			String cpuamount = (String) newVirtualAppForm.get("cpuamount");
			String memsize = (String) newVirtualAppForm.get("memsize");
			String language = (String) newVirtualAppForm.get("language");
			String loginstyle = (String) newVirtualAppForm.get("loginstyle");
			String username = (String) newVirtualAppForm.get("username");
			String password = (String) newVirtualAppForm.get("password");
			String description = (String) newVirtualAppForm.get("description");
			String thispage = (String) newVirtualAppForm.get("thispage");
			if (accessway == null || accessway.length == 0
					|| XMMPortalUtil.checkParamsBlankOrNull(accessway)
					|| XMMPortalUtil.checkParamsBlankOrNull(new String[] {
					name, os, memsize, category,
					 cpuamount, language, loginstyle}) 
					|| loginstyle.equals(VAMConstants.VA_LOGIN_STYLE_USER_PASS) 
					&& XMMPortalUtil.checkParamsBlankOrNull(new String[] {
							username, password})) {
				throw new Exception("Please input the correct parameters of "
						+ "name, os, category, " 
						+ "accessway, cpu amount, memery, language, " 
						+ "loginstyle, username, password!");
			}
			
			format = VAMConstants.VAF_FORMAT_DEFAULT;
			loader = VAMConstants.VA_BOOTLOAD_HVM;
			
			name = name.trim();
			os = os.trim();
			if (osversion == null) {
				osversion = "";
			}
			osversion = osversion.trim();
			format = format.trim();
			
			category = category.trim();
			loader = loader.trim();
			
			cpuamount = cpuamount.trim();
			int amount = Integer.parseInt(cpuamount);
			memsize = memsize.trim();
			int memery = Integer.parseInt(memsize);
			
			language = language.trim();
			loginstyle = loginstyle.trim();
			if (username != null) {
				username = username.trim();
			}
			if (password != null) {
				password = password.trim();
			}
			
			VAMUtil.checkDiskFormat(format);
			VAMUtil.checkBootLoader(loader);
			VAMUtil.checkLoginStyle(loginstyle);
			List<String> accessWayList = new ArrayList<String>();
			for (int i = 0; i < accessway.length; i++) {
				VAMUtil.checkAccessWay(accessway[i]);
				accessWayList.add(accessway[i]);
			}

			if (thispage == null || "".equals(thispage)) {
				this.url = request.getContextPath()
						+ "/JSP/viewVirtualAppliance.jsp";
			} else {
				if (!thispage.trim().startsWith("/")) {
					thispage = "/" + thispage.trim();
				}
				this.url = request.getContextPath() + thispage.trim();
			}
			log.info("User want to modify a virtual appliance from url : " + url);
			VirtualApplianceManager vam = VAMUtil.getVAManager();
			VirtualAppliance va = vam.queryAppliance(guid);
			va.setVAName(name);
			va.setAccessWay(accessWayList);
			va.setCpuAmount(amount);
			va.setMemory(memery);
			List<String> appl = new ArrayList<String>();
			if (app != null && !app.equals("")){
				String[] apps = app.split("\\|");
				for (int i = 0; i < apps.length; i++) {
					appl.add(apps[i]);
				}
			}
			va.setApplications(appl);
			// va.setBootLoader(loader);
			va.setCategory(category);
			va.setDescription(description);
			String[] langs = language.split("\\|");
			List<String> langl = new ArrayList<String>();
			for (int i = 0; i < langs.length; i++) {
				langl.add(langs[i]);
			}
			va.setLanguages(langl);
			va.setOs(os, osversion);
			// va.setFormat(format);
			if (loginstyle.equals(VAMConstants.VA_LOGIN_STYLE_GLOBAL_USER)) {
				va.setLoginStyle(VAMConstants.LOGIN_STYLE_GLOBAL_USER);
				va.setUsername("");
				va.setPassword("");
			} else {
				va.setLoginStyle(VAMConstants.LOGIN_STYLE_USER_PASS);
				va.setUsername(username);
				va.setPassword(password);
			}
			vam.updateAppliance(va);
			log.info("A virtual appliance with the name " + va.getVAName()
					+ " is updated successfully.");
			/*
			 * set object to request so that other pages can use.
			 */
			// request.setAttribute("thisPage", thisPage);
			request.getSession().removeAttribute("modifyVirtualApplianceForm");
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
