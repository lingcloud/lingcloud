/*
 *  @(#)GNodeConstants.java  2007-7-20
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

package org.lingcloud.molva.ocl.persistence;

/**
 * 
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2007-7-20<br>
 * @author zouyongqiang<br>
 * 
 */
public class GNodeConstants {
	private GNodeConstants() {

	}

	/**
	 * NAMEMAXLEN : The max length of Gnode's name field.
	 */
	public static final int NAMEMAXLEN = 255;

	/**
	 * PASSMAXLEN : The max length of User's password field.
	 */
	public static final int PASSMAXLEN = 80;

	/**
	 * DNMAXLEN : The max length of DN field.
	 */
	public static final int DNMAXLEN = 255;

	/**
	 * ENDPOINTURLMAXLEN : The max length of endpoint field.
	 */
	public static final int ENDPOINTURLMAXLEN = 255;

	/**
	 * PROXYMAXLEN : The max length of proxy field.
	 */
	public static final int PROXYMAXLEN = 12000;

	/**
	 * OBJMAXLEN : The max length of Gnode's obj field.
	 */
	public static final int OBJMAXLEN = 4096;

	/**
	 * TYPEMAXLEN : The max length of Gnode's type field.
	 */
	public static final int TYPEMAXLEN = 20;

	/**
	 * RCONTROLLERTYPE : The max length of Gnode's RCONTROLLERTYPE field.
	 */
	public static final int RCONTROLLERTYPEMAXLEN = 160;

	/**
	 * RCONTROLLERURL : The max length of Gnode's RCONTROLLERURL field.
	 */
	public static final int RCONTROLLERURLMAXLEN = 160;

	/**
	 * EXPORT : The max length of Gnode's export field.
	 */
	public static final int EXPORTMAXLEN = 255;

	/**
	 * DESCMAXLEN : The max length of Gnode's description field.
	 */
	public static final int DESCMAXLEN = 32768;

	/**
	 * GNodeType: UNKNOWN.
	 */
	public static final int GNODETYPE_UNKNOWN = 0;

	/**
	 * GNodeType: user.
	 */
	public static final int GNODETYPE_USER = 1;

	/**
	 * GNodeType: agora.
	 */
	public static final int GNODETYPE_AGORA = 2;

	/**
	 * GNodeType: resource. The range (GNODETYPE_AGORA, GNODETYPE_RESOURCE) is
	 * reserved. The type value larger than GNODETYPE_RESOURCE are all kinds of
	 * concrete resource types.
	 */
	public static final int GNODETYPE_RESOURCE = 10;

	/**
	 * GNodeType: service.
	 */
	public static final int GNODETYPE_SERVICE = 11;

	/**
	 * GNodeType: message.
	 */
	public static final int GNODETYPE_MESSAGE = 12;

	/**
	 * GNodeType: file.
	 */
	public static final int GNODETYPE_FILE = 13;

	/**
	 * GNodeType: jobqueue.
	 */
	public static final int GNODETYPE_JOBQUEUE = 14;

	/**
	 * GNodeType: usermap.
	 */
	public static final int GNODETYPE_USERMAP = 15;
	/**
	 * export mode: self.
	 */
	public static final String EXPORT_MODE_SELF = "self";

	/**
	 * GNodeType: delegate.
	 */
	public static final String EXPORT_MODE_DELEGATE = "delegate";
	/**
	 * default GNode acl.
	 */
	public static final String DEFAULT_ACL = "rwxr--r--";

	/**
	 * default export mode.
	 */
	public static final String DEFAULT_EXPORT = "*("
			+ GNodeConstants.EXPORT_MODE_SELF + ")";
	/**
	 * The max count of GNode attributes.
	 */

	public static final int MAX_GNODE_ATTR_COUNT = 65535 * 2;

	/**
	 * The max length of a single GNode attribute. 1024*1024
	 */
	public static final int MAX_LENGTH_OF_ONE_ATTR = 1048576;

	/**
	 * The max length of a all GNode attributes.
	 */
	public static final int MAX_LENGTH_OF_ALL_GNODE_ATTRS 
		= MAX_LENGTH_OF_ONE_ATTR;

	public static final int TEN = 10;
	/**
	 * The max length of the GNode object field.
	 */
	public static final int MAX_LENGTH_OF_GNODE_OBJ 
		= MAX_LENGTH_OF_ONE_ATTR / TEN;

	/**
	 * operation type.
	 */
	public static final int OPERTYPE_READ = 0;

	/**
	 * operation type.
	 */
	public static final int OPERTYPE_WRITE = 1;

	/**
	 * operation type.
	 */
	public static final int OPERTYPE_EXECUTE = 2;

	/**
	 * Port max Length.
	 */
	public static final int PORTMAXLEN = 20;

	/**
	 * Host max Length.
	 */
	public static final int HOSTMAXLEN = 255;

	/**
	 * Site Status max length.
	 */
	public static final int SITESTATUSMAXLEN = 20;
}
