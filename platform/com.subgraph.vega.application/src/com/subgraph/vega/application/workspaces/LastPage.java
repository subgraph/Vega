package com.subgraph.vega.application.workspaces;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LastPage extends WizardPage {
	
	LastPage() {
		super("lastPage");
		setTitle("New Workspace");
		setDescription("Finish creating the Workspace.");
	}

	public void createControl(Composite parent) {
		final Composite container = createComposite(parent);
	    createBanner(container, "Subgraph Vega will now restart with the newly created Workspace.");
		setPageComplete(true);
	}
	
	private void createBanner(Composite container, String text) {
		final Label label = new Label(container, SWT.NONE);
		final GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;		
		label.setLayoutData(gd);
		label.setText(text);
	}
	
	private Composite createComposite(Composite parent) {
		final Composite c = new Composite(parent, SWT.NONE);
	    final GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 2;
	    gridLayout.verticalSpacing = 20;
	    c.setLayout(gridLayout);
	    setControl(c);
	    return c;
	}
}