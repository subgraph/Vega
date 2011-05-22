package com.subgraph.vega.ui.http;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

// temporary class, probably
public class ErrorDisplay {

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
		} else {
			displayError(shell, e.getCause().getMessage());
		}
	}

}
