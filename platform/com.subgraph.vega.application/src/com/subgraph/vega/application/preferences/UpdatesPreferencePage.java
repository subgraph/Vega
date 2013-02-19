package com.subgraph.vega.application.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.subgraph.vega.application.Activator;

public class UpdatesPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public UpdatesPreferencePage() {
		super(GRID);
	}


	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Automatic Update Check");

	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(IPreferenceConstants.P_UPDATE_CHECK_ENABLED, "Automatically check for updates", getFieldEditorParent()));
		
		
	}

}
