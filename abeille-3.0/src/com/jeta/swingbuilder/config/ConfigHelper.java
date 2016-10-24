/**
 * 
 */
package com.jeta.swingbuilder.config;

import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.jeta.forms.beanmgr.BeanManager;
import com.jeta.forms.components.ObjectConvert;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.URLClassLoaderHelper;
import com.jeta.open.registry.JETARegistry;
import com.jeta.sample.SampleColorEditor;
import com.jeta.swingbuilder.gui.beanmgr.DefaultBean;
import com.jeta.swingbuilder.gui.componentstoolbar.ComponentsToolBarManager;
import com.jeta.swingbuilder.gui.properties.JETAPropertyEditor;
import com.jeta.swingbuilder.gui.properties.PropertyTableModel;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * @author Wolcen
 *
 */
public class ConfigHelper {

	public static final String XSD_ABEILLE_TOOLBAR = "/com/jeta/swingbuilder/config/abeille_toolbar.xsd";
	public static final String XSD_ABEILLE_BEANS = "/com/jeta/swingbuilder/config/abeille_beans.xsd";
	
	
	public static void registerBeansFromXmlString(String xmlstring) throws Exception {
		validateXml(xmlstring,XSD_ABEILLE_BEANS);
		StringReader reader = new StringReader(xmlstring);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(reader));
		Element root = doc.getDocumentElement();
		NodeList rlst = root.getChildNodes();
		Element beans = null; 
		for (int i = 0; i < rlst.getLength(); i++) {
			Node nd = rlst.item(i);
			if ("beans".equals(nd.getNodeName())) {
				beans = (Element) nd;
				break;
			}
		}
		
		BeanManager bm = (BeanManager) JETARegistry.lookup(BeanManager.COMPONENT_ID);
		
		Map<String,Element> bmmap = new HashMap<String,Element>();
		Map<String,String> bmref = new HashMap<String,String>();
		
		
		NodeList elst = beans.getChildNodes();
		for (int i = 0; i < elst.getLength(); i++) {
			Node nd = elst.item(i);
			if ("bean".equals(nd.getNodeName())) {
				Element ed = (Element) nd;
				if(!ed.hasAttribute("id")){
					System.err.println("found bean with no id.");
					continue;
				}
				String id = ed.getAttribute("id");
				if(bm.containsBean(id)){
					System.err.println("found registry bean with this id :"+id);
					continue;
				}
				if(ed.hasAttribute("ref")){
					bmref.put(id, ed.getAttribute("ref"));
					bmmap.put(id, ed);
				}else{
					DefaultBean bean = new DefaultBean(id);
					setDefaultBean(bean,ed);
					bm.registerCustomDefaultBean(id,bean.getDescription(),
							bean.getClassName(), 
							bean.getIcon(),
							bean.getCtorParams(),bean.getCtorArgs(),
							bean.getCustomProperties(),
							bean.getInitializeProperties(),
							bean.getCodeOnlyProperties(),
							bean.isScrollable());
				}
			}
		}
		for(String bid:bmmap.keySet()){
			List<String> refs = new ArrayList<String>();
			String ref = bid;
			while(bmref.containsKey(ref) && !bm.containsBean(ref)){
				refs.add(ref);
				ref = bmref.get(ref);
			}
			if(!bm.containsBean(ref)){
				System.err.println("found bean with error ref :"+bid);
				continue;
			}
			DefaultBean rb = (DefaultBean) bm.getRegisteredBean(ref);
			DefaultBean bean = (DefaultBean) rb.clone();
			bean.setId(bid);
			for(int i=refs.size() - 1; i >= 0;i --){
				ref = refs.get(i);
				Element ed = bmmap.get(ref);
				setDefaultBean(bean,ed);
			}
			bm.registerCustomDefaultBean(bid,bean.getDescription(),
					bean.getClassName(), 
					bean.getIcon(),
					bean.getCtorParams(),bean.getCtorArgs(),
					bean.getCustomProperties(),
					bean.getInitializeProperties(),
					bean.getCodeOnlyProperties(),
					bean.isScrollable());
		}
	}
	
	private static void setDefaultBean(DefaultBean bean,Element ed){
		String eid = ed.hasAttribute("id")?ed.getAttribute("id"):null;
		String className = ed.hasAttribute("class")?ed.getAttribute("class"):null;
		String description = ed.hasAttribute("description")?ed.getAttribute("description"):null;
		String icon = ed.hasAttribute("icon")?ed.getAttribute("icon"):null;
		String scrollable = ed.hasAttribute("scrollable")?ed.getAttribute("scrollable"):null;
		Class[] params = null;
		Object[] args = null;
		String factory = null;
		
		NodeList elst = ed.getChildNodes();
		for (int i = 0; i < elst.getLength(); i++) {
			Node nd = elst.item(i);
			if ("ctor".equals(nd.getNodeName())) {
				Element ed1 = (Element) nd;
				if(!ed1.hasAttribute("factory")){
					factory = ed1.getAttribute("factory");
					if(factory != null && factory.indexOf(".") != -1){
						bean.setFactory(factory);
					}else{
						factory = null;
						System.err.println("found bean ctor with error factory :" + bean.getId());
						return;
					}
				}
						
				NodeList elst1 = ed1.getChildNodes();
				int cnt = 0;
				for (int i1 = 0; i1 < elst1.getLength(); i1++) {
					Node nd1 = elst1.item(i1);
					if ("parm".equals(nd1.getNodeName())) {
						cnt ++;
					}
				}
				params = new Class[cnt];
				args = new Object[cnt];
				for (int i1 = 0; i1 < elst1.getLength(); i1++) {
					Node nd1 = elst1.item(i1);
					if ("parm".equals(nd1.getNodeName())) {
						Element ed2 = (Element) nd1;
						if(!ed2.hasAttribute("index") || !ed2.hasAttribute("class") || !ed2.hasAttribute("value")){
							System.err.println("found bean ctor parm with no index or class or value :" + bean.getId());
							return;
						}
						String index = ed2.getAttribute("index");
						Integer index1 = ObjectConvert.StringToInteger(index);
						if(index1 == null){
							System.err.println("found bean ctor parm with error index :" + bean.getId());
							return;
						}
						String className1 = ed2.getAttribute("class");
						Class<?> clazz = null;
						try {
							if(className1.indexOf(".") == -1) className1 = "java.lang."+className1;
							clazz = Class.forName(className1);
						} catch (ClassNotFoundException e) {
							System.err.println("found bean ctor parm with error class :" + bean.getId());
							return;
						}
						if(clazz == null){
							System.err.println("found bean ctor parm with error class :" + bean.getId());
							return;
						}
						String value = ed2.getAttribute("value");
						params[index1] = clazz;
						args[index1] = (value.equals("null")) ? null: ObjectConvert.Converter(clazz,value);
					}
				}
				
			}else if ("properties".equals(nd.getNodeName())) {
				Element ed1 = (Element) nd;
				NodeList elst1 = ed1.getChildNodes();
				for (int i1 = 0; i1 < elst1.getLength(); i1++) {
					Node nd1 = elst1.item(i1);
					if ("property".equals(nd1.getNodeName())) {
						Element ed2 = (Element) nd1;
						if(!ed2.hasAttribute("name")){
							System.err.println("found bean property with no name :" + bean.getId());
							return;
						}
						String name = ed2.getAttribute("name");
						String value = ed2.hasAttribute("value")?ed2.getAttribute("value"):null;
						String codeonly = ed2.hasAttribute("codeonly")?ed2.getAttribute("codeonly"):null;
						String write = ed2.hasAttribute("write")?ed2.getAttribute("write"):null;
						Boolean codeonly1 = ObjectConvert.StringToBoolean(codeonly);
						Boolean write1 = ObjectConvert.StringToBoolean(write);
						if(write1 != null){
							if(bean.getCustomProperties() == null){
								bean.setCustomProperties(new Properties());
							}
							bean.getCustomProperties().setProperty(name, write1?"rw":"r");
						}
						if(value != null && name.indexOf("@") == -1){
							if(bean.getInitializeProperties() == null){
								bean.setInitlizeProperties(new Properties());
							}
							bean.getInitializeProperties().setProperty(name, value);
						}
						if(value !=null && codeonly1 != null){
							if(bean.getCodeOnlyProperties() == null){
								bean.setCodeOnlyProperties(new Properties());
							}
							bean.getCodeOnlyProperties().setProperty(name, value);
						}
						if(ed2.hasAttribute("editor") && bean.getId().equals(eid)){
							String editor = ed2.getAttribute("editor");
							URLClassLoaderHelper loader = new URLClassLoaderHelper();
							try {
								Class clazz = loader.getClass(editor);
								if(clazz != null && JETAPropertyEditor.class.isAssignableFrom(clazz)){
									PropertyTableModel.registerCustomPropertyEditors(bean.getId(), name,SampleColorEditor.class);
								}else{
									System.err.println("found bean property with error edotor :" + bean.getId());
								}
							} catch (FormException e) {
								//e.printStackTrace();
								System.err.println("found bean property with error edotor :" + bean.getId());
							}
							
						}
						
					}
				}
			}
		}
		Boolean scrollable1 = ObjectConvert.StringToBoolean(scrollable);
		
		if(description != null) bean.setDescription(description);
		if(className != null) bean.setClassName(className);
		if(icon != null) bean.setIcon(FormDesignerUtils.loadImage(icon));
		if(scrollable1 != null) bean.setScrollable(scrollable1);
		
		//check ctor
		if(params != null){
			if(factory == null){
				className = bean.getClassName();
				URLClassLoaderHelper loader = new URLClassLoaderHelper();
				try {
					boolean ctor = false;
					Class clazz = loader.getClass(className);
					Constructor[] consts = clazz.getConstructors();
					for(Constructor c:consts){
						Class[] paramTypes = c.getParameterTypes();
						ctor = checkParams(paramTypes,params);
						if(ctor){
							params = paramTypes;
							break;
						}
					}
					if(!ctor){
						System.err.println("found bean ctor with error class :" + bean.getId());
						return;
					}
				} catch (FormException e) {
					//e.printStackTrace();
					System.err.println("found bean with error class :" + bean.getId());
					return;
				}
			}else{
				String fclassName,methodName;
				boolean staticMethod = true;
				fclassName = factory.substring(0, factory.lastIndexOf("."));
				methodName = factory.substring(factory.lastIndexOf(".") + 1);
				if(fclassName.startsWith("@")){
					staticMethod = false;
					fclassName = className.substring(1);
				}
				URLClassLoaderHelper loader = new URLClassLoaderHelper();
				try {
					boolean ctor = false;
					Class clazz = loader.getClass(fclassName);
					Method[] methods = clazz.getMethods();
					for(Method c:methods){
						if(!c.getName().equals(methodName)) continue;
						Class[] paramTypes = c.getParameterTypes();
						ctor = checkParams(paramTypes,params);
						if(ctor){
							params = paramTypes;
							break;
						}
					}
					if(!ctor){
						System.err.println("found bean ctor with error class :" + bean.getId());
						return;
					}
				} catch (FormException e) {
					//e.printStackTrace();
					System.err.println("found bean with error class :" + bean.getId());
					return;
				}
			}
			
			bean.setParams(params);
			bean.setArgs(args);
		}
	}
	
	private static boolean checkParams(Class[] paramTypes,Class[] params){
		if(paramTypes !=null && paramTypes.length==params.length){
			int index = 0;
			for(int i=0;i<paramTypes.length;i++){
				if(ObjectConvert.isClassEquals(paramTypes[i], params[i])){
					index ++;
				}else{
					return false;
				}
			}
			return (index == paramTypes.length);
		}
		return false;
	}

	public static void registerToolbarFromXmlString(String xmlstring) throws Exception{
		validateXml(xmlstring,XSD_ABEILLE_TOOLBAR);
		StringReader reader = new StringReader(xmlstring);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(reader));
		Element root = doc.getDocumentElement();
		NodeList rlst = root.getChildNodes();
		Element toolbar = null; 
		for (int i = 0; i < rlst.getLength(); i++) {
			Node nd = rlst.item(i);
			if ("toolbar".equals(nd.getNodeName())) {
				toolbar = (Element) nd;
				break;
			}
		}
		
		BeanManager bm = (BeanManager) JETARegistry.lookup(BeanManager.COMPONENT_ID);
		ComponentsToolBarManager ctm = (ComponentsToolBarManager) JETARegistry.lookup(ComponentsToolBarManager.COMPONENT_ID);
		
		NodeList elst = toolbar.getChildNodes();
		for (int i = 0; i < elst.getLength(); i++) {
			Node nd = elst.item(i);
			if ("bar".equals(nd.getNodeName())) {
				Element ed1 = (Element) nd;
				if(!ed1.hasAttribute("description")){
					System.err.println("found bar with no description ");
					continue;
				}
				String description = ed1.getAttribute("description");
				NodeList elst1 = ed1.getChildNodes();
				for (int i1 = 0; i1 < elst1.getLength(); i1++) {
					Node nd1 = elst1.item(i1);
					if ("item".equals(nd1.getNodeName())) {
						Element ed2 = (Element) nd1;
						if(!ed2.hasAttribute("ref")){
							System.err.println("found bar item with no ref : " + description);
							continue;
						}
						String description2 = ed2.hasAttribute("description")?ed2.getAttribute("description"):null;
						String icon = ed2.hasAttribute("icon")?ed2.getAttribute("icon"):null;
						String ref = ed2.getAttribute("ref");
						if(!bm.containsBean(ref)){
							System.err.println("found bar item with err ref : " + description);
							continue;
						}
						boolean rc = ctm.registerCustomToolbar(description, ref,description2,FormDesignerUtils.loadImage(icon));
						if(!rc){
							System.err.println("register toolbar item err : " + description +" :" + ref);
						}
					}
				}
				ctm.showToolbar(description);
			}
		}
	}
	public static void validateXml(String xmlstring,String xsd) throws Exception {
		StringReader reader = new StringReader(xmlstring);
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Source sourceSchema = new StreamSource(ConfigHelper.class.getResourceAsStream(xsd));
		Schema schema = factory.newSchema(sourceSchema);
		Validator validator = schema.newValidator();
		Source source = new StreamSource(reader);
		validator.validate(source);
	}
	
}
