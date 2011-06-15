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
package com.subgraph.vega.ui.http.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.ui.http.Activator;

public class ResetRequestFilter extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IModel model = Activator.getDefault().getModel();
		final IWorkspace workspace = model.getCurrentWorkspace();
		if(workspace != null) {
			final IHttpConditionManager conditionManager = workspace.getHttpConditionMananger();
			final IHttpConditionSet filterSet = conditionManager.getConditionSet("filter");
			filterSet.clearConditions();
			conditionManager.saveConditionSet("filter", filterSet);
		}		
		return null;
	}
}
