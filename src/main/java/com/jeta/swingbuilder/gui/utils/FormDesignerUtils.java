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

package com.jeta.swingbuilder.gui.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.form.ComponentConstraints;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.form.ReadOnlyConstraints;
import com.jeta.forms.store.jml.JMLException;
import com.jeta.forms.store.jml.JMLUtils;
import com.jeta.forms.store.jml.dom.JMLNode;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.forms.store.memento.FormPackage;
import com.jeta.forms.store.memento.StateRequest;
import com.jeta.forms.store.xml.writer.XMLWriter;
import com.jeta.jgoodies.forms.layout.CellConstraints;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.codegen.builder.SourceBuilder;
import com.jeta.swingbuilder.gui.commands.CommandUtils;
import com.jeta.swingbuilder.gui.commands.SetConstraintsCommand;
import com.jeta.swingbuilder.gui.components.TSErrorDialog;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jeta.swingbuilder.gui.filechooser.FileChooserConfig;
import com.jeta.swingbuilder.gui.filechooser.TSFileChooserFactory;
import com.jeta.swingbuilder.gui.filechooser.TSFileFilter;
import com.jeta.swingbuilder.interfaces.resources.ResourceLoader;

/**
 * Some common utilities used by the form designer.
 * 
 * @author Jeff Tassin
 */
public class FormDesignerUtils {
	public static final String JETA1_TEST = "jeta1.test";
	public static final String JETA1_SIMPLE = "jeta1.simple";
	public static final String JETA1_FIXED = "jeta1.fixed";
	public static final String JETA1_FIXED_COLSPEC = "jeta1.fixed.colspec";
	public static final String JETA1_FIXED_ROWSPEC = "jeta1.fixed.rowspec";

	private static Object[] ENV_VARS = null;

	/** cache of images */
	private static Hashtable m_imagecache = new Hashtable();
	
	private static Map<String,Object> m_formprops = new HashMap<String, Object>();  

	/**
	 * Checks if the span can be set to the given values for the given
	 * component.
	 */
	public static boolean checkSpan(GridComponent gc, int colspan, int rowspan) {
		if (gc == null)
			return false;

		int rowstart = gc.getRow();
		int colstart = gc.getColumn();

		GridView parentview = gc.getParentView();
		/**
		 * Make sure that the component will not overlay any other components
		 */
		for (int row = rowstart; row <= (rowstart + rowspan - 1); row++) {
			for (int col = colstart; col <= (colstart + colspan - 1); col++) {
				if (row != rowstart || col != colstart) {
					GridComponent gc_existing = parentview.getGridComponent(col, row);
					assert (gc_existing != gc);
					if (gc_existing.hasBean()) {
						String msg = I18N.format("Components_would_overlap_at_cell_2", new Integer(col), new Integer(row));
						String title = I18N.getLocalizedMessage("Error");
						JOptionPane.showMessageDialog(gc, msg, title, JOptionPane.ERROR_MESSAGE);
						return false;
					}
				}
			}
		}

		return true;
	}

	public static void copyFile(String dest_path, String src_path) throws IOException {
		if (dest_path == null)
			return;

		try {
			File f1 = new File(dest_path);
			File f2 = new File(src_path);

			if (f1.getCanonicalPath().equals(f2.getCanonicalPath())) {
				System.err.println("FormsDesignerUtils.copyFile  dest and src are same.");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		FileInputStream fis = new FileInputStream(src_path);
		FileOutputStream fos = new FileOutputStream(dest_path);

		BufferedInputStream bis = new BufferedInputStream(fis);
		BufferedOutputStream bos = new BufferedOutputStream(fos);

		byte[] buff = new byte[1024];
		int numread = bis.read(buff);
		while (numread > 0) {
			bos.write(buff, 0, numread);
			numread = bis.read(buff);
		}

		bos.flush();
		bos.close();
		bis.close();
	}

	/**
	 * Converts dialog units in the x and y to pixels. This is used for
	 * providing a consistent initial window size for growable panels.
	 */
	public static Dimension getWindowDimension(Component window, int dluX, int dluY) {
		Units units = Units.getInstance();
		return new Dimension(units.dialogUnitXAsPixel(dluX, window), units.dialogUnitYAsPixel(dluY, window));
	}

	/**
	 * Performs a fast trim on a string. Note: if the string does not need
	 * triming, then a copy is NOT created and the original string is returned.
	 * Null is never returned from this method. If null is passed, an empty
	 * string is returned.
	 */
	public static String fastTrim(String str) {
		if (str == null)
			return "";

		int len = str.length();
		if (len == 0)
			return str;

		if (str.charAt(0) == ' ' || str.charAt(len - 1) == ' ')
			return str.trim();
		else
			return str; // no leading/trailing spaces so just return the
		// original
		// string
	}

	/**
	 * Loads an image from disk. The image is loaded relative to the application
	 * directory.
	 * 
	 * @todo we need to cache these images
	 */
	public static ImageIcon loadImage(String imageName) {
		if (imageName != null) {
			ImageIcon icon = (ImageIcon) m_imagecache.get(imageName);
			if (icon == null) {
				try {
					ResourceLoader loader = (ResourceLoader) JETARegistry.lookup(ResourceLoader.COMPONENT_ID);
					assert (loader != null);
					icon = loader.loadImage(imageName);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (icon == null) {
					icon = new ImageIcon();
				}
				m_imagecache.put(imageName, icon);
			}
			return icon;
		}
		else {
			assert (false);
			return new ImageIcon();
		}
	}

	public static boolean canMoveComponent(GridComponent destCell, GridComponent sourceCell) {
		if (destCell == null || sourceCell == null)
			return false;

		if (sourceCell.getBeanDelegate() == null)
			return false;
		CellConstraints ccs = sourceCell.getConstraints().createCellConstraints();
		CellConstraints ccd = destCell.getConstraints().createCellConstraints();
		
		GridView pvd = destCell.getParentView();
		
		if(ccd.gridX + ccs.gridWidth - 1 > pvd.getRowCount() ) return false;
		if(ccd.gridY + ccs.gridHeight - 1 > pvd.getColumnCount() ) return false;
		
		for(int i=ccd.gridX;i<ccd.gridX + ccs.gridWidth ;i++){
			for(int j=ccd.gridY;j<ccd.gridY + ccs.gridHeight;j++){
				GridComponent gc = pvd.getGridComponent(i, j);
				if(gc != null && gc.getBeanDelegate() != null && gc != sourceCell ) return false;
			}
		}
		return true;
	}
	/**
	 * Sets the span on the given component
	 */
	public static void setSpan(GridComponent gc, int colspan, int rowspan) {
		if (gc == null || colspan < 1 || rowspan < 1)
			return;

		if (gc.getBeanDelegate() == null)
			return;

		GridView parentview = gc.getParentView();
		ComponentConstraints cc = gc.getConstraints();

		int rowstart = gc.getRow();
		int colstart = gc.getColumn();

		/**
		 * Make sure the span does not force the component past the end of the
		 * grid.
		 */
		colspan = calculateValidColumnSpan(gc, colspan);
		rowspan = calculateValidRowSpan(gc, rowspan);

		if (rowspan == cc.getRowSpan() && colspan == cc.getColumnSpan())
			return;

		if (checkSpan(gc, colspan, rowspan)) {
			ReadOnlyConstraints rocc = new ReadOnlyConstraints(colstart, rowstart, colspan, rowspan, cc.getHorizontalAlignment(), cc.getVerticalAlignment(), cc
					.getInsets());

			SetConstraintsCommand cmd = new SetConstraintsCommand(parentview.getParentForm(), gc, rocc);
			CommandUtils.invoke(cmd, FormEditor.getEditor(gc));
		}
	}

	/**
	 * Checks if the column span can be set on the given grid component. If the
	 * component would extend off the form, then the resulting span that is
	 * returned is trimed to fit.
	 */
	public static int calculateValidColumnSpan(GridComponent gc, int colspan) {
		if (gc == null || colspan < 1)
			return 1;

		GridView parentview = gc.getParentView();
		ComponentConstraints cc = gc.getConstraints();

		int colstart = gc.getColumn();

		/**
		 * Make sure the span does not force the component past the end of the
		 * grid.
		 */
		return Math.min(colspan, parentview.getColumnCount() - colstart + 1);
	}

	/**
	 * Checks if the row span can be set on the given grid component. If the
	 * component would extend off the form, then the resulting span that is
	 * returned is trimed to fit.
	 */
	public static int calculateValidRowSpan(GridComponent gc, int rowspan) {
		if (gc == null || rowspan < 1)
			return 1;

		GridView parentview = gc.getParentView();
		ComponentConstraints cc = gc.getConstraints();

		int rowstart = gc.getRow();

		/**
		 * Make sure the span does not force the component past the end of the
		 * grid.
		 */
		return Math.min(rowspan, parentview.getRowCount() - rowstart + 1);
	}

	/**
	 * Debugging flag
	 */
	public static boolean isDebug() {
		try {
			String result = System.getProperty("jeta1.debug");
			return (result != null && result.equals("true"));
		} catch (Exception e) {

		}
		return false;
	}

	/**
	 * Saves the specified form to the specified file. If the file name ends in
	 * .xml, the form is stored in xml format. Otherwise, it is stored in binary
	 * form.
	 * 
	 * @param fpackage
	 * @param file
	 * @throws IOException
	 * @throws JMLException
	 */
	public static void saveForm(FormPackage fpackage, File file) throws IOException, JMLException {
		if (file.getName().endsWith(".xml") || file.getName().endsWith(".XML")) {
			JMLNode node = JMLUtils.writeObject(fpackage);
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
			new XMLWriter().write(writer, node);
			writer.write('\n');
			writer.flush();
			writer.close();
		}
		else {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream current_stream = new ObjectOutputStream(fos);
			current_stream.writeObject(fpackage);
			current_stream.close();
		}
	}
	public static void saveForm(OutputStream os,FormPackage fpackage, String ext) throws IOException, JMLException {
		if(".xml".equalsIgnoreCase(ext)){
			JMLNode node = JMLUtils.writeObject(fpackage);
			Writer writer = new BufferedWriter(new OutputStreamWriter(os, "UTF8"));
			new XMLWriter().write(writer, node);
			writer.write('\n');
			writer.flush();
			writer.close();
		}
		else {
			ObjectOutputStream current_stream = new ObjectOutputStream(os);
			current_stream.writeObject(fpackage);
			current_stream.close();
		}
	}

	public static String xmlFromFormPackage(FormPackage fpackage){
		if(fpackage == null) return null;
		String xml = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			FormDesignerUtils.saveForm(out,fpackage, ".xml");
			xml = out.toString();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return xml;
	}
	public static String xmlFromFormMemento(FormMemento fm){
		if(fm == null) return null;
		FormPackage fpackage = new FormPackage(fm);
		return xmlFromFormPackage(fpackage);
	}
	
	public static String xmlFromFormComponent(FormComponent fc){
		String xml = null;
		try {
			FormMemento fm = (FormMemento) fc.getExternalState(StateRequest.DEEP_COPY);
			return xmlFromFormMemento(fm);
			
		} catch (FormException e) {
			e.printStackTrace();
		}
		return xml;
	}
	
	public static String javaFromFormMemento(FormMemento fm){
		if(fm == null) return null;
		String src = null;
		try {
			String builderClass = fastTrim(fm.getCodeModel().getSourcebuilder());
			if(builderClass == null || builderClass.length() == 0 ){
				builderClass = "com.jeta.swingbuilder.codegen.builder.DefaultSourceBuilder";
			}
			Class clazz = Class.forName(builderClass);
			if(clazz == null ) return null;
			SourceBuilder builder = (SourceBuilder)clazz.newInstance();
			if(builder == null ) return null;
			src = builder.buildSource(fm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return src;
	}
	public static String javaFromFormComponent(FormComponent fc){
		String src = null;
		try {
			FormMemento fm = (FormMemento) fc.getExternalState(StateRequest.DEEP_COPY);
			src = javaFromFormMemento(fm);
			
		} catch (FormException e) {
			e.printStackTrace();
		}
		return src;
	}
	
	public static boolean saveFile(Component invoker,String ext,String extName,String extDescript,String fileName,String text){
		FileChooserConfig fcc = new FileChooserConfig(ext, new TSFileFilter(extName, extDescript));
		fcc.setParentComponent(invoker);
		fcc.setInitialDirectory(null);
		fcc.setInitialFile(fileName);
		File file = TSFileChooserFactory.showSaveDialog(fcc);
		if (file == null)
			return false;
		else {
			try {

				String path = file.getPath();
				int pos = path.lastIndexOf(ext);
				if (pos != path.length() - 5) {
					path = path + ext;
					file = new File(path);
				}

				FileOutputStream fos = new FileOutputStream(file);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
				bw.write(text);
				bw.close();
				return true;
			} catch (Exception e) {
				TSErrorDialog dlg = TSErrorDialog.createDialog(invoker, I18N.getLocalizedMessage("Error"), I18N.getLocalizedMessage("Unable to save file"), e);
				dlg.showCenterEx();
				return false;
			}
		}
	}
	

	public static Object[] getEnvVars(boolean refresh) {
		if ((ENV_VARS == null) || (refresh)) {
			List envVarDirs = new LinkedList();
			if (refresh) {
				EnvUtils.getInstance().refresh();
			}
			Map map = EnvUtils.getInstance().getEnvVars();
			Object[] envVars = map.keySet().toArray();
			Arrays.sort(envVars);
			for (int index = 0; index < envVars.length; index++) {
				String envVar = (String) envVars[index];

				String value = EnvUtils.getInstance().getEnvVar(envVar);
				try {
					File file = new File(value);
					if (file.exists() && file.isDirectory()) {
						envVarDirs.add(envVar);
					}
				} catch (Exception e) {
				}

			}
			
			ENV_VARS = envVarDirs.toArray();
			Arrays.sort(ENV_VARS);
		}
		return ENV_VARS;
	}
	
	public static Object getFormProperty(String key){
		return m_formprops.get(key);
	}
	
	public static void setFormProperty(String key,Object value){
		m_formprops.put(key, value);
	}
	
	/**
	 * Testing flag
	 */
	public static boolean isTest() {
		if(m_formprops.containsKey(JETA1_TEST)){
			Boolean ret = (Boolean)m_formprops.get(JETA1_TEST);
			if(ret != null) return ret.booleanValue(); 
		}
		return false;
	}
	public static void setTest(boolean test){
		m_formprops.put(JETA1_TEST, Boolean.valueOf(test));
	}
	/**
	 * Simple flag
	 */
	public static boolean isSimple() {
		if(m_formprops.containsKey(JETA1_SIMPLE)){
			Boolean ret = (Boolean)m_formprops.get(JETA1_SIMPLE);
			if(ret != null) return ret.booleanValue(); 
		}
		return false;
	}
	public static void setSimple(boolean simple){
		m_formprops.put(JETA1_SIMPLE, Boolean.valueOf(simple));
	}
	/**
	 * Fixed flag
	 */
	public static boolean isFixed() {
		if(m_formprops.containsKey(JETA1_FIXED)){
			Boolean ret = (Boolean)m_formprops.get(JETA1_FIXED);
			if(ret != null) return ret.booleanValue(); 
		}
		return false;
	}
	
	public static String getFixedColSpec(){
		if(m_formprops.containsKey(JETA1_FIXED_COLSPEC)){
			String ret = (String)m_formprops.get(JETA1_FIXED_COLSPEC);
			return ret; 
		}
		return null;
	}
	public static String getFixedRowSpec(){
		if(m_formprops.containsKey(JETA1_FIXED_ROWSPEC)){
			String ret = (String)m_formprops.get(JETA1_FIXED_ROWSPEC);
			return ret; 
		}
		return null;
	}
	
	public static void setFixed(boolean fixed,int cols,int colodd,int coleven,int rows,int rowodd, int roweven){
		m_formprops.put(JETA1_FIXED, Boolean.valueOf(fixed));
		if(fixed){
			setSimple(true);
			StringBuffer colspec = new StringBuffer();
			for (int col = 1; col <= cols; col++) {
				if (col > 1)
					colspec.append(",");
				colspec.append((col%2==0)?"f:"+coleven+"PX:n":"f:"+colodd+"PX:n");

			}
			StringBuffer rowspec = new StringBuffer();
			for (int row = 1; row <= rows; row++) {
				if (row > 1)
					rowspec.append(",");
				rowspec.append((row%2==0)?"f:"+roweven+"PX:n":"f:"+rowodd+"PX:n");
			}
			m_formprops.put(JETA1_FIXED_COLSPEC, colspec.toString());
			m_formprops.put(JETA1_FIXED_ROWSPEC, rowspec.toString());
		}
	}
	
}
