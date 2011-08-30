<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%>
<%@ page import="org.lingcloud.molva.xmm.pojos.*"%>
<%@ page import="org.lingcloud.molva.xmm.util.XMMConstants"%>

<%String path = request.getContextPath();
			String basePath = request.getScheme() + "://"
					+ request.getServerName() + ":" + request.getServerPort()
					+ path + "/";
			String guid = (String) request.getParameter("guid");
			String nodetype = (String) request.getParameter("nodetype");
			String screensize = (String) request.getParameter("screensize");
			String rdprfile = (String) request.getParameter("rdprfile");
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

			String username = "";
			vxc = XMMPortalUtil.getXMMClient();
			try {
				Node node = null;
				if (XMMConstants.PHYSICAL_NODE_TYPE.equals(nodetype)) {
					node = vxc.viewPhysicalNode(guid);
				} else if (XMMConstants.VIRTUAL_NODE_TYPE
						.equals(nodetype)) {
					node = vxc.viewVirtualNode(guid);
				}

				if (node == null) {
					throw new Exception("No Node with guid " + guid + " !'");
				} else if (!node.isHeadNode()
						&& !XMMConstants.MachineRunningState.RUNNING
								.toString().equals(node.getRunningStatus())) {
					throw new Exception(
							"Wrong State of Node with guid "
									+ guid
									+ ", you should login when its state is RUNNING!'");
				} else {
					//ip:port	
									
					String ipport = XMMPortalUtil.getProperIpPort4RDP(node);
					if (ipport == null || "".equals(ipport)) {
						throw new Exception("The node " + node.getName()
								+ " is not ready for rdp login.");
					}
					String ip = ipport.split(":")[0];
					String port = ipport.split(":")[1];
					
					StringBuilder buf = new StringBuilder();
					//buf.append("<script language=\"javascript\"> function tabkeyPressHandler(event) {event = event || window.event; if(event.keyCode==9){alert('js');}}</script>");
					buf.append("<APPLET CODE=\"net.propero.rdp.applet.RdpApplet\" CODEBASE=\""
									+ basePath
									+ "JSP/RdpApplet/\" ARCHIVE=\"properoRDP.jar,java-getopt-1.0.9.jar,log4j-1.2.14.jar\" WIDTH=\"1px\" HEIGHT=\"1px\">");
					buf.append("<PARAM NAME=\"server\" VALUE=\"" + ip
									+ "\"/>");
					if(screensize.startsWith("f")){
						buf.append("<PARAM NAME=\"fullscreen\" VALUE=\" \"/>");
					}else{
						buf.append("<PARAM NAME=\"geometry\" VALUE=\"" + screensize + "\"/>");
					}
					if(rdprfile != null && rdprfile != "")
						buf.append("<PARAM NAME=\"rdprfile\" VALUE=\"disk:fs=" + rdprfile + "\"/>");
					buf.append("<PARAM NAME=\"username\" VALUE=\"Administrator\">");
					buf.append("</APPLET>");
					out.write(buf.toString());
					out.flush();
				}
			} catch (Exception e) {
				String error = "Service Internal Error: "+e.toString();
				out.println("error=" + error);
				return;
			}

		%>

