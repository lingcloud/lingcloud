/*******************************************************************************
 * Copyright 2002-2010, OpenNebula Project Leads (OpenNebula.org)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.lingcloud.molva.xmm.vmc.one;

import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Represents a generic element of a Pool in XML format.
 * 
 */
public abstract class PoolElement {

	private static XPath xpath;

	// In order to make sure id >= 0, we init its value as -1.
	// Xiaoyi added at 2010-12-19, this is a very good and important trick for
	// fault tolerance.
	private int id = -1;

	

	private Node xml;

	// This field is added by Xiaoyi Lu for saving one's response message.
	private String info;

	private Client client;

	
	/**
	 * Returns the element's ID.
	 * 
	 * @return the element's ID.
	 */
	public String getId() {
		return Integer.toString(id);
	}
	
	public int getID() {
		return id;
	}

	/**
	 * Returns the element's name.
	 * 
	 * @return the element's name.
	 */
	public String getName() {
		return xpath("name");
	}
	
	public String getInfo() {
		return this.info;
	}
	
	public Client getClient() {
		return client;
	}
	
	/**
	 * Creates a new PoolElement with the specified attributes.
	 * 
	 * @param id
	 *            Id of the element.
	 * @param client
	 *            XML-RPC Client.
	 */
	protected PoolElement(int id, Client client) {
		if (xpath == null) {
			XPathFactory factory = XPathFactory.newInstance();
			xpath = factory.newXPath();
		}

		this.id = id;
		this.client = client;
	}

	/**
	 * Creates a new PoolElement from the xml provided.
	 * 
	 * @param client
	 *            XML-RPC Client.
	 * @param xmlElement
	 *            XML representation of the element.
	 */
	protected PoolElement(Node xmlElement, Client client) {
		if (xpath == null) {
			XPathFactory factory = XPathFactory.newInstance();
			xpath = factory.newXPath();
		}

		this.xml = xmlElement;
		this.client = client;
		this.id = Integer.parseInt(xpath("id"));
	}

	// Added by Xiaoyi Lu to construct a PoolElement object by oneresponse
	// message.
	protected PoolElement(String info, Client client)
			throws XPathExpressionException {
		if (xpath == null) {
			XPathFactory factory = XPathFactory.newInstance();
			xpath = factory.newXPath();
		}
		this.info = info;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(info
					.getBytes()));
			xml = doc.getDocumentElement();
		} catch (Exception e) {
		}
		this.client = client;
		String idstr = null;
		try {
			idstr = xpath.evaluate("id".toUpperCase(), xml);
		} catch (XPathExpressionException e) {
		}
		if (idstr != null) {
			this.id = Integer.parseInt(idstr);
		}
	}

	/**
	 * After a *.info call, this method builds the internal xml representation
	 * of the pool.
	 * 
	 * @param info
	 *            The XML-RPC *.info response
	 */
	protected void processInfo(OneResponse info) {
		if (info.isError()) {
			return;
		}
		// Added by Xiaoyi Lu for saving One's response message.
		this.info = info.getMessage();
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(info
					.getMessage().getBytes()));

			xml = doc.getDocumentElement();

		} catch (Exception e) {
		}
	}

	

	/**
	 * Performs an xpath evaluation for the "state" expression.
	 * 
	 * @return The value of the STATE element.
	 */
	public int state() {
		String state = xpath("state");
		if (state != null) {
			return Integer.parseInt(state);
		}
		return -1;
	}

	/**
	 * Evaluates an XPath expression and returns the result as a String. If the
	 * internal xml representation is not built, returns null. The subclass
	 * method info() must be called before.
	 * 
	 * @param expression
	 *            The XPath expression.
	 * @return The String that is the result of evaluating the expression and
	 *         converting the result to a String. Null if the internal xml
	 *         representation is not built.
	 */
	public String xpath(String expression) {
		String result = null;

		try {
			result = xpath.evaluate(expression.toUpperCase(), xml);
		} catch (XPathExpressionException e) {
		}

		return result;
	}

	
}
