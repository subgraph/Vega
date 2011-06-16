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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public class MatchActionArgumentPanel extends Composite {

	private final StackLayout stackLayout;
	
	private final Composite matchStringInputPanel;
	private final Text matchStringText;
	
	private final Composite regexInputPanel;
	private final Text regexText;
	
	private final Composite integerInputPanel;
	private final Text integerText;
	
	private final Composite rangeInputPanel;
	private final Text rangeLowText;
	private final Text rangeHighText;
	
	public MatchActionArgumentPanel(Composite parent) {
		super(parent, SWT.NONE);
		stackLayout = new StackLayout();
		setLayout(stackLayout);
		
		regexInputPanel = createStackedPanel(1);
		regexText = createTextField(regexInputPanel, "regular expression", true);
		
		matchStringInputPanel = createStackedPanel(1);
		matchStringText = createTextField(matchStringInputPanel, "matching string", true);
		
		integerInputPanel = createStackedPanel(1);
		integerText = createTextField(integerInputPanel, "integer value", true);
		
		rangeInputPanel = createStackedPanel(3);
		rangeLowText = createTextField(rangeInputPanel, "from", false);
		createRangeSeperator(rangeInputPanel);
		rangeHighText = createTextField(rangeInputPanel, "to", false);
	}
	
	public void displayPanelForMatchAction(IHttpConditionMatchAction matchAction) {
		switch(matchAction.getArgumentType()) {
		case ARGUMENT_REGEX:
			displayRegexPanel();
			break;
		case ARGUMENT_STRING:
			displayStringPanel();
			break;
		case ARGUMENT_INTEGER:
			displayIntegerPanel();
			break;
		case ARGUMENT_RANGE:
			displayRangePanel();
			break;
		}
	}
	
	public void displayRegexPanel() {
		regexText.setText("");
		stackLayout.topControl = regexInputPanel;
		layout();
	}
	
	public String getRegexText() {
		return regexText.getText();
	}
	
	public void displayStringPanel() {
		matchStringText.setText("");
		stackLayout.topControl = matchStringInputPanel;
		layout();
	}
	
	public String getStringText() {
		return matchStringText.getText();
	}
	
	public void displayIntegerPanel() {
		integerText.setText("");
		stackLayout.topControl = integerInputPanel;
		layout();
	}
	
	public String getIntegerText() {
		return integerText.getText();
	}
	
	public void displayRangePanel() {
		rangeLowText.setText("");
		rangeHighText.setText("");
		stackLayout.topControl = rangeInputPanel;
		layout();
	}
	
	public String getRangeLowText() {
		return rangeLowText.getText();
	}
	
	public String getRangeHighText() {
		return rangeHighText.getText();
	}

	private Composite createStackedPanel(int columns) {
		final Composite panel = new Composite(this, SWT.NONE);
		panel.setLayout(new GridLayout(columns, false));
		return panel;
	}
	
	private Text createTextField(Composite parent, String message, boolean expandHorizontal) {
		final Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
		text.setMessage(message);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, expandHorizontal, false));
		return text;
	}
	
	private Label createRangeSeperator(Composite parent) {
		final Label sep = new Label(parent, SWT.NONE);
		sep.setText(" - ");
		sep.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		return sep;
	}

}
