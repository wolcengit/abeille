/*
 * Copyright (c) 2004 JETA Software, Inc.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JETA Software nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeta.forms.beanmgr;

import java.util.Collection;
import java.util.Properties;

import javax.swing.Icon;

import com.jeta.forms.gui.beans.JETABeanFactory;
import com.jeta.forms.gui.common.FormException;
import com.jeta.swingbuilder.gui.beanmgr.BeanLoader;
import com.jeta.swingbuilder.gui.beanmgr.DefaultBean;
import com.jeta.swingbuilder.store.RegisteredBean;

/**
 * The bean manager is responsible for managing imported beans in the designer.
 * 
 * @author Jeff Tassin
 */
public interface BeanManager {
	public static final String COMPONENT_ID = "jeta.forms.bean.manager";
	
	public static final String ID_EMBEDDED_FORM_COMPONENT = "embedded.form.tool";
	public static final String ID_LINKED_FORM_COMPONENT = "linked.form.tool";
	public static final String ID_GENERIC_COMPONENT = "generic.component";

	/**
	 * @return the underlying class loader for loading imported beans. This can
	 *         be null
	 */
	public ClassLoader getClassLoader() throws FormException;

	public BeanLoader getBeanLoader() ;
	
	/**
	 * 
	 * @param beanID
	 * @return
	 */
	public boolean containsBean(String beanID);
	/**
	 * @return the class for the given bean class name
	 * @param beanID
	 */
	public Class getBeanClass(String beanID) throws FormException;

	/**
	 * 
	 * @param beanID
	 * @return
	 */
	public RegisteredBean getRegisteredBean(String beanID);
	/**
	 * 
	 * @return
	 */
	public Collection getBeans();
	
	public void registerCustomDefaultBean(String id,String description, String className, Icon icon,Properties custom,Properties initlize,Properties codeOnly, boolean scrollable) throws FormException ;
	public void registerCustomDefaultBean(String id,String description, String className, Icon icon, Class[] params, Object[] args,Properties custom,Properties initlize,Properties codeOnly, boolean scrollable) throws FormException ;
	

}
