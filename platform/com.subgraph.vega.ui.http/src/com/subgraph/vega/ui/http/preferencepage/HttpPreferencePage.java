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

import com.subgraph.vega.ui.http.Activator;

public class HttpPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage, IPreferenceConstants {

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
		final StringFieldEditor userAgent = new StringFieldEditor(P_USER_AGENT, "Default &User-Agent:", 60, getFieldEditorParent());
		addField(userAgent);

		final BooleanFieldEditor userAgentOverride = new BooleanFieldEditor(P_USER_AGENT_OVERRIDE, "Override client User-Agent", getFieldEditorParent());
		addField(userAgentOverride);

		final BooleanFieldEditor cacheBrowserDisable = new BooleanFieldEditor(P_DISABLE_BROWSER_CACHE, "Prevent browser caching", getFieldEditorParent());
		addField(cacheBrowserDisable);

		final BooleanFieldEditor cacheProxyDisable = new BooleanFieldEditor(P_DISABLE_PROXY_CACHE, "Prevent intermediate (proxy) caching", getFieldEditorParent());
		addField(cacheProxyDisable);
	}

}
