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
package com.subgraph.vega.application.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.subgraph.vega.application.Activator;

public class ProxyPreferenceInitializer extends
		AbstractPreferenceInitializer implements IPreferenceConstants {

	public ProxyPreferenceInitializer() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(P_SOCKS_ENABLED, false);
		store.setDefault(P_SOCKS_ADDRESS, "127.0.0.1");
		store.setDefault(P_SOCKS_PORT, 9050);
		store.setDefault(P_PROXY_ENABLED, false);
		store.setDefault(P_PROXY_ADDRESS, "127.0.0.1");
		store.setDefault(P_PROXY_PORT, 8080);
	}

	@Override
	public void initializeDefaultPreferences() {
	}
}
