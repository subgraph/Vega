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
package com.subgraph.vega.impl.scanner.forms;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class FormHints {
	private final static String DEFAULT_VALUE = "1";
	private final List<NameValuePair> hintList = new ArrayList<NameValuePair>();

	FormHints() {
		add("phone", "2129824052");
		add("zip", "10003");
		add("first", "Joey");
		add("last", "Ramone");
		add("name", "Joey");
		add("mail", "vega@example.com");
		add("street", "315 Bowery");
		add("city", "New York");
		add("state", "NY");
		add("country", "US");
		add("language", "en");
		add("company", "subgraph");
		add("search", "vega");
		add("login", "vega");
		add("user", "vega");
		add("nick", "vega");
		add("pass", "vega");
		add("pwd", "vega");
		add("year", "2012");
		add("card", "4111111111111111");
		add("code", "000");
		add("cvv", "000");
		add("expir", "1212");
		add("ssn", "987654320");
		add("url", "http://example.com/?vega_form_test");
		add("site", "http://example.com/?vega_form_test");
		add("domain", "example.com");
		add("search", "a");
		add("comment", "vega");
		add("desc", "vega");
		add("title", "vega");
		add("subject", "vega");
		add("message", "vega");
	}

	private void add(String k, String v) {
		hintList.add(new BasicNameValuePair(k, v));
	}

	public String lookupHint(String fieldName) {
		if(fieldName == null || fieldName.isEmpty())
			return DEFAULT_VALUE;
		final String name = fieldName.toLowerCase();
		for(NameValuePair p: hintList) {
			if(name.contains(p.getName()))
				return p.getValue();
		}
		return DEFAULT_VALUE;
	}

}
