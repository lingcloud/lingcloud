<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%>
<%@ page import="org.lingcloud.molva.xmm.vam.pojos.*"%>
<%@ page import="org.lingcloud.molva.xmm.vam.services.*"%>
<%@ page import="org.lingcloud.molva.xmm.vam.util.*"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page isELIgnored="false"%>
<%
			String path = request.getContextPath();
			String basePath = request.getScheme() + "://"
					+ request.getServerName() + ":" + request.getServerPort()
					+ path + "/";
			response.setHeader("Pragma","No-Cache");
			response.setHeader("Cache-Control","No-Cache");
			response.setDateHeader("Expires", 0);
			VirtualApplianceManager vam = null;
			VirtualAppliance va = null;
			VACategory vac = null;
			String vaDir = null;
			String guid = null;
			String apps = null;
			String langs = null;
			String accessWays = null;
			vam = VAMUtil.getVAManager();
			guid = request.getParameter("guid");
			try{
				
				va = vam.queryAppliance(guid);
				if (!va.getCategory().equals("0")) {
					vac = vam.queryCategory(va.getCategory());
				}
				
				List<String> appl = va.getApplications();
				apps = "";
				if (appl != null ) {
					int appSize = appl.size();
					apps = appl.get(0);
					for (int i=1; i<appSize; i++ ) {
						apps += ", " + appl.get(i);
					}
				}
				
				List<String> langl = va.getLanguages();
				langs="";
				if (langl != null ) {
					int langSize = langl.size();
					langs = langl.get(0);
					for (int i=1; i<langSize; i++ ) {
						langs += ", " + langl.get(i);
					}
				}
				
				List<String> accessWayList = va.getAccessWay();
				accessWays = "";
				if (accessWayList != null ) {
					int accessSize = accessWayList.size();
					accessWays = accessWayList.get(0);
					for (int i=1; i<accessSize; i++ ) {
						accessWays += ", " + accessWayList.get(i);
					}
				}
				
			} catch (Exception e){
				String error = e.toString();
				response.sendRedirect(basePath+"JSP/error.jsp?error="+error);
			}
			if (va != null) {
%>

<table class="tbInfo">
	<tr>
		<td class="tdKey"><bean:message
			key="org.lingcloud.molva.xmm.virtualAppliance.cate" /></td>
		<td class="tdValue"><%=vac != null ? vac.getCategory() : "No Category" %></td>
	</tr>
	<tr>
		<td class="tdKey"><bean:message
			key="org.lingcloud.molva.xmm.virtualAppliance.format" /></td>
		<td class="tdValue"><%=va.getFormat() %></td>
	</tr>
	<tr>
		<td class="tdKey"><bean:message
			key="org.lingcloud.molva.xmm.virtualAppliance.os" /></td>
		<td class="tdValue"><%=VAMUtil.getOperatingSystemString(va.getOs(),va.getOsVersion()) %></td>
	</tr>
	<tr>
		<td class="tdKey"><bean:message
			key="org.lingcloud.molva.xmm.virtualAppliance.app" /></td>
		<td class="tdValue"><%=apps %></td>
	</tr>
	<tr>
		<td class="tdKey"><bean:message
			key="org.lingcloud.molva.xmm.virtualAppliance.capacity" /></td>
		<td class="tdValue"><%=VAMUtil.getCapacityString(va.getCapacity()) %></td>
	</tr>
	<tr>
		<td class="tdKey"><bean:message
			key="org.lingcloud.molva.xmm.virtualAppliance.size" /></td>
		<td class="tdValue"><%=VAMUtil.getCapacityString(va.getSize()) %></td>
	</tr>
	<tr>
		<td class="tdKey"><bean:message
			key="org.lingcloud.molva.xmm.virtualAppliance.cpuamount" /></td>
		<td class="tdValue"><%=va.getCpuAmount() %></td>
	</tr>
	<tr>
		<td class="tdKey"><bean:message
			key="org.lingcloud.molva.xmm.virtualAppliance.memery" /></td>
		<td class="tdValue"><%=va.getMemory() %>MB</td>
	</tr>
	<tr>
		<td class="tdKey"><bean:message
			key="org.lingcloud.molva.xmm.virtualAppliance.state" /></td>
		<td class="tdValue"><%=VAMUtil.getStateString(va.getState()) %></td>
	</tr>
	<tr>
		<td class="tdKey"><bean:message
			key="org.lingcloud.molva.xmm.virtualAppliance.bootloader" /></td>
		<td class="tdValue"><%=va.getBootLoader() %></td>
	</tr>
	<tr>
		<td class="tdKey"><bean:message
			key="org.lingcloud.molva.xmm.virtualAppliance.accessway" /></td>
		<td class="tdValue"><%=accessWays %></td>
	</tr>
	<tr>
		<td class="tdKey"><bean:message
			key="org.lingcloud.molva.xmm.virtualAppliance.language" /></td>
		<td class="tdValue"><%=langs %></td>
	</tr>
	<tr>
		<td class="tdKey"><bean:message
			key="org.lingcloud.molva.xmm.virtualAppliance.addTime" /></td>
		<td class="tdValue"><%=VAMUtil.getTimeString(va.getAddTime()) %></td>
	</tr>
	<tr>
		<td class="tdKey"><bean:message
			key="org.lingcloud.molva.xmm.virtualAppliance.description" /></td>
		<td class="tdValue"><%=va.getDescription() %></td>
	</tr>

</table>
<% 			}%>