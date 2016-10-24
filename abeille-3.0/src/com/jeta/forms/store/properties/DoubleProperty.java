/**
 * 
 */ 
package com.jeta.forms.store.properties;

import java.io.IOException;

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
public class DoubleProperty extends JETAProperty {
	public static final int VERSION = 1;
	
	private double m_value = 0;

	/**
	 * 
	 */
	public DoubleProperty() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 */
	public DoubleProperty(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	
	
	public boolean equals(Object object) {
		if (object instanceof DoubleProperty) {
			DoubleProperty jp = (DoubleProperty) object;
			return (super.equals(object) && isEqual(m_value, jp.m_value) );
		}
		else {
			return false;
		}
	}

	public double getValue(){
		return m_value;
	}
	
	
	/**
	 * @see com.jeta.forms.store.properties.JETAProperty#setValue(java.lang.Object)
	 */
	
	public void setValue(Object obj) {
		if (obj instanceof DoubleProperty){
			DoubleProperty jp = (DoubleProperty)obj;
			this.m_value = jp.m_value;
		}else{
			this.m_value = (Double)obj;
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
		m_value = in.readDouble(VALUE);
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeDouble(VALUE, m_value);
	}


}
