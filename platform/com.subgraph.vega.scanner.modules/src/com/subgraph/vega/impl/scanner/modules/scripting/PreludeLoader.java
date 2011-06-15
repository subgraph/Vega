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
package com.subgraph.vega.impl.scanner.modules.scripting;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

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

public class PreludeLoader {
	private final Logger logger = Logger.getLogger("prelude-loader");
	private final File preludeDirectory;
	private final ScriptCompiler preludeCompiler;

	private final FileFilter scriptFilter = new FileFilter() {
		public boolean accept(File pathname) {
			return pathname.isFile() && pathname.getName().endsWith(".js");
		}
	};
	
	private Scriptable preludeScope;
	
	PreludeLoader(File directory, Scriptable scope) {
		this.preludeDirectory = directory;
		this.preludeCompiler = new ScriptCompiler(scope);
	}
	
	boolean load() {
		try {
			Context cx = Context.enter();
			Scriptable scope = preludeCompiler.newScope(cx);
			for(File ps: preludeDirectory.listFiles(scriptFilter)) {
				ScriptFile scriptFile = new ScriptFile(ps);
				if(!preludeCompiler.compile(scriptFile, cx, scope)) {
					logger.warning("Failed to load module prelude");
					logger.warning(scriptFile.getCompileFailureMessage());
					return false;
				}
			}
			
			defineHostObject(scope, NodeJS.class);
			defineHostObject(scope, DocumentJS.class);
			defineHostObject(scope, ElementJS.class);
			defineHostObject(scope, AttrJS.class);
			defineHostObject(scope, CharacterDataJS.class);
			defineHostObject(scope, TextJS.class);
			defineHostObject(scope, AnchorJS.class);
			defineHostObject(scope, FormJS.class);
			defineHostObject(scope, CommentJS.class);
			defineHostObject(scope, HTMLCollectionJS.class);
			defineHostObject(scope, HTMLDocumentJS.class);
			defineHostObject(scope, InputJS.class);
			defineHostObject(scope, LinkJS.class);
			defineHostObject(scope, OptionJS.class);
			defineHostObject(scope, SelectJS.class);
			defineHostObject(scope, NodeListJS.class);
			defineHostObject(scope, ResponseJS.class);
			preludeScope = scope;
			return true;
			
		} catch (Exception e) {
			logger.log(Level.WARNING, "Unexpected exception loading prelude: "+ e);
			return false;
		} finally {
			Context.exit();
		}
	}
	
	private void defineHostObject(Scriptable scope, Class<? extends ScriptableObject> klass) {
		try {
			ScriptableObject.defineClass(scope, klass, true, true);
		} catch (IllegalAccessException e) {
			logger.warning("IllegalAccessException defining class '"+ klass.getName() + "' : "+ e.getMessage());
		} catch (InstantiationException e) {
			logger.warning("InstantiationException defining class '"+ klass.getName() + "' : "+ e.getMessage());
		} catch (InvocationTargetException e) {
			logger.warning("InvocationTargetException defining class '"+ klass.getName() + "' : "+ e.getMessage());
		}		
	}
	
	Scriptable getPreludeScope() {
		return preludeScope;
	}
}
