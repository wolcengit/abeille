/**
 * 
 */
package com.jeta.swingbuilder.gui.main;

import java.io.InputStream;

import com.jeta.forms.gui.form.ComponentConstraints;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;

/**
 * @author Wolcen
 *
 */
public interface MainPanelPlugin {
	public static final String COMPONENT_ID = "jeta.forms.mainpanel.plugin";
	/**
	 * 
	 * @param fc
	 * @return true  --- to continue 
	 *         false --- to cancel
	 */
	public boolean NewFormAction(FormComponent fc);
	
	/**
	 * 
	 * @return null --- to continue with default action
	 *         is   --- to open is 
	 */
	public InputStream OpenFormAction();
	/**
	 * 
	 * @param fc
	 * @param saveAs
	 * @return true  --- to continue 
	 *         false --- to cancel
	 */
	public boolean SaveFormAction(FormComponent fc,boolean saveAs);
	
	/**
	 * 
	 * @param fc
	 * @param isModified
	 * @return true  --- to continue 
	 *         false --- to cancel
	 */
	public boolean CloseFormAction(FormComponent fc,boolean isModified);
	
	
	
	/**
	 * @param fc
	 * @param gc
	 * @param cc
	 * @return true  --- to continue 
	 *         false --- to cancel
	 */
	public boolean AddComponentCommand(FormComponent fc, GridComponent gc, ComponentConstraints cc);
	
	
	
}

