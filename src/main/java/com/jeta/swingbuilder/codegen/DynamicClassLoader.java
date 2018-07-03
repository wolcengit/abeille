package com.jeta.swingbuilder.codegen;
 

public class DynamicClassLoader extends ClassLoader {
	private String className;
	private byte[] classData;
	
	public DynamicClassLoader(String className,byte[] classData) {
		this.className = className;
		this.classData = classData;
	}
	
	public DynamicClassLoader(String className,byte[] classData,ClassLoader parent) {
		super(parent);
		this.classData = classData;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		return super.defineClass(name, classData, 0, classData.length);
	}

	
	public static Class<?> getDynamicClass(String className,byte[] classData) throws ClassNotFoundException {
		DynamicClassLoader loader = new DynamicClassLoader(className,classData);
		return loader.loadClass(className);
	}
	
	public static Class<?> getDynamicClass(String className,byte[] classData,ClassLoader parent) throws ClassNotFoundException {
		DynamicClassLoader loader = new DynamicClassLoader(className,classData,parent);
		return loader.loadClass(className);
	}
	
	
}



