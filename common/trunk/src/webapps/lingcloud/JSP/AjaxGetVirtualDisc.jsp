<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ page import="org.lingcloud.molva.xmm.vam.util.*"%><%@ page import="org.lingcloud.molva.xmm.vam.pojos.*"%><%@ page import="org.lingcloud.molva.xmm.vam.services.*"%><%
	// Notice can't format this file.
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	response.setHeader("Pragma","No-Cache");
	response.setHeader("Cache-Control","No-Cache");
	response.setDateHeader("Expires", 0);
	VirtualApplianceManager vam = null;
	VADisk disc = null;
	String guid = request.getParameter("guid");
	String result = "";
	if (guid != null) {
		vam = VAMUtil.getVAManager();
		try {
			VAFile file = vam.queryFile(guid);
			disc = new VADisk(file);

			//Json类
			Json json = new Json();
			json.reSet();
			json.setSuccess(true);

			String apps = "";
			List<String> appl = disc.getApplications();
			if (appl != null && appl.size() > 0) {
				int appSize = appl.size();
				apps = appl.get(0);
				for (int j = 1; j < appSize; j++) {
					apps += "|" + appl.get(j);
				}
			}

			json.addItem("name", disc.getId());
			json.addItem("guid", disc.getGuid());
			json.addItem("os", disc.getOs());
			json.addItem("osversion", disc.getOsVersion());
			json.addItem("app", apps);
			json.addItem("diskType", disc.getDiskType());
			json.addItem("format", disc.getFormat());
			json.addItemOk();

			result = json.toString();

		} catch (Exception e) {
			String error = e.toString();
			response.sendRedirect(basePath + "JSP/error.jsp?error="
					+ error);
		}

	} else {

	}
	out.println(result);
%>
