<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<br /><br />
<b><font size="3px"><bean:message key="org.lingcloud.molva.xmm.mgmtMyConsole" /></font></b> <br />
<br />
<ul>
	<li><a href="<%=basePath%>JSP/ViewVirtualCluster.jsp"><bean:message
		key="org.lingcloud.molva.xmm.mgmtMyCluster" /></a> <br />
	</li>
	<br />
	<li><a href="<%=basePath%>JSP/ViewMakeVirtualAppliance.jsp"><bean:message
		key="org.lingcloud.molva.xmm.mgmtMyAppliance" /></a> <br />
	</li>
	<br />
</ul>
<br />
<% if (loc.getLanguage().equals("zh")) { %>
<b><font size="3px">关于凌云</font></b>
<br /><br />
LingCloud
<br />
版本：<%=version%>
<br />
授权：Apache License 2.0
<br /><br />
中国科学院计算技术研究所
<br />
分布式与云计算研究团队
<br />
网站：<a href="http://lingcloud.org">http://lingcloud.org</a>
<br />
邮箱：<a href="mailto:support@lingcloud.org">support@lingcloud.org</a>
<% } else { %>
<b><font size="3px">About</font></b>
<br /><br />
LingCloud
<br />
Version: <%=version%>
<br />
License: Apache License 2.0
<br /><br />
LingCloud Team, ICT, CAS,
<br />
Beijing, China
<br />
Website: <a href="http://lingcloud.org">http://lingcloud.org</a>
<br />
Email: <a href="mailto:support@lingcloud.org">support@lingcloud.org</a>
<% } %>