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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import com.jeta.forms.components.image.ImageComponent;
import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.components.ComponentFactory;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.form.FormAccessor;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridViewEvent;
import com.jeta.forms.gui.form.GridViewListener;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.jgoodies.forms.layout.CellConstraints;
import com.jeta.jgoodies.forms.layout.FormLayout;
import com.jeta.open.gui.framework.JETAContainer;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.gui.framework.UIDirector;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.open.support.CompositeComponentFinder;
import com.jeta.swingbuilder.common.ComponentNames;
import com.jeta.swingbuilder.gui.components.TSButtonBar;
import com.jeta.swingbuilder.gui.components.TSCell;
import com.jeta.swingbuilder.gui.components.TSComponentNames;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jeta.swingbuilder.gui.formmgr.EditorManager;
import com.jeta.swingbuilder.gui.formmgr.FormManagerView;
import com.jeta.swingbuilder.gui.handler.AbstractMouseHandler;
import com.jeta.swingbuilder.gui.properties.PropertyPaneContainer;
import com.jeta.swingbuilder.gui.undo.UndoManagerView;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.interfaces.app.ObjectStore;
import com.jeta.swingbuilder.main.FormsInitializer;
import com.jeta.swingbuilder.resources.Icons;
import com.jeta.swingbuilder.support.DesignTimeComponentFinder;

/**
 * The main frame window for the application
 * 
 * @author Jeff Tassin
 */
public class MainPanel extends JPanel implements ComponentSource, GridViewListener, ActionListener, JETAContainer, EditorManager {
	/**
	 * The buttonbar that contains the property pane, FormSpec views, and
	 * CellConstaint view
	 */
	private TSButtonBar m_buttonbar;

	/**
	 * Frame window that displays the properties for a selected Java Bean
	 */
	private PropertyPaneContainer m_propsview;

	/**
	 * Panels that show the RowSpec and ColumnSpec for the selected row/column
	 * in the current form
	 */
	private SpecView m_col_spec_panel;

	private SpecView m_row_spec_panel;

	/**
	 * View that displays the CellConstraints for the selected cell
	 */
	private CellConstraintsView m_cc_view;
	
	private CellSimpleView m_cs_view;

	/**
	 * Split pane.
	 */
	private JSplitPane m_split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	/**
	 * The controller for this frame
	 */
	private MainPanelController m_controller;

	/**
	 * The component finder for this frame. Component finders are used to
	 * implement getComponentByName methods.
	 */
	private CompositeComponentFinder m_composite_finder = new CompositeComponentFinder();

	/**
	 * Tab pane that contains the forms
	 */
	private JTabbedPane m_forms_tab = new JTabbedPane(JTabbedPane.BOTTOM);

	/**
	 * Listener for tab change events on m_forms_tab
	 */
	private TabListener m_tab_listener;

	/**
	 * The toolbar that displays the components that we can place on a frame
	 */
	private ComponentsToolBar m_component_tools;

	/**
	 * The form manager for the application.
	 */
	private MainPanelFormManager m_formmgr;

	/**
	 * Handles enabling/disabling
	 */
	private MainPanelUIDirector m_uidirector;

	/**
	 * Popup menu for right-context mouse clicks
	 */
	FormEditorPopupMenu m_form_popup = new FormEditorPopupMenu();

	/**
	 * Then menu for this frame
	 */
	private JMenuBar m_menuBar; 
	/**
	 * The toolbar for this frame.
	 */
	private JToolBar m_toolbar;

	/**
	 * The status bar cell for this frame. Shows current project information.
	 */
	private TSCell m_status_cell;

	/**
	 * The properties view
	 */
	private FormPanel m_properties_view;

	/**
	 * The main container the contains the tool palette, form editor, and
	 * optionally the properties window
	 */
	private JPanel m_main_panel;

	/**
	 * Responsible for handling dock behavior for the properties window
	 */
	private FrameDocker m_docker;

	public static final String ID_FRAME_BOUNDS = "main.frame.bounds";

	
	private FormManagerView m_formMgrview ;
	private UndoManagerView m_formUndoview;
	
	/**
	 * ctor
	 */
	public MainPanel() {
		FormsLogger.debug("Creating MainPanel");

		try {
			m_component_tools = new ComponentsToolBar();

			m_formmgr = new MainPanelFormManager(this);

			JETARegistry.rebind(FormManager.COMPONENT_ID, m_formmgr);
			JETARegistry.rebind(EditorManager.COMPONENT_ID, this);
			JETARegistry.rebind(ComponentSource.COMPONENT_ID, this);
			JETARegistry.rebind(JETAToolbox.APPLICATION_FRAME, this);

			this.setLayout(new BorderLayout());

			createMenu();
			createToolBar();
			createStatusBar();

			JPanel panel = new JPanel(new BorderLayout());
			panel.add(m_menuBar, BorderLayout.NORTH);
			panel.add(m_toolbar, BorderLayout.CENTER);
			this.add(panel, BorderLayout.NORTH);

			/**
			 * create the main panel
			 */
			m_split.setResizeWeight(1.0);
			m_main_panel = new JPanel();
			m_main_panel.setLayout(new BorderLayout());
			m_main_panel.add(m_split, BorderLayout.CENTER);

			JPanel editor_panel = new JPanel(new BorderLayout());
			editor_panel.add(m_component_tools, BorderLayout.WEST);
			editor_panel.add(m_forms_tab, BorderLayout.CENTER);

			JPanel controls_panel = (JPanel) createControlsView();
			controls_panel.setPreferredSize(FormDesignerUtils.getWindowDimension(controls_panel, 150, 200));

			m_docker = new FrameDocker(m_main_panel, editor_panel, m_split, m_properties_view);

			m_composite_finder.add(new DesignTimeComponentFinder(m_component_tools));
			m_composite_finder.add(new DesignTimeComponentFinder(m_forms_tab));
			m_composite_finder.add(new DesignTimeComponentFinder(m_form_popup));

			this.add(m_main_panel);

			m_tab_listener = new TabListener();
			m_forms_tab.addChangeListener(m_tab_listener);

			FormUtils.setDesignMode(true);

			initializeFrameBounds();

			m_split.add(editor_panel);
			m_split.add(controls_panel);

			m_uidirector = new MainPanelUIDirector(this);
			m_controller = new MainPanelController(this);

			updateComponents();
			
			if (FormDesignerUtils.isDebug()) {
				m_formMgrview = new FormManagerView();
				m_forms_tab.addTab(I18N.getLocalizedMessage("Form Manager"),FormDesignerUtils.loadImage(Icons.WINDOWS_16), m_formMgrview);
				m_formMgrview.reload();
				
				m_formUndoview = new UndoManagerView(this);
				m_forms_tab.addTab(I18N.getLocalizedMessage("Undo Manager"),FormDesignerUtils.loadImage(Icons.WINDOWS_16), m_formUndoview);
				m_formUndoview.reload();
			}
			
		} catch (Exception e) {
			FormsLogger.debug(e);
		}
	}

	/**
	 * Called when the button bar is updated
	 */
	public void actionPerformed(ActionEvent evt) {
		Component comp = m_buttonbar.getCurrentView();
		if (comp instanceof ControlsScrollPane) {
			/** loads the view if it is not already loaded */
			((ControlsScrollPane) comp).initialize();
		}

		FormEditor editor = getCurrentEditor();
		if (editor != null) {
			GridComponent gc = editor.getSelectedComponent();
			Component view = getCurrentControlsView();
			if (view == null)
				return;

			if (view == m_propsview) {
				m_propsview.update(gc);
			}
			else if (view == m_col_spec_panel) {
				m_col_spec_panel.update(gc);
			}
			else if (view == m_row_spec_panel) {
				m_row_spec_panel.update(gc);
			}
			else if (view == m_cc_view) {
				m_cc_view.update(gc);
			}
			else if (view == m_cs_view) {
				m_cs_view.update(gc);
			}
		}
	}

	/**
	 * Adds the tab listener to the forms tab
	 */
	private void addTabListener() {
		m_forms_tab.removeChangeListener(m_tab_listener);
		m_forms_tab.addChangeListener(m_tab_listener);
	}

	/**
	 * Adds the given form to the tab window
	 */
	public void addForm(FormEditor editor) {
		editor.addListener(this);
		m_controller.installHandlers(editor);

		removeTabListener();
		deactivateAllForms();

		if (editor.isLinked()) {
			String filename = editor.getForm().getFileName();
			if (filename == null)
				filename = I18N.getLocalizedMessage("New Form");

			m_forms_tab.addTab(filename, FormDesignerUtils.loadImage(Icons.LINKED_FORM_16), editor);
		}
		else {
			m_forms_tab.addTab(editor.getForm().getName(), FormDesignerUtils.loadImage(Icons.EMBEDDED_FORM_16), editor);
		}

		m_forms_tab.setSelectedIndex(m_forms_tab.getTabCount() - 1);
		m_formmgr.activateForm(editor.getTopParent().getId());
		addTabListener();
	}

	/**
	 * Creates the panel to the right of the design window. This panel contains
	 * the properties and grid controls.
	 */
	private Container createControlsView() {
		m_propsview = new PropertyPaneContainer();

		m_buttonbar = new TSButtonBar();
		m_buttonbar.addListener(this);

		m_buttonbar.addView(I18N.getLocalizedMessage("Component"), m_propsview, FormDesignerUtils.loadImage(Icons.COMPONENT_16));

		/**
		 * lazily load the views to speed up application startup.
		 */
		JScrollPane scroll = null;
		if(!FormDesignerUtils.isSimple()){
			scroll = new ControlsScrollPane() {
				protected Component loadView() {
					m_col_spec_panel = new SpecView("com/jeta/swingbuilder/gui/main/columnSpec.frm", false);
					return m_col_spec_panel;
				}
			};
			m_buttonbar.addView(I18N.getLocalizedMessage("Column"), scroll, FormDesignerUtils.loadImage(Icons.COLUMN_16));
	
			scroll = new ControlsScrollPane() {
				protected Component loadView() {
					m_row_spec_panel = new SpecView("com/jeta/swingbuilder/gui/main/rowSpec.frm", true);
					return m_row_spec_panel;
				}
			};
			m_buttonbar.addView(I18N.getLocalizedMessage("Row"), scroll, FormDesignerUtils.loadImage(Icons.ROW_16));
	
			scroll = new ControlsScrollPane() {
				protected Component loadView() {
					m_cc_view = new CellConstraintsView();
					return m_cc_view;
				}
			};
			m_buttonbar.addView(I18N.getLocalizedMessage("Cell"), scroll, FormDesignerUtils.loadImage(Icons.CELL_16));
		}else{
			if(!FormDesignerUtils.isFixed()){
				scroll = new ControlsScrollPane() {
					protected Component loadView() {
						m_cs_view = new CellSimpleView();
						return m_cs_view;
					}
				};
				m_buttonbar.addView(I18N.getLocalizedMessage("Cell"), scroll, FormDesignerUtils.loadImage(Icons.CELL_16));
			}
		}
		m_buttonbar.updateView();

		m_properties_view = new FormPanel("com/jeta/swingbuilder/gui/main/formProperties.jfrm");
		
		ImageComponent btn = (ImageComponent) m_properties_view.getComponentByName(FormPropertiesNames.ID_DOCK_FRAME);
		btn.setVisible(false);
		
		FormAccessor fa = m_properties_view.getFormAccessor(FormPropertiesNames.ID_MAIN_FORM);
		fa.replaceBean(FormPropertiesNames.ID_PROPERTIES_BAR, m_buttonbar);
		m_composite_finder.add(new DesignTimeComponentFinder(m_properties_view));
		return m_properties_view;
	}


	/**
	 * @return the current view in the button bar
	 */
	Component getCurrentControlsView() {
		Component view = m_buttonbar.getCurrentView();
		if (view instanceof JScrollPane) {
			view = ((JScrollPane) view).getViewport().getView();
		}
		return view;
	}


	public JButton i18n_createToolBarButton(String cmd, String imageFile, String toolTip) {
		JButton button = new JButton(FormDesignerUtils.loadImage(imageFile)) {
			public boolean isFocusTraversable() {
				return false;
			}
		};

		button.setMargin(new java.awt.Insets(1, 0, 1, 0));
		button.setName(cmd);
		button.setActionCommand(cmd);
		if (toolTip != null)
			button.setToolTipText(I18N.getLocalizedMessage(toolTip));

		if (!JETAToolbox.isOSX()) {
			button.setBorderPainted(false);
			button.setFocusPainted(false);
		}
		return button;
	}

	public javax.swing.AbstractButton i18n_createToolBarToggleButton(String cmd, String unselImage, String selImage, String toolTip) {

		JToggleButton button = new JToggleButton(FormDesignerUtils.loadImage(unselImage)) {
			public boolean isFocusTraversable() {
				return false;
			}
		};
		button.setSelectedIcon(FormDesignerUtils.loadImage(selImage));
		button.setName(cmd);
		button.setActionCommand(cmd);
		button.setMargin(new java.awt.Insets(1, 1, 1, 1));

		if (toolTip != null)
			button.setToolTipText(I18N.getLocalizedMessage(toolTip));

		return button;
	}
	/**
	 * Creates the menus for this frame window.
	 */
	public JMenuItem i18n_createMenuItem(String itemText, String actionCmd, KeyStroke keyAccelerator) {
		JMenuItem item = new JMenuItem(I18N.getLocalizedMessage(itemText));
		item.setName(actionCmd);
		item.setActionCommand(actionCmd);
		if (keyAccelerator != null)
			item.setAccelerator(keyAccelerator);
		return item;
	}
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		m_menuBar = menuBar;
		
		DesignTimeComponentFinder menu_finder = new DesignTimeComponentFinder(menuBar);
		m_composite_finder.add(menu_finder);

		JMenu menu = new JMenu(I18N.getLocalizedMessage("File"));
		menu.setName(MainPanelNames.ID_ABEILLEFORMS_FILE);
		menu.add(i18n_createMenuItem("New Form", MainPanelNames.ID_CREATE_FORM, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK, false)));
		menu.add(i18n_createMenuItem("Open Form", MainPanelNames.ID_OPEN_FORM, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK, false)));
		menu.add(i18n_createMenuItem("Save", MainPanelNames.ID_SAVE_FORM, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK, false)));
		menu.add(i18n_createMenuItem("Save As", MainPanelNames.ID_SAVE_FORM_AS, null));
		menu.add(i18n_createMenuItem("Close Form", MainPanelNames.ID_CLOSE_FORM, null));
		menu.addSeparator();

		menu.add(i18n_createMenuItem("Exit", MainPanelNames.ID_EXIT, null));
		menuBar.add(menu);

		menu = new JMenu(I18N.getLocalizedMessage("Edit"));
		menu.setName(MainPanelNames.ID_ABEILLEFORMS_EDIT);
		menu.add(i18n_createMenuItem("Cut", TSComponentNames.ID_CUT, KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK, false)));
		menu.add(i18n_createMenuItem("Copy", TSComponentNames.ID_COPY, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK, false)));
		menu.add(i18n_createMenuItem("Paste", TSComponentNames.ID_PASTE, KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK, false)));
		menu.add(i18n_createMenuItem("Delete", FormEditorNames.ID_DELETE_COMPONENT,null ));
		menu.addSeparator();
		menu.add(i18n_createMenuItem("Undo", TSComponentNames.ID_UNDO, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK, false)));
		menu.add(i18n_createMenuItem("Redo", TSComponentNames.ID_REDO, KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK, false)));
		menu.addSeparator();
		menu.add(i18n_createMenuItem("Move Left", FormEditorNames.ID_MOVE_LEFT,null ));
		menu.add(i18n_createMenuItem("Move Right", FormEditorNames.ID_MOVE_RIGHT,null ));
		menu.add(i18n_createMenuItem("Move Up", FormEditorNames.ID_MOVE_UP,null ));
		menu.add(i18n_createMenuItem("Move Down", FormEditorNames.ID_MOVE_DOWN,null ));
		menuBar.add(menu);

		JMenu submenu = new JMenu(I18N.getLocalizedMessage("Column"));
		menu.setName(MainPanelNames.ID_ABEILLEFORMS_COLUMN);
		if(!FormDesignerUtils.isFixed()){
			submenu.add(i18n_createMenuItem("Insert Left", FormEditorNames.ID_INSERT_COLUMN_LEFT, KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK, false)));
			submenu.add(i18n_createMenuItem("Insert Right", FormEditorNames.ID_INSERT_COLUMN_RIGHT, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK,false)));
			submenu.add(i18n_createMenuItem("Set As Separator", FormEditorNames.ID_SET_AS_COLUMN_SEPARATOR, KeyStroke.getKeyStroke(KeyEvent.VK_P,InputEvent.CTRL_MASK, false)));
			submenu.add(i18n_createMenuItem("Set As Large Separator", FormEditorNames.ID_SET_AS_BIG_COLUMN_SEPARATOR, null));
	
			submenu.add(i18n_createMenuItem("Delete Column", FormEditorNames.ID_DELETE_COLUMN, KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK, false)));
			submenu.add(i18n_createMenuItem("Trim", FormEditorNames.ID_TRIM_COLUMNS, null));
			
			if(!FormDesignerUtils.isSimple()){
				submenu.addSeparator();
				submenu.add(i18n_createMenuItem("Size: Preferred", FormEditorNames.ID_COLUMN_PREFERRED_SIZE, KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.CTRL_MASK, false)));
				submenu.add(i18n_createMenuItem("Resize: Grow", FormEditorNames.ID_COLUMN_RESIZE_GROW, KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK,false)));
				submenu.add(i18n_createMenuItem("Resize: None", FormEditorNames.ID_COLUMN_RESIZE_NONE, KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.SHIFT_MASK,false)));
			}
			
			submenu.addSeparator();
		}
		submenu.add(i18n_createMenuItem("Increase Column Span", FormEditorNames.ID_COLUMN_INCREASE_SPAN, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,InputEvent.SHIFT_MASK, false)));
		submenu.add(i18n_createMenuItem("Decrease Column Span", FormEditorNames.ID_COLUMN_DECREASE_SPAN, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,InputEvent.SHIFT_MASK, false)));

		menuBar.add(submenu);

		submenu = new JMenu(I18N.getLocalizedMessage("Row"));
		menu.setName(MainPanelNames.ID_ABEILLEFORMS_ROW);
		if(!FormDesignerUtils.isFixed()){
			submenu.add(i18n_createMenuItem("Insert Above", FormEditorNames.ID_INSERT_ROW_ABOVE, KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK, false)));
			submenu.add(i18n_createMenuItem("Insert Below", FormEditorNames.ID_INSERT_ROW_BELOW, KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK, false)));
			submenu.add(i18n_createMenuItem("Set As Separator", FormEditorNames.ID_SET_AS_ROW_SEPARATOR, KeyStroke.getKeyStroke(KeyEvent.VK_E,InputEvent.CTRL_MASK, false)));
			submenu.add(i18n_createMenuItem("Set As Large Separator", FormEditorNames.ID_SET_AS_BIG_ROW_SEPARATOR, null));
	
			submenu.add(i18n_createMenuItem("Delete Row", FormEditorNames.ID_DELETE_ROW, KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK, false)));
			submenu.add(i18n_createMenuItem("Trim", FormEditorNames.ID_TRIM_ROWS, null));
			if(!FormDesignerUtils.isSimple()){
				submenu.addSeparator();
				submenu.add(i18n_createMenuItem("Size: Preferred", FormEditorNames.ID_ROW_PREFERRED_SIZE, KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK,false)));
				submenu.add(i18n_createMenuItem("Resize: Grow", FormEditorNames.ID_ROW_RESIZE_GROW, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK, false)));
				submenu.add(i18n_createMenuItem("Resize: None", FormEditorNames.ID_ROW_RESIZE_NONE, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.SHIFT_MASK, false)));
			}
			submenu.addSeparator();
		}
		submenu.add(i18n_createMenuItem("Increase Row Span", FormEditorNames.ID_ROW_INCREASE_SPAN, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,InputEvent.SHIFT_MASK, false)));
		submenu.add(i18n_createMenuItem("Decrease Row Span", FormEditorNames.ID_ROW_DECREASE_SPAN, KeyStroke.getKeyStroke(KeyEvent.VK_UP,InputEvent.SHIFT_MASK, false)));

		menuBar.add(submenu);

		menu = new JMenu(I18N.getLocalizedMessage("Form"));
		menu.setName(MainPanelNames.ID_ABEILLEFORMS_FORM);
		menu.add(i18n_createMenuItem("Show Form", MainPanelNames.ID_SHOW_FORM, KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK, false)));
		menu.add(i18n_createMenuItem("Toggle Grid", FormEditorNames.ID_SHOW_GRID, KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK, false)));
		if (FormDesignerUtils.isDebug()) {
			menu.add(i18n_createMenuItem("Export Names", FormEditorNames.ID_EXPORT_COMPONENT_NAMES, null));
		}
		menuBar.add(menu);

		menu = new JMenu(I18N.getLocalizedMessage("Tools"));
		menu.setName(MainPanelNames.ID_ABEILLEFORMS_TOOLS);
		menu.add(i18n_createMenuItem("Form Properties", MainPanelNames.ID_FORM_PROPERTIES, KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0, false)));
		menu.add(i18n_createMenuItem("Code Generation", MainPanelNames.ID_FORWARD_ENGINEER, null));
		menu.add(i18n_createMenuItem("Preferences", MainPanelNames.ID_ENV_SETTINGS, null));
		if (FormDesignerUtils.isDebug()) {
			menu.add(i18n_createMenuItem("System Properties", MainPanelNames.ID_SYSTEM_PROPERTIES, null));
		}
		menuBar.add(menu);

		menu = new JMenu(I18N.getLocalizedMessage("Help"));
		menu.setName(MainPanelNames.ID_ABEILLEFORMS_HELP);
		menu.add(i18n_createMenuItem("About", MainPanelNames.ID_ABOUT, null));
		menuBar.add(menu);

	}


	/**
	 * Creates the status bar the application. Shows current project.
	 */
	private void createStatusBar() {
		JETAPanel panel = new JETAPanel();
		FormLayout layout = new FormLayout("pref:grow", "pref");
		panel.setLayout(layout);

		m_status_cell = new TSCell("formcell", "left:pref:nogrow");
		m_status_cell.setFont(javax.swing.UIManager.getFont("Table.font"));
		m_status_cell.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		m_status_cell.setIcon(FormDesignerUtils.loadImage(Icons.APPLICATION_16));
		m_status_cell.setBorder(BorderFactory.createCompoundBorder(m_status_cell.getBorder(), BorderFactory.createEmptyBorder(1, 0, 0, 0)));
		CellConstraints cc = new CellConstraints();
		panel.add(m_status_cell, cc.xy(1, 1));
		this.add(panel, BorderLayout.SOUTH);
	}

	/**
	 * Creates the toolbar for this frame window.
	 */
	private void createToolBar() {
		JToolBar toolbar = new JToolBar();
		m_toolbar = toolbar;

		m_composite_finder.add(new DesignTimeComponentFinder(toolbar));

		toolbar.setFloatable(false);

		toolbar.add(i18n_createToolBarButton(MainPanelNames.ID_OPEN_FORM, Icons.OPEN_24, "Open Form"));
		toolbar.add(i18n_createToolBarButton(MainPanelNames.ID_SAVE_FORM, Icons.SAVE_24, "Save Form"));
		toolbar.add(i18n_createToolBarButton(MainPanelNames.ID_CREATE_FORM, Icons.ADD_24, "Create Form"));
		toolbar.addSeparator();

		toolbar.add(i18n_createToolBarButton(TSComponentNames.ID_CUT, Icons.CUT_24, "Cut"));
		toolbar.add(i18n_createToolBarButton(TSComponentNames.ID_COPY, Icons.COPY_24, "Copy"));
		toolbar.add(i18n_createToolBarButton(TSComponentNames.ID_PASTE, Icons.PASTE_24, "Paste"));
		toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_DELETE_COMPONENT, Icons.DELE_COMP_24, "Delete"));

		toolbar.add(i18n_createToolBarButton(TSComponentNames.ID_UNDO, Icons.UNDO_24, "Undo"));
		toolbar.add(i18n_createToolBarButton(TSComponentNames.ID_REDO, Icons.REDO_24, "Redo"));

		toolbar.addSeparator();
		toolbar.add(i18n_createToolBarButton(MainPanelNames.ID_FORM_PROPERTIES, Icons.PROP_24, "Form Properties"));
		toolbar.add(i18n_createToolBarButton(MainPanelNames.ID_SHOW_GRID, Icons.TABLE_16, "Toggle Grid"));
		toolbar.add(i18n_createToolBarButton(MainPanelNames.ID_SHOW_FORM, Icons.PLAY_24, "Show Form"));
		toolbar.add(i18n_createToolBarButton(MainPanelNames.ID_FORWARD_ENGINEER, Icons.CODE_24, "Code Generation"));
		toolbar.addSeparator();
		if(!FormDesignerUtils.isFixed()){
			toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_INSERT_COLUMN_LEFT, Icons.COLUMN_INSERT_24, "Insert Left"));
			toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_INSERT_COLUMN_RIGHT, Icons.COLUMN_INSERT_RIGHT_24, "Insert Right"));
			toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_DELETE_COLUMN, Icons.COLUMN_DELETE_24, "Delete Column"));
	
			toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_SET_AS_COLUMN_SEPARATOR, Icons.COLUMN_SEP_SMALL_24, "Set As Column Separator"));
			toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_SET_AS_BIG_COLUMN_SEPARATOR, Icons.COLUMN_SEP_LARGE_24, "Set As Large Column Separator"));
		}
		toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_COLUMN_INCREASE_SPAN, Icons.COLUMN_INCREASE_SPAN_24, "Increase Column Span"));
		toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_COLUMN_DECREASE_SPAN, Icons.COLUMN_DECREASE_SPAN_24, "Decrease Column Span"));

		toolbar.addSeparator();
		if(!FormDesignerUtils.isFixed()){
			toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_INSERT_ROW_ABOVE, Icons.ROW_INSERT_24, "Insert Above"));
			toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_INSERT_ROW_BELOW, Icons.ROW_INSERT_BELOW_24, "Insert Below"));
	
			toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_DELETE_ROW, Icons.ROW_DELETE_24, "Delete Row"));
			toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_SET_AS_ROW_SEPARATOR, Icons.ROW_SEP_SMALL_24, "Set As Row Separator"));
			toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_SET_AS_BIG_ROW_SEPARATOR, Icons.ROW_SEP_LARGE_24, "Set As Large Row Separator"));
		}
		toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_ROW_INCREASE_SPAN, Icons.ROW_INCREASE_SPAN_24, "Increase Row Span"));
		toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_ROW_DECREASE_SPAN, Icons.ROW_DECREASE_SPAN_24, "Decrease Row Span"));

		toolbar.addSeparator();
		toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_MOVE_LEFT, Icons.MOVE_LEFT_24, "Move Left"));
		toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_MOVE_RIGHT, Icons.MOVE_RIGHT_24, "Move Right"));
		toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_MOVE_UP, Icons.MOVE_UP_24, "Move Up"));
		toolbar.add(i18n_createToolBarButton(FormEditorNames.ID_MOVE_DOWN, Icons.MOVE_DOWN_24, "Move Down"));
		
		

	}

	/**
	 * Deactivates all forms in the frame
	 */
	private void deactivateAllForms() {
		for (int index = 0; index < m_forms_tab.getTabCount(); index++) {
			Component comp = m_forms_tab.getComponentAt(index);
			if(comp instanceof FormEditor ){
				FormEditor other_ed = (FormEditor) comp;
				other_ed.deactivate();
				m_formmgr.deactivateForms(other_ed);
			}
		}
	}

	/**
	 * Enables/Disables the menu/toolbar button associated with the commandid
	 * 
	 * @param commandId
	 *            the id of the command whose button to enable/disable
	 * @param bEnable
	 *            true/false to enable/disable
	 */
	public void enableComponent(String commandId, boolean bEnable) {
		m_composite_finder.enableComponent(commandId, bEnable);
	}

	/**
	 * JETAContainer implementation
	 */
	public UIDirector getUIDirector() {
		return m_uidirector;
	}

	/**
	 * Called when the form component's path has changed. We update the tab
	 * label in this case.
	 */
	void formNameChanged(FormComponent fc) {
		for (int index = 0; index < m_forms_tab.getTabCount(); index++) {
			Component comp = m_forms_tab.getComponentAt(index);
			if(comp instanceof FormEditor){
				FormEditor editor = (FormEditor) comp;
				if (editor.getForm() == fc) {
					String filename = fc.getFileName();
					assert (filename != null);
					m_forms_tab.setTitleAt(index, filename);
					if (fc.isEmbedded())
						m_forms_tab.setIconAt(index, FormDesignerUtils.loadImage(Icons.EMBEDDED_FORM_16));
					else
						m_forms_tab.setIconAt(index, FormDesignerUtils.loadImage(Icons.LINKED_FORM_16));
	
					break;
				}
			}
		}
	}

	/**
	 * @return the buttonbar that contains the property pane, FormSpec views,
	 *         and CellConstaint view
	 */
	public TSButtonBar getButtonBar() {
		return m_buttonbar;
	}

	/**
	 * JETAContainer implementation
	 * 
	 * @return the component that is NOT a JMenuItem that has the given name
	 */
	public Component getComponentByName(String compName) {
		return m_composite_finder.getComponentByName(compName);
	}

	/**
	 * JETAContainer implementation
	 * 
	 * @return all components in this frame with the given name.
	 */
	public Collection getComponentsByName(String compName) {
		return m_composite_finder.getComponentsByName(compName);
	}

	/**
	 * @return the editor that is currently active in the frame. Null is
	 *         returned if no editors are opened.
	 */
	public FormEditor getCurrentEditor() {
		Component comp = m_forms_tab.getSelectedComponent();
		if(comp instanceof FormEditor){
			FormEditor editor = (FormEditor) comp;
			return editor;
		}else{
			return null;
		}
			
	}

	/**
	 * ComponentSource imlementation
	 */
	public ComponentFactory getComponentFactory() {
		return m_component_tools.getComponentFactory();
	}

	public ComponentsToolBar getComponentsToolBar() {
		return m_component_tools;
	}

	/**
	 * Returns the object responsible for handling dock behavior for the
	 * properties window
	 */
	FrameDocker getDocker() {
		return m_docker;
	}

	/**
	 * @return the editor whose main form is the given form. If the given form
	 *         is not opened in its own editor, null is returned.
	 */
	public FormEditor getEditor(FormComponent form) {
		for (int index = 0; index < m_forms_tab.getTabCount(); index++) {
			Component comp = m_forms_tab.getComponentAt(index);
			if(comp instanceof FormEditor){
				FormEditor editor = (FormEditor) comp;
				if (editor.getForm() == form)
					return editor;
			}
		}
		return null;
	}

	/**
	 * @return a collection of currently opened FormEditors
	 */
	public Collection getEditors() {
		LinkedList editors = new LinkedList();
		if(m_forms_tab != null){
			for (int index = 0; index < m_forms_tab.getTabCount(); index++) {
				Component comp = m_forms_tab.getComponentAt(index);
				if(comp instanceof FormEditor){
					FormEditor editor = (FormEditor) comp;
					editors.add(editor);
				}
			}
		}
		return editors;
	}

	/**
	 * @return the property frame
	 */
	public PropertyPaneContainer getPropertyContainer() {
		return m_propsview;
	}

	/**
	 * @return the form that has the given id *if* it is opened in the frame.
	 */
	public FormComponent getForm(String formId) {
		for (int index = 0; index < m_forms_tab.getTabCount(); index++) {
			Component comp = m_forms_tab.getComponentAt(index);
			if(comp instanceof FormEditor){
				FormEditor editor = (FormEditor) comp;
				if (formId.equals(editor.getId())) {
					return editor.getFormComponent();
				}
			}
		}
		return null;
	}

	/** GridViewListener implementation */
	public void gridChanged(GridViewEvent evt) {
		if (evt != null) {
			if ((!isSelectionTool() || AbstractMouseHandler.isDragging()) && evt.getId() == GridViewEvent.CELL_SELECTED) {
				return;
			}

			if (evt.getId() == GridViewEvent.EDIT_COMPONENT) {
				m_buttonbar.setCurrentView(m_propsview);
			}
		}

		Object obj = getCurrentControlsView();
		if (obj instanceof GridViewListener) {
			((GridViewListener) obj).gridChanged(evt);
		}
		else {
			assert (false);
		}

		updateComponents();

	}

	/**
	 * @return true if the focus manager is currently active
	 */
	public void setFocusSelected(boolean focus_sel) {
		/*
		 * Collection comps = getComponentsByName(
		 * MainFrameNames.ID_FOCUS_MANAGER ); Iterator iter = comps.iterator();
		 * while( iter.hasNext() ) { Component comp = (Component)iter.next(); if (
		 * comp instanceof javax.swing.AbstractButton ) {
		 * ((javax.swing.AbstractButton)comp).setSelected( focus_sel ); } }
		 */
	}

	/**
	 * ComponentSource imlementation
	 */
	public boolean isSelectionTool() {
		return m_component_tools.isSelectionTool();
	}

	/**
	 * Reloads the components toolbar because it may have changed
	 */
	public void reloadComponentsToolbar() {
		m_component_tools.reload();
		m_split.revalidate();
	}

	/**
	 * Removes the given form from the tabbed pane.
	 */
	public void removeForm(String formId) {
		try {
			removeTabListener();
			deactivateAllForms();

			for (int index = 0; index < m_forms_tab.getTabCount(); index++) {
				Component comp = m_forms_tab.getComponentAt(index);
				if(comp instanceof FormEditor){
					FormEditor editor = (FormEditor) comp;
					if (formId.equals(editor.getForm().getId())) {
						m_forms_tab.remove(index);
						break;
					}
				}
			}
			FormEditor editor = getCurrentEditor();
			if (editor != null) {
				m_formmgr.activateForm(editor.getTopParent().getId());
				editor.activate();
			}
		} finally {
			addTabListener();
		}

		if (m_forms_tab.getTabCount() == 0) {
			Object obj = getCurrentControlsView();
			if (obj instanceof GridViewListener) {
				((GridViewListener) obj).gridChanged(new GridViewEvent(null, GridViewEvent.CELL_SELECTED));
			}
		}
		else {
			FormEditor editor = getCurrentEditor();
			if(editor != null) gridChanged(new GridViewEvent(null, GridViewEvent.CELL_SELECTED, editor.getSelectedComponent()));
		}
	}

	/**
	 * Adds the tab listener to the forms tab
	 */
	private void removeTabListener() {
		m_forms_tab.removeChangeListener(m_tab_listener);
	}

	/**
	 * Sets the frame location and size to the last know position.
	 */
	private void initializeFrameBounds() {
		try {
			Dimension screensz = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

			int x = 40;
			int y = 40;
			int width = screensz.width - 80;
			int height = screensz.height - 80;

			ObjectStore os = (ObjectStore) JETARegistry.lookup(ComponentNames.APPLICATION_STATE_STORE);
			Rectangle rect = (Rectangle) os.load(MainPanel.ID_FRAME_BOUNDS);
			if (rect != null) {
				x = Math.max(rect.x, 20);
				y = Math.max(rect.y, 20);
				width = rect.width;
				height = rect.height;

				if ((x + width) > screensz.width)
					width = screensz.width - x - 40;

				if ((y + height) > screensz.height)
					height = screensz.height - y - 40;

				if (width < 100 || height < 100) {
					x = 10;
					y = 10;
					width = 700;
					height = 600;
				}
			}
			setSize(width, height);
			setLocation(x, y);

			m_docker.initializeFrameBounds();
		} catch (Exception e) {
			e.printStackTrace();
			setSize(700, 600);
			setLocation(10, 10);
		}
	}

	/**
	 * Sets the active component factory to the selection tool
	 */
	public void setSelectionTool() {
		m_component_tools.setSelectionTool();
	}

	/**
	 * Selects the form in the frame. If the form is not currently in the tab
	 * window, a new tab is added.
	 */
	public void showForm(FormComponent fc) {
		assert (fc != null);
		removeTabListener();

		deactivateAllForms();

		for (int index = 0; index < m_forms_tab.getTabCount(); index++) {
			Component comp = m_forms_tab.getComponentAt(index);
			if(comp instanceof FormEditor){
				FormEditor editor = (FormEditor) comp;
				assert (editor != null);
				if (editor.getId().equals(fc.getId())) {
					m_forms_tab.setSelectedIndex(index);
					m_formmgr.activateForm(getCurrentEditor().getTopParent().getId());
					addTabListener();
					editor = getCurrentEditor();
					editor.activate();
					gridChanged(new GridViewEvent(null, GridViewEvent.CELL_SELECTED, editor.getSelectedComponent()));
					return;
				}
			}
		}

		FormEditor editor = new FormEditor(this, fc);
		/** add form resets the tab listener */
		addForm(editor);
		editor = getCurrentEditor();
		editor.activate();
		gridChanged(new GridViewEvent(null, GridViewEvent.CELL_SELECTED, editor.getSelectedComponent()));

	}

	/**
	 * Shuts down this frame. Saves the frame state to the object store and
	 * Closes all open windows.
	 */
	public void shutDown() {
		try {
			ObjectStore os = (ObjectStore) JETARegistry.lookup(ComponentNames.APPLICATION_STATE_STORE);
			os.store(MainPanel.ID_FRAME_BOUNDS, getBounds());

			m_docker.shutDown();
		} catch (Exception e) {
			// ignore
		}
		
		FormsInitializer.shutdown();
	}

	/**
	 * EditorManager implementation. Called when a form or forms have changed.
	 * This tells the EditorManager to update any modified indicators on the GUI
	 * if a form is modified/unmodified.
	 */
	public void updateModifiedStatus() {
		for (int index = 0; index < m_forms_tab.getTabCount(); index++) {
			Component comp = m_forms_tab.getComponentAt(index);
			if(comp instanceof FormEditor){
				FormEditor editor = (FormEditor) comp;
				String filename = editor.getForm().getFileName();
				StringBuffer tab_label = new StringBuffer();
				if (editor.isLinked()) {
					filename = editor.getForm().getFileName();
					if (filename == null)
						filename = I18N.getLocalizedMessage("New Form");
				}
				else {
					filename = I18N.getLocalizedMessage("embedded form");
				}
	
				tab_label.append(filename);
				if (editor.isModified()) {
					tab_label.append('*');
				}
				m_forms_tab.setTitleAt(index, tab_label.toString());
			}
		}
	}

	/**
	 * Updates all child components when the look and feel has changed
	 */
	public void updateUI() {
		/** first update all editors */
		Collection editors = getEditors();
		Iterator iter = editors.iterator();
		while (iter.hasNext()) {
			FormUtils.updateLookAndFeel((Component) iter.next());
		}
		if(m_formmgr != null){
			Collection forms = m_formmgr.getForms();
			iter = forms.iterator();
			while (iter.hasNext()) {
				String formid = (String) iter.next();
				FormUtils.updateLookAndFeel((Component) m_formmgr.getForm(formid));
			}
		}
		if(m_docker != null) m_docker.updateUI();
		if(m_form_popup != null) FormUtils.updateLookAndFeel(m_form_popup);
	}

	/**
	 * Runs unit test routines on this Frame.
	 */
	void unitTest() {
		if (FormDesignerUtils.isTest()) {
			// com.jeta.swingbuilder.test.JETATestFactory.runTest(
			// "test.jeta.swingbuilder.gui.main.MainFrameValidator", this );
			m_formmgr.unitTest();
		}
	}

	public void updateComponents() {
		UIDirector uidirector = getUIDirector();
		if (uidirector != null)
			uidirector.updateComponents(null);
	}

	/**
	 * Listener for the Forms tab. When we get the tab changed event, we
	 * deactivat all other forms.
	 */
	private class TabListener implements javax.swing.event.ChangeListener {
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			deactivateAllForms();
			Component comp = m_forms_tab.getSelectedComponent();
			if(comp instanceof FormEditor){
				FormEditor editor = (FormEditor) comp;
				m_formmgr.activateForm(editor.getTopParent().getId());
				editor = getCurrentEditor();
				if(editor != null){
					editor.activate();
					unitTest();
					gridChanged(new GridViewEvent(null, GridViewEvent.CELL_SELECTED, editor.getSelectedComponent()));
				}
			}else if(comp instanceof FormManagerView){
				if(m_formMgrview != null) m_formMgrview.reload();
			}else if(comp instanceof UndoManagerView){
				if(m_formUndoview != null) m_formUndoview.reload();
				 
			}
		}
	}
	
	public void hideMenuBar(String id){
		if(id == null){
			this.m_menuBar.setVisible(false);
			return;
		}
		Collection comps = this.getComponentsByName(id);
		Iterator iter = comps.iterator();
		while (iter.hasNext()) {
			Component comp = (Component) iter.next();
			if(comp instanceof JMenuItem)
				comp.setVisible(false);
		}
	}
	public void showMenuBar(String id){
		if(id == null){
			this.m_menuBar.setVisible(true);
			return;
		}
		Collection comps = this.getComponentsByName(id);
		Iterator iter = comps.iterator();
		while (iter.hasNext()) {
			Component comp = (Component) iter.next();
			if(comp instanceof JMenuItem)
				comp.setVisible(true);
		}
	}
	public void hideToolBar(String id){
		if(id == null){
			this.m_menuBar.setVisible(false);
			return;
		}
		Collection comps = this.getComponentsByName(id);
		Iterator iter = comps.iterator();
		while (iter.hasNext()) {
			Component comp = (Component) iter.next();
			if(comp instanceof JButton)
				comp.setVisible(false);
		}
	}
	public void showToolBar(String id){
		if(id == null){
			this.m_menuBar.setVisible(true);
			return;
		}
		Collection comps = this.getComponentsByName(id);
		Iterator iter = comps.iterator();
		while (iter.hasNext()) {
			Component comp = (Component) iter.next();
			if(comp instanceof JButton)
				comp.setVisible(true);
		}
	}

	/**
	 * Specialization of JScrollPane for Row/Column Spec and CellConstraints
	 * views to help improve application startup time.
	 */
	private abstract class ControlsScrollPane extends JScrollPane {
		private Component m_viewport_view;

		ControlsScrollPane() {
			super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		}

		void initialize() {
			if (m_viewport_view == null) {
				m_viewport_view = loadView();
				setViewportView(m_viewport_view);
			}
		}

		protected abstract Component loadView();
	}

}
