<table border="0">
	<tr>
		<td><img src="<%=basePath%>images/monitor.png" /></td>
		<td width="20px">&nbsp;</td>
		<td width="150px" align="center"><a
			href="<%=basePath%>JSP/ViewMonitor.jsp" class="current"><img
			src="<%=basePath%>images/base.png" align="center" />
		<h3><bean:message key="org.lingcloud.molva.xmm.monitor.monitorOverview" />
		</h3>
		</a></td>
		<td width="150px" align="center"><a
			href="<%=basePath%>JSP/ViewMonitorHosts.jsp"><img
			src="<%=basePath%>images/hosts.png" align="center" />
		<h3><bean:message key="org.lingcloud.molva.xmm.monitor.monitorDetail" />
		</h3>
		</a></td>
		<td width="150px" align="center"><a
			href="<%=basePath%>JSP/ViewMonitorMngt.jsp"> <img
			src="<%=basePath%>images/setting.png" align="center" />
		<h3><bean:message
			key="org.lingcloud.molva.xmm.monitor.monitorManagement" /></h3>
		</a></td>
		<td width="150px" align="center" />
	</tr>
	<tr>
		<td colspan="6">
		<p><font color="red"><html:errors /></font></p>
		<br />
		</td>
	</tr>
</table>