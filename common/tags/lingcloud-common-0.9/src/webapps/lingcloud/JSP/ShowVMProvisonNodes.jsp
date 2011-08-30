
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.xmm.pojos.*"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Show VM Provision Nodes page</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page"><!--
    <link rel="stylesheet" type="text/css" href="styles.css">
    -->
</head>

<body>
<table border="0" cellspacing=1 width="100%">
	<tbody>
		<tr class="actionlog_title">
			<th width="50" valign="middle"><bean:message
				key="org.lingcloud.molva.xmm.machine.type" /></th>
			<th width="300" valign="middle"><bean:message
				key="org.lingcloud.molva.xmm.machine.info" /></th>
			<th width="100" valign="middle"><bean:message
				key="org.lingcloud.molva.xmm.machine.cpuinfo" /></th>
			<th width="100" valign="middle"><bean:message
				key="org.lingcloud.molva.xmm.machine.meminfo" /></th>
			<th width="50" valign="middle"><bean:message
				key="org.lingcloud.molva.xmm.list" /></th>
		</tr>
		<%int size = hil.size();
			for (int i = 0; i < size; i++) {
				HostInfo hi = (HostInfo) hil.get(i);

				%>
		<tr class="actionlog">
			<td align="center"><img src="<%=basePath%>/images/phnode.png"
				style="border: medium none;" width="16" height="16" /></td>
			<td><%=hi.getHostName()%> | <font color="green"> This is
			dom0 </font> <br />
			<b>GUID</b>: <%=hi.getGuid()%> <br />
			<b>Private IP</b>:<a
				href="JavaScript:showVNC4Host('<%=basePath%>','<%=hi.getGuid()%>');"><%=hi.getHostName()%><img
				src="<%=basePath%>/images/vnc.png" style="border: medium none;"
				width="16" height="16" /></a> &nbsp;&nbsp;&nbsp;&nbsp; <%String pubaddress = hi.getPublicIp();
				if (pubaddress != null && !"".equals(pubaddress)) {
					%><b>Public IP</b>:<a
				href="JavaScript:showVNC4Host('<%=basePath%>','<%=hi.getGuid()%>');"><%=pubaddress%><img
				src="<%=basePath%>/images/vnc.png" style="border: medium none;"
				width="16" height="16" /></a>
			<%
				}
				%> <br />
			<b><bean:message key="org.lingcloud.molva.xmm.owner" /></b> : <%=hi.getOwnerDN()%>
			<br />
			<b><bean:message key="org.lingcloud.molva.xmm.acl" /></b> : <%=hi.getAcl()%>
			</td>
			<td><b>CPUSpeed</b>: <%=hi.getCpuSpeed()%> <br />
			<b>TotalCPU</b>: <%=hi.getTotalCpu()%> <br />
			<b>FreeCPU</b>: <%=hi.getFreeCpu()%> <br />
			<b>ProcessModal</b>: <%=hi.getProcessModal()%></td>
			<td><b>TotalMem</b>: <%=hi.getTotalMemory()%> <br />
			<b>FreeMem</b>: <%=hi.getFreeMemory()%></td>
			<td onclick="JavaScript:show('<%=hi.getHostName()%>_info')"
				style="cursor: pointer" align="center"><span><img
				src="<%=basePath%>/images/list.png" style="border: medium none;"
				width="16" height="16" /></span></td>
		</tr>
		<tr class="actionlog">
			<td id="<%=hi.getHostName()%>_info" colspan="9" style="DISPLAY: none"
				valign="middle">
			<table border="1" width="100%" align="center">
				<tbody>
					<tr>
						<td align="center"><b><bean:message
							key="org.lingcloud.molva.xmm.virtualNetwork.leases" /></b> &nbsp;&nbsp;( <b><bean:message
							key="org.lingcloud.molva.xmm.virtualNetwork.interface" /></b> ,&nbsp;&nbsp;
						<b><bean:message key="org.lingcloud.molva.xmm.virtualNetwork.mac" /></b>
						,&nbsp;&nbsp; <b><bean:message
							key="org.lingcloud.molva.xmm.virtualNetwork.ip" /></b> )</td>
					</tr>
					<tr>
						<td align="center">
						<table>
							<tbody>
								<%List<NicLease> ll = hi.getNics();
				if (ll == null) {

				%>
								<tr>
									<td>No message now! Please Wait...</td>
								</tr>
								<%} else {
												int j = 0;
					for (; j < ll.size(); j++) {
						NicLease le = ll.get(j);
						if (j % 2 == 0) {

						%>
								<tr>
									<%}%>
									<td><img src="<%=basePath%>images/nic.png" /> ( <%=le.getNicName()%>
									,&nbsp; <%=le.getMac()%> ,&nbsp; <%=le.getIp()%> )</td>
									<%if (j % 2 == 1) {

						%>
								</tr>
								<%}%>
								<%}
												// j has been added 1.
												if (j % 2 <= 1) {
						%>
								</tr>
								<%}
				}%>
							</tbody>
						</table>
						</td>
					</tr>
				</tbody>
			</table>
			</td>
		</tr>
		<%}%>
	</tbody>
</table>
</body>
</html>
