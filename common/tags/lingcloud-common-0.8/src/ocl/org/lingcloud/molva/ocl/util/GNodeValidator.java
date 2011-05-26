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

import java.util.Locale;

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
		return GenericValidator.isBlankOrNull(value);
	}

	public static void validateBlankOrNull(String value) throws GNodeException {
		validateBlankOrNull(value, "the invoker doesn't give a description");
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
		if (GenericValidator.isBlankOrNull(value)) {
			throw new GNodeException(
					"The input string with the description of '" + desc
							+ "' is blank or null, but it's required!");
		}
	}

	/**
	 * <p>
	 * Checks if the value matches the regular expression.
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param regexp
	 *            The regular expression.
	 * @return true if matches the regular expression.
	 */
	public static boolean matchRegexp(String value, String regexp) {
		log.debug("Check " + value + " matchRegexp: " + regexp);
		return GenericValidator.matchRegexp(value, regexp);
	}

	public static void validateMatchRegexp(String value, String regexp)
			throws GNodeException {
		log.debug("Check " + value + " validateMatchRegexp: " + regexp);
		validateBlankOrNull(value,
				"Check this string whether or not apply to a regexp format");
		validateBlankOrNull(regexp, "A regexp string");
		if (!GenericValidator.matchRegexp(value, regexp)) {
			throw new GNodeException("The string of " + value
					+ " doesn't apply to a regexp format: " + regexp);
		}
	}

	/**
	 * <p>
	 * Checks if the value can safely be converted to a byte primitive.
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @return true if the value can be converted to a Byte.
	 */
	public static boolean canConvert2Byte(String value) {
		log.debug("Check " + value + " CanConvert2Byte!");
		return GenericValidator.isByte(value);
	}

	public static void validateCanConvert2Byte(String value)
			throws GNodeException {
		log.debug("Check " + value + " validateCanConvert2Byte!");
		validateBlankOrNull(value,
				"Check this string whether or not can Convert2Byte");
		if (!GenericValidator.isByte(value)) {
			throw new GNodeException("The string value of " + value
					+ " can't be convert to byte type.");
		}
	}

	/**
	 * <p>
	 * Checks if the value can safely be converted to a short primitive.
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @return true if the value can be converted to a Short.
	 */
	public static boolean isShort(String value) {
		log.debug("Check " + value + " isShort!");
		return GenericValidator.isShort(value);
	}

	public static void validateShort(String value) throws GNodeException {
		log.debug("Check " + value + " validateShort!");
		validateBlankOrNull(value,
				"Check this string whether or not can Convert2Short");
		if (!GenericValidator.isShort(value)) {
			throw new GNodeException("The string of " + value
					+ " can't be convert to short type!");
		}
	}

	/**
	 * <p>
	 * Checks if the value can safely be converted to a int primitive.
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @return true if the value can be converted to an Integer.
	 */
	public static boolean isInt(String value) {
		log.debug("Check " + value + " isInt!");
		return GenericValidator.isInt(value);
	}

	public static void validateInt(String value) throws GNodeException {
		log.debug("Check " + value + " validateInt!");
		validateBlankOrNull(value,
				"Check this string whether or not can Convert2Int");
		if (!GenericValidator.isInt(value)) {
			throw new GNodeException("The string of " + value
					+ " can't be convert to int type!");
		}
	}

	/**
	 * <p>
	 * Checks if the value can safely be converted to a long primitive.
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @return true if the value can be converted to a Long.
	 */
	public static boolean isLong(String value) {
		log.debug("Check " + value + " isLong!");
		return GenericValidator.isLong(value);
	}

	public static void validateLong(String value) throws GNodeException {
		log.debug("Check " + value + " validateLong!");
		validateBlankOrNull(value,
				"Check this string whether or not can Convert2Long");
		if (!GenericValidator.isLong(value)) {
			throw new GNodeException("The string of " + value
					+ " can't be convert to long type!");
		}
	}

	/**
	 * <p>
	 * Checks if the value can safely be converted to a float primitive.
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @return true if the value can be converted to a Float.
	 */
	public static boolean isFloat(String value) {
		log.debug("Check " + value + " isFloat!");
		return GenericValidator.isFloat(value);
	}

	public static void validateFloat(String value) throws GNodeException {
		log.debug("Check " + value + " validateFloat!");
		validateBlankOrNull(value,
				"Check this string whether or not can Convert2Float");
		if (!GenericValidator.isFloat(value)) {
			throw new GNodeException("The string of " + value
					+ " can't be convert to float type!");
		}
	}

	/**
	 * <p>
	 * Checks if the value can safely be converted to a double primitive.
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @return true if the value can be converted to a Double.
	 */
	public static boolean isDouble(String value) {
		log.debug("Check " + value + " isDouble!");
		return GenericValidator.isDouble(value);
	}

	public static void validateDouble(String value) throws GNodeException {
		log.debug("Check " + value + " validateDouble!");
		validateBlankOrNull(value,
				"Check this string whether or not can Convert2Double");
		if (!GenericValidator.isDouble(value)) {
			throw new GNodeException("The string of " + value
					+ " can't be convert to double type!");
		}
	}

	/**
	 * <p>
	 * Checks if the field is a valid date. The <code>Locale</code> is used with
	 * <code>java.text.DateFormat</code>. The setLenient method is set to
	 * <code>false</code> for all.
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param locale
	 *            The locale to use for the date format, defaults to the system
	 *            default if null.
	 * @return true if the value can be converted to a Date.
	 */
	public static boolean isDateByLocale(String value, Locale locale) {
		log.debug("Check " + value + " isDate by locale: "
				+ locale.getDisplayLanguage());
		return GenericValidator.isDate(value, locale);
	}

	public static void validateDateByLocale(String value, Locale locale)
			throws GNodeException {
		if (locale == null) {
			throw new GNodeException(
					"The locale object is null in the method of "
							+ "GOSValidator.validateDateByLocale!");
		}
		log.debug("Check " + value + " validateDateByLocale by locale: "
				+ locale.getDisplayLanguage());
		validateBlankOrNull(value, "A Date String");
		
		if (!GenericValidator.isDate(value, locale)) {
			throw new GNodeException("The string of " + value
					+ "doesn't apply to locale : "
					+ locale.getDisplayLanguage());
		}
	}

	/**
	 * <p>
	 * Checks if the field is a valid date. The pattern is used with
	 * <code>java.text.SimpleDateFormat</code>. If strict is true, then the
	 * length will be checked so '2/12/1999' will not pass validation with the
	 * format 'MM/dd/yyyy' because the month isn't two digits. The setLenient
	 * method is set to <code>false</code> for all.
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param datePattern
	 *            The pattern passed to <code>SimpleDateFormat</code>.
	 * @param strict
	 *            Whether or not to have an exact match of the datePattern.
	 * @return true if the value can be converted to a Date.
	 */
	public static boolean isDateByPattern(String value, String datePattern,
			boolean strict) {
		log.debug("Check " + value + " isDate by datePattern: " + datePattern
				+ " strict: " + strict);
		return GenericValidator.isDate(value, datePattern, strict);
	}

	public static void validateDateByPattern(String value, String datePattern,
			boolean strict) throws GNodeException {
		log.debug("Check " + value + " validateDateByLocale by datePattern: "
				+ datePattern + " strict: " + strict);
		validateBlankOrNull(value, "A Date String");
		validateBlankOrNull(datePattern, "A DatePattern String");
		if (!GenericValidator.isDate(value, datePattern, strict)) {
			throw new GNodeException("The string of " + value
					+ "doesn't apply to the date partten : " + datePattern
					+ " with the strict policy: " + strict);
		}
	}

	/**
	 * <p>
	 * Checks if a value is within a range (min &amp; max specified in the vars
	 * attribute).
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param min
	 *            The minimum value of the range.
	 * @param max
	 *            The maximum value of the range.
	 * @return true if the value is in the specified range.
	 */
	public static boolean isInRangeByByte(byte value, byte min, byte max) {
		log.debug("Check " + value + " isInRange between min: " + min
				+ " max: " + max);
		return GenericValidator.isInRange(value, min, max);
	}

	/**
	 * <p>
	 * Checks if a value is within a range (min &amp; max specified in the vars
	 * attribute).
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param min
	 *            The minimum value of the range.
	 * @param max
	 *            The maximum value of the range.
	 * @return true if the value is in the specified range.
	 */
	public static boolean isInRangeByInt(int value, int min, int max) {
		log.debug("Check " + value + " isInRange between min: " + min
				+ " max: " + max);
		return GenericValidator.isInRange(value, min, max);
	}

	/**
	 * <p>
	 * Checks if a value is within a range (min &amp; max specified in the vars
	 * attribute).
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param min
	 *            The minimum value of the range.
	 * @param max
	 *            The maximum value of the range.
	 * @return true if the value is in the specified range.
	 */
	public static boolean isInRangeByFloat(float value, float min, float max) {
		log.debug("Check " + value + " isInRange between min: " + min
				+ " max: " + max);
		return GenericValidator.isInRange(value, min, max);
	}

	/**
	 * <p>
	 * Checks if a value is within a range (min &amp; max specified in the vars
	 * attribute).
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param min
	 *            The minimum value of the range.
	 * @param max
	 *            The maximum value of the range.
	 * @return true if the value is in the specified range.
	 */
	public static boolean isInRangeByShort(short value, short min, short max) {
		log.debug("Check " + value + " isInRange between min: " + min
				+ " max: " + max);
		return GenericValidator.isInRange(value, min, max);
	}

	/**
	 * <p>
	 * Checks if a value is within a range (min &amp; max specified in the vars
	 * attribute).
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param min
	 *            The minimum value of the range.
	 * @param max
	 *            The maximum value of the range.
	 * @return true if the value is in the specified range.
	 */
	public static boolean isInRangeByLong(long value, long min, long max) {
		log.debug("Check " + value + " isInRange between min: " + min
				+ " max: " + max);
		return GenericValidator.isInRange(value, min, max);
	}

	/**
	 * <p>
	 * Checks if a value is within a range (min &amp; max specified in the vars
	 * attribute).
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param min
	 *            The minimum value of the range.
	 * @param max
	 *            The maximum value of the range.
	 * @return true if the value is in the specified range.
	 */
	public static boolean isInRangeByDouble(double value, 
			double min, double max) {
		log.debug("Check " + value + " isInRange between min: " + min
				+ " max: " + max);
		return GenericValidator.isInRange(value, min, max);
	}

	/**
	 * <p>
	 * Checks if a field has a valid e-mail address.
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @return true if the value is valid Email Address.
	 */
	public static boolean isEmail(String value) {
		log.debug("Check " + value + " isEmail!");
		return GenericValidator.isEmail(value);
	}

	public static void validateEmail(String value) throws GNodeException {
		log.debug("Check " + value + " validateEmail!");
		validateBlankOrNull(value,
				"Check this string whether or not apply to Email format");

		if (!GenericValidator.isEmail(value)) {
			throw new GNodeException("The string of " + value
					+ " doesn't apply to email format!");
		}
	}

	/**
	 * <p>
	 * Checks if a field is a valid url address.
	 * </p>
	 * If you need to modify what is considered valid then consider using the
	 * UrlValidator directly.
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @return true if the value is valid Url.
	 */
	public static boolean isUrl(String value) {
		log.debug("Check " + value + " isUrl!");
		// if (value != null && !value.startsWith("http://")) {
		// value = "http://" + value;
		// }
		return GenericValidator.isUrl(value);
	}

	public static void validateUrl(String value) throws GNodeException {
		log.debug("Check " + value + " validateUrl!");
		// validateBlankOrNull(value,
		// "Check this string whether or not apply to an Url format");
		if (!GenericValidator.isUrl(value)) {
			throw new GNodeException("The string of " + value
					+ " doesn't apply to an Url format.");
		}
	}

	/**
	 * <p>
	 * Checks if the value's length is less than or equal to the max.
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param max
	 *            The maximum length.
	 * @return true if the value's length is less than the specified maximum.
	 */
	public static boolean maxLength(String value, int max) {
		log.debug("Check " + value + " maxLength :" + max);
		return GenericValidator.maxLength(value, max);
	}

	public static void validateMaxLength(String value, int max)
			throws GNodeException {
		log.debug("Check " + value + " validateMaxLength :" + max);
		// validateBlankOrNull(value, "validateMaxLength");
		if (!isBlankOrNull(value)) {
			if (!GenericValidator.maxLength(value, max)) {
				throw new GNodeException("The length of " + value + " is "
						+ value.length() + " and exceeds " + max);
			}
		}
	}

	/**
	 * <p>
	 * Checks if the value's length is greater than or equal to the min.
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param min
	 *            The minimum length.
	 * @return true if the value's length is more than the specified minimum.
	 */
	public static boolean minLength(String value, int min) {
		log.debug("Check " + value + " minLength :" + min);
		return GenericValidator.minLength(value, min);
	}

	public static void validateMinLength(String value, int min)
			throws GNodeException {
		log.debug("Check " + value + " validateMinLength :" + min);
		validateBlankOrNull(value, "validateMinLength");
		if (!GenericValidator.minLength(value, min)) {
			throw new GNodeException("The length of " + value + " is "
					+ value.length() + " and is smaller than " + min);
		}
	}
	
	private GNodeValidator() {
		
	}
}
