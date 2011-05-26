/* 
 * @(#)XMMPortalUtil.java 2009-10-6 
 *  
 * Copyright (C) 2008-2011, 
 * LingCloud Team, 
 * Institute of Computing Technology, 
 * Chinese Academy of Sciences. 
 * P.O.Box 2704, 100190, Beijing, China. 
 * 
 * http://lingcloud.org 
 *  
 */
package org.lingcloud.molva.portal.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.ocl.asset.AssetConstants;
import org.lingcloud.molva.ocl.persistence.GNodeException;
import org.lingcloud.molva.ocl.util.GenericValidator;
import org.lingcloud.molva.ocl.util.StringUtil;
import org.lingcloud.molva.xmm.client.XMMClient;
import org.lingcloud.molva.xmm.pojos.Node;
import org.lingcloud.molva.xmm.pojos.Partition;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;
import org.lingcloud.molva.xmm.pojos.VirtualCluster;
import org.lingcloud.molva.xmm.pojos.VirtualNode;
import org.lingcloud.molva.ocl.util.ConfigUtil;
import org.lingcloud.molva.xmm.util.XMMConstants;
import org.lingcloud.molva.xmm.util.XMMException;

/**
 * <strong>Purpose:Some methods for portal.</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2009-10-4<br>
 * @author Xiaoyi Lu<br>
 * @email luxiaoyi@software.ict.ac.cn
 */
public class XMMPortalUtil {
	private static Log log = LogFactory.getFactory().getInstance(
			XMMPortalUtil.class);

	private static String namingUrl;

	private static String monitorServerUrl = null;

	private static String userNameSuffix = null;

	private static XMMClient XMMClient = null;
	
	private static String defaultGroupId = null;
	
	private static String adminGroupId = null;
	
	private static ResourceBundle resource = null;
	
	private static Locale locale = new Locale("zh", "CN");

	public static final int OK = 0;

	public static final int WARN = 1;

	public static final int CRITICAL = 2;

	private static final int DEFAULT_LINGCLOUD_VNC_PORT = 50001;

	private static final int DEFAULT_VNC_PORT = 5901;

	private static final int DEFAULT_RDP_PORT = 3389;

	private static final int DEFAULT_SSH_PORT = 22;

	public static final String VN_AUTO_CREATE = "auto_create";

	public static final String VN_USE_EXIST = "use_exist";

	public static final String PUBIP_HEADNODE = "pubip_headnode";

	public static final String PUBIP_ALLNODE = "pubip_allnode";

	public static final String NODE_INFO_TYPE_SIMPLE = "simple_node_info";

	public static final String NODE_INFO_TYPE_DETAIL = "detail_node_info";

	public static final int[] CPU_NUM = new int[] { 1, 2, 3, 4, 6, 8 };

	public static final int[] MEM_SIZE = new int[] { 128, 256, 512, 1024, 2048,
			4096, 8192 };

	private XMMPortalUtil() {

	}

	public static String getMonitorServerUrlInCfgFile() throws Exception {
		ConfigUtil conf = new ConfigUtil(XMMConstants.CONFIG_FILE_NAME);
		String url = conf.getProperty("MonitorServerUrl", "");

		if (StringUtil.isEmpty(url)) {
			String msg = "get monitor server url from config file failed."
					+ " The result is null.";
			throw new Exception(new Exception(msg));
		}
		return url.trim();
	}

	public static void setNamingUrl(String namingUrl) {
		XMMPortalUtil.namingUrl = namingUrl;
	}

	public static List<Partition> listAllPartition() throws XMMException, Exception {
		try {
			return getXMMClient().listAllPartition();
		} catch (GNodeException e) {
			return null;
		}
	}

	public static List<PhysicalNode> listPhysicalNodeInPartition(String parid) {
		try {
			return getXMMClient().listPhysicalNodeInPartition(parid);
		} catch (Exception e) {
			return null;
		}
	}

	public static XMMClient getXMMClient() throws Exception {
		if (XMMClient == null) {
			XMMClient = new XMMClient("");
		}
		return XMMClient;
	}

	public static boolean checkParamsBlankOrNull(String[] params) {
		if (params == null) {
			return true;
		}
		for (int i = 0; i < params.length; i++) {
			if (GenericValidator.isBlankOrNull(params[i])) {
				return true;
			}
		}
		return false;
	}

	public static String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		} else {
			String[] ips = ip.split(",");
			return ips[0].trim();
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static boolean isInInnerNet(HttpServletRequest request,
			String hostPrivateIp) {
		// FIXME Donot use http://localhost:8080/xxxx url to login
		String ip = getClientIpAddr(request);
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			return false;
		}
		String prefix = hostPrivateIp.split("\\.")[0] + ".";
		if (ip.startsWith(prefix)) {
			return true;
		}
		return false;
	}
	
	public static String getNginxScriptsLocation() throws Exception {
		ConfigUtil conf = new ConfigUtil(XMMConstants.CONFIG_FILE_NAME);
		String suffix = conf.getProperty("NginxScriptsLocation", "");

		if (StringUtil.isEmpty(suffix)) {
			String msg = "get nginx script location from config file failed."
					+ " The result is null.";
			throw new Exception(new Exception(msg));
		}
		return suffix.trim();
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	public static String getProperIpPort4RDP(Node node) {
		if (node == null) {
			return "";
		}
		// check public ip is reachable or not, firstly.
		String[] pubIps = node.getPublicIps();
		if (pubIps != null || pubIps.length > 0) {
			for (int i = 0; i < pubIps.length; i++) {
				String pubip = pubIps[i];
				// For fast error if the ip address is not reachable. the method
				// of checkPortIsListening is too slow.
				return pubip + ":" + DEFAULT_RDP_PORT;
			}
		}

		// check private ip is reachable or not, secondly.
		String[] priIps = node.getPrivateIps();
		if (priIps != null || priIps.length > 0) {
			for (int i = 0; i < priIps.length; i++) {
				String priip = priIps[i];
				return priip + ":" + DEFAULT_RDP_PORT;
			}
		}
		return null;
	}

	public static String getProperIpPort4SSH(Node node) {
		if (node == null) {
			return "";
		}
		// check public ip is reachable or not, firstly.
		String[] pubIps = node.getPublicIps();
		if (pubIps != null || pubIps.length > 0) {
			for (int i = 0; i < pubIps.length; i++) {
				String pubip = pubIps[i];
				// For fast error if the ip address is not reachable. the method
				// of checkPortIsListening is too slow.
				if (checkIpIsReachable(pubip)) {
					if (checkPortIsListening(pubip, DEFAULT_SSH_PORT)) {
						return pubip + ":" + DEFAULT_SSH_PORT;
					}
				}
			}
		}

		// check private ip is reachable or not, secondly.
		String[] priIps = node.getPrivateIps();
		if (priIps != null || priIps.length > 0) {
			for (int i = 0; i < priIps.length; i++) {
				String priip = priIps[i];
				if (checkIpIsReachable(priip)) {
					if (checkPortIsListening(priip, DEFAULT_SSH_PORT)) {
						return priip + ":" + DEFAULT_SSH_PORT;
					}
				}
			}
		}
		return null;
	}

	public static String getProperIpPort4VNC(Node node, String pass, String geometry) {
		if (node == null) {
			return "";
		}
		
		// check public ip is reachable or not, firstly.
		String[] pubIps = node.getPublicIps();
		if (pubIps != null || pubIps.length > 0) {
			for (int i = 0; i < pubIps.length; i++) {
				String pubip = pubIps[i];
				// For fast error if the ip address is not reachable. the method
				// of checkPortIsListening is too slow.
				if (checkIpIsReachable(pubip)) {
//					if (checkPortIsListening(pubip, DEFAULT_LINGCLOUD_VNC_PORT)) {
//						//to change here
//						String result = ClientCode.getPortForVNC(pubip, pass, geometry);
//						if(!result.equals(""))
//							return pubip + result;
//					}
					if (checkPortIsListening(pubip, DEFAULT_VNC_PORT)) {
						return pubip + ":" + DEFAULT_VNC_PORT;
					}
				}
			}
		}

		// check private ip is reachable or not, secondly.
		String[] priIps = node.getPrivateIps();
		if (priIps != null || priIps.length > 0) {
			for (int i = 0; i < priIps.length; i++) {
				String priip = priIps[i];
				if (checkIpIsReachable(priip)) {
//					if (checkPortIsListening(priip, DEFAULT_LINGCLOUD_VNC_PORT)) {
//						String result = ClientCode.getPortForVNC(priip, pass, geometry);
//						if(!result.equals(""))
//							return priip + result;						
//					}
					if (checkPortIsListening(priip, DEFAULT_VNC_PORT)) {
						return priip + ":" + DEFAULT_VNC_PORT;
					}
				}
			}
		}

		// check ip:port from its parent host.
		// TODO here we can use this.isInnerNet() method to adjust the client
		// side, then if out of the inner net, we can choose the public ip of
		// parent host to connect.
		if (node instanceof VirtualNode) {
			String parentHost = ((VirtualNode) node).getParentPhysialNodeName();
			if (parentHost == null || "".equals(parentHost)) {
				return "";
			}
			try {
				int port = Integer.parseInt(((VirtualNode) node)
						.getVncPortInParentPhysialNode());
				if (checkIpIsReachable(parentHost)) {
					if (checkPortIsListening(parentHost, port)) {
						return parentHost + ":" + port;
					}
				} else {
					String[] conds = new String[] { "name" };
					String[] opers = new String[] { "=" };
					Object[] values = new Object[] { parentHost };
					List<PhysicalNode> pnlist = getXMMClient()
							.searchPhysicalNode(conds, opers, values);
					if (pnlist == null || pnlist.isEmpty()) {
						return "";
					} else {
						PhysicalNode pn = pnlist.get(0);
						String[] pubPHIps = pn.getPublicIps();
						if (pubPHIps != null || pubPHIps.length > 0) {
							for (int i = 0; i < pubPHIps.length; i++) {
								String pubip = pubPHIps[i];
								if (checkIpIsReachable(pubip)) {
									if (checkPortIsListening(pubip, port)) {
										return pubip + ":" + port;
									}
								}
							}
						} else {
							return "";
						}
					}
				}

			} catch (Throwable e) {
				return "";
			}
			// }
		}

		return "";
	}

	public static boolean checkPortIsListening(String ip, int port) {
		try {
			// FIXME we must close immedially.
			new Socket(ip, port).close();
			return true;
		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean checkIpIsReachable(String ip) {
		if (ip == null || "".equals(ip)) {
			return false;
		}
		try {
			InetAddress inetaddr = InetAddress.getByName(ip);
			// five seconds timeout time.
			final int timeout = 2000;
			if (inetaddr.isReachable(timeout)) {
				return true;
			}
		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return false;
	}

	public static List<PhysicalNode> listIdlePhysicalNodeInPartition(
			String parid) {
		String[] conds = new String[] { "assetLeaserId", "assetState" };
		String[] opers = new String[] { "=", "=" };
		Object[] values = new Object[] { parid,
				AssetConstants.AssetState.IDLE.toString() };
		try {
			return getXMMClient().searchPhysicalNode(conds, opers, values);
		} catch (Exception e) {
			return null;
		}
	}

	public static Date getServerCurrentTime() throws Exception {
		return getXMMClient().getServerCurrentTime();
	}

	public static List<VirtualNode> getVirtualNodeByUserID(final String userid)
			throws Exception {
		String[] conditions = new String[] { "tenantId" };
		String[] operations = new String[] { "=" };
		Object[] values = new Object[] { userid };
		List<VirtualCluster> vcl = getXMMClient().searchVirtualCluster(
				conditions, operations, values);
		if (vcl == null || vcl.isEmpty()) {
			return null;
		}
		List<VirtualNode> vnodes = new ArrayList<VirtualNode>();
		for (int i = 0; i < vcl.size(); i++) {
			VirtualCluster vc = vcl.get(i);
			List<VirtualNode> vns = getXMMClient()
					.listVirtualNodeInVirtualCluster(vc.getGuid());
			if (vns == null || vns.isEmpty()) {
				continue;
			}
			vnodes.addAll(vns);
		}
		return vnodes;
	}
	
	public static String getVMhostName(int parid, String vmname){
		
		if (vmname == null || "".equals(vmname)) {
			return null;
		}
		try {
			XMMClient vxc = getXMMClient();
			List<Partition> pl = vxc.listAllPartition();
			List<VirtualNode> vnodes = vxc.listVirtualNodeInPartition(pl.get(parid).getGuid());
		
			if (vnodes == null || vnodes.isEmpty()) {
				return null;
			}
			
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			for (int i = 0; i < vnodes.size(); i++) {
				VirtualNode vn = vnodes.get(i);
				// here we need mapping vnode to a right name in monitor system.
				String info = vn.getVmInfo();
				try {
					org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(info
							.getBytes()));
					org.w3c.dom.Node xml = doc.getDocumentElement();
					String idstr = xpath.evaluate("id".toUpperCase(), xml);
					if (idstr != null) {
						int id = Integer.parseInt(idstr);
						
						
						log.info("the vm of " + vmname + "'s ip is " + vn.getPublicIps()[0]);
						return vn.getPublicIps()[0];
					}
				} catch (Exception e) {
					// ignore;
					return null;
				}
			}
			return null;
		} catch (ParserConfigurationException e) {
			return null;
		} catch (Exception e) {
			return null;
		} 
		
	}

	public static List<PhysicalNode> getPhysicalNodeByUserID(final String userid)
			throws Exception {
		String[] conditions = new String[] { "tenantId" };
		String[] operations = new String[] { "=" };
		Object[] values = new Object[] { userid };
		List<VirtualCluster> vcl = getXMMClient().searchVirtualCluster(
				conditions, operations, values);
		if (vcl == null || vcl.isEmpty()) {
			return null;
		}
		List<PhysicalNode> pnodes = new ArrayList<PhysicalNode>();
		for (int i = 0; i < vcl.size(); i++) {
			VirtualCluster vc = vcl.get(i);
			List<PhysicalNode> pns = getXMMClient()
					.listPhysicalNodeInVirtualCluster(vc.getGuid());
			if (pns == null || pns.isEmpty()) {
				continue;
			}
			pnodes.addAll(pns);
		}
		return pnodes;
	}
	
	public static void  setRB(Locale loc){
		resource = ResourceBundle.getBundle("org.lingcloud.molva.portal.struts.ApplicationResources", loc);
	}
	
	private static ResourceBundle getRB(){
		if(resource == null)
			resource = ResourceBundle.getBundle("org.lingcloud.molva.portal.struts.ApplicationResources", locale);
		return resource;
	}
	
	public static String getMessage(String key){		
		try{
			ResourceBundle resource = getRB();
			return resource.getString(key);
		}catch(Exception e){
			log.error("can not get local message for the key " + key);
			return "";			
		}
	}
}

class ClientCode {
	private static Log log = LogFactory.getFactory().getInstance(
			ClientCode.class);
	public static int portNo = 50001;
	public ClientCode(){
		
	}
	
	public static String getPortForVNC(String ip, String pass, String geometry){
		String port = "";

		String gPassword = pass;
		log.info("in clientcode: gusername = " + "");
		try{
			port = Wrapper("", gPassword, "", geometry, ip);
		}catch(Exception e){
			return "";
		}
		//to adjust whether the port starts with 59
		String tPort = port.substring(1);
		try{
			int pt = Integer.parseInt(tPort);
			if(pt < 5900)
				return ":" + (5900 + pt);
		}catch(Exception e){
						
		}
		
		
		return port;
	}

	/*
	 * Encryption
	 */
	public static byte[] senc(String g_password) throws Exception {
		// Get the key from the file types of
		String keyDir = System.getProperty("gos.home");

		FileInputStream f = new FileInputStream(keyDir + File.separator
				+ "conf" + File.separator
				+ "vnc-wrapper.key");
		ObjectInputStream b = new ObjectInputStream(f);
		Key k = (Key) b.readObject();

		// Create a password device
		Cipher cp = Cipher.getInstance("DESede");

		// Initialize Password Remover
		cp.init(Cipher.ENCRYPT_MODE, k);

		// Wait and encrypted to ciphertext
		byte[] ptext = g_password.getBytes("UTF8");

		// Implementation of the encryption
		byte[] ctext = cp.doFinal(ptext);

		return ctext;
	}

	public static String reverse(byte[] byte_encryptResult) {

		String str = "";
		for (int i = 0; i < byte_encryptResult.length; i++) {
			str += (int) byte_encryptResult[i] + ",";
		}

		return str;
	}

	public static String Wrapper(String g_username, String g_password,
			String username, String resolution, String ip) throws Exception {
		// TODO Auto-generated method stub
		
		// Modified by Jian Lin, 2010-09-22
		// Use "" so that user mapping will be done in vnc-wrapper.
		String vncUsername = "";
		
		g_password += "\n";
		// Set the connection address instance, connect local
		InetAddress addr = InetAddress.getByName(ip);

		// corresponding server port number to 50001
		Socket socket = new Socket(addr, portNo);

		try {

			// Set IO handle
			BufferedReader in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())), true);

			// Encrypt g_password
			byte[] byte_encryptResult = senc(g_password);

			// The byte array into a text stream flow for transmission
			String str_encryptResult = reverse(byte_encryptResult);

			// Passing variables (global user name, password, server, user name,
			// resolution)
			String str = g_username + "\n" + str_encryptResult + "\n"
					+ vncUsername + "\n" + resolution;

			out.println(str);

			String port = in.readLine();

			socket.close();
			return port;

		} catch(Exception e) {
			return "";
		}
	}
}
