<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%>
<%@ page import="org.lingcloud.molva.xmm.pojos.*"%>
<%@ page import="org.lingcloud.molva.xmm.util.XMMConstants"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	String guid = (String) request.getParameter("guid");
	String nodetype = (String) request.getParameter("nodetype");
	String username = (String) request.getParameter("username");
	String pass = (String) request.getParameter("pass");
	if (guid == null || "".equals(guid)) {
		out.println("error=the node id is null or blank!");
		return;
	}

	if (nodetype == null || "".equals(nodetype)) {
		out.println("error=the node type is null or blank!");
		return;
	}

	String password = (String) request.getSession().getAttribute(
			"Password");
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
				&& !XMMConstants.MachineRunningState.RUNNING
						.toString().equals(node.getRunningStatus())) {
			throw new Exception("Wrong State of Node with guid " + guid
					+ ", you should login when its state is RUNNING!'");
		} else {
			//ip:port
			String ipport = XMMPortalUtil.getProperIpPort4SSH(node);
			if (ipport == null || "".equals(ipport)) {
				throw new Exception("The node " + node.getName()
						+ " is not ready for ssh login.");
			}
			String ip = ipport.split(":")[0];
			String port = ipport.split(":")[1];
			StringBuilder buf = new StringBuilder();
			buf
					.append("<APPLET CODE=\"applet.ssh.JCTermApplet.class\" CODEBASE=\""
							+ basePath
							+ "JSP/SshApplet/\" ARCHIVE=\"jsch-0.1.38.jar,lingcloud-client-ssh.jar\" WIDTH=\"1px\" HEIGHT=\"1px\">");
			buf.append("<PARAM NAME=\"HOST\" VALUE=\"" + ip + "\">");
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

