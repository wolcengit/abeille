/**
 * 
 */
package com.jeta.swingbuilder.gui.componentstoolbar;

import java.util.Collection;

import javax.swing.Icon;

import com.jeta.forms.gui.components.ComponentFactory;
import com.jeta.forms.gui.components.ComponentSource;

/**
 * @author Wolcen
 *
 */
public interface ComponentsToolBarManager extends ComponentSource {
	public static final String COMPONENT_ID = "jeta.forms.componentstoolbar.manager";

	public static final String ID_SELECTION_TOOL = "selection.tool";
	
	public static final String ID_CATALOG_DEFAULT = "Default";

	/**
	 * 
	 * @return
	 */
	public Collection getCatalogs();
	
	/**
	 * 
	 * @param catalog
	 * @return
	 */
	public Collection getBeans(String catalog);
	
	/**
	 * 
	 * @param beanID
	 * @return
	 */
	public ComponentFactory getComponentFactory(String beanID);
	
	/**
	 * 
	 * @param beanID
	 */
	void setSelectionId(String beanID);
	
	/**
	 * 
	 * @param catalog
	 * @param beanID
	 * @param description
	 * @param icon
	 * @return
	 */
	public boolean registerCustomToolbar(String catalog, String beanID, String description, Icon icon);
	
	public void hideToolbar(String catalog);
	public void showToolbar(String catalog);
}
