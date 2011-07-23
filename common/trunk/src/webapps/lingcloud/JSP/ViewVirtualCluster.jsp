<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%>
<%@ page import="org.lingcloud.molva.xmm.pojos.*"%>
<%@ page import="org.lingcloud.molva.xmm.ac.PartitionAC"%>
<%@ page import="org.lingcloud.molva.ocl.lease.LeaseConstants.LeaseLifeCycleState"%>
<%@ page import="org.lingcloud.molva.xmm.util.XMMConstants"%>
<%@ page import="org.lingcloud.molva.xmm.vam.util.VAMConfig"%>
<%@ page import="org.apache.struts.Globals"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page import="org.lingcloud.molva.portal.util.AccessControl"%>
<%@ page isELIgnored="false"%>
<%String path = request.getContextPath();
			String basePath = request.getScheme() + "://"
					+ request.getServerName() + ":" + request.getServerPort()
					+ path + "/";
			
			String highlight = "cluster";
					
			Locale loc = (Locale)request.getSession().getAttribute(Globals.LOCALE_KEY);

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
			String error = request.getParameter("error");
			XMMClient vxc = null;
			List<Partition> pl = null;
			List<VirtualCluster> vclist = null;
			List<VirtualNode> vnodelist = null;
			List<PhysicalNode> pnnodelist = null;
			HashMap<String, List<VirtualCluster>> vcmap = new HashMap<String, List<VirtualCluster>>();
			HashMap<String, List<Node>> nodemap = new HashMap<String, List<Node>>();
			vxc = XMMPortalUtil.getXMMClient();
			try {
				pl = vxc.listAllPartition();
				vclist = vxc.searchVirtualCluster(null, null, null);
			 	vnodelist = vxc.searchVirtualNode(null, null, null);
				pnnodelist = vxc.searchPhysicalNode(null, null, null);
				if (vclist != null && vclist.size() > 0) {
					for (int j = 0; j < pl.size(); j++) {
						Partition par = pl.get(j);
						List<VirtualCluster> vctmp = new ArrayList<VirtualCluster>();
						for (int i = 0; i < vclist.size(); i++) {
							VirtualCluster vcluster = vclist.get(i);
							if (vcluster.getPartitionId().equals(par.getGuid())) {
								vctmp.add(vcluster);
							}
						}
						vcmap.put(par.getGuid(), vctmp);
					}

					for (int i = 0; i < vclist.size(); i++) {
						VirtualCluster vcluster = vclist.get(i);
						List<Node> nodelist = new ArrayList<Node>();
						if (vnodelist != null && vnodelist.size() > 0) {
							for (int k = 0; k < vnodelist.size(); k++) {
								VirtualNode vnode = vnodelist.get(k);
								String clusterid = vnode.getVirtualClusterID();
								if (clusterid != null && !"".equals(clusterid)) {
									if (clusterid.equals(vcluster.getGuid())) {
										nodelist.add(vnode);
									}
								}
							}
						}

						if (pnnodelist != null && pnnodelist.size() > 0) {
							for (int k = 0; k < pnnodelist.size(); k++) {
								PhysicalNode pnode = pnnodelist.get(k);
								String clusterid = pnode.getVirtualClusterID();
								if (clusterid != null && !"".equals(clusterid)) {
									if (clusterid.equals(vcluster.getGuid())) {
										nodelist.add(pnode);
									}
								}
							}
						}
						nodemap.put(vcluster.getGuid(), nodelist);
					}
				}
			} catch (Exception e) {
				error = e.toString();
			}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<script type="text/javascript" language="JavaScript">
<!-- begin
	window.onload=function showError() {
		var errormsg = "<%=request.getAttribute("errormsg")%>";
		if(errormsg != null && errormsg != "null")
			jAlert(errormsg,"ERROR");
		<%if(error !=null && !"".equals(error)) {%>
			jAlert("<%=error%>","ERROR");
		<%}%>
	}
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
<title><bean:message key="org.lingcloud.molva.xmm.portal.virtualCluster" />&nbsp;- <bean:message key="org.lingcloud.molva.lingcloud" /></title>
<!-- InstanceEndEditable -->
<!-- InstanceBeginEditable name="head" -->
<link rel="shortcut icon" href="<%=basePath%>images/icon.png"
	type="image/x-icon" />
<link href="<%=basePath%>css/style.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/style-vam.css" rel="stylesheet"
	type="text/css" />
<link href="<%=basePath%>css/lingcloudPageDiv.css" rel="stylesheet"
	type="text/css" />
<link rel="StyleSheet" href="<%=basePath%>css/dtree.css" type="text/css" />

<script type="text/javascript" src="<%=basePath%>js/dtree.js"></script>
<link href="<%=basePath%>css/jquery.alerts.css" rel="stylesheet"
	type="text/css" media="screen" />
<script src="<%=basePath%>js/jquery.js"></script>
<script src="<%=basePath%>js/jquery.alerts.js"></script>
<script src="<%=basePath%>js/jquery.ui.draggable.js"></script>
<%if(loc == null || loc.getLanguage().equals("zh")){ %>
<script type="text/javascript"
	src="<%=basePath%>js/lingcloud-common-zh_cn.js"></script>
<script src="<%=basePath%>js/lingcloudPageDiv_zh_cn.js"></script>
<%}else{ %>
<script type="text/javascript"
	src="<%=basePath%>js/lingcloud-common-en_us.js"></script>
<script src="<%=basePath%>js/lingcloudPageDiv_en_us.js"></script>
<%} %>
<script src="<%=basePath%>js/lingcloudxmm.js"></script>
<script src="<%=basePath%>js/lingcloudvam.js"></script>
<script src="<%=basePath%>js/PopupCalendarTime.js"></script>

<!-- InstanceEndEditable -->
</head>

<body>
<%@ include file="OtherAccessControl.jsp" %>
<script type="text/javascript">
		
	</script>
<!-- container -->
<div id="container"><!-- header -->
<%@ include file="BannerAndMenu.jsp" %>
<!--end header --> <!-- main -->
<div id="main">
<div id="middletext"><!-- InstanceBeginEditable name="EditRegion4" -->
<table border="0">
	<tr class="viewmenu">
		<td><img src="<%=basePath%>images/virtualCluster.png" /></td>
		<td width="20px">&nbsp;</td>
		<td width="150px" align="center"><a
			href="javascript:showDialogForAddPartition('<%=basePath%>');"> <img
			src="<%=basePath%>images/add.png" align="center" />
		<h3><bean:message key="org.lingcloud.molva.xmm.partition.create" /></h3>
		</a></td>
		<td width="185px" align="center"><a
			href="javascript:showDialogForDeletePartition('<%=basePath%>');">
		<img src="<%=basePath%>images/delete.png" align="center" />
		<h3><bean:message key="org.lingcloud.molva.xmm.partition.destroy" /></h3>
		</a></td>
		<td width="150px" align="center"><a
			href="javascript:showDialogForAddNewPNNode('<%=basePath%>');"><img
			src="<%=basePath%>images/increase.png" align="center" />
		<h3><bean:message key="org.lingcloud.molva.xmm.PNNode.add" /></h3>
		</a></td>
		<td width="150px" align="center"><a
			href="javascript:showDialogForDeletePNNode('<%=basePath%>');"><img
			src="<%=basePath%>images/decrease.png" align="center" />
		<h3><bean:message key="org.lingcloud.molva.xmm.PNNode.delete" /></h3>
		</a></td>
		<td width="150px" align="center"><a
			href="javascript:showDialogForFastCreateCluster('<%=basePath%>');"><img
			src="<%=basePath%>images/vccreate.png" align="center" />
		<h3><bean:message key="org.lingcloud.molva.xmm.virtualCluster.create" />
		</h3>
		</a></td>
		<td width="150px" align="center"><a
			href="javascript:showDialogForStartCluster('<%=basePath%>');"><img
			src="<%=basePath%>images/vcstart.png" align="center" />
		<h3><bean:message key="org.lingcloud.molva.xmm.virtualCluster.start" />
		</h3>
		</a></td>
		<td width="150px" align="center"><a
			href="javascript:showDialogForStopCluster('<%=basePath%>');"><img
			src="<%=basePath%>images/vcstop.png" align="center" />
		<h3><bean:message key="org.lingcloud.molva.xmm.virtualCluster.stop" />
		</h3>
		</a></td>
		<td width="180px" align="center"><a
			href="javascript:showDialogForFreeCluster('<%=basePath%>');"><img
			src="<%=basePath%>images/vcdelete.png" align="center" />
		<h3><bean:message key="org.lingcloud.molva.xmm.virtualCluster.destroy" />
		</h3>
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
						<tr class="actionlog_title">
							<th width="200px" align="left">&nbsp;&nbsp;&nbsp;&nbsp; <bean:message
								key="org.lingcloud.molva.xmm.virtualCluster.list" /></th>
							<th width="700px" align="left">&nbsp;&nbsp;&nbsp;&nbsp; <bean:message
								key="org.lingcloud.molva.xmm.virtualCluster.console" /></th>
						</tr>
						<tr class="actionlog_cluster">
							<td width="200px" align="left" valign="top">
							<div class="dtree">
							<p><a href="javascript: d.openAll();"><img
								src="<%=basePath%>images/allopen.png" /><bean:message
								key="org.lingcloud.molva.xmm.virtualCluster.allopen" /></a> &nbsp;|&nbsp;<a
								href="javascript: d.closeAll();"><img
								src="<%=basePath%>images/allclose.png" /><bean:message
								key="org.lingcloud.molva.xmm.virtualCluster.allclose" /></a>
							 &nbsp;|&nbsp;<a
								href="<%=basePath%>JSP/ViewVirtualCluster.jsp"><img
								src="<%=basePath%>images/refresh.png" /><bean:message
								key="org.lingcloud.molva.xmm.refresh" /></a></p>
							<%int treenodeid = 0;%> <script type="text/javascript">
								var d = new dTree('d','<%=basePath%>');
								d.add(<%=treenodeid%>,-1,'<bean:message key="org.lingcloud.molva.xmm.elastic.infrasset" />','','', '','','','','');
								<%if(pl==null || pl.size() ==0 ){
										treenodeid++;
										String msg;
										//msg = "No Asset Now";
										msg = XMMPortalUtil.getMessage("org.lingcloud.molva.xmm.noAsset");
										%>											
										d.add(<%=treenodeid%>,0,'<%=msg%>','','', '','<%=basePath%>images/partition.png','<%=basePath%>images/partition.png','','');
										<%
									}else {
										for(int k=0; k < pl.size(); k++){
											Partition tmp = pl.get(k);
											%>
											d.add('<%=tmp.getGuid()%>',0,'<%=tmp.getName()%>','javascript:showPartitionInfo(\'<%=basePath%>\',\'<%=tmp.getGuid()%>\');','', '','<%=basePath%>images/partition.png','<%=basePath%>images/partition.png','','asset_info_div');
											<%
											List<VirtualCluster> vcl = null;
											try {
												vcl = vcmap.get(tmp.getGuid());
												if(vcl!=null && vcl.size()>0){
												for(int m=0;m<vcl.size();m++){
													VirtualCluster vctmp = vcl.get(m);
													LeaseLifeCycleState state = vctmp.getLifecycleState();
													if(state == LeaseLifeCycleState.FAIL || state == LeaseLifeCycleState.EXPIRED || state == LeaseLifeCycleState.REJECTED){
													%>
														d.add('<%=vctmp.getGuid()%>','<%=tmp.getGuid()%>','<%=vctmp.getName()%>','javascript:showVirtualClusterInfo(\'<%=basePath%>\',\'<%=vctmp.getGuid()%>\');','', '','<%=basePath%>images/vcerror.png','<%=basePath%>images/vcerror.png','','asset_info_div');
													<%} else if(state == LeaseLifeCycleState.TERMINATION){%>
														d.add('<%=vctmp.getGuid()%>','<%=tmp.getGuid()%>','<%=vctmp.getName()%>','javascript:showVirtualClusterInfo(\'<%=basePath%>\',\'<%=vctmp.getGuid()%>\');','', '','<%=basePath%>images/vcshutdown.png','<%=basePath%>images/vcshutdown.png','','asset_info_div');
													<%} else {%>
														d.add('<%=vctmp.getGuid()%>','<%=tmp.getGuid()%>','<%=vctmp.getName()%>','javascript:showVirtualClusterInfo(\'<%=basePath%>\',\'<%=vctmp.getGuid()%>\');','', '','<%=basePath%>images/cluster.png','<%=basePath%>images/cluster.png','','asset_info_div');
														<%List<Node> nodelist = nodemap.get(vctmp.getGuid());
															if(nodelist!=null && nodelist.size()>0){
																for(int n_num = 0; n_num < nodelist.size(); n_num++){
																	Node node = nodelist.get(n_num);
																	String nodetype = "";
																	if(node instanceof VirtualNode){
																		nodetype = XMMConstants.VIRTUAL_NODE_TYPE;
																	}else{
																		nodetype = XMMConstants.PHYSICAL_NODE_TYPE;
																	}
																	if(node.isHeadNode()){
																	%>
																	d.add('<%=node.getGuid()%>','<%=vctmp.getGuid()%>','<%=node.getName()%>','javascript:showNodeInfo(\'<%=basePath%>\',\'<%=node.getGuid()%>\',\'<%=nodetype%>\');','', '','<%=basePath%>images/headnode.png','<%=basePath%>images/headnode.png','','asset_info_div');
																	<%
																	}else{
																	%>
																	d.add('<%=node.getGuid()%>','<%=vctmp.getGuid()%>','<%=node.getName()%>','javascript:showNodeInfo(\'<%=basePath%>\',\'<%=node.getGuid()%>\',\'<%=nodetype%>\');','', '','<%=basePath%>images/node.png','<%=basePath%>images/node.png','','asset_info_div');
																	<%
																	}
																}	
														}%>
													<%}
												}
												}
											} catch (Exception e) {
												error = e.toString();
											}
										}
									}
									%>
									document.write(d);
								</script></div>
							</td>
							<td id="asset_info_td" width="700px" valign="top">
							<div id="asset_info_div" style="DISPLAY: none"></div>
							</td>
						</tr>
					</tbody>
				</table>
			</tr>
</table>
<!-- InstanceEndEditable --></div>
</div>
<!-- end main --> <!-- footer -->
<%@ include file="Foot.jsp" %>
<!-- end footer --></div>
<!-- end container -->
<div id="vnccontainer"></div>
<div id="rdpcontainer"></div>
<div id="sshcontainer"></div>
</body>
<!-- InstanceEnd -->
</html>
