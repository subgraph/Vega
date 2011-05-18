package com.subgraph.vega.ui.http.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class TitleAreaConfigDialog extends TitleAreaDialog {

	private final IConfigDialogContent content;
	
	public TitleAreaConfigDialog(Shell parentShell, IConfigDialogContent content) {
		super(parentShell);
		this.content = content;
	}
	
	@Override 
	protected Point getInitialSize() {
		return content.getSize();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	public void create() {
		super.create();
		setTitle(content.getTitle());
		setMessage(content.getMessage());
	}

	@Override
	protected void okPressed() {
		content.onOk();
		super.okPressed();
	}

	@Override
	public boolean close() {
		content.onClose();
		return super.close();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);
		content.createContents(parent);
		return dialogAreaComposite;
	}
}
