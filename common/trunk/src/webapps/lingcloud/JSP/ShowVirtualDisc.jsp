<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<table border="0" cellspacing=1 width="100%">
	<tbody>
		<tr class="actionlog_title">
			<th width="50" align="center" id="actionlog_title">
			<a title="<bean:message key="org.lingcloud.molva.xmm.refresh"/>"
				href="<%=basePath %>JSP/ViewVirtualDisc.jsp"><img
				src="<%=basePath %>images/refresh.png" style="border: medium none;" width="16"
				height="16" /></a>&nbsp;
			<a	title="<bean:message key="org.lingcloud.molva.xmm.virtualAppliance.tip4AddCD"/>"
				href="#" onclick="showDialogForCreateVirtualDisc('<%=basePath%>');"><img
				src="<%=basePath%>images/increase.png" style="border: medium none;"
				width="16" height="16" /></a>&nbsp;<a
				title="<bean:message key="org.lingcloud.molva.xmm.virtualAppliance.tip4UploadCD"/>"
				href="#"
				onclick="open_ul();">
				<img
				src="<%=basePath%>images/upload.png" style="border: medium none;"
				width="16" height="16" /></a></th>
			<th width="100" valign="middle" id="actionlog_title"><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.vcd.info" /></th>
			<th width="50" valign="middle" id="actionlog_title"><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.vcd.format" /></th>
			<th width="100" valign="middle" id="actionlog_title"><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.vcd.size" /></th>
			<th width="100" valign="middle" id="actionlog_title"><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.vcd.state" /></th>
		</tr>
		<%
		int size = vafl.size();
			for (int i = 0; i < size; i++) {
				VAFile vaf = (VAFile) vafl.get(i);
				VADisk vad = new VADisk(vaf);
				%>
		<tr>

			<td align="center"><a
				title="<bean:message key="org.lingcloud.molva.xmm.virtualAppliance.tip4DelCD"/>"
				href="#"
				onclick="showDialogForDeleteVirtualDisc('<%=basePath%>', '<%=vaf.getGuid()%>');"><img
				src="<%=basePath%>images/vcdelete.png" style="border: medium none;"
				width="16" height="16" /></a>&nbsp;
			<a
				title="<bean:message key="org.lingcloud.molva.xmm.virtualAppliance.tip4UpdateCD"/>"
				href="#"
				onclick="showDialogForModifyVirtualDisc('<%=basePath%>', '<%=vaf.getGuid()%>');"><img
				src="<%=basePath%>images/edit.png" style="border: medium none;"
				width="16" height="16" /></a></td>
			<td><b><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.name" /></b>: <%=vaf.getId()%> <br />
			<b><bean:message key="org.lingcloud.molva.xmm.virtualAppliance.vcd.type" /></b>:
			<%
							String type = VAMUtil.checkDiscType(vad.getDiskType());
						 %> <%=type%> <br />
			<%
							if (type.equals(VAMConstants.VAD_DISK_TYPE_OS)) {
						 %> <b><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.vcd.os" /></b>: <%=VAMUtil.getOperatingSystemString(vad.getOs(), vad.getOsVersion())%>
			<%
							} else if (type.equals(VAMConstants.VAD_DISK_TYPE_APP)) {
						%> <b><bean:message
				key="org.lingcloud.molva.xmm.virtualAppliance.vcd.app" /></b>: <%=VAMUtil.list2String(vad.getApplications())%>
			<%
						 	}
						%>
			</td>
			<td align="center"><%=vaf.getFormat()%></td>
			<td align="center"><%=VAMUtil.getCapacityString(vaf.getSize())%>
			</td>
			<td align="center"><%=VAMUtil.getStateString(vaf.getState())%></td>
		</tr>
		<%} %>
	</tbody>
</table>
