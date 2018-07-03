/**
 * 
 */
package com.jeta.forms.components;

import java.awt.Component;

/**
 * @author Wolcen
 *
 */
public interface SwingComponentBindingListener {
	public void firePropertyChange(String key,Component comp,String prop,Class clazz,Object oldValue,Object newValue);
}
