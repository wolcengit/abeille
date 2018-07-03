package com.jeta.swingbuilder.codegen.builder;

import java.util.Collection;

import com.jeta.forms.store.memento.ComponentMemento;

public interface BaseBeanWriter {

	public abstract void createBeanSource(MethodWriter mr, ComponentMemento cm);

	public abstract void createBeanSourceCustom(MethodWriter mr, ComponentMemento cm, String variable);

	public abstract void addStatement(Statement stmt);

	public abstract Collection getStatements();

	public abstract void setBeanVariable(String varName, Class beanType);

	public abstract String getBeanVariable();

	public abstract Class getBeanType();

	public abstract String getResultVariable();

	public abstract Class getResultType();

	public abstract void setResultVariable(String varName, Class resultType);

}