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

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class IdentityWizardDialog extends WizardDialog {
	
	public IdentityWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
		addPageChangedListener(createPageChangedListener());
	}

	private IPageChangedListener createPageChangedListener() {
		return new IPageChangedListener() {
			@Override
			public void pageChanged(PageChangedEvent event) {
				Object page = event.getSelectedPage();
				if (page instanceof IIdentityWizardPage) {
					((IIdentityWizardPage)page).pageSelected();
				}
			}
		};
	}
	
}
