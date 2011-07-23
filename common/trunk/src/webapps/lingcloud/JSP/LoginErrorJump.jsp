<%@ page language="java" import="java.util.*" pageEncoding="gb2312"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<script language="javascript"> 
var times=4; 
clock(); 
function clock() 
{ 
   window.setTimeout('clock()',1000); 
   times=times-1; 
   time.innerHTML =times; 
} 
</script>
<head>
<link href="<%=basePath%>css/style.css" rel="stylesheet" type="text/css" />
<meta http-equiv= "Refresh" content= "3;url=<%=basePath%>/index.jsp">
<title>LoginJumpError</title>
</head>
<body>
<div id = "body">
<table style="margin-top:10%;" align = center WIDTH=75%   BORDER=0  background=<%=basePath%>images/main.png>
<tr>
<td align = center ><bean:message key="org.lingcloud.molva.portal.loginerror" /></font></td>
</tr> 
<tr> 
<td  align = center><bean:message key="org.lingcloud.molva.portal.beforeJumpsecond" /></font> </td> 
</tr>
<tr>
<td  align = center> <div id= "time"> 3 </div> </td> 
</tr>
<tr>
<td  align = center><bean:message key="org.lingcloud.molva.portal.afterJumpsecond" /></font> </td> 
</tr>
</table> 
</div>
</body>
</html>
 


