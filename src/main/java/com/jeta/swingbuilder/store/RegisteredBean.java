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

package com.jeta.swingbuilder.store;

import java.awt.Component;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.swing.Icon;

public interface RegisteredBean {
	
	public String getId();
	
	public String getDescription();

	public String getClassName();

	public Icon getIcon();
	
	public Class[] getCtorParams();
	
	public Object[] getCtorArgs();
	
	public Properties getCodeOnlyProperties();
	
	public Properties getInitializeProperties();
	
	public Properties getCustomProperties();
	
	public Method getInitializeMethod(String key);
	
	public Class getInitializeClass(String key);
	
	public boolean isScrollable();
	
	public String getFactory();
	
	public void setDescription(String description);
	public void setClassName(String class_name);
	public void setIcon(Icon icon);
	public void setParams(Class[] params);
	public void setArgs(Object[] args);
	public void setCodeOnlyProperties(Properties codeOnly);
	public void setInitlizeProperties(Properties initlize);
	public void setCustomProperties(Properties custom);
	public void setScrollable(boolean scrollable);
	public void setFactory(String factory);
	
	public Component newComponent() throws Exception;
	
	
}
