/**
 * 
 */ 
package com.jeta.forms.store.properties;

import java.io.IOException;
import java.util.ArrayList;
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
public class StringListProperty extends JETAProperty {
	public static final int VERSION = 1;

	public static final String VALUES = "values";
	
	private String m_value = null;
	private List<String> m_values = null;

	/**
	 * 
	 */
	public StringListProperty() {
	}

	/**
	 * @param name
	 */
	public StringListProperty(String name,List<String> values) {
		super(name);
		m_values = values;
	}
	
	
	public boolean equals(Object object) {
		if (object instanceof StringListProperty) {
			StringListProperty jp = (StringListProperty) object;
			return (super.equals(object) && isEqual(m_value, jp.m_value) && isEqual(m_values, jp.m_values));
		}
		else {
			return false;
		}
	}

	public String getValue(){
		return m_value;
	}
	
	public List<String> getValues(){
		return m_values;
	}
	
	/**
	 * @see com.jeta.forms.store.properties.JETAProperty#setValue(java.lang.Object)
	 */
	
	public void setValue(Object obj) {
		if (obj instanceof StringListProperty){
			StringListProperty jp = (StringListProperty)obj;
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
		m_values = new ArrayList<String>();
		for(String v:values)
			m_values.add(v);
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeObject(VALUE, m_value);
		String valuesstr = "";
		for(String v:m_values)
			valuesstr += v + "|";
		out.writeObject(VALUES, valuesstr);
	}


}
