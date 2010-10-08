package com.subgraph.vega.impl.scanner.modules.scripting;

import java.io.File;
import java.io.FileFilter;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class PreludeLoader {
	private final File preludeDirectory;
	private final Scriptable globalScope;
	private final ScriptCompiler preludeCompiler;

	private final FileFilter scriptFilter = new FileFilter() {
		public boolean accept(File pathname) {
			return pathname.isFile() && pathname.getName().endsWith(".js");
		}
	};
	
	private Scriptable preludeScope;
	
	PreludeLoader(File directory, Scriptable scope) {
		this.preludeDirectory = directory;
		this.globalScope = scope;
		this.preludeCompiler = new ScriptCompiler(globalScope);
	}
	
	void load() {
		try {
			Context cx = Context.enter();
			Scriptable scope = preludeCompiler.newScope(cx);
			for(File ps: preludeDirectory.listFiles(scriptFilter)) {
				preludeCompiler.compileFile(ps, cx, scope);
			}
			preludeScope = scope;
		} finally {
			Context.exit();
		}
	}
	
	Scriptable getPreludeScope() {
		return preludeScope;
	}
}
