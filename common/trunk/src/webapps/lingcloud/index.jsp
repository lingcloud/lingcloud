<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<%@ page import="org.apache.struts.Globals"%>
<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.io.File"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="java.io.InputStreamReader"%>
<%@ page import="org.lingcloud.molva.portal.util.AccessControl"%>
<%@ page import="org.lingcloud.molva.xmm.vam.util.VAMConfig"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page isELIgnored="false"%>
<%
	String path = request.getContextPath();
	String hostPath = request.getScheme() + "://"
			+ request.getServerName() + ":80";
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	String highlight = "index";

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

	String version = "";
	BufferedReader br = new BufferedReader(
			new InputStreamReader(
					new FileInputStream(
							new File(request.getRealPath("/")+"WEB-INF/version.txt"))));
	String temp = br.readLine();
	while(temp != null){
		String[] test = temp.split("=");
		if (test.length > 1 && test[0] != null && test[1] != null) {
			version = test[1];
		}else if(test[0] != null){
			version = "";
		}
		temp = br.readLine();
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<!-- InstanceBeginEditable name="doctitle" -->
<title><bean:message key="org.lingcloud.molva.xmm.title" /></title>
<!-- InstanceEndEditable -->
<!-- InstanceBeginEditable name="head" -->
<link rel="shortcut icon" href="<%=basePath%>images/icon.png"
	type="image/x-icon" />
<link href="<%=basePath%>css/style.css" rel="stylesheet" type="text/css" />
<!-- InstanceEndEditable -->
</head>

<body>
<div id="container"><!-- header -->
<%@ include file="JSP/BannerAndMenu.jsp" %>
<!--end header --> <!-- main -->

<div id="main">
<div id="mainright">
<div id="loginbox">
	<%@ include file="JSP/IndexAccessControl.jsp" %>
</div>
<div id="sidebar">
	<%@ include file="JSP/HomepageSidebar.jsp" %>
</div>
</div>
<div id="text">
	<%@ include file="JSP/HomepageMain.jsp" %>
</div>
</div>
<!-- end main --> <!-- footer -->
<%@ include file="JSP/Foot.jsp" %>
</body>
<!-- InstanceEnd -->
</html>
