/**
 * 
 */
package com.jeta.sample;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jeta.forms.components.ObjectConvert;
import com.jeta.swingbuilder.gui.properties.JETAPropertyEditor;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * @author Wolcen
 *
 */
public class SampleColorEditor extends JETAPropertyEditor {
	/**
	 * Panel that is used to hold our editor
	 */
	private JPanel m_panel;

	/**
	 * Text field 
	 */
	private JTextField m_field = new JTextField();

	/**
	 * ctor
	 * 
	 */
	public SampleColorEditor() {
		m_panel = new JPanel();
		m_panel.setLayout(new BorderLayout());
		m_panel.add(m_field, BorderLayout.CENTER);

		m_panel.setBackground(javax.swing.UIManager.getColor("Table.background"));

		m_field.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setValue(m_field.getText());
			}
		});

	}

	/**
	 * @return the custom editor
	 */
	public Component getCustomEditor() {
		if(isCustom()) m_field.setEnabled(isEnabled());
		return m_panel;
	}

	/**
	 * @return true if this editor supports custom editing inline in the
	 *         property table. Property types such as the Java primitives and
	 *         Strings support inline editing.
	 */
	public boolean supportsInlineEditing() {
		return true;
	}

	/**
	 * Sets the value
	 */
	public void setValue(Object value) {
		super.setValue(value);
		Color clr = null;
		if(value instanceof Color)
			clr = (Color)value;
		else if(value instanceof String)
			clr = ObjectConvert.StringToColor((String) value);
		else
			clr = Color.black;
		m_field.setText(clr.getRed()+","+clr.getGreen()+","+clr.getBlue());
	}

	/**
	 * @return the value represented by this field
	 */
	public Object getValue() {
		String field_txt = FormDesignerUtils.fastTrim(m_field.getText());
		Color clr = ObjectConvert.StringToColor(field_txt);
		if(clr != null){
			return clr;
		}else{
			assert (false);
			return null;
		}
	}
}

