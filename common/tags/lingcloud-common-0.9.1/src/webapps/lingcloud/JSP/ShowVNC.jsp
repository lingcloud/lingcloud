<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%>
<%@ page import="org.lingcloud.molva.xmm.pojos.*"%>
<%@ page import="java.awt.Toolkit"%>
<%@ page import="org.apache.struts.Globals"%>
<%@ page import="java.util.*"%>
<%@page import="org.apache.commons.logging.Log"%>
<%@page import="org.apache.commons.logging.LogFactory"%>
<%@ page import="org.lingcloud.molva.xmm.util.XMMConstants"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	final Log log = LogFactory.getFactory()
			.getInstance(this.getClass());
	Locale loc = (Locale) request.getSession().getAttribute(
			Globals.LOCALE_KEY);
	String guid = (String) request.getParameter("guid");
	String nodetype = (String) request.getParameter("nodetype");

	if (guid == null || "".equals(guid)) {
		out.println("error=the node id is null or blank!");
		return;
	}

	if (nodetype == null || "".equals(nodetype)) {
		out.println("error=the node type is null or blank!");
		return;
	}

	XMMClient vxc = null;
	vxc = XMMPortalUtil.getXMMClient();
	try {
		Node node = null;
		if (XMMConstants.PHYSICAL_NODE_TYPE.equals(nodetype)) {
			node = vxc.viewPhysicalNode(guid);
		} else if (XMMConstants.VIRTUAL_NODE_TYPE.equals(nodetype)) {
			node = vxc.viewVirtualNode(guid);
		}

		if (node == null) {
			throw new Exception("No Node with guid " + guid + " !'");
		} else if (!node.isHeadNode()
				&& !XMMConstants.MachineRunningState.RUNNING.toString()
						.equals(node.getRunningStatus())) {
			throw new Exception("Wrong State of Node with guid " + guid
					+ ", you should login when its state is RUNNING!'");
		} else {
			//username:password
			String username = "";
			String pass = (String) request.getSession().getAttribute(
					"Password");
			//ip:port
			String ipport = XMMPortalUtil.getProperIpPort4VNC(node,
					pass, "1024x768");
			if (ipport == null || "".equals(ipport)) {
				throw new Exception("The node " + node.getName()
						+ " is not ready for vnc login.");
			}
			log.info(ipport + "test");
			String ip = ipport.split(":")[0];
			String port = ipport.split(":")[1];
			StringBuilder buf = new StringBuilder();

			buf
					.append("<APPLET CODE=\"com.glavsoft.viewer.Viewer\" ARCHIVE=\""
							+ basePath
							+ "JSP/VncApplet/tightvnc-jviewer.jar\" WIDTH=\"1px\" HEIGHT=\"1px\">");
			buf.append("<PARAM NAME=\"Host\" VALUE=\"" + ip + "\">");
			buf.append("<PARAM NAME=\"Port\" VALUE=\"" + port + "\">");

			buf.append("<PARAM NAME=\"OpenNewWindow\" VALUE=\"Yes\">");
			buf.append("<PARAM NAME=\"ShowControls\" VALUE=\"No\">");
			buf.append("<PARAM NAME=\"ViewOnly\" VALUE=\"No\">");
			buf.append("<PARAM NAME=\"ShareDesktop\" VALUE=\"Yes\">");
			buf.append("<PARAM NAME=\"AllowCopyRect\" VALUE=\"Yes\">");
			buf.append("<PARAM NAME=\"Encoding\" VALUE=\"Yes\">");
			buf.append("<PARAM NAME=\"LocalPointer\" VALUE=\"No\">");
			buf.append("<PARAM NAME=\"colorDepth\" VALUE=\"24\">");
			buf.append("<PARAM NAME=\"ScalingFactor\" VALUE=\"100\">");
			buf.append("</APPLET>");
			out.write(buf.toString());
			out.flush();
		}
	} catch (Exception e) {
		String error = "Service Internal Error: " + e.toString();
		out.println("error=" + error);
		return;
	}
%>

