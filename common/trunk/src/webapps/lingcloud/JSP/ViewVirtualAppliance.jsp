<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%>
<%@ page import="org.lingcloud.molva.xmm.vam.util.*"%>
<%@ page import="org.lingcloud.molva.xmm.vam.pojos.*"%>
<%@ page import="org.lingcloud.molva.xmm.vam.services.*"%>
<%@ page import="org.apache.struts.Globals"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page isELIgnored="false"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	String highlight = "appliance";
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
	VirtualApplianceManager vam = null;
	List<VirtualAppliance> val = null;
	List<VACategory> vacl = null;
	Map<String, String> category = null;
	String vaDir = null;
	String cate = request.getParameter("cate");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<!-- InstanceBeginEditable name="doctitle" -->
<title><bean:message key="org.lingcloud.molva.xmm.virtualAppliance.manage" />&nbsp;- <bean:message key="org.lingcloud.molva.lingcloud" /></title>
<!-- InstanceEndEditable -->
<!-- InstanceBeginEditable name="head" -->
<link rel="shortcut icon" href="<%=basePath%>images/icon.png"
	type="image/x-icon" />
<link type="text/css" rel="stylesheet"
	href="<%=basePath%>css/lingcloudTab.css"></link>
<link href="<%=basePath%>css/style.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/style-vam.css" rel="stylesheet"
	type="text/css" />
<link href="<%=basePath%>css/jquery.alerts.css" rel="stylesheet"
	type="text/css" media="screen" />
<script src="<%=basePath%>js/jquery.js"></script>
<script src="<%=basePath%>js/jquery.alerts.js"></script>
<script src="<%=basePath%>js/jquery.ui.draggable.js"></script>
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
<script src="<%=basePath%>js/lingcloudvam.js"></script>
<script type="text/javascript" src="<%=basePath%>js/lingcloudTab.js"></script>
<script type="text/javascript">
var fileType='disk';
var title=lingcloud.upload.disk;
</script>
<%@ include file="UploadFile.jsp"%>
<!-- InstanceEndEditable -->
</head>

<body onload="applianceManagementTab('<%=basePath%>')">
<!-- container -->
<div id="container"><!-- header -->
<%@ include file="BannerAndMenu.jsp" %>
<!--end header --> <!-- main -->
<div id="main">
<div id="middletext"><!-- InstanceBeginEditable name="EditRegion4" -->
<table border="0">
	<tr>
		<td><img src="<%=basePath%>images/virtualAppliance.png" /></td>
		<td width="20px">&nbsp;</td>
		<td width="150px" align="center"><a
			href="<%=basePath%>JSP/ViewVirtualAppliance.jsp"><img
			src="<%=basePath%>images/appliance.png" align="center" />
		<h3><bean:message key="org.lingcloud.molva.xmm.virtualAppliance.manage" />
		</h3>
		</a></td>
		<td width="150px" align="center"><a
			href="<%=basePath%>JSP/ViewMakeVirtualAppliance.jsp"><img
			src="<%=basePath%>images/make.png" align="center" />
		<h3><bean:message key="org.lingcloud.molva.xmm.virtualAppliance.make" />
		</h3>
		</a></td>
		<td width="150px" align="center"><a
			href="<%=basePath%>JSP/ViewVirtualDisc.jsp"><img
			src="<%=basePath%>images/cd.png" align="center" />
		<h3><bean:message
			key="org.lingcloud.molva.xmm.virtualAppliance.vcd.show" /></h3>
		</a></td>
		<td width="150px" align="center"><a
			href="<%=basePath%>JSP/VirtualAppMgnt.jsp"><img
			src="<%=basePath%>images/file.png"
			align="center" />
		<h3><bean:message
			key="org.lingcloud.molva.xmm.virtualAppliance.application" /></h3>
		</a></td>
		<tr>
			<tr>
				<td width="700px" colspan="8">
				<p><font color="red"><html:errors /></font></p>
				</td>
			</tr>
			<tr>

			</tr>
</table>
<div class="bugop">
<div id="applianceTab"></div>
<div id="applianceContent"></div>
</div>
<!-- InstanceEndEditable --></div>
</div>
<!-- end main --> <!-- footer -->
<%@ include file="Foot.jsp" %>
<!-- end footer -->

<!-- end container -->
</body>
<!-- InstanceEnd -->
</html>
