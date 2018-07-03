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

package com.jeta.swingbuilder.gui.main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.forms.store.memento.ComponentMemento;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.forms.store.memento.FormPackage;
import com.jeta.forms.store.memento.StateRequest;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.codegen.ForwardEngineer;
import com.jeta.swingbuilder.gui.components.SystemPropertiesPanel;
import com.jeta.swingbuilder.gui.components.TSErrorDialog;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jeta.swingbuilder.gui.export.ComponentNamesExporter;
import com.jeta.swingbuilder.gui.export.ExportNamesView;
import com.jeta.swingbuilder.gui.filechooser.FileChooserConfig;
import com.jeta.swingbuilder.gui.filechooser.TSFileChooserFactory;
import com.jeta.swingbuilder.gui.filechooser.TSFileFilter;
import com.jeta.swingbuilder.gui.formmgr.FormManagerDesignUtils;
import com.jeta.swingbuilder.gui.project.UserPreferencesNames;
import com.jeta.swingbuilder.gui.project.UserPreferencesView;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.interfaces.userprops.TSUserPropertiesUtils;

/**
 * Controller class for MainFrame
 * 
 * @author Jeff Tassin
 */
public class MainPanelController extends FormEditorController {
	private MainPanel m_frame;

	static final int CLOSE_OK = 0;

	static final int CLOSE_CANCELED = 1;

	/**
	 * The controller for the MainFrame window. Handles all user events.
	 * 
	 * @param frame
	 */
	MainPanelController(MainPanel frame) {
		super(frame, frame.m_form_popup);
		m_frame = frame;
		assignAction(MainPanelNames.ID_CREATE_FORM, new NewFormAction());
		assignAction(MainPanelNames.ID_OPEN_FORM, new OpenFormAction());
		assignAction(MainPanelNames.ID_SHOW_FORM, new ShowFormAction());
		assignAction(MainPanelNames.ID_SAVE_FORM, new SaveFormAction());
		assignAction(MainPanelNames.ID_SAVE_FORM_AS, new SaveAsAction());
		assignAction(MainPanelNames.ID_CLOSE_FORM, new CloseFormAction());

		if (FormDesignerUtils.isDebug()) {
			assignAction(FormEditorNames.ID_EXPORT_COMPONENT_NAMES, new ExportNamesAction());
		}
		assignAction(MainPanelNames.ID_FORWARD_ENGINEER, new ForwardEngineerAction());
		assignAction(MainPanelNames.ID_SYSTEM_PROPERTIES, new SystemPropertiesAction());
		assignAction(MainPanelNames.ID_ENV_SETTINGS, new UserPreferencesAction());

		assignAction(MainPanelNames.ID_ABOUT, new AboutAction());
		assignAction(MainPanelNames.ID_EXIT, new ExitAction());

		assignAction(MainPanelNames.ID_FORM_PROPERTIES, new TogglePropertiesFrame());
		m_frame.getComponentByName(FormPropertiesNames.ID_CLOSE_FRAME).addMouseListener(new ClosePropertiesFrame());
		m_frame.getComponentByName(FormPropertiesNames.ID_DOCK_FRAME).addMouseListener(new DockPropertiesFrame());

		/**
		 * clear the clipboard of any previous form components. This causes
		 * problems with falsely enabling the paste command
		 */
		try {

			java.awt.Toolkit kit = java.awt.Toolkit.getDefaultToolkit();
			java.awt.datatransfer.Clipboard clipboard = kit.getSystemClipboard();
			java.awt.datatransfer.Transferable transferable = clipboard.getContents(null);
			if (com.jeta.swingbuilder.gui.dnd.FormObjectFlavor.isDesignerFlavorSupported(transferable)) {
				clipboard.setContents(new java.awt.datatransfer.StringSelection(""), null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return the current form editor
	 */
	public FormEditor getCurrentEditor() {
		return m_frame.getCurrentEditor();
	}

	/**
	 * Closes the current form.
	 * 
	 * @return CLOSE_OK(0) if the form was successfully closed CLOSE_CANCELED
	 *         (1) if the operation was canceled by the user
	 */
	private int closeEditor(FormEditor editor) {
		if (editor != null) {
			MainPanelPlugin plugin = (MainPanelPlugin) JETARegistry.lookup(MainPanelPlugin.COMPONENT_ID);
			if(plugin != null){
				boolean r = plugin.CloseFormAction(editor.getForm(),editor.isModified());
				if(!r) return CLOSE_CANCELED;
			}
			
			if (editor.isModified() && editor.isLinked()) {
				String filename = editor.getForm().getFileName();
				if (filename == null)
					filename = I18N.getLocalizedMessage("New Form");
				String msg = I18N.format("Form_is_modified_save_1", filename);
				String title = I18N.getLocalizedMessage("Confirm");
				int result = JOptionPane.showConfirmDialog(m_frame, msg, title, JOptionPane.YES_NO_CANCEL_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					if (saveForm(false) == null)
						return CLOSE_CANCELED;
				}
				else if (result == JOptionPane.CANCEL_OPTION) {
					return CLOSE_CANCELED;
				}
			}
			FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
			fmgr.closeForm(editor.getForm().getId());
			FormManagerDesignUtils.clearUnreferencedForms();
		}
		return CLOSE_OK;
	}


	private void postSaveForm(boolean saveAs) {
		final boolean sa = saveAs;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				saveForm(sa);
			}
		});
	}

	/**
	 * Saves the form to a file.
	 * 
	 * @param saveAs
	 *            if true, then prompts the user for a new file name. If false,
	 *            uses the current filename for the selected form.
	 */
	private File saveForm(boolean saveAs) {
		m_frame.unitTest();
		m_frame.getPropertyContainer().stopEditing();

		FormEditor editor = m_frame.getCurrentEditor();
		if(editor == null) return null;
		editor.saveFocusPolicy();
		FormComponent fc = editor.getFormComponent();

		MainPanelPlugin plugin = (MainPanelPlugin) JETARegistry.lookup(MainPanelPlugin.COMPONENT_ID);
		if(plugin != null){
			boolean r = plugin.SaveFormAction(fc, saveAs);
			if(!r) return null;
		}
		/**
		 * the list of form files that we are saving. This is the current form
		 * and any linked forms
		 */
		LinkedList files = new LinkedList();
		File file = null;
		try {
			if (fc != null) {
				FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);

				String path = fc.getAbsolutePath();
				if (saveAs)
					path = null;

				if (path == null) {
					FileChooserConfig fcc = new FileChooserConfig(".xml", new TSFileFilter("jfrm,xml", "Form Files(*.jfrm,*.xml)"));
					fcc.setParentComponent(m_frame);
					if (TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_STORE_AS_XML, false))
						fcc.setInitialFile(fc.getCodeModel().getFileName()+".xml");
					else
						fcc.setInitialFile(fc.getCodeModel().getFileName()+".jfrm");

					file = TSFileChooserFactory.showSaveDialog(fcc);
					if (file == null)
						return null;

					path = file.getPath();
					int pos = path.lastIndexOf(".jfrm");
					if (pos != path.length() - 5) {
						pos = path.lastIndexOf(".xml");
						if (pos != path.length() - 4) {
							String ext = ".jfrm";
							if (TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_STORE_AS_XML, false))
								ext = ".xml";

							path = path + ext;
							file = new File(path);
						}
					}

					/** see if form is already opened */
					if (fmgr.isOpened(file.getPath())) {
						String msg = I18N.format("Form_is_already_opened_in_editor_1", file.getName());
						String title = I18N.getLocalizedMessage("Error");
						JOptionPane.showMessageDialog(m_frame, msg, title, JOptionPane.ERROR_MESSAGE);
						return null;
					}
				}
				else {
					file = new File(path);
				}

				String oldid = fc.getId();
				path = file.getPath();

				FormPackage fpackage = new FormPackage(fc.getExternalState(StateRequest.SHALLOW_COPY));
				FormDesignerUtils.saveForm(fpackage, file);

				files.add(path);
				if (!path.equals(oldid)) {
					// the clone operation will update the forms ids with the
					// FormManager
					fc = FormManagerDesignUtils.clone(fmgr, editor, path);
					FormManagerDesignUtils.registerForms(fmgr, fc);
					editor.setFormComponent(fc);
					fmgr.installHandlers(m_frame, fc);
					installHandlers(editor);
					fmgr.activateForm(editor.getTopParent().getId());
					editor.activate();

					/** unit test after the clone */
					m_frame.unitTest();
				}
				m_frame.formNameChanged(fc);


				editor.clearUndoableEdits();
				FormManagerDesignUtils.clearUnreferencedForms();
				m_frame.unitTest();

				return file;

			}
		} catch (Exception e) {
			TSErrorDialog dlg = null;

			String caption = I18N.getLocalizedMessage("Error.  Unable to save file");
			if (file == null) {
				dlg = TSErrorDialog.createDialog(m_frame, caption, null, e);
			}
			else {
				String msg = I18N.format("Unable_to_save_file_1", file.getName());
				dlg = TSErrorDialog.createDialog(m_frame, caption, msg, e);
			}

			dlg.showCenter();
		}
		m_frame.updateModifiedStatus();
		return null;
	}


	/**
	 * Show About dialog
	 */
	public class AboutAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, m_frame, true);
			AboutView view = new AboutView();
			dlg.setPrimaryPanel(view);
			dlg.setTitle(I18N.getLocalizedMessage("About"));
			dlg.pack();
			dlg.showOkButton(false);
			dlg.setCloseText(I18N.getLocalizedMessage("Close"));
			Dimension d = dlg.getSize();
			if (JETAToolbox.isWindows() || JETAToolbox.isOSX()) {
				d.width -= 2;
				dlg.setSize(d);
			}
			dlg.showCenter();
		}
	}

	/**
	 * Closes the form
	 */
	public class CloseFormAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			closeEditor(getCurrentEditor());
		}
	}

	public class TogglePropertiesFrame implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			m_frame.getDocker().togglePropertiesFrame();
		}
	}

	public class ClosePropertiesFrame extends MouseAdapter {
		public void mouseClicked(MouseEvent evt) {
			m_frame.getDocker().togglePropertiesFrame();
		}
	}

	public class DockPropertiesFrame extends MouseAdapter {
		public void mouseClicked(MouseEvent evt) {
			m_frame.getDocker().dockPropertiesFrame(null);
		}
	}

	/**
	 * Exit action handler
	 */
	public class ExitAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			m_frame.shutDown();
		}
	}

	/**
	 * Exports the component names on a given form.
	 */
	public class ExportNamesAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			FormEditor editor = m_frame.getCurrentEditor();
			if (editor != null) {
				GridComponent gc = editor.getSelectedComponent();
				if (gc instanceof FormComponent) {
					JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, m_frame, true);
					ExportNamesView view = new ExportNamesView();
					dlg.setTitle(I18N.getLocalizedMessage("Export Component Names"));
					dlg.setPrimaryPanel(view);
					dlg.setSize(dlg.getPreferredSize());
					dlg.showCenter();
					if (dlg.isOk()) {
						view.saveToModel();
						ComponentNamesExporter exporter = new ComponentNamesExporter(view);
						exporter.exportToClipboard((FormComponent) gc);
					}
				}
			}
		}
	}

	/**
	 * Exports a Form to Java Source
	 */
	public class ForwardEngineerAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			FormEditor editor = m_frame.getCurrentEditor();
			if (editor != null) {
				try {
					FormComponent fc = editor.getFormComponent();
					FormMemento fm = (FormMemento) fc.getExternalState(StateRequest.DEEP_COPY);
					ForwardEngineer fe = new ForwardEngineer();
					fe.generate(m_frame, fm);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Focus Management
	 */
	public class FocusManagerAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			FormEditor editor = m_frame.getCurrentEditor();
			if (editor != null) {
				boolean show_focus = false;
				Collection comps = m_frame.getComponentsByName(MainPanelNames.ID_FOCUS_MANAGER);
				Iterator iter = comps.iterator();
				while (iter.hasNext()) {
					Component comp = (Component) iter.next();
					if (comp instanceof javax.swing.AbstractButton) {
						show_focus = ((javax.swing.AbstractButton) comp).isSelected();
						break;
					}
				}
				editor.setFocusViewVisible(show_focus);
				editor.revalidate();
				editor.repaint();
			}
		}
	}

	/**
	 * ActionListener for creating a new form.
	 */
	public class NewFormAction implements ActionListener {
		/**
		 * @param evt
		 */
		public void actionPerformed(ActionEvent evt) {
			FormEditor editor = null;
			if(!FormDesignerUtils.isFixed())
				editor = new FormEditor(m_frame, 7, 7);
			else
				editor = new FormEditor(m_frame, FormDesignerUtils.getFixedColSpec(), FormDesignerUtils.getFixedRowSpec());
			MainPanelPlugin plugin = (MainPanelPlugin) JETARegistry.lookup(MainPanelPlugin.COMPONENT_ID);
			if(plugin != null){
				boolean r = plugin.NewFormAction(editor.getForm());
				if(!r) return;
			}
			FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
			fmgr.registerForm(editor.getForm());
			m_frame.addForm(editor);
			editor.getForm().setControlButtonsVisible(false);
		}
	}

	/**
	 * Opens a form from a previously saved file
	 */
	public class OpenFormAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			try {
				MainPanelPlugin plugin = (MainPanelPlugin) JETARegistry.lookup(MainPanelPlugin.COMPONENT_ID);
				if(plugin != null){
					InputStream is = plugin.OpenFormAction();
					if(is != null){
						FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
						fmgr.deactivateForms(m_frame.getCurrentEditor());
						FormComponent fc = fmgr.openForm(is);
						fmgr.activateForm(fc.getId());
						fmgr.showForm(fc.getId());
						return;
					}
				}
				FileChooserConfig fcc = new FileChooserConfig(".form", new TSFileFilter("jfrm,xml", "Form Files(*.jfrm,*.xml)"));
				fcc.setParentComponent(m_frame);
				File f = TSFileChooserFactory.showOpenDialog(fcc);
				if (f != null) {
					FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
					fmgr.deactivateForms(m_frame.getCurrentEditor());
					FormComponent fc = fmgr.openLinkedForm(f);
					fmgr.activateForm(fc.getId());
					fmgr.showForm(fc.getId());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Saves the form to a file
	 */
	public class SaveFormAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			saveForm(false);
		}
	}

	/**
	 * Saves the form to a new file
	 */
	public class SaveAsAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			saveForm(true);
		}
	}

	/**
	 * Shows the form as it would appear when running
	 */
	public class ShowFormAction implements ActionListener {

		public void actionPerformed(ActionEvent evt) {
			try {
				int index0 = 0;
				int index = -1;
				FormEditor editor0 = m_frame.getCurrentEditor();
				JTabbedPane jtabbed = new JTabbedPane();
				Iterator iter = m_frame.getEditors().iterator();
				while (iter.hasNext()) {
					FormEditor editor = (FormEditor)iter.next();
					if (editor != null) {
						editor.unitTest();
						FormComponent fc = editor.getFormComponent();
						String filename = fc.getFileName();
						if (filename == null)
							filename = I18N.getLocalizedMessage("New Form");
						ComponentMemento cm = fc.getExternalState(StateRequest.DEEP_COPY);
	
						FormUtils.setDesignMode(false);
	
						fc = FormComponent.create();
						fc.setState(cm);
	
						FormPanel jetapanel = new FormPanel(fc);
						fc.postInitialize(jetapanel);
						
						jtabbed.addTab(filename,null,jetapanel);
						index ++;
						if(editor0 == editor){
							index0 = index;
						}
					}
				}
				jtabbed.setSelectedIndex(index0);
				
				JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, m_frame, true);
				dlg.setTitle(I18N.getLocalizedMessage("Show Form"));
				dlg.setPrimaryPanel(jtabbed);
				Dimension size = dlg.getPreferredSize();
				size.setSize(size.getWidth(), size.getHeight() + 20);
				dlg.setSize(size);
				dlg.showOkButton(false);
				dlg.setResizable(true);
				dlg.showCenterEx();
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				FormUtils.setDesignMode(true);
			}
		}
	}

	public class SystemPropertiesAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, m_frame, true);
			dlg.setTitle(I18N.getLocalizedMessage("System Properties"));
			SystemPropertiesPanel view = new SystemPropertiesPanel();
			dlg.setPrimaryPanel(view);
			dlg.setSize(dlg.getPreferredSize());
			dlg.showCenterEx();
		}
	}

	public class UserPreferencesAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, m_frame, true);
			UserPreferencesView view = new UserPreferencesView();
			dlg.setPrimaryPanel(view);
			dlg.setTitle(I18N.getLocalizedMessage("Preferences"));
			dlg.setSize(dlg.getPreferredSize());
			dlg.showCenterEx();
			if (dlg.isOk()) {
				view.saveToModel();
				Collection editors = m_frame.getEditors();
				Iterator iter = editors.iterator();
				while (iter.hasNext()) {
					FormEditor editor = (FormEditor) iter.next();
					editor.updatePreferences();
				}
			}
		}
	}

}
