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

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

public class DomImplementationImpl implements DOMImplementation {

	@Override
	public boolean hasFeature(String feature, String version) {
		return false;
	}

	@Override
	public DocumentType createDocumentType(String qualifiedName,
			String publicId, String systemId) throws DOMException {
		throw NodeImpl.createNoXMLSupportException();
	}

	@Override
	public Document createDocument(String namespaceURI, String qualifiedName,
			DocumentType doctype) throws DOMException {
		throw NodeImpl.createNoXMLSupportException();
	}

	@Override
	public Object getFeature(String feature, String version) {
		throw NodeImpl.createNoLevel3SupportException();
	}

}
