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