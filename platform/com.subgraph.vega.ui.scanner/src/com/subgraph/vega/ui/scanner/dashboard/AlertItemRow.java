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
package com.subgraph.vega.ui.scanner.dashboard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * This class renders a single row containing the title of an alert type and the number
 * of times this alert appears in the current scan model.
 */
public class AlertItemRow extends Composite {

	private final Label countLabel;
	private int count;
	
	public AlertItemRow(Composite parent, String title) {
		super(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		setLayout(layout);
		setBackground(parent.getBackground());
		final Label label = new Label(this, SWT.WRAP);
		label.setText(title);
		label.setBackground(parent.getBackground());
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gd.widthHint = 300;
		label.setLayoutData(gd);
		
		countLabel = new Label(this, SWT.NONE);
		countLabel.setText("0");
		countLabel.setBackground(parent.getBackground());
		
		countLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));		
	}
	
	void incrementCount() {
		count += 1;
		if(!countLabel.isDisposed()) {
			countLabel.setText(Integer.toString(count));
		}
	}
}
