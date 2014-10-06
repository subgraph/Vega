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
package com.subgraph.vega.application.about;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.api.vuge.IConstants;
import com.subgraph.vega.application.Activator;

public class AboutDialog extends TitleAreaDialog {

	private final static String LOGO_IMAGE = "icons/subgraph.png";
	
	private String aboutText = "Vega, the Open Source Web Application Security Platform.\n\n" +
								"Version: " + IConstants.VERSION_STRING + "\n" +
								"Build id: 0xC0FFEEEE\n" +
								"\n" +
								"(c) Copyright Subgraph Technologies, Inc. and others, 2014. All rights reserved.\n" +
								"https://subgraph.com\n" +
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
								"The Vega 1.0 team is:\n" +
								"\n" +
								"Bruce Leidl\n" +
								"David Mirza Ahmad\n" +
								"Tina Salameh\n" +
								"David McKinney" +
								"\n\n" +
								"Vega Beta contributors also included Hugo Fortier and Cade Cairns."+
								"\n\n"+
								"Special thanks to Lars Vogel for his Eclipse RCP tutorials.\n";

	
	public AboutDialog(Shell parentShell) {
		super(parentShell);
		setTitle("About Vega");
		setTitleImage(createLogoImage());
	}
	
	private Image createLogoImage() {
		final ImageDescriptor descriptor = Activator.getImageDescriptor(LOGO_IMAGE);
		if(descriptor != null) {
			return descriptor.createImage();
		} else {
			return null;
		}
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
