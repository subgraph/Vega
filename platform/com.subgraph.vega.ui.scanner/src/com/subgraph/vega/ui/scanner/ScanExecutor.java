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
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.ui.scanner.wizards.NewScanWizard;
import com.subgraph.vega.ui.scanner.wizards.NewWizardDialog;

public class ScanExecutor {
	
	public String runScan(Shell shell, String target) {
		final IScanner scanner = Activator.getDefault().getScanner();

		NewScanWizard wizard = new NewScanWizard();
		if(target != null) {
			wizard.setTargetField(target);
		}
		wizard.setScannerModules(scanner.getAllModules());
		
		WizardDialog dialog = new NewWizardDialog(shell, wizard);
		if(dialog.open() == IDialogConstants.OK_ID) {
			if(wizard.isDomTest()) {
				runDomTest();
				return null;
			}
			return maybeLaunchScanFromWizard(shell, wizard, scanner);
		}
		return null;
	}

	
	private String maybeLaunchScanFromWizard(Shell shell, NewScanWizard wizard, IScanner scanner) {
		URI targetURI = wizard.getScanHostURI();
		if(targetURI == null) {
			return null;
		}

		scanner.lock();
		final IScannerConfig config = scanner.createScannerConfig();
		config.setBaseURI(targetURI);
		config.setCookieList(getCookieList(wizard.getCookieStringList(), targetURI));
		config.setBasicUsername(wizard.getBasicUsername());
		config.setBasicPassword(wizard.getBasicPassword());
		config.setBasicRealm(wizard.getBasicRealm());
		config.setBasicDomain(wizard.getBasicDomain());
		config.setExclusions(wizard.getExclusions());
		config.setNtlmUsername(wizard.getNtlmUsername());
		config.setNtlmPassword(wizard.getNtlmPassword());
		final IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		config.setLogAllRequests(preferences.getBoolean("LogAllRequests"));
		config.setDisplayDebugOutput(preferences.getBoolean("DisplayDebugOutput"));
		config.setMaxRequestsPerSecond(preferences.getInt("MaxRequestsPerSecond"));
		config.setMaxDescendants(preferences.getInt("MaxScanDescendants"));
		config.setMaxChildren(preferences.getInt("MaxScanChildren"));
		config.setMaxDepth(preferences.getInt("MaxScanDepth"));
		config.setMaxDuplicatePaths(preferences.getInt("MaxScanDuplicatePaths"));
		config.setMaxResponseKilobytes(preferences.getInt("MaxResponseLength"));
		scanner.setScannerConfig(config);

		final Thread probeThread = new Thread(new ScanProbeTask(shell, targetURI, scanner, config));
		probeThread.start();

		return wizard.getTargetField();
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
		return null;
	}
	
	private void runDomTest() {
		IScanner scanner = Activator.getDefault().getScanner();
		if(scanner != null) {
			scanner.runDomTests();
		}
	}
}
