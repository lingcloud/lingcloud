/*
 *  @(#)GNodeValidator.java  2008-4-31
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.lingcloud.molva.ocl.persistence.GNodeException;

/**
 * <strong>Purpose: This class contains basic methods for performing
 * validations.</strong><br>
 * 
 * @version 1.0.1 2008-4-21<br>
 * @author Xiaoyi Lu<br>
 */
public class GNodeValidator {

	private GNodeValidator() {
		
	}
	
	private static Log log = LogFactory.getLog(GNodeValidator.class);
	
	/**
	 * <p>
	 * Checks if the field isn't null and length of the field is greater than
	 * zero not including whitespace.
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @return true if blank or null.
	 */
	public static boolean isBlankOrNull(String value) {
		log.debug("Check " + value + " isBlankOrNull!");
		return ((value == null) || (value.trim().length() == 0));
	}

	/**
	 * Validate the input value. Throw exception if is blank or null.
	 * 
	 * @param value
	 * @param desc
	 * @throws GNodeException
	 */
	public static void validateBlankOrNull(String value, String desc)
			throws GNodeException {
		log.debug("Check " + value + " validateBlankOrNull!");
		if (isBlankOrNull(value)) {
			throw new GNodeException(
					"The input string with the description of '" + desc
							+ "' is blank or null, but it's required!");
		}
	}
	
	public static void validateMaxLength(String value, int max)
			throws GNodeException {
		log.debug("Check " + value + " validateMaxLength :" + max);
		// validateBlankOrNull(value, "validateMaxLength");
		if (!isBlankOrNull(value)) {
			if (!(value.length() <= max)) {
				throw new GNodeException("The length of " + value + " is "
						+ value.length() + " and exceeds " + max);
			}
		}
	}
}
