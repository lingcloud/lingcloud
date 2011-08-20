<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path +  "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<script language="javascript"> 
var times = 4; 
clock(); 
function clock() 
{ 
   window.setTimeout('clock()',1000); 
   times = times - 1; 
   time.innerHTML = times; 
} 
</script>
<head>
<title><bean:message key="org.lingcloud.molva.xmm.title" /></title>
<link rel="shortcut icon" href="<%=basePath%>images/icon.png"
	type="image/x-icon" />
<link href="<%=basePath%>css/style.css" rel="stylesheet" type="text/css" />
<meta http-equiv="Refresh" content="3;url=<%=basePath%>index.jsp">
</head>
<body>
<div id="body">
<table style="margin-top:10%; background-color: #F3F3F3;" align="center" width="75%" border="0">
<tr>
<td align="center"><b><bean:message key="org.lingcloud.molva.portal.loginerror" /></b></td>
</tr> 
<tr> 
<td align="center"><bean:message key="org.lingcloud.molva.portal.beforeJumpsecond" />&nbsp;<span id="time">3</span>&nbsp;<bean:message key="org.lingcloud.molva.portal.afterJumpsecond" /></td>
</tr>
</table> 
</div>
</body>
</html>
