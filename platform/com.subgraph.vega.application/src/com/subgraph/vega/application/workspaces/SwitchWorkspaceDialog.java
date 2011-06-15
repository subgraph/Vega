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
package com.subgraph.vega.application.workspaces;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class SwitchWorkspaceDialog extends Dialog {

	private List list;
	private int index;
	private java.util.List<WorkspaceRecord> records;
	private WorkspaceRecord selectedWorkspaceRecord;
	
	SwitchWorkspaceDialog(Shell shell, java.util.List<WorkspaceRecord> records) {
		super(shell);
		this.records = records;
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Switch Workspace");
	}

	 protected Control createDialogArea(Composite parent) {
		 Composite composite = (Composite) super.createDialogArea(parent);
		 Label label = new Label(composite, SWT.NONE);
		 label.setText("Choose a workspace to open:");
		 GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, true);
		 label.setLayoutData(gd);
		  list = new List(composite, SWT.SINGLE);
		 gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		 list.setLayoutData(gd);
		 for(WorkspaceRecord rec : records) {
			 list.add(rec.getName());
		 }
		 list.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {				
			}

			public void widgetSelected(SelectionEvent e) {
				setIndex(list.getSelectionIndex());
				
			}
			 
		 });
		 list.addListener(SWT.MouseDoubleClick, new Listener() {

			public void handleEvent(Event event) {
				setIndex(list.getSelectionIndex());
				okPressed();	
			}
		 });
		 
		 list.setSelection(0);
		 setIndex(0);
		 
		 composite.setLayout(new GridLayout());
		 return composite;
		 
	 }
	 @Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		createButton(parent,IDialogConstants.NEXT_ID,"New workspace >>",false);
	}
	 @Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
		if(buttonId == IDialogConstants.NEXT_ID) {
			createWorkspacePressed();
		}
	}
	
	private void createWorkspacePressed() {
		setReturnCode(IDialogConstants.NEXT_ID);
		close();
	}
	
	 private void setIndex(int index) {
		 this.index = index;
		 setSelectedWorkspaceRecord(records.get(index));
	 }
	 
	 public int getIndex() {
		 return index;
	 }

	public void setSelectedWorkspaceRecord(WorkspaceRecord selectedWorkspaceRecord) {
		this.selectedWorkspaceRecord = selectedWorkspaceRecord;
	}

	public WorkspaceRecord getSelectedWorkspaceRecord() {
		return selectedWorkspaceRecord;
	}
	
}
