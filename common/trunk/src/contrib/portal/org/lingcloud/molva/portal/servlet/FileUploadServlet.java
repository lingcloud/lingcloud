/*
 *  @(#)FileUploadServlet.java  2010-5-27
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


package org.lingcloud.molva.portal.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.xmm.vam.util.VAMConfig;

public class FileUploadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String type = req.getParameter("type");
		log.info("upload file type" + type);
		Enumeration e = req.getParameterNames();
		String paramName = null;
		while (e.hasMoreElements()) {
			paramName = (String) e.nextElement();
			String values[] = req.getParameterValues(paramName);
			for (int i = 0; i < values.length; i++) {
				log.info(values[i].toString());
			}
		}
		if (type != null) {
			String uploadDir = null;

			if (type.equals("application")) {
				uploadDir = VAMConfig.getAppUploadDirLocation();
			} else if (type.equals("disk")) {
				uploadDir = VAMConfig.getDiskUploadDirLocation();
			} else if (type.equals("disc")) {
				uploadDir = VAMConfig.getDiscUploadDirLocation();
			}
			log.info("User uploaded a file to " + uploadDir);
			try {
				if (uploadDir != null) {
					File dir = new File(uploadDir);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					List fileList = null;
					DiskFileItemFactory fac = new DiskFileItemFactory();
					fac.setRepository(dir);
					log.info(fac.getRepository());
					fac.setSizeThreshold(10240);
					ServletFileUpload upload = new ServletFileUpload(fac);

					upload.setSizeMax(81920000000l);
					upload.setHeaderEncoding("utf-8");
					try {

						fileList = upload.parseRequest(req);
					} catch (FileUploadException ex) {
						log.info(ex.getMessage());
						return;
					}

					Iterator<FileItem> it = fileList.iterator();

					while (it.hasNext()) {

						FileItem item = it.next();

						if (!item.isFormField()) {
							File tempFile = new File(uploadDir + "/"
									+ item.getName());
							log.info(uploadDir + "/" + item.getName());
							item.write(tempFile);

						}

					}
				}
			} catch (Exception ex) {
				log.info(ex.getMessage());
			}
		}

		resp.getWriter().write("true");
	}
}
