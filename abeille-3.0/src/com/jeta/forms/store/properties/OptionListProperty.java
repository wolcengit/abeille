/**
 * 
 */ 
package com.jeta.forms.store.properties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * simple introduction
 *
 * <p>detailed comment
 * @author foxhis 2013-8-13
 * @see
 * @since 1.0
 */
public class OptionListProperty extends JETAProperty {
	public static final int VERSION = 1;

	public static final String VALUES = "values";
	
	private String m_value = null;
	private transient HashMap m_values = new HashMap();
	private Object[][] m_options ;

	/**
	 * 
	 */
	public OptionListProperty() {
	}

	/**
	 * @param name
	 */
	public OptionListProperty(String name,Object[][] values) {
		super(name);
		m_options = values;
		for (int index = 0; index < m_options.length; index++) {
			String option = (String) m_options[index][0];
			Integer value = (Integer) m_options[index][1];
			m_values.put(option, value);
		}
	}
	
	
	public boolean equals(Object object) {
		if (object instanceof OptionListProperty) {
			OptionListProperty jp = (OptionListProperty) object;
			return (super.equals(object) && isEqual(m_value, jp.m_value) && isEqual(m_values, jp.m_values));
		}
		else {
			return false;
		}
	}

	public String getValue(){
		return m_value;
	}
	
	public Collection getValues(){
		return m_values.keySet();
	}
	
	/**
	 * @see com.jeta.forms.store.properties.JETAProperty#setValue(java.lang.Object)
	 */
	
	public void setValue(Object obj) {
		if (obj instanceof OptionListProperty){
			OptionListProperty jp = (OptionListProperty)obj;
			this.m_value = jp.m_value;
		}else{
			this.m_value = (String)obj;
		}
	}

	/**
	 * @see com.jeta.forms.store.properties.JETAProperty#updateBean(com.jeta.forms.gui.beans.JETABean)
	 */
	
	public void updateBean(JETABean jbean) {

	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readVersion();
		m_value = in.readString(VALUE);
		String valuesstr = in.readString(VALUES);
		String[] values = valuesstr.split("\\|");
		int len = (values.length)/2;
		m_options = new Object[len][2];
		for(int i=0;i<len;i++){
			m_options[i][0] = values[i*2 + 1];
			m_options[i][1] = Integer.valueOf(values[i*2 ]);
		}
		for (int index = 0; index < m_options.length; index++) {
			String option = (String) m_options[index][0];
			Integer value = (Integer) m_options[index][1];
			m_values.put(option, value);
		}
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeObject(VALUE, m_value);
		String valuesstr = "";
		for (int index = 0; index < m_options.length; index++) {
			valuesstr += m_options[index][1] + "|" + m_options[index][0] + "|";
		}
		out.writeObject(VALUES, valuesstr);
	}


}
