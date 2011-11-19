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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.ui.http.Activator;

public class HttpPreferenceInitializer extends AbstractPreferenceInitializer implements IPreferenceConstants {

	public HttpPreferenceInitializer() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(P_USER_AGENT, IHttpRequestEngineFactory.DEFAULT_USER_AGENT);
		store.setDefault(P_USER_AGENT_OVERRIDE, false);
		store.setDefault(P_DISABLE_BROWSER_CACHE, false);
		store.setDefault(P_DISABLE_PROXY_CACHE, false);
		store.setDefault(P_PROXY_LISTENERS, "[127.0.0.1]:8888");
	}

	@Override
	public void initializeDefaultPreferences() {
	}

}
