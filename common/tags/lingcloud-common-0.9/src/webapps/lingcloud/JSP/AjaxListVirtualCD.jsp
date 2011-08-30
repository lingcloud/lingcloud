<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ page import="org.lingcloud.molva.xmm.util.*"%><%@ page import="org.lingcloud.molva.xmm.vam.pojos.*" %><%@ page import="org.lingcloud.molva.xmm.vam.services.*" %><%@ page import="org.lingcloud.molva.xmm.vam.util.*"%><%
	// Notice can't format this file.
	VirtualApplianceManager vam = null;
	List<VAFile> vafl = null;
	vam = VAMUtil.getVAManager();
	StringBuffer buf = new StringBuffer();
	try {
		vafl = vam.getFilesByType(VAMConstants.VAF_FILE_TYPE_DISC);
	} catch (Exception e) {
		buf.append(e.getMessage());
	}
	response.setContentType("text/xml;charset=UTF-8");
	response.setHeader("Pragma","No-Cache");
	response.setHeader("Cache-Control","No-Cache");
	response.setDateHeader("Expires", 0);
	buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	buf.append("<VirtualCD>");
	if (vafl != null && vafl.size() > 0) {
		for (int i = 0; i < vafl.size(); i++) {
			buf.append("<option>");
			buf.append("<value>" + vafl.get(i).getGuid() + "</value>");
			buf.append("<text>" + vafl.get(i).getId() + "</text>");
			buf.append("</option>");

		}
	}
	if (!(buf.toString().indexOf("option") > -1)) {
		buf.append("<option>");
		buf.append("<value>-1</value>");
		buf.append("<text>No Available VirtualCD</text>");
		buf.append("</option>");
	}
	buf.append("</VirtualCD>");
	out.write(buf.toString());
	out.flush();
%>
