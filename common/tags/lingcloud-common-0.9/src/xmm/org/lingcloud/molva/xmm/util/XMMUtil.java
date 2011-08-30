/*
 *  @(#)XMMUtil.java  2010-5-27
 *
 *  Copyright (C) 2008-2011,
 *  LingCloud Team,
 *  Institute of Computing Technology,
 *  Chinese Academy of Sciences.
 *  P.O.Box 2704, 100190, Beijing, China.
 *
 *  http://lingcloud.org
 *  
 */

package org.lingcloud.molva.xmm.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetManagerImpl;
import org.lingcloud.molva.ocl.util.ConfigUtil;
import org.lingcloud.molva.ocl.util.ParaChecker;
import org.lingcloud.molva.xmm.pojos.PhysicalNode;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2009-9-18<br>
 * @author Xiaoyi Lu<br>
 */
public class XMMUtil {

	/**
	 * Molva Web Service Url, get it from conf file.
	 */
	private static String molvaServiceUrl = null;
	private static final String REGX_IP = "((25[0-5]|2[0-4]\\d|1\\d{2}"
		+ "|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
	private static final int A = 0;
	@SuppressWarnings("unused")
	private static final int B = 1;
	@SuppressWarnings("unused")
	private static final int C = 2;
	private static final int D = 3;
	private static final int BITS_IN_A_BYTE = 8;

	private XMMUtil() {

	}

	public static String getMolvaServiceUrl() {
		if (molvaServiceUrl == null) {
			try {
				molvaServiceUrl = XMMUtil
						.getValueInCfgFile(XMMConstants.CONFIG_ITEM_SERVICEURL);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return molvaServiceUrl;
	}

	/**
	 * @param string
	 *            The string
	 * @return True if the string is null or ""
	 */
	public static boolean isEmptyString(final String string) {
		return string == null || string.trim().equals("");
	}

	/**
	 * 
	 * @param serverUrl
	 * @return
	 * @throws XMMException
	 */
	public static String formVirtualClusterServiceUrl(String serverUrl)
			throws XMMException {
		try {
			ParaChecker.checkNullParameter(serverUrl, "namingUrl");
		} catch (Exception e) {
			throw new XMMException(e.getMessage());
		}

		return serverUrl.replaceAll("naming", "XMM");
	}

	public static String getValueInCfgFile(String key) throws Exception {
		ConfigUtil conf = new ConfigUtil(XMMConstants.CONFIG_FILE_NAME);
		String value = conf.getProperty(key, "");

		if (isEmptyString(value)) {
			String msg = "get " + key
					+ " from config file failed. The result is null.";
			throw new Exception(msg);
		}
		return value.trim();
	}

	public static Object callService(String serviceUrl, String operation,
			Object[] parameters) throws XMMException {
		try {
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(serviceUrl);

			Object res = call.invoke(operation, parameters);
			return res;
		} catch (Exception e) {
			throw new XMMException(e);
		}
	}

	/**
	 * Get virtualization server url from config file.
	 * 
	 * @return the virtualization server url.
	 * @throws Exception
	 *             get failed with various reasons.
	 */
	public static String getVirtualizationServerUrlInCfgFile() 
		throws Exception {
		return getValueInCfgFile("virtualizationServerUrl");
	}

	/*
	 * Usage: ipIsValid("192.168.3.54","192.168.1.1-192.168.1.10");
	 */
	public static boolean ipIsValid(String ip, String ipSection) {
		if (ipSection == null) {
			return false;
		}
		if (ip == null) {
			return false;
		}
		ipSection = ipSection.trim();
		ip = ip.trim();
		
		final String regxIPB = REGX_IP + "\\-" + REGX_IP;
		if (!ipSection.matches(regxIPB) || !ip.matches(REGX_IP)) {
			return false;
		}
		int idx = ipSection.indexOf('-');
		String[] sips = ipSection.substring(0, idx).split("\\.");
		String[] sipe = ipSection.substring(idx + 1).split("\\.");
		String[] sipt = ip.split("\\.");
		// Bug fixed, a valid ip should not be ended with 0 or 255.
		if (sipt[D].equals("0") || sipt[D].equals("255")
				|| sipt[D].equals("254")) {
			return false;
		}
		long ips = 0L, ipe = 0L, ipt = 0L;
		for (int i = A; i <= D; ++i) {
			ips = ips << BITS_IN_A_BYTE | Integer.parseInt(sips[i]);
			ipe = ipe << BITS_IN_A_BYTE | Integer.parseInt(sipe[i]);
			ipt = ipt << BITS_IN_A_BYTE | Integer.parseInt(sipt[i]);
		}
		if (ips > ipe) {
			long t = ips;
			ips = ipe;
			ipe = t;
		}
		return ips <= ipt && ipt <= ipe;
	}

	public static String runCommand(String command) throws Exception {
		Process child = null;
		String systemType = System.getProperty("os.name");
		if (systemType.equals("Linux")) {
			child = Runtime.getRuntime().exec(command);
		} else if (systemType.indexOf("Windows") > -1) {
			// If the os is windows, you need to add cmd before command.
			child = Runtime.getRuntime().exec(command);
		}

		// Get the input stream and read from it
		BufferedReader in = new BufferedReader(new InputStreamReader(
				child.getInputStream()));
		String c = null;
		StringBuilder sb = new StringBuilder();
		while ((c = in.readLine()) != null) {
			sb.append(c + System.getProperty("line.separator"));
		}
		in.close();
		child.destroy();
		return sb.toString();
	}
	
	public static String getOperatePhysicalNodeCmdInCfgFile() throws Exception {
		return getValueInCfgFile("operatePhysicalNodeCmd");
	}
	
	public static String getTestPhysicalNodeCmdInCfgFile() throws Exception {
		return getValueInCfgFile("testPhysicalNodeCmd");
	}
	
	public static String getOperateVirtualNodeCmdInCfgFile() throws Exception {
		return getValueInCfgFile("operateVirtualNodeCmd");
	}

	public static boolean getPublicIpEnableInCfgFile() throws Exception {
		return Boolean.parseBoolean(getValueInCfgFile("publicIpEnable"));
	}

	public static boolean getIsMonitorClusterEnabledInCfgFile()
			throws Exception {
		return Boolean
				.parseBoolean(getValueInCfgFile("isMonitorClusterEnabled"));
	}

	public static String getMonitorConfigCommandInCfgFile() throws Exception {
		return getValueInCfgFile("monitorConfigCommand");
	}

	public static ArrayList<String> loadPublicIp() throws Exception {
		ConfigUtil conf = new ConfigUtil(XMMConstants.CONFIG_FILE_NAME);
		String pipool = conf.getProperty("publicIpPool", "");

		if (isEmptyString(pipool)) {
			String msg = "get PublicIpPool from config file failed. "
				+ "The result is null.";
			throw new Exception(new Exception(msg));
		}
		ArrayList<String> ayl = new ArrayList<String>();
		pipool = pipool.trim();
		String[] pis = pipool.split(";");
		for (int i = 0; i < pis.length; ++i) {
			int position = pis[i].trim().indexOf("-");
			if (position > -1) {
				String begin = pis[i].trim().substring(0, position).trim();
				String begin1 = begin.substring(0, begin.lastIndexOf("."));
				String begin2 = begin.substring(begin.lastIndexOf(".") + 1,
						begin.length());
				String tmpend = pis[i].trim()
						.substring(position + 1, pis[i].trim().length()).trim();
				int beginInt = Integer.parseInt(begin2);
				int endInt = Integer.parseInt(tmpend);
				if (endInt < beginInt) {
					continue;
				}
				for (int j = beginInt; j <= endInt; ++j) {
					ayl.add(begin1 + "." + j);
				}
			} else {
				ayl.add(pis[i].trim());
			}
		}
		return ayl;
	}

	public static void validateQuotaKey(String quotaKey) throws XMMException {
		// FIXME CPU/MEM/DISK/NETTRAFFIC
		if (quotaKey == null || "".equals(quotaKey)) {
			throw new XMMException("QuotaKey should not be null or blank.");
		}
		if (quotaKey.equals(XMMConstants.QUOTA_CPU)) {
			return;
		} else if (quotaKey.equals(XMMConstants.QUOTA_DISK)) {
			return;
		} else if (quotaKey.equals(XMMConstants.QUOTA_MEM)) {
			return;
		} else if (quotaKey.equals(XMMConstants.QUOTA_NETTRAFFIC)) {
			return;
		}
		throw new XMMException("Invalid QuotaKey, it should be [ "
				+ XMMConstants.QUOTA_CPU + " | " + XMMConstants.QUOTA_DISK
				+ " | " + XMMConstants.QUOTA_MEM + " | "
				+ XMMConstants.QUOTA_NETTRAFFIC + " ].");
	}

	public static void validateQuotaValue(String quotaKey, int quotaValue) {
		// TODO MIN/MAX
	}

	public static void validatePreferKey(String preferKey) throws XMMException {
		// FIXME CPU/MEM/DISK/NETTRAFFIC
		if (preferKey == null || "".equals(preferKey)) {
			throw new XMMException("PreferKey should not be null or blank.");
		}
		if (preferKey.equals(XMMConstants.PREFER_CPU)) {
			return;
		} else if (preferKey.equals(XMMConstants.PREFER_DISK)) {
			return;
		} else if (preferKey.equals(XMMConstants.PREFER_MEM)) {
			return;
		} else if (preferKey.equals(XMMConstants.PREFER_NETTRAFFIC)) {
			return;
		}
		throw new XMMException("Invalid PreferKey, it should be [ "
				+ XMMConstants.PREFER_CPU + " | " + XMMConstants.PREFER_DISK
				+ " | " + XMMConstants.PREFER_MEM + " | "
				+ XMMConstants.PREFER_NETTRAFFIC + " ].");
	}

	public static void validatePreferValue(String preferKey, int preferValue) {
		// TODO MIN/MAX; if user's preference is -1, then it means no limited.
	}

	public static void validateUsageLeftKey(String usageLeftKey)
			throws Exception {
		// FIXME CPU/MEM/DISK/NETTRAFFIC
		if (usageLeftKey == null || "".equals(usageLeftKey)) {
			throw new Exception("UsageLeftKey should not be null or blank.");
		}
		if (usageLeftKey.equals(XMMConstants.USAGELEFT_CPU)) {
			return;
		} else if (usageLeftKey.equals(XMMConstants.USAGELEFT_DISK)) {
			return;
		} else if (usageLeftKey.equals(XMMConstants.USAGELEFT_MEM)) {
			return;
		} else if (usageLeftKey.equals(XMMConstants.USAGELEFT_NETTRAFFIC)) {
			return;
		}
		throw new Exception("Invalid UsageLeftKey, it should be [ "
				+ XMMConstants.USAGELEFT_CPU + " | "
				+ XMMConstants.USAGELEFT_DISK + " | "
				+ XMMConstants.USAGELEFT_MEM + " | "
				+ XMMConstants.USAGELEFT_NETTRAFFIC + " ].");
	}

	public static void validateUsageLeftValue(String usageLeftKey,
			int usageLeftValue) {
		// TODO MIN/MAX;

	}

	public static void main(String[] args) {
		try {
			System.setProperty("lingcloud.home", ".");
			String url = XMMUtil.getMonitorConfigCommandInCfgFile();
			System.out.println(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getPartitionServerAddressInCfgFile() throws Exception {
		return getValueInCfgFile("partitionImageServer");
	}

	public static String getPartitionDriverPathInCfgFile() throws Exception {
		return getValueInCfgFile("partitionDriverPath");
	}

	public static String getStaticMetaInfoCollectorInCfgFile() 
		throws Exception {
		return getValueInCfgFile("staticMetaInfoCollector");
	}

	public static String getStaticMetaInfoSenderInCfgFile() throws Exception {
		return getValueInCfgFile("staticMetaInfoSender");
	}

	public static PhysicalNode getPhysicalNodeByHostName(String hostname)
			throws Exception {
		AssetManagerImpl ami = new AssetManagerImpl();
		String[] fields = new String[] { "attributes['hostname']", "type" };
		String[] operators = new String[] { "=", "=" };
		Object[] values = new Object[] { hostname,
				XMMConstants.PHYSICAL_NODE_TYPE };
		List<Asset> al = ami.search(fields, operators, values);
		if (al == null || al.isEmpty()) {
			return null;
		}
		return new PhysicalNode(al.get(0));
	}

	public static String getLocalHostName() throws Exception {
		String command = "/bin/hostname";
		return XMMUtil.runCommand(command);
	}

	public static String getVirtualClusterConfigDriverPathInCfgFile()
			throws Exception {
		return getValueInCfgFile("virtualClusterConfigDriverPath");
	}

	public static String getVMKillerInCfgFile() throws Exception {
		return getValueInCfgFile("vmKiller");
	}

	public static boolean isBlankOrNull(String value) {
		boolean ret = true;
		ret = (value == null) || (value.trim().length() == 0);
		return ret;
	}

	/**
	 * Validate the input value. Throw exception if is blank or null.
	 * 
	 * @param value
	 * @param desc
	 * @throws XMMException
	 */
	public static void validateBlankOrNull(String value, String desc)
			throws XMMException {
		if (isBlankOrNull(value)) {
			throw new XMMException("The input string with the description of '"
					+ desc + "' is blank or null, but it's required!");
		}
	}

	public static void validateIp(String ip, String msg) throws Exception {
		if (ip == null || "".equals(ip)) {
			throw new Exception(msg + " should not be null or blank.");
		}
		ip = ip.trim();
		if (!ip.matches(REGX_IP)) {
			throw new Exception(msg + " is not a valid ip address.");
		}
	}

	public static boolean ipIsValid(String ip) throws Exception {
		if (ip == null || "".equals(ip)) {
			return false;
		}
		ip = ip.trim();
		if (!ip.matches(REGX_IP)) {
			return false;
		}
		return true;
	}

	public static String getONEVersion() throws Exception {
		return getValueInCfgFile("oneVersion");
	}

	public static String getUserPasswordToken4ONE() throws Exception {
		return getValueInCfgFile("oneUserPassword");
	}

	public static boolean getIsAcEnable() throws Exception {

		String acEnable = getValueInCfgFile("isAcEnable");
		return acEnable.equalsIgnoreCase("true");
	}

	public static String getImageMgmtWay() throws Exception {
		return getValueInCfgFile("imageMgmtWay");
	}
	
	public static boolean getAccessControlEnable() throws Exception {
		String accessControlEnable = getValueInCfgFile("accessControlEnable");
		return accessControlEnable.equals("true");
	}
	
	public static String getAccessControlAdminGroup() throws Exception {
		return getValueInCfgFile("accessControlAdminGroup");
	}	
	
	public static String getUtilityScriptsPath() throws Exception {
		return getValueInCfgFile("utilityScriptsPath");
	}
	
	public static String getAccessControlUserGroup() throws Exception {
		return getValueInCfgFile("accessControlUserGroup");
	}
	
	public static String getNetworkBridge() throws Exception {
		String bridge = getValueInCfgFile("networkBridge");
		if (bridge == null || "".equals(bridge)) {
			bridge = XMMConstants.DEFAULT_BRIDGE;
		}
		return bridge;
	}
}
