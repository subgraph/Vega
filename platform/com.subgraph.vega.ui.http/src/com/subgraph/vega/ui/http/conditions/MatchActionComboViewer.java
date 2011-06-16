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

import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public class MatchActionComboViewer extends ComboViewer {

	private MatchActionArgumentPanel argumentPanel;
	
	public MatchActionComboViewer(Composite parent) {
		super(parent, SWT.READ_ONLY);
		setContentProvider(new ArrayContentProvider());
		setLabelProvider(createLabelProvider());
		addSelectionChangedListener(createSelectionChangedListener());
	}
	
	public void setMatchActionArgumentPanel(MatchActionArgumentPanel argumentPanel) {
		this.argumentPanel = argumentPanel;
		setArgumentPanelBySelection(getSelection());
	}
	
	private ILabelProvider createLabelProvider() {
		return new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IHttpConditionMatchAction) element).getLabel();
			}
		};
	}
	
	private ISelectionChangedListener createSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setArgumentPanelBySelection(event.getSelection());
			}
		};
	}
	
	public void reset() {
		final Object element = getElementAt(0);
		if(element != null)
			setSelection(new StructuredSelection(element));
	}

	public void setMatchActionInput(List<IHttpConditionMatchAction> matchActions) {
		setInput(matchActions.toArray());
		reset();
	}
	
	public IHttpConditionMatchAction getSelectedMatchAction() {
		final IStructuredSelection selection = (IStructuredSelection) getSelection();
		if(selection.isEmpty())
			return null;
		return (IHttpConditionMatchAction) selection.getFirstElement();
	}
	
	private void setArgumentPanelBySelection(ISelection selection) {
		final IStructuredSelection ss = (IStructuredSelection) selection;
		if(argumentPanel != null && !ss.isEmpty() && (ss.getFirstElement() instanceof IHttpConditionMatchAction))		
			argumentPanel.displayPanelForMatchAction((IHttpConditionMatchAction) ss.getFirstElement());
	}
}
