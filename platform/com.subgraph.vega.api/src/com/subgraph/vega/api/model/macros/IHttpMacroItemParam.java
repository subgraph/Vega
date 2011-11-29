/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.api.model.macros;

/**
 * Parameter submitted as part of an IHttpMacroItem.
 */
public interface IHttpMacroItemParam {
	/**
	 * Enumeration of parameter value sources.
	 */
	enum ValueSource {
		SOURCE_LITERAL("literal value"), /** Use a literal value. */
		SOURCE_VARIABLE("use a dictionary variable"), /** Obtain the variable from an identity variable dictionary. */
		SOURCE_DERIVE("derive from previous page"); /** Derive the value from the previous page retrieval. */
		
		private String description;

		private ValueSource(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}
	};

	/**
	 * Get the parameter name.
	 * @return Parameter name.
	 */
	String getName();

	/**
	 * Set the value source for the parameter.
	 * @param source Value source.
	 */
	void setValueSource(ValueSource source);
	
	/**
	 * Get the value source for the parameter.
	 * @return Value source.
	 */
	ValueSource getValueSource();

	/**
	 * Set the value of the parameter. Varies depending on the ValueSource:
	 * 	- SOURCE_LITERAL: the literal value to submit.
	 * 	- SOURCE_VARIABLE: the name of the identity variable to obtain a value from.
	 *  - SOURCE_DERIVE: must be null
	 */
	void setValue(String value);
	
	/**
	 * Get the value of the parameter. Varies depending on the ValueSource:
	 * 	- SOURCE_LITERAL: the literal value to submit.
	 * 	- SOURCE_VARIABLE: the name of the identity variable to obtain a value from.
	 *  - SOURCE_DERIVE: always null
	 * @return Parameter value, or null if none is set.
	 */
	String getValue();	
}
