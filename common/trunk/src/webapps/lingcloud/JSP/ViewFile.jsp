<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%>
<%@ page import="org.lingcloud.molva.vam.util.*"%>
<%@ page import="org.lingcloud.molva.vam.pojos.*"%>
<%@ page import="org.lingcloud.molva.vam.services.*"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page isELIgnored="false"%>
<%@ page import="org.lingcloud.molva.portal.util.AccessControl"%>
<%String path = request.getContextPath();
			String basePath = request.getScheme() + "://"
					+ request.getServerName() + ":" + request.getServerPort()
					+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<script type="text/javascript" language="JavaScript">
<!-- begin
	function show(id){			
		if(document.getElementById(id).style.display=='')
			document.getElementById(id).style.display='none';
		else
			document.getElementById(id).style.display='';
		}
// end-->
</script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<!-- InstanceBeginEditable name="doctitle" -->
<title>LINGCLOUD XMM</title>
<!-- InstanceEndEditable -->
<!-- InstanceBeginEditable name="head" -->
<link rel="shortcut icon" href="<%=basePath%>images/icon.png"
	type="image/x-icon" />
<link href="<%=basePath%>css/style.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/style-vam.css" rel="stylesheet"
	type="text/css" />
<link href="<%=basePath%>js/jquery.alerts.css" rel="stylesheet"
	type="text/css" media="screen" />
<script src="<%=basePath%>js/jquery.js"></script>
<script src="<%=basePath%>js/jquery.alerts.js"></script>
<script src="<%=basePath%>js/jquery.ui.draggable.js"></script>
<script src="<%=basePath%>js/lingcloudxmm.js"></script>
<script src="<%=basePath%>js/lingcloudvam.js"></script>
<%@ include file="uploadFile.jsp"%>
<!-- InstanceEndEditable -->
</head>

<body>
<%@ include file="OtherAccessControl.jsp" %>
<!-- container -->
<div id="container"><!-- header -->
<div id="logo"><a href="#"> <img
	src="<%=basePath%>images/logo.png" alt="LINGCLOUD" /></a></div>

<div id="menu">
<ul>
	<li><a href="<%=basePath%>index.jsp"><bean:message
		key="org.lingcloud.molva.xmm.portal.introduction" /></a></li>
	<li><a href="<%=basePath%>JSP/viewVirtualCluster.jsp"><bean:message
		key="org.lingcloud.molva.xmm.portal.virtualCluster" /></a></li>
	<!-- <li><a href="<%=basePath%>JSP/viewVirtualNetwork.jsp"><bean:message key="org.lingcloud.molva.xmm.portal.virtualNetwork" /></a></li> -->
	<li><a href="<%=basePath%>JSP/viewVirtualAppliance.jsp"
		class="current"><bean:message
		key="org.lingcloud.molva.xmm.portal.virtualAppliance" /></a></li>
	<li><a href="<%=basePath%>JSP/userMgmt.jsp"><bean:message
		key="org.lingcloud.molva.xmm.portal.userMgmt" /></a></li>
	<li><a href="<%=basePath%>JSP/monitor.jsp"><bean:message
		key="org.lingcloud.molva.xmm.portal.monitor" /></a></li>
</ul>
</div>
<!--end header --> <!-- main -->
<div id="main">
<div id="middletext"><!-- InstanceBeginEditable name="EditRegion4" -->
<table border="0">
	<tr>
		<td><img src="<%=basePath%>images/virtualAppliance.png" /></td>
		<td width="20px">&nbsp;</td>
		<td width="150px" align="center"><a
			href="<%=basePath%>JSP/viewVirtualAppliance.jsp"><img
			src="<%=basePath%>images/appliance.png" align="center" />
		<h3><bean:message key="org.lingcloud.molva.xmm.virtualAppliance.manage" />
		</h3>
		</a></td>
		<td width="150px" align="center"><a
			href="<%=basePath%>JSP/viewMakeVirtualAppliance.jsp"><img
			src="<%=basePath%>images/make.png" align="center" />
		<h3><bean:message key="org.lingcloud.molva.xmm.virtualAppliance.make" />
		</h3>
		</a></td>
		<td width="150px" align="center"><a
			href="<%=basePath%>JSP/viewVirtualDisc.jsp"><img
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
				<table border="0" cellspacing=1 width="900px">
					<tbody>
						<tr class="actionlog">
							<td colspan="6" valign="top"><%@ include file="ShowFile.jsp"%>
							</td>
						</tr>
					</tbody>
				</table>
			</tr>
</table>
<!-- InstanceEndEditable --></div>
</div>
<!-- end main --> <!-- footer -->
<div id="footer"></div>
<!-- end footer --></div>
<!-- end container -->
</body>
<!-- InstanceEnd -->
</html>
