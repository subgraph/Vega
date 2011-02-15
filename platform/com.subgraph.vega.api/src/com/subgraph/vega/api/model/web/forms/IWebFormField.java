package com.subgraph.vega.api.model.web.forms;

import java.util.Set;


public interface IWebFormField {
	public enum FieldType { INPUT_TEXT, INPUT_PASSWORD, INPUT_CHECKBOX, INPUT_RADIO, INPUT_HIDDEN, INPUT_FILE, TEXTAREA, SELECT_OPTION };
	
	FieldType getType();
	String getName();
	String getValue();
	boolean hasMultipleValues();
	Set<String> getMultipleValues();

}
