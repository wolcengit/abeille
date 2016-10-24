/**
 * 
 */ 
package com.jeta.forms.store.properties;

import java.awt.Font;
import java.io.IOException;

import com.jeta.forms.components.ObjectConvert;
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
public class FontProperty2 extends JETAProperty {
	public static final int VERSION = 1;
	
	private Font m_value = null;

	/**
	 * 
	 */
	public FontProperty2() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 */
	public FontProperty2(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	
	
	public boolean equals(Object object) {
		if (object instanceof FontProperty2) {
			FontProperty2 jp = (FontProperty2) object;
			return (super.equals(object) && isEqual(m_value, jp.m_value) );
		}
		else {
			return false;
		}
	}

	public Font getValue(){
		return m_value;
	}
	
	
	/**
	 * @see com.jeta.forms.store.properties.JETAProperty#setValue(java.lang.Object)
	 */
	
	public void setValue(Object obj) {
		if (obj instanceof FontProperty2){
			FontProperty2 jp = (FontProperty2)obj;
			this.m_value = jp.m_value;
		}else if (obj instanceof Font){
			this.m_value = (Font)obj;
		}else{
			if(obj != null)
				this.m_value = ObjectConvert.StringToFont(obj.toString());
			else
				this.m_value = null;
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
		String value = in.readString(VALUE);
		m_value = ObjectConvert.StringToFont(value);
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeString(VALUE, ObjectConvert.FontToString(m_value));
	}


}
