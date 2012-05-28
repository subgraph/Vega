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
package com.subgraph.vega.internal.ui.macros.macrodialog;

import java.util.List;
import java.util.UUID;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.ui.http.requestlogviewer.RequestLogViewer;
import com.subgraph.vega.ui.http.requestlogviewer.RequestResponseViewer;

public class MacroItemSelectionDialog extends TitleAreaDialog {
	private RequestLogViewer logViewer;
	private RequestResponseViewer requestResponseViewer;
	private List<IRequestLogRecord> selectionList;

	public MacroItemSelectionDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		selectionList = logViewer.getSelectionList();
		super.okPressed();
	}

	@Override
	public void create() {
		super.create();
		setTitle("Macro Item Selector");
		setMessage("Select one or more requests below to be added to the macro");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite dialogArea = (Composite) super.createDialogArea(parent);
		final SashForm form = new SashForm(dialogArea, SWT.VERTICAL);
		form.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		logViewer = new RequestLogViewer(form, UUID.randomUUID().toString(), 12);
		requestResponseViewer = new RequestResponseViewer(form);
		logViewer.setRequestResponseViewer(requestResponseViewer);

		form.setWeights(new int[] {40, 60});

		return dialogArea;
	}

	public List<IRequestLogRecord> getSelectionList() {
		return selectionList;
	}
	
}
