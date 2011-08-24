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
package com.subgraph.vega.ui.util.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

// temporary class, probably
public class ErrorDialog {

	static public void displayError(Shell shell, String text) {
		MessageBox messageDialog = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		messageDialog.setText("Error");
		if (text == null) {
			text = "Unexpected error occurred";
		}
		messageDialog.setMessage(text);
		messageDialog.open();
	}

	static public void displayExceptionError(Shell shell, Exception e) {
		if (e.getMessage() != null) {
			displayError(shell, e.getMessage());
		} if (e.getCause() != null) {
			displayError(shell, e.getCause().getMessage());
		} else {
			displayError(shell, e.toString());
		}
	}

	static public void displayExceptionError(Shell shell, NullPointerException e) {
		displayError(shell, "Unexpected error encountered");
	}
	
}
