package com.subgraph.vega.internal.html.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLOptionElement;
import org.w3c.dom.html2.HTMLOptionsCollection;

public class HTMLOptionsCollectionImpl implements HTMLOptionsCollection {

	private List<HTMLOptionElementImpl> byIndex = new ArrayList<HTMLOptionElementImpl>();
	private Map<String, HTMLOptionElement> byId = new HashMap<String, HTMLOptionElement>();
	private Map<String, HTMLOptionElement> byName = new HashMap<String, HTMLOptionElement>();
	
	
	HTMLOptionsCollectionImpl(List<Element> jsoupElements, HTMLFormElement form, Document document) {
		for(Element e: jsoupElements) {
			int index = byIndex.size();
			HTMLOptionElementImpl htmlElement = new HTMLOptionElementImpl(e, form, index, document);
			addElement(htmlElement);
		}
	}
	
	private void addElement(HTMLOptionElementImpl element) {
		byIndex.add(element);
		final String id = element.getAttribute("id");
		final String name = element.getAttribute("name");
		if(id != null)
			byId.put(id, element);
		if(name != null)
			byName.put(name, element);
	}
	
	
	@Override
	public int getLength() {
		return byIndex.size();
	}

	@Override
	public void setLength(int length) throws DOMException {
		
	}

	@Override
	public HTMLOptionElementImpl item(int index) {
		if(index >= 0 && index < byIndex.size())
			return byIndex.get(index);
		else
			return null;
	}

	@Override
	public HTMLOptionElement namedItem(String name) {
		if(byId.containsKey(name))
			return byId.get(name);
		else
			return byName.get(name);
	}
	
	int getSelectedIndex() {
		for(HTMLOptionElement option: byIndex) {
			if(option.getSelected())
				return option.getIndex();
		}
		return -1;
	}
	
	String getValue() {
		for(HTMLOptionElement option: byIndex) {
			if(option.getSelected())
				return option.getValue();
		}
		return null;
	}

}
