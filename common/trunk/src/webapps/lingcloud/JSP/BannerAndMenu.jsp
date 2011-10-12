<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<!-- container -->
<script type="text/javascript" src="<%=basePath%>js/locale.js"></script>
<%
	if (loc == null) {
		loc = new Locale("zh", "CN");
		request.getSession().setAttribute(Globals.LOCALE_KEY, loc);
	}	
%>
<div id="header">
	<div id="logo"><a href="#"> <img
		src="<%=basePath%>images/logo.png" alt="LingCloud" /></a></div>

	<div id="menu">
		<div id="menuleft">
			<ul>
				<li><a href="<%=basePath%>index.jsp"<%if (highlight == "index") {%> class="current"<%}%> ><bean:message
					key="org.lingcloud.molva.xmm.portal.introduction" /></a></li>
				<li><a href="<%=basePath%>JSP/ViewVirtualCluster.jsp"<%if (highlight == "cluster") {%> class="current"<%}%>><bean:message
					key="org.lingcloud.molva.xmm.portal.virtualCluster" /></a></li>
				<li><a href="<%=basePath%>JSP/ViewVirtualAppliance.jsp"<%if (highlight == "appliance") {%> class="current"<%}%>><bean:message
					key="org.lingcloud.molva.xmm.portal.virtualAppliance" /></a></li>
				<li><a href="<%=basePath%>JSP/ViewMonitor.jsp"<%if (highlight == "monitor") {%> class="current"<%}%>><bean:message
					key="org.lingcloud.molva.xmm.portal.systemMonitor" /></a></li>
				<%
					List<String> extensionNames = XMMPortalUtil.getExtensionNames(getServletContext().getRealPath("/"));
					if (extensionNames != null ) {
						for (int i = 0; i < extensionNames.size(); i++ ) {
							String name = extensionNames.get(i);
							if (name.length() > 0 ) {
								String menuitem = XMMPortalUtil.getExtensionMessage(name, XMMPortalUtil.getExtensionMenuKey(name), loc);
								if (menuitem == null ) {
									menuitem = name;
								}
				%>
				<li><a href="<%=basePath%>JSP/<%=name%>.jsp"<%if (highlight.equals(name)) {%> class="current"<%}%>><%=menuitem%></a></li>
				<%
							}
						}
					}
				%>
			</ul>
		</div>
		<div id="menuright">
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
		</div>
	</div>
</div>
