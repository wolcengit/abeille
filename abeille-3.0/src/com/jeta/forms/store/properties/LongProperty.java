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
public class LongProperty extends JETAProperty {
	public static final int VERSION = 1;
	
	private long m_value = 0;

	/**
	 * 
	 */
	public LongProperty() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 */
	public LongProperty(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	
	
	public boolean equals(Object object) {
		if (object instanceof LongProperty) {
			LongProperty jp = (LongProperty) object;
			return (super.equals(object) && isEqual(m_value, jp.m_value) );
		}
		else {
			return false;
		}
	}

	public long getValue(){
		return m_value;
	}
	
	
	/**
	 * @see com.jeta.forms.store.properties.JETAProperty#setValue(java.lang.Object)
	 */
	
	public void setValue(Object obj) {
		if (obj instanceof LongProperty){
			LongProperty jp = (LongProperty)obj;
			this.m_value = jp.m_value;
		}else {
			this.m_value = (Long)obj;
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
		m_value = in.readLong(VALUE);
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeLong(VALUE, m_value);
	}


}
