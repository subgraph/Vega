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

import org.eclipse.jface.wizard.Wizard;

import com.subgraph.vega.api.model.identity.IIdentity;
import com.subgraph.vega.api.model.identity.IIdentityModel;
import com.subgraph.vega.ui.identity.Activator;

public class IdentityWizard extends Wizard {
	private final IdentityWizardPage1 page1;
	private final IdentityWizardPage2 page2;
	private IIdentity identity;

	public IdentityWizard() {
		page1 = new IdentityWizardPage1();
		page2 = new IdentityWizardPage2(page1);
	}

	@Override
	public void addPages() {
		addPage(page1);
		addPage(page2);
	}

	@Override
	public boolean performFinish() {
		final IIdentityModel identityModel = Activator.getDefault().getModel().getCurrentWorkspace().getIdentityModel();
		identity = identityModel.createIdentity();
		identity.setName(page1.getName());
		identity.setAuthMethod(page2.getAuthMethod());
		return true;
	}

	public IIdentity getIdentity() {
		return identity;
	}
	
}
