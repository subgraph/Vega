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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.subgraph.vega.api.model.web.forms.IWebForm;
import com.subgraph.vega.api.model.web.forms.IWebFormField;
import com.subgraph.vega.api.model.web.forms.IWebFormField.FieldType;

public class WebForm implements IWebForm {
	private final static Logger logger = Logger.getLogger("forms");
	private final URI action;
	private final FormMethod method;
	private final EncType encoding;
	
	private final Map<String, WebFormField> fields;

	WebForm(URI action, FormMethod method, EncType encoding) {
		this.action = action;
		this.method = method;
		this.encoding = encoding;
		this.fields = new HashMap<String, WebFormField>();
	}	

	@Override
	public EncType getEncodingType() {
		return encoding;
	}

	@Override
	public FormMethod getMethod() {
		return method;
	}	
	
	@Override
	public URI getAction() {
		return action;
	}

	@Override
	public Collection<IWebFormField> getFields() {
		return new ArrayList<IWebFormField>(fields.values());
	}
	
	void addTextField(String name, String value, String id, String label) {
		System.out.println("Text field name = "+ name + " value = "+ value + " label = "+ label);
		final WebFormField field = new WebFormField(FieldType.INPUT_TEXT, name, value, id);
		if(label != null && label.length() > 0)
			field.setLabel(label);
		fields.put(name, field);
	}
	
	void addPasswordField(String name, String value, String id, String label) {
		System.out.println("Password field name = "+ name + " value = "+ value + " label = "+ label);
		final WebFormField field = new WebFormField(FieldType.INPUT_PASSWORD, name, value, id);
		if(label != null && label.length() > 0)
			field.setLabel(label);
		fields.put(name, field);
	}
	
	void addCheckboxField(String name, String value, String id, boolean checked) {
		if(!fields.containsKey(name)) 
			fields.put(name, new WebFormField(FieldType.INPUT_CHECKBOX, name, null, id));
		final WebFormField f = fields.get(name);
		if(f.getType() != FieldType.INPUT_CHECKBOX) {
			logger.warning("Cannot add checkbox field because field name "+ name +" is duplicated");
			return;
		}
		f.addValue(value, checked);		
	}
	
	void addRadioField(String name, String value, String id, boolean checked) {
		if(!fields.containsKey(name))
			fields.put(name, new WebFormField(FieldType.INPUT_RADIO, name, null, id));
		final WebFormField f = fields.get(name);
		if(f.getType() != FieldType.INPUT_RADIO) {
			logger.warning("Cannot add radio field because field name "+ name + " is duplicated");
			return;
		}
		f.addValue(value, checked);	
	}
	
	void addHiddenField(String name, String value, String id) {
		fields.put(name, new WebFormField(FieldType.INPUT_HIDDEN, name, value, id));
	}
	
	void addFileField(String name) {
		fields.put(name, new WebFormField(FieldType.INPUT_FILE, name, null, null));
	}
	
	void addSelectOption(String name, String value, boolean multiple) {
		
	}
	
	void addTextArea(String name, String value) {
		fields.put(name, new WebFormField(FieldType.TEXTAREA, name, value, null));
	}
	
	void addLabelToField(String fieldId, String label) {
		for(WebFormField field: fields.values()) {
			if(field.getId() != null && field.getId().equalsIgnoreCase(fieldId)) {
				field.setLabel(label);
				return;
			}
		}
		logger.warning("Could not find corresponding field element with id = "+ fieldId + " for <LABEL> content "+ label);
	}
}
