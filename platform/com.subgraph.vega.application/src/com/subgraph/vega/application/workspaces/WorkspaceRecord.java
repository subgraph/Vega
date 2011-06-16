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
package com.subgraph.vega.application.workspaces;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class WorkspaceRecord {
	private final String name;
	private final File path;
	private final boolean isAutostart;
	
	public WorkspaceRecord(String name, File path, boolean autostart) {
		this.name = name;
		this.path = path;
		this.isAutostart = autostart;
	}
	
	public String getName() {
		return name;
	}
	
	public File getPath() {
		return path;
	}
	
	public boolean isAutostart() {
		return isAutostart;
	}
	
	public URL getURL() {
		try {
			return new URL("file:" + path.getPath());
		} catch (MalformedURLException e) {
			throw new IllegalStateException("Could not create URL");
		}
	}

}
