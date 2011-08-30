<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ page import="org.lingcloud.molva.xmm.vam.pojos.*"%><%@ page import="org.lingcloud.molva.xmm.vam.services.*"%><%@ page import="org.lingcloud.molva.xmm.vam.util.*"%><%
	// Notice can't format this file.
	response.setHeader("Pragma","No-Cache");
	response.setHeader("Cache-Control","No-Cache");
	response.setDateHeader("Expires", 0);
	VirtualApplianceManager vam = null;
	List vacl = null;
	vam = VAMUtil.getVAManager();
	try {
		vacl = vam.getAllCategory();
	} catch (Exception e) {
		//
	}
	response.setContentType("text/xml;charset=UTF-8");
	response.setHeader("Pragma","No-Cache");
	response.setHeader("Cache-Control","No-Cache");
	response.setDateHeader("Expires", 0);
	StringBuffer buf = new StringBuffer();
	buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	buf.append("<ApplianceCategory>");
	
	buf.append("<option>");
	buf.append("<value>0</value>");
	buf.append("<text>No Category</text>");
	buf.append("</option>");
	
	if (vacl != null && vacl.size() > 0) {
		for (int i = 0; i < vacl.size(); i++) {
			VACategory vac = (VACategory) vacl.get(i);
			buf.append("<option>");
			buf.append("<value>" + vac.getGuid() + "</value>");
			buf.append("<text>" + vac.getCategory() + "</text>");
			buf.append("</option>");
		}
	}
	if (!(buf.toString().indexOf("option") > -1)) {
		buf.append("<option>");
		buf.append("<value>-1</value>");
		buf.append("<text>No Available ApplianceCategory</text>");
		buf.append("</option>");
	}
	buf.append("</ApplianceCategory>");
	out.write(buf.toString());
	out.flush();
%>
