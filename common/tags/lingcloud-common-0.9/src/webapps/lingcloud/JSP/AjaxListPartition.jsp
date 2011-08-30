<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%><%@ page import="org.lingcloud.molva.xmm.pojos.*"%><%
	// Notice can't format this file.
	List<Partition> uhl = null;
	try {
		uhl = XMMPortalUtil.listAllPartition();
	} catch (Exception e) {
		//
	}
	response.setContentType("text/xml;charset=UTF-8");
	response.setHeader("Pragma","No-Cache");
	response.setHeader("Cache-Control","No-Cache");
	response.setDateHeader("Expires", 0);
	StringBuffer buf = new StringBuffer();
	buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	buf.append("<Partition>");

	if (uhl != null && uhl.size() > 0) {
		for (int i = 0; i < uhl.size(); i++) {
			buf.append("<option>");
			buf.append("<value>" + uhl.get(i).getGuid() + "</value>");
			buf.append("<text>" + uhl.get(i).getName() + "</text>");
			buf.append("</option>");
		}
	}
	if (!(buf.toString().indexOf("option") > -1)) {
		buf.append("<option>");
		buf.append("<value>-1</value>");
		buf.append("<text>No Partition Info</text>");
		buf.append("</option>");
	}
	buf.append("</Partition>");
	out.write(buf.toString());
	out.flush();
%>
