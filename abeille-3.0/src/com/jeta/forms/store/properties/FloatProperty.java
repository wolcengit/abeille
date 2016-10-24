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
public class FloatProperty extends JETAProperty {
	public static final int VERSION = 1;
	
	private float m_value = 0;

	/**
	 * 
	 */
	public FloatProperty() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 */
	public FloatProperty(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	
	
	public boolean equals(Object object) {
		if (object instanceof FloatProperty) {
			FloatProperty jp = (FloatProperty) object;
			return (super.equals(object) && isEqual(m_value, jp.m_value) );
		}
		else {
			return false;
		}
	}

	public float getValue(){
		return m_value;
	}
	
	
	/**
	 * @see com.jeta.forms.store.properties.JETAProperty#setValue(java.lang.Object)
	 */
	
	public void setValue(Object obj) {
		if (obj instanceof FloatProperty){
			FloatProperty jp = (FloatProperty)obj;
			this.m_value = jp.m_value;
		}else{
			this.m_value = (Float)obj;
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
		m_value = in.readFloat(VALUE);
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeFloat(VALUE, m_value);
	}


}
