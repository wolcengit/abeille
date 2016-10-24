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

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormSpecAdapter;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.form.GridViewEvent;
import com.jeta.forms.gui.form.GridViewListener;
import com.jeta.jgoodies.forms.layout.CellConstraints;
import com.jeta.jgoodies.forms.layout.ColumnSpec;
import com.jeta.jgoodies.forms.layout.FormLayout;
import com.jeta.jgoodies.forms.layout.RowSpec;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.components.FloatDocument;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jeta.swingbuilder.gui.formmgr.EditorManager;

/**
 * Displays the column and row settings.
 * 
 * @author Jeff Tassin
 */
public class CellSimpleView extends JETAPanel implements GridViewListener {
	/**
	 * The panel that contains the form.
	 */
	private FormPanel m_spec_panel;

	/**
	 * The grid component we are currently displaying values for
	 */
	private GridComponent m_current_comp;

	private EditorManager m_editor_mgr;

	public CellSimpleView() {

		m_editor_mgr = (EditorManager) JETARegistry.lookup(EditorManager.COMPONENT_ID);
		FormLayout layout = new FormLayout("fill:pref:grow", "pref");
		CellConstraints cc = new CellConstraints();

		setLayout(layout);

		m_spec_panel = new FormPanel("com/jeta/swingbuilder/gui/main/cellSimple.frm");
		add(m_spec_panel, cc.xy(1, 1));

		String def = m_spec_panel.getText(CellViewNames.ID_CONST_SIZE_CX);
		m_spec_panel.getTextField(CellViewNames.ID_CONST_SIZE_CX).setDocument(new FloatDocument(false));
		if (def != null)
			m_spec_panel.setText(CellViewNames.ID_CONST_SIZE_CX, def);
		
		def = m_spec_panel.getText(CellViewNames.ID_CONST_SIZE_CY);
		m_spec_panel.getTextField(CellViewNames.ID_CONST_SIZE_CY).setDocument(new FloatDocument(false));
		if (def != null)
			m_spec_panel.setText(CellViewNames.ID_CONST_SIZE_CY, def);

		setController(new CellSimpleViewController(this));
	}

	/**
	 * @return the component that we are currently displaying values for
	 */
	GridComponent getCurrentComponent() {
		return m_current_comp;
	}

	/**
	 * @return the underlying form panel
	 */
	FormPanel getFormPanel() {
		return m_spec_panel;
	}

	/** GridViewListener implementation */
	public void gridChanged(GridViewEvent evt) {
		FormEditor editor = m_editor_mgr.getCurrentEditor();
		if (editor != null) {
			GridComponent comp = editor.getSelectedComponent();
			update(comp);
		}
		else {
			update((GridComponent) null);
		}
	}


	/**
	 * Updates the panel using the specs from the currently selected cell in the
	 * given editor
	 */
	public void update(GridComponent gc) {
		m_current_comp = gc;

		if (gc == null) {
			setEnabled(false);
		}
		else {
			GridView view = gc.getParentView();
			if (view != null) {
				setEnabled(true);
				int row = gc.getRow();
				int col = gc.getColumn();
				/*
				if(row==1 && col==1){
					setEnabled(false);
					return;
				}
				*/
				RowSpec rspec = view.getRowSpec(row);
				FormSpecAdapter fspec = new FormSpecAdapter(rspec);
				m_spec_panel.getTextField(CellViewNames.ID_CONST_SIZE_CX).setText(String.valueOf(Math.round(fspec.getConstantSize())));
				ColumnSpec cspec = view.getColumnSpec(col);
				fspec = new FormSpecAdapter(cspec);
				m_spec_panel.getTextField(CellViewNames.ID_CONST_SIZE_CY).setText(String.valueOf(Math.round(fspec.getConstantSize())));
			}
			else {
				setEnabled(false);
			}
		}
	}

	
	public void setEnabled(boolean enabled) {
		m_spec_panel.getTextField(CellViewNames.ID_CONST_SIZE_CX).setEnabled(enabled);
		m_spec_panel.getTextField(CellViewNames.ID_CONST_SIZE_CY).setEnabled(enabled);
		if(!enabled){
			m_spec_panel.getTextField(CellViewNames.ID_CONST_SIZE_CX).setText("");
			m_spec_panel.getTextField(CellViewNames.ID_CONST_SIZE_CY).setText("");
		}
		super.setEnabled(enabled);
	}
	
	

}
