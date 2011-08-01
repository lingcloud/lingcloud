/* 
 * @(#)AccessControl.java Jul 18, 2011
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

import net.sf.jpam.Pam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.xmm.util.XMMUtil;

/**
 * 
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.0 17-July,2011<br>
 * @author Xuechao Liu<br>
 * 
 */
public class AccessControl {
	
	/**
	 *identify if the AccessControl service is opened.
	 */
	private static boolean isEnabled;
	
	/**
	 *the admin group that allowed to access.
	 */
	private static String adminGroup;
	
	/**
	 * the userGroup.
	 */
	private static String userGroup;
		
	/**
	 *path of the execute isUserInGroup script.
	 */
	private static String shPathIsUserInGroup; 
	
	/**
	 *path of the execute getUidByUsername script.
	 */
	private static String shPathGetID;
	
	/**
	 *path of the execute getUsernameByUid script.
	 */
	private static String shPathGetUname;

	/**
	 * name of the user. 
	 */
	private String username;

	/**
	 * @author LXC
	 *status of a user(admin, invalid or unused)
	 */
	public enum accessControlStatus {
		/**
		 *the status is ADMIN, INVALID, UNUSED.
		 */
		ADMIN, INVALID, UNUSED;
	}
	
	/**
	 * the enum Variable. 
	 */
	private accessControlStatus status;
	
	/**
	 * log.
	 */
	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	
	/**
	 * get access control info from molva.conf.
	 */
	static {
		
		try {
			isEnabled = XMMUtil.getAccessControlEnable();
		} catch (Exception e) {
			isEnabled = false;
		}

		try {
			adminGroup = XMMUtil.getAccessControlAdminGroup();
		} catch (Exception e) {
			adminGroup = null;
		}

		try {
			userGroup = XMMUtil.getAccessControlUserGroup();
		} catch (Exception e) {
			userGroup = null;
		}
		
		try {
			String shpath = XMMUtil.getUtilityScriptsPath();
			shPathGetID = shpath + "/getUidByUsername.sh";
			shPathGetUname = shpath + "/getUsernameByUid.sh";
			shPathIsUserInGroup = shpath + "/isUserInGroup.sh";
		} catch (Exception e) {
			shPathGetID = null;
			shPathGetUname = null;
			shPathIsUserInGroup = null;	
		}
		
	}
	
	/**
	 * Initial function which reads the profile.
	 * @throws Exception throw common info
	 */
	public AccessControl() throws Exception {
		setStatus(accessControlStatus.INVALID);
	}
	
	/**
	 * get the status of the user.
	 * @return status
	 */
	public final accessControlStatus getStatus() {
		return status;
	}
	
	/**
	 * set the status of the user.
	 * @param statusvalue the status of the user
	 */
	public final void setStatus(final accessControlStatus statusvalue) {
		this.status = statusvalue;
	}

	/**
	 * the function return whether the AccessControl is opened.
	 * @return isEnabled
	 */
	public static final boolean  isAccessControlEnabled() {
		return isEnabled;
	}
	
	/**
	 * the function return the shPathGetID.
	 * @return shPathGetID
	 */
	public static final String  getshPathGetID() {
		return shPathGetID;
	}
	
	/**
	 * the function return the shPathGetUname.
	 * @return shPathGetUname
	 */
	public static final String  getshPathGetUname() {
		return shPathGetUname;
	}
	
	/**
	 * the function return the shPathIsUserInGroup.
	 * @return shPathIsUserInGroup
	 */
	public static final String  getshPathIsUserInGroup() {
		return shPathIsUserInGroup;
	}
	
	/**
	 * the function return the userGroup.
	 * @return userGroup
	 */
	public static final String  getuserGroup() {
		return userGroup;
	}
	

	/**
	 * the function judge whether the user is existed in system 
	 * through PAM authentication.
	 * @param usernamevalue the user' name
	 * @param password the user's password
	 * @return authenticated
	 */
	public final boolean isAuthenticate(
			final String usernamevalue, final String password) {
	    Pam pam = new Pam();
	    boolean authenticated = 
	    	pam.authenticateSuccessful(usernamevalue, password);
	    return authenticated;
	}
	
	/**
	 * the function judge whether the existed user is belongs to admin group.
	 * @param usernamevalue the user's name
	 * @return result the result
	 * @throws Exception throw common info
	 */
	public final boolean isAdmin(final String usernamevalue) throws Exception {
           boolean result;
           result = isUserInGroup(usernamevalue, adminGroup);
           log.info("the isAdmin result is:" + result + "\n");
           return result;
	}
	
	/**
	 * the function judge whether the existed user is belongs to some group.
	 * @param usernamevalue the user's name
	 * @param groupname the group name
	 * @return true or false
	 * @throws Exception throw common info 
	 */
	private  boolean isUserInGroup(
			final String usernamevalue, final String groupname) 
	throws Exception {
		String cmd = shPathIsUserInGroup + " " 
		+ usernamevalue + " " + groupname;
		log.info("the cmd is:" + cmd + "\n");
		String result =  XMMUtil.runCommand(cmd);
		log.info("the result is:" + result + "\n");
		return result.equals("true" + System.getProperty("line.separator"));
	}

	/**
	 * @param usernamevalue the user's name
	 */
	public final void setUsername(final String usernamevalue) {
		this.username = usernamevalue;
	}

	/**
	 * @return username
	 */
	public final String getUsername() {
		return username;
	}

}
