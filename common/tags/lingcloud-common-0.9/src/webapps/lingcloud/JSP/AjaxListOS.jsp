<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ page import="org.lingcloud.molva.xmm.vam.pojos.*"%><%@ page import="org.lingcloud.molva.xmm.vam.services.*"%><%@ page import="org.lingcloud.molva.xmm.vam.util.*"%><%
	// Notice can't format this file.
	String[] osList = VAMConstants.VA_OS_LIST;
	response.setContentType("text/xml;charset=UTF-8");
	response.setHeader("Pragma","No-Cache");
	response.setHeader("Cache-Control","No-Cache");
	response.setDateHeader("Expires", 0);
	StringBuffer buf = new StringBuffer();
	buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	buf.append("<OperationSystem>");
	if (osList != null && osList.length > 0) {
		for (int i = 0; i < osList.length; i++) {
			buf.append("<option>");
			buf.append("<value>" + osList[i] + "</value>");
			buf.append("<text>" + osList[i] + "</text>");
			buf.append("</option>");
		}
	}
	if (!(buf.toString().indexOf("option") > -1)) {
		buf.append("<option>");
		buf.append("<value>-1</value>");
		buf.append("<text>No Available Operation System</text>");
		buf.append("</option>");
	}
	buf.append("</OperationSystem>");
	out.write(buf.toString());
	out.flush();
%>
