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

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jeta.forms.components.SwingComponentBindingListener;
import com.jeta.forms.components.SwingComponentBindingSupport;
import com.jeta.forms.store.memento.BeanMemento;
import com.jeta.forms.store.memento.ComponentMemento;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.forms.store.memento.PropertiesMemento;
import com.jeta.open.i18n.I18NUtils;
import com.jeta.open.registry.JETARegistry;

public class BuilderUtils {
	public static final String PROP_TABORDER = "taborder@Integer";
	public static final String PROP_BINDING = "binding@String";

	/**
	 * Builds the constructor
	 */
	public static void buildConstructor(DeclarationManager declMgr, String className) {
		declMgr.addImport("javax.swing.JFrame");

		final String ctor = className;
		MethodWriter method_writer = new MethodWriter(declMgr, null, "main") {
			protected String getSignature() {
				return "public " + ctor + "()";
			}
		};

		method_writer.addCommentLine("Default constructor");

		Block block = new Block();
		block.addCode("initializePanel();");
		method_writer.addSegment(block);
		declMgr.addMethod(method_writer);
	}

	/**
	 * Builds the initializer
	 */
	public static void buildInitializer(DeclarationManager declMgr, String methodExpression) {
		MethodWriter method_writer = new MethodWriter(declMgr, null, "initializePanel");
		method_writer.setAccess("protected");

		method_writer.addCommentLine("Initializer");
		declMgr.addImport("java.awt.BorderLayout");

		Block block = new Block();
		String frame_code = "setLayout(new BorderLayout());" + "add(" + methodExpression + ", BorderLayout.CENTER);";
		block.addCode(frame_code);
		method_writer.addSegment(block);
		
		Block taborders = buildTabOrder((ClassDeclarationManager)declMgr);
		if(taborders != null){
			method_writer.addSegment(taborders);
		}
		
		if(BuilderUtils.buildBinding((ClassDeclarationManager)declMgr)){
			block = new Block();
			block.addCode("initializeBindings();");
			method_writer.addSegment(block);
		}

		
		declMgr.addMethod(method_writer);
		
	}

	/**
	 * Builds a 'main' method
	 */
	public static void buildMain(DeclarationManager declMgr, String className) {
		MethodWriter method_writer = new MethodWriter(declMgr, null, "main") {
			protected String getSignature() {
				return "public static void main(String[] args)";
			}
		};

		method_writer.addCommentLine("Main method for panel");

		Block block = new Block();
		String frame_code = "JFrame frame = new JFrame();" + "frame.setSize(600, 400);" + "frame.setLocation(100, 100);" + "frame.getContentPane().add(new "
				+ className + "());" + "frame.setVisible(true);";

		block.addCode(frame_code);
		block.println();

		declMgr.addImport("java.awt.event.WindowAdapter");
		declMgr.addImport("java.awt.event.WindowEvent");

		String listener_code = "frame.addWindowListener( new WindowAdapter()" + "{" + "public void windowClosing( WindowEvent evt )" + "{" + "System.exit(0);"
				+ "}\n});";
		block.addCode(listener_code);

		method_writer.addSegment(block);
		declMgr.addMethod(method_writer);
	}

	/**
	 * Builds a 'fill' method
	 */
	public static void buildFillMethod(DeclarationManager declMgr) {
		MethodWriter method_writer = new MethodWriter(declMgr, null, "addFillComponents") {
			protected String getSignature() {
				return "void addFillComponents( Container panel, int[] cols, int[] rows )";
			}
		};

		declMgr.addImport("java.awt.Container");
		declMgr.addImport("java.awt.Dimension");
		declMgr.addImport("javax.swing.Box");

		method_writer.addCommentLine("Adds fill components to empty cells in the first row and first column of the grid.");
		method_writer.addCommentLine("This ensures that the grid spacing will be the same as shown in the designer.");
		method_writer.addCommentLine("@param cols an array of column indices in the first row where fill components should be added.");
		method_writer.addCommentLine("@param rows an array of row indices in the first column where fill components should be added.");

		Block block = new Block();
		String fill_code1 = "Dimension filler = new Dimension(10,10);\n" + "boolean filled_cell_11 = false;" + "CellConstraints cc = new CellConstraints();"
				+ "if ( cols.length > 0 && rows.length > 0 )" + "{" + "if ( cols[0] == 1 && rows[0] == 1 )" + "{" + "/** add a rigid area  */\n"
				+ "panel.add( Box.createRigidArea( filler ), cc.xy(1,1) );" + "filled_cell_11 = true;" + "}\n" + "}\n\n";

		block.addCode(fill_code1);
		method_writer.addSegment(block);

		block = new Block();
		method_writer.addSegment(new BasicExpression("for( int index = 0; index < cols.length; index++ )"));
		String fill_code2 = "{" + "if ( cols[index] == 1 && filled_cell_11 )" + "{" + "continue;" + "}\n"
				+ "panel.add( Box.createRigidArea( filler ), cc.xy(cols[index],1) );" + "}\n\n";

		block.addCode(fill_code2);
		method_writer.addSegment(block);

		block = new Block();
		method_writer.addSegment(new BasicExpression("for( int index = 0; index < rows.length; index++ )"));
		String fill_code3 = "{" + "if ( rows[index] == 1 && filled_cell_11 )" + "{" + "continue;" + "}\n"
				+ "panel.add( Box.createRigidArea( filler ), cc.xy(1,rows[index]) );" + "}\n\n";

		block.addCode(fill_code3);
		method_writer.addSegment(block);
		declMgr.addMethod(method_writer);
	}

	public static void buildImageLoader(DeclarationManager declMgr) {
		MethodWriter method_writer = new MethodWriter(declMgr, null, "loadImage") {
			protected String getSignature() {
				return "public ImageIcon loadImage( String imageName )";
			}
		};

		declMgr.addImport("javax.swing.ImageIcon");

		method_writer.addCommentLine("Helper method to load an image file from the CLASSPATH");
		method_writer.addCommentLine("@param imageName the package and name of the file to load relative to the CLASSPATH");
		method_writer.addCommentLine("@return an ImageIcon instance with the specified image file");
		method_writer.addCommentLine("@throws IllegalArgumentException if the image resource cannot be loaded.");

		Block block = new Block();
		String codeblock = "try" + "{" + "ClassLoader classloader = getClass().getClassLoader();" + "java.net.URL url = classloader.getResource( imageName );"
				+ "if ( url != null )" + "{" + "ImageIcon icon = new ImageIcon( url );" + "return icon;" + "}\n" + "}\n" + "catch( Exception e )" + "{"
				+ "e.printStackTrace();" + "}\n" + "throw new IllegalArgumentException( \"Unable to load image: \" + imageName );";

		block.addCode(codeblock);
		method_writer.addSegment(block);
		declMgr.addMethod(method_writer);
	}

	public static void buildApplyComponentOrientation(DeclarationManager declMgr) {
		MethodWriter method_writer = new MethodWriter(declMgr, null, "applyComponentOrientation") {
			protected String getSignature() {
				return "public void applyComponentOrientation( ComponentOrientation orientation )";
			}
		};

		declMgr.addImport("java.awt.ComponentOrientation");
		//declMgr.addImport("com.jeta.open.i18n.I18NUtils");

		method_writer.addCommentLine("Method for recalculating the component orientation for ");
		method_writer.addCommentLine("right-to-left Locales.");
		method_writer.addCommentLine("@param orientation the component orientation to be applied");

		Block block = new Block();
		StringBuffer codeblock = new StringBuffer();
		codeblock.append("// Not yet implemented...").append("\n");
		codeblock.append("// I18NUtils.applyComponentOrientation(this, orientation);");
		codeblock.append("super.applyComponentOrientation(orientation);");

		block.addCode(codeblock.toString());
		method_writer.addSegment(block);
		declMgr.addMethod(method_writer);
	}
	
	public static Block buildTabOrder(ClassDeclarationManager declMgr) {
		Map<String,Integer> taborders = new HashMap<String,Integer>();
		Collection fields = declMgr.getFields();
		Iterator iter = fields.iterator();
		while (iter.hasNext()) {
			MemberVariableDeclaration field = (MemberVariableDeclaration) iter.next();
			ComponentMemento cm = field.getMemento();
			if(cm != null){
				PropertiesMemento pm = null;
				if(cm instanceof FormMemento )
					pm = ((FormMemento)cm).getPropertiesMemento();
				else
					pm = ((BeanMemento)cm).getProperties();
				if(pm.containsProperty(PROP_TABORDER)){
					Object value = pm.getPropertyValueEx(PROP_TABORDER);
					if(value instanceof Integer){
						Integer nvalue = (Integer)value;
						taborders.put(field.getVariable(),nvalue);
					}
				}
			}
		}
		if(taborders.size() == 0) return null;
		declMgr.addImport("com.jeta.forms.components.FocusTraversalOnArray");
		declMgr.addImport("java.awt.Component");
		
		
		List<Map.Entry<String,Integer>> taborders1 =  new ArrayList<Map.Entry<String,Integer>>(taborders.entrySet()); 
		Collections.sort(taborders1, new Comparator<Map.Entry<String,Integer>>(){ 
			public int compare(Map.Entry<String,Integer> obj1,Map.Entry<String,Integer> obj2){ 
				return obj1.getValue().compareTo(obj2.getValue()); 
			} 
			});
		
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("this.setFocusTraversalPolicyProvider(true);");
		sb.append("this.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{");
		int count = taborders.keySet().size();
		int index = 0;
		for(Map.Entry<String,Integer> obj:taborders1){ 
			sb.append(obj.getKey());
			index ++;
			if(index < count) sb.append(",");
		} 
		sb.append("\n}));");
		Block block = new Block();
		block.addCode(sb.toString());
		
		return block;
	}

	public static boolean buildBinding(ClassDeclarationManager declMgr) {
		Map<String,String> bindings = new HashMap<String,String>();
		Collection fields = declMgr.getFields();
		Iterator iter = fields.iterator();
		while (iter.hasNext()) {
			MemberVariableDeclaration field = (MemberVariableDeclaration) iter.next();
			ComponentMemento cm = field.getMemento();
			if(cm != null){
				PropertiesMemento pm = null;
				if(cm instanceof FormMemento )
					pm = ((FormMemento)cm).getPropertiesMemento();
				else
					pm = ((BeanMemento)cm).getProperties();
				if(pm.containsProperty(PROP_BINDING)){
					Object value = pm.getPropertyValueEx(PROP_BINDING);
					if(value instanceof String){
						String svalue = (String)value;
						bindings.put(field.getVariable(),svalue);
					}
				}
			}
		}
		boolean buildIncBinding = (Boolean) JETARegistry.lookup(SourceBuilder.BUILD_INCLUDE_BINDING);
		if(bindings.size() == 0 && !buildIncBinding){
			return false;
		}
		declMgr.addImport("com.jeta.forms.components.SwingComponentBindingSupport");
		declMgr.addImport("com.jeta.forms.components.SwingComponentBindingListener");
		declMgr.addImport("java.awt.Component");
		
		VariableDeclaration ds = new MemberVariableDeclaration(declMgr, SwingComponentBindingSupport.class,true);
		declMgr.addMemberVariable(ds);
		
		MethodWriter method_writer = new MethodWriter(declMgr, null, "initializeBindings");
		method_writer.setAccess("protected");
		
		Block block = new Block();
		for(String comp:bindings.keySet()){
			String value = bindings.get(comp);
			block.addCode(ds.getVariable()+".registerBindings("+comp+",\""+value+"\");");
		}
		method_writer.addSegment(block);
		declMgr.addMethod(method_writer);
		
		method_writer = new MethodWriter(declMgr, null, "addListener") {
			protected String getSignature() {
				return "public void addListener(SwingComponentBindingListener listener)";
			}
		};
		block = new Block();
		block.addCode(ds.getVariable()+".addListener(listener);");
		method_writer.addSegment(block);
		declMgr.addMethod(method_writer);
		
		method_writer = new MethodWriter(declMgr, null, "removeListener") {
			protected String getSignature() {
				return "public void removeListener(SwingComponentBindingListener listener)";
			}
		};
		block = new Block();
		block.addCode(ds.getVariable()+".removeListener(listener);");
		method_writer.addSegment(block);
		declMgr.addMethod(method_writer);
		
		method_writer = new MethodWriter(declMgr, null, "setPropertyValue") {
			protected String getSignature() {
				return "public void setPropertyValue(String propKey,Object value)";
			}
		};
		block = new Block();
		block.addCode(ds.getVariable()+".setPropertyValue(propKey, value);");
		method_writer.addSegment(block);
		declMgr.addMethod(method_writer);
		
		
		
		
		return true;
	}
	
	
}
