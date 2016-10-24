package com.jeta.swingbuilder.codegen;
 

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;

import org.apache.commons.io.IOUtils;

import com.sun.tools.javac.api.JavacTool;
 
 
public class DynamicJavaCompiler {
 
    public static Object[]  javaCodeToClass(String cls,String src) {
        JavaCompiler jc = JavacTool.create();
        
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = jc.getStandardFileManager(diagnostics, null, null);

        List<JavaFileObject> compilationUnits = new ArrayList<JavaFileObject>();
        compilationUnits.add(new JavaSourceFromString(cls, src));
        
        boolean b = jc.getTask(null, fileManager, null, null, null, compilationUnits).call();
        if(!b) return null;
        String fn = cls.substring(cls.lastIndexOf(".")+ 1)+".class";
        File f = new File(fn);
        if(!f.exists()) return null;
		
		Class clazz = null;
		byte[] classData = null;
		try {
			FileInputStream input = new FileInputStream(fn);
			classData = IOUtils.toByteArray(input);
			input.close();
			clazz = DynamicClassLoader.getDynamicClass(cls,classData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	   f.delete();
       Object[] ret = new Object[2];
       ret[0] = clazz;
       ret[1] = classData;
       return ret;
    }
 
    public static class JavaSourceFromString extends SimpleJavaFileObject {
        final String code;
 
        /**
         * 
         *
         * @param name the name of the compilation unit represented by this file object
         * @param code the source code for the compilation unit represented by this file object
         */
        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
                    Kind.SOURCE);
            this.code = code;
        }
 
        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
    
}
