/*
 *  @(#)GNodeUtil.java  2006-5-12
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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.lingcloud.molva.ocl.persistence.GNode;
import org.lingcloud.molva.ocl.persistence.GNodeConstants;
import org.lingcloud.molva.ocl.persistence.GNodeException;

/**
 * 
 * <strong>Purpose:</strong><br>
 * TODO.
 *
 * @version 1.0.1 May 12, 2006<br>
 * @author Yongqiang Zou<br>
 *
 */
public class GNodeUtil {
	/**
	 * To check whether it's a valid attribute map of GNode. Here are some
	 * rules.<br>
	 * <ul>
	 * <li>Should use empty attribute map, don't use null. </li>
	 * <li>A single GNode can only support at most
	 * GNodeConstants.MAX_GNODE_ATTR_COUNT attributes.</li>
	 * <li>The value of the attribute can only be String.</li>
	 * <li>The max length of a single attribute should never exceed
	 * GNodeConstants.MAX_LENGTH_OF_ONE_ATTR</li>
	 * <li>The max length of all attributes should never exceed
	 * GNodeConstants.MAX_LENGTH_OF_ALL_GNODE_ATTRS</li>
	 * </ul>
	 * 
	 * @param gnode
	 *            the GNode to be checked.
	 * @throws GNodeException
	 *             check failed will throw exception.
	 */
	public static void checkGNodeAttr(GNode gnode) throws GNodeException {
		if (gnode == null) {
			return;
		}

		if (gnode.getObj() != null
				&& gnode.getObj().length() 
					> GNodeConstants.MAX_LENGTH_OF_GNODE_OBJ) {
			String msg = "The obj field of a GNode should at most "
					+ GNodeConstants.MAX_LENGTH_OF_GNODE_OBJ;
			throw new GNodeException(new Exception(msg));
		}

		Map attributes = gnode.getAttributes();

		if (attributes == null) {
			String msg = "Should use empty attribute map, don't use null";
			throw new GNodeException(new Exception(msg));
		}
		if (attributes.size() > GNodeConstants.MAX_GNODE_ATTR_COUNT) {
			String msg = "A single GNode can only support at most "
					+ GNodeConstants.MAX_GNODE_ATTR_COUNT + " attributes.";
			throw new GNodeException(new Exception(msg));
		}

		long totalLength = 0;
		Set attrs = attributes.entrySet();
		Iterator it = attrs.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object obj = entry.getValue();
			// bug fixed: if is null, OK!
			if (obj == null) {
				continue;
			}
			if (!(obj instanceof String)) {
				String msg = "The value of attribute must be String.";
				throw new GNodeException(new Exception(msg));
			}
			String strObj = (String) obj;
			if (strObj.length() > GNodeConstants.MAX_LENGTH_OF_ONE_ATTR) {
				String msg = "The max length of a single attribute "
					+ "should never exceed "
						+ GNodeConstants.MAX_LENGTH_OF_ONE_ATTR;
				throw new GNodeException(new Exception(msg));
			}
			totalLength += strObj.length();
		}

		if (totalLength > GNodeConstants.MAX_LENGTH_OF_ALL_GNODE_ATTRS) {
			String msg = "The max length of all attributes should never exceed "
					+ GNodeConstants.MAX_LENGTH_OF_ALL_GNODE_ATTRS;
			throw new GNodeException(new Exception(msg));
		}
	}
	
	private GNodeUtil() {
		
	}
}
