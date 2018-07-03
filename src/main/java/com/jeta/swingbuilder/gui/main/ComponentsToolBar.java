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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jeta.forms.gui.components.ComponentFactory;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.jgoodies.forms.layout.CellConstraints;
import com.jeta.jgoodies.forms.layout.FormLayout;
import com.jeta.jgoodies.forms.layout.RowSpec;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.open.support.EmptyCollection;
import com.jeta.swingbuilder.gui.components.TSButtonBar;
import com.jeta.swingbuilder.gui.componentstoolbar.ComponentsToolBarManager;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.resources.Icons;
import com.jeta.swingbuilder.store.RegisteredBean;

/**
 * The frame used to contain the toolbar for editing forms.
 * 
 * @author Jeff Tassin
 */
public class ComponentsToolBar extends JETAPanel implements ComponentSource {
	public static final int MAX_TOOLBAR_ROWS = 120;
	/**
	 * for toolbar catalog 
	 */ 
	private TSButtonBar m_buttonbar;

	/** @param model */
	public ComponentsToolBar() {
		try {
			setLayout(new BorderLayout());
			m_buttonbar = new TSButtonBar(true);
			reload();
			add(m_buttonbar, BorderLayout.CENTER);
		} catch (Exception e) {
			FormsLogger.debug(e);
		}
	}

	/** Creates a button for our toolbar */
	private Component createPaletteButton(Icon icon, String id,String text) {
		AbstractButton btn = new javax.swing.JToggleButton();
		Dimension d = new Dimension(120, 20);
		btn.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				AbstractButton src = (AbstractButton) e.getSource();
				JPanel btn_panel = (JPanel) src.getParent();
				if (src.isSelected()) {
					btn_panel.setBackground(java.awt.Color.lightGray);
					btn_panel.setBorder(BorderFactory.createLineBorder(java.awt.Color.gray));
				}
				else {
					btn_panel.setBackground(javax.swing.UIManager.getColor("control"));
					btn_panel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
				}
			}
		});
		// 按esc取消选择控件
		btn.addKeyListener(new KeyAdapter() {
			
			public void keyPressed(KeyEvent e) {
				if(KeyEvent.VK_ESCAPE == e.getKeyCode() && e.getSource() != getComponentByName(ComponentsToolBarManager.ID_SELECTION_TOOL)) {
					setSelectionTool();
				}
			}
		});
		btn.setOpaque(false);
		btn.setContentAreaFilled(false);
		btn.setFocusPainted(false);
		btn.setPreferredSize(d);
		btn.setMinimumSize(d);
		btn.setSize(d);
		btn.setBorderPainted(false);
		btn.setIcon(icon);
		btn.setActionCommand(id);
		btn.setName(id);
		btn.setText(text);
		btn.setHorizontalAlignment(AbstractButton.LEFT);
		if (text != null)
			btn.setToolTipText(text);
		return btn;
	}

	/**
	 * Creates the java beans palette toolbar.
	 * 
	 */
	private Container createToolbar(String cat) {

		LinkedList button_list = new LinkedList();
		button_list.addAll(registerBeans(cat));

		StringBuffer colspec = new StringBuffer();
		StringBuffer rowspec = new StringBuffer();

		int cols = button_list.size() / MAX_TOOLBAR_ROWS + (button_list.size() % MAX_TOOLBAR_ROWS == 0 ? 0 : 1);
		int rows = Math.min(button_list.size(), MAX_TOOLBAR_ROWS);
		for (int col = 1; col <= cols; col++) {
			if (col > 1)
				colspec.append(",");

			colspec.append("pref");
		}

		for (int row = 1; row <= rows; row++) {
			if (row > 1)
				rowspec.append(",");

			rowspec.append("pref");
		}

		ButtonGroup bgroup = new ButtonGroup();

		JETAPanel toolbar = new JETAPanel();
		toolbar.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 4, 0, 4));
		FormLayout layout = new FormLayout(colspec.toString(), rowspec.toString());
		CellConstraints cc = new CellConstraints();
		toolbar.setLayout(layout);

		int row_count = button_list.size() / cols + (button_list.size() % cols == 0 ? 0 : 1);
		Iterator iter = button_list.iterator();
		for (int col = 1; col <= cols; col++) {
			for (int row = 1; row <= row_count; row++) {
				if (iter.hasNext()) {
					Component btn = (Component) iter.next();
					bgroup.add((AbstractButton) btn);
					JPanel btn_panel = new JPanel(new BorderLayout());
					btn_panel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
					btn_panel.add(btn, BorderLayout.CENTER);
					toolbar.add(btn_panel, cc.xy(col, row));
				}
				else {
					break;
				}
			}
		}
		layout.insertRow(1, new RowSpec("5px"));
		toolbar.setController(new ToolbarController(toolbar, button_list));

		assert (!iter.hasNext());
		return toolbar;
	}

	/** ComponentSource imlementation */
	public ComponentFactory getComponentFactory() {
		ComponentsToolBarManager tbm = (ComponentsToolBarManager) JETARegistry.lookup(ComponentsToolBarManager.COMPONENT_ID);
		if (tbm != null && !tbm.isSelectionTool()){
			return tbm.getComponentFactory();
		}
		return null;
	}

	/** ComponentSource imlementation */
	public boolean isSelectionTool() {
		ComponentsToolBarManager tbm = (ComponentsToolBarManager) JETARegistry.lookup(ComponentsToolBarManager.COMPONENT_ID);
		if (tbm != null){
			return tbm.isSelectionTool();
		}
		return true;
	}

	/**
	 * Registers all default bean factories
	 */
	private Collection registerBeans(String cat) {
		try {
			LinkedList btns = new LinkedList();
			ComponentsToolBarManager tbm = (ComponentsToolBarManager) JETARegistry.lookup(ComponentsToolBarManager.COMPONENT_ID);
			if (tbm == null)
				return btns;

			boolean add_forms = true;
			Collection default_beans = tbm.getBeans(cat);
			Iterator iter = default_beans.iterator();
			while (iter.hasNext()) {
				RegisteredBean rbean = (RegisteredBean) iter.next();
				Icon icon = rbean.getIcon();
				if (icon == null) {
					icon = FormDesignerUtils.loadImage(Icons.BEAN_16);
				}
				btns.add(createPaletteButton(icon, rbean.getId(), rbean.getDescription()));
			}
			return btns;
		} catch (Exception e) {
			FormsLogger.debug(e);
		}
		return EmptyCollection.getInstance();

	}

	/**
	 * Reloads the toolbar
	 */
	public void reload() {
		m_buttonbar.clear();
		ComponentsToolBarManager tbm = (ComponentsToolBarManager) JETARegistry.lookup(ComponentsToolBarManager.COMPONENT_ID);
		if (tbm != null){
			Collection cats = tbm.getCatalogs();
			Iterator iter = cats.iterator();
			while (iter.hasNext()) {
				String cat = (String) iter.next();
				JScrollPane scroll = new JScrollPane();
				scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				scroll.setViewportView(createToolbar(cat));
				m_buttonbar.addView(I18N.getLocalizedMessage(cat), scroll, FormDesignerUtils.loadImage(Icons.BEAN_16));
			}
		}
		m_buttonbar.updateView();
	}
	public void clear() {
		m_buttonbar.clear();
		m_buttonbar.updateView();
		
	}

	/**
	 * ComponentSource implementation Sets the active component factory to the
	 * selection tool
	 */
	public void setSelectionTool() {
		AbstractButton btn = (AbstractButton) getComponentByName(ComponentsToolBarManager.ID_SELECTION_TOOL);
		btn.setSelected(true);
		ComponentsToolBarManager tbm = (ComponentsToolBarManager) JETARegistry.lookup(ComponentsToolBarManager.COMPONENT_ID);
		if (tbm != null) tbm.setSelectionTool();
	}

	/** The controller for this frame */
	public class ToolbarController extends JETAController {
		public ToolbarController(JETAPanel view, Collection btns) {
			super(view);

			Iterator iter = btns.iterator();
			while (iter.hasNext()) {
				Component comp = (Component) iter.next();
				if (comp instanceof AbstractButton) {
					((AbstractButton) comp).addActionListener(new StandardComponentAction(comp.getName()));
				}
			}
		}
	}

	/** ActionHandler for standard Swing components */
	public class StandardComponentAction implements ActionListener {
		private String m_compname;

		public StandardComponentAction(String compName) {
			m_compname = compName;
		}

		public void actionPerformed(ActionEvent evt) {
			ComponentsToolBarManager tbm = (ComponentsToolBarManager) JETARegistry.lookup(ComponentsToolBarManager.COMPONENT_ID);
			if (tbm != null) tbm.setSelectionId(m_compname);
		}
	}

}
