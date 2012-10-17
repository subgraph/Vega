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
package com.subgraph.vega.ui.scanner;

import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.subgraph.vega.api.scanner.IScan;
import com.subgraph.vega.api.model.identity.IIdentity;
import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.ui.scanner.preferences.IPreferenceConstants;
import com.subgraph.vega.ui.scanner.wizards.NewScanWizard;
import com.subgraph.vega.ui.scanner.wizards.NewWizardDialog;

public class ScanExecutor {
	
	public String runScan(Shell shell, String target) {
		final IScanner scanner = Activator.getDefault().getScanner();
		final IScan scan = scanner.createScan();
		final Collection<IIdentity> identities = Activator.getDefault().getModel().getCurrentWorkspace().getIdentityModel().getAllIdentities();

		NewScanWizard wizard = new NewScanWizard(target, identities, scan.getModuleList(), scan.getConfig().getDefaultExcludedParameterNames());
		WizardDialog dialog = new NewWizardDialog(shell, wizard);
		if(dialog.open() == IDialogConstants.OK_ID) {
			return maybeLaunchScanFromWizard(shell, wizard, scanner, scan);
		} else {
			// REVISIT: delete the scan so the ID can be used in the future?
			scan.stopScan(); // stop to unlock workspace
		}
		return null;
	}
	
	private String maybeLaunchScanFromWizard(Shell shell, NewScanWizard wizard, IScanner scanner, IScan scan) {

		final ITargetScope scanTargetScope = wizard.getScanTargetScope();
		if(scanTargetScope == null) {
			return null;
		}

		final IScannerConfig config = scan.getConfig();
		config.setScanTargetScope(scanTargetScope);
		config.setUserAgent(IPreferenceConstants.P_USER_AGENT);
		config.setCookieList(getCookieListForScope(wizard.getCookieStringList(), scanTargetScope));
		config.setScanIdentity(wizard.getScanIdentity());
		config.setExcludedParameterNames(wizard.getExcludedParameterNames());
		final IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		config.setLogAllRequests(preferences.getBoolean(IPreferenceConstants.P_LOG_ALL_REQUESTS));
		config.setDisplayDebugOutput(preferences.getBoolean(IPreferenceConstants.P_DISPLAY_DEBUG_OUTPUT));
		config.setMaxRequestsPerSecond(preferences.getInt(IPreferenceConstants.P_MAX_REQUESTS_PER_SECOND));
		config.setMaxDescendants(preferences.getInt(IPreferenceConstants.P_MAX_SCAN_DESCENDANTS));
		config.setMaxChildren(preferences.getInt(IPreferenceConstants.P_MAX_SCAN_CHILDREN));
		config.setMaxDepth(preferences.getInt(IPreferenceConstants.P_MAX_SCAN_DEPTH));
		config.setMaxDuplicatePaths(preferences.getInt(IPreferenceConstants.P_MAX_SCAN_DUPLICATE_PATHS));
		config.setMaxResponseKilobytes(preferences.getInt(IPreferenceConstants.P_MAX_RESPONSE_LENGTH));

		final Thread probeThread = new Thread(new ScanProbeTask(shell, scan));
		probeThread.start();

		return wizard.getTargetField();
	}

	private List<Cookie> getCookieListForScope(List<String> cookieStringList, ITargetScope scope) {
		final List<Cookie> cookies = new ArrayList<Cookie>();
		for(URI uri: scope.getScopeURIs()) {
			cookies.addAll(getCookieList(cookieStringList, uri));
		}
		return cookies;
	}
	
	// gross hack
	private List<Cookie> getCookieList(List<String> cookieStringList, URI uri) {
		if (cookieStringList.size() != 0) {
			ArrayList<Cookie> cookieList = new ArrayList<Cookie>(cookieStringList.size());
			for (String cookieString: cookieStringList) {
				List<HttpCookie> parseList = HttpCookie.parse(cookieString);
				for (HttpCookie cookie: parseList) {
					BasicClientCookie cp = new BasicClientCookie(cookie.getName(), cookie.getValue());
					cp.setComment(cookie.getComment());
					if (cookie.getDomain() != null) {
						cp.setDomain(cookie.getDomain());
					} else {
						// just set it to the target host for now - may need something slightly less specific
						cp.setDomain(uri.getHost());
					}
					long maxAge = cookie.getMaxAge();
					if (maxAge > 0) {
						Calendar calendar = Calendar.getInstance();
						calendar.add(Calendar.SECOND, (int) maxAge);
						cp.setExpiryDate(calendar.getTime());
					}
					cp.setPath(cookie.getPath());
					cp.setSecure(cookie.getSecure());
					cp.setVersion(cookie.getVersion());
					cookieList.add(cp);
				}
			}
			return cookieList;
		}
		return Collections.emptyList();
	}
}
