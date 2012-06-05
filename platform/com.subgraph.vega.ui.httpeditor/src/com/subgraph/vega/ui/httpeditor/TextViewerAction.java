package com.subgraph.vega.ui.httpeditor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;

public class TextViewerAction extends Action {
	private final int operationCode;
	private final ITextOperationTarget operationTarget;
	
	TextViewerAction(ITextViewer viewer, int operationCode) {
		this.operationCode = operationCode;
		this.operationTarget = viewer.getTextOperationTarget();
		
	}
	
	public void run() {
		if(operationTarget != null) {
			operationTarget.doOperation(operationCode);
		}
	}
	
	public void update() {
		final boolean wasEnabled = isEnabled();
		final boolean isEnabled = (operationTarget != null && operationTarget.canDoOperation(operationCode));
		setEnabled(isEnabled);
		if(wasEnabled != isEnabled) {
			firePropertyChange(ENABLED, wasEnabled, isEnabled);
		}
	}
	
	void configureAction(String text, String tooltipText, String description) {
		setText(text);
		setToolTipText(tooltipText);
		setDescription(description);
	}
	

}
