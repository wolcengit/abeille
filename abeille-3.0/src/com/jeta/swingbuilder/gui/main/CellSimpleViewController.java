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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.jgoodies.forms.layout.ColumnSpec;
import com.jeta.jgoodies.forms.layout.RowSpec;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.commands.CommandUtils;
import com.jeta.swingbuilder.gui.commands.EditColumnSpecCommand;
import com.jeta.swingbuilder.gui.commands.EditRowSpecCommand;
import com.jeta.swingbuilder.gui.editor.FormEditor;

/**
 * The controller for this panel
 */
public class CellSimpleViewController extends JETAController {
	/**
	 * The view we are handling events for.
	 */
	private CellSimpleView m_view;

	/**
	 * The form manager.
	 */
	private FormManager m_formmgr;

	/**
	 * ctor
	 */
	public CellSimpleViewController(CellSimpleView view) {
		super(view);
		m_view = view;
		m_formmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
		assignAction(CellViewNames.ID_CONST_SIZE_CX, new ConstantSizeListener());
		assignAction(CellViewNames.ID_CONST_SIZE_CY, new ConstantSizeListener());
	}

	/**
	 * Gets the latest settings from the view and updates the Form.
	 */
	private void updateForm(boolean isRow,double sz) {
		FormEditor editor = (FormEditor) m_formmgr.getCurrentEditor();
		if (editor != null) {
			GridComponent gc = editor.getSelectedComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				int row = gc.getRow();
				int col = gc.getColumn();
				if (isRow) {
					RowSpec oldspec = view.getRowSpec(row);
					String newspec = "f:"+sz+"PX:n";
					EditRowSpecCommand cmd = new EditRowSpecCommand(view.getParentForm(), row, new RowSpec(newspec), oldspec);
					CommandUtils.invoke(cmd, editor);
				}
				else {
					ColumnSpec oldspec = view.getColumnSpec(col);
					String newspec = "f:"+sz+"PX:n";
					EditColumnSpecCommand cmd = new EditColumnSpecCommand(view.getParentForm(), col, new ColumnSpec(newspec), oldspec);
					CommandUtils.invoke(cmd, editor);
				}
			}
		}
	}


	/**
	 * Listener for constant size amount field on CellView
	 */
	public class ConstantSizeListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			boolean isRow = true;
			String result =  null;
			if(evt.getSource() == m_view.getTextField(CellViewNames.ID_CONST_SIZE_CX)){
				isRow = true;
				result = m_view.getTextField(CellViewNames.ID_CONST_SIZE_CX).getText();
			}else if(evt.getSource() == m_view.getTextField(CellViewNames.ID_CONST_SIZE_CY)){
				isRow = false;
				result = m_view.getTextField(CellViewNames.ID_CONST_SIZE_CY).getText();
			}
			try {
				double sz = Double.parseDouble(result);
				updateForm(isRow,sz);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
