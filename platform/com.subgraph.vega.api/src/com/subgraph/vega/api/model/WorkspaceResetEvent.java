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

import com.subgraph.vega.api.events.IEvent;

public class WorkspaceResetEvent implements IEvent {
private final IWorkspace workspace;
	
	public WorkspaceResetEvent(IWorkspace workspace) {
		this.workspace = workspace;
	}

	public IWorkspace getWorkspace() {
		return workspace;
	}
}
