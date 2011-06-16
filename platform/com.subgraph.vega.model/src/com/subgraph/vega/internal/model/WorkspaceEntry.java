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
package com.subgraph.vega.internal.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import com.subgraph.vega.api.model.IWorkspaceEntry;

public class WorkspaceEntry implements IWorkspaceEntry {
	private static final Logger logger = Logger.getLogger("model");
	
	static WorkspaceEntry createFromPath(File path) {
		if(!Character.isDigit(path.getName().charAt(0))) {
			return null;
		}
		final int index = pathToIndex(path);
		if(index == -1)
			return null;
		final String name = pathToWorkspaceName(path);
		if(name == null)
			return null;
		final boolean autostart = pathToAutostartFlag(path);

		return new WorkspaceEntry(name, index, autostart, path);
	}
	
	static int pathToIndex(File path) {
		String indexName = path.getName();
		try {
			final int n = Integer.parseInt(indexName);
			if(n < 0 || n > 1000) {
				logger.warning("Could not read workspace because path is incorrectly formatted "+ path);
				return -1;
			}
			else
				return n;
		} catch (NumberFormatException e) {
			logger.warning("Could not read workspace because path is incorrectly formatted "+ path);
			return -1;
		}
	}
	
	static String pathToWorkspaceName(File path) {
		final File nameFile = new File(path, ".name");
		final BufferedReader reader = openNameFileReader(nameFile);
		if(reader == null)
			return null;
		
		try {
			final String name = reader.readLine();
			if(name == null || name.isEmpty()) {
				logger.warning("Could not read workpace because name file is empty "+ nameFile);
				return null;
			}
			return name;
			
		}  catch (IOException e) {
			logger.warning("Could not read workspace, I/O error reading name file "+ nameFile);		
			return null;
		} finally {
			try {
				reader.close();
			} catch (IOException e) { }
		}
		
	}
	
	static boolean pathToAutostartFlag(File path) {
		final File autostartFile = new File(path, ".autostart");
		return autostartFile.exists();
	}
	
	static BufferedReader openNameFileReader(File nameFile) {
		try {
			final FileInputStream in = new FileInputStream(nameFile);
			return new BufferedReader(new InputStreamReader(in));
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	private final String name;
	private final int index;
	private final boolean autostart;
	private final File path;
	
	WorkspaceEntry(String name, int index, boolean autostart, File path) {
		this.name = name;
		this.index = index;
		this.autostart = autostart;
		this.path = path;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public File getPath() {
		return path;
	}


	@Override
	public boolean isAutostart() {
		return autostart;
	}
}
