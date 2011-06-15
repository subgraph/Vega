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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.mozilla.javascript.Scriptable;

public class ScriptFile {
	public enum CompileStatus { NOT_COMPILED, COMPILE_FAILED, COMPILE_SUCCEEDED };
	private final URL scriptURL;
	private final File scriptFile;
	
	private long fileLastModified;
	private CompileStatus compileStatus;
	private Scriptable compiledScript;
	private String compileFailureMessage = "";
	
	public ScriptFile(File scriptFile) {
		this(scriptFile, null, scriptFile.lastModified());
	}
	
	public ScriptFile(URL scriptURL) {
		this(null, scriptURL, 0);
	}
	
	private ScriptFile(File scriptFile, URL scriptURL, long lastModified) {
		this.scriptFile = scriptFile;
		this.scriptURL = scriptURL;
		this.fileLastModified = lastModified;
		this.compileStatus = CompileStatus.NOT_COMPILED;
		this.compileFailureMessage = "";
	}
	
	public String getPath() {
		if(scriptFile != null)
			return scriptFile.getAbsolutePath();
		else if(scriptURL != null)
			return scriptURL.getPath();
		else 
			return null;
	}
	
	public Reader createReader() throws IOException {
		if(scriptFile != null)
			return new FileReader(scriptFile);
		else if(scriptURL != null) 
			return new InputStreamReader(scriptURL.openStream());
		else
			return null;
	}
	
	void setCompiledScript(Scriptable script) {
		this.compiledScript = script;
		compileFailureMessage = "";
		setCompileStatus(CompileStatus.COMPILE_SUCCEEDED);
	}
	
	void setCompileFailed(String failureMessage) {
		compileFailureMessage = failureMessage;
		compiledScript = null;
		setCompileStatus(CompileStatus.COMPILE_FAILED);
	}
	
	public String getCompileFailureMessage() {
		return compileFailureMessage;
	}
	
	private void setCompileStatus(CompileStatus status) {
		this.compileStatus = status;
		if(scriptFile != null)
			fileLastModified = scriptFile.lastModified();
	}
	
	public CompileStatus getCompileStatus() {
		return compileStatus;
	}
	
	public Scriptable getCompiledScript() {
		return compiledScript;
	}
	
	public boolean hasFileChanged() {
		if(scriptURL != null)
			return false;
		else
			return (scriptFile.lastModified() > fileLastModified);
	}
}
