package com.subgraph.vega.internal.model.web.forms;

import java.util.HashSet;
import java.util.Set;

import com.subgraph.vega.api.model.web.forms.IWebFormField;

public class WebFormField implements IWebFormField {

	private final FieldType type;
	private final String name;
	private final String value;
	private final Set<String> multipleValues;
	private final Set<String> checkedValues;
	
	WebFormField(FieldType type, String name, String value) {
		this.type = type;
		this.name = name;
		this.value = value;
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

	@Override
	public boolean hasMultipleValues() {
		return multipleValues != null;
	}

	@Override
	public Set<String> getMultipleValues() {
		return multipleValues;
	}
}
