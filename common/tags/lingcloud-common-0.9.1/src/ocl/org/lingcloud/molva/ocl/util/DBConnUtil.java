/*
 *  @(#)DBConnUtil.java  2006-5-12
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

/**
 * 
 * <strong>Purpose:</strong><br>
 * TODO.
 *
 * @version 1.0.1 May 12, 2006<br>
 * @author Yongqiang Zou<br>
 *
 */
public class DBConnUtil {
	// the escape chars. by Yongqiang Zou. 2006.9.12.
	// To support bug fixed of bug 58. Don't support ' and other special chars.
	// Yongqiang Zou. 2006.5.12
	private static String escapeChars = "\\\'\"";

	/**
	 * add \ to escape special chars in sql statement, such as \, ' and ".
	 * 
	 * @param orginString
	 * @return the escaped string.
	 */
	public static String doEscape(String orginString) {
		// To support bug fixed of bug 58. Don't support ' and other special
		// chars. Yongqiang Zou. 2006.9.12
		if (orginString == null) { // add this judge, or else it will throw
									// exception. Yongqiang Zou. 2006.9.21
			return null;
		}
		// here we are sure it's not null.

		StringBuffer result = new StringBuffer();

		for (int i = 0; i < orginString.length(); ++i) {
			char ch = orginString.charAt(i);
			// to check that whether the char need to be escaped.
			boolean isEscape = false;
			for (int j = 0; j < escapeChars.length(); ++j) {
				char escapeChar = escapeChars.charAt(j);
				if (ch == escapeChar) {
					isEscape = true;
					break;
				}
			}

			if (isEscape) { // if need escape, add a \
				result.append('\\');
			}
			result.append(ch);
		}

		return result.toString();
	}

	/**
	 * change the escaped string to orgin string.
	 * 
	 * @param escapedString
	 * @return the orgin no escape char string.
	 */
	public static String undoEscape(String escapedString) {
		// To support bug fixed of bug 58. Don't support ' and other special
		// chars. Yongqiang Zou. 2006.9.12
		if (escapedString == null) { // add this judge, or else it will throw
									// exception. Yongqiang Zou. 2006.9.21
			return null;
		}
		// here we are sure it's not null.

		StringBuffer result = new StringBuffer();

		for (int i = 0; i < escapedString.length(); ++i) {
			char ch = escapedString.charAt(i);

			if (ch == '\\') { // should see the next char to decide whether
								// this should be discarded.
				boolean isEscape = false;
				char nextChar = ch;
				// to check that whether the next char is escaped char.
				if (i < escapedString.length() - 1) {
					nextChar = escapedString.charAt(i + 1);
					for (int j = 0; j < escapeChars.length(); ++j) {
						char escapeChar = escapeChars.charAt(j);
						if (nextChar == escapeChar) {
							isEscape = true;
							break;
						}
					}
				}

				if (isEscape) { // if has been escape, discard the \
					result.append(nextChar);
					++i;
				} else { // it's a normal \
					result.append(ch);
				}
			} else {
				result.append(ch);
			}
		}

		return result.toString();
	}
	
	private DBConnUtil() {
		
	}
}
