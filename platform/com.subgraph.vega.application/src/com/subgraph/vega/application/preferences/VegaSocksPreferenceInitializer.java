package com.subgraph.vega.application.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.subgraph.vega.application.Activator;

public class VegaSocksPreferenceInitializer extends
		AbstractPreferenceInitializer implements IVegaSocksPreferenceConstants {

	public VegaSocksPreferenceInitializer() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(P_SOCKS_ENABLED, false);
		store.setDefault(P_SOCKS_ADDRESS, "127.0.0.1");
		store.setDefault(P_SOCKS_PORT, 9050);
	}

	@Override
	public void initializeDefaultPreferences() {
	}
}
