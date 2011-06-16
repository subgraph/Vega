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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class SwitchWorkspaceDelegate implements IWorkbenchWindowActionDelegate {

	public void init(IWorkbenchWindow window) {
	}

	public void dispose() {
	}

	public void run(IAction action) {
		SwitchWorkspaceHandler.openChoseWorkspaceDialog(true);
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}
