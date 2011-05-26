/* 
 * @(#)SetCharacterEncodingFilter.java 2009-10-6 
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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * <strong>Purpose:This class is to filt all request and response's charset.</strong><br>
 * TODO.
 * 
 * @version 1.0.1 Jan 3, 2008<br>
 * @author Xiaoyi Lu<br>
 * @email luxiaoyi@software.ict.ac.cn
 */
public class SetCharacterEncodingFilter implements Filter {

	/**
	 * This variable indicates which character-encoding used.
	 */
	protected String encoding = null;

	/**
	 * An instance to config filter.
	 */
	protected FilterConfig filterConfig = null;

	/**
	 * If ignore is true, it indicates to use encoding variable's value.
	 */
	protected boolean ignore = true;

	/**
	 * The method need to be implemented to invoke before filter call. To set
	 * filter's config instance.
	 */
	public void init(FilterConfig arg0) throws ServletException {
		this.filterConfig = arg0;
		/*
		 * encoding's value will be transtioned form a xml config file.
		 */
		this.encoding = this.filterConfig.getInitParameter("encoding");
		String value = this.filterConfig.getInitParameter("ignore");
		if (value == null) {
			this.ignore = true;
		} else if (value.trim().equalsIgnoreCase("true")) {
			this.ignore = true;
		} else if (value.trim().equalsIgnoreCase("yes")) {
			this.ignore = true;
		} else {
			this.ignore = false;
		}
	}

	/**
	 * Filter's function will be implement here.
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (ignore || (request.getCharacterEncoding() == null)) {
			String tmEncoding = selectEncoding(request);
			if (tmEncoding != null) {
				request.setCharacterEncoding(tmEncoding);
			}
		}
		/*
		 * Continue to do the next filter.
		 */
		chain.doFilter(request, response);
	}

	/**
	 * The method need to be implemented to invoke after filter call.
	 */
	public void destroy() {
		this.encoding = null;
		this.filterConfig = null;
	}

	protected String selectEncoding(ServletRequest request) {
		return (this.encoding);
	}

}
