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
package com.subgraph.vega.ui.http.requestfilters;


import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.util.dialogs.IConfigDialogContent;

public class RequestFilterConfigContent implements IConfigDialogContent {
	private Composite composite;
	private TreeViewer treeViewer;
	private IHttpConditionManager conditionManager;
	private IHttpConditionSet conditionSet;
	private boolean conditionSetDirty;
	
	@Override
	public String getTitle() {
		return "Configure Request View Filters";
	}

	@Override
	public String getMessage() {
		return "Create and remove filters which will control the displayed HTTP request and response records.";
	}

	@Override
	public Control getFocusControl() {
		return composite;
	}

	@Override
	public void onClose() {
	}
	
	@Override
	public void onOk() {
		if(conditionSetDirty) {
			conditionManager.saveConditionSet("filter", conditionSet);
			conditionSetDirty = false;
		}		
	}


	@Override
	public Composite createContents(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout();
		layout.marginHeight = 10;
		layout.verticalSpacing = 20;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label label = new Label(composite, SWT.WRAP);
		label.setText("Filter out information displayed in the Requests table within the Proxy.");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Composite treePanel = new Composite(composite, SWT.NONE);
		treePanel.setLayout(new GridLayout(2, false));
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		treePanel.setLayoutData(gd);
		
		createTreeViewer(treePanel);
		createTreeViewerButtons(treePanel);
		
		final IWorkspace workspace = Activator.getDefault().getModel().getCurrentWorkspace();
		if(workspace != null) {
			conditionManager = workspace.getHttpConditionMananger();
			conditionSet = conditionManager.getConditionSetCopy("filter");

		} else {
			conditionManager = null;
			conditionSet = null;
		}
		treeViewer.setInput(conditionSet);
		return composite;
	}

	private void createTreeViewer(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewer.setContentProvider(new ConditionTreeContentProvider());
		treeViewer.setLabelProvider(new ConditionTreeLabelProvider());
		treeViewer.setAutoExpandLevel(2);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3);
		gd.heightHint = 300;
		treeViewer.getTree().setLayoutData(gd);
	}

	private void createTreeViewerButtons(Composite parent) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText("create");
		button.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		button.addSelectionListener(createSelectionListenerButtonCreate());
		button = new Button(parent, SWT.PUSH);
		button.setText("remove");
		button.setLayoutData(new GridData(SWT.TOP, SWT.CENTER, false, false));
		button.addSelectionListener(createSelectionListenerButtonRemove());
	}

	private SelectionListener createSelectionListenerButtonCreate() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				openConditionCreateDialog();
			}
		};
	}

	private SelectionListener createSelectionListenerButtonRemove() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
				for(Object element: selection.toList()) {
					if(element instanceof IHttpCondition) {
						conditionSet.removeCondition((IHttpCondition) element);
						conditionSetDirty = true;
					} else if (element instanceof IHttpConditionType) {
						IHttpConditionType type = (IHttpConditionType) element;
						for(IHttpCondition c: conditionSet.getAllConditions()) {
							if(c.getType() == type) {
								conditionSet.removeCondition(c);
								conditionSetDirty = true;
							}
						}
					}
						
				}
				treeViewer.refresh();
			}
		};
	}

	private void openConditionCreateDialog() {
		if(conditionSet == null || conditionManager == null)
			return;
		
		ConditionCreateDialog dialog = new ConditionCreateDialog(null, conditionManager);
		dialog.setBlockOnOpen(true);
		if (dialog.open() == ConditionCreateDialog.OK) {
			final IHttpCondition condition = dialog.getNewCondition();
			if(condition != null) {
				conditionSet.appendCondition(condition);
				conditionSetDirty = true;
				treeViewer.refresh();
				treeViewer.expandToLevel(condition, TreeViewer.ALL_LEVELS);
			}
		}
	}
}
