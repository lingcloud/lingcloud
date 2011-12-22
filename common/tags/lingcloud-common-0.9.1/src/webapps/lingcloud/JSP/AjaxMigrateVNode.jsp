<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.portal.util.XMMPortalUtil"%>
<%@ page import="org.lingcloud.molva.xmm.client.XMMClient"%>
<%@ page import="org.lingcloud.molva.xmm.pojos.*"%>
<%@ page import="org.lingcloud.molva.xmm.ac.PartitionAC"%>
<%@ page import="org.lingcloud.molva.xmm.vam.pojos.*" %>
<%@ page import="org.lingcloud.molva.xmm.vam.services.*" %>
<%@ page import="org.lingcloud.molva.xmm.vam.util.*" %>
<%@ page import="org.apache.commons.logging.Log"%>
<%@ page import="org.apache.commons.logging.LogFactory"%>
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
			String vNodeGuid = request.getParameter("vNodeGuid");
			VirtualNode vNode = null;
			String parentName = null;
			XMMClient vxc = null;
			List<PhysicalNode> pnlist = null;
			List<PhysicalNode> pNodes = new ArrayList<PhysicalNode>();
			vxc = XMMPortalUtil.getXMMClient();
			try {
				vNode = vxc.viewVirtualNode(vNodeGuid);
				pnlist = vxc.listPhysicalNodeInPartition(vNode.getPartitionId());
				parentName = vNode.getParentPhysialNodeName();
				for(int i = 0 ; i < pnlist.size() ; i++) {
					if (!parentName.equals(pnlist.get(i).getName())) {
						pNodes.add(pnlist.get(i));
					}
				}
			} catch (Exception e) {
				out.println(e.toString());
				return;
			}
%>
<table border="0" cellspacing=1 width="100%">
	<tbody>
		<tr class="actionlog_title">
			<th width="200" valign="middle"><bean:message
					key="org.lingcloud.molva.xmm.node.operate.migrate.vNode" />
			</th>
			<td width="250" valign="middle" id="vNodeName" name="vNodeName"> <%=vNode.getName()%>
			</td>
		</tr>
		<tr class="actionlog_title">
			<th width="200" valign="middle"><bean:message
					key="org.lingcloud.molva.xmm.node.operate.migrate.currParent" />
			</th>
			<td width="250" valign="middle" id="parentName"> <%=parentName%>
			</td>
		</tr>
		<tr class="actionlog_title">
			<th width="200" valign="middle"><bean:message
					key="org.lingcloud.molva.xmm.node.operate.migrate.targetNode" />
			</th>
			<td width="250" valign="middle">
				
				<select id="pnName" name="pnName">
					<%if(pNodes == null || pNodes.size() <= 0) {%>
						<option value="-1"><bean:message
							key="org.lingcloud.molva.xmm.node.operate.migrate.empty" /></option>
					<%}else{
						for(int i=0; i<pNodes.size(); i++){
							String name = pNodes.get(i).getName();
							String guid = pNodes.get(i).getGuid();
						%>
						<option value="<%=guid%>"><%=name%></option>
						<%}
					}%>
				</select>
			</td>
		</tr>
	</tbody>
</table>
