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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
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
		final StringFieldEditor userAgent = new StringFieldEditor(IPreferenceConstants.P_USER_AGENT, "&User-Agent:", 60, getFieldEditorParent());
		addField(userAgent);
		addIntegerField(IPreferenceConstants.P_MAX_SCAN_DESCENDANTS, "Maximum number of total path descendants", 10, 100000);
		addIntegerField(IPreferenceConstants.P_MAX_SCAN_CHILDREN, "Maximum number child paths for a single node", 10, 100000);
		addIntegerField(IPreferenceConstants.P_MAX_SCAN_DEPTH, "Maximum path depth", 1, 10000);
		addIntegerField(IPreferenceConstants.P_MAX_SCAN_DUPLICATE_PATHS, "Maximum number of duplicate path elements", 2, 100);
		addIntegerField(IPreferenceConstants.P_MAX_ALERT_STRING, "Maximum length of strings to display in alert reports", 10, 100000);
		addIntegerField(IPreferenceConstants.P_MAX_REQUESTS_PER_SECOND, "Maximum number of requests per second to send", 1, 10000);
		addIntegerField(IPreferenceConstants.P_MAX_RESPONSE_LENGTH, "Maximum response size to process in kilobytes (0 for unlimited)", 0, 100000);
	}
	
	private void addIntegerField(String var, String description, int min, int max) {
		final Composite parent = getFieldEditorParent();
		final IntegerFieldEditor editor = new IntegerFieldEditor(var, description, parent);
		editor.setValidRange(min, max);
		addField(editor);
	}
}
