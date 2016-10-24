/**
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

package com.jeta.swingbuilder.gui.beanmgr;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.ListOrderedMap;

import com.jeta.forms.beanmgr.BeanManager;
import com.jeta.forms.components.border.TitledBorderBottom;
import com.jeta.forms.components.border.TitledBorderLabel;
import com.jeta.forms.components.border.TitledBorderSide;
import com.jeta.forms.components.colors.JETAColorWell;
import com.jeta.forms.components.label.JETALabel;
import com.jeta.forms.components.line.HorizontalLineComponent;
import com.jeta.forms.components.line.VerticalLineComponent;
import com.jeta.forms.gui.beans.JETABeanFactory;
import com.jeta.forms.gui.common.FormException;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.resources.Icons;
import com.jeta.swingbuilder.store.RegisteredBean;

/**
 * The bean manager is responsible for managing imported beans in the builder.
 * 
 * @author Jeff Tassin
 */
public class DefaultBeanManager implements BeanManager {
	/**
	 * The class loader for the beans
	 */
	private BeanLoader m_loader;

	/**
	 * A collection of DefaultBean objects. These are the standard Swing
	 * components supported by the designer.
	 */
	private OrderedMap m_default_beans = new ListOrderedMap();

	/**
	 * ctor
	 */
	public DefaultBeanManager() {
		registerDefaultBeans();
	}

	/**
	 * @return the underlying class loader for loading imported beans. This can
	 *         be null
	 */
	public ClassLoader getClassLoader() throws FormException {
		return getBeanLoader().getClassLoader();
	}

	public BeanLoader getBeanLoader() {
		if (m_loader == null) {
			m_loader = new BeanLoader();
		}
		return m_loader;
	}

	public boolean containsBean(String beanID){
		return m_default_beans.containsKey(beanID);
	}
	/**
	 * @return the class for the given bean class name
	 */
	public Class getBeanClass(String beanID) throws FormException {
		if(m_default_beans.containsKey(beanID)){
			RegisteredBean rbean = (RegisteredBean) m_default_beans.get(beanID);
			return getBeanLoader().getClass(rbean.getClassName());
		}
		return null;
	}

	/**
	 * @return the default beans supported by the application.
	 */
	public Collection getBeans() {
		Collection beans = new LinkedList();
		for (Object key : m_default_beans.keySet()) {
			RegisteredBean rbean = (RegisteredBean) m_default_beans.get(key.toString());
			beans.add(rbean);
		}
		return beans;
	}
	
	public RegisteredBean getRegisteredBean(String beanID){
		if(m_default_beans.containsKey(beanID)){
			return (RegisteredBean) m_default_beans.get(beanID);
		}
		return null;
	}

	/**
	 * Registers a default bean used by the designer.
	 */
	private DefaultBean registerDefaultBean(String description, String className, Icon icon) {
		return registerDefaultBean(className,description, className, icon, null,null,null);
	}
	
	public DefaultBean registerDefaultBean(String id,String description, String className, Icon icon,Properties custom,Properties initlize,Properties codeOnly) {
		DefaultBean rbean = new DefaultBean(id,description, className, icon, custom,initlize,codeOnly);
		m_default_beans.put(id, rbean);
		return rbean;
	}
	public DefaultBean registerDefaultBean(String id,String description, String className, Icon icon, Class[] params, Object[] args,Properties custom,Properties initlize,Properties codeOnly) {
		DefaultBean rbean = new DefaultBean(id,description, className, icon, params,args,custom,initlize,codeOnly);
		m_default_beans.put(id, rbean);
		return rbean;
	}
	public void tryRegisterCustomFactory(DefaultBean rbean, boolean scrollable) throws FormException{
		JETABeanFactory.tryRegisterCustomFactory(rbean, scrollable);
	}
	
	public void registerCustomDefaultBean(String id,String description, String className, Icon icon,Properties custom,Properties initlize,Properties codeOnly, boolean scrollable) throws FormException {
		DefaultBean rbean = registerDefaultBean(id,description, className, icon, custom,initlize,codeOnly);
		tryRegisterCustomFactory(rbean,scrollable);
	}
	public void registerCustomDefaultBean(String id,String description, String className, Icon icon, Class[] params, Object[] args,Properties custom,Properties initlize,Properties codeOnly, boolean scrollable) throws FormException {
		DefaultBean rbean = registerDefaultBean(id,description, className, icon, params,args,custom,initlize,codeOnly);
		tryRegisterCustomFactory(rbean,scrollable);
	}
	
	
	/**
	 * Loads the default Swing components supported by the application
	 */
	private void registerDefaultBeans() {
		registerDefaultBean(I18N.getLocalizedMessage("JLabel"), JETALabel.class.getName(), FormDesignerUtils.loadImage(Icons.LABEL_16));
		registerDefaultBean(I18N.getLocalizedMessage("JRadioButton"), JRadioButton.class.getName(), FormDesignerUtils.loadImage(Icons.RADIO_16));
		registerDefaultBean(I18N.getLocalizedMessage("JCheckBox"), JCheckBox.class.getName(), FormDesignerUtils.loadImage(Icons.CHECK_16));
		registerDefaultBean(I18N.getLocalizedMessage("JButton"), JButton.class.getName(), FormDesignerUtils.loadImage(Icons.BUTTON_16));
		registerDefaultBean(I18N.getLocalizedMessage("JToggleButton"), JToggleButton.class.getName(), FormDesignerUtils.loadImage(Icons.TOGGLE_BUTTON_16));
		registerDefaultBean(I18N.getLocalizedMessage("JComboBox"), JComboBox.class.getName(), FormDesignerUtils.loadImage(Icons.COMBO_16));
		registerDefaultBean(I18N.getLocalizedMessage("JList"), JList.class.getName(), FormDesignerUtils.loadImage(Icons.LIST_16));
		registerDefaultBean(I18N.getLocalizedMessage("JTable"), JTable.class.getName(), FormDesignerUtils.loadImage(Icons.TABLE_16));
		registerDefaultBean(I18N.getLocalizedMessage("JTree"), JTree.class.getName(), FormDesignerUtils.loadImage(Icons.TREE_16));
		registerDefaultBean(I18N.getLocalizedMessage("JProgressBar"), JProgressBar.class.getName(), FormDesignerUtils.loadImage(Icons.PROGRESS_BAR_16));
		registerDefaultBean(I18N.getLocalizedMessage("JSlider"), JSlider.class.getName(), FormDesignerUtils.loadImage(Icons.SLIDER_16));
		registerDefaultBean(I18N.getLocalizedMessage("JSpinner"), JSpinner.class.getName(), FormDesignerUtils.loadImage(Icons.SPINNER_16));
		registerDefaultBean(I18N.getLocalizedMessage("JTextField"), JTextField.class.getName(), FormDesignerUtils.loadImage(Icons.TEXT_FIELD_16));
		registerDefaultBean(I18N.getLocalizedMessage("JPasswordField"), JPasswordField.class.getName(), FormDesignerUtils.loadImage(Icons.PASSWORD_FIELD_16));
		registerDefaultBean(I18N.getLocalizedMessage("JFormattedTextField"), JFormattedTextField.class.getName(), FormDesignerUtils.loadImage(Icons.FORMATTED_FIELD_16));
		registerDefaultBean(I18N.getLocalizedMessage("JTextArea"), JTextArea.class.getName(), FormDesignerUtils.loadImage(Icons.TEXT_16));
		registerDefaultBean(I18N.getLocalizedMessage("JEditorPane"), JEditorPane.class.getName(), FormDesignerUtils.loadImage(Icons.RICH_TEXT_16));
		registerDefaultBean(I18N.getLocalizedMessage("JTabbedPane"), JTabbedPane.class.getName(), FormDesignerUtils.loadImage(Icons.TABPANE_16));
		
		registerDefaultBean(ID_EMBEDDED_FORM_COMPONENT,I18N.getLocalizedMessage("Embedded Form"), null, FormDesignerUtils.loadImage(Icons.EMBEDDED_FORM_16),null,null,null);
		//registerDefaultBean(ID_LINKED_FORM_COMPONENT,I18N.getLocalizedMessage("LinkedForm"), null, FormDesignerUtils.loadImage(Icons.LINKED_FORM_16),null,null,null);
		registerDefaultBean(ID_GENERIC_COMPONENT,I18N.getLocalizedMessage("Generic Component"), null, FormDesignerUtils.loadImage(Icons.GENERIC_COMPONENT_16),null,null,null);

		registerDefaultBean(I18N.getLocalizedMessage("Horizontal Line"), HorizontalLineComponent.class.getName(), FormDesignerUtils.loadImage(Icons.HORIZONTAL_LINE_16));
		registerDefaultBean(I18N.getLocalizedMessage("Vertical Line"), VerticalLineComponent.class.getName(), FormDesignerUtils.loadImage(Icons.VERTICAL_LINE_16));
		registerDefaultBean(I18N.getLocalizedMessage("Color Well"), JETAColorWell.class.getName(), FormDesignerUtils.loadImage(Icons.COLOR_WELL_16));
		registerDefaultBean(I18N.getLocalizedMessage("Image"), com.jeta.forms.components.image.ImageComponent.class.getName(), FormDesignerUtils.loadImage(Icons.PORTRAIT_16));
		registerDefaultBean(I18N.getLocalizedMessage("Titled Border Label"), TitledBorderLabel.class.getName(), FormDesignerUtils.loadImage(Icons.TITLE_BORDER_LABEL_16));
		registerDefaultBean(I18N.getLocalizedMessage("Titled Border Bottom"), TitledBorderBottom.class.getName(), FormDesignerUtils.loadImage(Icons.TITLE_BORDER_BOTTOM_16));
		registerDefaultBean(I18N.getLocalizedMessage("Titled Border Side"), TitledBorderSide.class.getName(), FormDesignerUtils.loadImage(Icons.TITLE_BORDER_SIDE_16));
		registerDefaultBean(I18N.getLocalizedMessage("JGoodies Separator"), com.jeta.forms.components.separator.TitledSeparator.class.getName(),FormDesignerUtils.loadImage(Icons.JGOODIES_SEPARATOR_16));
	}



}
