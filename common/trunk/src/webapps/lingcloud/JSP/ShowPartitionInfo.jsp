<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%>
<%@ page import="org.lingcloud.molva.xmm.pojos.*"%>
<%@ page
	import="org.lingcloud.molva.ocl.asset.AssetConstants.AssetState"%>
<%@ page import="org.lingcloud.molva.xmm.ac.PartitionAC"%>
<%@ page import="org.lingcloud.molva.xmm.util.XMMConstants"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page isELIgnored="false"%>
<%
	String basePath = request.getParameter("basePath");
	String parid = request.getParameter("parid");
	if (basePath == null || "".equals(basePath)) {
		String path = request.getContextPath();
		basePath = request.getScheme() + "://"
				+ request.getServerName() + ":"
				+ request.getServerPort() + path + "/";
	}
	if (parid == null || "".equals(parid)) {
		return;
	}
	response.setHeader("Pragma","No-Cache");
	response.setHeader("Cache-Control","No-Cache");
	response.setDateHeader("Expires", 0);
	String error = null;
	XMMClient vxc = null;
	List<PhysicalNode> pnl = null;
	Partition par = null;
	int lnum = 0, rnum = 0, inum = 0;
	String kvalue = "";
	int leaseWay = 0;
	boolean isProvisionVM = false;
	vxc = XMMPortalUtil.getXMMClient();
	try {
		par = vxc.viewPartition(parid);
		pnl = vxc.listPhysicalNodeInPartition(parid);
		for (int l = 0; l < pnl.size(); l++) {
			PhysicalNode pn = pnl.get(l);
			if (pn.getAssetState().equals(AssetState.LEASED)) {
				lnum++;
			} else if (pn.getAssetState().equals(AssetState.IDLE)) {
				inum++;
			} else if (pn.getAssetState().equals(AssetState.RESERVED)) {
				rnum++;
			}
		}
		String ktag = par.getAttributes().get(
				PartitionAC.REQUIRED_ATTR_NODETYPE);
		if (ktag == null || "".equals(ktag)) {
			kvalue = "none";
		} else {
			if (ktag.equals(PartitionAC.VM)) {
				leaseWay = 1;
				isProvisionVM = true;
			} else if (ktag.equals(PartitionAC.GENERAL)) {
				leaseWay = 2;
			} else { //PartitionAC.STORAGE/HPC/DC
				leaseWay = 3;
			}
			kvalue = par.getAttributes().get(
					PartitionAC.ATTR_NODE_PRE_INSTALLED_SOFT);
			if (kvalue == null || kvalue.equals("")) {
				kvalue = "none";
			}
		}

	} catch (Exception e) {
		error = e.toString();
		out.println(error);
		return;
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<script type="text/javascript" language="JavaScript">
function refreshPN(parID){
	
}
	
</script>
</head>

<body>
<table border="0" cellspacing=1 width="100%">
	<tbody>
		<tr class="actionlog_title">
			<th width="50" valign="middle">
				<bean:message key="org.lingcloud.molva.xmm.virtualCluster.operation" /></th>
			<th width="200" valign="middle"><bean:message
				key="org.lingcloud.molva.xmm.partition.info" /></th>
			<th width="200" valign="middle"><bean:message
				key="org.lingcloud.molva.xmm.partition.nodestat" /></th>
			<th width="100" valign="middle"><bean:message
				key="org.lingcloud.molva.xmm.partition.default.software" /></th>
			<th width="150" valign="middle"><bean:message
				key="org.lingcloud.molva.xmm.partition.leaseway" /></th>
		</tr>
		<tr class="actionlog">
			<td onclick="JavaScript:showPartitionInfo('<%=basePath%>','<%=parid%>')"
				style="cursor: pointer" align="center"><span><img
				title="<bean:message key="org.lingcloud.molva.xmm.refresh"/>" src="<%=basePath%>/images/refresh.png"
				style="border: medium none;" width="16" height="16" /></span></td>
			<td><img src="<%=basePath%>/images/partition.png"
				style="border: medium none;" width="16" height="16" /> <b><%=par.getName()%></b>
			| <font color="green"> <bean:message
				key="org.lingcloud.molva.xmm.vc.apart" /> </font> <br />
			<b>GUID</b>: <%=par.getGuid()%> <br />
			<b><bean:message key="org.lingcloud.molva.xmm.acl" /></b> : <%=par.getAcl()%>
			<br />
			<b><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.description" /></b> : <%=par.getDescription()%>
			</td>
			<td><b><bean:message
				key="org.lingcloud.molva.xmm.partition.allnodenum" /></b>:<%=pnl.size()%><br />
			<%
				if (!isProvisionVM) {
			%> <b><bean:message
				key="org.lingcloud.molva.xmm.partition.idlenodenum" /></b>: <%=inum%> <br />
			<b><bean:message key="org.lingcloud.molva.xmm.partition.leasednodenum" /></b>:
			<%=lnum%><br />
			<b><bean:message key="org.lingcloud.molva.xmm.partition.reservednodenum" /></b>:
			<%=rnum%> <%
 	} else {
 %> <b><bean:message
				key="org.lingcloud.molva.xmm.partition.idlenodenum" /></b>: -- <br />
			<b><bean:message key="org.lingcloud.molva.xmm.partition.leasednodenum" /></b>:
			--<br />
			<b><bean:message key="org.lingcloud.molva.xmm.partition.reservednodenum" /></b>:
			-- <%
				}
			%>
			</td>
			<td align="center"><b><%if (kvalue.equals("none")) {
				%><bean:message key="org.lingcloud.molva.xmm.partition.noapp" /><%
			}else{
				%><%=kvalue%><%
			}%></b></td>
			<td align="center"><b><%
				switch(leaseWay){
				case 1:
					%>
					<bean:message key="org.lingcloud.molva.xmm.partition.leaseway1" />
					<%
					break;
				case 2:
					%>
					<bean:message key="org.lingcloud.molva.xmm.partition.leaseway2" />
					<%
					break;
				case 3:
					%>
					<bean:message key="org.lingcloud.molva.xmm.partition.leaseway3" />
					<%
					break;
				}
			%></b></td>
		</tr>
		<input id="ptNodeNum4Trick" type="hidden" value="<%=pnl.size()%>"></input>
		<tr class="actionlog_title">
			<th valign="middle" colspan="5"><bean:message
				key="org.lingcloud.molva.xmm.virtualCluster.allnodes" /></th>
		</tr>
		<tr class="actionlog_title">
			<td colspan="5">
			<table border="0" cellspacing=1 width="100%">
				<tbody>
					<tr>
						<th width="50" valign="middle"><bean:message
							key="org.lingcloud.molva.xmm.virtualCluster.operation" /></th>
						<th width="200" valign="middle"><bean:message
							key="org.lingcloud.molva.xmm.machine.info" /></th>
						<th width="150" valign="middle"><bean:message
							key="org.lingcloud.molva.xmm.machine.cpuinfo" /></th>
						<th width="110" valign="middle"><bean:message
							key="org.lingcloud.molva.xmm.machine.meminfo" /></th>
						<th width="80" valign="middle"><bean:message
							key="org.lingcloud.molva.xmm.machine.netinfo" /></th>
						<th width="110" valign="middle"><bean:message
							key="org.lingcloud.molva.xmm.virtualAppliance.accessway" /></th>
					</tr>
					<%
						int size = pnl.size();
						if (size == 0) {
					%>
					<tr>
						<td colspan="5">
						<h3><bean:message key="org.lingcloud.molva.xmm.physicalNode.empty" />
						</h3>
						</td>
					</tr>
					<%
						}
						for (int i = 0; i < size; i++) {
							PhysicalNode hi = (PhysicalNode) pnl.get(i);
					%>
					<tr class="actionlog" id="ptNodeRrd<%=i + 1%>">
						<td align="center" width="25">
							<a
							onclick="<%vxc.refreshPhysicalNode(hi.getGuid());%>;JavaScript:showPartitionInfo('<%=basePath%>','<%=parid%>')"
							style="cursor: pointer" align="center"><img
							title="<bean:message key="org.lingcloud.molva.xmm.refresh"/>"
							src="<%=basePath%>/images/refresh.png"
							style="border: medium none;" width="16" height="16" /></a><br />
							<a
							title="<bean:message key="org.lingcloud.molva.xmm.node.operate.start"/>"
							href="javascript:showDialogForOperatePhysicalNode('<%=basePath%>','start','<%=hi.getGuid()%>','0');"><img
							src="<%=basePath%>/images/poweron.png" style="border: medium none;"
							width="16" height="16" /></a><br />
							<a
							title="<bean:message key="org.lingcloud.molva.xmm.node.operate.stop"/>"
							href="javascript:showDialogForOperatePhysicalNode('<%=basePath%>','stop','<%=hi.getGuid()%>','<%=hi.getRunningVms()%>');"><img
							src="<%=basePath%>/images/poweroff.png" style="border: medium none;"
							width="16" height="16" /></a><br />
						</td>
						<td><img src="<%=basePath%>/images/phnode.png"
							style="border: medium none;" width="16" height="16" /> <%=hi.getName()%>
						| <font color="green"> <bean:message
							key="org.lingcloud.molva.xmm.vc.physicalNode" /> </font> <br />
						<b>GUID</b>: <%=hi.getGuid()%> <br />
						<b><bean:message key="org.lingcloud.molva.xmm.running.status" /></b>: <font
							color="red"><%=hi.getRunningStatus()%></font> <%
 	if (!XMMConstants.MachineRunningState.RUNNING.toString()
 				.equals(hi.getRunningStatus())) {
 			String msg = hi.getLastErrorMessage();
 			if (msg == null || "".equals(msg))
 				msg = XMMPortalUtil
 						.getMessage("org.lingcloud.molva.xmm.cluster.nostatus");
 %> <a
							title="<bean:message key="org.lingcloud.molva.xmm.machine.status.tips" />"
							href="javascript:void(0)" onclick="showWrongStatus('<%=msg%>')"><img
							src="<%=basePath%>images/lock.png"></img></a> <%
 	}
 %> <br />
						<b><bean:message key="org.lingcloud.molva.xmm.lease.status" /></b>: <font
							color="red"><%=hi.getAssetState()%></font> <br />
						<b><bean:message key="org.lingcloud.molva.xmm.virtualNetwork.ip" /></b>:<%=hi.getPrivateIps()[0]%></a> &nbsp;&nbsp;&nbsp;&nbsp; <%
 	String[] pubaddresses = hi.getPublicIps();
 		if (pubaddresses != null && pubaddresses.length > 0) {
 			String pubaddress = pubaddresses[0];
 %> /<a
							title="<bean:message key="org.lingcloud.molva.xmm.physicalNode.remoteConnection" />"
							href="JavaScript:showVNC('<%=basePath%>','<%=hi.getGuid()%>','<%=XMMConstants.PHYSICAL_NODE_TYPE%>');"><%=pubaddress%><img
							src="<%=basePath%>/images/vnc.png" style="border: medium none;"
							width="16" height="16" /></a> <%
 	}
 %> <br />
						<b><bean:message key="org.lingcloud.molva.xmm.acl" /></b> : <%=hi.getAcl()%>
						</td>
						<td><b><bean:message key="org.lingcloud.molva.xmm.physicalNode.cpu.speed" /></b>: <%=hi.getCpuSpeed()%> <br />
						<b><bean:message key="org.lingcloud.molva.xmm.physicalNode.cpu.total" /></b>: <%=hi.getCpuNum()%> <br />
						<b><bean:message key="org.lingcloud.molva.xmm.physicalNode.cpu.free" /></b>: <%=hi.getFreeCpu()%> <br />
						<b><bean:message key="org.lingcloud.molva.xmm.physicalNode.cpu.modal" /></b>: <%=hi.getCpuModal()%></td>
						<td><b><bean:message key="org.lingcloud.molva.xmm.physicalNode.mem.total" /></b>: <%=hi.getMemorySize()%> <br />
						<b><bean:message key="org.lingcloud.molva.xmm.physicalNode.mem.free" /></b>: <%=hi.getFreeMemory()%></td>
						<td
							onclick="JavaScript:showMac('<%=basePath%>', '<%=hi.getName()%>_info', '<%=hi.getName()%>_img')"
							style="cursor: pointer" align="center"><span><img
							title="<bean:message key="org.lingcloud.molva.xmm.partition.tip4UnfoldNetInfo" />"
							id="<%=hi.getName()%>_img" src="<%=basePath%>/images/allopen.png"
							style="border: medium none;" width="16" height="16" /></span></td>
						<td align="center">
						SSH:<a
							title="<bean:message key="org.lingcloud.molva.xmm.physicalNode.sshConnection" />"
							href="JavaScript:showSSH('<%=basePath%>','<%=hi.getGuid()%>','<%=hi.getType()%>');">
						<img src="<%=basePath%>/images/ssh.png"
							style="border: medium none;" width="16" height="16" /> </a> <br />
						VNC:<a
							title="<bean:message key="org.lingcloud.molva.xmm.physicalNode.vncConnection" />"
							href="JavaScript:showVNC('<%=basePath%>','<%=hi.getGuid()%>','<%=hi.getType()%>');">
						<img src="<%=basePath%>/images/vnc.png"
							style="border: medium none;" width="16" height="16" /> </a>
						</td>
					</tr>
					<tr class="actionlog">
						<td id="<%=hi.getName()%>_info" colspan="9" style="DISPLAY: none"
							valign="middle">
						<table border="1" width="100%" align="center">
							<tbody>
								<tr>
									<td align="center"><b><bean:message
										key="org.lingcloud.molva.xmm.virtualNetwork.nic.list" /></b>
									&nbsp;&nbsp;( <b><bean:message
										key="org.lingcloud.molva.xmm.virtualNetwork.interface" /></b>
									,&nbsp;&nbsp; <b><bean:message
										key="org.lingcloud.molva.xmm.virtualNetwork.mac" /></b> ,&nbsp;&nbsp; <b><bean:message
										key="org.lingcloud.molva.xmm.virtualNetwork.ip" /></b> )</td>
								</tr>
								<tr>
									<td align="center">
									<table>
										<tbody>
											<%
												List<Nic> ll = hi.getNics();
													if (ll == null || ll.size() == 0) {
											%>
											<tr>
												<td>No message now! Please Wait...</td>
											</tr>
											<%
												} else {
														int j = 0;
														for (; j < ll.size(); j++) {
															Nic le = ll.get(j);
															if (j % 2 == 0) {
											%>
											<tr>
												<%
													}
												%>
												<td><img src="<%=basePath%>images/nic.png" /> ( <%=le.getNicName()%>
												,&nbsp; <%=le.getMac()%> ,&nbsp; <%=le.getIp()%> )</td>
												<%
													if (j % 2 == 1) {
												%>
											</tr>
											<%
												}
											%>
											<%
												}
														// j has been added 1.
														if (j % 2 <= 1) {
											%>
											</tr>
											<%
												}
													}
											%>
										</tbody>
									</table>
									</td>
								</tr>
							</tbody>
						</table>
						</td>
					</tr>
					<%
						}
					%>
					<tr>
						<td colspan="5">
						<div id="nodeDetailsDiv4pt"></div>
						</td>
					</tr>
				</tbody>
			</table>
			</td>
		</tr>
	</tbody>
</table>
</body>
</html>
