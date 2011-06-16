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
package com.subgraph.vega.api.model.web.forms;

import java.util.Set;


public interface IWebFormField {
	public enum FieldType { INPUT_TEXT, INPUT_PASSWORD, INPUT_CHECKBOX, INPUT_RADIO, INPUT_HIDDEN, INPUT_FILE, TEXTAREA, SELECT_OPTION };
	
	FieldType getType();
	String getName();
	String getValue();
	boolean hasMultipleValues();
	Set<String> getMultipleValues();
	Set<String> getCheckedValues();

}
