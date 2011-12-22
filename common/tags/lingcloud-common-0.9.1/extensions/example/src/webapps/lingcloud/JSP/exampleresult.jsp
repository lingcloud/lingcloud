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
<bean:define name="message" id="message" scope="request" type="String"></bean:define>
<bean:define name="extensionsDir" id="extensionsDir" scope="request" type="String"></bean:define>
<bean:define name="exampleDir" id="exampleDir" scope="request" type="String"></bean:define>
<bean:define name="exampleConf" id="exampleConf" scope="request" type="String"></bean:define>
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
<table>
	<tr>
		<html:errors />
	</tr>
	<tr>
		<td><bean:message key="org.lingcloud.molva.extensions.example.message" bundle="example" />: </td>
		<td><%=message.replaceAll("\r\n", "<br/>")%></td>
	</tr>
	<tr>
		<td>Extensions Dir: </td>
		<td><%=extensionsDir%></td>
	</tr>
	<tr>
		<td>Example Dir: </td>
		<td><%=exampleDir%></td>
	</tr>
	<tr>
		<td>Example Config: </td>
		<td><%=exampleConf%></td>
	</tr>
</table>

</div>
<!-- end main --> <!-- footer -->
<%@ include file="Foot.jsp" %>
</body>
</html>