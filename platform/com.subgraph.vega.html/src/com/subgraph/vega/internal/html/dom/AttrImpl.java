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

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;


public class AttrImpl extends NodeImpl implements Attr {

	private final String name;
	private final String value;
	private final ElementImpl ownerElement;
	
	AttrImpl(String name, String value, ElementImpl owner, Document ownerDocument) {
		super(null, ownerDocument);
		this.name = name;
		this.value = value;
		this.ownerElement = owner;
	}
	
	public String getNodeName() {
		return name;
	}
	
	@Override
	public String getNodeValue() {
		return value;
	}

	@Override
	public short getNodeType() {
		return Node.ATTRIBUTE_NODE;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean getSpecified() {
		return true;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) throws DOMException {
		throw createReadOnlyException();	
	}

	@Override
	public Element getOwnerElement() {
		return ownerElement;
	}

	@Override
	public TypeInfo getSchemaTypeInfo() {
		throw createNoLevel3SupportException();
	}

	@Override
	public boolean isId() {
		throw createNoLevel3SupportException();
	}
	
	private String nulltoEmpty(String s) {
		return (s == null) ? "" : s;
	}
	public boolean equals(Object other) {
		if(!(other instanceof AttrImpl)) 
			return false;
		AttrImpl that = (AttrImpl) other;
		return nulltoEmpty(name).equals(nulltoEmpty(that.name)) && nulltoEmpty(value).equals(nulltoEmpty(that.value));
	}
	
	public int hashCode() {
		return nulltoEmpty(name).hashCode() * 41 + nulltoEmpty(value).hashCode();
	}
	
}
