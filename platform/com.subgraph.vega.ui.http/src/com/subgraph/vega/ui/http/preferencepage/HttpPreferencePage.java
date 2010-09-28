package com.subgraph.vega.ui.http.preferencepage;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;

import com.subgraph.vega.internal.ui.http.Activator;

public class HttpPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public HttpPreferencePage() {
		super(GRID);
	}
	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Configuration parameters for the HTTP Proxy");

	}

	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		IntegerFieldEditor portField = new IntegerFieldEditor("ProxyPort","&Proxy port:",getFieldEditorParent());
		portField.setValidRange(1,65535);
		portField.setTextLimit(5);
		addField(portField);
		
	}

}
