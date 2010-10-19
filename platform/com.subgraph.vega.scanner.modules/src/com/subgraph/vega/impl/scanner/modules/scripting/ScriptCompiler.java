package com.subgraph.vega.impl.scanner.modules.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

public class ScriptCompiler {
	
	private final Scriptable parentScope;
	
	public ScriptCompiler(Scriptable scope) {
		this.parentScope = scope;
	}
	
	
	public Scriptable compileFile(File f) {
		try {
			final Context cx = Context.enter();
			final Scriptable scope = newScope(cx);
			return compileFile(f, cx, scope);
		} finally {
			Context.exit();
		}
		
	}
	public Scriptable compileFile(File f, Context cx, Scriptable scriptScope) {
		try {
			final Reader r = new FileReader(f);
			return compileReader(r, f.getCanonicalPath(), cx, scriptScope);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
	}
	
	public Scriptable compileReader(Reader r, String path) {
		try {
			final Context cx = Context.enter();
			final Scriptable scope = newScope(cx);
			return compileReader(r, path, cx, scope);
		} finally {
			Context.exit();
		}
	}
	
	public Scriptable compileReader(Reader r, String path, Context cx, Scriptable scriptScope) {
		try {
			final Script script = cx.compileReader(r, path, 1, null);			
			script.exec(cx, scriptScope);
			return scriptScope;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
	}
	
	public Scriptable newScope(Context cx) {
		final Scriptable scope = cx.newObject(parentScope);
		scope.setPrototype(parentScope);
		scope.setParentScope(null);
		return scope;
	}

}
