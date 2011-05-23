package com.subgraph.vega.ui.scanner.preferences;


import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.subgraph.vega.ui.scanner.Activator;

public class ScannerDebugPreferencePage extends FieldEditorPreferencePage implements
	IWorkbenchPreferencePage {

	public ScannerDebugPreferencePage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Scanner Debugging Options");		
	}

	@Override
	protected void createFieldEditors() {
		BooleanFieldEditor logRequestsField = new BooleanFieldEditor("LogAllRequests", "Log all scanner requests", getFieldEditorParent());
		BooleanFieldEditor debugOutputField = new BooleanFieldEditor("DisplayDebugOutput", "Display debug output in console", getFieldEditorParent());
		addField(logRequestsField);
		addField(debugOutputField);
	}
}
