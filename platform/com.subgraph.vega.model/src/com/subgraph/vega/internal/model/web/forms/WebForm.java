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
	
	void addTextField(String name, String value) {
		fields.put(name, new WebFormField(FieldType.INPUT_TEXT, name, value));
	}
	
	void addPasswordField(String name, String value) {
		fields.put(name, new WebFormField(FieldType.INPUT_PASSWORD, name, value));
	}
	
	void addCheckboxField(String name, String value, boolean checked) {
		if(!fields.containsKey(name)) 
			fields.put(name, new WebFormField(FieldType.INPUT_CHECKBOX, name, null));
		final WebFormField f = fields.get(name);
		if(f.getType() != FieldType.INPUT_CHECKBOX) {
			logger.warning("Cannot add checkbox field because field name "+ name +" is duplicated");
			return;
		}
		f.addValue(value, checked);		
	}
	
	void addRadioField(String name, String value, boolean checked) {
		if(!fields.containsKey(name))
			fields.put(name, new WebFormField(FieldType.INPUT_RADIO, name, null));
		final WebFormField f = fields.get(name);
		if(f.getType() != FieldType.INPUT_RADIO) {
			logger.warning("Cannot add radio field because field name "+ name + " is duplicated");
			return;
		}
		f.addValue(value, checked);	
	}
	
	void addHiddenField(String name, String value) {
		fields.put(name, new WebFormField(FieldType.INPUT_HIDDEN, name, value));
	}
	
	void addFileField(String name) {
		fields.put(name, new WebFormField(FieldType.INPUT_FILE, name, null));
	}
	
	void addSelectOption(String name, String value, boolean multiple) {
		
	}
	
	void addTextArea(String name, String value) {
		fields.put(name, new WebFormField(FieldType.TEXTAREA, name, value));
	}



}
