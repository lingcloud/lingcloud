<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ page	import="org.lingcloud.molva.portal.util.Json"%><%@ page	import="org.lingcloud.molva.xmm.util.*"%><%@ page	import="org.lingcloud.molva.xmm.vam.pojos.*"%><%@ page	import="org.lingcloud.molva.xmm.vam.services.*"%><%@ page	import="org.lingcloud.molva.xmm.vam.util.*"%><%
	// Notice can't format this file.
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	VirtualApplianceManager vam = null;
	response.setHeader("Pragma","No-Cache");
	response.setHeader("Cache-Control","No-Cache");
	response.setDateHeader("Expires", 0);
	VirtualAppliance appliance = null;
	String guid = request.getParameter("guid");
	String result = "";
	if (guid != null) {
		vam = VAMUtil.getVAManager();
		try {
			appliance = vam.queryAppliance(guid);

			//Json类
			Json json = new Json();
			json.reSet();
			json.setSuccess(true);

			String apps = "";
			List<String> appl = appliance.getApplications();
			if (appl != null && appl.size() > 0) {
				int appSize = appl.size();
				apps = appl.get(0);
				for (int j = 1; j < appSize; j++) {
					apps += "|" + appl.get(j);
				}
			}

			String langs = "";
			List<String> langl = appliance.getLanguages();
			if (langl != null && langl.size() > 0) {
				int langSize = langl.size();
				langs = langl.get(0);
				for (int j = 1; j < langSize; j++) {
					langs += "|" + langl.get(j);
				}
			}

			String accessWay = "";
			List<String> accessl = appliance.getAccessWay();
			if (accessl != null && accessl.size() > 0) {
				int accessSize = accessl.size();
				accessWay = accessl.get(0);
				for (int j = 1; j < accessSize; j++) {
					accessWay += "|" + accessl.get(j);
				}
			}

			String loginStyle = "";
			if (appliance.getLoginStyle() == VAMConstants.LOGIN_STYLE_USER_PASS) {
				loginStyle = VAMConstants.VA_LOGIN_STYLE_USER_PASS;
			} else {
				loginStyle = VAMConstants.VA_LOGIN_STYLE_GLOBAL_USER;
			}

			json.addItem("name", appliance.getVAName());
			json.addItem("category", appliance.getCategory());
			json.addItem("guid", appliance.getGuid());
			json.addItem("os", appliance.getOs());
			json.addItem("osversion", appliance.getOsVersion());
			json.addItem("app", apps);
			json.addItem("format", appliance.getFormat());
			json.addItem("accessWay", accessWay);
			json.addItem("cpu", "" + appliance.getCpuAmount());
			json.addItem("memery", "" + appliance.getMemory());
			json.addItem("language", langs);
			json.addItem("loginStyle", loginStyle);
			json.addItem("description", appliance.getDescription());
			json.addItem("username", appliance.getUsername());
			json.addItem("password", appliance.getPassword());

			json.addItemOk();

			result = json.ToString();

		} catch (Exception e) {
			String error = e.toString();
			response.sendRedirect(basePath + "JSP/error.jsp?error="
					+ error);
		}

	} else {

	}
	out.println(result);
%>