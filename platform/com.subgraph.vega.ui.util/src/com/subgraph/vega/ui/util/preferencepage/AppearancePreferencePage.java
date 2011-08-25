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
package com.subgraph.vega.ui.util.preferencepage;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.subgraph.vega.ui.util.Activator;

public class AppearancePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage, IPreferenceConstants {

	public AppearancePreferencePage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("General Appearance Configuration");
	}

	@Override
	protected void createFieldEditors() {
		final BooleanFieldEditor configPopup = new BooleanFieldEditor(P_CONFIG_POPUP, "Use Popup style configuration dialogs", getFieldEditorParent());
		addField(configPopup);
	}

}
