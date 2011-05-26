<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%>
<%@ page import="org.lingcloud.molva.xmm.pojos.*"%>
<%@ page import="org.lingcloud.molva.xmm.ac.PartitionAC"%>
<%@ page import="org.lingcloud.molva.xmm.vam.pojos.*" %>
<%@ page import="org.lingcloud.molva.xmm.vam.services.*" %>
<%@ page import="org.lingcloud.molva.xmm.vam.util.*" %>
<%@ page import="org.lingcloud.molva.xmm.util.XMMConstants" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page isELIgnored="false"%>
<%
	String basePath = request.getParameter("basePath");
	if (basePath == null || "".equals(basePath)) {
		String path = request.getContextPath();
		basePath = request.getScheme() + "://"
				+ request.getServerName() + ":"
				+ request.getServerPort() + path + "/";
	}

	String appid = request.getParameter("appid");

	XMMClient vxc = null;
	VirtualApplianceManager vam = null;
	VirtualAppliance va = null;
	int cpuPreferNum = 1, memPreferSize = 512;
	vxc = XMMPortalUtil.getXMMClient();
	vam = VAMUtil.getVAManager();
	try {
		String result = "";
		va = vam.queryAppliance(appid);
		result += va.getCpuAmount() + ";" + va.getMemory();
		out.println(result);
	} catch (Exception e) {
		out.println("");
		return;
	}
%>

