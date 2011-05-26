/*
 * Copyright 2008 Federal Emergency Management Agency
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.lingcloud.molva.ocl.util;

import java.io.File;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

/**
 * Basic Hibernate helper class for Hibernate configuration and startup.
 * <p>
 * Uses a static initializer to read startup options and initialize
 * <tt>Configuration</tt> and <tt>SessionFactory</tt>.
 * <p>
 * This class also tries to figure out if JNDI binding of the
 * <tt>SessionFactory</tt> is used, otherwise it falls back to a global static
 * variable (Singleton). If you use this helper class to obtain a
 * <tt>SessionFactory</tt> in your code, you are shielded from these
 * deployment differences.
 * <p>
 * Another advantage of this class is access to the <tt>Configuration</tt>
 * object that was used to build the current <tt>SessionFactory</tt>. You can
 * access mapping metadata programmatically with this API, and even change it
 * and rebuild the <tt>SessionFactory</tt>.
 * <p>
 * Note: This class supports annotations if you replace the line that creates a
 * Configuration object.
 * <p>
 * Note: This class supports only one data store. Support for several
 * <tt>SessionFactory</tt> instances can be easily added (through a static
 * <tt>Map</tt>, for example). You could then lookup a
 * <tt>SessionFactory</tt> by its name.
 * 
 * @author Christian Bauer
 * @author Yongqiang Zou, modified at 2007.8.30.
 */
public abstract class HibernateUtil {

	private Log log = LogFactory.getLog(HibernateUtil.class);

	// Change all functions to a null-static method, and use singleton pattern
	// to allow be inherited and preserve the only once initlize.

	private Configuration configuration;

	private SessionFactory sessionFactory;

	/**
	 * Get the cfg.xml file name for the class so that to allow subclass to
	 * inherit from it.
	 * 
	 * @return the file name.
	 */
	protected abstract String getCfgXmlFileName();
	
	/**
	 * Get the cfg.xml file for the class so that to allow subclass to
	 * inherit from it.
	 * 
	 * @return the File.
	 */
	protected abstract File getCfgXmlFile();

	/**
	 * init the configuration object it if neeed.
	 */
	private void initConfiguration() {
		// Create the initial SessionFactory from the default configuration
		// files
		if (configuration == null) {
			try {
				log.debug("Initializing Hibernate");

				// Read hibernate.properties, if present
				configuration = new Configuration();
				// Use annotations: configuration = new
				// AnnotationConfiguration();

				// Read hibernate.cfg.xml (has to be present)
				configuration.configure(getCfgXmlFile());

				log.debug("Hibernate initialized, call "
						+ "HibernateUtil.getSessionFactory()");
			} catch (Throwable ex) {
				// We have to catch Throwable, otherwise we will miss
				// NoClassDefFoundError and other subclasses of Error
				log.error("Building SessionFactory failed.", ex);
				throw new ExceptionInInitializerError(ex);
			}
		}
	}

	/**
	 * Returns the Hibernate configuration that was used to build the
	 * SessionFactory.
	 * 
	 * @return Configuration
	 */
	public Configuration getConfiguration() {
		initConfiguration(); // Change the init point. Yongqiang Zou.
								// 2007.8.30
		return configuration;
	}

	/**
	 * Returns the global SessionFactory either from a static variable or a JNDI
	 * lookup.
	 * 
	 * @return SessionFactory
	 */
	public SessionFactory getSessionFactory() {
		initConfiguration(); // Change the init point. Yongqiang Zou.
								// 2007.8.30

		String sfName = configuration
				.getProperty(Environment.SESSION_FACTORY_NAME);
		if (sfName != null) {
			log.debug("Looking up SessionFactory in JNDI");
			try {
				return (SessionFactory) new InitialContext().lookup(sfName);
			} catch (NamingException ex) {
				throw new RuntimeException(ex);
			}
		} else if (sessionFactory == null) {
			rebuildSessionFactory();
		}
		return sessionFactory;
	}

	/**
	 * Closes the current SessionFactory and releases all resources.
	 * <p>
	 * The only other method that can be called on HibernateUtil after this one
	 * is rebuildSessionFactory(Configuration).
	 */
	public void shutdown() {
		log.debug("Shutting down Hibernate");
		// Close caches and connection pools
		getSessionFactory().close();

		// Clear static variables
		sessionFactory = null;
	}

	/**
	 * Rebuild the SessionFactory with the static Configuration.
	 * <p>
	 * Note that this method should only be used with static SessionFactory
	 * management, not with JNDI or any other external registry. This method
	 * also closes the old static variable SessionFactory before, if it is still
	 * open.
	 */
	public void rebuildSessionFactory() {
		log.debug("Using current Configuration to rebuild SessionFactory");
		initConfiguration(); // Change the init point. Yongqiang Zou.
								// 2007.8.30
		rebuildSessionFactory(configuration);
	}

	/**
	 * Rebuild the SessionFactory with the given Hibernate Configuration.
	 * <p>
	 * HibernateUtil does not configure() the given Configuration object, it
	 * directly calls buildSessionFactory(). This method also closes the old
	 * static variable SessionFactory before, if it is still open.
	 * 
	 * @param cfg
	 */
	public void rebuildSessionFactory(Configuration cfg) {
		log.debug("Rebuilding the SessionFactory from given Configuration");
		initConfiguration();

		if (sessionFactory != null && !sessionFactory.isClosed()) {
			sessionFactory.close();
		}
		if (cfg.getProperty(Environment.SESSION_FACTORY_NAME) != null) {
			log.debug("Managing SessionFactory in JNDI");
			cfg.buildSessionFactory();
		} else {
			log.debug("Holding SessionFactory in static variable");
			sessionFactory = cfg.buildSessionFactory();
		}
		configuration = cfg;
	}

}
