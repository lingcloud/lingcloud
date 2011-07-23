/* 
 * @(#)Json.java 2009-10-6 
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

import java.util.*;
import net.sf.jpam.*;

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
	
	//identify if the AccessControl service is opened
	boolean isOpen;
	
	//the admin group that allowed to access
	String adminGroup;
	
	//path of the executeable script
	String shPath; 
	
	//name of the user 
	public String username;
		
	//status of a user(admin,invalid or usused)
	public enum accessControlStatus 
	{
		ADMIN, INVALID, UNUSED,DEFAULT;
	}
	
	//the enum Variable
	private accessControlStatus status;
	
	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	
	//Initial function which reads the profile
	public AccessControl() throws Exception
	{
		isOpen = XMMUtil.getAccessControlEnable();
		adminGroup = XMMUtil.getAccessControlAdminGroup();
		shPath = XMMUtil.getUtilityScriptsPath()+"/isUserInGroup.sh";
		setStatus(accessControlStatus.DEFAULT);
	}
	
    //get the status of the user
	public accessControlStatus getStatus() {
		return status;
	}
	
    //set the status of the user
	public void setStatus(accessControlStatus status) {
		this.status = status;
	}

	
	//the function return whether the AccessControl is opened
	public boolean isAccessControlEnabled()
	{
		if (isOpen == true) 
			return true;
		else 
			return false;
	}
		
	//the function judge whether the user is existed in system through PAM authentication
	public boolean Authenticate(String username, String password)
	{
	    Pam pam = new Pam();
	    boolean authenticated = pam.authenticateSuccessful(username, password);
	    return authenticated;
	}
	
	//the function judge whether the existed user is belongs to admin group
	public boolean isAdmin(String username) throws Exception
	{
           boolean result;
           result = isUserInGroup(username,adminGroup);
           log.info("the isAdimin result is:"+result+"\n");
           return result;
	}
	
	//the function judge whether the existed user is belongs to some group
	private boolean isUserInGroup(String username, String groupname) throws Exception
	{
		String cmd = shPath+" "+username+" "+groupname;
		log.info("the cmd is:"+cmd+"\n");
		String result =  XMMUtil.runCommand(cmd);
		log.info("the result is:"+result+"\n");
		if (result.equals("true"+System.getProperty("line.separator")))
			return true;
		else 
			return false;
	}
	
	//the combination of the last two functions 
	public boolean isAuthenticate(String username, String password) throws Exception
	{
		if(Authenticate(username, password) == true)
		{
			if (isAdmin(username) == true)
				return true;
			else 
				return false;
		}
		else 
			return false;
	}

}
