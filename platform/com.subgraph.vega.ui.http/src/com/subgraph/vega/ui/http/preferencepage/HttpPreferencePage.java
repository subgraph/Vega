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
package com.subgraph.vega.ui.http.preferencepage;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;

import com.subgraph.vega.ui.http.Activator;

public class HttpPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public HttpPreferencePage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Configuration parameters for the HTTP Proxy");
	}

	@Override
	protected void createFieldEditors() {
		final IntegerFieldEditor portField = new IntegerFieldEditor(PreferenceConstants.P_PROXY_PORT, "&Proxy port:", getFieldEditorParent());
		portField.setValidRange(1, 65535);
		portField.setTextLimit(5);
		addField(portField);

		final StringFieldEditor userAgent = new StringFieldEditor(PreferenceConstants.P_USER_AGENT, "Default &User-Agent:", 60, getFieldEditorParent());
		addField(userAgent);

		final BooleanFieldEditor userAgentOverride = new BooleanFieldEditor(PreferenceConstants.P_USER_AGENT_OVERRIDE, "Override client User-Agent", getFieldEditorParent());
		addField(userAgentOverride);

		final BooleanFieldEditor cacheBrowserDisable = new BooleanFieldEditor(PreferenceConstants.P_DISABLE_BROWSER_CACHE, "Prevent browser caching", getFieldEditorParent());
		addField(cacheBrowserDisable);

		final BooleanFieldEditor cacheProxyDisable = new BooleanFieldEditor(PreferenceConstants.P_DISABLE_PROXY_CACHE, "Prevent intermediate (proxy) caching", getFieldEditorParent());
		addField(cacheProxyDisable);
		
		final BooleanFieldEditor configPopup = new BooleanFieldEditor(PreferenceConstants.P_CONFIG_POPUP, "Use Popup style configuration dialogs", getFieldEditorParent());
		addField(configPopup);
	}

}
