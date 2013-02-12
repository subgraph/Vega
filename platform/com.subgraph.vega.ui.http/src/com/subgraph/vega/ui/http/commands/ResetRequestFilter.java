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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.http.request.view.HttpRequestView;

public class ResetRequestFilter extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart instanceof HttpRequestView) {
			final IModel model = Activator.getDefault().getModel();
			final IWorkspace workspace = model.getCurrentWorkspace();
			if(workspace != null) {
				final String secondaryId = ((HttpRequestView) activePart).getViewSite().getSecondaryId();
				final String conditionSetId;
				if (secondaryId != null) {
					conditionSetId = IHttpConditionManager.CONDITION_SET_FILTER + "." + secondaryId;
				} else {
					conditionSetId = IHttpConditionManager.CONDITION_SET_FILTER;
				}
				final IHttpConditionManager conditionManager = workspace.getHttpConditionMananger();
				final IHttpConditionSet filterSet = conditionManager.getConditionSet(conditionSetId);
				filterSet.clearConditions(false);
				filterSet.clearTemporaryConditions(false);
				filterSet.notifyChanged();
				conditionManager.saveConditionSet(conditionSetId, filterSet);
			}
		} else {
			final Logger logger = Logger.getLogger("proxy");
			logger.log(Level.WARNING, "ResetRequestFilter command occurred from unexpected origin");
		}
		return null;
	}

}
