/**
 * 
 */
package com.jeta.sample;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.apache.commons.io.IOUtils;

import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.codegen.builder.PropertyWriterFactory;
import com.jeta.swingbuilder.config.ConfigHelper;
import com.jeta.swingbuilder.gui.main.MainPanelPlugin;
import com.jeta.swingbuilder.gui.project.UserPreferencesNames;
import com.jeta.swingbuilder.gui.properties.PropertyTableModel;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.interfaces.userprops.TSUserPropertiesUtils;
import com.jeta.swingbuilder.main.FormsInitializer;

/**
 * @author Wolcen
 *
 */
public class SampleLauncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("jeta1.debug","true");
		
		FormDesignerUtils.setTest(true);
		FormDesignerUtils.setSimple(true);
		//FormDesignerUtils.setFixed(true, 5, 8, 90, 5, 8, 23);

		
		FormsInitializer.initialize(args);
		
		sampleInitialize();
		
		SampleAbeillePanel primaryPanel = new SampleAbeillePanel();
		
		String abeilleTitle = com.jeta.forms.support.AbeilleForms.getAbeilleTitle() +" for Sample";
		
		JFrame frame = new JFrame();
		frame.setTitle(abeilleTitle);
		frame.setSize(1024, 768);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().add(primaryPanel);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		});
		
		/*
		JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, null, true);
		dlg.setTitle(abeilleTitle);
		dlg.setPrimaryPanel(primaryPanel);
		Dimension size = dlg.getPreferredSize();
		size.setSize(size.getWidth(), size.getHeight() + 20);
		dlg.setSize(size);
		dlg.showOkButton(false);
		dlg.getCloseButton().setVisible(false);
		dlg.setResizable(true);
		dlg.showCenter();
		*/
		
		
	}
	
	
	public static void sampleInitialize(){
		String xmlstring1;
		try {
			xmlstring1 = IOUtils.toString(SampleLauncher.class.getResourceAsStream("/com/jeta/sample/SampleBeans.xml"));
			ConfigHelper.registerBeansFromXmlString(xmlstring1);
			xmlstring1 = IOUtils.toString(SampleLauncher.class.getResourceAsStream("/com/jeta/sample/SampleToolbar.xml"));
			ConfigHelper.registerToolbarFromXmlString(xmlstring1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		PropertyTableModel.registerCustomPropertyEditors("test_color@Color", Color.class,SampleColorEditor.class);
		
		JETARegistry.rebind(PropertyWriterFactory.COMPONENT_ID, new SamplePropertyWriterFactory());
		JETARegistry.rebind(MainPanelPlugin.COMPONENT_ID, new SampleMainPanelPlugin());
		
		TSUserPropertiesUtils.setString(UserPreferencesNames.ID_CLASS_NAME, "MyView");
		TSUserPropertiesUtils.setString(UserPreferencesNames.ID_CLASS_EXTENDS, "javax.swing.JPanel");
		TSUserPropertiesUtils.setString(UserPreferencesNames.ID_CLASS_IMPLMENTS, "");
		TSUserPropertiesUtils.setString(UserPreferencesNames.ID_SOURCE_BUILDER, "com.jeta.sample.SampleSourceBuilder");
		
		
	}
		
	
}
