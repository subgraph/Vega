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

import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.ui.scanner.Activator;

public class ScannerOptionsPreferenceInitializer extends
		AbstractPreferenceInitializer {

	public ScannerOptionsPreferenceInitializer() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(IPreferenceConstants.P_USER_AGENT, IHttpRequestEngineFactory.DEFAULT_USER_AGENT);
		store.setDefault(IPreferenceConstants.P_MAX_SCAN_DESCENDANTS, IScannerConfig.DEFAULT_MAX_DESCENDANTS);
		store.setDefault(IPreferenceConstants.P_MAX_SCAN_CHILDREN, IScannerConfig.DEFAULT_MAX_CHILDREN);
		store.setDefault(IPreferenceConstants.P_MAX_SCAN_DEPTH, IScannerConfig.DEFAULT_MAX_DEPTH);
		store.setDefault(IPreferenceConstants.P_MAX_SCAN_DUPLICATE_PATHS, IScannerConfig.DEFAULT_MAX_DUPLICATE_PATHS);
		store.setDefault(IPreferenceConstants.P_MAX_REQUESTS_PER_SECOND, IScannerConfig.DEFAULT_MAX_REQUEST_PER_SECOND);
		store.setDefault(IPreferenceConstants.P_MAX_ALERT_STRING, 400);
		store.setDefault(IPreferenceConstants.P_MAX_RESPONSE_LENGTH, IScannerConfig.DEFAULT_MAX_RESPONSE_KILOBYTES);
	}

	@Override
	public void initializeDefaultPreferences() {
	}
}
