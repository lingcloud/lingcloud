<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ page
	import="org.lingcloud.molva.xmm.util.*"%><%@ page
	import="org.lingcloud.molva.xmm.vam.pojos.*"%><%@ page
	import="org.lingcloud.molva.xmm.vam.services.*"%><%@ page
	import="org.lingcloud.molva.xmm.vam.util.*"%>
<%
	// Notice can't format this file.
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	response.setHeader("Pragma","No-Cache");
	response.setHeader("Cache-Control","No-Cache");
	response.setDateHeader("Expires", 0);
	VirtualApplianceManager vam = null;
	List<VirtualAppliance> val = null;
	String cate = request.getParameter("cate");
	String result = "";
	vam = VAMUtil.getVAManager();
	try {
		if (cate == null) {
			val = vam.getAllAppliance();
		} else {
			val = vam.getAppliancesByCategory(cate);
		}
		//Json类
		Json json = new Json();
		json.reSet();
		json.setSuccess(true);

		for (int i = 0; i < val.size(); i++) {
			VirtualAppliance va = val.get(i);
			json.addItem("name", va.getVAName());
			json.addItem("guid", va.getGuid());
			json.addItem("os", VAMUtil.getOperatingSystemString(va
					.getOs(), va.getOsVersion()));

			String apps = "";
			List<String> appl = va.getApplications();
			if (appl != null && appl.size() > 0) {
				int appSize = appl.size();
				apps = appl.get(0);
				for (int j = 1; j < appSize && j < 3; j++) {
					apps += ", " + appl.get(j);
				}
				if (appSize > 3) {
					apps += "...";
				}
			} else {
				apps = "nothing";
			}

			json.addItem("app", apps);
			json.addItem("capacity", VAMUtil.getCapacityString(va
					.getCapacity()));
			json.addItem("size", VAMUtil
					.getCapacityString(va.getSize()));
			json.addItem("format", va.getFormat());
			json.addItem("state", VAMUtil.getStateString(va.getState()));
			json.addItemOk();
		}
		result = json.toString();

	} catch (Exception e) {
		String error = e.toString();
		response
				.sendRedirect(basePath + "JSP/Error.jsp?error=" + error);
	}
	out.println(result);
%>
