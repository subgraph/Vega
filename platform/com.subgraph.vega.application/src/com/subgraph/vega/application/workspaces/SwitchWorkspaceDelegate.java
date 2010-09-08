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