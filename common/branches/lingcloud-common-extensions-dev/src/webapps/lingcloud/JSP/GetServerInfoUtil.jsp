<%@ page language="java"
	import="java.util.*,java.util.List,org.apache.struts.Globals,java.text.SimpleDateFormat"
	pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%>
<%@ page import="org.lingcloud.molva.xmm.pojos.Partition"%>
<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	response.setHeader("Pragma","No-Cache");
	response.setHeader("Cache-Control","No-Cache");
	response.setDateHeader("Expires", 0);
	Locale loc = null;
	int sevNum = Integer.parseInt(request.getParameter("sevNum"));
	if (sevNum == 0) {
		try {
			loc = (Locale) request.getSession().getAttribute(
					Globals.LOCALE_KEY);
			Date dt = XMMPortalUtil.getServerCurrentTime();
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy年MM月dd日HH时mm分ss秒");
			SimpleDateFormat sdf2 = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			if (loc == null || loc.getLanguage().equals("zh"))
				out.println("服务器当前时间: " + sdf.format(dt) + ";"
						+ sdf2.format(dt));
			else
				out.println("Current server time: " + sdf2.format(dt)
						+ ";" + sdf2.format(dt));
		} catch (Exception e) {
			out
					.println(XMMPortalUtil
							.getMessage("org.lingcloud.molva.xmm.cluster.getServerTimeFailed"));
		}

	}
	if (sevNum == 1) {
		//to check whether the partition name has been used
		String parName = request.getParameter("parName");
		try {
			List<Partition> parl = XMMPortalUtil.listAllPartition();
			if (parl == null) {
				out.println("no");
			} else {
				int i = 0;
				for (i = 0; i < parl.size(); i++) {
					if (parName.equals(parl.get(i).getName())) {
						out.println("no");
						break;
					}
				}
				if (i >= parl.size())
					out.println("yes");
			}
		} catch (Exception e) {
			out.println("no");
		}
	} else if (sevNum == 2) {
		int sIndex = Integer.parseInt(request.getParameter("lan"));
		if (sIndex == 0) {
			// zh						
			loc = new Locale("zh", "CN");
			request.getSession().setAttribute(Globals.LOCALE_KEY, loc);

		} else {
			// en
			loc = new Locale("en", "US");
			request.getSession().setAttribute(Globals.LOCALE_KEY, loc);
		}
		XMMPortalUtil.setRB(loc);
		out.println("ok");
	}
%>