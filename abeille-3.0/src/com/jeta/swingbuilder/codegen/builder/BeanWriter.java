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

package com.jeta.swingbuilder.codegen.builder;

import java.awt.Color;
import java.awt.Font;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JPanel;

import com.jeta.forms.beanmgr.BeanManager;
import com.jeta.forms.components.label.JETALabel;
import com.jeta.forms.gui.beans.DynamicBeanInfo;
import com.jeta.forms.gui.beans.DynamicPropertyDescriptor;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.beans.JETABeanFactory;
import com.jeta.forms.gui.beans.JETAPropertyDescriptor;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.memento.BeanMemento;
import com.jeta.forms.store.memento.ComponentMemento;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.forms.store.memento.PropertiesMemento;
import com.jeta.forms.store.properties.BooleanProperty;
import com.jeta.forms.store.properties.ColorProperty2;
import com.jeta.forms.store.properties.DoubleProperty;
import com.jeta.forms.store.properties.FloatProperty;
import com.jeta.forms.store.properties.FontProperty2;
import com.jeta.forms.store.properties.IntegerProperty;
import com.jeta.forms.store.properties.LongProperty;
import com.jeta.forms.store.properties.StringProperty;
import com.jeta.forms.store.properties.TransformOptionsProperty;
import com.jeta.open.registry.JETARegistry;

public class BeanWriter implements BaseBeanWriter {
	private String m_bean_variable_name;
	private Class m_bean_type;

	private String m_result_variable_name;
	private Class m_result_type;

	private LinkedList m_statements = new LinkedList();

	public BeanWriter(){
	}
	
	/* (non-Javadoc)
	 * @see com.jeta.swingbuilder.codegen.builder.BaseBeanWriter#createBeanSource(com.jeta.swingbuilder.codegen.builder.MethodWriter, com.jeta.forms.store.memento.ComponentMemento)
	 */
	@Override
	public void createBeanSource(MethodWriter mr, ComponentMemento cm) {
		assert (cm != null);
		PropertiesMemento pm = null;
		if(cm instanceof FormMemento )
			pm = ((FormMemento)cm).getPropertiesMemento();
		else
			pm = ((BeanMemento)cm).getProperties();
		assert (pm != null);
		
		String classname = pm.getBeanClassName();

		if (GridView.class.getName().equals(classname))
			classname = "javax.swing.JPanel";
		else if (JETALabel.class.getName().equals(classname))
			classname = "javax.swing.JLabel";

		try {
			Class beanclass = null;
			try {
				BeanManager bmgr = (BeanManager) JETARegistry.lookup(BeanManager.COMPONENT_ID);
				if (bmgr != null) {
					if(cm instanceof BeanMemento){
						BeanMemento bm = (BeanMemento)cm;
						beanclass = bmgr.getBeanClass(bm.getJETABeanID());
					}
					if (beanclass == null) beanclass = bmgr.getBeanClass(classname);
				}
			} catch (Exception e) {
				FormsLogger.severe(e);
			}

			if (beanclass == null) {
				beanclass = Class.forName(classname);
			}

			VariableDeclaration ds = null;
			/**
			 * don't declare panels or labels as member variables if they don't
			 * have a name
			 */
			//if (JLabel.class.isAssignableFrom(beanclass) || JPanel.class.isAssignableFrom(beanclass)) {
			if (JPanel.class.isAssignableFrom(beanclass)) {
				String compname = pm.getComponentName();
				if (compname == null)
					compname = "";
				else
					compname = compname.trim();

				if (compname.length() == 0) {
					ds = new LocalVariableDeclaration(mr, beanclass, null);
					/** temporary hack */
					mr.addStatement(ds);
				}
			}

			if (ds == null) {
				ds = new MemberVariableDeclaration(mr, beanclass,pm.getComponentName(), false);
				((MemberVariableDeclaration)ds).setMemento(cm);
				mr.addMemberVariable(ds);
				VariableInitializer vi = new VariableInitializer(mr, beanclass, ds.getVariable(),null);
				mr.addStatement(vi);
			}

			setBeanVariable(ds.getVariable(), beanclass);
			setResultVariable(ds.getVariable(), beanclass);

			Collection custProps = new LinkedList();
			for(Object key:pm.getPropertyNames()){
				String skey = key.toString();
				if(skey.startsWith("*")){
					skey = skey.substring(1);
					if(skey.indexOf("@") != -1){
						skey = skey.substring(0,skey.indexOf("@"));
					}
					custProps.add(skey);
				}
			}
			
			PropertyWriterFactory fac = (PropertyWriterFactory) JETARegistry.lookup(PropertyWriterFactory.COMPONENT_ID);
			LinkedList dynamic_props = new LinkedList();
			
			Class lookup_class = beanclass;
			if (GridView.class.getName().equals(pm.getBeanClassName()))
				lookup_class = GridView.class;

			DynamicBeanInfo beaninfo = JETABeanFactory.getBeanInfo(lookup_class);
			Collection jeta_pds = beaninfo.getPropertyDescriptors();
			Iterator iter = jeta_pds.iterator();
			while (iter.hasNext()) {
				try {
					JETAPropertyDescriptor jpd = (JETAPropertyDescriptor) iter.next();
					if (pm.containsProperty(jpd.getName())) {

						if (jpd instanceof DynamicPropertyDescriptor) {
							dynamic_props.add(jpd);
						}
						else {
							Object prop_value = pm.getPropertyValue(jpd.getName());
							if ("name".equals(jpd.getName()) && "".equals(prop_value)) {
								continue;
							}

							PropertyWriter pw = fac.createWriter(jpd.getPropertyType());
							if (pw != null && !custProps.contains(jpd.getName())) {
								pw.writeProperty(mr, this, jpd, prop_value);
							}
						}
					}
				} catch (Exception e) {
					FormsLogger.debug(e);
				}
			}

			/** now do the dynamic props */
			iter = dynamic_props.iterator();
			while (iter.hasNext()) {
				DynamicPropertyDescriptor dpd = (DynamicPropertyDescriptor) iter.next();

				PropertyWriter pw = fac.createWriter(dpd.getPropertyType());
				if (pw != null && !custProps.contains(dpd.getName())) {
					Object prop_value = null;
					if (dpd.getPropertyType() == TransformOptionsProperty.class) {
						JETABean jetabean = JETABeanFactory.createBean(beanclass.getName(), null, true, true);
						jetabean.setState(pm);
						TransformOptionsProperty tprop = (TransformOptionsProperty) jetabean.getCustomProperty(dpd.getName());
						prop_value = tprop;
					}
					else {
						prop_value = pm.getPropertyValue(dpd.getName());
					}
					pw.writeProperty(mr, this, dpd, prop_value);
						
				}
			}
			
			createBeanSourceCustom(mr,cm,ds.getVariable());
			//
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.jeta.swingbuilder.codegen.builder.BaseBeanWriter#createBeanSourceCustom(com.jeta.swingbuilder.codegen.builder.MethodWriter, com.jeta.forms.store.memento.ComponentMemento, java.lang.String)
	 */
	@Override
	public void createBeanSourceCustom(MethodWriter mr, ComponentMemento cm, String variable) {
		assert (cm != null);
		PropertiesMemento pm = null;
		if(cm instanceof FormMemento )
			pm = ((FormMemento)cm).getPropertiesMemento();
		else
			pm = ((BeanMemento)cm).getProperties();
		assert (pm != null);
		StringBuffer sb = new StringBuffer();
		for(Object key:pm.getPropertyNames()){
			String skey = key.toString();
			String sType = "String";
			if(skey.startsWith("*")){
				Object value = pm.getPropertyValueEx(skey);
				if(value != null){
					String svalue = value.toString();
					skey = skey.substring(1);
					if(skey.indexOf("@") != -1){
						sType = skey.substring(skey.indexOf("@") + 1);
						skey = skey.substring(0,skey.indexOf("@"));
					}else{
						sType = "String";
					}
					if(value instanceof Integer){
						svalue = String.valueOf((Integer)value);
					}else if(value instanceof Long){
						svalue = String.valueOf((Long)value);
					}else if(value instanceof Float){
						svalue = String.valueOf((Float)value);
					}else if(value instanceof Double){
						svalue = String.valueOf((Double)value);
					}else if(value instanceof Boolean){
						svalue = String.valueOf((Boolean)value);
					}else if(value instanceof Color){
						mr.addImport("java.awt.Color");

						Color clr = (Color)value;
						svalue = "new Color("+clr.getRed()+","+clr.getGreen()+","+clr.getBlue()+")";
					}else if(value instanceof Font){
						mr.addImport("java.awt.Font");
						Font fnt = (Font)value;
						svalue = "new Font(\""+fnt.getFamily()+"\","+fnt.getStyle()+","+fnt.getSize()+")";
					}else if(value instanceof String){
						if("Static".equals(sType))
							svalue = (String)value;
						else
							svalue = "\""+(String)value+"\"";
					}else{
						continue;
					}
					
					sb.append(variable+".set");
					sb.append(skey.substring(0, 1).toUpperCase()+skey.substring(1));
					sb.append("(");
					sb.append(svalue);
					sb.append(");");
				}
			}
		}
		Block block = new Block();
		block.addCode(sb.toString());
		mr.addSegment(block);
		
	}

	/* (non-Javadoc)
	 * @see com.jeta.swingbuilder.codegen.builder.BaseBeanWriter#addStatement(com.jeta.swingbuilder.codegen.builder.Statement)
	 */
	@Override
	public void addStatement(Statement stmt) {
		if (stmt != null)
			m_statements.add(stmt);
	}

	/* (non-Javadoc)
	 * @see com.jeta.swingbuilder.codegen.builder.BaseBeanWriter#getStatements()
	 */
	@Override
	public Collection getStatements() {
		return m_statements;
	}

	/* (non-Javadoc)
	 * @see com.jeta.swingbuilder.codegen.builder.BaseBeanWriter#setBeanVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public void setBeanVariable(String varName, Class beanType) {
		m_bean_variable_name = varName;
		m_bean_type = beanType;
	}

	/* (non-Javadoc)
	 * @see com.jeta.swingbuilder.codegen.builder.BaseBeanWriter#getBeanVariable()
	 */
	@Override
	public String getBeanVariable() {
		return m_bean_variable_name;
	}

	/* (non-Javadoc)
	 * @see com.jeta.swingbuilder.codegen.builder.BaseBeanWriter#getBeanType()
	 */
	@Override
	public Class getBeanType() {
		return m_bean_type;
	}

	/* (non-Javadoc)
	 * @see com.jeta.swingbuilder.codegen.builder.BaseBeanWriter#getResultVariable()
	 */
	@Override
	public String getResultVariable() {
		return m_result_variable_name;
	}

	/* (non-Javadoc)
	 * @see com.jeta.swingbuilder.codegen.builder.BaseBeanWriter#getResultType()
	 */
	@Override
	public Class getResultType() {
		return m_result_type;
	}

	/* (non-Javadoc)
	 * @see com.jeta.swingbuilder.codegen.builder.BaseBeanWriter#setResultVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public void setResultVariable(String varName, Class resultType) {
		m_result_variable_name = varName;
		m_result_type = resultType;
	}
}
