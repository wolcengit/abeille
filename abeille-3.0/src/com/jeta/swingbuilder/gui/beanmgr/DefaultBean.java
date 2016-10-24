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

package com.jeta.swingbuilder.gui.beanmgr;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.Icon;

import com.jeta.forms.beanmgr.BeanManager;
import com.jeta.forms.gui.common.FormException;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.store.RegisteredBean;

public class DefaultBean implements RegisteredBean,Cloneable {
	private String m_id;
	private String m_description;
	private String m_class_name;
	private Icon m_icon;
	private boolean m_scrollable;
	private String m_factory = null;
	
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
	private Properties m_codeOnly = null;
	private Properties m_initlize = null;
	private Properties m_custom = null;
	
	private HashMap m_methods = new HashMap();
	private HashMap m_clazzs = new HashMap();

	public DefaultBean(String id) {
		m_id = id;
	}
	public DefaultBean(String id,String description, String className, Icon icon) {
		m_id = id;
		m_description = description;
		m_class_name = className;
		m_icon = icon;
	}
	public DefaultBean(String id,String description, String className, Icon icon, Properties custom,Properties initlize,Properties codeOnly) {
		m_id = id;
		m_description = description;
		m_class_name = className;
		m_icon = icon;
		m_custom = custom;
		m_initlize = initlize;
		m_codeOnly = codeOnly;
	}
	public DefaultBean(String id,String description, String className, Icon icon, Class[] params, Object[] args) {
		m_id = id;
		m_description = description;
		m_class_name = className;
		m_icon = icon;
		m_params = params;
		m_args = args;
	}
	public DefaultBean(String id,String description, String className, Icon icon, Class[] params, Object[] args,Properties custom,Properties initlize,Properties codeOnly) {
		m_id = id;
		m_description = description;
		m_class_name = className;
		m_icon = icon;
		m_params = params;
		m_args = args;
		m_custom = custom;
		m_initlize = initlize;
		m_codeOnly = codeOnly;
	}

	public DefaultBean(String description, String className, Icon icon) {
		m_id  = className;
		m_description = description;
		m_class_name = className;
		m_icon = icon;
	}

	public String getId() {
		return m_id;
	}

	public String getDescription() {
		return m_description;
	}

	public String getClassName() {
		return m_class_name;
	}

	public Icon getIcon() {
		return m_icon;
	}

	@Override
	public Class[] getCtorParams() {
		return m_params;
	}

	@Override
	public Object[] getCtorArgs() {
		return m_args;
	}

	@Override
	public Properties getCodeOnlyProperties() {
		return m_codeOnly;
	}

	@Override
	public Properties getInitializeProperties() {
		return m_initlize;
	}

	@Override
	public Properties getCustomProperties() {
		return m_custom;
	}
	@Override
	public Method getInitializeMethod(String key) {
		if(m_methods.containsKey(key)){
			return (Method) m_methods.get(key);
		}else{
			createInitializeInfo(key);
			return (Method) m_methods.get(key);
		}
	}
	@Override
	public Class getInitializeClass(String key) {
		if(m_clazzs.containsKey(key)){
			return (Class) m_clazzs.get(key);
		}else{
			createInitializeInfo(key);
			return (Class) m_clazzs.get(key);
		}
	}
	@Override
	public String getFactory(){
		return m_factory;
	}
	
	private void createInitializeInfo(String key){
		if(!m_methods.containsKey(key)){
			try {
				String name = key.substring(0, 1).toUpperCase() + key.substring(1);
				Class type = Class.forName(m_class_name);
				Method method0 = type.getMethod("get"+name);
				Class clazz = method0.getReturnType();
				Method method = type.getMethod("set"+name,clazz);
				method.setAccessible(true);
				m_methods.put(key, method);
				m_clazzs.put(key, clazz);
			} catch (Exception e) {
				e.printStackTrace();
				m_methods.put(key, (Method)null);
				m_clazzs.put(key, (Class)null);
			} 
		}
	}
	public void setId(String id) {
		this.m_id = id;
	}
	public void setDescription(String description) {
		this.m_description = description;
	}
	public void setClassName(String class_name) {
		this.m_class_name = class_name;
	}
	public void setIcon(Icon icon) {
		this.m_icon = icon;
	}
	public void setParams(Class[] params) {
		this.m_params = params;
	}
	public void setArgs(Object[] args) {
		this.m_args = args;
	}
	public void setCodeOnlyProperties(Properties codeOnly) {
		this.m_codeOnly = codeOnly;
	}
	public void setInitlizeProperties(Properties initlize) {
		this.m_initlize = initlize;
	}
	public void setCustomProperties(Properties custom) {
		this.m_custom = custom;
	}
	public boolean isScrollable() {
		return m_scrollable;
	}
	public void setScrollable(boolean scrollable) {
		this.m_scrollable = scrollable;
	}
	public void setFactory(String factory){
		this.m_factory = factory;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		DefaultBean bean = (DefaultBean) super.clone();
		bean.m_codeOnly = (Properties) m_codeOnly.clone();
		bean.m_initlize = (Properties) m_initlize.clone();
		bean.m_custom = (Properties) m_custom.clone();
		return bean;
	}
	
	@Override
	public Component newComponent() throws Exception {
		BeanManager bm = (BeanManager) JETARegistry.lookup(BeanManager.COMPONENT_ID);
		if(m_factory == null){
			Component comp = bm.getBeanLoader().createBean(m_class_name, m_params, m_args);
			return comp;
		}else{
			Component comp = bm.getBeanLoader().createBeanByFactory(m_factory,  m_params, m_args);
			return comp;
		}
	}
	
	
}
