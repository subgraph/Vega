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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.ui.http.requestlogviewer.RequestLogViewer;

public class MacroItemSelectionDialog extends TitleAreaDialog {
	private RequestLogViewer logViewer;
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

		logViewer = new RequestLogViewer(dialogArea, UUID.randomUUID().toString());

		return dialogArea;
	}

	public List<IRequestLogRecord> getSelectionList() {
		return selectionList;
	}
	
}
