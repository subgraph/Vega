package com.subgraph.vega.impl.scanner.modules.scripting.dom;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.html2.HTMLFormElement;

public class FormJS extends HTMLElementJS {

	private static final long serialVersionUID = 1L;
	
	private final HTMLFormElement formElement;
	
	public FormJS() {
		this.formElement = null;
	}
	
	public FormJS(HTMLFormElement element) {
		super(element);
		this.formElement = element;
	}
	
	@Override
	public void jsConstructor(Object ob) {
		
	}
	
	public Scriptable jsGet_elements() {
		final HTMLCollectionJS collection = new HTMLCollectionJS(formElement.getElements(), ScriptableObject.getTopLevelScope(this));
		exportObject(collection);
		return collection;
	}
	
	public int jsGet_length() {
		return formElement.getLength();
	}
	
	public String jsGet_acceptCharset() {
		return formElement.getAcceptCharset();
	}
	
	public String jsGet_action() {
		return formElement.getAction();
	}
	
	public String jsGet_enctype() {
		return formElement.getEnctype();
	}
	
	public String jsGet_method() {
		return formElement.getMethod();
	}
	
	public String jsGet_name() {
		return formElement.getName();
	}
	
	public String jsGet_target() {
		return formElement.getTarget();
	}
	
	public void jsFunction_reset() {
		formElement.reset();
	}
	
	public void jsFunction_submit() {
		formElement.submit();
	}
	
	public Scriptable jsGet_onsubmit() {
		return null;
	}
	
	public Scriptable jsGet_onreset() {
		return null;
	}
}
