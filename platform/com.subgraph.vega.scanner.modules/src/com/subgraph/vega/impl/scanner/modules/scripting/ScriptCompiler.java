package com.subgraph.vega.impl.scanner.modules.scripting;

import java.io.IOException;
import java.io.Reader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

public class ScriptCompiler {
	
	private final Scriptable parentScope;
	
	public ScriptCompiler(Scriptable scope) {
		this.parentScope = scope;
	}
	
	public boolean compile(ScriptFile scriptFile) {
		try {
			final Context cx = Context.enter();
			final Scriptable scope = newScope(cx);
			return compile(scriptFile, cx, scope);
		} finally {
			Context.exit();
		}
	}

	public boolean compile(ScriptFile scriptFile, Context cx, Scriptable scope) {
		try {
			final Scriptable compiledScript =  compileReader(scriptFile.createReader(), scriptFile.getPath(), cx, scope);
			if(compiledScript != null)  {
				scriptFile.setCompiledScript(compiledScript);
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			scriptFile.setCompileFailed("I/O error compiling script "+ scriptFile.getPath() + ": "+ e.getMessage());
			return false;
		} catch (RhinoException e) {
			final String msg = "Failed to compile script "+ scriptFile.getPath();
			scriptFile.setCompileFailed(new RhinoExceptionFormatter(msg, e).toString());
			return false;
		} catch (Exception e) {
			scriptFile.setCompileFailed("Unexpected exception compiling script: "+ e);
			return false;
		}finally {
			Context.exit();
		}
	}
		
	public Scriptable compileReader(Reader r, String path, Context cx, Scriptable scriptScope) throws IOException {
		final Script script = cx.compileReader(r, path, 1, null);			
		script.exec(cx, scriptScope);
		return scriptScope;
	}
	
	public Scriptable newScope(Context cx) {
		final Scriptable scope = cx.newObject(parentScope);
		scope.setPrototype(parentScope);
		scope.setParentScope(null);
		return scope;
	}
}
