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

package com.jeta.forms.store.memento;

import java.io.IOException;

import com.jeta.forms.store.AbstractJETAPersistable;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.swingbuilder.gui.project.UserPreferencesNames;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.interfaces.userprops.TSUserPropertiesUtils;

/**
 * Model for code generation options for a form.
 * 
 * @author Jeff Tassin
 */
public class FormCodeModel extends AbstractJETAPersistable {
	static final long serialVersionUID = 5327987415154522473L;

	/**
	 * verion of this class
	 */
	public static final int VERSION = 2;

	/**
	 * attibutes local to a form
	 */
	private String m_class_name = "MyForm";
	private String m_class_extends = "javax.swing.JPanel";
	private String m_class_implments = null;

	/**
	 * build attibutes
	 */
	private String m_source_builder = "com.jeta.swingbuilder.codegen.builder.DefaultSourceBuilder";
	private String m_member_prefix = "m_";
	private boolean m_include_main = true;
	private boolean m_include_ctor = true;
	private boolean m_include_nonstandard = true;
	private boolean m_include_loadimage = false;
	private boolean m_include_binding = false;
	private boolean m_build_constant = true;

	public FormCodeModel() {
		m_class_name = TSUserPropertiesUtils.getString(UserPreferencesNames.ID_CLASS_NAME, "MyForm");
		m_class_extends = TSUserPropertiesUtils.getString(UserPreferencesNames.ID_CLASS_EXTENDS, "javax.swing.JPanel");
		m_class_implments = TSUserPropertiesUtils.getString(UserPreferencesNames.ID_CLASS_IMPLMENTS, "");
		m_source_builder = TSUserPropertiesUtils.getString(UserPreferencesNames.ID_SOURCE_BUILDER, "com.jeta.swingbuilder.codegen.builder.DefaultSourceBuilder");

		m_member_prefix = TSUserPropertiesUtils.getString(UserPreferencesNames.ID_MEMBER_PREFIX, "m_");
		m_include_main = TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_INCLUDE_MAIN, true);
		m_include_nonstandard = TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_INCLUDE_NONSTANDARD, true);
		m_include_loadimage = TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_INCLUDE_LOADIMAGE, false);
		m_include_binding = TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_INCLUDE_BINDING, true);
		m_build_constant = TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_BUILD_CONSTANT, false);
		
		if(FormDesignerUtils.isSimple()){
			m_include_binding = true;
			m_build_constant = true;
		}
		
	}

	public String getFileName() {
		if(m_class_name == null){
			return "NoNamePanel";
		}
		if(m_class_name.indexOf(".") != -1)
			return  m_class_name.substring(m_class_name.lastIndexOf(".")+1);
		else
			return m_class_name;
	}

	public String getClassImplments() {
		return m_class_implments;
	}

	public String getClassExtends() {
		return m_class_extends;
	}

	public String getClassName() {
		return m_class_name;
	}

	public String getMemberPrefix() {
		return m_member_prefix;
	}

	public boolean isIncludeMain() {
		return m_include_main;
	}
	public boolean isIncludeCtor() {
		return m_include_ctor;
	}

	public boolean isIncludeNonStandard() {
		return m_include_nonstandard;
	}
	public boolean isBuildConstant() {
		return m_build_constant;
	}

	public boolean isIncludeLoadImage() {
		return m_include_loadimage;
	}

	public boolean isIncludeBinding() {
		return m_include_binding;
	}

	public String getSourcebuilder() {
		return m_source_builder;
	}


	public void setSourcebuilder(String sourcebuilder) {
		this.m_source_builder = sourcebuilder;
	}

	public void setClassImplments(String impl) {
		m_class_implments = impl;
	}


	public void setClassExtends(String ext) {
		m_class_extends = ext;
	}

	public void setClassName(String cname) {
		m_class_name = cname;
	}

	public void setMemberPrefix(String prefix) {
		m_member_prefix = prefix;
	}

	public void setIncludeMain(boolean inc) {
		m_include_main = inc;
	}

	public void setIncludeCtor(boolean inc) {
		m_include_ctor = inc;
	}

	public void setIncludeNonStandard(boolean inc) {
		m_include_nonstandard = inc;
	}

	public void setBuildConstant(boolean build) {
		this.m_build_constant = build;
	}
	public void setIncludeLoadImage(boolean inc) {
		this.m_include_loadimage = inc;
	}
	public void setIncludeBinding(boolean inc) {
		this.m_include_binding = inc;
	}


	@Override
	public void read(JETAObjectInput in) throws ClassNotFoundException,
			IOException {
		int version = in.readVersion();
		m_class_name = in.readString("class_name");
		m_class_extends = in.readString("class_extends");
		m_class_implments = in.readString("class_implments");
		m_source_builder = in.readString("source_builder");
		m_member_prefix = in.readString("member_prefix");
		m_include_main = in.readBoolean("include_main");
		m_include_ctor = in.readBoolean("include_ctor");
		m_include_nonstandard = in.readBoolean("include_nonstandard");
		m_include_loadimage = in.readBoolean("include_loadimage");
		m_include_binding = in.readBoolean("include_binding");
		m_build_constant = in.readBoolean("build_constant");
		
	}


	@Override
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeString("class_name",m_class_name);
		out.writeString("class_extends",m_class_extends);
		out.writeString("class_implments",m_class_implments);
		out.writeString("source_builder",m_source_builder);
		out.writeString("member_prefix",m_member_prefix);
		out.writeBoolean("include_main",m_include_main);
		out.writeBoolean("include_ctor",m_include_ctor);
		out.writeBoolean("include_nonstandard",m_include_nonstandard);
		out.writeBoolean("include_loadimage",m_include_loadimage);
		out.writeBoolean("include_binding",m_include_binding);
		out.writeBoolean("build_constant",m_build_constant);
	}



}
