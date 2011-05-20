package com.subgraph.vega.ui.scanner.wizards;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class NewWizardDialog extends WizardDialog {

	public NewWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
		setShellStyle(SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER | SWT.MODELESS | SWT.RESIZE | getDefaultOrientation());
	}

}
