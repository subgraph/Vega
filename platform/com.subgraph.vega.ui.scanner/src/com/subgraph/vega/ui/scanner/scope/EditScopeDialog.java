package com.subgraph.vega.ui.scanner.scope;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.ui.scanner.Activator;

public class EditScopeDialog extends Dialog {

	public EditScopeDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(500,600);
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		createDialogPanel(area);
		return area;
	}
	
	private void createDialogPanel(Composite parent) {
		
		final Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createLabel(panel);
		final IWorkspace workspace = Activator.getDefault().getModel().getCurrentWorkspace();
		ScopeSelector scopeSelector = new ScopeSelector(panel, workspace.getTargetScopeManager());
		scopeSelector.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		Composite c1 = new BasePathWidget(panel, scopeSelector.getViewer());
		c1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		Composite c2 = new ExcludeWidget(panel, scopeSelector.getViewer());
		c2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		panel.setLayout(new GridLayout());
	}
	
	private void createLabel(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText("Target Scope");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

}
