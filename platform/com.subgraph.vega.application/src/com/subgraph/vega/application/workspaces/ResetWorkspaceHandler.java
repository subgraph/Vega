package com.subgraph.vega.application.workspaces;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.application.Activator;

public class ResetWorkspaceHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IModel model = Activator.getDefault().getModel();
		if(model != null)
			model.resetCurrentWorkspace();
		return null;
	}

	
}
