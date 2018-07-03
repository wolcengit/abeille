/**
 * 
 */
package com.jeta.forms.components;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Wolcen
 *
 */
public class SwingComponentBindingSupport implements PropertyChangeListener {
	private List listeners = new ArrayList();
	private List<ComponentBindingInfo> infos;
	/**
	 * 
	 */
	public SwingComponentBindingSupport() {
	}
	
	public void clearListeners(){
		listeners.clear();
	}
	
	public void addListener(SwingComponentBindingListener listener){
		if(!listeners.contains(listener)){
			listeners.add(listener);
		}
	}
	public void removeListener(SwingComponentBindingListener listener){
		if(listeners.contains(listener)){
			listeners.remove(listener);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	      Object source = evt.getSource();
	      String propName = evt.getPropertyName();
	      Object oldValue = evt.getOldValue();
	      Object newValue = evt.getNewValue();
	      if(propName == null) return;
	      if(oldValue == null && newValue == null)
	      {
	         	return;
	      }if(oldValue != null&&newValue!=null&&oldValue.equals(newValue))
	      {
	         	return;
	      }if((oldValue instanceof Date) && (newValue instanceof Date))
	      {
	         	if(((Date)oldValue).getTime() == ((Date)newValue).getTime()) return;
	      }
			for(ComponentBindingInfo cbi:infos){
				if(cbi.comp.equals(source) && cbi.prop.equals(propName)){
					for(Object o:listeners){
						SwingComponentBindingListener listener = (SwingComponentBindingListener)o;
						listener.firePropertyChange(cbi.key, cbi.comp, cbi.prop, cbi.clazz,oldValue, newValue);
					}
					
				}
			}
	}
	
	public void setPropertyValue(String propKey,Object value){
		for(ComponentBindingInfo cbi:infos){
			if(cbi.key.equals(propKey) && cbi.method != null && cbi.clazz != null){
				Object newValue = ObjectConvert.Converter(cbi.clazz, value);
				try {
					cbi.method.invoke(cbi.comp, newValue);
				} catch (Exception e) {
					e.printStackTrace();
				}
				 
			}
		}
	}
	
	public void registerBindings(Component comp,String bindings){
		if(infos == null){
			infos = new ArrayList<ComponentBindingInfo>();
		}
		String[] binddefs = bindings.split(";");
		for(String binddef:binddefs){
			String key = comp+".getName()";
			String prop = binddef;
			if(binddef.indexOf("=") != -1){
				key  = binddef.substring(0,binddef.indexOf("="));
				prop = binddef.substring(binddef.indexOf("=") + 1);
			}
			infos.add(new ComponentBindingInfo(key,comp,prop));
		}
	}
	
	public void replaceBindings(Component compOld,Component comp,String bindings){
		if(infos == null){
			infos = new ArrayList<ComponentBindingInfo>();
		}
		String[] binddefs = bindings.split(";");
		for(String binddef:binddefs){
			String key = comp+".getName()";
			String prop = binddef;
			if(binddef.indexOf("=") != -1){
				key  = binddef.substring(0,binddef.indexOf("="));
				prop = binddef.substring(binddef.indexOf("=") + 1);
			}
			infos.add(new ComponentBindingInfo(key,comp,prop));
		}
		for(ComponentBindingInfo cbi:infos){
			if(cbi.comp.equals(compOld)){
				infos.remove(cbi);
			}
		}
	}
	

	class ComponentBindingInfo{
		public String key = null;
		public Component comp = null;
		public String prop = null;
		public Method method = null;
		public Class  clazz = null;
		
		private ComponentBindingInfo(String key, Component comp,String prop){
			this.key = key;
			this.comp = comp;
			this.prop = prop;
			try {
				String name = prop.substring(0,1).toUpperCase()+prop.substring(1);
				Class type = comp.getClass();
				Method method0 = type.getMethod("get"+name);
				this.clazz = method0.getReturnType();
				this.method = type.getMethod("set"+name,this.clazz);
				this.method.setAccessible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
