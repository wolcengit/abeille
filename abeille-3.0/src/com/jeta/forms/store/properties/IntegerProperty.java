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
public class IntegerProperty extends JETAProperty {
	public static final int VERSION = 1;
	
	private int m_value;

	/**
	 * 
	 */
	public IntegerProperty() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 */
	public IntegerProperty(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	
	
	public boolean equals(Object object) {
		if (object instanceof IntegerProperty) {
			IntegerProperty jp = (IntegerProperty) object;
			return (super.equals(object) && isEqual(m_value, jp.m_value) );
		}
		else {
			return false;
		}
	}

	public int getValue(){
		return m_value;
	}
	
	
	/**
	 * @see com.jeta.forms.store.properties.JETAProperty#setValue(java.lang.Object)
	 */
	
	public void setValue(Object obj) {
		if (obj instanceof IntegerProperty){
			IntegerProperty jp = (IntegerProperty)obj;
			this.m_value = jp.m_value;
		}else{
			this.m_value = (Integer)obj;
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
		m_value = in.readInt(VALUE);
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeInt(VALUE, m_value);
	}


}
