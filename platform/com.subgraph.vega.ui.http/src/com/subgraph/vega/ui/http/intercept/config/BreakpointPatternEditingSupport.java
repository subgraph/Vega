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
package com.subgraph.vega.ui.http.intercept.config;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.subgraph.vega.api.model.conditions.IHttpCondition;

public class BreakpointPatternEditingSupport extends EditingSupport {
	private final TableViewer viewer;

	public BreakpointPatternEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new TextCellEditor(viewer.getTable());
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		final IHttpCondition condition = (IHttpCondition) element;
		return condition.getMatchAction().getArgumentAsString();
	}

	@Override
	protected void setValue(Object element, Object value) {
		if(!(value instanceof String))
			return;
		final IHttpCondition condition = (IHttpCondition) element;
		condition.getMatchAction().setArgumentFromString((String) value);
		viewer.refresh(true);
	}
}
