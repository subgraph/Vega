package com.subgraph.vega.ui.scanner.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.subgraph.vega.ui.scanner.Activator;


public class ScannerDebugPreferenceInitializer extends AbstractPreferenceInitializer {

	public ScannerDebugPreferenceInitializer() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault("LogAllRequests", false);
		store.setDefault("DisplayDebugOutput", false);
	}

	@Override
	public void initializeDefaultPreferences() {
		// TODO Auto-generated method stub
		
	}

}
