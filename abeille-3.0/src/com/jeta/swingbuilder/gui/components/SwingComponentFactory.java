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

package com.jeta.swingbuilder.gui.components;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import com.jeta.forms.beanmgr.BeanManager;
import com.jeta.forms.components.ObjectConvert;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.beans.JETABeanFactory;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.components.StandardComponentFactory;
import com.jeta.forms.gui.form.FormContainerComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.form.StandardComponent;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.store.RegisteredBean;

/**
 * A factory for creating swing components
 */
public class SwingComponentFactory extends StandardComponentFactory {

	/**
	 * The class name of the component to create
	 */
	private String m_comp_class;

	/**
	 * Parameter types for the constructor of the component class.
	 */
	private Class[] m_params = new Class[0];

	/**
	 * Object values for the constructor of the component class
	 */
	private Object[] m_args = new Object[0];
	
	/**
	 * Properties
	 */
	private RegisteredBean m_bean = null;

	/**
	 * ctor
	 */
	public SwingComponentFactory(ComponentSource compSrc, String compClass) {
		super(compSrc);
		m_comp_class = compClass;
	}

	/**
	 * ctor
	 */
	public SwingComponentFactory(ComponentSource compSrc, String compClass, Class[] params, Object[] args) {
		super(compSrc);
		m_comp_class = compClass;
		m_params = params;
		m_args = args;
	}
	/**
	 * ctor
	 */
	public SwingComponentFactory(ComponentSource compSrc, RegisteredBean bean) {
		super(compSrc);
		m_bean = bean;
		m_comp_class = m_bean.getClassName();
		m_params = m_bean.getCtorParams();
		m_args = m_bean.getCtorArgs();
		System.out.println("SwingComponentFactory =>"+bean.getId()+"  "+bean.getClassName());
	}

	public GridComponent createComponent(String compName, GridView view) throws FormException {
		try {
			JETABean jetabean = null;
			if(m_bean != null){
				jetabean = JETABeanFactory.createBean(m_bean.getId(), getComponentClass(), compName, true, true);
			}else{
				jetabean = JETABeanFactory.createBean(getComponentClass(), compName, true, true);
			}
			if (jetabean == null) {
				BeanManager bm = (BeanManager) JETARegistry.lookup(BeanManager.COMPONENT_ID);
				Class c = bm.getBeanClass(getComponentClass());
				Constructor ctor = c.getConstructor(m_params);
				Component comp = (Component) ctor.newInstance(m_args);
				comp.setName(compName);
				initializeBean(comp);
				return super.createComponent(comp, view);
			}
			else {
				if (jetabean.getDelegate() instanceof javax.swing.JTabbedPane) {
					FormContainerComponent gc = new FormContainerComponent(jetabean, view);
					installHandlers(gc);
					return gc;
				}
				else {
					StandardComponent gc = new StandardComponent(jetabean, view);
					installHandlers(gc);
					return gc;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new FormException(e);
		}
	}

	public String getComponentClass() {
		return m_comp_class;
	}
	
	protected void initializeBean(Component comp){
		if(m_bean == null) return;
		Properties props = m_bean.getInitializeProperties();
		if(props == null) return ;
		for(Object key:props.keySet()){
			Method method = m_bean.getInitializeMethod(key.toString());
			Class clazz = m_bean.getInitializeClass(key.toString());
			if(method != null){
				try {
					Object value = ObjectConvert.Converter(clazz, props.getProperty(key.toString()));
					method.invoke(comp,value );
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
