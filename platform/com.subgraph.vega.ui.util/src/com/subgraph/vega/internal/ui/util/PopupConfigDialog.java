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

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.subgraph.vega.ui.util.dialogs.IConfigDialogContent;

public class PopupConfigDialog extends PopupDialog {
	private static final GridDataFactory LAYOUTDATA_GRAB_BOTH = 
		GridDataFactory.fillDefaults().grab(true, true);
	private static final GridLayoutFactory POPUP_LAYOUT_FACTORY = 
		GridLayoutFactory
			.fillDefaults().margins(POPUP_MARGINWIDTH, POPUP_MARGINHEIGHT)
			.spacing(POPUP_HORIZONTALSPACING, POPUP_VERTICALSPACING);
	private final Point origin;
	private final IConfigDialogContent content;
	private Control dialogArea;
	
	public PopupConfigDialog(Shell parentShell, Point origin, IConfigDialogContent content) {
		super(parentShell, PopupDialog.INFOPOPUP_SHELLSTYLE, true, false, false, false, false, content.getTitle(), "Press 'ESC' to close");
		this.origin = origin;
		this.content = content;
	}
	
	@Override
	public boolean close() {
		content.onOk();
		content.onClose();
		return super.close();
	}

	@Override
	protected Control getFocusControl() {
		return content.getFocusControl();
	}
	
	@Override
	protected Point getInitialLocation(Point initialSize) {
		final int diff = origin.x - initialSize.x;
		final int x = (diff > 0) ? (diff) : (origin.x); 
		return new Point(x, origin.y);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		return content.createContents(parent);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		POPUP_LAYOUT_FACTORY.applyTo(composite);
		
		//LAYOUTDATA_GRAB_BOTH.applyTo(composite);
		GridData gd = LAYOUTDATA_GRAB_BOTH.create();
		
		composite.setLayoutData(gd);
		if(hasTitleArea()) {
			createTitleMenuArea(composite);
			createHorizontalSeparator(composite);
		}
		dialogArea = createDialogArea(composite);
		if(dialogArea.getLayoutData() == null)
			LAYOUTDATA_GRAB_BOTH.applyTo(composite);
		
		gd = LAYOUTDATA_GRAB_BOTH.create();
		dialogArea.pack();
		gd.widthHint = dialogArea.getSize().x;
		gd.heightHint = dialogArea.getSize().y;
		dialogArea.setLayoutData(gd);
		
		if(hasInfoArea()) {
			createHorizontalSeparator(composite);
			createInfoTextArea(composite);
		}
		return composite;
		
	}
	
	private Control createHorizontalSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL
				| SWT.LINE_DOT);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true,
				false).applyTo(separator);
		return separator;
	}
}
