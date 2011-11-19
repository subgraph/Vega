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
		BooleanFieldEditor logRequestsField = new BooleanFieldEditor(IPreferenceConstants.P_LOG_ALL_REQUESTS, "Log all scanner requests", getFieldEditorParent());
		BooleanFieldEditor debugOutputField = new BooleanFieldEditor(IPreferenceConstants.P_DISPLAY_DEBUG_OUTPUT, "Display debug output in console", getFieldEditorParent());
		addField(logRequestsField);
		addField(debugOutputField);
	}
}
