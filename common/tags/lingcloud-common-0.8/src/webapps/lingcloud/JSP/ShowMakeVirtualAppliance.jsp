<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<table border="0" cellspacing=1 width="100%">
	<tbody>
		<tr class="actionlog_title">
			<th width="30" align="center" id="actionlog_title"><a
				title="<bean:message key="org.lingcloud.molva.xmm.refresh"/>"
				href="<%=basePath %>JSP/ViewMakeVirtualAppliance.jsp"><img
				src="<%=basePath %>images/refresh.png" style="border: medium none;" width="16"
				height="16" /></a>&nbsp;<a
				title="<bean:message key="org.lingcloud.molva.xmm.virtualAppliance.tip4AddAppliance"/>"
				href="#"
				onclick="showDialogForMakeVirtualAppliance('<%=basePath%>');"><img
				src="<%=basePath%>images/increase.png" style="border: medium none;"
				width="16" height="16" /></a></th>
			<th width="120" valign="middle" id="actionlog_title"><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.info" /></th>
			<th width="120" valign="middle" id="actionlog_title"><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.hardware" /></th>
			<th width="80" valign="middle" id="actionlog_title"><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.operation" /> (<a
				href="#" onclick="showDialogForMakeApplianceHelp()">Help?</a>)</th>
			<th width="50" valign="middle" id="actionlog_title"><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.state" /></th>
		</tr>
		<%
			int size = val.size();
			for (int i = 0; i < size; i++) {
				VirtualAppliance va = val.get(i);
				String state = null;

				try {
					String diskGuid = va.getDisks().get(0);
					VAFile disk = vam.queryFile(diskGuid);
					if (disk.getState() == VAMConstants.STATE_ERROR) {
						state = "Error";
					} else if (disk.getState() != VAMConstants.STATE_READY) {
						state = "Preparing";
					} else {
						state = "Ready";
					}
				} catch (Exception e) {
					state = "Error";
				}

				if (state != null && state.equals("Ready")) {
					if (!names.equals("")) {
						names += "|";
					}
					names += va.getVmName();
				}
		%>
		<tr>
			<td align="center"><a
				title="<bean:message key="org.lingcloud.molva.xmm.virtualAppliance.tip4DelAppliance"/>"
				href="#"
				onclick="showDialogForDeleteAppliance('<%=basePath%>', '<%=va.getGuid()%>', '/JSP/viewMakeVirtualAppliance.jsp');"><img
				src="<%=basePath%>images/vcdelete.png" style="border: medium none;"
				width="16" height="16" /></a></td>
			<td><b><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.name" /></b>: <%=va.getVAName()%>
			<br />
			<b><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.format" /></b>: <%=va.getFormat()%>
			<br />
			<b><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.os" /></b>: <%=VAMUtil.getOperatingSystemString(va.getOs(), va
						.getOsVersion())%></td>
			<td><b><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.cpuamount" /></b>: <%=va.getCpuAmount()%>
			<br />
			<b><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.memsize" /></b>: <%=va.getMemory()%>
			MB <br />
			<b><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.disksize" /></b>: <%=VAMUtil.getCapacityString(va.getCapacity())%>
			<br />
			<b><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.currentDisc" /></b>:
			<%=discl.get(i)%></td>
			<td align="center"><a href="#"
				onclick="showDialogForShowVNC('<%=basePath%>', '<%=VAMConfig.getMakeApplianceHost()%>',  '<%=va.getVncPort() + 5900%>' );"
				title="<bean:message key="org.lingcloud.molva.xmm.virtualAppliance.showVNC" />"><img
				src="<%=basePath%>images/vnc.png" width="16px" height="16px" /></a>&nbsp;<a
				href="#"
				onclick="showDialogForOperateAppliance('<%=basePath%>', '<%=va.getGuid()%>', 'start');"
				title="<bean:message key="org.lingcloud.molva.xmm.virtualAppliance.startAppliance" />"><img
				src="<%=basePath%>images/poweron.png" /></a>&nbsp;<a href="#"
				onclick="showDialogForOperateAppliance('<%=basePath%>', '<%=va.getGuid()%>', 'stop');"
				title="<bean:message key="org.lingcloud.molva.xmm.virtualAppliance.stopAppliance" />"><img
				src="<%=basePath%>images/poweroff.png" /></a>&nbsp;<a href="#"
				onclick="showDialogForChangeDisc('<%=basePath%>', '<%=va.getGuid()%>',  '<%=discl.get(i)%>');"
				title="<bean:message key="org.lingcloud.molva.xmm.virtualAppliance.mountCD" />"><img
				src="<%=basePath%>images/cd.png" /></a>&nbsp;<a href="#"
				onclick="showDialogForSaveAppliance('<%=basePath%>', '<%=va.getGuid()%>');"
				title="<bean:message key="org.lingcloud.molva.xmm.virtualAppliance.saveAppliance" />"><img
				src="<%=basePath%>images/save.png" /></a></td>
			<td align="center" id="<%=va.getVmName()%>">
			<%
				if (state.equals("Ready")) {
			%> <img src="<%=basePath%>images/table_loading.gif" /> <%
 	} else {
 %> <%=state%> <%
 	}
 %>
			</td>
		</tr>
		<%
			}
		%>
	</tbody>
</table>
<script language="javascript">loadRunningState('<%=basePath%>', '<%=names%>');</script>