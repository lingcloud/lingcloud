<%@ page language="java" import="java.util.*, java.net.*"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.BufferedInputStream"%>
<%@ page import="java.io.IOException"%>
<%@ page import="org.apache.commons.httpclient.HttpClient"%>
<%@ page import="org.apache.commons.httpclient.HttpException"%>
<%@ page import="org.apache.commons.httpclient.HttpStatus"%>
<%@ page import="org.apache.commons.httpclient.methods.GetMethod"%>
<%@ page import="org.lingcloud.molva.portal.util.*"%>
<%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%>
<%@ page import="org.lingcloud.molva.xmm.monitor.*"%>
<%@ page import="org.lingcloud.molva.xmm.pojos.*"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
	+ request.getServerName() + ":" + request.getServerPort()
	+ path + "/";
	
	MonitorClient monitorClient = MonitorClient.getInstanse();
	String proxy = request.getParameter("proxy");
	String res = "";
	try {
		if ("statistics".equals(proxy)) {
	
			res = monitorClient.getStaticsInJson();
		}
		else if ("showTab4Patition".equals(proxy)) {
	
			res = monitorClient.getPartitionInJson();
		}
		else if ("getHostsInPartition".equals(proxy)) {
			int parId = Integer.parseInt(
					request.getParameter("parId"));
			int pageId = Integer.parseInt(
					request.getParameter("cp"));
			res = monitorClient.getHost4PartitionInJson(parId, pageId);
		}
		else if ("getMonitorSet".equals(proxy)) {
			int parId = Integer.parseInt(
					request.getParameter("parId"));
			res = monitorClient.getMonitorConfInJson(parId);
		}
		else if ("setMonitorConf".equals(proxy)) {
			int parId = Integer.parseInt(
					request.getParameter("parId"));
			String sets = request.getParameter("monitorconf");
			res = monitorClient.setMonitorConfByParName(sets, parId);
		}
		else if ("getHost4srv".equals(proxy)) {
			int parId = Integer.parseInt(
					request.getParameter("parId"));
			String srvName = request.getParameter("name");
			res = monitorClient.getHosts4Srv(srvName, parId);
		}
		else if ("getNodesByState".equals(proxy)) {
			String par = request.getParameter("parId");
			int parId = -1 ;
			try {
				parId = Integer.parseInt(par);
			}catch(Exception e) {
			}
			String state = request.getParameter("status");
			res = monitorClient.getNodesByState(state, parId);
		}
		else if ("getSrvHistoryImg".equals(proxy)) {
			String hostName = request.getParameter("host");
			String srvName = request.getParameter("srv");
			res = monitorClient.getSrvHistoryImg(hostName, srvName);
		}
	} catch(Exception e) {
		e.printStackTrace();
		res = "false";
	}
	out.println(res);
	
%>