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
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.logging.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.osgi.framework.Bundle;

import com.subgraph.vega.impl.scanner.modules.scripting.RhinoExceptionFormatter;
import com.subgraph.vega.impl.scanner.modules.scripting.ScriptCompiler;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.AnchorJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.AttrJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.CharacterDataJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.CommentJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.DocumentJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.ElementJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.FormJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.HTMLCollectionJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.HTMLDocumentJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.InputJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.LinkJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.NodeJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.NodeListJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.OptionJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.SelectJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.TextJS;

public class QunitLoader {
	private final static String QUNIT_PATH = "/tests/qunit/qunit.js";
	private final Logger logger = Logger.getLogger("qunit-loader");
	private final ScriptCompiler qunitCompiler;
 	private final Bundle bundle;
	
	private Scriptable qunitScope;
	
	QunitLoader(Scriptable globalScope, Bundle bundle) {
		this.qunitCompiler = new ScriptCompiler(globalScope);
		this.bundle = bundle;
	}
	
	void load() {
		try {
			Context cx = Context.enter();
			Scriptable scope = qunitCompiler.newScope(cx);
			qunitCompiler.compileReader(openQunit(QUNIT_PATH), QUNIT_PATH, cx, scope);
			ScriptableObject.defineClass(scope, NodeJS.class, true, true);
			ScriptableObject.defineClass(scope, DocumentJS.class, true, true);
			ScriptableObject.defineClass(scope, ElementJS.class, true, true);
			ScriptableObject.defineClass(scope, AttrJS.class, true, true);
			ScriptableObject.defineClass(scope, CharacterDataJS.class, true, true);
			ScriptableObject.defineClass(scope, TextJS.class, true, true);
			ScriptableObject.defineClass(scope, AnchorJS.class, true, true);
			ScriptableObject.defineClass(scope, FormJS.class, true, true);
			ScriptableObject.defineClass(scope, CommentJS.class, true, true);
			ScriptableObject.defineClass(scope, HTMLCollectionJS.class, true, true);
			ScriptableObject.defineClass(scope, HTMLDocumentJS.class, true, true);
			ScriptableObject.defineClass(scope, InputJS.class, true, true);
			ScriptableObject.defineClass(scope, LinkJS.class, true, true);
			ScriptableObject.defineClass(scope, OptionJS.class, true, true);
			ScriptableObject.defineClass(scope, SelectJS.class, true, true);
			ScriptableObject.defineClass(scope, NodeListJS.class, true, true);
			qunitScope = scope;			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(RhinoException e) {
			logger.warning(new RhinoExceptionFormatter("Failed to compile module prelude script.", e).toString());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Context.exit();
		}
		
	}

	private Reader openQunit(String path) throws IOException {
		final URL url = bundle.getEntry(path);
		if(url == null)
			throw new FileNotFoundException("Could not locate qunit library at "+ path);
		final InputStream input = url.openStream();
		return new InputStreamReader(input);
	}
	
	Scriptable getScope() {
		return qunitScope;
	}
}
