<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.portal.util.AccessControl"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="MonitorHeader.jsp" %>
</head>

<body onload="monitorManagementTab('<%=basePath%>');">
<%@ include file="OtherAccessControl.jsp" %>
<!-- container -->
<div id="container">
<!-- banner and menu -->
<%@ include file="BannerAndMenu.jsp" %>
<!--end header -->
<!-- main -->
<div id="main">
<div id="middletext"><!-- InstanceBeginEditable name="EditRegion4" -->

<%@ include file="MonitorMenu.jsp" %>


<div id="lingcloudMonitorShowPanel">
<div id="lingcloudMonitorTabPanel"></div>
<div id="lingcloudMonitorPagePanel"></div>
</div>


<!-- InstanceEndEditable -->
</div>
<!-- end main -->
</div>
<!-- footer -->
<%@ include file="Foot.jsp" %>
<!-- end footer -->
<!-- end container -->
</div>
</body>
<!-- InstanceEnd -->
</html>
