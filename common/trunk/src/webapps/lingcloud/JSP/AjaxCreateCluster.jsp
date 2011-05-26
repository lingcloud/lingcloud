<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%>
<%@ page import="org.lingcloud.molva.xmm.pojos.*"%>
<%@ page import="org.lingcloud.molva.xmm.ac.PartitionAC"%>
<%@ page import="org.lingcloud.molva.xmm.vam.pojos.*" %>
<%@ page import="org.lingcloud.molva.xmm.vam.services.*" %>
<%@ page import="org.lingcloud.molva.xmm.vam.util.*" %>
<%@page import="org.apache.commons.logging.Log"%>
<%@page import="org.apache.commons.logging.LogFactory"%>
<%@ page import="org.lingcloud.molva.xmm.deploy.policy.VirtualMachineDeployPolicier" %>
<%@ page import="org.lingcloud.molva.xmm.util.XMMConstants" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page isELIgnored="false"%>
<%	String basePath = request.getParameter("basePath");
final Log log = LogFactory.getFactory().getInstance(this.getClass());
			if (basePath == null || "".equals(basePath)) {
				String path = request.getContextPath();
				basePath = request.getScheme() + "://"
						+ request.getServerName() + ":"
						+ request.getServerPort() + path + "/";
			}
			response.setHeader("Pragma","No-Cache");
			response.setHeader("Cache-Control","No-Cache");
			response.setDateHeader("Expires", 0);
			String parid = request.getParameter("parid");
			String targetDiv = request.getParameter("targetDiv");
			Partition par = null;
			XMMClient vxc = null;
			VirtualApplianceManager vam = null;
			List<Partition> parl = null;
			List<VirtualNetwork> vnlist = null;
			List<PhysicalNode> pnlist = null;
			List<PhysicalNode> phnl = null;
			List<VirtualAppliance> valist = null;
			String nodeType;
			String defaultUserId;
			int cpuPreferNum = 1, memPreferSize =512;
			vxc = XMMPortalUtil.getXMMClient();
			vam = VAMUtil.getVAManager();
			try {
				valist = (List<VirtualAppliance>) vam.getAllAppliance();
				try{
					if(valist == null || valist.isEmpty()){
						valist = new ArrayList<VirtualAppliance>();
					}else{
						cpuPreferNum = valist.get(0).getCpuAmount();
						memPreferSize = valist.get(0).getMemory();
						for(int k=0; k<valist.size(); k++){
							if (VAMConstants.STATE_READY != valist.get(k).getState()){
								valist.remove(k);
							} 
						}
					}
				}catch (Exception e){
					//ignore error;
				}finally {
					if(cpuPreferNum < 0) cpuPreferNum = 1;
					if(memPreferSize < 0) memPreferSize = 512;
				}
				parl = vxc.listAllPartition();
				if (parl == null || parl.size() == 0) {
					out.println(XMMPortalUtil.getMessage("org.lingcloud.molva.xmm.partition.createFirst"));
					return;
				}
				
				if(parid == null || "".equals(parid)){
					par = parl.get(0);
				}else{
					par = vxc.viewPartition(parid);
				}
				//list all the physical node in the partition
				phnl = vxc.listPhysicalNodeInPartition(par.getGuid());
				if (!par.getAssetController().equals(PartitionAC.class.getName())) {
					out.println(XMMPortalUtil.getMessage("org.lingcloud.molva.xmm.partition.controllerNotValid"));
					return;
				}else{
					nodeType = par.getAttributes().get(
						PartitionAC.REQUIRED_ATTR_NODETYPE);
				}
				vnlist = vxc.listVirtualNetwork(par.getGuid());
				if(!PartitionAC.VM.equals(nodeType)){
					pnlist = XMMPortalUtil.listIdlePhysicalNodeInPartition(par.getGuid());
				}
			} catch (Exception e) {
				out.println(e.toString());
				return;
				//response.sendRedirect(basePath+"JSP/error.jsp?error="+error);
			}
%>
<table border="0" cellspacing=1 width="100%">
	<tbody>
		<form id="fastNewVirtualClusterForm"
			action="<%=basePath%>/fastNewVirtualCluster.do method='post'">
		<div id='divTab' class='divTab' style=''>
		<ul style='list-style-type: none'>
			<li style='float: left'><a id='link1'
				href="javascript:changeTab2('<%=basePath%>',2,1)" class='active'><span><bean:message
				key="org.lingcloud.molva.xmm.createcluster.basic" /></span></a></li>
			<li style='float: left'><a id='link2'
				href="javascript:changeTab2('<%=basePath%>',2,2)"><span><bean:message
				key="org.lingcloud.molva.xmm.createcluster.advanced" /></span></a></li>
		</ul>
		</div>

		<table id='divTab1' border="0" cellspacing=1 width="100%">

			<tr class="actionlog_title">
				<th width="80" valign="middle"><bean:message
					key="org.lingcloud.molva.xmm.virtualCluster.goalPart" /></th>
				<td width="200"><select id="parguid" name="parguid" onchange="javascript:changeCreateClusterTable('<%=basePath%>',this.options[this.selectedIndex].value,'<%=targetDiv%>','<%=new Random().nextInt()%>');">
					<option value='<%=par.getGuid()%>'
						onclick="">
					<%=par.getName()%></option>
					<%for (int i = 0; i < parl.size(); i++) {
								Partition tmppar = parl.get(i);
								if(tmppar.getGuid().equals(par.getGuid())){
									log.info(tmppar.getName());
									continue;
								}
							%>
					<option value='<%=tmppar.getGuid()%>'>
					<%=tmppar.getName()%></option>
					<%}%>
				</select>*</td>
			</tr>
			<tr class="actionlog_title">
				<th width="80" valign="middle"><bean:message
					key="org.lingcloud.molva.xmm.virtualAppliance.name" /></th>
				<td width="200"><input type="text" name="clustername" size="20"
					maxlength="20" />*</td>
			</tr>
			<tr class="actionlog_title">
				<input type="hidden" name="amm" value="VirtualClusterAMM" />
				<%if (PartitionAC.VM.equals(nodeType)) {
						// VM Partition Config.
						%>
				<tr class="actionlog_title">
					<th width="80" valign="middle"><bean:message
						key="org.lingcloud.molva.xmm.virtualNetwork.config" /></th>
					<td width="400"><div style="display:none"><bean:message
						key="org.lingcloud.molva.xmm.virtualNetwork.autocreate" /><input
						type="radio" name="vntype"
						value="<%=XMMPortalUtil.VN_AUTO_CREATE%>"
						onclick="changeShowTable('autocreate_table','useexist_table');changeShowTable('detail_table','simple_table',callbackNodeDetailForCreateCluster,'<%=basePath%>','detail_div','<%=par.getGuid() %>');"
						checked /> &nbsp;&nbsp;&nbsp;&nbsp;<bean:message
						key="org.lingcloud.molva.xmm.virtualNetwork.useexist" /><input
						type="radio" name="vntype" value="<%=XMMPortalUtil.VN_USE_EXIST%>"
						onclick="changeShowTable('useexist_table','autocreate_table'); changeShowTable('detail_table','simple_table',callbackNodeDetailForCreateCluster,'<%=basePath%>','detail_div','<%=par.getGuid() %>');" /></div>
					<table id="autocreate_table" width="400">
						<tbody>
							<tr>
								<td><bean:message
									key="org.lingcloud.molva.xmm.virtualNetwork.size" />:&nbsp; <input
									type="text" name="nodenum" size="2" maxlength="2"
									onmouseout="javascript:changeNodeNum('detail_table','simple_table',callbackNodeDetailForCreateCluster,'<%=basePath%>','detail_div','<%=par.getGuid() %>')"></input>&nbsp;nodes
								* &nbsp;&nbsp;<font color="red"><bean:message
									key="org.lingcloud.molva.xmm.virtualNetwork.autosize.limit" /></font><br />
								<input type="radio" name="publicIpSupport"
									value="<%=XMMPortalUtil.PUBIP_HEADNODE%>" checked /><bean:message
									key="org.lingcloud.molva.xmm.virtualNetwork.publicip.headnode" />&nbsp;&nbsp;
								<input type="radio" name="publicIpSupport"
									value="<%=XMMPortalUtil.PUBIP_ALLNODE%>" /><bean:message
									key="org.lingcloud.molva.xmm.virtualNetwork.publicip.allnode" />
								</td>
							</tr>
						</tbody>
					</table>

					<table id="useexist_table" style="Display: none" width="400">
						<tbody>
							<tr>
								<td><bean:message
									key="org.lingcloud.molva.xmm.virtualNetwork.select" />:&nbsp;
								<select id="vnguid" name="vnguid"
									onchange="javascript:changeShowTable('detail_table','simple_table',callbackNodeDetailForCreateCluster,'<%=basePath%>','detail_div','<%=par.getGuid() %>')">
									<%if(vnlist == null || vnlist.size() <= 0) {%>
									<option value="-1"><bean:message
										key="org.lingcloud.molva.xmm.virtualNetwork.empty" /></option>
									<%}else{
										boolean allUsedTag = true;
										for(int i=0; i<vnlist.size(); i++){
											VirtualNetwork vn = vnlist.get(i);
											if(vn.getClusterID()==null || "".equals(vn.getClusterID())){
												allUsedTag = false;
												%><option value="<%=vn.getGuid()%>"><%=vn.getName()%></option>
									<%
											}
										}
										if(allUsedTag){
										%><option value="-1"><bean:message
										key="org.lingcloud.molva.xmm.virtualNetwork.allused" /></option>
									<%
										}
									}%>
								</select></td>
							</tr>
						</tbody>
					</table>

					</td>
				</tr>
				<tr class="actionlog_title">
					<th width="80" valign="middle"><bean:message
						key="org.lingcloud.molva.xmm.machine.config" /></th>
					<td width="630"><bean:message
						key="org.lingcloud.molva.xmm.machine.simple.setting" /><input
						type="radio" name="nodeinfotype"
						value="<%=XMMPortalUtil.NODE_INFO_TYPE_SIMPLE%>"
						onclick="javascript:changeShowTable('simple_table','detail_table')"
						checked /> &nbsp;&nbsp;&nbsp;&nbsp;<bean:message
						key="org.lingcloud.molva.xmm.machine.detail.setting" /><input
						type="radio" name="nodeinfotype"
						value="<%=XMMPortalUtil.NODE_INFO_TYPE_DETAIL%>"
						onclick="javascript:changeShowTable('detail_table','simple_table',callbackNodeDetailForCreateCluster,'<%=basePath%>','detail_div','<%=par.getGuid() %>')" />

					<table id="simple_table">
						<tbody>
							<tr>
								<td>
								<TABLE>
									<tbody>
										<tr class="actionlog_title">
											<th><bean:message
												key="org.lingcloud.molva.xmm.machine.info" /></th>
											<th><bean:message
												key="org.lingcloud.molva.xmm.virtualAppliance" /></th>
											<th><bean:message
												key="org.lingcloud.molva.xmm.virtualAppliance.cpuamount" /></th>
											<th><bean:message
												key="org.lingcloud.molva.xmm.virtualAppliance.memsize" /></th>
											<th><bean:message
												key="org.lingcloud.molva.xmm.machine.deploy.policy" /></th>
										</tr>
										<tr>
											<td><input type="hidden" id="nodeip" name="nodeip"
												value="<%=XMMConstants.ALL_NODE_SAME_REQUIREMENT_TAG%>" /><bean:message
												key="org.lingcloud.molva.xmm.allnode" /></td>
											<td><select id="vaguid0" name="vaguid"
												onchange="appChange('<%=basePath%>',0)">
												<%if(valist == null || valist.size()<=0){
											%>
												<option value="-1"><bean:message
													key="org.lingcloud.molva.xmm.partition.noappliance" /></option>
												<%
										}else{
											for(int j=0;j<valist.size();j++){
											%>
												<option value="<%=valist.get(j).getGuid()%>"><%=valist.get(j).getVAName()%></option>
												<%}
										}
										%>
											</select></td>
											<td><select id="cpunum0" name="cpunum">
												<%if(cpuPreferNum >1) { %>
												<%for(int n = 0; n < XMMPortalUtil.CPU_NUM.length; n++){
												if(cpuPreferNum == XMMPortalUtil.CPU_NUM[n]){
												%>
												<option value="<%=XMMPortalUtil.CPU_NUM[n]%>" selected><%=XMMPortalUtil.CPU_NUM[n]%></option>
												<%}else{
												%>
												<option value="<%=XMMPortalUtil.CPU_NUM[n]%>"><%=XMMPortalUtil.CPU_NUM[n]%></option>
												<%
												}
											}
											}else{%>
												<%for(int n = 0; n < XMMPortalUtil.CPU_NUM.length; n++){
												if(cpuPreferNum == XMMPortalUtil.CPU_NUM[n]){
												%>
												<option value="<%=XMMPortalUtil.CPU_NUM[n]%>" selected><%=XMMPortalUtil.CPU_NUM[n]%></option>
												<%}else{
												%>
												<option value="<%=XMMPortalUtil.CPU_NUM[n]%>" disabled><%=XMMPortalUtil.CPU_NUM[n]%></option>
												<%
												}
											}
											}%>

											</select></td>
											<td><select id="memsize0" name="memsize">
												<%for(int m = 0; m < XMMPortalUtil.MEM_SIZE.length; m++){
												if(memPreferSize == XMMPortalUtil.MEM_SIZE[m]){
													int mem = XMMPortalUtil.MEM_SIZE[m];
													String mem_dis;
													if(mem < 1024){
														mem_dis = mem + "MB";
													}else{
														mem_dis = mem/1024 + "GB";
													}
												%>
												<option value="<%=XMMPortalUtil.MEM_SIZE[m]%>" selected><%=mem_dis%></option>
												<%}else{
													int mem = XMMPortalUtil.MEM_SIZE[m];
													String mem_dis;
													if(mem < 1024){
														mem_dis = mem + "MB";
													}else{
														mem_dis = mem/1024 + "GB";
													}
												%>
												<option value="<%=XMMPortalUtil.MEM_SIZE[m]%>"><%=mem_dis%></option>
												<%
												}
											}%>
											</select></td>
											<td align="center"><select id="deployPolicy0"
												name="deployPolicy"
												onchange="showDeployPN(this.selectedIndex, 0)">
												<option
													value="<%=VirtualMachineDeployPolicier.RANDOM_DEPLOY%>"><bean:message
													key="org.lingcloud.molva.xmm.machine.deploy.random" /></option>
												<option
													value="<%=VirtualMachineDeployPolicier.EFFECT_DEPLOY%>"><bean:message
													key="org.lingcloud.molva.xmm.machine.deploy.effect" /></option>
												<!-- <option value="<%=VirtualMachineDeployPolicier.PERFORMANCE_DEPLOY%>"><bean:message key="org.lingcloud.molva.xmm.machine.deploy.performance" /></option> -->
												<option
													value="<%=VirtualMachineDeployPolicier.LOADAWARE_DEPLOY%>"><bean:message
													key="org.lingcloud.molva.xmm.machine.deploy.loadaware" /></option>
												<option
													value="<%=VirtualMachineDeployPolicier.ASSIGN_DEPLOY%>"><bean:message
													key="org.lingcloud.molva.xmm.machine.deploy.assign" /></option>
											</select> <select name="deployPolicyParam" style="display: none;"
												id="deployInPhysicalNode0">
												<%if(phnl == null || phnl.size() <= 0) {%>
												<option value="-1"><bean:message
													key="org.lingcloud.molva.xmm.physicalNode.empty2" /></option>
												<%}else{
												for(int i=0; i<phnl.size(); i++){																									
												%>
												<option value="<%=phnl.get(i).getName()%>"><%=phnl.get(i).getName()%></option>
												<%}
												}
											%>
											</select></td>
										</tr>
									</tbody>
								</TABLE>
								</td>
							</tr>
						</tbody>
					</table>

					<table id="detail_table" style="Display: none">
						<tbody>
							<tr>
								<td>
								<div id="detail_div" />
								</td>
							</tr>
						</tbody>
					</table>

					</td>
				</tr>

				<%
					} else {
						// PM Partition Config.	
						%>

				<tr class="actionlog_title">
					<th width="80" valign="middle"><bean:message
						key="org.lingcloud.molva.xmm.virtualNetwork.config" /></th>
					<td width="530"><bean:message
						key="org.lingcloud.molva.xmm.virtualNetwork.autocreate" /><input
						type="radio" name="vntype"
						value="<%=XMMPortalUtil.VN_AUTO_CREATE%>"
						onclick="javascript:changeShowTable('autocreate_table','useexist_table')"
						checked /> &nbsp;&nbsp;&nbsp;&nbsp;<bean:message
						key="org.lingcloud.molva.xmm.virtualNetwork.useexist" /><input
						type="radio" name="vntype" value="<%=XMMPortalUtil.VN_USE_EXIST%>"
						onclick="javascript:changeShowTable('useexist_table','autocreate_table')" />

					<table id="autocreate_table" width="530">
						<tbody>
							<tr>
								<td><bean:message
									key="org.lingcloud.molva.xmm.machine.select" />:<br />
								<%if(pnlist == null || pnlist.size() <= 0) {%> <bean:message
									key="org.lingcloud.molva.xmm.physicalNode.empty" /> <%}else{										
										for(int i=0; i<pnlist.size(); i++){
											PhysicalNode pn = pnlist.get(i);
											if(i!=0&&i%4==0){
												%><br />
								<%
											}
										%> <input type="checkbox" name="pnnodeip"
									value="<%=pn.getName()%>"><%=pn.getName()%>
								&nbsp;&nbsp; <%}
										%> <br />
								<input type="checkbox" id="chose" name="chose" value=""
									onclick="selectAll('chose', 'pnnodeip')"><bean:message
									key="org.lingcloud.molva.selectAll" /> <%
									}%>
								</td>
							</tr>
						</tbody>
					</table>

					<table id="useexist_table" style="Display: none" width="400">
						<tbody>
							<tr>
								<td><bean:message
									key="org.lingcloud.molva.xmm.virtualNetwork.select" />:&nbsp;
								<select id="vnguid" name="vnguid">
									<%if(vnlist == null || vnlist.size() <= 0) {%>
									<option value="-1"><bean:message
										key="org.lingcloud.molva.xmm.virtualNetwork.empty" /></option>
									<%}else{
										for(int i=0; i<vnlist.size(); i++){
											VirtualNetwork vn = vnlist.get(i);
										%>
									<option value="<%=vn.getName()%>"><%=vn.getName()%></option>
									<%}
									}%>
								</select></td>
							</tr>
						</tbody>
					</table>

					</td>
				</tr>

				<%}%>
			
		</table>


		<table border="0" cellspacing=1 width="560px" id='divTab2'
			style='display: none;'>
			<tr class="actionlog_title">
				<td colspan="4"><b><bean:message
					key="org.lingcloud.molva.xmm.virtualCluster.RentConfig" /></b>:</td>
			</tr>

			<tr>
				<td width="140px"><bean:message
					key="org.lingcloud.molva.xmm.virtualCluster.RentStart" />:</td>
				<td width="220px"><input readonly type="text" size="26"
					name="effectiveTime" onClick="getDateString(this,rentStartTime)"
					value="click here..." /></td>
				<td width="140px"><bean:message
					key="org.lingcloud.molva.xmm.virtualCluster.RentEnd" />:</td>
				<td width="220px"><input readonly type="text" size="26"
					name="expireTime" onClick="getDateString(this,rentEndTime)"
					value="click here..." /></td>
			</tr>
			<tr>
				<td />
				<td>
				<div width="220px" id="etcontainer"></div>
				</td>
				<td />
				<td>
				<div width="220px" id="epcontainer"></div>
				</td>
			</tr>

			<tr class="actionlog_title">
				<td colspan="4"><font color="red">
				<div id="serverTime"></div>
				</font></td>
			</tr>
			<tr class="actionlog_title">
				<td colspan="4"><b><bean:message
					key="org.lingcloud.molva.xmm.virtualCluster.desc" /></b>:</td>
			</tr>
			<tr>
				<td colspan="4"><textarea name="desc" cols="50%"></textarea></td>
			</tr>
		</table>

		</form>
	</tbody>
</table>
