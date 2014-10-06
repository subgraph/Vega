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
package com.subgraph.vega.internal.html.dom;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Attribute;
import org.jsoup.select.Elements;
import org.jsoup.nodes.FormElement;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;


public class ElementImpl extends NodeImpl implements Element {
	protected final org.jsoup.nodes.Element jsoupElement;
	
	protected ElementImpl(org.jsoup.nodes.Element jsoupElement, Document ownerDocument) {
		super(jsoupElement, ownerDocument);
		this.jsoupElement = jsoupElement;
	}
	
	public String getNodeName() {
		return getTagName();
	}
	
	@Override
	public short getNodeType() {
		return Node.ELEMENT_NODE;
	}
	
	org.jsoup.nodes.Element getJsoupElement() {
		return jsoupElement;
	}
	
	@Override
	public String getTagName() {
		return jsoupElement.tagName().toUpperCase();
	}

	@Override
	public String getTextContent() {
		final short type = getNodeType();
		switch(type) {
		case Node.TEXT_NODE:
		case Node.CDATA_SECTION_NODE:
		case Node.COMMENT_NODE:
			return getNodeValue();
		default:
			return concatenateChildTextContent();
		}
	}
	
	private String concatenateChildTextContent() {
		final StringBuilder sb = new StringBuilder();
		final NodeList nlist = getChildNodes();
		for(int i = 0; i < nlist.getLength(); i++) {
			Node node = nlist.item(i);
			if((node.getNodeType() != Node.COMMENT_NODE) && (node.getNodeType() != Node.PROCESSING_INSTRUCTION_NODE)) {
				String text = node.getTextContent();
				if(text != null)
					sb.append(text);
			}
		}
		return sb.toString();
	}
	
	Element getElementById(String elementId) {
		org.jsoup.nodes.Element elementById = jsoupElement.getElementById(elementId);
		if(elementById == null)
			return null;
		else
			return HTMLElementImpl.create(elementById, getOwnerDocument());
	}
	
	@Override
	public NamedNodeMap getAttributes() {
		NamedNodeMapImpl map = new NamedNodeMapImpl();
		for(Attribute a: jsoupElement.attributes()) 
			map.addNode(a.getKey(), new AttrImpl(a.getKey(), a.getValue(), this, getOwnerDocument()));
		return map;
	}
	
	@Override
	public boolean hasAttributes() {
		return jsoupElement.attributes().size() > 0;
	}
	
	@Override
	public String getAttribute(String name) {
		if(jsoupElement.hasAttr(name))
			return jsoupElement.attr(name);
		else if(getTagName().equals("LABEL") && name.equals("htmlFor") && jsoupElement.hasAttr("for")) 
				return jsoupElement.attr("for");
		else if(name.equals("className") && jsoupElement.hasAttr("class"))
			return jsoupElement.attr("class");
		else
			return null;				
	}

	@Override
	public void setAttribute(String name, String value) throws DOMException {
		throw createReadOnlyException();		
	}

	@Override
	public void removeAttribute(String name) throws DOMException {
		throw createReadOnlyException();		
	}

	@Override
	public Attr getAttributeNode(String name) {
		if(jsoupElement.hasAttr(name)) {
			return new AttrImpl(name, getAttribute(name), this, getOwnerDocument());
		} else {
			return null;
		}
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		throw createReadOnlyException();
	}

	@Override
	public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
		throw createReadOnlyException();
	}

	@Override
	public NodeList getElementsByTagName(String name) {
		final List<NodeImpl> elements = new ArrayList<NodeImpl>();
		
		// Special case
		if(getTagName().equals("HTML") && name.equalsIgnoreCase("html")) {
			elements.add(this);
			return new NodeListImpl(elements);
		}

		// Special case #2 - to handle jsoup.org.FormElement, which has saved nested elements 
		// see:             	return new NodeListImpl(elements);

		if(getTagName().equals("FORM") && "*".equals(name)) {
            Elements fe;
            fe = ((FormElement) this.jsoupElement).elements();
            for (org.jsoup.nodes.Element f : fe)
            {
            	elements.add(HTMLElementImpl.create(f, getOwnerDocument()));
            }
        	return new NodeListImpl(elements);

		}
		for(org.jsoup.nodes.Element e: jsoupElementsForTag(name)) {
			if(e != jsoupElement)
				elements.add(HTMLElementImpl.create(e, getOwnerDocument()));
		}
		return new NodeListImpl(elements);
	}

	private Elements jsoupElementsForTag(String name) {
		if("*".equals(name)) {
			return jsoupElement.getAllElements();
		}
		else
			return jsoupElement.getElementsByTag(name);
	}
	
	@Override
	public String getAttributeNS(String namespaceURI, String localName)
			throws DOMException {
		throw createNoXMLSupportException();
	}

	@Override
	public void setAttributeNS(String namespaceURI, String qualifiedName,
			String value) throws DOMException {
		throw createNoXMLSupportException();		
	}

	@Override
	public void removeAttributeNS(String namespaceURI, String localName)
			throws DOMException {
		throw createNoXMLSupportException();		
	}

	@Override
	public Attr getAttributeNodeNS(String namespaceURI, String localName)
			throws DOMException {
		throw createNoXMLSupportException();
	}

	@Override
	public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
		throw createNoXMLSupportException();
	}

	@Override
	public NodeList getElementsByTagNameNS(String namespaceURI, String localName)
			throws DOMException {
		throw createNoXMLSupportException();
	}

	@Override
	public boolean hasAttribute(String name) {
		return jsoupElement.hasAttr(name);
	}

	@Override
	public boolean hasAttributeNS(String namespaceURI, String localName)
			throws DOMException {
		throw createNoXMLSupportException();
	}

	@Override
	public TypeInfo getSchemaTypeInfo() {
		throw createNoLevel3SupportException();
	}

	@Override
	public void setIdAttribute(String name, boolean isId) throws DOMException {
		throw createNoLevel3SupportException();
	}

	@Override
	public void setIdAttributeNS(String namespaceURI, String localName,
			boolean isId) throws DOMException {
		throw createNoLevel3SupportException();
	}

	@Override
	public void setIdAttributeNode(Attr idAttr, boolean isId)
			throws DOMException {
		throw createNoLevel3SupportException();		
	}
}
