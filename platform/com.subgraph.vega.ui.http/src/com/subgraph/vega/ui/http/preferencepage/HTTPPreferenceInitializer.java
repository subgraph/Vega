package com.subgraph.vega.ui.http.preferencepage;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.subgraph.vega.ui.http.Activator;

public class HTTPPreferenceInitializer extends AbstractPreferenceInitializer {

	public HTTPPreferenceInitializer() {
		// TODO Auto-generated constructor stub
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault("ProxyPort",8888);
		
	}

	@Override
	public void initializeDefaultPreferences() {
		// TODO Auto-generated method stub

	}

}
