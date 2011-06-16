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

import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;

public class CharacterDataJS extends NodeJS {
	
	private static final long serialVersionUID = 1L;
	private final CharacterData characterData;
	
	public CharacterDataJS() {
		this.characterData = null;
	}
	
	public CharacterDataJS(CharacterData data, DocumentJS document) {
		super(data, document);
		this.characterData = data;
	}
	
	@Override
	public void jsConstructor(Object ob) {
	}
	
	@Override
	public String getClassName() {
		return "CharacterData";
	}
	
	public String jsGet_data() {
		return characterData.getData();
	}
	
	public int jsGet_length() {
		return characterData.getLength();
	}
	
	public void jsFunction_appendData(String arg) throws DOMException {
		characterData.appendData(arg);
	}
	
	public void jsFunction_deleteData(int offset, int count) throws DOMException {
		characterData.deleteData(offset, count);
	}
	
	public void jsFunction_insertData(int offset, String arg) throws DOMException {
		characterData.insertData(offset, arg);
	}
	
	public void jsFunction_replaceData(int offset, int length, String arg) throws DOMException {
		characterData.replaceData(offset, length, arg);
	}
	
	public String jsFunction_substringData(int offset, int count) throws DOMException {
		return characterData.substringData(offset, count);
	}
}
