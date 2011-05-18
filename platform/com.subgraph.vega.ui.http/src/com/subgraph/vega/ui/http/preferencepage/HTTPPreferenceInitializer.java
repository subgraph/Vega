package com.subgraph.vega.ui.http.preferencepage;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.subgraph.vega.ui.http.Activator;

public class HTTPPreferenceInitializer extends AbstractPreferenceInitializer {

	public HTTPPreferenceInitializer() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_PROXY_PORT, 8888);
		store.setDefault(PreferenceConstants.P_USER_AGENT, "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; InfoPath.1; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; Vega/1.0");
		store.setDefault(PreferenceConstants.P_USER_AGENT_OVERRIDE, false);
		store.setDefault(PreferenceConstants.P_DISABLE_BROWSER_CACHE, false);
		store.setDefault(PreferenceConstants.P_DISABLE_PROXY_CACHE, false);
		store.setDefault(PreferenceConstants.P_CONFIG_POPUP, true);
	}

	@Override
	public void initializeDefaultPreferences() {
	}

}
