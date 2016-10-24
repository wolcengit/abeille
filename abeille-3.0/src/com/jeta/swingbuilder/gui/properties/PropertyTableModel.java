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

package com.jeta.swingbuilder.gui.properties;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.BeanDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

import com.jeta.forms.beanmgr.BeanManager;
import com.jeta.forms.gui.beans.DynamicBeanInfo;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.beans.JETAPropertyDescriptor;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.properties.BooleanProperty;
import com.jeta.forms.store.properties.ButtonGroupProperty;
import com.jeta.forms.store.properties.ColorProperty;
import com.jeta.forms.store.properties.ColorProperty2;
import com.jeta.forms.store.properties.CompoundBorderProperty;
import com.jeta.forms.store.properties.CompoundLineProperty;
import com.jeta.forms.store.properties.DoubleProperty;
import com.jeta.forms.store.properties.FloatProperty;
import com.jeta.forms.store.properties.FontProperty;
import com.jeta.forms.store.properties.FontProperty2;
import com.jeta.forms.store.properties.IntegerProperty;
import com.jeta.forms.store.properties.ItemsProperty;
import com.jeta.forms.store.properties.JETAProperty;
import com.jeta.forms.store.properties.LongProperty;
import com.jeta.forms.store.properties.OptionListProperty;
import com.jeta.forms.store.properties.ScrollBarsProperty;
import com.jeta.forms.store.properties.StringListProperty;
import com.jeta.forms.store.properties.StringProperty;
import com.jeta.forms.store.properties.TabbedPaneProperties;
import com.jeta.forms.store.properties.TransformOptionsProperty;
import com.jeta.forms.store.properties.effects.PaintProperty;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.commands.CommandUtils;
import com.jeta.swingbuilder.gui.commands.SetPropertyCommand;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jeta.swingbuilder.gui.properties.editors.BooleanEditor;
import com.jeta.swingbuilder.gui.properties.editors.BorderEditor;
import com.jeta.swingbuilder.gui.properties.editors.ButtonGroupEditor;
import com.jeta.swingbuilder.gui.properties.editors.ColorEditor;
import com.jeta.swingbuilder.gui.properties.editors.ComboEditor;
import com.jeta.swingbuilder.gui.properties.editors.DimensionEditor;
import com.jeta.swingbuilder.gui.properties.editors.FillEditor;
import com.jeta.swingbuilder.gui.properties.editors.FontEditor;
import com.jeta.swingbuilder.gui.properties.editors.IconEditor;
import com.jeta.swingbuilder.gui.properties.editors.ItemsEditor;
import com.jeta.swingbuilder.gui.properties.editors.LineEditor;
import com.jeta.swingbuilder.gui.properties.editors.NumericEditor;
import com.jeta.swingbuilder.gui.properties.editors.ScrollBarsEditor;
import com.jeta.swingbuilder.gui.properties.editors.StringEditor;
import com.jeta.swingbuilder.gui.properties.editors.TabbedPaneEditor;
import com.jeta.swingbuilder.gui.properties.editors.UnknownEditor;
import com.jeta.swingbuilder.store.RegisteredBean;

/**
 * TableModel for managing properties for a given bean.
 * 
 * @author Jeff Tassin
 */
public class PropertyTableModel extends AbstractTableModel {
	private JETAPropertyDescriptor[] m_descriptors;

	private BeanDescriptor m_beandescriptor;

	private DynamicBeanInfo m_beaninfo;

	private JETABean m_bean;
	private Properties m_customProperties = null;

	// Cached property editors.
	private static Hashtable m_prop_editors;
	
	// Custom property editors.
	private static Map m_custom_prop_editors = new HashMap();

	// Shared instance of a comparator
	private static DescriptorComparator comparator = new DescriptorComparator();

	private UnknownEditor m_unknown_editor = new UnknownEditor();

	private static final int NUM_COLUMNS = 2;

	public static final int COL_NAME = 0;

	public static final int COL_VALUE = 1;

	// Filter options
	public static final int VIEW_ALL = 0;

	public static final int VIEW_PREFERRED = 7;

	private int currentFilter = VIEW_PREFERRED;

	/**
	 * A list of PropertyEditorListeners that want to receive
	 * PropertyEditorEvents
	 */
	private LinkedList m_listeners = new LinkedList();

	public PropertyTableModel() {

		if (m_prop_editors == null) {
			m_prop_editors = new Hashtable();
			registerPropertyEditors();
		}
		setFilter(VIEW_ALL);
	}

	public PropertyTableModel(JETABean jbean) {
		this();
		setBean(jbean);
	}

	public void addPropertyListener(PropertyEditorListener listener) {
		m_listeners.add(listener);
	}

	/**
	 * Filters the table to display only properties with specific attributes.
	 * Will sort the table after the data has been filtered.
	 * 
	 * @param view
	 *            The properties to display.
	 */
	public void filterTable(int view) {
		if (m_beaninfo == null)
			return;

		Collection descriptors = m_beaninfo.getPropertyDescriptors();

		// Use collections to filter out unwanted properties
		ArrayList list = new ArrayList();
		list.addAll(descriptors);

		ListIterator iterator = list.listIterator();
		JETAPropertyDescriptor desc;
		while (iterator.hasNext()) {
			desc = (JETAPropertyDescriptor) iterator.next();

			switch (view) {
			case VIEW_ALL:
				if (desc.isHidden()) {
					iterator.remove();
				}
				break;

			case VIEW_PREFERRED:
				//System.out.println( "PropertyTableModel.filterProps: desc: " + desc.getName() + " pref: " + desc.isPreferred() );
				if(m_customProperties != null){
					if(!m_customProperties.containsKey(desc.getName())){
						iterator.remove();
					}
				}else{
					if (!desc.isPreferred() || desc.isHidden()) {
						iterator.remove();
					}
				}
				break;
			}
		}
		m_descriptors = (JETAPropertyDescriptor[]) list.toArray(new JETAPropertyDescriptor[list.size()]);
		fireTableDataChanged();
	}

	public void firePropertyEditorEvent(PropertyEditorEvent evt) {
		Iterator iter = m_listeners.iterator();
		while (iter.hasNext()) {
			PropertyEditorListener listener = (PropertyEditorListener) iter.next();
			listener.propertyChanged(evt);
		}
	}

	/**
	 * Sets the current filter of the Properties.
	 * 
	 * @param filter
	 *            one of VIEW_ constants
	 */
	public void setFilter(int filter) {
		this.currentFilter = filter;
		filterTable(currentFilter);
	}

	/**
	 * Returns the current filter type
	 */
	public int getFilter() {
		return currentFilter;
	}

	/**
	 * Return the current object that is represented by this model.
	 */
	public JETABean getBean() {
		return m_bean;
	}

	/**
	 * Get row count (total number of properties shown)
	 */
	public int getRowCount() {
		if (m_descriptors == null) {
			return 0;
		}
		return m_descriptors.length;
	}

	/**
	 * Get column count (2: name, value)
	 */
	public int getColumnCount() {
		return NUM_COLUMNS;
	}

	/**
	 * Check if given cell is editable
	 * 
	 * @param row
	 *            table row
	 * @param col
	 *            table column
	 */
	public boolean isCellEditable(int row, int col) {
		if (col == COL_VALUE) {
			boolean rw = getPropertyRW(row);
			return rw;
		}
		else {
			return false;
		}
	}

	/**
	 * Get text value for cell of table
	 * 
	 * @param row
	 *            table row
	 * @param col
	 *            table column
	 */
	public Object getValueAt(int row, int col) {
		Object value = null;

		if (col == COL_NAME) {
			value = m_descriptors[row].getDisplayName();
		}
		else {
			try {
				// COL_VALUE is handled
				JETAPropertyDescriptor dpd = getPropertyDescriptor(row);
				value = dpd.getPropertyValue(m_bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	/**
	 * Returns the Java type info for the property at the given row.
	 */
	public Class getPropertyType(int row) {
		return m_descriptors[row].getPropertyType();
	}
	public Class getPropertyTypeEx(int row) {
		Class clazz = m_descriptors[row].getPropertyType();
		if(IntegerProperty.class.equals(clazz))
			return Integer.class;
		else if(FloatProperty.class.equals(clazz))
			return Float.class;
		else if(LongProperty.class.equals(clazz))
			return Long.class;
		else if(DoubleProperty.class.equals(clazz))
			return Double.class;
		else if(StringProperty.class.equals(clazz))
			return String.class;
		else if(ColorProperty.class.equals(clazz))
			return Color.class;
		else if(ColorProperty2.class.equals(clazz))
			return Color.class;
		else if(FontProperty.class.equals(clazz))
			return Font.class;
		else if(FontProperty2.class.equals(clazz))
			return Font.class;
		else
			return clazz;
	}

	/**
	 * Returns the PropertyDescriptor for the row.
	 */
	public JETAPropertyDescriptor getPropertyDescriptor(int row) {
		return (JETAPropertyDescriptor) m_descriptors[row];
	}

	/**
	 * Returns a new instance of the property editor for a given class. If an
	 * editor is not specified in the property descriptor then it is looked up
	 * in the PropertyEditorManager.
	 */
	public PropertyEditor getPropertyEditor(int row) {
		PropertyEditor editor = null;
		
		String peid = m_descriptors[row].getName()+"@"+m_bean.getBeanID();
		if(m_custom_prop_editors.containsKey(peid)){
			try {
				editor = (PropertyEditor) ((Class)m_custom_prop_editors.get(peid)).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		if(editor != null){
			if(editor instanceof JETAPropertyEditor){
				((JETAPropertyEditor)editor).setCustom(isCustomProperties());
				((JETAPropertyEditor)editor).setEnabled(getPropertyRW(row));
			}
			return editor;
		}
		
		Class propertyType = getPropertyTypeEx(row);
		if(propertyType != null){
			peid = m_descriptors[row].getName()+"#"+propertyType.getName();
			if(m_custom_prop_editors.containsKey(peid)){
				try {
					editor = (PropertyEditor) ((Class)m_custom_prop_editors.get(peid)).newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			if(editor != null){
				if(editor instanceof JETAPropertyEditor){
					((JETAPropertyEditor)editor).setCustom(isCustomProperties());
					((JETAPropertyEditor)editor).setEnabled(getPropertyRW(row));
				}
				return editor;
			}
		}
		
		Class cls = m_descriptors[row].getPropertyEditorClass();
		if (cls != null) {

			try {
				editor = (PropertyEditor) cls.newInstance();
			} catch (Exception ex) {
				// XXX - debug
				System.out.println("PropertyTableModel: Instantiation exception creating PropertyEditor");
			}
		}
		else {

			// Look for a registered editor for this type.
			if (propertyType != null) {
				editor = (PropertyEditor) m_prop_editors.get(propertyType);
				if (editor == null) {
					// Load a shared instance of the property editor.
					editor = PropertyEditorManager.findEditor(propertyType);
					/**
					 * The property editor manager will return default editors
					 * for the Java primitives. Everything else is up to the
					 * application.
					 */

					if (editor != null)
						m_prop_editors.put(propertyType, editor);
				}

				if (editor == null) {
					// Use the editor for Object.class
					editor = (PropertyEditor) m_prop_editors.get(Object.class);
					if (editor == null) {
						editor = PropertyEditorManager.findEditor(Object.class);
						if (editor != null){
							// fix error for java.lang.InstantiationException: com.sun.beans.editors.EnumEditor
							if(!"com.sun.beans.editors.EnumEditor".equals(editor.getClass().getName()))
								m_prop_editors.put(Object.class, editor);
						}
					}

				}

				if (editor == null) {
					editor = m_unknown_editor;
				}


			}
		}
		if(editor != null){
			if(editor instanceof JETAPropertyEditor){
				((JETAPropertyEditor)editor).setCustom(isCustomProperties());
				((JETAPropertyEditor)editor).setEnabled(getPropertyRW(row));
			}
		}
		return editor;
	}

	/**
	 * Returns a flag indicating if the encapsulated object has a customizer.
	 */
	public boolean hasCustomizer() {
		if (m_beandescriptor != null) {
			Class cls = m_beandescriptor.getCustomizerClass();
			return (cls != null);
		}

		return false;
	}

	/**
	 * Gets the customizer for the current object.
	 * 
	 * @return New instance of the customizer or null if there isn't a
	 *         customizer.
	 */
	public Component getCustomizer() {
		Component customizer = null;

		if (m_beandescriptor != null) {
			Class cls = m_beandescriptor.getCustomizerClass();

			if (cls != null) {
				try {
					customizer = (Component) cls.newInstance();
				} catch (Exception ex) {
					System.out.println("PropertyTableModel: Instantiation exception creating Customizer");
				}
			}
		}

		return customizer;
	}

	/**
	 * Method which registers property editors for types.
	 */
	private static void registerPropertyEditors() {
		PropertyEditorManager.registerEditor(Font.class, FontEditor.class);
		PropertyEditorManager.registerEditor(Color.class, ColorEditor.class);
		PropertyEditorManager.registerEditor(Boolean.class, BooleanEditor.class);
		PropertyEditorManager.registerEditor(boolean.class, BooleanEditor.class);
		PropertyEditorManager.registerEditor(String.class, StringEditor.class);
		PropertyEditorManager.registerEditor(Dimension.class, DimensionEditor.class);
		PropertyEditorManager.registerEditor(Icon.class, IconEditor.class);

		PropertyEditorManager.registerEditor(byte.class, NumericEditor.ByteEditor.class);

		PropertyEditorManager.registerEditor(short.class, NumericEditor.ShortEditor.class);
		PropertyEditorManager.registerEditor(Short.class, NumericEditor.ShortEditor.class);

		PropertyEditorManager.registerEditor(int.class, NumericEditor.IntegerEditor.class);
		PropertyEditorManager.registerEditor(Integer.class, NumericEditor.IntegerEditor.class);

		PropertyEditorManager.registerEditor(long.class, NumericEditor.LongEditor.class);
		PropertyEditorManager.registerEditor(Long.class, NumericEditor.LongEditor.class);

		PropertyEditorManager.registerEditor(float.class, NumericEditor.FloatEditor.class);
		PropertyEditorManager.registerEditor(Float.class, NumericEditor.FloatEditor.class);

		PropertyEditorManager.registerEditor(double.class, NumericEditor.DoubleEditor.class);
		PropertyEditorManager.registerEditor(Double.class, NumericEditor.DoubleEditor.class);

		PropertyEditorManager.registerEditor(ButtonGroupProperty.class, ButtonGroupEditor.class);
		PropertyEditorManager.registerEditor(ItemsProperty.class, ItemsEditor.class);
		PropertyEditorManager.registerEditor(TransformOptionsProperty.class, ComboEditor.class);
		PropertyEditorManager.registerEditor(CompoundBorderProperty.class, BorderEditor.class);
		PropertyEditorManager.registerEditor(CompoundLineProperty.class, LineEditor.class);
		PropertyEditorManager.registerEditor(PaintProperty.class, FillEditor.class);
		PropertyEditorManager.registerEditor(ScrollBarsProperty.class, ScrollBarsEditor.class);
		PropertyEditorManager.registerEditor(TabbedPaneProperties.class, TabbedPaneEditor.class);

		PropertyEditorManager.registerEditor(IntegerProperty.class,NumericEditor.IntegerEditor.class);
		PropertyEditorManager.registerEditor(FloatProperty.class,NumericEditor.FloatEditor.class);
		PropertyEditorManager.registerEditor(LongProperty.class,NumericEditor.LongEditor.class);
		PropertyEditorManager.registerEditor(DoubleProperty.class,NumericEditor.DoubleEditor.class);
		PropertyEditorManager.registerEditor(BooleanProperty.class,BooleanEditor.class);
		PropertyEditorManager.registerEditor(StringProperty.class,StringEditor.class);
		
		PropertyEditorManager.registerEditor(ColorProperty.class,ColorEditor.class);
		PropertyEditorManager.registerEditor(ColorProperty2.class,ColorEditor.class);
		PropertyEditorManager.registerEditor(FontProperty.class,FontEditor.class);
		PropertyEditorManager.registerEditor(FontProperty2.class,FontEditor.class);

		PropertyEditorManager.registerEditor(StringListProperty.class, ComboEditor.class);
		PropertyEditorManager.registerEditor(OptionListProperty.class, ComboEditor.class);

	}
	public static void registerPropertyEditors(Class propClass,Class editorClass) {
		PropertyEditorManager.registerEditor(propClass, editorClass);
	}
	/**
	 * Method which registers property editors for types.
	 */
	public static void registerCustomPropertyEditors(String beanID,String prop,Class editorClass) {
		String peid = prop+"@"+beanID;
		m_custom_prop_editors.put(peid, editorClass);
	}
	public static void registerCustomPropertyEditors(String prop,Class propClass,Class editorClass) {
		String peid = prop+"#"+propClass.getName();
		m_custom_prop_editors.put(peid, editorClass);
	}
	
	/**
	 * Set the table model to represents the properties of the object.
	 */
	public void setBean(JETABean bean) {
		if (bean == m_bean) {
			fireTableDataChanged();
			return;
		}

		m_bean = bean;
		m_customProperties = null;
		if (m_bean != null){
			BeanManager bm = (BeanManager) JETARegistry.lookup(BeanManager.COMPONENT_ID);
			RegisteredBean rbean = bm.getRegisteredBean(m_bean.getBeanID());
			if(rbean != null){
				m_customProperties = rbean.getCustomProperties();
			}
		}

		if (m_bean == null || m_bean.getDelegate() == null) {
			if (m_descriptors != null && m_descriptors.length > 0) {
				m_descriptors = new JETAPropertyDescriptor[0];
				m_beaninfo = null;
				fireTableDataChanged();
			}
			return;
		}

		try {
			m_beaninfo = bean.getBeanInfo();
		} catch (Exception ex) {
			FormsLogger.severe(ex);
		}

		if (m_beaninfo != null) {
			m_beandescriptor = m_beaninfo.getBeanDescriptor();
			filterTable(getFilter());
		}
	}

	/**
	 * Set the value of the Values column.
	 */
	public void setValueAt(Object value, int row, int column) {
		if (column != COL_VALUE || m_descriptors == null || row > m_descriptors.length) {
			return;
		}

		try {
			Object old_value = getValueAt(row, column);
			if(!(value instanceof JETAProperty) && (old_value instanceof JETAProperty)){
				if(old_value instanceof BooleanProperty) old_value = ((BooleanProperty)old_value).getValue();
				else if(old_value instanceof ColorProperty) old_value = ((ColorProperty)old_value).getColor();
				else if(old_value instanceof ColorProperty2) old_value = ((ColorProperty2)old_value).getValue();
				else if(old_value instanceof DoubleProperty) old_value = ((DoubleProperty)old_value).getValue();
				else if(old_value instanceof FloatProperty) old_value = ((FloatProperty)old_value).getValue();
				else if(old_value instanceof FontProperty2) old_value = ((FontProperty2)old_value).getValue();
				else if(old_value instanceof IntegerProperty) old_value = ((IntegerProperty)old_value).getValue();
				else if(old_value instanceof LongProperty) old_value = ((LongProperty)old_value).getValue();
				else if(old_value instanceof StringProperty) old_value = ((StringProperty)old_value).getValue();
				else if(old_value instanceof StringListProperty) old_value = ((StringListProperty)old_value).getValue();
				else if(old_value instanceof OptionListProperty) old_value = ((OptionListProperty)old_value).getValue();
			}

			if (old_value == null && value == null)
				return;

			if (old_value == value)
				return;

			if (value != null && value.equals(old_value))
				return;

			if (old_value != null && old_value.equals(value))
				return;

			//System.out.println( "setValueAt new_value: " + value + " " +value.hashCode() + " old_value: " + old_value + " " +old_value.hashCode() +" ::" + value.getClass().getName());

			JETAPropertyDescriptor dpd = getPropertyDescriptor(row);
			SetPropertyCommand cmd = new SetPropertyCommand(dpd, m_bean, value, old_value, FormComponent.getParentForm(m_bean));
			CommandUtils.invoke(cmd, FormEditor.getEditor(m_bean));
			fireTableRowsUpdated(row, row);
			firePropertyEditorEvent(new PropertyEditorEvent(PropertyEditorEvent.BEAN_PROPERTY_CHANGED, m_bean));
		} catch (Exception e) {
			FormsLogger.severe(e);
		}
	}
	public boolean isCustomProperties() {
		return (m_customProperties != null);
	}
	
	public boolean getPropertyRW(int row) {
		boolean rw = true;
		if(m_customProperties != null){
			String key = m_descriptors[row].getName();
			if(m_customProperties.containsKey(key)){
				String value = m_customProperties.getProperty(key);
				rw = "rw".equals(value);
				if(rw) return rw;
			}
		}
		if(rw){
			JETAPropertyDescriptor pd = getPropertyDescriptor(row);
			JETAPropertyDescriptor dpd = (JETAPropertyDescriptor) pd;
			rw = dpd.isWritable();
			// rw = ( pd.getWriteMethod() == null) ? false : true;
			if(rw){
				Class propertyType = getPropertyTypeEx(row);
				String peid = m_descriptors[row].getName()+"@"+m_bean.getBeanID();
				if(!m_custom_prop_editors.containsKey(peid)){
					if(propertyType != null){
						peid = m_descriptors[row].getName()+"#"+propertyType.getName();
						if(!m_custom_prop_editors.containsKey(peid)){
							PropertyEditor editor = (PropertyEditor) m_prop_editors.get(propertyType);
							if (editor == null)
								rw = false;
						}
					
					}
				}
				
			}

		}
		return rw;
	}

}
