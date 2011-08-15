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

import com.subgraph.vega.ui.http.Activator;

public class HttpPreferenceInitializer extends AbstractPreferenceInitializer implements IPreferenceConstants {

	public HttpPreferenceInitializer() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(P_USER_AGENT, "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; InfoPath.1; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; Vega/1.0");
		store.setDefault(P_USER_AGENT_OVERRIDE, false);
		store.setDefault(P_DISABLE_BROWSER_CACHE, false);
		store.setDefault(P_DISABLE_PROXY_CACHE, false);
		store.setDefault(P_CONFIG_POPUP, true);
		store.setDefault(P_PROXY_LISTENERS, "[127.0.0.1]:8888");
	}

	@Override
	public void initializeDefaultPreferences() {
	}

}
