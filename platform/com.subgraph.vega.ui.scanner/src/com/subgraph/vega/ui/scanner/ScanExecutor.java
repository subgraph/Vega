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
		IScanner scanner = Activator.getDefault().getScanner();

		NewScanWizard wizard = new NewScanWizard();
		if(target != null)
			wizard.setTargetField(target);
		wizard.setScannerModules(scanner.getAllModules());
		
		if(scanner != null) {
			IScannerConfig scannerConfig = scanner.createScannerConfig();
		
			WizardDialog dialog = new NewWizardDialog(shell, wizard);
			if(dialog.open() == IDialogConstants.OK_ID) {
				if(wizard.isDomTest()) {
					runDomTest();
					return null;
				}
				
				URI uri = wizard.getScanHostURI();
				if(uri != null) {
					scannerConfig.setBaseURI(uri);
					scannerConfig.setCookieList(getCookieList(wizard.getCookieStringList(), uri));
					scannerConfig.setBasicUsername(wizard.getBasicUsername());
					scannerConfig.setBasicPassword(wizard.getBasicPassword());
					scannerConfig.setBasicRealm(wizard.getBasicRealm());
					scannerConfig.setBasicDomain(wizard.getBasicDomain());
					scannerConfig.setExclusions(wizard.getExclusions());
					scannerConfig.setNtlmUsername(wizard.getNtlmUsername());
					scannerConfig.setNtlmPassword(wizard.getNtlmPassword());
					final IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
					scannerConfig.setLogAllRequests(preferences.getBoolean("LogAllRequests"));
					scannerConfig.setDisplayDebugOutput(preferences.getBoolean("DisplayDebugOutput"));
					scannerConfig.setMaxRequestsPerSecond(preferences.getInt("MaxRequestsPerSecond"));
					scannerConfig.setMaxDescendants(preferences.getInt("MaxScanDescendants"));
					scannerConfig.setMaxChildren(preferences.getInt("MaxScanChildren"));
					scannerConfig.setMaxDepth(preferences.getInt("MaxScanDepth"));
					scannerConfig.setMaxDuplicatePaths(preferences.getInt("MaxScanDuplicatePaths"));
					scanner.setScannerConfig(scannerConfig);
					scanner.startScanner(scannerConfig);
					return wizard.getTargetField();
				}
			}
		}
		return null;
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
