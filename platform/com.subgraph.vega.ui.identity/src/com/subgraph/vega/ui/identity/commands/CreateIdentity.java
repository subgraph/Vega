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
package com.subgraph.vega.ui.identity.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.identity.IIdentity;
import com.subgraph.vega.api.model.identity.IIdentityModel;
import com.subgraph.vega.ui.identity.Activator;
import com.subgraph.vega.ui.identity.identitywizard.IdentityWizard;
import com.subgraph.vega.ui.identity.identitywizard.IdentityWizardDialog;

public class CreateIdentity extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkspace workspace = Activator.getDefault().getModel().getCurrentWorkspace();
		if (workspace != null) {			
			IIdentityModel identityModel = workspace.getIdentityModel();
			IdentityWizard wizard = new IdentityWizard();
			IdentityWizardDialog dialog = new IdentityWizardDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), wizard);
			if (dialog.open() == IDialogConstants.OK_ID) {
				IIdentity identity = wizard.getIdentity();
				identityModel.store(identity);
			}
		}
		return null;
	}

}
