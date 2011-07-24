/* 
 * @(#)MakeNewVirtualApplianceAction.java 2009-10-6 
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
import org.lingcloud.molva.xmm.vam.util.VAMConfig;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;

/**
 * <strong>Purpose:To make a new virtual appliance.</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2009-10-6<br>
 * @author Xiaoyi Lu<br>
 */
public class MakeNewVirtualApplianceAction extends NeedLoginAction {
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
			String name = (String) newVirtualAppForm.get("appname");
			String vcd = (String) newVirtualAppForm.get("vcd");
			String os = (String) newVirtualAppForm.get("os");
			String osversion = (String) newVirtualAppForm.get("osversion");
			String memsize = (String) newVirtualAppForm.get("memsize");
			String diskcapacity = (String) newVirtualAppForm
					.get("diskcapacity");
			String format = (String) newVirtualAppForm.get("format");
			String loader = (String) newVirtualAppForm.get("loader");
			String cpuamount = (String) newVirtualAppForm.get("cpuamount");
			String thispage = (String) newVirtualAppForm.get("thispage");
			if (XMMPortalUtil.checkParamsBlankOrNull(new String[] { name, vcd,
					os, memsize, diskcapacity, cpuamount })) {
				throw new Exception("Please input the correct parameters of "
						+ "name, os, memsize, disk capacity, " + "cpu amount!");
			}

			format = VAMConfig.getImageFormat();
			loader = VAMConstants.VA_BOOTLOAD_HVM;

			name = name.trim();
			vcd = vcd.trim();
			os = os.trim();
			if (osversion == null) {
				osversion = "";
			}
			osversion = osversion.trim();
			memsize = memsize.trim();
			int memery = Integer.parseInt(memsize);
			long capacity;
			capacity = Long.parseLong(diskcapacity);
			loader = loader.trim();
			int amount;
			amount = Integer.parseInt(cpuamount);
			loader = loader.trim();
			format = format.trim();

			VAMUtil.checkDiskFormat(format);
			VAMUtil.checkBootLoader(loader);

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
			va.setVAName(name);
			va.setCpuAmount(amount);
			va.setMemory(memery);

			List<String> vcdl = new ArrayList<String>();
			vcdl.add(vcd);
			va.setDiscs(vcdl);
			va.setCapacity(capacity * VAMConstants.GB);
			va.setBootLoader(loader);

			va.setOs(os, osversion);
			va.setFormat(format);

			vam.makeAppliance(va, null, memery);
			log.info("A virtual appliance with the name " + va.getVAName()
					+ " is being made successfully.");
			/*
			 * set object to request so that other pages can use.
			 */
			// request.setAttribute("thisPage", thisPage);
			request.getSession().removeAttribute("makeNewVirtualApplianceForm");
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
