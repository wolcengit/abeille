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

package com.jeta.swingbuilder.codegen.gui.config;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.store.memento.FormCodeModel;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * View for entering options for code generation
 * 
 * @author Jeff Tassin
 */
public class FormCodeModelView extends FormPanel {
	private FormCodeModel m_model;

	public FormCodeModelView(FormCodeModel om) {
		super("com/jeta/swingbuilder/codegen/gui/config/FormCodeModel.jfrm");
		m_model = om;
		loadModel(om);
		if(FormDesignerUtils.isSimple()){
			this.getCheckBox(FormCodeModelNames.ID_BUILD_CONSTANT).setEnabled(false);
		}

	}

	public FormCodeModel getModel() {
		return m_model;
	}

	public void loadModel(FormCodeModel om) {
		setText(FormCodeModelNames.ID_CLASS_NAME, om.getClassName());
		setText(FormCodeModelNames.ID_CLASS_EXTENDS, om.getClassExtends());
		setText(FormCodeModelNames.ID_CLASS_IMPLMENTS, om.getClassImplments());
		setText(FormCodeModelNames.ID_SOURCE_BUILDER, om.getSourcebuilder());
		setText(FormCodeModelNames.ID_MEMBER_PREFIX, om.getMemberPrefix());
		setSelected(FormCodeModelNames.ID_INCLUDE_MAIN, om.isIncludeMain());
		setSelected(FormCodeModelNames.ID_INCLUDE_CTOR, om.isIncludeCtor());
		setSelected(FormCodeModelNames.ID_INCLUDE_NONSTANDARD, om.isIncludeNonStandard());
		setSelected(FormCodeModelNames.ID_INCLUDE_LOADIMAGE, om.isIncludeLoadImage());
		setSelected(FormCodeModelNames.ID_INCLUDE_BINDING, om.isIncludeBinding());
		setSelected(FormCodeModelNames.ID_BUILD_CONSTANT, om.isBuildConstant());
	}

	public void saveToModel() {
		m_model.setClassName(FormDesignerUtils.fastTrim(getText(FormCodeModelNames.ID_CLASS_NAME)));
		m_model.setClassExtends(FormDesignerUtils.fastTrim(getText(FormCodeModelNames.ID_CLASS_EXTENDS)));
		m_model.setClassImplments(FormDesignerUtils.fastTrim(getText(FormCodeModelNames.ID_CLASS_IMPLMENTS)));
		m_model.setSourcebuilder(FormDesignerUtils.fastTrim(getText(FormCodeModelNames.ID_SOURCE_BUILDER)));
		m_model.setMemberPrefix(FormDesignerUtils.fastTrim(getText(FormCodeModelNames.ID_MEMBER_PREFIX)));
		m_model.setIncludeMain(isSelected(FormCodeModelNames.ID_INCLUDE_MAIN));
		m_model.setIncludeCtor(isSelected(FormCodeModelNames.ID_INCLUDE_CTOR));
		m_model.setIncludeNonStandard(isSelected(FormCodeModelNames.ID_INCLUDE_NONSTANDARD));
		m_model.setIncludeLoadImage(isSelected(FormCodeModelNames.ID_INCLUDE_LOADIMAGE));
		m_model.setIncludeBinding(isSelected(FormCodeModelNames.ID_INCLUDE_BINDING));
		m_model.setBuildConstant(isSelected(FormCodeModelNames.ID_BUILD_CONSTANT));
	}
}
