<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page import="org.lingcloud.molva.xmm.vam.util.VAMConfig"%>
<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.io.File"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="java.io.InputStreamReader"%>
<%@ page import="org.apache.struts.Globals"%>
<%
	String path = request.getContextPath();
	String hostPath = request.getScheme() + "://"
			+ request.getServerName() + ":80";
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
			

	Locale loc = (Locale) request.getSession().getAttribute(
			Globals.LOCALE_KEY);
	
	String lang = VAMConfig.getLanguageSetting();

	if (lang != null && lang.equals("zh")){
		
		if(loc == null){
			loc = new Locale("zh","CN");
			request.getSession().setAttribute(Globals.LOCALE_KEY, loc);	
		}		
	}else{
		if(loc == null){
			loc = new Locale("en","US");
			request.getSession().setAttribute(Globals.LOCALE_KEY, loc);	
		}
	}
	
	String highlight = "example";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><bean:message key="org.lingcloud.molva.extensions.example.title" bundle="example" /></title>
<link rel="shortcut icon" href="<%=basePath%>images/icon.png"
	type="image/x-icon" />
<link href="<%=basePath%>css/style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<div id="container"><!-- header -->
<%@ include file="BannerAndMenu.jsp" %>
<!--end header --> <!-- main -->

<div id="main">
<bean:message key="org.lingcloud.molva.extensions.example.messageTip" bundle="example" />
<form action="<%=basePath%>/example.do" method="post">
	<input type="hidden" name="action" value="message" />
	<table>
		<tr>
			<td><bean:message key="org.lingcloud.molva.extensions.example.message" bundle="example" />: </td>
			<td><input type="text" name="message" /></td>
			<td><input type="submit" value="<bean:message key="org.lingcloud.molva.extensions.example.submit" bundle="example" />" /></td> 
		</tr>
	</table>
</form>
<hr />
<bean:message key="org.lingcloud.molva.extensions.example.shellTip" bundle="example" />
<form action="<%=basePath%>/example.do" method="post">
	<input type="hidden" name="action" value="shell" />
	<table>
		<tr>
			<input type="submit" value="<bean:message key="org.lingcloud.molva.extensions.example.shell" bundle="example" />" /> 
		</tr>
	</table>
</form>
</div>
<!-- end main --> <!-- footer -->
<%@ include file="Foot.jsp" %>
</body>
</html>