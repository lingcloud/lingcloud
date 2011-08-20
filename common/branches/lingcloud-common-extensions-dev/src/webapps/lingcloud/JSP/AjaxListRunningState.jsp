<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ page import="org.lingcloud.molva.xmm.vam.pojos.*"%><%@ page import="org.lingcloud.molva.xmm.vam.services.*"%><%@ page import="org.lingcloud.molva.xmm.vam.util.*"%><%
	// Notice can't format this file.
	VirtualApplianceManager vam = null;
	List stateList = null;

	String names = request.getParameter("names");
	if (names != null) {
		vam = VAMUtil.getVAManager();
		List<String> nameList = null;
		try {
			nameList = new ArrayList<String>();
			String[] nameArray = names.split("\\|");
			for (int i = 0; i < nameArray.length; i++) {
				nameList.add(nameArray[i]);
			}
			stateList = vam.getApplianceRunningState(nameList);
		} catch (Exception e) {
			//
		}
		response.setContentType("text/xml;charset=UTF-8");
		response.setHeader("Pragma","No-Cache");
		response.setHeader("Cache-Control","No-Cache");
		response.setDateHeader("Expires", 0);
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buf.append("<RunningState>");
		if (stateList != null && stateList.size() > 0) {
			for (int i = 0; i < stateList.size(); i++) {
				String state = (String) stateList.get(i);
				buf.append("<vm>");
				buf.append("<name>" + nameList.get(i) + "</name>");
				buf.append("<state>" + state + "</state>");
				buf.append("</vm>");
			}
		}
		if (!(buf.toString().indexOf("vm") > -1)) {
			buf.append("<vm>");
			buf.append("<name>-1</name>");
			buf.append("<state>No Available RunningState</state>");
			buf.append("</vm>");
		}
		buf.append("</RunningState>");
		out.write(buf.toString());
		out.flush();
	} else {
		response.setContentType("text/xml;charset=UTF-8");
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buf.append("<RunningState>");
		buf.append("<vm>");
		buf.append("<name>-1</name>");
		buf.append("<state>No Available RunningState</state>");
		buf.append("</vm>");
		buf.append("</RunningState>");
		out.write(buf.toString());
		out.flush();
	}
%>
