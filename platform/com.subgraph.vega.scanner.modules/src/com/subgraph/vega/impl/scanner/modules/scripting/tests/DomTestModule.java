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
package com.subgraph.vega.impl.scanner.modules.scripting.tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.osgi.framework.Bundle;

import com.subgraph.vega.api.html.IHTMLParseResult;
import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.impl.scanner.modules.scripting.AbstractScriptModule;
import com.subgraph.vega.impl.scanner.modules.scripting.ScriptedModule;

public class DomTestModule extends AbstractScriptModule {

	private final Bundle bundle;
	private final ScriptedModule testScript;
	private final IHTMLParser htmlParser;
	
	public DomTestModule(ScriptedModule module, Bundle bundle, IHTMLParser parser) {
		super(module);
		testScript = module;
		this.bundle = bundle;
		this.htmlParser = parser;
	}
	
	public void run(IWorkspace workspace) throws IOException {
		String html = lookupModuleString("html");
		IHTMLParseResult parseResult = loadHTML(html);
		final List<ExportedObject> exports = new ArrayList<ExportedObject>();
		export(exports, "testDom", parseResult.getDOMDocument());
		export(exports, "workspace", workspace);
		runScript(exports, "domtest");
		
	}

	private String lookupModuleString(String name) {
		Scriptable moduleObject = getModuleObject();
		Object ob = moduleObject.get(name, testScript.getModuleScope());
		if(ob == Scriptable.NOT_FOUND) {
			throw new IllegalArgumentException("Could not find "+ name);
		}
		if(!(ob instanceof String)) {
			throw new IllegalArgumentException("Property "+ name +" is not a string");
		}
		return (String) ob;
		
	}
	
	private Scriptable getModuleObject() {
		final Scriptable scope = testScript.getModuleScope();
		final Object ob = scope.get("module", scope);
		if(ob == Scriptable.NOT_FOUND) 
			throw new IllegalArgumentException();
		return Context.toObject(ob, scope);
	}
	
	IHTMLParseResult loadHTML(String name) throws IOException {
		URL url = bundle.getEntry("/tests/html/"+ name);
		if(url == null)
			throw new FileNotFoundException("Could not locate test html file /data/test/"+ name);
		final InputStream input = url.openStream();
		final String html = streamToString(input);
		return htmlParser.parseString(html, null);
	}
	
	String streamToString(InputStream input) throws IOException {
		Reader r = new InputStreamReader(input);
		StringWriter sw = new StringWriter();
		char[] buffer = new char[2048];
		
		while(true) {
			int n = r.read(buffer);
			if(n == -1) {
				return sw.toString();
			}
			sw.write(buffer, 0, n);
		}
	}

	@Override
	public boolean isDifferential() {
		return false;
	}

	@Override
	public boolean isTimeSensitive() {
		return false;
	}
}
