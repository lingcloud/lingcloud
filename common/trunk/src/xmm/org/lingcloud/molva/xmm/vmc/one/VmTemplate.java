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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * A helper class that allows setting up a VM template programmatically.
 * <p>
 * This class is not thread-safe
 */
public class VmTemplate {

	public static final String NAME = "NAME";

	public static final String CPU = "CPU";

	public static final String MEMORY = "MEMORY";

	public static final String OS = "OS";

	public static final String OS_KERNEL = "kernel";

	public static final String OS_INITRD = "initrd";

	public static final String OS_ROOT = "root";

	public static final String OS_KERNEL_CMD = "kernel_cmd";

	public static final String OS_BOOT = "boot";

	public static final String FEATURES = "FEATURES";

	public static final String DISK = "DISK";

	public static final String DISK_TYPE = "type";

	public static final String DISK_SOURCE = "source";

	public static final String DISK_TARGET = "target";

	public static final String DISK_BUS = "bus";

	public static final String DISK_RO = "readonly";

	public static final String NIC = "NIC";

	public static final String NIC_BRIDGE = "bridge";

	public static final String NIC_TARGET = "target";

	public static final String NIC_MAC = "mac";

	public static final String NIC_SCRIPT = "script";

	public static final String INPUT = "INPUT";

	public static final String GRAPHICS = "GRAPHICS";

	public static final String RAW = "RAW";

	public static final String REQUIREMENTS = "REQUIREMENTS";

	public static final String RANK = "RANK";

	private static final String[] ALLOWS_ONE_VALUE = new String[] { OS };

	private SortedMap<String, String> simpleAttributes;

	/** This a multi-map. */
	private SortedMap<String, Set<Map<String, String>>> vectorAttributes;

	private boolean hvmTag = false;

	private static final String CONTEXT_FOR_XEN = 
			"import os, re\narch = os.uname()[4]\n"
			+ "if re.search('64', arch):\n  arch_libdir = 'lib64'\n"
			+ "else:\n  arch_libdir = 'lib'\n";

	public VmTemplate() {
		vectorAttributes = new TreeMap<String, Set<Map<String, String>>>();
		simpleAttributes = new TreeMap<String, String>();
	}

	/**
	 * Copy constructor. Performs a deep cloning of the template
	 * 
	 * @param toClone
	 */
	public VmTemplate(VmTemplate toClone) {

		simpleAttributes = new TreeMap<String, String>(
				toClone.simpleAttributes);

		TreeMap<String, Set<Map<String, String>>> newVectorAttributes = 
			new TreeMap<String, Set<Map<String, String>>>();

		for (Entry<String, Set<Map<String, String>>> entryToClone 
				: toClone.vectorAttributes.entrySet()) {
			final Set<Map<String, String>> currentSet = entryToClone.getValue();
			Set<Map<String, String>> newAttributeSet = 
				new LinkedHashSet<Map<String, String>>(1);
			for (Map<String, String> vectorAttributeToClone : currentSet) {
				final Map<String, String> attClone = 
					new HashMap<String, String>(vectorAttributeToClone);
				newAttributeSet.add(attClone);
			}
			newVectorAttributes.put(entryToClone.getKey(), newAttributeSet);
		}
		vectorAttributes = newVectorAttributes;
	}

	public void newSimpleAttribute(String name, String value) {

		simpleAttributes.put(name, value);
	}

	/**
	 * Creates a new vector attribute.
	 * <p>
	 * If this attribute allows multiple values for a single name, a new entry
	 * will be created for the new attribute. On the other hand, if this
	 * attribute allows only a single value, the current attribute contained in
	 * this template will be replaced
	 * <p>
	 * This method returns a reference to map on which sub attributes must be
	 * inserted
	 * 
	 * @param name
	 *            name of the new attribute
	 * @return a map to which sub attributes must be inserted
	 */
	public void newVectorAttribute(String name, Map<String, String> value) {

		Set<Map<String, String>> s = vectorAttributes.get(name);
		if (s == null) {
			s = new LinkedHashSet<Map<String, String>>();
			vectorAttributes.put(name, s);
		}
		if (allowsOnlyOneValue(name)) {
			s.clear();
		}
		s.add(new HashMap<String, String>(value));
	}

	@Override
	public String toString() {

		StringBuilder b = new StringBuilder();

		if (this.hvmTag) {
			b.append("RAW=[data=\"" + CONTEXT_FOR_XEN + "\",type=\"xen\"]");
			b.append(System.getProperty("line.separator"));
		}

		for (Entry<String, String> att : simpleAttributes.entrySet()) {
			b.append(att.getKey()).append('=').append(att.getValue()).append(
					System.getProperty("line.separator"));
		}
		for (Entry<String, Set<Map<String, String>>> vectorAtt 
				: vectorAttributes.entrySet()) {

			for (Map<String, String> subAttribute : vectorAtt.getValue()) {
				b.append(vectorAtt.getKey()).append("=[");
				Iterator<Entry<String, String>> it = subAttribute.entrySet()
						.iterator();
				while (it.hasNext()) {
					Entry<String, String> entry = it.next();
					// Bug fixed for RAW attribute, change '"' to "'".
					b.append(entry.getKey()).append('=').append("\"").append(
							entry.getValue()).append("\"");
					if (it.hasNext()) {
						b.append(',');
					}
				}
				b.append(']').append(System.getProperty("line.separator"));
			}
		}
		return b.toString();
	}

	private static boolean allowsOnlyOneValue(String attName) {
		// Now in the array of ALLOWS_ONE_VALUE, there is only one value of
		// "OS", so it means only the OS configuration parameter of VM is
		// allowed one attribute, and the other may contain multi-attrbutes.
		// Comments added by Xiaoyi Lu at 2009.09.15.
		for (String a : ALLOWS_ONE_VALUE) {
			if (attName.equals(a)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result;
		if (simpleAttributes != null) {
			result += simpleAttributes.hashCode();
		}
		result = prime * result;
		if (vectorAttributes != null) {
			result += vectorAttributes.hashCode();
		}

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		VmTemplate other = (VmTemplate) obj;

		if (!simpleAttributes.equals(other.simpleAttributes)) {
			return false;
		}

		if (!vectorAttributes.equals(other.vectorAttributes)) {
			return false;
		}
		return true;
	}
	
	public void setForHVM() {
		this.hvmTag = true;
	}
}
