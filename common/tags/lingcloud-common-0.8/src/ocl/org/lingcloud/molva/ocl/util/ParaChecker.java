/*
 *  @(#)ParaChecker.java  2007-9-1
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
package org.lingcloud.molva.ocl.util;

import org.lingcloud.molva.ocl.persistence.GNodeException;
import org.lingcloud.molva.ocl.persistence.ID;

/**
 * <strong>Purpose:</strong><br>
 * A class to check all kinds of parameters.
 * 
 * @version 1.0.1 2007-9-1<br>
 * @author zouyongqiang<br>
 * 
 */
public class ParaChecker {

	public static final char ACL_DENY_CHAR = '-';

	public static void checkGuidFormat(String guid, String desc)
			throws GNodeException {
		if (!StringUtil.isEmpty(guid)) {
			try {
				ID.getIDfromHexString(guid);
			} catch (Exception e) {
				String msg = "Not a valid guid string for " + desc
						+ ", guid = " + guid + ", Reason: " + e;
				throw new GNodeException(new Exception(msg));
			}
		}
	}

	public static void checkNullParameter(Object info, String desc)
			throws GNodeException {
		if (info == null) {
			String msg = "the object " + desc + " is null.";
			throw new GNodeException(new Exception(msg));
		}
	}

	/**
	 * Check whether is a valid acl format.
	 * 
	 * @param acl
	 * @throws GNodeException all kinds of exceptions.
	 */
	public static void checkAclFormat(String acl) throws GNodeException {
		// check null.
		checkNullParameter(acl, "acl");

		// check length.
		final int aclLength = 9;
		if (acl.length() != aclLength) {
			String msg = "the acl string length must be " + aclLength
					+ ". But the acl " + acl + " has length " + acl.length();
			throw new GNodeException(new Exception(msg));
		}

		// check whether is valid char.
		final String validchar = "rwx-";
		for (int i = 0; i < acl.length(); ++i) {
			char ch = acl.charAt(i);
			if (validchar.indexOf(ch) == -1) {
				String msg = "the acl " + acl + " has invalid char " + ch
						+ ". All valid chars are " + validchar;
				throw new GNodeException(new Exception(msg));
			}
		}

		// check whether is right position.
		final String fullStr = "rwxrwxrwx";
		for (int i = 0; i < acl.length(); ++i) {
			char ch = acl.charAt(i);
			if (ch != ACL_DENY_CHAR && ch != fullStr.charAt(i)) {
				String msg = "the acl " + acl + " has wrong char " + ch
						+ " at position " + i + ". It can be only "
						+ ACL_DENY_CHAR + " or " + fullStr.charAt(i);
				throw new GNodeException(new Exception(msg));
			}
		}
	}
	private ParaChecker() {
		
	}
}
