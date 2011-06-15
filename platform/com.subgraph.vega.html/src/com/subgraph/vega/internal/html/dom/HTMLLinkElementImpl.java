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

import org.jsoup.nodes.Element;
import org.w3c.dom.Document;
import org.w3c.dom.html2.HTMLLinkElement;

public class HTMLLinkElementImpl extends HTMLElementImpl implements HTMLLinkElement {

	HTMLLinkElementImpl(Element jsoupElement, Document ownerDocument) {
		super(jsoupElement, ownerDocument);
	}

	@Override
	public boolean getDisabled() {
		return false;
	}

	@Override
	public void setDisabled(boolean disabled) {
	}

	@Override
	public String getCharset() {
		return getAttribute("charset");
	}

	@Override
	public void setCharset(String charset) {		
	}

	@Override
	public String getHref() {
		return getAttribute("href");
	}

	@Override
	public void setHref(String href) {		
	}

	@Override
	public String getHreflang() {
		return getAttribute("hreflang");
	}

	@Override
	public void setHreflang(String hreflang) {
	}

	@Override
	public String getMedia() {
		return getAttribute("media");
	}

	@Override
	public void setMedia(String media) {		
	}

	@Override
	public String getRel() {
		return getAttribute("rel");
	}

	@Override
	public void setRel(String rel) {		
	}

	@Override
	public String getRev() {
		return getAttribute("rev");
	}

	@Override
	public void setRev(String rev) {		
	}

	@Override
	public String getTarget() {
		return getAttribute("target");
	}

	@Override
	public void setTarget(String target) {		
	}

	@Override
	public String getType() {
		return getAttribute("type");
	}

	@Override
	public void setType(String type) {		
	}
}
