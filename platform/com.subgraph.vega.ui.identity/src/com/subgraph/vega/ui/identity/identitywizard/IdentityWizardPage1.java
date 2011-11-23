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
package com.subgraph.vega.ui.identity.identitywizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.ui.identity.identitiesview.AuthMethodComboViewer;
import com.subgraph.vega.ui.identity.identitiesview.AuthMethodComboViewer.AuthMethodSelection;

public class IdentityWizardPage1 extends WizardPage implements IIdentityWizardPage {
	private Composite parentComposite;
	private Text identityNameText;
	private AuthMethodComboViewer authMethodComboViewer;

	protected IdentityWizardPage1() {
		super("Create an identity");
		setTitle("Create an identity");
		setDescription("Specify basic information about the identity");
	}

	@Override
	public void createControl(Composite parent) {
		parentComposite = new Composite(parent, SWT.NULL);
		parentComposite.setLayout(new GridLayout(1, false));

		Label label = new Label(parentComposite, SWT.NONE);
		label.setText("Input an identity name:");

		identityNameText = new Text(parentComposite, SWT.SINGLE | SWT.BORDER);
		identityNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		identityNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(!identityNameText.getText().trim().isEmpty());
			}
		});
		
		label = new Label(parentComposite, SWT.NONE);
		label.setText("Authentication type:");

		authMethodComboViewer = new AuthMethodComboViewer(parentComposite); 
		
		setControl(parentComposite);
		setPageComplete(false);
	}

	@Override
	public void pageSelected() {
	}

	public String getName() {
		return identityNameText.getText();
	}
	
	public AuthMethodSelection getAuthMethodSelection() {
		return authMethodComboViewer.getAuthMethodSelection(); 
	}

}
