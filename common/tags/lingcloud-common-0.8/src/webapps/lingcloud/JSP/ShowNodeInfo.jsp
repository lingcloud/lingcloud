<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%>
<%@ page import="org.lingcloud.molva.xmm.pojos.*"%>
<%@ page import="org.lingcloud.molva.ocl.asset.AssetConstants.AssetState"%>
<%@ page import="org.lingcloud.molva.ocl.lease.LeaseConstants.LeaseLifeCycleState"%>
<%@ page import="org.lingcloud.molva.xmm.ac.PartitionAC"%>
<%@ page import="org.lingcloud.molva.xmm.util.XMMConstants"%>
<%@ page import="org.lingcloud.molva.xmm.vam.util.VAMConstants"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page isELIgnored="false"%>
<%String basePath = request.getParameter("basePath");
			String virid = request.getParameter("nodeid");
			String nodetype = request.getParameter("nodetype");
			if (basePath == null || "".equals(basePath)) {
				String path = request.getContextPath();
				basePath = request.getScheme() + "://"
						+ request.getServerName() + ":"
						+ request.getServerPort() + path + "/";
			}
			response.setHeader("Pragma","No-Cache");
			response.setHeader("Cache-Control","No-Cache");
			response.setDateHeader("Expires", 0);
			if (virid == null || "".equals(virid)) {
				out.println(XMMPortalUtil.getMessage("org.lingcloud.molva.xmm.physicalNode.select"));
				return;
			}

			String error = null;
			XMMClient vxc = null;
			Node node = null;

			vxc = XMMPortalUtil.getXMMClient();
			try {
				if (nodetype != null) {
					if (XMMConstants.VIRTUAL_NODE_TYPE.equals(nodetype)) {
						node = vxc.viewVirtualNode(virid);
					} else if (XMMConstants.PHYSICAL_NODE_TYPE
							.equals(nodetype)) {
						node = vxc.viewPhysicalNode(virid);
					} else {
						node = null;
					}
				}
			} catch (Exception e) {
				error = e.toString();
				out.println(error);
				return;
				//response.sendRedirect(basePath+"JSP/error.jsp?error="+error);
			}

			if (node == null) {
				out.println(XMMPortalUtil.getMessage("org.lingcloud.molva.xmm.physicalNode.notExist"));
				return;
			}

			%>
<table border="0" cellspacing=1 width="100%">
	<tbody>
		<tr class="actionlog_title">
			<th width="25" valign="middle">
				<bean:message key="org.lingcloud.molva.xmm.virtualCluster.operation" />
			</th>
			<th width="300" valign="middle">
				<bean:message key="org.lingcloud.molva.xmm.machine.info" />
			</th>
			<th width="100" valign="middle">
				<bean:message key="org.lingcloud.molva.xmm.machine.deploy.info" />
			</th>
			<th width="80" valign="middle">
				<bean:message key="org.lingcloud.molva.xmm.machine.cpuinfo" />
			</th>
			<th width="70" valign="middle">
				<bean:message key="org.lingcloud.molva.xmm.machine.meminfo" />
			</th>
			<th width="100" valign="middle">
				<bean:message key="org.lingcloud.molva.xmm.virtualAppliance.accessway" />
			</th>
		</tr>
		<%String mimg = basePath + "/images/node.png";
			String publicIpStr = "";
			if (node.isHeadNode()) {
				mimg = basePath + "images/headnode.png";
			}
			if (node.getPublicIps() != null && node.getPublicIps().length > 0) {
				publicIpStr = "&nbsp/&nbsp<a href=\"http://"
						+ node.getPublicIps()[0] + "\">"
						+ node.getPublicIps()[0] + "</a>";
			} else {
				publicIpStr = "";
			}

			%>
		<tr class="actionlog">
			<td align="center" width="25">
				<a
				title="<bean:message key="org.lingcloud.molva.xmm.refresh"/>"
				href="javascript:showNodeInfo('<%=basePath%>','<%=virid%>','<%=nodetype%>');"><img
				src="<%=basePath%>/images/refresh.png" style="border: medium none;"
				width="16" height="16" /></a><br />
			</td>
			<td width="200">
				<img src="<%=mimg%>" style="border: medium none ;" width="16" height="16" />
				<%=node.getName()%>
				| <font color="green"> <%if (node.isHeadNode()) {%> <bean:message key="org.lingcloud.molva.xmm.vc.headNode" /> <%} else {%> <bean:message key="org.lingcloud.molva.xmm.vc.slaveNode" /> <%}%></font>
				<br />
				<b><bean:message key="org.lingcloud.molva.xmm.running.status" /></b>: <font color="red"><%=node.getRunningStatus()%></font>
				<%if (!XMMConstants.MachineRunningState.RUNNING.toString()
					.equals(node.getRunningStatus())) {
					String msg = node.getLastErrorMessage();
							if(msg == null || "".equals(msg))
								msg = XMMPortalUtil.getMessage("org.lingcloud.molva.xmm.cluster.nostatus");
					%>
				<a title="<bean:message key="org.lingcloud.molva.xmm.machine.status.tips" />" href="javascript:void(0)" onclick="showWrongStatus('<%=msg %>')"><img src="<%=basePath%>images/lock.png" /></a>
				<%}

			%>
				<br />
				<b><bean:message key="org.lingcloud.molva.xmm.lease.status" /></b>: <font color="red"><%=node.getAssetState()%></font>
				<br />
				<b><bean:message key="org.lingcloud.molva.xmm.virtualNetwork.ip" /></b>:<a href="http://<%=node.getName()%>"><%=node.getName()%></a>
				<%=publicIpStr%>
				<br />
				<b><bean:message key="org.lingcloud.molva.xmm.acl" /></b> :
				<%=node.getAcl()%>
			</td>
			<td width="100">
				<%if (node instanceof VirtualNode) {

				%>
				<b><bean:message key="org.lingcloud.molva.xmm.vm" /></b>
				<br>
				<b><bean:message key="org.lingcloud.molva.virtualnode.deploy.host" /></b> :
				<%=((VirtualNode) node).getParentPhysialNodeName()%>
				<br>
				<b><bean:message key="org.lingcloud.molva.xmm.virtualAppliance" /></b> :
				<a href="javascript:showApplianceInfo('<%=basePath%>','<%=((VirtualNode) node).getApplianceId()%>','<%=((VirtualNode) node).getApplianceName()%>');" ><%=((VirtualNode) node).getApplianceName()%></a>
				<%} else if (node instanceof PhysicalNode) {
				nodetype = XMMConstants.PHYSICAL_NODE_TYPE;

			%>
				<b><bean:message key="org.lingcloud.molva.xmm.pm" /></b>
				<%}%>
			</td>
			<td width="100">
				<b><bean:message key="org.lingcloud.molva.xmm.virtualAppliance.cpuamount" /></b>:
				<%=node.getCpuNum()%>
				<br />
			</td>
			<td width="100">
				<b><bean:message key="org.lingcloud.molva.xmm.virtualAppliance.memsize" /></b>:
				<%=node.getMemorySize()%>
				<br />
			</td>
			<td>
				<%List<String> accessWay = node.getAccessWay();
			if (accessWay == null || accessWay.size() <= 0) {
				if (!node.isHeadNode()) {%>
				<bean:message key="org.lingcloud.molva.xmm.node.accessway.empty" />
				<br />
				<%} else {%>
				<%=VAMConstants.VA_ACCESS_WAY_VNC%>
				:<a title="<bean:message key="org.lingcloud.molva.xmm.physicalNode.vncConnection" />" href="JavaScript:showVNC('<%=basePath%>','<%=node.getGuid()%>','<%=nodetype%>');"> <img src="<%=basePath%>/images/vnc.png" style="border: medium none ;" width="16" height="16" /> </a>
				<%}
			} else {
				for (int a = 0; a < accessWay.size(); a++) {
					String acw = accessWay.get(a);
					if (acw.equals(VAMConstants.VA_ACCESS_WAY_SSH)) {

						%>
				<%=VAMConstants.VA_ACCESS_WAY_SSH%>
				:<a title="<bean:message key="org.lingcloud.molva.xmm.physicalNode.sshConnection" />" href="JavaScript:showSSH('<%=basePath%>','<%=node.getGuid()%>','<%=nodetype%>');"> <img src="<%=basePath%>/images/ssh.png" style="border: medium none ;" width="16" height="16" /> </a>
				<br />
				<%} else if (acw.equals(VAMConstants.VA_ACCESS_WAY_VNC)) {

						%>
				<%=VAMConstants.VA_ACCESS_WAY_VNC%>
				:<a title="<bean:message key="org.lingcloud.molva.xmm.physicalNode.vncConnection" />" href="JavaScript:showVNC('<%=basePath%>','<%=node.getGuid()%>','<%=nodetype%>');"> <img src="<%=basePath%>/images/vnc.png" style="border: medium none ;" width="16" height="16" /> </a>
				<br />
				<%} else if (acw.equals(VAMConstants.VA_ACCESS_WAY_RDP)) {

						%>
				<%=VAMConstants.VA_ACCESS_WAY_RDP%>
				:<a title="<bean:message key="org.lingcloud.molva.xmm.physicalNode.rdpConnection" />" href="JavaScript:showRDP('<%=basePath%>','<%=node.getGuid()%>','<%=nodetype%>');"> <img src="<%=basePath%>/images/rdp.png" style="border: medium none ;" width="16" height="16" /> </a>
				<br />
				<%} else {

					%>
				<bean:message key="org.lingcloud.molva.xmm.node.accessway.unsupport" />
				<%}
				}
			}

		%>
			</td>
		</tr>
	</tbody>
</table>
