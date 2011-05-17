package com.subgraph.vega.application.about;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AboutDialog extends TitleAreaDialog {

	private String aboutText = "Vega, the Open Source Web Application Security Platform.\n\n" +
								"Version: Beta 1\n" +
								"Build id: 0xC0FFEEEE\n" +
								"\n" +
								"(c) Copyright Subgraph Technologies, Inc. and others, 2011. All rights reserved.\n" +
								"http://www.subgraph.com\n" +
								"http://vega.subgraph.com\n" +
								"\n" +
								"Vega would not have been possible without the generous contributions of the \nopen source and security research communities.\n\n" +
								"With much appreciation, we acknowledge that Vega is built upon the work of many \nother individuals and projects, and includes code from the following:\n " +
								"\n" +
								"the Eclipse Foundation http://www.eclipse.org\n" +
								"the Apache Software Foundation http://apache.org\n" +
								"the Mozilla Foundation http://mozilla.org\n" +
								"Jonathan Headley http://jsoup.org\n" +
								"Google, Inc. http://www.google.com\n" +
								"\n" +
								"The Vega scanner owes much to the innovative work implemented in Skipfish\nby Michal Zalewski. \n" +
								"\n" +
								"The Vega Beta team is:\n" +
								"\n" +
								"Bruce Leidl\n" +
								"Cade Cairns\n" +
								"David Mirza Ahmad\n" +
								"Hugo Fortier\n" +
								"Tina Salameh\n" +
								"\n" +
								"Special thanks to Lars Vogel for his Eclipse RCP tutorials.\n";

	
	public AboutDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea (parent); 
		
	    GridData gridData = new GridData();
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.horizontalAlignment = GridData.FILL;
	    gridData.grabExcessVerticalSpace = true; 
	    gridData.verticalAlignment = GridData.FILL;

	    Text aboutBox = new Text(composite, SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL);
	    aboutBox.setLayoutData(gridData);
	    aboutBox.setText(aboutText);

	    return composite;
	}

	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button closeButton = createButton(parent, OK, "Close", true);
		closeButton.setEnabled(true);
		    closeButton.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent e) {
		        close();
		      }
		    });
	}
	
}
