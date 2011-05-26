/**
 * License
 * ProxyToys is open source software, made available under a BSD license.
 * 
 * Copyright (c) 2003-2005, 2009, 2010 Thoughtworks Ltd. 
 * Written by Dan North, Aslak Hellesøy, Jörg Schaible, Paul Hammant.
 * 
 * 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. Redistributions in binary form must reproduce
 * the above copyright notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of proxytoys nor the names of its contributors may be used to endorse
 * or promote products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 * 
 */

/*
 * Created on 11-May-2004
 *
 * (c) 2003-2005 ThoughtWorks
 *
 * See license.txt for license details
 */

package org.lingcloud.molva.ocl.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper class for introspecting interface and class hierarchies.
 * 
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @since 0.2
 */
public class ReflectionUtils {
	/**
	 * the {@link Object#equals(Object)} method.
	 */
	public static final Method EQUALS;

	/**
	 * the {@link Object#hashCode()} method.
	 */
	public static final Method HASH_CODE;

	/**
	 * the {@link Object#toString()} method.
	 */
	public static final Method TO_STRING;

	static {
		try {
			EQUALS = Object.class.getMethod("equals",
					new Class[] { Object.class });
			HASH_CODE = Object.class.getMethod("hashCode", null);
			TO_STRING = Object.class.getMethod("toString", null);
		} catch (NoSuchMethodException e) {
			// /CLOVER:OFF
			throw new InternalError();
			// /CLOVER:ON
		} catch (SecurityException e) {
			// /CLOVER:OFF
			throw new InternalError();
			// /CLOVER:ON
		}
	}

	/**
	 * Constructor. Do not call, it is a factory.
	 */
	private ReflectionUtils() {
	}

	/**
	 * Get all the interfaces implemented by a list of objects.
	 * 
	 * @param objects
	 *            the list of objects to consider.
	 * @return an set of interfaces. The set may be empty
	 */
	public static Set getAllInterfaces(final Object[] objects) {
		final Set interfaces = new HashSet();
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] != null) {
				getInterfaces(objects[i].getClass(), interfaces);
			}
		}
		// interfaces.remove(InvokerReference.class);
		return interfaces;
	}

	/**
	 * Get all interfaces of the given type. If the type is a class, the
	 * returned set contains any interface, that is implemented by the class. If
	 * the type is an interface, the all superinterfaces and the interface
	 * itself are included.
	 * 
	 * @param clazz
	 *            type to explore.
	 * @return a {@link Set} with all interfaces. The set may be empty.
	 */
	public static Set getAllInterfaces(final Class clazz) {
		final Set interfaces = new HashSet();
		getInterfaces(clazz, interfaces);
		// interfaces.remove(InvokerReference.class);
		return interfaces;
	}

	private static void getInterfaces(Class clazz, final Set interfaces) {
		if (clazz.isInterface()) {
			interfaces.add(clazz);
		}
		// Class.getInterfaces will return only the interfaces that are
		// implemented by the current class. Therefore we must loop up
		// the hierarchy for the superclasses and the superinterfaces.
		while (clazz != null) {
			final Class[] implemented = clazz.getInterfaces();
			for (int i = 0; i < implemented.length; i++) {
				if (!interfaces.contains(implemented[i])) {
					getInterfaces(implemented[i], interfaces);
				}
			}
			clazz = clazz.getSuperclass();
		}
	}

	/**
	 * Get most common superclass for all given objects.
	 * 
	 * @param objects
	 *            the array of objects to consider.
	 * @return the superclass or <code>{@link Void Void.class}</code> for an
	 *         empty array.
	 */
	public static Class getMostCommonSuperclass(final Object[] objects) {
		Class clazz = null;
		boolean found = false;
		if (objects != null && objects.length > 0) {
			while (!found) {
				for (int i = 0; i < objects.length; i++) {
					found = true;
					if (objects[i] != null) {
						final Class currentClazz = objects[i].getClass();
						if (clazz == null) {
							clazz = currentClazz;
						}
						if (!clazz.isAssignableFrom(currentClazz)) {
							if (currentClazz.isAssignableFrom(clazz)) {
								clazz = currentClazz;
							} else {
								clazz = clazz.getSuperclass();
								found = false;
								break;
							}
						}
					}
				}
			}
		}
		if (clazz == null) {
			clazz = Object.class;
		}
		return clazz;
	}

	/**
	 * Convert the collection of class types to an array of class types.
	 * 
	 * @param collection
	 *            with class types
	 * @return an array of class types
	 */
	public static Class[] toClassArray(final Collection collection) {
		return (Class[]) collection.toArray(new Class[collection.size()]);
	}

	/**
	 * Get the method of the given type, that has matching parameter types to
	 * the given arguments.
	 * 
	 * @param type
	 *            the type
	 * @param methodName
	 *            the name of the method to search
	 * @param args
	 *            the arguments to match
	 * @return the matching {@link Method}
	 * @throws NoSuchMethodException
	 *             if no matching {@link Method} exists
	 * @since 0.2
	 */
	public static Method getMatchingMethod(final Class type,
			final String methodName, final Object[] args)
			throws NoSuchMethodException {
		final Object[] newArgs = args == null ? new Object[0] : (Object[]) args;
		final Method[] methods = type.getMethods();
		final Set possibleMethods = new HashSet();
		Method method = null;
		for (int i = 0; method == null && i < methods.length; i++) {
			if (methodName.equals(methods[i].getName())) {
				final Class[] argTypes = methods[i].getParameterTypes();
				if (argTypes.length == newArgs.length) {
					boolean exact = true;
					Method possibleMethod = methods[i];
					for (int j = 0; possibleMethod != null
							&& j < argTypes.length; j++) {
						final Class newArgType = newArgs[j] != null ? newArgs[j]
								.getClass() : Object.class;
						if ((argTypes[j].equals(byte.class) && newArgType
								.equals(Byte.class))
								|| (argTypes[j].equals(char.class) 
										&& newArgType.equals(Character.class))
								|| (argTypes[j].equals(short.class) 
										&& newArgType.equals(Short.class))
								|| (argTypes[j].equals(int.class) 
										&& newArgType.equals(Integer.class))
								|| (argTypes[j].equals(long.class) 
										&& newArgType.equals(Long.class))
								|| (argTypes[j].equals(float.class) 
										&& newArgType.equals(Float.class))
								|| (argTypes[j].equals(double.class) 
										&& newArgType.equals(Double.class))
								|| (argTypes[j].equals(boolean.class) 
										&& newArgType.equals(Boolean.class))) {
							exact = true;
						} else if (!argTypes[j].isAssignableFrom(newArgType)) {
							possibleMethod = null;
							exact = false;
						} else if (!argTypes[j].isPrimitive()) {
							if (!argTypes[j].equals(newArgType)) {
								exact = false;
							}
						}
					}
					if (exact) {
						method = possibleMethod;
					} else if (possibleMethod != null) {
						possibleMethods.add(possibleMethod);
					}
				}
			}
		}
		if (method == null && possibleMethods.size() > 0) {
			method = (Method) possibleMethods.iterator().next();
		}
		if (method == null) {
			final StringBuffer name = new StringBuffer(type.getName());
			name.append('.');
			name.append(methodName);
			name.append('(');
			for (int i = 0; i < newArgs.length; i++) {
				if (i != 0) {
					name.append(", ");
				}
				// FIXME here fix a NullPointerException bug. if somebody
				// passed a null parameter in the newArgs, there will be a
				// NullPointerException. fixed by Xiaoyi Lu at 2009-02-26.
				if (newArgs[i] != null) {
					name.append(newArgs[i].getClass().getName());
				} else {
					name.append("null");
				}
			}
			name.append(')');
			throw new NoSuchMethodException(name.toString());
		}
		return method;
	}

	/**
	 * Write a {@link Method} into an {@link ObjectOutputStream}.
	 * 
	 * @param out
	 *            the stream
	 * @param method
	 *            the {@link Method} to write
	 * @throws IOException
	 *             if writing causes a problem
	 * @since 1.2
	 */
	public static void writeMethod(final ObjectOutputStream out,
			final Method method) throws IOException {
		out.writeObject(method.getDeclaringClass());
		out.writeObject(method.getName());
		out.writeObject(method.getParameterTypes());
	}

	/**
	 * Read a {@link Method} from an {@link ObjectInputStream}.
	 * 
	 * @param in
	 *            the stream
	 * @return the read {@link Method}
	 * @throws IOException
	 *             if reading causes a problem
	 * @throws ClassNotFoundException
	 *             if class types from objects of the InputStream cannot be
	 *             found
	 * @throws InvalidObjectException
	 *             if the {@link Method} cannot be found
	 * @since 1.2
	 */
	public static Method readMethod(final ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		final Class type = (Class) in.readObject();
		final String name = (String) in.readObject();
		final Class[] parameters = (Class[]) in.readObject();
		try {
			return type.getMethod(name, parameters);
		} catch (final NoSuchMethodException e) {
			throw new InvalidObjectException(e.getMessage());
		}
	}

	public static Object getProperty(Object bean, String name) 
		throws Exception {
		if (bean == null) {
			throw new NullPointerException();
		}
		if (name == null) {
			return null;
		}

		Class cls = bean.getClass();
		Method method = cls.getMethod("get" + name, null);
		return method.invoke(bean, null);
	}

	public static void setProperty(Object bean, String name, Object value)
			throws Exception {
		if (bean == null) {
			throw new NullPointerException();
		}
		if (name == null) {
			return;
		}

		Class cls = bean.getClass();
		Method method = cls.getMethod("set" + name,
				new Class[] { Object.class });
		method.invoke(bean, new Object[] { value });
	}

	public static Object invoke(Object object, String methodName, Object[] args)
			throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		if (object == null) {
			throw new NullPointerException();
		}
		Method method = getMatchingMethod(object.getClass(), methodName, args);
		return method.invoke(object, args);
	}

}
