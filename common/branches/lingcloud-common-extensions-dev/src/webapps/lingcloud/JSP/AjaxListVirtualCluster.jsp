<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%><%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%><%@ page import="org.lingcloud.molva.xmm.pojos.*"%><%
	// Notice can't format this file.
	String parid = request.getParameter("parid");
	XMMClient vxc = null;
	List<VirtualCluster> vcl = null;
	vxc = XMMPortalUtil.getXMMClient();
	try {
		vcl = vxc.listVirtualCluster(parid);
	} catch (Exception e) {
		//
	}
	response.setContentType("text/xml;charset=UTF-8");
	response.setHeader("Pragma","No-Cache");
	response.setHeader("Cache-Control","No-Cache");
	response.setDateHeader("Expires", 0);
	StringBuffer buf = new StringBuffer();
	buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	buf.append("<VirtualCluster>");
	if (vcl != null && vcl.size() > 0) {
		for (int i = 0; i < vcl.size(); i++) {
			buf.append("<option>");
			buf.append("<value>" + vcl.get(i).getGuid() + "</value>");
			buf.append("<text>" + vcl.get(i).getName() + "</text>");
			buf.append("</option>");
		}
	}
	if (!(buf.toString().indexOf("option") > -1)) {
		buf.append("<option>");
		buf.append("<value>-1</value>");
		buf.append("<text>No Available VirtualCluster</text>");
		buf.append("</option>");
	}
	buf.append("</VirtualCluster>");
	out.write(buf.toString());
	out.flush();
%>
