/*
 *  @(#)TestConstants.java 2011-6-23
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

package org.lingcloud.molva.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * <strong>Purpose:</strong><br>
 * The utils for LingCloud Portal test.
 * 
 * @version 1.0.0 2011-6-23<br>
 * @author Jian Lin<br>
 * 
 */
public class TestUtils {

	private static String browser = TestConstants.Browser.FIREFOX;

	public static void setBrowser(String browser) {
		TestUtils.browser = browser;
	}

	public static String getBrowser() {
		return browser;
	}

	public static void copyFile(String srcPath, String dstPath)
			throws Exception {
		int total = 0;
		int read = 0;
		File srcfile = new File(srcPath);
		if (srcfile.exists()) {
			FileInputStream fis = new FileInputStream(srcPath);
			FileOutputStream fos = new FileOutputStream(dstPath);
			byte[] buffer = new byte[TestConstants.K];

			while ((read = fis.read(buffer)) != -1) {
				total += read;
				fos.write(buffer, 0, read);
			}
			fis.close();
			fos.close();
		}
	}

	private TestUtils() {

	}

}
