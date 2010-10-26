package com.subgraph.vega.impl.scanner.modules.scripting.dom;

import org.mozilla.javascript.Scriptable;
import org.w3c.dom.html2.HTMLOptionElement;

public class OptionJS extends HTMLElementJS {

	private static final long serialVersionUID = 1L;

	private final HTMLOptionElement optionElement;
	
	public OptionJS() {
		optionElement = null;
	}
	
	public OptionJS(HTMLOptionElement element, DocumentJS document) {
		super(element, document);
		this.optionElement = element;
	}
	
	@Override
	public void jsConstructor(Object ob) {
		
	}
	
	@Override 
	public String getClassName() {
		return "Option";
	}
	
	public boolean jsGet_defaultSelected() {
		return optionElement.getDefaultSelected();
	}
	
	public boolean jsGet_disabled() {
		return optionElement.getDisabled();
	}

	public Scriptable jsGet_form() {
		return exportNode(optionElement.getForm());
	}
	
	public int jsGet_index() {
		return optionElement.getIndex();
	}
	
	public String jsGet_label() {
		return optionElement.getLabel();
	}
	
	public boolean jsGet_selected() {
		return optionElement.getSelected();
	}
	
	public String jsGet_text() {
		return optionElement.getText();
	}
	
	public String jsGet_value() {
		return optionElement.getValue();
	}
}

