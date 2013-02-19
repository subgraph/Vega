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
package com.subgraph.vega.impl.scanner.modules.scripting.dom;

import org.mozilla.javascript.Scriptable;
import org.w3c.dom.html2.HTMLAnchorElement;
import org.w3c.dom.html2.HTMLElement;
import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLInputElement;
import org.w3c.dom.html2.HTMLLinkElement;
import org.w3c.dom.html2.HTMLOptionElement;
import org.w3c.dom.html2.HTMLSelectElement;

import com.subgraph.vega.api.html.IInnerHtmlProvidingElement;

public class HTMLElementJS extends ElementJS {

	static HTMLElementJS domHTMLElementToJS(HTMLElement element, DocumentJS document) {
		if(element instanceof HTMLAnchorElement) {
			return new AnchorJS((HTMLAnchorElement) element, document);
		} else if(element instanceof HTMLFormElement) {
			return new FormJS((HTMLFormElement) element, document);
		} else if(element instanceof HTMLInputElement) {
			return new InputJS((HTMLInputElement) element, document);
		} else if(element instanceof HTMLLinkElement) {
			return new LinkJS((HTMLLinkElement) element);
		} else if(element instanceof HTMLOptionElement) {
			return new OptionJS((HTMLOptionElement) element, document);
		} else if(element instanceof HTMLSelectElement) {
			return new SelectJS((HTMLSelectElement) element, document);
		} else {
			return new HTMLElementJS(element, document);
		}
	}
	
	private static final long serialVersionUID = 1L;
	private final HTMLElement htmlElement;
	
	public HTMLElementJS() {
		this.htmlElement = null;
	}
	
	public HTMLElementJS(HTMLElement element, DocumentJS document) {
		super(element, document);
		this.htmlElement = element;
	}
	
	@Override
	public void jsConstructor(Object ob) {
	}
	
	@Override
	public String getClassName() {
		return "HTMLElement";
	}
	
	public String jsGet_className() {
		return htmlElement.getClassName();
	}
	
	public Scriptable jsGet_currentStyle() {
		return null;
	}
	
	public String jsGet_dir() {
		return htmlElement.getDir();
	}
	
	public String jsGet_id() {
		return htmlElement.getId();
	}
	
	public String jsGet_innerHTML() {
		if(htmlElement instanceof IInnerHtmlProvidingElement) {
			return ((IInnerHtmlProvidingElement) htmlElement).getInnerHtml();
		} else {
			return "";
		}
	}
	
	public String jsGet_lang() {
		return htmlElement.getLang();
	}
	
	public int jsGet_offsetHeight() {
		return 0;
	}
	
	public int jsGet_offsetWidth() {
		return 0;
	}
	
	public int jsGet_offsetLeft() {
		return 0;
	}
	
	public int jsGet_offsetTop() {
		return 0;
	}
	
	public Scriptable jsGet_offsetParent() {
		return null;
	}
	
	public int jsGet_scrollHeight() {
		return 0;
	}
	
	public int jsGet_scrollWidth() {
		return 0;
	}
	
	public int jsGet_scrollLeft() {
		return 0;
	}
	
	public int jsGet_scrollTop() {
		return 0;
	}
	
	public Scriptable jsGet_style() {
		return null;
	}
	
	public String jsGet_title() {
		return htmlElement.getTitle();
	}
	
	public Scriptable jsGet_onclick() {
		return null;
	}
	
	public Scriptable jsGet_ondblclick() {
		return null;
	}

	public Scriptable jsGet_onkeydown() {
		return null;
	}

	public Scriptable jsGet_onkeypress() {
		return null;
	}

	public Scriptable jsGet_onkeyup() {
		return null;
	}

	public Scriptable jsGet_onmousedown() {
		return null;
	}

	public Scriptable jsGet_onmousemove() {
		return null;
	}

	public Scriptable jsGet_onmouseout() {
		return null;
	}

	public Scriptable jsGet_onmouseover() {
		return null;
	}
	
	public Scriptable jsGet_onmouseup() {
		return null;
	}
	
	public void jsFunction_scrollIntoView(boolean top) {
		
	}

}
