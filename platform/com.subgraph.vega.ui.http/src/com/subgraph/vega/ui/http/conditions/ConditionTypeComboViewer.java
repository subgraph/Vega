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
package com.subgraph.vega.ui.http.conditions;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.subgraph.vega.api.model.conditions.IHttpConditionType;

public class ConditionTypeComboViewer extends ComboViewer {
	private MatchActionComboViewer matchActionViewer;
	
	public ConditionTypeComboViewer(Composite parent, List<IHttpConditionType> conditionTypes) {
		super(parent, SWT.READ_ONLY);
		
		setContentProvider(new ArrayContentProvider());
		setLabelProvider(createLabelProvider());
		addSelectionChangedListener(createSelectionChangedListener());
		setInput(conditionTypes.toArray());
		reset();
	}
	
	
	private ILabelProvider createLabelProvider() {
		return new LabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof IHttpConditionType) 
					return ((IHttpConditionType) element).getName();
				else
					return null;
			}
		};
	}
	
	private ISelectionChangedListener createSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setMatchActionInputForSelection(event.getSelection());
			}
		};
	}

	public void reset() {
		final Object element = getElementAt(0);
		if(element != null)
			setSelection(new StructuredSelection(element));
	}

	public void setMatchTypeViewer(MatchActionComboViewer viewer) {
		matchActionViewer = viewer;
		setMatchActionInputForSelection(getSelection());
	}
	
	private void setMatchActionInputForSelection(ISelection selection) {
		if(matchActionViewer == null || !(selection instanceof IStructuredSelection))
			return;
		final IStructuredSelection ss = (IStructuredSelection) selection;
		if(!(ss.getFirstElement() instanceof IHttpConditionType))
			return;
		
		final IHttpConditionType conditionType = (IHttpConditionType) ss.getFirstElement();
		matchActionViewer.setMatchActionInput(conditionType.getMatchActions());
	}
	
	public IHttpConditionType getSelectedConditionType() {
		final IStructuredSelection selection = (IStructuredSelection) getSelection();
		if(selection.isEmpty())
			return null;
		return (IHttpConditionType) selection.getFirstElement();
	}
}
