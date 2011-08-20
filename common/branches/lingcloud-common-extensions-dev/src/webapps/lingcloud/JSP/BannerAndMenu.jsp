<!-- container -->
<script type="text/javascript" src="<%=basePath%>js/locale.js"></script>
<%
	if (loc == null) {
		loc = new Locale("zh", "CN");
		request.getSession().setAttribute(Globals.LOCALE_KEY, loc);
	}
%>
<div id="logo"><a href="#"> <img
	src="<%=basePath%>images/logo.png" alt="LingCloud" /></a></div>
 
<div id="menu">

<ul>
	<li><a href="<%=basePath%>index.jsp"<%if (highlight == "index") {%> class="current"<%}%> ><bean:message
		key="org.lingcloud.molva.xmm.portal.introduction" /></a></li>
	<li><a href="<%=basePath%>JSP/ViewVirtualCluster.jsp"<%if (highlight == "cluster") {%> class="current"<%}%>><bean:message
		key="org.lingcloud.molva.xmm.portal.virtualCluster" /></a></li>
	<li><a href="<%=basePath%>JSP/ViewVirtualAppliance.jsp"<%if (highlight == "appliance") {%> class="current"<%}%>><bean:message
		key="org.lingcloud.molva.xmm.portal.virtualAppliance" /></a></li>
	<li><a href="<%=basePath%>JSP/ViewMonitor.jsp"<%if (highlight == "monitor") {%> class="current"<%}%>><bean:message
		key="org.lingcloud.molva.xmm.portal.systemMonitor" /></a></li>
	<select style="float:right"
		onchange="changeLocale('<%=basePath%>', this.selectedIndex);">
		<%
			if (loc.getLanguage().equals("zh")) {
		%>
		<option value="zh" selected><bean:message
		key="org.lingcloud.molva.chinese" /></option>
		<option value="en">English</option>
		<%
			} else {
		%>
		<option value="zh"><bean:message
		key="org.lingcloud.molva.chinese" /></option>
		<option value="en" selected>English</option>
		<%
			}
		%>
	</select>
</ul>

</div>