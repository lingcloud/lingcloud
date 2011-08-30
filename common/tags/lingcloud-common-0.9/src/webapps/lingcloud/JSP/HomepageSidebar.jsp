<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% if (loc.getLanguage().equals("zh")) { %>
<h3><b>关于</b></h3>
LingCloud
<br />
版本：<%=version%>
<br />
中国科学院计算技术研究所
<br />
分布式与云计算研究团队
<% } else { %>
<h3><b>About</b></h3>
LingCloud
<br />
Version: <%=version%>
<br />
LingCloud Team, ICT, CAS,
<br />
Beijing, China
<% } %>