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
package com.subgraph.vega.api.model;

import java.util.List;

import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;


public interface IModel {
	IWorkspace addWorkspaceListener(IEventHandler handler);
	void removeWorkspaceListener(IEventHandler handler);
	void resetCurrentWorkspace();
	
	/**
	 * Attempts to open the 'default' workspace, returning <code>true</code> on success.  If another workspace is currently
	 * active, it will be closed before opening the new workspace.
	 * 
	 * The default workspace is chosen using the following strategy:
	 * 
	 * 1) If no workspace directories currently exist, a new workspace directory is created with index 00 and name 'default'.
	 * 2) If a single workspace directory exists, it is chosen as the default workspace.
	 * 3) If multiple workspace directories exist, they are searched in sequential order for the first one containing a file 
	 *    named '.autostart', which is chosen as the default workspace.  Otherwise the workspace with the lowest index is chosen.
	 *    
	 * @return <code>true</code> if the default workspace could be successfully opened, <code>false</code> otherwise.
	 */
	boolean openDefaultWorkspace();

	/**
	 * Return a list of <code>IWorkspaceEntry</code> elements corresponding to existing workspace directories.
	 * 
	 * @return A list of <code>IWorkspaceEntry</code> elements corresponding to existing workspace directories.
	 */
	List<IWorkspaceEntry> getWorkspaceEntries();
	
	/**
	 * Attempt to open the workspace specified by the workspace index argument.  A valid workspace index is obtained by
	 * first calling <code>getWorkspaceEntries()</code> to retrieve a list of metadata describing existing workspaces.
	 * 
	 * @param index The index of the workspace to open.
	 * @return <code>true</code> if the workspace specified by the <code>index</code> argument could be successfully opened, <code>false</code> otherwise.
	 */
	boolean openWorkspaceByIndex(int index);
	
	IWorkspace getCurrentWorkspace();

	/**
	 * Tracks condition set changes across changing workspaces
	 * 
	 * @param name Name of condition set
	 * @param handler Callback which processes ConditionSetChanged messages
	 * @return The current named condition set if a workspace is currently open, null otherwise.
	 */
	IHttpConditionSet addConditionSetTracker(String name, IEventHandler handler);
	
	void removeConditionSetTracker(String name, IEventHandler handler);
}
