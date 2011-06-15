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

import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class CharacterDataImpl extends NodeImpl implements CharacterData {

	private final String data;
	
	
	CharacterDataImpl(org.jsoup.nodes.Node jsoupNode, String data, Document ownerDocument) {
		super(jsoupNode, ownerDocument);
		this.data = data;
	}
	
	public String getNodeName() {
		return "#cdata-section";
	}
	
	@Override
	public String getNodeValue() {
		return data;
	}

	@Override
	public short getNodeType() {
		return Node.CDATA_SECTION_NODE;
	}
	
	@Override
	public String getData() throws DOMException {
		return data;
	}

	@Override
	public void setData(String data) throws DOMException {
		throw createReadOnlyException();		
	}

	@Override
	public int getLength() {
		return data.length();
	}

	@Override
	public String getTextContent() {
		return data;
	}
	
	@Override
	public String substringData(int offset, int count) throws DOMException {
		String data = getData();
		if(offset < 0 || offset >= data.length())
			throw new DOMException(DOMException.INDEX_SIZE_ERR, "Offset "+ offset +" is out of range");
		int end = offset + count;
		if(end < offset)
			end = offset;
		if(end > data.length())
			end = data.length();
		return data.substring(offset, end);
	}

	@Override
	public void appendData(String arg) throws DOMException {
		throw createReadOnlyException();		
	}

	@Override
	public void insertData(int offset, String arg) throws DOMException {
		throw createReadOnlyException();		
	}

	@Override
	public void deleteData(int offset, int count) throws DOMException {
		throw createReadOnlyException();		
	}

	@Override
	public void replaceData(int offset, int count, String arg)
			throws DOMException {
		throw createReadOnlyException();		
	}

}
