<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%>
<%@ page import="org.lingcloud.molva.xmm.pojos.*"%>
<%@ page import="org.lingcloud.molva.xmm.vam.pojos.*" %>
<%@ page import="org.lingcloud.molva.xmm.vam.services.*" %>
<%@ page import="org.lingcloud.molva.xmm.vam.util.*" %>
<%@ page import="org.lingcloud.molva.xmm.util.XMMConstants" %>
<%@ page import="org.lingcloud.molva.xmm.deploy.policy.VirtualMachineDeployPolicier" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<%@ page isELIgnored="false"%>
<%String basePath = request.getParameter("basePath");
			if (basePath == null || "".equals(basePath)) {
				String path = request.getContextPath();
				basePath = request.getScheme() + "://"
						+ request.getServerName() + ":"
						+ request.getServerPort() + path + "/";
			}
			String nodenum = request.getParameter("nodenum");
			String parid = request.getParameter("parid");
			int nodeNum = 0;
			String vnguid = request.getParameter("vnguid");
			if((nodenum == null || "".equals(nodenum.trim())) && (vnguid == null || "".equals(vnguid.trim()))){
				out.println("please choose node num or virtual network.");
				return;
			}
			XMMClient vxc = null;
			VirtualApplianceManager vam = null;
			VirtualNetwork vn = null;
			List<VirtualAppliance> valist = null;
			List<PhysicalNode> phnl = null;
			int cpuPreferNum = 1, memPreferSize =512;

			vxc = XMMPortalUtil.getXMMClient();
			vam = VAMUtil.getVAManager();
			try {
				valist = (List<VirtualAppliance>) vam.getAllAppliance();
				//list all the physical node in the partition
				phnl = vxc.listPhysicalNodeInPartition(parid);
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
				
				if(vnguid != null && !"".equals(vnguid.trim())){
					vn = vxc.viewVirtualNetwork(vnguid);
				}else{
					nodeNum = Integer.parseInt(nodenum);
				}
			} catch (Exception e) {
				out.println(e.toString());
				return;
				//response.sendRedirect(basePath+"JSP/error.jsp?error="+error);
			}
%>

			<TABLE width="100%"><tbody>
				<tr class="actionlog_title">
					<th><bean:message key="org.lingcloud.molva.xmm.machine.info" /></th>
					<th><bean:message key="org.lingcloud.molva.xmm.virtualAppliance" /></th>
					<th><bean:message key="org.lingcloud.molva.xmm.virtualAppliance.cpuamount" /></th>
					<th><bean:message key="org.lingcloud.molva.xmm.virtualAppliance.memsize" /></th>
					<th><bean:message key="org.lingcloud.molva.xmm.machine.deploy.policy" /></th>
				</tr>
				<%if(vn!=null) {
					List<Nic> nics = vn.getPrivateIpNics();
					for(int i=1; i<=nics.size(); i++){
						String ip = nics.get(i-1).getIp();
						%>
						<tr  id="nodeDetailR<%=i%>">
							<td><input type="hidden" id="nodeip" name="nodeip" value="<%=ip%>"><%=ip%></td>
							<td>
									<select id="vaguid<%=i%>" name="vaguid" onchange="appChange('<%=basePath%>','<%=i%>')">
									<%if(valist == null || valist.size()<=0){
											%>
											<option value="-1" ><bean:message key="org.lingcloud.molva.xmm.virtualAppliance.empty" /></option>
											<%
										}else{
											for(int j=0;j<valist.size();j++){
											%>
											<option value="<%=valist.get(j).getGuid()%>" ><%=valist.get(j).getVAName()%></option>
											<%}
										}
										%>
										</select>
							</td>
							<td>
										<select id="cpunum<%=i%>" name="cpunum">
											<%if(cpuPreferNum >1) { %>
											<%for(int n = 0; n < XMMPortalUtil.CPU_NUM.length; n++){
												if(cpuPreferNum == XMMPortalUtil.CPU_NUM[n]){
												%>
													<option value="<%=XMMPortalUtil.CPU_NUM[n]%>" selected><%=XMMPortalUtil.CPU_NUM[n]%></option>
												<%}else{
												%>
													<option value="<%=XMMPortalUtil.CPU_NUM[n]%>" ><%=XMMPortalUtil.CPU_NUM[n]%></option>
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
										</select>
							</td>
							<td>
										<select id="memsize<%=i%>" name="memsize">
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
													<option value="<%=XMMPortalUtil.MEM_SIZE[m]%>" ><%=mem_dis%></option>
												<%
												}
											}%>
										</select>
							</td>
							<td>
										<select id="deployPolicy<%=i%>" name="deployPolicy"   onchange= "showDeployPN(this.selectedIndex, <%=i %>)">
											<option value="<%=VirtualMachineDeployPolicier.RANDOM_DEPLOY%>"><bean:message key="org.lingcloud.molva.xmm.machine.deploy.random" /></option>
											<option value="<%=VirtualMachineDeployPolicier.EFFECT_DEPLOY%>"><bean:message key="org.lingcloud.molva.xmm.machine.deploy.effect" /></option>
											<!-- <option value="<%=VirtualMachineDeployPolicier.PERFORMANCE_DEPLOY%>"><bean:message key="org.lingcloud.molva.xmm.machine.deploy.performance" /></option> -->
											<option value="<%=VirtualMachineDeployPolicier.LOADAWARE_DEPLOY%>"><bean:message key="org.lingcloud.molva.xmm.machine.deploy.loadaware" /></option>
											<option value="<%=VirtualMachineDeployPolicier.ASSIGN_DEPLOY%>"><bean:message key="org.lingcloud.molva.xmm.machine.deploy.assign" /></option>
										</select>
										<select style = "display:none;" id = "deployInPhysicalNode<%=i%>" name = "deployPolicyParam">
											<%if(phnl == null || phnl.size() <= 0) {%>
												<option value="-1">\u6CA1\u6709\u7269\u7406\u8282\u70B9</option>
											<%}else{
												for(int j=0; j<phnl.size(); j++){	
																									
												%>
													<option value="<%=phnl.get(j).getName()%>"><%=phnl.get(j).getName()%></option>
												<%
												}
											}%>
										</select>
							</td>
						</tr>
						<%
					}
				} else if(nodenum!=null && !"".equals(nodenum)){
				
					for(int i=1; i<=nodeNum; i++){
						String node = "node_"+i;
						%>
						<tr id="nodeDetailR<%=i%>">
							<td><input type="hidden" id="nodeip" name="nodeip" value="<%=node%>"/><%=node%></td>
							<td>
									<select id="vaguid<%=i%>" name="vaguid" onchange="appChange('<%=basePath%>','<%=i%>')">
									<%if(valist == null || valist.size()<=0){
											%>
											<option value="-1" ><bean:message key="org.lingcloud.molva.xmm.virtualAppliance.empty" /></option>
											<%
										}else{
											for(int j=0;j<valist.size();j++){
											%>
											<option value="<%=valist.get(j).getGuid()%>" ><%=valist.get(j).getVAName()%></option>
											<%}
										}
										%>
										</select>
							</td>
							<td>
										<select id="cpunum<%=i%>" name="cpunum">
											<%if(cpuPreferNum >1) { %>
											<%for(int n = 0; n < XMMPortalUtil.CPU_NUM.length; n++){
												if(cpuPreferNum == XMMPortalUtil.CPU_NUM[n]){
												%>
													<option value="<%=XMMPortalUtil.CPU_NUM[n]%>" selected><%=XMMPortalUtil.CPU_NUM[n]%></option>
												<%}else{
												%>
													<option value="<%=XMMPortalUtil.CPU_NUM[n]%>" ><%=XMMPortalUtil.CPU_NUM[n]%></option>
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
										</select>
							</td>
							<td>
										<select id="memsize<%=i%>" name="memsize">
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
													<option value="<%=XMMPortalUtil.MEM_SIZE[m]%>" ><%=mem_dis%></option>
												<%
												}
											}%>
										</select>
							</td>
							<td align="center">
										<select id="deployPolicy<%=i%>" name="deployPolicy" onchange= "showDeployPN(this.selectedIndex, <%=i %>)">
											<option value="<%=VirtualMachineDeployPolicier.RANDOM_DEPLOY%>"><bean:message key="org.lingcloud.molva.xmm.machine.deploy.random" /></option>
											<option value="<%=VirtualMachineDeployPolicier.EFFECT_DEPLOY%>"><bean:message key="org.lingcloud.molva.xmm.machine.deploy.effect" /></option>
											<!-- <option value="<%=VirtualMachineDeployPolicier.PERFORMANCE_DEPLOY%>"><bean:message key="org.lingcloud.molva.xmm.machine.deploy.performance" /></option> -->
											<option value="<%=VirtualMachineDeployPolicier.LOADAWARE_DEPLOY%>"><bean:message key="org.lingcloud.molva.xmm.machine.deploy.loadaware" /></option>
											<option value="<%=VirtualMachineDeployPolicier.ASSIGN_DEPLOY%>"><bean:message key="org.lingcloud.molva.xmm.machine.deploy.assign" /></option>
										</select>
										<select style = "display:none;" id = "deployInPhysicalNode<%=i%>" name = "deployPolicyParam">
											<%if(phnl == null || phnl.size() <= 0) {%>
												<option value="-1">\u6CA1\u6709\u7269\u7406\u8282\u70B9</option>
											<%}else{
												for(int j=0; j<phnl.size(); j++){																										
												%>
													<option value="<%=phnl.get(j).getName()%>"><%=phnl.get(j).getName()%></option>
												<%}
												
											}%>
										</select>
							</td>
						</tr>
				<%	}
				} else {
				%>
				
				<%	
				}%>
				<tr><td colspan="5"><div id= "nodeDetailsDiv"></div></td></tr>
		</tbody></TABLE>
