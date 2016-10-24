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

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.swingbuilder.gui.main.MainPanel;
import com.jeta.swingbuilder.gui.main.Splash;

/**
 * This is the main launcher class for the application.
 * 
 * @author Jeff Tassin
 */
public class AbeilleForms {

	private Splash m_splash;

	public AbeilleForms() {
	}

	/**
	 * Launches the application and optionally displays the frame window. This
	 * method is needed for command line utilities that depend on the designer
	 * platform but don't show the main frame window.
	 * 
	 * @param args
	 *            command line arguments
	 * @param showSplash
	 *            true if the main frame is displayed. False otherwise.
	 */
	public void launch(String[] args, boolean showSplash) {
		try {
			if (showSplash)
				m_splash = new Splash();

			FormsInitializer.initialize(args);

			launchFrame(showSplash);
			//launchDialog(showSplash);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Launched base components needed by the rest of the application
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void launchFrame(boolean showSplash) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		if (showSplash && m_splash != null) {
			m_splash.dispose();
		}

		JFrame frame = new JFrame();
		frame.setTitle(com.jeta.forms.support.AbeilleForms.getAbeilleTitle());
		frame.setSize(1024, 768);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().add(new MainPanel());
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		});
	
	}

	private void launchDialog(boolean showSplash) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		if (showSplash && m_splash != null) {
			m_splash.dispose();
		}

		JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, null, true);
		dlg.setTitle(com.jeta.forms.support.AbeilleForms.getAbeilleTitle());
		dlg.setPrimaryPanel(new MainPanel());
		Dimension size = dlg.getPreferredSize();
		size.setSize(size.getWidth(), size.getHeight() + 20);
		dlg.setSize(size);
		dlg.showOkButton(false);
		dlg.getCloseButton().setVisible(false);
		dlg.setResizable(true);
		dlg.showCenter();

	}

	
	
	
}
