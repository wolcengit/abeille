/**
 * 
 */
package com.jeta.sample;

import com.jeta.swingbuilder.codegen.builder.BaseBeanWriter;
import com.jeta.swingbuilder.codegen.builder.BeanWriter;
import com.jeta.swingbuilder.codegen.builder.PropertyWriterFactory;

/**
 * @author Wolcen
 *
 */
public class SamplePropertyWriterFactory extends PropertyWriterFactory {

	/**
	 * 
	 */
	public SamplePropertyWriterFactory() {
	}

	@Override
	public BaseBeanWriter createBeanWriter() {
		BaseBeanWriter writer = new SampleBeanWriter();
		return  writer;
	}

	
}
