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

import java.util.HashSet;
import java.util.Set;

import com.subgraph.vega.api.model.web.forms.IWebFormField;

public class WebFormField implements IWebFormField {

	private final FieldType type;
	private final String name;
	private final String value;
	private final String id;
	private final Set<String> multipleValues;
	private final Set<String> checkedValues;
	private String label;
	
	WebFormField(FieldType type, String name, String value, String id) {
		this.type = type;
		this.name = name;
		this.value = value;
		this.id = id;
		if(type == FieldType.INPUT_CHECKBOX || type == FieldType.INPUT_RADIO || type == FieldType.SELECT_OPTION) {
			this.multipleValues = new HashSet<String>();
			this.checkedValues = new HashSet<String> ();
		}
		else {
			this.multipleValues = null;
			this.checkedValues = null;
		}
	}
	
	void addValue(String value, boolean checked) {
		if(multipleValues == null) 
			throw new IllegalStateException();
		multipleValues.add(value);
		if(checked)
			checkedValues.add(value);
	}
	
	void setLabel(String label) {
		this.label = label;
	}
	
	String getLabel() {
		return label;
	}

	@Override
	public FieldType getType() {
		return type;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}
	
	String getId() {
		return id;
	}

	@Override
	public boolean hasMultipleValues() {
		return multipleValues != null;
	}

	@Override
	public Set<String> getMultipleValues() {
		return multipleValues;
	}

	@Override
	public Set<String> getCheckedValues() {
		return checkedValues;
	}
}
