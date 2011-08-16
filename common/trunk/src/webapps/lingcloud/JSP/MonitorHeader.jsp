<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%>
<%@ page import="org.lingcloud.molva.xmm.pojos.*"%>
<%@ page import="org.lingcloud.molva.xmm.monitor.*"%>
<%@ page import="org.lingcloud.molva.xmm.monitor.pojos.*"%>
<%@ page import="org.lingcloud.molva.xmm.vam.util.VAMConfig"%>
<%@ page import="org.apache.struts.Globals"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page isELIgnored="false"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	String highlight = "monitor";
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
%>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<!-- InstanceBeginEditable name="doctitle" -->
<title><bean:message key="org.lingcloud.molva.xmm.portal.systemMonitor" />&nbsp;- <bean:message key="org.lingcloud.molva.lingcloud" /></title>
<!-- InstanceEndEditable -->
<!-- InstanceBeginEditable name="head" -->
<link rel="shortcut icon" href="<%=basePath%>images/icon.png"
	type="image/x-icon" />
<link type="text/css" rel="stylesheet"
	href="<%=basePath%>css/lingcloudTab.css"></link>
<link href="<%=basePath%>css/style.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/style-vam.css" rel="stylesheet"
	type="text/css" />
<link href="<%=basePath%>css/lingcloudPageDiv.css" rel="stylesheet"
	type="text/css" />
<link href="<%=basePath%>css/jquery.alerts.css" rel="stylesheet"
	type="text/css" media="screen" />

<%
	if (loc == null || loc.getLanguage().equals("zh")) {
%>
<script type="text/javascript"
	src="<%=basePath%>js/lingcloud-common-zh_cn.js"></script>
<%
	} else {
%>
<script type="text/javascript"
	src="<%=basePath%>js/lingcloud-common-en_us.js"></script>
<%
	}
%>
<script src="<%=basePath%>js/lingcloudxmm.js"></script>

<script type="text/javascript" src="<%=basePath%>js/lingcloudTab.js"></script>


<!--[if lt IE 9]><script language="javascript" type="text/javascript" src="<%=basePath%>js/excanvas.min.js"></script><![endif]-->

<link rel="stylesheet" type="text/css"
	href="<%=basePath%>css/jquery.jqplot.min.css" />

<!-- BEGIN: load jquery -->
<script src="<%=basePath%>js/jquery.js"></script>
<script src="<%=basePath%>js/jquery.alerts.js"></script>
<script src="<%=basePath%>js/jquery.ui.draggable.js"></script>
<!-- END: load jquery -->

<!-- BEGIN: load jqplot -->
<script language="javascript" type="text/javascript"
	src="<%=basePath%>js/jquery.jqplot.min.js"></script>
	
<script language="javascript" type="text/javascript"
	src="<%=basePath%>js/plugins/jqplot.barRenderer.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>js/plugins/jqplot.canvasTextRenderer.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>js/plugins/jqplot.canvasAxisTickRenderer.min.js"></script>
<script language="javascript" type="text/javascript"
	src="<%=basePath%>js/plugins/jqplot.categoryAxisRenderer.min.js"></script>
<script language="javascript" type="text/javascript"
	src="<%=basePath%>js/plugins/jqplot.highlighter.min.js"></script>
<script language="javascript" type="text/javascript"
	src="<%=basePath%>js/plugins/jqplot.pointLabels.min.js"></script>
<!-- END: load jqplot -->

<%if(loc == null || loc.getLanguage().equals("zh")){ %>
<script type="text/javascript"
	src="<%=basePath%>js/lingcloud-common-zh_cn.js"></script>
<script src="<%=basePath%>js/lingcloudPageDiv_zh_cn.js"></script>
<%}else{ %>
<script type="text/javascript"
	src="<%=basePath%>js/lingcloud-common-en_us.js"></script>
<script src="<%=basePath%>js/lingcloudPageDiv_en_us.js"></script>
<%} %>

<script src="<%=basePath%>js/lingcloudMonitor.js"></script>

<!-- InstanceEndEditable -->