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
package com.subgraph.vega.internal.model.web.forms;

import java.util.Set;

import com.subgraph.vega.api.model.web.forms.IWebFormField;

public class FormFiller {
	

	String getDefaultValue(IWebFormField field) {
		switch(field.getType()) {
		case INPUT_TEXT:
			return getDefaultTextValue(field);
		case INPUT_PASSWORD:
		case INPUT_FILE:
			return "";
		case INPUT_HIDDEN:
			return field.getValue();
		case INPUT_RADIO:
		case SELECT_OPTION:
		case INPUT_CHECKBOX:
			return getDefaultMultipleValue(field);
		case TEXTAREA:
			return "Put Text here";
		}
		return "foo";
	}
	
	String getDefaultTextValue(IWebFormField field) {
		return "foo";
	}
	
	String getDefaultPasswordValue(IWebFormField field) {
		return "foo";
	}
	
	String getDefaultMultipleValue(IWebFormField field) {
		if(!field.hasMultipleValues())
			return "foo";
		
		final String checkedValue = firstSetValue(field.getCheckedValues());
		if(checkedValue != null)
			return checkedValue;
		final String oneValue = firstSetValue(field.getMultipleValues());
		if(oneValue != null)
			return oneValue;
		return "foo";
	}
	
	private String firstSetValue(Set<String> stringSet) {
		if(stringSet.isEmpty())
			return null;
		return stringSet.toArray(new String[0])[0];
	}

}
