package com.jeta.swingbuilder.codegen.builder;

import com.jeta.forms.store.memento.FormMemento;

public interface BasePanelWriter {

	public abstract MethodWriter createPanel(DeclarationManager decl_mgr,
			FormMemento fm);

}