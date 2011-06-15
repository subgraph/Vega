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

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public class BreakpointMatchTypeEditingSupport extends EditingSupport {
	private final TableViewer viewer;

	public BreakpointMatchTypeEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		ComboBoxViewerCellEditor editor = new ComboBoxViewerCellEditor(viewer.getTable(), SWT.READ_ONLY);
		editor.setContenProvider(new ArrayContentProvider());
		editor.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((IHttpConditionMatchAction) element).getLabel();
			}
		});
		final IHttpCondition condition = (IHttpCondition) element;
		final List<IHttpConditionMatchAction> matchActions = condition.getType().getMatchActions();
		editor.setInput(matchActions);
		for(IHttpConditionMatchAction ma: matchActions) {
			if(ma.getLabel().equals(condition.getMatchAction().getLabel()))
				editor.getViewer().setSelection(new StructuredSelection(ma));
		}
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		return ((IHttpCondition) element).getType();
	}

	@Override
	protected void setValue(Object element, Object value) {
		final IHttpCondition condition = (IHttpCondition) element;
		final IHttpConditionMatchAction newMatchAction = (IHttpConditionMatchAction) value;
		final IHttpConditionMatchAction oldMatchAction = condition.getMatchAction();
		if(newMatchAction.getLabel().equals(oldMatchAction.getLabel()))
			return;
		// Input will be quietly rejected if it doesn't make sense  (ie: an integer input to a range match action)
		newMatchAction.setArgumentFromString(oldMatchAction.getArgumentAsString());
		condition.setMatchAction(newMatchAction);
		viewer.refresh(true);
	}

}
