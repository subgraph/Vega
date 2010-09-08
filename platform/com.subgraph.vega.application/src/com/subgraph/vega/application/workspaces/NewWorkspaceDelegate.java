package com.subgraph.vega.application.workspaces;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class NewWorkspaceDelegate implements IWorkbenchWindowActionDelegate {

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		NewWorkspaceHandler.openNewWorkspaceWizard(true /* restart */);
	}

	
	public void selectionChanged(IAction action, ISelection selection) {
	}
}