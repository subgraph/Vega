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
package com.subgraph.vega.ui.scanner.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.subgraph.vega.ui.scanner.Activator;


public class ScannerDebugPreferenceInitializer extends AbstractPreferenceInitializer {

	public ScannerDebugPreferenceInitializer() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(IPreferenceConstants.P_LOG_ALL_REQUESTS, false);
		store.setDefault(IPreferenceConstants.P_DISPLAY_DEBUG_OUTPUT, false);
	}

	@Override
	public void initializeDefaultPreferences() {
	}
}
