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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.subgraph.vega.api.model.IWorkspaceEntry;
import com.subgraph.vega.api.paths.IPathFinder;

public class WorkspaceEntries {
	private final static int MAX_WORKSPACE_INDEX = 1000;
	private final static int DEFAULT_WORKSPACE_INDEX = 0;
	private final Logger logger = Logger.getLogger("model");
	private final File workspaceDirectory;
	private final IWorkspaceEntry[] entryArray = new IWorkspaceEntry[MAX_WORKSPACE_INDEX + 1];
	
	WorkspaceEntries(IPathFinder pathFinder) {
		this.workspaceDirectory = pathFinder.getWorkspaceDirectory();
		loadWorkspaceEntries();
	}
	
	private void loadWorkspaceEntries() {
		if(!workspaceDirectory.exists()) {
			if(!workspaceDirectory.mkdirs()) {
				logger.warning("Failed to create workspace directory: "+ workspaceDirectory);
				return;
			}
		}
		
		if(!workspaceDirectory.isDirectory()) {
			logger.warning("Workspace directory name exists but is not a directory: "+ workspaceDirectory);
			return;
		}
		for(File ws: workspaceDirectory.listFiles()) {
			final WorkspaceEntry entry = WorkspaceEntry.createFromPath(ws);
			if(entry != null) {
				entryArray[entry.getIndex()] = entry;
			}
		}
		
		if(entryArray[DEFAULT_WORKSPACE_INDEX] == null) 
			entryArray[DEFAULT_WORKSPACE_INDEX] = createDefaultWorkspace();
					
	}

	private WorkspaceEntry createDefaultWorkspace() {
		return createWorkspace(DEFAULT_WORKSPACE_INDEX, "default");
	}
	
	private WorkspaceEntry createWorkspace(int index, String name) {
		final File path = indexToWorkspacePath(index);
		final File nameFile = new File(path, ".name");
		if(path.exists())
			throw new IllegalStateException("Workspace directory already exists "+ path);
		if(!path.mkdirs()) {
			logger.warning("Failed to make directory while creating workspace: "+ path);
			return null;
		}
		final Writer writer = openNameFileWriter(nameFile);
		if(writer == null)
			return null;
		try {
			writer.write(name +"\n");
			return new WorkspaceEntry(name, index, false, path);
		} catch (IOException e) {
			logger.warning("I/O error writing to name file while creating workspace "+ nameFile);
			return null;
		} finally {
			try {
				writer.close();
			} catch (IOException e) {}
		}
	}
	
	private Writer openNameFileWriter(File nameFile) {
		try {
			final FileOutputStream out = new FileOutputStream(nameFile);
			return new OutputStreamWriter(out);
		} catch (FileNotFoundException e) {
			logger.warning("Failed to open name file for writing :"+ nameFile);
			return null;
		}
	}
	
	private File indexToWorkspacePath(int index) {
		if(index < 0 || index > MAX_WORKSPACE_INDEX)
			throw new IllegalArgumentException("Workspace index out of range (0 - "+ MAX_WORKSPACE_INDEX +") : "+ index);
		final String indexStr = String.format("%02d", index);
		return new File(workspaceDirectory, indexStr);
	}
	
	List<IWorkspaceEntry> getWorkspaceEntries() {
		final List<IWorkspaceEntry> entryList = new ArrayList<IWorkspaceEntry>();
		for(int i = 0; i <= MAX_WORKSPACE_INDEX; i++)
			if(entryArray[i] != null)
				entryList.add(entryArray[i]);
		return entryList;
	}
	
	IWorkspaceEntry getDefaultWorkspaceEntry() {
		IWorkspaceEntry firstEntry = null;
		
		for(int i = 0; i <= MAX_WORKSPACE_INDEX; i++) {
			IWorkspaceEntry entry = entryArray[i];
			if(entry != null) {
				if(entry.isAutostart())
					return entry;
				else if(firstEntry == null)
					firstEntry = entry;
			}
		}
		
		if(firstEntry == null) 
			throw new IllegalStateException("No default workspace entry found");
		
		return firstEntry;
	}
}
