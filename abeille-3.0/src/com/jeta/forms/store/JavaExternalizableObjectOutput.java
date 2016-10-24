package com.jeta.forms.store;

import java.io.IOException;
import java.io.ObjectOutput;

public class JavaExternalizableObjectOutput implements JETAObjectOutput {

	private ObjectOutput m_out;

	public JavaExternalizableObjectOutput(ObjectOutput delegate) {
		m_out = delegate;
	}

	public void writeVersion(int version) throws IOException {
		m_out.writeInt(version);
	}

	public void writeFloat(String tagName, float value) throws IOException {
		m_out.writeFloat(value);
	}

	public void writeInt(String tagName, int value) throws IOException {
		m_out.writeInt(value);
	}

	public void writeObject(String tagName, Object obj) throws IOException {
		m_out.writeObject(obj);
	}
	public void writeString(String tagName, String strVlaue) throws IOException {
		m_out.writeObject(strVlaue);
	}

	public void writeBoolean(String string, boolean boolValue) throws IOException {
		m_out.writeBoolean(boolValue);
	}

	public JETAObjectOutput getSuperClassOutput(Class superClass) {
		return this;
	}

	public void writeInt(String string, int value, int defaultValue) throws IOException {
		m_out.writeInt(value);

	}

	public void writeFloat(String string, float value, float defaultValue) throws IOException {
		m_out.writeFloat(value);
	}

	public void writeBoolean(String string, boolean bval, boolean defaultValue) throws IOException {
		m_out.writeBoolean(bval);
	}

	
	
	public void writeDouble(String tagName, double value) throws IOException {
		m_out.writeDouble(value);
	}

	
	public void writeDouble(String string, double value, double defaultValue)
			throws IOException {
		m_out.writeDouble(value);
	}

	
	public void writeLong(String tagName, long value) throws IOException {
		m_out.writeLong(value);
	}

	
	public void writeLong(String string, long value, long defaultValue)
			throws IOException {
		m_out.writeLong(value);
	}
}
