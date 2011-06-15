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
import org.w3c.dom.html2.HTMLAnchorElement;

public class HTMLAnchorElementImpl extends HTMLElementImpl implements HTMLAnchorElement {

	HTMLAnchorElementImpl(Element jsoupElement, Document ownerDocument) {
		super(jsoupElement, ownerDocument);
	}

	@Override
	public String getAccessKey() {
		return getAttribute("accesskey");
	}

	@Override
	public void setAccessKey(String accessKey) {		
	}

	@Override
	public String getCharset() {
		return getAttribute("charset");
	}

	@Override
	public void setCharset(String charset) {		
	}

	@Override
	public String getCoords() {
		return getAttribute("coords");
	}

	@Override
	public void setCoords(String coords) {		
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
	public String getName() {
		return getAttribute("name");
	}

	@Override
	public void setName(String name) {		
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
	public String getShape() {
		return getAttribute("shape");
	}

	@Override
	public void setShape(String shape) {		
	}

	@Override
	public int getTabIndex() {
		return getIntAttribute("tabindex");
	}

	@Override
	public void setTabIndex(int tabIndex) {		
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

	@Override
	public void blur() {
	}

	@Override
	public void focus() {		
	}
}
