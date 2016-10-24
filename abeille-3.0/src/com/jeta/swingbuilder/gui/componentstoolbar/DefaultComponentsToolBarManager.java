/**
 * 
 */
package com.jeta.swingbuilder.gui.componentstoolbar;

import java.awt.Component;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import com.jeta.forms.beanmgr.BeanManager;
import com.jeta.forms.components.line.HorizontalLineComponent;
import com.jeta.forms.gui.components.ComponentFactory;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.open.support.EmptyCollection;
import com.jeta.swingbuilder.gui.beanmgr.DefaultBean;
import com.jeta.swingbuilder.gui.components.EmbeddedFormComponentFactory;
import com.jeta.swingbuilder.gui.components.GenericComponentFactory;
import com.jeta.swingbuilder.gui.components.LinkedFormComponentFactory;
import com.jeta.swingbuilder.gui.components.SwingComponentFactory;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.resources.Icons;
import com.jeta.swingbuilder.store.RegisteredBean;

/**
 * @author Wolcen
 *
 */
public class DefaultComponentsToolBarManager implements ComponentsToolBarManager {
	/**
	 * 
	 */
	private Map m_catalogs = new HashMap();
	
	private Collection m_catalogs_hide = new LinkedList();
	/**
	 * Factories for creating components
	 */
	private Map m_factories = new HashMap();
	
	/** the selected tool */
	private String m_current_tool = ID_SELECTION_TOOL;
	
	
	public DefaultComponentsToolBarManager(){
		m_catalogs.put(ID_CATALOG_DEFAULT, registerDefaultBeans());
	}
	
	
	@Override
	public Collection getCatalogs() {
		Collection catalogs = new LinkedList();
		for(Object key:m_catalogs.keySet()){
			if(!m_catalogs_hide.contains(key))
				catalogs.add(key);
		}
		return catalogs;
	}

	@Override
	public Collection getBeans(String catalog) {
		if(m_catalogs.containsKey(catalog)){
			return (Collection) m_catalogs.get(catalog);
		}
		return null;
	}

	@Override
	public ComponentFactory getComponentFactory(String beanID) {
		if(m_factories.containsKey(beanID)){
			return (ComponentFactory) m_factories.get(beanID);
		}
		return null;
	}


	@Override
	public ComponentFactory getComponentFactory() {
		if (!isSelectionTool()) {
			return getComponentFactory(m_current_tool);
		}
		return null;
	}


	@Override
	public boolean isSelectionTool() {
		return (m_current_tool == ID_SELECTION_TOOL);
	}


	@Override
	public void setSelectionTool() {
		m_current_tool = ID_SELECTION_TOOL;
	}



	@Override
	public void setSelectionId(String beanID) {
		m_current_tool = beanID;
	}
	
	/**
	 * Registers all default bean factories
	 */
	private Collection registerDefaultBeans() {
		try {
			Collection beans = new LinkedList();
			RegisteredBean rbean = new DefaultBean(ID_SELECTION_TOOL,I18N.getLocalizedMessage("Selection Tool"), "", FormDesignerUtils.loadImage(Icons.MOUSE_16)) ;
			beans.add(rbean);
			BeanManager bm = (BeanManager) JETARegistry.lookup(BeanManager.COMPONENT_ID);
			if (bm == null)	return beans;

			Collection default_beans = bm.getBeans();
			Iterator iter = default_beans.iterator();
			while (iter.hasNext()) {
				rbean = (RegisteredBean) iter.next();
				Icon icon = rbean.getIcon();
				if (icon == null) {
					icon = FormDesignerUtils.loadImage(Icons.BEAN_16);
				}
				ComponentFactory factory = null;
				if(BeanManager.ID_EMBEDDED_FORM_COMPONENT.equals(rbean.getId())){
					factory = new EmbeddedFormComponentFactory(this);
				}else if(BeanManager.ID_LINKED_FORM_COMPONENT.equals(rbean.getId())){
					factory = new LinkedFormComponentFactory(this);
				}else if(BeanManager.ID_GENERIC_COMPONENT.equals(rbean.getId())){
					factory = new GenericComponentFactory(this);
				}else{
					factory = new SwingComponentFactory(this, rbean);
				}
				registerBeanFactory(rbean.getId(), factory);
				beans.add(rbean);
			}
			return beans;
			
		} catch (Exception e) {
			FormsLogger.debug(e);
		}
		return null;
	}

	/**
	 * Registers a Java bean factory
	 */
	public void registerBeanFactory(String beanID, ComponentFactory factory) {
		if (factory != null) {
			if (m_factories.get(beanID) != null) {
				System.out.println("registerBeanFactory:  factory registered twice: " + beanID);
				return;
			}
			m_factories.put(beanID, factory);
		}
	}
	
	public boolean registerCustomToolbar(String catalog, String beanID, String description, Icon icon){
		Collection beans = null;
		if(m_catalogs.containsKey(catalog)){
			beans = (Collection) m_catalogs.get(catalog);
		}else{
			beans = new LinkedList();
			RegisteredBean rbean = new DefaultBean(ID_SELECTION_TOOL,I18N.getLocalizedMessage("Selection Tool"), "", FormDesignerUtils.loadImage(Icons.MOUSE_16)) ;
			beans.add(rbean);
		}
		if(ID_SELECTION_TOOL.equals(beanID)){
			RegisteredBean rbean = new DefaultBean(ID_SELECTION_TOOL,I18N.getLocalizedMessage("Selection Tool"), "", FormDesignerUtils.loadImage(Icons.MOUSE_16)) ;
			beans.add(rbean);
			return true;
		}
		
		BeanManager bm = (BeanManager) JETARegistry.lookup(BeanManager.COMPONENT_ID);
		DefaultBean rb = (DefaultBean) bm.getRegisteredBean(beanID);
		DefaultBean rbean = null;
		try {
			rbean = (DefaultBean) rb.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		if(rbean == null){
			return false ;
		}
		if(description != null){
			rbean.setDescription(description);
		}
		Icon icon1 = (icon ==null)?rbean.getIcon():icon;
		if (icon1 == null) {
			icon1 = FormDesignerUtils.loadImage(Icons.BEAN_16);
		}
		
		ComponentFactory factory = null;
		if(BeanManager.ID_EMBEDDED_FORM_COMPONENT.equals(rbean.getId())){
			factory = new EmbeddedFormComponentFactory(this);
		}else if(BeanManager.ID_LINKED_FORM_COMPONENT.equals(rbean.getId())){
			factory = new LinkedFormComponentFactory(this);
		}else if(BeanManager.ID_GENERIC_COMPONENT.equals(rbean.getId())){
			factory = new GenericComponentFactory(this);
		}else{
			factory = new SwingComponentFactory(this, rbean);
		}
		registerBeanFactory(rbean.getId(), factory);
		beans.add(rbean);
		
		m_catalogs.put(catalog, beans);
		return true;
	}
	
	public void showToolbar(String catalog){
		m_catalogs_hide.remove(catalog);
	}
	public void hideToolbar(String catalog){
		if(!m_catalogs_hide.contains(catalog))
			m_catalogs_hide.add(catalog);
	}
	
}
