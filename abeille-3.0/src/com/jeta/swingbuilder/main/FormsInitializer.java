/*
 * Copyright (C) 2005 Jeff Tassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeta.swingbuilder.main;

import java.io.File;
import java.util.Locale;

import com.jeta.forms.logger.FormsLogger;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.app.AppResourceLoader;
import com.jeta.swingbuilder.app.ApplicationStateStore;
import com.jeta.swingbuilder.app.UserPropertiesStore;
import com.jeta.swingbuilder.common.ComponentNames;
import com.jeta.swingbuilder.debug.DebugLogger;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.interfaces.app.ObjectStore;
import com.jeta.swingbuilder.interfaces.resources.ResourceLoader;

/**
 * This class is responsible for initializing all components needed for the
 * Abeille Forms application.
 * 
 * @author Jeff Tassin
 */
public class FormsInitializer {

	/**
	 * Initializes the application
	 * 
	 * @param args
	 */
	public static void initialize(String[] args) {
		try {
			/** for Mac OS X menu bar integration */
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Abeille Forms Designer");

			if (!FormDesignerUtils.isDebug()) {
				System.setOut(new java.io.PrintStream(new EmptyStream()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// now try to load properties
		try {
			String tshome = null;
			String language = null;
			String country = null;
			if (args != null) {
				for (int x = 0; x < args.length; x++) {
					try {
						if (args[x].equals("-language"))
							language = args[x + 1];
						else if (args[x].equals("-country"))
							country = args[x + 1];
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (tshome == null) {
				tshome = System.getProperty("user.home");
				if (tshome != null && tshome.length() > 0) {
					char c = tshome.charAt(tshome.length() - 1);
					if (c != '\\' && c != '/')
						tshome = tshome + File.separatorChar + ".abeilleforms13";
					else
						tshome = tshome + ".abeilleforms13";
				}
			}
			/**
			 * Checks that the home directory is present. If not, tries to create it.
			 */
			boolean bresult = false;
			try {
				File f = new File(tshome);
				f.mkdir();
				bresult = true;
			} catch (Exception e) {
				
			}
			if (bresult) {
				/** the jeta framework */
				AppResourceLoader loader = new AppResourceLoader(tshome, "jeta.resources/images/");
				JETARegistry.rebind(ResourceLoader.COMPONENT_ID, loader);

				/**
				 * Initialize the components needed by the forms sub-system.
				 */
				com.jeta.forms.defaults.DefaultInitializer.initialize();
				/**
				 * Gets the language and country from the command line and sets as the
				 * default locale
				 */
				Locale locale = null;
				if (language != null && country != null)
					locale = new Locale(language, country);
				else
					locale = Locale.getDefault();

				com.jeta.open.i18n.I18NHelper oi18n = com.jeta.open.i18n.I18NHelper.getInstance();
				oi18n.setLocale(locale);
				oi18n.loadBundle("com.jeta.swingbuilder.resources.MessagesBundle");

				// load global application user settings
				ApplicationStateStore appstore = new ApplicationStateStore("application");
				JETARegistry.rebind(ComponentNames.APPLICATION_STATE_STORE, appstore);
				System.setProperty("abeilleforms.home", tshome);
				System.setProperty("abeilleforms.version", com.jeta.forms.support.AbeilleForms.getVersionEx());

				/**
				 * Initialize the components needed by the swing builder system.
				 */
				com.jeta.swingbuilder.defaults.DefaultInitializer.initialize();
				/**
				 * 
				 */
				UserPropertiesStore ups = new UserPropertiesStore();
				ups.startup();
			}
			else {
				System.out.println("Unable to establish home directory: " + tshome);
				System.exit(0);
			}

			FormDesignerUtils.getEnvVars(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		FormsLogger.debug("FormsInitializer.completed.... ");

	}
	
	public static void shutdown() {
		try {
			try {
				Object obj = JETARegistry.lookup(UserPropertiesStore.COMPONENT_ID);
				if (obj instanceof UserPropertiesStore) {
					UserPropertiesStore userstore = (UserPropertiesStore) obj;
					userstore.shutdown();
				}

				// save the main application state
				ObjectStore os = (ObjectStore) JETARegistry.lookup(ComponentNames.APPLICATION_STATE_STORE);
				os.flush();
			} catch (Exception e) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}


	/**
	 * Trap all system.out
	 */
	private static class EmptyStream extends java.io.OutputStream {
		public void write(byte[] b) throws java.io.IOException {
			// ignore
		}

		public void write(int ival) throws java.io.IOException {
			// ignore
		}
	}
	

}
