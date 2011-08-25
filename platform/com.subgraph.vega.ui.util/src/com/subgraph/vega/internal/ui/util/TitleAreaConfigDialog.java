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
package com.subgraph.vega.internal.ui.util;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.subgraph.vega.ui.util.dialogs.IConfigDialogContent;

public class TitleAreaConfigDialog extends TitleAreaDialog {

	private final IConfigDialogContent content;
	
	public TitleAreaConfigDialog(Shell parentShell, IConfigDialogContent content) {
		super(parentShell);
		this.content = content;
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	public void create() {
		super.create();
		setTitle(content.getTitle());
		setMessage(content.getMessage());
	}

	@Override
	protected void okPressed() {
		content.onOk();
		super.okPressed();
	}

	@Override
	public boolean close() {
		content.onClose();
		return super.close();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);
		content.createContents(parent);
		return dialogAreaComposite;
	}
}
