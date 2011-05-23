package com.subgraph.vega.ui.scanner.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.subgraph.vega.ui.scanner.Activator;

public class ScannerOptionsPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public ScannerOptionsPreferencePage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Scanner Options");
	}

	@Override
	protected void createFieldEditors() {
		addField(new IntegerFieldEditor("MaxScanDescendants", "Maximum number of total path descendants", getFieldEditorParent()));
		addField(new IntegerFieldEditor("MaxScanChildren", "Maximum number child paths for a single node", getFieldEditorParent()));
		addField(new IntegerFieldEditor("MaxScanDepth", "Maximum path depth", getFieldEditorParent()));
		addField(new IntegerFieldEditor("MaxScanDuplicatePaths", "Maximum number of duplicate path elements", getFieldEditorParent()));
		addField(new IntegerFieldEditor("MaxAlertString", "Maximum length of strings to display in alert reports", getFieldEditorParent()));
		addField(new IntegerFieldEditor("MaxRequestsPerSecond", "Maximum number of requests per second to send", getFieldEditorParent()));
	}
}
