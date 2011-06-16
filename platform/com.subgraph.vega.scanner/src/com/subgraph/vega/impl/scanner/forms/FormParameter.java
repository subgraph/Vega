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
package com.subgraph.vega.impl.scanner.forms;

public class FormParameter {
	static FormParameter create(String name, String value) {
		return null;
	}

	private final String name;
	private final String value;
	private final boolean isPasswordField;
	private final boolean needsGuess;

	FormParameter(String name, String value, boolean isPassword, boolean needsGuess) {
		this.name = name;
		this.value = value;
		this.isPasswordField = isPassword;
		this.needsGuess = needsGuess;
	}

}
