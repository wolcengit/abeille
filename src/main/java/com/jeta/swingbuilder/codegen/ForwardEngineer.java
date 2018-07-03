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

package com.jeta.swingbuilder.codegen;

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import javax.swing.JTabbedPane;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.store.memento.FormCodeModel;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.forms.store.memento.FormPackage;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.framework.JETADialogListener;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.codegen.builder.SourceBuilder;
import com.jeta.swingbuilder.codegen.gui.config.FormCodeModelView;
import com.jeta.swingbuilder.codegen.gui.editor.SyntaxEditor;
import com.jeta.swingbuilder.gui.components.TSErrorDialog;
import com.jeta.swingbuilder.gui.filechooser.FileChooserConfig;
import com.jeta.swingbuilder.gui.filechooser.TSFileChooserFactory;
import com.jeta.swingbuilder.gui.filechooser.TSFileFilter;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * Front end class for invoking code generation.
 * 
 * @author Jeff Tassin
 */
public class ForwardEngineer {
	/**
	 * This is a map of form ids to the last filename used to store generated
	 * code for that form. It is used to popuplate the file save dialog so the
	 * user does not have to re-type the file name every time.
	 */
	private Component invoker = null;
	private String src = null;
	private String xml = null;
	private FormCodeModel cgenmodel = null;
	private FormMemento fm = null;
	private SyntaxEditor editorJava = null;
	private SyntaxEditor editorXml = null;
	private FormCodeModelView view = null;

	public ForwardEngineer() {

	}

	public void generate(Component invoker, FormMemento infm) {
		this.invoker = invoker;
		this.fm = infm;
		try {
			FormUtils.setDesignMode(false);

			cgenmodel = fm.getCodeModel();

			src = FormDesignerUtils.javaFromFormMemento(fm);
			xml = FormDesignerUtils.xmlFromFormMemento(fm);
			
			
			editorJava = new SyntaxEditor();
			editorJava.setText(src);
			editorJava.setSyntaxStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
			editorJava.setEditable(false);
			
			editorXml = new SyntaxEditor();
			editorXml.setSyntaxStyle(SyntaxConstants.SYNTAX_STYLE_XML);
			editorXml.setText(xml);
			editorXml.setEditable(false);
			
			view = new FormCodeModelView(cgenmodel);

			
			final JTabbedPane jtabbed = new JTabbedPane();
			jtabbed.addTab(I18N.getLocalizedMessage("Java"),null,editorJava);
			jtabbed.addTab(I18N.getLocalizedMessage("XML"),null,editorXml);
			jtabbed.addTab(I18N.getLocalizedMessage("Option"),null,view);

			final JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, invoker, true);
			dlg.setTitle(I18N.getLocalizedMessage("Code Generation"));
			dlg.setPrimaryPanel(jtabbed);
			dlg.setSize(dlg.getPreferredSize());
			dlg.setOkText(I18N.getLocalizedMessage("Save"));
			dlg.setResizable(true);

			String formid = fm.getId();
			String formfile = cgenmodel.getFileName()+".java";
			String formxml = cgenmodel.getFileName()+".xml";
			dlg.addDialogListener(new JETADialogListener() {
				public boolean cmdOk() {
					if(jtabbed.getSelectedIndex() == 0){
						String ext = ".java";
						String extName = "Java";
						String extDescript = "Java Files(*.java)";
						FormDesignerUtils.saveFile(dlg,ext,extName,extDescript,cgenmodel.getFileName()+".java",src);
					}else if(jtabbed.getSelectedIndex() == 1){
						String ext = ".xml";
						String extName = "Xml";
						String extDescript = "Xml Files(*.xml)";
						FormDesignerUtils.saveFile(dlg,ext,extName,extDescript,cgenmodel.getFileName()+".xml",xml);
					}else if(jtabbed.getSelectedIndex() == 2){
						view.saveToModel();
						src = FormDesignerUtils.javaFromFormMemento(fm);
						xml = FormDesignerUtils.xmlFromFormMemento(fm);
						editorJava.setText(src);
						editorXml.setText(xml);
						jtabbed.setSelectedIndex(0);
					}
					return false;
				}
			});
			dlg.showCenterEx();
		} finally {
			FormUtils.setDesignMode(true);
		}

	}
	
}
