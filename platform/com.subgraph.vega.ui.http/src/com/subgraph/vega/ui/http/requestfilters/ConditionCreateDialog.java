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

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.ui.http.conditions.ConditionInput;

public class ConditionCreateDialog extends TitleAreaDialog {

	private final ConditionInput conditionInput;
	private Composite parentComposite;
	private IHttpCondition newCondition;

	public ConditionCreateDialog(Shell parent, IHttpConditionManager conditionManager) {
		super(parent);
		conditionInput = new ConditionInput(conditionManager);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Create a filter");
		setMessage("Create a new filter to filter out information displayed in the Requests table within the Proxy.");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parentComposite = (Composite) super.createDialogArea(parent);
		createFields(parentComposite);
		return parentComposite;
	}
	
	@Override
	protected void okPressed() {
		newCondition = conditionInput.createConditionFromData();
		super.okPressed();
	}
	
	private Composite createFields(Composite parent) {
		Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(2, true));

		Label label = new Label(rootControl, SWT.NONE);
		label.setText("Condition type:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		conditionInput.createConditionTypeCombo(rootControl);
		
		label = new Label(rootControl, SWT.NONE);
		label.setText("Match type:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		conditionInput.createConditionMatchCombo(rootControl);

		label = new Label(rootControl, SWT.NONE);
		label.setText("Input:");
		conditionInput.createInputPanel(rootControl);
		
		return rootControl;
	}
	
	public IHttpCondition getNewCondition() {
		return newCondition;
	}
}
