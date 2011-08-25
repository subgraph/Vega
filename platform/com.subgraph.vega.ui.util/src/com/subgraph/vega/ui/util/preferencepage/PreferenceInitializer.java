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
package com.subgraph.vega.ui.util.preferencepage;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.subgraph.vega.ui.util.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer implements IPreferenceConstants {

	public PreferenceInitializer() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(P_CONFIG_POPUP, true);
	}

	@Override
	public void initializeDefaultPreferences() {
	}

}
