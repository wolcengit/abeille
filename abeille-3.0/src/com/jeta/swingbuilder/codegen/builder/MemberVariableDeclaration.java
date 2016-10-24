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

package com.jeta.swingbuilder.codegen.builder;

import com.jeta.forms.store.memento.ComponentMemento;

public class MemberVariableDeclaration extends VariableDeclaration {

	private ComponentMemento m_memento;
	
	public MemberVariableDeclaration(DeclarationManager declMgr, Class varClass, boolean declInitializer) {
		super(declMgr, varClass, null, false, declInitializer, null, null);
	}

	public MemberVariableDeclaration(DeclarationManager declMgr, Class varClass, String varName, boolean declInitializer) {
		super(declMgr, varClass, varName, false, declInitializer, null, null);
	}

	public MemberVariableDeclaration(DeclarationManager declMgr, Class varClass, String varName, boolean declInitializer, String declaration) {
		super(declMgr, varClass, varName, false, declInitializer, null, null);
	}
	public MemberVariableDeclaration(DeclarationManager declMgr, Class varClass, String initializer) {
		super(declMgr, varClass, null, false, true, initializer, null);
	}
	public MemberVariableDeclaration(DeclarationManager declMgr, Class varClass, String initializer, String varName) {
		super(declMgr, varClass, varName, false, true, initializer, null);
	}
	public MemberVariableDeclaration(DeclarationManager declMgr, Class varClass, String initializer, String varName,String declaration) {
		super(declMgr, varClass, varName, false, true, initializer, declaration);
	}

	public ComponentMemento getMemento() {
		return m_memento;
	}

	public void setMemento(ComponentMemento cm) {
		this.m_memento = cm;
	}
}
