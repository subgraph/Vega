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
package com.subgraph.vega.internal.html;

import org.jsoup.nodes.Document;
import org.w3c.dom.html2.HTMLDocument;

import com.subgraph.vega.api.html.IHTMLParseResult;
import com.subgraph.vega.internal.html.dom.HTMLDocumentImpl;

public class HTMLParseResult implements IHTMLParseResult {
	
	private final Document jsoupDocument;
	private HTMLDocument cachedDomDocument;
	
	HTMLParseResult(Document jsoupDocument) {
		this.jsoupDocument = jsoupDocument;
	}

	@Override
	public Document getJsoupDocument() {
		return jsoupDocument;		
	}

	@Override
	public HTMLDocument getDOMDocument() {
		synchronized(jsoupDocument) {
			if(cachedDomDocument == null)
				cachedDomDocument = HTMLDocumentImpl.createFromJsoup(jsoupDocument);
			return cachedDomDocument;
		}
	}
}
