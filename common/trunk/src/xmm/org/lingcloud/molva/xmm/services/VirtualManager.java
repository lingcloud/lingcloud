/*
 *  @(#)VirtualManager.java  2010-5-27
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

package org.lingcloud.molva.xmm.services;

import org.lingcloud.molva.xmm.util.XMMUtil;
import org.lingcloud.molva.xmm.vmc.VirtualClient;
import org.lingcloud.molva.xmm.vmc.VirtualClientImpl;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2009-9-19<br>
 * @author Xiaoyi Lu<br>
 * @email luxiaoyi@software.ict.ac.cn
 */
public class VirtualManager {

	private static VirtualManager vm = new VirtualManager();

	private static String virtualServerUrl = null;

	private static String oneVersion = null;

	private static String userPasswordToken = null;

	private VirtualManager() {
	}

	public static synchronized VirtualManager getInstance() {
		return vm;
	}

	public VirtualClient getVirtualClient() throws Exception {
		if (virtualServerUrl == null) {
			virtualServerUrl = XMMUtil
					.getVirtualizationServerUrlInCfgFile();
		}

		if (oneVersion == null) {
			oneVersion = XMMUtil.getONEVersion();
		}
		if (oneVersion != null && oneVersion.trim().startsWith("1.2")) {
			/**
			 * for other versions.
			 */
		} else {
			if (userPasswordToken == null) {
				userPasswordToken = XMMUtil.getUserPasswordToken4ONE();
			}
			return new VirtualClientImpl(virtualServerUrl, userPasswordToken);
		}
		return new VirtualClientImpl(virtualServerUrl, userPasswordToken);

	}
}
