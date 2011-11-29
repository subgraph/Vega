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
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;

import com.subgraph.vega.api.model.identity.IAuthMethod;
import com.subgraph.vega.api.model.identity.IAuthMethodRfc2617;
import com.subgraph.vega.ui.identity.identitiesview.AuthMethodComboViewer;

public class IdentityWizardPage2 extends WizardPage implements IIdentityWizardPage {
	private final IdentityWizardPage1 page1;
	private Composite parentComposite;
	private StackLayout stackLayout;
	private AuthMethodComboViewer.AuthMethodSelection authMethodSelCurr;
	private IAuthMethodControl authMethodControl;

	protected IdentityWizardPage2(IdentityWizardPage1 page1) {
		super("Create an identity");
		setTitle("Create an identity");
		setDescription("Specify scheme-specific authentication information");
		this.page1 = page1;
	}

	@Override
	public void createControl(Composite parent) {
		parentComposite = new Composite(parent, SWT.NULL);
		stackLayout = new StackLayout();
		parentComposite.setLayout(stackLayout);
		setControl(parentComposite);
		setPageComplete(false);		
	}

	@Override
	public void pageSelected() {
		if (authMethodSelCurr != page1.getAuthMethodSelection()) {
			authMethodSelCurr = page1.getAuthMethodSelection();
			authMethodControl = instAuthMethodControl(authMethodSelCurr);
			stackLayout.topControl = authMethodControl.getControl();
			stackLayout.topControl.setFocus();
			parentComposite.layout();
		}
	}

	private IAuthMethodControl instAuthMethodControl(AuthMethodComboViewer.AuthMethodSelection authMethodSel) {
		IAuthMethodControl control = null;
		switch (authMethodSelCurr) {
		case AUTH_METHOD_BASIC:
			control = new AuthMethodControlRfc2617(parentComposite, this, IAuthMethodRfc2617.AuthScheme.AUTH_SCHEME_BASIC);
			break;
		case AUTH_METHOD_DIGEST:
			control = new AuthMethodControlRfc2617(parentComposite, this, IAuthMethodRfc2617.AuthScheme.AUTH_SCHEME_DIGEST);
			break;
		case AUTH_METHOD_NTLM:
			control = new AuthMethodControlNtlm(parentComposite, this);
			break;
		case AUTH_METHOD_HTTP_MACRO:
			control = new AuthMethodControlHttpMacro(parentComposite, this);
			break;
		default:
			return null;
		}
		return control;
	}

	public IAuthMethod getAuthMethod() {
		return authMethodControl.getAuthMethod();
	}

}
