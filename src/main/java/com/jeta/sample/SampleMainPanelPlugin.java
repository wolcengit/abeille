/**
 * 
 */
package com.jeta.sample;

import java.io.InputStream;

import javax.swing.JOptionPane;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.form.ComponentConstraints;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.codegen.DynamicClassLoader;
import com.jeta.swingbuilder.codegen.DynamicJavaCompiler;
import com.jeta.swingbuilder.gui.main.MainPanelPlugin;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * @author Wolcen
 *
 */
public class SampleMainPanelPlugin implements MainPanelPlugin {

	/**
	 * 
	 */
	public SampleMainPanelPlugin() {
	}

	/* (non-Javadoc)
	 * @see com.jeta.swingbuilder.gui.main.MainPanelPlugin#NewFormAction(com.jeta.forms.gui.form.FormComponent)
	 */
	@Override
	public boolean NewFormAction(FormComponent fc) {
		System.out.println("NewFormAction=>"+fc.getFileName());
		return true;
	}

	/* (non-Javadoc)
	 * @see com.jeta.swingbuilder.gui.main.MainPanelPlugin#OpenFormAction()
	 */
	@Override
	public InputStream OpenFormAction() {
		System.out.println("OpenFormAction");
		return null;
	}

	/* (non-Javadoc)
	 * @see com.jeta.swingbuilder.gui.main.MainPanelPlugin#SaveFormAction(com.jeta.forms.gui.form.FormComponent, boolean)
	 */
	@Override
	public boolean SaveFormAction(FormComponent fc, boolean saveAs) {
		System.out.println("SaveFormAction=>"+fc.getFileName());
		return true;
	}

	/* (non-Javadoc)
	 * @see com.jeta.swingbuilder.gui.main.MainPanelPlugin#CloseFormAction(com.jeta.forms.gui.form.FormComponent, boolean)
	 */
	@Override
	public boolean CloseFormAction(FormComponent fc, boolean isModified) {
		System.out.println("CloseFormAction=>"+fc.getFileName());
/*		
		String src = FormDesignerUtils.javaFromFormComponent(fc);
		String xml = FormDesignerUtils.xmlFromFormComponent(fc);

		String cls = fc.getCodeModel().getClassName();
		Object cd[] = DynamicJavaCompiler.javaCodeToClass(cls, src); 
		Class clazz = (Class) cd[0];
		byte[] clazzBytes = (byte[]) cd[1];
		
		try {
			Class clazz1 = DynamicClassLoader.getDynamicClass(cls, clazzBytes);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		CAN SAVE DATA TO DB: xml,cls,clazzBytes
*/	
				
		return true;
	}

	@Override
	public boolean AddComponentCommand(FormComponent fc, GridComponent gc,
			ComponentConstraints cc) {
		// TODO Auto-generated method stub
		JETABean bean = gc.getBean();
		if(bean.getBeanID().equals("jtextfield")){
			String msg = I18N.format("Only one instance jtextfield allowed per view.", fc.getId());
			String title = I18N.getLocalizedMessage("Error");
			JOptionPane.showMessageDialog(fc, msg, title, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

}
