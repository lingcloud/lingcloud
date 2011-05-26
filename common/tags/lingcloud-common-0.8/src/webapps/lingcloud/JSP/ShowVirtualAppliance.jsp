<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<div class="divCategory">
<h3 align="center"><bean:message
	key="org.lingcloud.molva.xmm.virtualAppliance.cate" />&nbsp;&nbsp;<a
	href="javascript:showDialogForCreateCategory('<%=basePath%>');"><img
	src="<%=basePath%>images/increase.png" /></a>
</h2>
<ul class="ulCategory">

	<%
		for (int i = 0; i < vacl.size(); i++) {
			VACategory vac = (VACategory) vacl.get(i);
	%>
	<li><a
		href="javascript:showDialogForDeleteCategory('<%=basePath%>', '<%=vac.getGuid()%>', '/JSP/viewVirtualAppliance.jsp');"><img
		src="<%=basePath%>images/decrease.png" /></a>&nbsp;&nbsp;<a
		href="<%=basePath%>JSP/ViewVirtualAppliance.jsp?cate=<%=vac.getGuid()%>"
		target="_top"><%=vac.getCategory()%></a></li>
	<%
		}
	%>
	<li><a href="<%=basePath%>JSP/viewVirtualAppliance.jsp"
		target="_top">All Categories</a></li>
</ul>
</div>
<div class="divApp">
<div class="divResult"><span class="spanResult"><bean:message
	key="org.lingcloud.molva.xmm.virtualAppliance.count" />: <%=val.size()%></span>
<div style="float: right; width: 20%;"><span><a
	href="javascript:showDialogForCreateAppliance('<%=basePath%>');"><img
	src="<%=basePath%>images/increase.png" align="center" /></a></span></div>
</div>
<%
	int size = val.size();
	for (int i = 0; i < size; i++) {
		VirtualAppliance va = val.get(i);
%>
<div class="divAppInfo">
<div class="divAppInfoLeft">
<div class="divTitle"><span class="spanTitle"><a
	href="javascript:showApplianceInfo('<%=basePath%>','<%=va.getGuid()%>','<%=va.getVAName()%>');"><%=va.getVAName()%></a></span>
</div>
<div class="divDsc"><%=va.getDescription()%></div>
<div class="divInfo"><bean:message
	key="org.lingcloud.molva.xmm.virtualAppliance.os" />: <%=va.getOs()%>&nbsp;&nbsp;&nbsp;&nbsp;
<%
	String apps = "";
		List<String> appl = va.getApplications();
		if (appl != null) {
			int appSize = appl.size();
			apps = appl.get(0);
			for (int j = 1; j < appSize && j < 3; j++) {
				apps += ", " + appl.get(j);
			}
			if (appSize > 3) {
				apps += "...";
			}
		}
%> <bean:message key="org.lingcloud.molva.xmm.virtualAppliance.app" />:
<%=apps%></div>
</div>
<div class="divAppInfoRight">
<ul>
	<li><a
		href="javascript:showDialogForDeleteAppliance('<%=basePath%>', '<%=va.getGuid()%>');">Delete</a></li>
	<li><a href="">DownLoad</a></li>
	<li><a
		href="javascript:showApplianceInfo('<%=basePath%>','<%=va.getGuid()%>','<%=va.getVAName()%>');">View
	More</a></li>

</ul>
</div>
</div>


<%
	}
%>
</div>
