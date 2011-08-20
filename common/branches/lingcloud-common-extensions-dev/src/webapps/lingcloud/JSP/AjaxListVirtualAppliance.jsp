<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ page import="org.lingcloud.molva.xmm.util.*"%><%@ page import="org.lingcloud.molva.xmm.vam.pojos.*" %><%@ page import="org.lingcloud.molva.xmm.vam.services.*" %><%@ page import="org.lingcloud.molva.xmm.vam.util.*"%><%
	// Notice can't format this file.
	VirtualApplianceManager vam = null;
	List<VirtualAppliance> val = null;
	vam = VAMUtil.getVAManager();
	try {
		val = vam.getAllAppliance();
	} catch (Exception e) {
		//
	}
	response.setContentType("text/xml;charset=UTF-8");
	response.setHeader("Pragma","No-Cache");
	response.setHeader("Cache-Control","No-Cache");
	response.setDateHeader("Expires", 0);
	StringBuffer buf = new StringBuffer();
	buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	buf.append("<VirtualAppliance>");
	if (val != null && val.size() > 0) {
		for (int i = 0; i < val.size(); i++) {
			VirtualAppliance va = val.get(i);
			if (va.getState() != VAMConstants.STATE_READY) {
				continue;
			}
			buf.append("<option>");
			buf.append("<value>" + val.get(i).getGuid() + "</value>");
			buf.append("<text>" + val.get(i).getVAName() + "</text>");
			buf.append("</option>");

		}
	}
	if (!(buf.toString().indexOf("option") > -1)) {
		buf.append("<option>");
		buf.append("<value>-1</value>");
		buf.append("<text>No Available VirtualAppliance</text>");
		buf.append("</option>");
	}
	buf.append("</VirtualAppliance>");
	out.write(buf.toString());
	out.flush();
%>
