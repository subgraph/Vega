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

import java.net.URI;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.subgraph.vega.api.html.IHTMLParseResult;
import com.subgraph.vega.api.html.IHTMLParser;

public class HTMLParser implements IHTMLParser {

	@Override
	public IHTMLParseResult parseString(String html, URI baseURI) {
		String base = (baseURI == null) ? ("") : (baseURI.toString());
		Document jsoupDocument = Jsoup.parse(html, base);
		if(jsoupDocument == null)
			return null;
		else 
			return new HTMLParseResult(jsoupDocument);
	}
}
