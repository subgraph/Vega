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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Close workspace handler extends AbstractHandler, an IHandler base class.
 * This handler is set to the Exit menu in ui.application/plugin.xml
 * and is invoked from preWindowShellClose in ApplicationWorkbenchWindowAdvisor 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class CloseWorkspaceHandler extends AbstractHandler {
	private IWorkbenchWindow activeWorkbenchWindow;

	public CloseWorkspaceHandler() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		activeWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		if (activeWorkbenchWindow == null) {
			// action has been disposed
			return null;
		}
		
		/* code to execute before closing follows */
			
		return null;
	}
}
