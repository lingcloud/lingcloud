<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%>
<%@ page import="org.lingcloud.molva.xmm.pojos.*"%>
<%@ page
	import="org.lingcloud.molva.ocl.asset.AssetConstants.AssetState"%>
<%@ page
	import="org.lingcloud.molva.ocl.lease.LeaseConstants.LeaseLifeCycleState"%>
<%@ page import="org.lingcloud.molva.xmm.ac.PartitionAC"%>
<%@ page import="org.lingcloud.molva.xmm.util.XMMConstants"%>
<%@ page import="org.lingcloud.molva.xmm.vam.util.VAMConstants"%>

<%@ page import="org.lingcloud.molva.xmm.util.XMMUtil"%>
<%@ page import="org.lingcloud.molva.portal.util.AccessControl" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page isELIgnored="false"%>
<%	response.setHeader("Pragma","No-Cache");
	response.setHeader("Cache-Control","No-Cache");
	response.setDateHeader("Expires", 0);
	String basePath = request.getParameter("basePath");
	String virid = request.getParameter("virid");
	if (basePath == null || "".equals(basePath)) {
		String path = request.getContextPath();
		basePath = request.getScheme() + "://"
				+ request.getServerName() + ":"
				+ request.getServerPort() + path + "/";
	}
	if (virid == null || "".equals(virid)) {
		out.println(XMMPortalUtil
				.getMessage("org.lingcloud.molva.xmm.cluster.select"));
		return;
	}
	String error = null;
	XMMClient vxc = null;
	VirtualCluster tmvc = null;
	HashMap<String, String> nodeIdAndType = null;
	String tenantName = null;
	String refresh = request.getParameter("refresh");
	List<Node> nodelist = new ArrayList<Node>();
	Date effectTime = null, expiredTime = null;
	vxc = XMMPortalUtil.getXMMClient();
	try {
		if (refresh != null && !"".equals(virid)) {
			vxc.refreshVirtualCluster(virid);
		}
		tmvc = vxc.viewVirtualCluster(virid);
		if (tmvc == null) {
			out.println(XMMPortalUtil
					.getMessage("org.lingcloud.molva.xmm.cluster.notExist"));
			return;
		}
		effectTime = tmvc.getEffectiveTime();
		expiredTime = tmvc.getExpireTime();
		nodeIdAndType = tmvc.getNodeIdsAndTypes();
		if (nodeIdAndType != null && !nodeIdAndType.isEmpty()) {
			if (nodeIdAndType
					.containsValue(VirtualNode.class.getName())) {
				List<VirtualNode> vnodelist = vxc
						.listVirtualNodeInVirtualCluster(tmvc.getGuid());
				if (vnodelist != null && vnodelist.size() > 0) {
					nodelist.addAll(vnodelist);
				}
			} else if (nodeIdAndType.containsValue(PhysicalNode.class
					.getName())) {
				List<PhysicalNode> pnodelist = vxc
						.listPhysicalNodeInVirtualCluster(tmvc
								.getGuid());
				if (pnodelist != null && pnodelist.size() > 0) {
					nodelist.addAll(pnodelist);
				}
			}
		}
	} catch (Exception e) {
		error = e.toString();
		out.println(error);
		return;
	}
%>
<div id="<%=tmvc.getName()%>_info">
<table border="0" cellspacing=1 width="100%">
	<tbody>
		<tr class="actionlog_title">
			<th width="25" valign="middle"><bean:message
				key="org.lingcloud.molva.xmm.virtualCluster.operation" /></th>
			<th width="200" valign="middle"><bean:message
				key="org.lingcloud.molva.xmm.virtualCluster.info" /></th>
			<th width="100" valign="middle"><bean:message
				key="org.lingcloud.molva.xmm.machine.info" /></th>
			<th width="225" valign="middle"><bean:message
				key="org.lingcloud.molva.xmm.lease.info" /></th>
		</tr>
		<tr class="actionlog">
			<td align="center">
			<a
				title="<bean:message key="org.lingcloud.molva.xmm.refresh"/>"
				href="#" onclick="javascript:showVirtualClusterInfo('<%=basePath%>','<%=virid%>', 'refresh');"><img
				src="<%=basePath%>/images/refresh.png" style="border: medium none;"
				width="16" height="16" /></a><br />
			<a
				title="<bean:message key="org.lingcloud.molva.xmm.virtualCluster.start" />"
				href="javascript:directStartCluster('<%=basePath%>','<%=tmvc.getGuid()%>');"><img
				src="<%=basePath%>/images/vcstart.png" style="border: medium none;"
				width="16" height="16" /></a><br />
			<a
				title="<bean:message key="org.lingcloud.molva.xmm.virtualCluster.stop" />"
				href="javascript:directStopCluster('<%=basePath%>','<%=tmvc.getGuid()%>');"><img
				src="<%=basePath%>/images/vcstop.png" style="border: medium none;"
				width="16" height="16" /></a><br />
			<a
				title="<bean:message key="org.lingcloud.molva.xmm.virtualCluster.destroy" />"
				href="javascript:directFreeCluster('<%=basePath%>','<%=tmvc.getGuid()%>');"><img
				src="<%=basePath%>/images/vcdelete.png" style="border: medium none;"
				width="16" height="16" /></a></td>
			<td><img src="<%=basePath%>/images/cluster.png"
				style="border: medium none;" width="16" height="16" /> <b><%=tmvc.getName()%></b>
			| <font color="green"> <bean:message
				key="org.lingcloud.molva.xmm.vc.acluster" /> </font> <br />
			<b>GUID</b>: <%=tmvc.getGuid()%> <br />
			<b><bean:message key="org.lingcloud.molva.xmm.lease.status" /></b>: <font
				color="red"><%=tmvc.getLifecycleState().toString()%></font> <%
 	if (LeaseLifeCycleState.EFFECTIVE != tmvc.getLifecycleState()) {
 		String msg = tmvc.getLastErrorMessage();
 		if (msg == null || "".equals(msg))
 			msg = XMMPortalUtil
 					.getMessage("org.lingcloud.molva.xmm.cluster.nostatus");
 %> <a
				title="<bean:message key="org.lingcloud.molva.xmm.machine.status.tips" />"
				href="javascript:void(0)" onclick="showWrongStatus('<%=msg%>')"><img
				src="<%=basePath%>images/lock.png" /></a> <%
 	}
 %> <br />
			<b><bean:message key="org.lingcloud.molva.xmm.acl" /></b> : <%=tmvc.getAcl()%>
			<br />
			<b><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.description" /></b> : <%=tmvc.getDescription()%>
			</td>
			<td>
			<%
				if (nodelist != null && nodelist.size() > 0) {
			%> <b><bean:message key="org.lingcloud.molva.xmm.partition.allnodenum" /></b>:
			<%=nodelist.size()%> <br />
			<%
				int normalNum = nodelist.size(), errorNum = 0;
					for (int m = 0; m < nodelist.size(); m++) {
						Node tmnode = nodelist.get(m);
						if (XMMConstants.MachineRunningState.ERROR.toString()
								.equals(tmnode.getRunningStatus())) {
							errorNum++;
							normalNum--;
						}
					}
			%> <b><bean:message key="org.lingcloud.molva.xmm.node.normal.num" /></b>: <%=normalNum%>
			<br />
			<b><bean:message key="org.lingcloud.molva.xmm.node.error.num" /></b>: <%=errorNum%>
			<br />
			<%
				} else {
			%> <b><bean:message key="org.lingcloud.molva.xmm.partition.allnodenum" /></b>:--
			<br />
			<b><bean:message key="org.lingcloud.molva.xmm.node.normal.num" /></b>:-- <br />
			<b><bean:message key="org.lingcloud.molva.xmm.node.error.num" /></b>:-- <br />
			<%
				}
			%>
			</td>
			<td>
			<%
				String tenantId = tmvc.getTenantId();
				if (!(tenantId == null || "".equals(tenantId))) {
					String shPathGetUname = AccessControl.getshPathGetUname();
					String cmd = shPathGetUname + " " + tenantId;
					String result =  XMMUtil.runCommand(cmd);
					int length = result.length();
					int sublength = System.getProperty("line.separator").length();
					tenantName = result.substring(0, length - sublength);
				} else {
					tenantName = null;
				}
				if (tenantName == null || "".equals(tenantName)) {
			%> <b><bean:message key="org.lingcloud.molva.xmm.lease.tenant" /></b>:-- <br />
			<%
				} else {
			%> <b><bean:message key="org.lingcloud.molva.xmm.lease.tenant" /></b>: <%=tenantName%>
			<br />
			<%
				}
			%> <%
 	if (effectTime == null) {
 %> <b><bean:message key="org.lingcloud.molva.xmm.lease.effective.time" /></b>:--
			<br />
			<%
				} else {
			%> <b><bean:message key="org.lingcloud.molva.xmm.lease.effective.time" /></b>:
			<%=effectTime.toString()%> <br />
			<%
				}
			%> <%
 	if (expiredTime == null) {
 %> <b><bean:message key="org.lingcloud.molva.xmm.lease.expired.time" /></b>:--
			<br />
			<%
				} else {
			%> <b><bean:message key="org.lingcloud.molva.xmm.lease.expired.time" /></b>:
			<%=expiredTime.toString()%> <br />
			<%
				}
			%>
			</td>
		</tr>
		<input id="vcNodeNum4Trick" type="hidden" value="<%=nodelist.size()%>"></input>
		<tr class="actionlog_title">
			<th valign="middle" colspan="4"><bean:message
				key="org.lingcloud.molva.xmm.virtualCluster.allnodes" /></th>
		</tr>
		<%
			if (nodelist != null && nodelist.size() > 0) {
		%>
		<tr class="actionlog_title">
			<td colspan="4">
			<table border="0" cellspacing=1 width="100%">
				<tbody>
					<tr class="actionlog_title">
						<th width="25" valign="middle"><bean:message
							key="org.lingcloud.molva.xmm.virtualCluster.operation" /></th>
						<th width="300" valign="middle"><bean:message
							key="org.lingcloud.molva.xmm.machine.info" /></th>
						<th width="100" valign="middle"><bean:message
							key="org.lingcloud.molva.xmm.machine.deploy.info" /></th>
						<th width="80" valign="middle"><bean:message
							key="org.lingcloud.molva.xmm.machine.cpuinfo" /></th>
						<th width="70" valign="middle"><bean:message
							key="org.lingcloud.molva.xmm.machine.meminfo" /></th>
						<th width="100" valign="middle"><bean:message
							key="org.lingcloud.molva.xmm.virtualAppliance.accessway" /></th>
					</tr>
					<%
						for (int k = 0; k < nodelist.size(); k++) {
								Node node = nodelist.get(k);
								String mimg = basePath + "/images/node.png";
								String publicIpStr = "";
								if (node.isHeadNode()) {
									mimg = basePath + "images/headnode.png";
								}
								if (node.getPublicIps() != null
										&& node.getPublicIps().length > 0) {
									publicIpStr = "/" + node.getPublicIps()[0];
								} else {
									publicIpStr = "";
								}
					%>
					<!--  div id="<%=node.getGuid()%>_info">-->
					<tr class="actionlog" id="vcNodeRrd<%=k + 1%>">
						<td align="center" width="25">
						
							<a
								title="<bean:message key="org.lingcloud.molva.xmm.node.operate.refresh"/>"
								href="javascript:showVirtualClusterInfo('<%=basePath%>','<%=virid%>', 'refresh');"><img
								src="<%=basePath%>/images/refresh.png" style="border: medium none;"
								width="16" height="16" /></a><br />
							<a
								title="<bean:message key="org.lingcloud.molva.xmm.node.operate.start"/>"
								href="javascript:showDialogForOperateVirtualNode('<%=basePath%>','start','<%=node.getGuid()%>');"><img
								src="<%=basePath%>/images/vcstart.png" style="border: medium none;"
								width="16" height="16" /></a><br />
							<a
								title="<bean:message key="org.lingcloud.molva.xmm.node.operate.stop"/>"
								href="javascript:showDialogForOperateVirtualNode('<%=basePath%>','stop','<%=node.getGuid()%>');"><img
								src="<%=basePath%>/images/vcstop.png" style="border: medium none;"
								width="16" height="16" /></a><br />
							<!--
							<a
								title="<bean:message key="org.lingcloud.molva.xmm.node.operate.destroy"/>"
								href="javascript:showDialogForOperateVirtualNode('<%=basePath%>','destroy','<%=node.getGuid()%>');"><img
								src="<%=basePath%>/images/vcdelete.png" style="border: medium none;"
								width="16" height="16" /></a><br />
							-->
							<a
								title="<bean:message key="org.lingcloud.molva.xmm.node.operate.migrate"/>"
								href="javascript:showDialogForOperateVirtualNode('<%=basePath%>','migrate','<%=node.getGuid()%>');"><img
								src="<%=basePath%>/images/migrate.png" style="border: medium none;"
								width="16" height="16" /></a><br />
					</td>
							
						<td width="200"><img src="<%=mimg%>"
							style="border: medium none;" width="16" height="16" /> <%=node.getName()%>
						| <font color="green"> <%
 	if (node.isHeadNode()) {
 %> <bean:message key="org.lingcloud.molva.xmm.vc.headNode" /> <%
 	} else {
 %> <bean:message key="org.lingcloud.molva.xmm.vc.slaveNode" /> <%
 	}
 %> </font> <br />
						<b><bean:message key="org.lingcloud.molva.xmm.running.status" /></b>: <font
							color="red"><%=node.getRunningStatus()%></font> <%
 	if (!XMMConstants.MachineRunningState.RUNNING
 					.toString().equals(node.getRunningStatus())) {
 				String msg = node.getLastErrorMessage();
 				if (msg == null || "".equals(msg))
 					msg = XMMPortalUtil
 							.getMessage("org.lingcloud.molva.xmm.cluster.nostatus");
 %> <a
							title="<bean:message key="org.lingcloud.molva.xmm.machine.status.tips" />"
							href="javascript:void(0)" onclick="showWrongStatus('<%=msg%>')"><img
							src="<%=basePath%>images/lock.png" /></a> <%
 	}
 %> <br />
						<b><bean:message key="org.lingcloud.molva.xmm.lease.status" /></b>: <font
							color="red"><%=node.getAssetState()%></font> <br />
						<b><bean:message key="org.lingcloud.molva.xmm.virtualNetwork.ip" /></b>:<%=node.getName()%>
						<%=publicIpStr%> <br />
						<b><bean:message key="org.lingcloud.molva.xmm.acl" /></b> : <%=node.getAcl()%>
						</td>
						<td width="100">
						<%
							String nodetype = XMMConstants.VIRTUAL_NODE_TYPE;
									if (node instanceof VirtualNode) {
						%> <b><bean:message key="org.lingcloud.molva.xmm.vm" /></b> <br>
						<b><bean:message key="org.lingcloud.molva.virtualnode.deploy.host" /></b> :
						<%=((VirtualNode) node)
								.getParentPhysialNodeName()%><br />
						<b><bean:message key="org.lingcloud.molva.xmm.virtualAppliance" /></b> :
						<a
							href="javascript:showApplianceInfo('<%=basePath%>','<%=((VirtualNode) node).getApplianceId()%>','<%=((VirtualNode) node).getApplianceName()%>');"><%=((VirtualNode) node).getApplianceName()%></a>
						<%
							} else if (node instanceof PhysicalNode) {
										nodetype = XMMConstants.PHYSICAL_NODE_TYPE;
						%> <b><bean:message key="org.lingcloud.molva.xmm.pm" /></b> <%
 	}
 %>
						</td>
						<td width="100"><b><bean:message
							key="org.lingcloud.molva.xmm.virtualAppliance.cpuamount" /></b>: <%=node.getCpuNum()%>
						<br />
						</td>
						<td width="100"><b><bean:message
							key="org.lingcloud.molva.xmm.virtualAppliance.memsize" /></b>: <%=node.getMemorySize()%>
						<br />
						</td>
						<td>
						<%
							List<String> accessWay = node.getAccessWay();
									if (accessWay == null || accessWay.size() <= 0) {
										if (!node.isHeadNode()) {
						%> <bean:message key="org.lingcloud.molva.xmm.node.accessway.empty" /> <br />
						<%
							} else {
						%> <%=VAMConstants.VA_ACCESS_WAY_VNC%> :<a
							title="<bean:message key="org.lingcloud.molva.xmm.physicalNode.vncConnection" />"
							href="JavaScript:showVNC('<%=basePath%>','<%=node.getGuid()%>','<%=nodetype%>');">
						<img src="<%=basePath%>/images/vnc.png"
							style="border: medium none;" width="16" height="16" /> </a> <%
 	}
 			} else {
 				for (int a = 0; a < accessWay.size(); a++) {
 					String acw = accessWay.get(a);
 					if (acw.equals(VAMConstants.VA_ACCESS_WAY_SSH)) {
 %> <%=VAMConstants.VA_ACCESS_WAY_SSH%> :<a
							title="<bean:message key="org.lingcloud.molva.xmm.physicalNode.sshConnection" />"
							href="JavaScript:showSSH('<%=basePath%>','<%=node.getGuid()%>','<%=nodetype%>');">
						<img src="<%=basePath%>/images/ssh.png"
							style="border: medium none;" width="16" height="16" /> </a> <br />
						<%
							} else if (acw
													.equals(VAMConstants.VA_ACCESS_WAY_VNC)) {
						%> <%=VAMConstants.VA_ACCESS_WAY_VNC%> :<a
							title="<bean:message key="org.lingcloud.molva.xmm.physicalNode.vncConnection" />"
							href="JavaScript:showVNC('<%=basePath%>','<%=node.getGuid()%>','<%=nodetype%>');">
						<img src="<%=basePath%>/images/vnc.png"
							style="border: medium none;" width="16" height="16" /> </a> <br />
						<%
							} else if (acw
													.equals(VAMConstants.VA_ACCESS_WAY_RDP)) {
						%> <%=VAMConstants.VA_ACCESS_WAY_RDP%> :<a
							title="<bean:message key="org.lingcloud.molva.xmm.physicalNode.rdpConnection" />"
							href="JavaScript:showRDP('<%=basePath%>','<%=node.getGuid()%>','<%=nodetype%>');">
						<img src="<%=basePath%>/images/rdp.png"
							style="border: medium none;" width="16" height="16" /> </a> <br />
						<%
							} else {
						%> <bean:message key="org.lingcloud.molva.xmm.node.accessway.unsupport" />
						<%
							}
										}
									}
						%>
						</td>
					</tr>
					<!--  </div>-->
					<%
						}
					%>
					<tr>
						<td colspan="5">
						<div id="nodeDetailsDiv4vc"></div>
						</td>
					</tr>
				</tbody>
			</table>
			</td>
		</tr>
		<%
			} else {
		%>
		<tr class="actionlog_title">
			<th valign="middle" colspan="4"><bean:message
				key="org.lingcloud.molva.xmm.virtualcluster.node.empty" /></th>
		</tr>
		<%
			}
		%>
	</tbody>
</table>
</div>

