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

/**
 * Unchecked exception thrown when the application is unable to open the
 * workspace.
 * 
 * This exception is critical and terminal, once thrown the application exits.
 * 
 */
public class WorkspaceOpenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public WorkspaceOpenException(String message) {
		super(message);
	}
}
