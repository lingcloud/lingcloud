/*
 *  @(#)XmlUtil.java  2010-5-27
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

package org.lingcloud.molva.xmm.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Some utilities for xml manipulation, all obvious and straightforward.
 * 
 * @author pxbl
 * @version $Id: XmlUtil.java,v 1.4 2007/10/03 08:07:00 zouyongqiang Exp $
 */
public final class XmlUtil {

	private static Log log = LogFactory.getLog(XmlUtil.class);

	private static DocumentBuilder builder;

	private static Transformer transformer;

	static {

		DocumentBuilderFactory documentFactory = DocumentBuilderFactory
				.newInstance();
		documentFactory.setCoalescing(true);
		documentFactory.setExpandEntityReferences(true);
		documentFactory.setIgnoringComments(true);
		documentFactory.setIgnoringElementContentWhitespace(false);
		documentFactory.setNamespaceAware(false);
		documentFactory.setValidating(false);
		try {
			builder = documentFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}

	}

	private XmlUtil() {

	}

	public static void output(Node node, OutputStream os) {
		Source source = new DOMSource(node);
		Result result = new StreamResult(os);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			if (log.isDebugEnabled()) {
				e.printStackTrace();
			}
		}
	}

	public static void output(Node node, Writer writer) {
		Source source = new DOMSource(node);
		Result result = new StreamResult(writer);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			if (log.isDebugEnabled()) {
				e.printStackTrace();
			}
		}
	}

	public static String toString(Node node) {
		try {
			StringWriter sw = new StringWriter();
			output(node, sw);
			sw.close();
			return sw.getBuffer().toString();
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public static Document build(File file) {
		try {
			return builder.parse(file);
		} catch (FileNotFoundException e) {
			if (log.isDebugEnabled()) {
				e.printStackTrace();
			}
			return null;
		} catch (SAXException e) {
			if (log.isDebugEnabled()) {
				e.printStackTrace();
			}
			return null;
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public static Document build(InputStream is) {
		try {
			return builder.parse(is);
		} catch (SAXException e) {
			if (log.isDebugEnabled()) {
				e.printStackTrace();
			}
			return null;
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public static Document build(Reader reader) {
		try {
			return builder.parse(new InputSource(reader));
		} catch (SAXException e) {
			if (log.isDebugEnabled()) {
				e.printStackTrace();
			}
			return null;
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public static Document build(String str) {
		return build(new StringReader(str));
	}

	public static String encodeBase64(byte[] data) {
		return new String(Base64.encodeBase64(data));
	}

	public static byte[] decodeBase64(String data) {
		return Base64.decodeBase64(data.getBytes());
	}

	// add toXml() and fromXml(). by Yongqiang Zou. 2007.1.24.
	private static final XStream XSTREAM = new XStream(new DomDriver());

	public static String toXml(Object obj) {
		if (obj == null) {
			return "";
		}
		return XSTREAM.toXML(obj);
	}

	public static Object fromXml(String xml) {
		if (XMMUtil.isEmptyString(xml)) {
			return null;
		}
		Object obj = XSTREAM.fromXML(xml);
		return obj;
	}
}
