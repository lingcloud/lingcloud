<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ page import="org.lingcloud.molva.xmm.util.*"%><%@ page import="org.lingcloud.molva.xmm.vam.pojos.*" %><%@ page import="org.lingcloud.molva.xmm.vam.services.*" %><%@ page import="org.lingcloud.molva.xmm.vam.util.*"%><%
	// Notice can't format this file.
	String type = request.getParameter("type");
	String format = request.getParameter("format");
	List<String> fileList = VAMUtil.listDirectory(VAMConfig
			.getUploadDirLocation(), type, format);

	response.setContentType("text/xml;charset=UTF-8");
	response.setHeader("Pragma","No-Cache");
	response.setHeader("Cache-Control","No-Cache");
	response.setDateHeader("Expires", 0);
	StringBuffer buf = new StringBuffer();
	buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	buf.append("<File>");
	if (fileList != null && fileList.size() > 0) {
		for (int i = 0; i < fileList.size(); i++) {
			String filename = fileList.get(i);
			String text = filename;
			if (text.length() > 20) {
				text = text.substring(0, 20) + "...";
			}
			buf.append("<option>");
			buf.append("<value>" + fileList.get(i) + "</value>");
			buf.append("<text>" + text + "</text>");
			buf.append("</option>");

		}
	}
	if (!(buf.toString().indexOf("option") > -1)) {
		buf.append("<option>");
		buf.append("<value>-1</value>");
		buf.append("<text>No Available File</text>");
		buf.append("</option>");
	}
	buf.append("</File>");
	out.write(buf.toString());
	out.flush();
%>
