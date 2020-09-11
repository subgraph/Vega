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
import org.eclipse.jface.preference.IPreferenceStore;

import com.subgraph.vega.api.scanner.IScan;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.ui.scanner.preferences.IPreferenceConstants;

/*---new imports---*/
import com.subgraph.vega.api.util.UriTools;
//import com.subgraph.vega.export.Activator;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import com.subgraph.vega.api.model.identity.*;

public class MyScanExecutor {
		
	private boolean scanRunning = false;
	private String target = "127.0.0.1";
	private String userAgent = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; InfoPath.1; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; Vega/1.0";
	private List<String> cookieList = new ArrayList<String>();
	private List<String> excludedParameters = Arrays.asList("csrfmiddlewaretoken","__viewstateencrypted","__eventvalidation",
			"__eventtarget","__viewstate","xsrftoken","csrftoken","anticsrf","__eventargument");
	private String identity = "";
	private boolean logAllRequests = false;
	private boolean displayDebugOutput = false;
	private int maxRequestsPerSecond = 25;
	private int maxScanDescendants = 8192;
	private int maxScanChildren = 512;
	private int maxScanDepth = 16;
	private int maxScanDuplicatePaths = 3;
	private int maxResponseLength = 1024;
	private boolean useAllModules = false;
	
	
	public boolean isUseAllModules() {
		return useAllModules;
	}

	public void setUseAllModules(boolean useAllModules) {
		this.useAllModules = useAllModules;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public List<String> getCookieList() {
		return cookieList;
	}

	public void setCookieList(List<String> cookieList) {
		this.cookieList = cookieList;
	}

	public List<String> getExcludedParameters() {
		return excludedParameters;
	}

	public void setExcludedParameters(List<String> excludedParameters) {
		this.excludedParameters = excludedParameters;
	}

	public String getIdentity() {
		return identity;
	}

	public boolean setIdentity(String name, String username, String password) {
		
		IIdentityModel identityModel = Activator.getDefault().getModel().getCurrentWorkspace().getIdentityModel();
		IIdentity myIdentity = identityModel.createIdentity();
		
		myIdentity.setName(name);
		IAuthMethodRfc2617 myAuthMethod = Activator.getDefault().getModel().getCurrentWorkspace().getIdentityModel().createAuthMethodRfc2617();
		myAuthMethod.setAuthScheme(IAuthMethodRfc2617.AuthScheme.AUTH_SCHEME_BASIC);
		myAuthMethod.setUsername(username);
		myAuthMethod.setPassword(password);
		myIdentity.setAuthMethod(myAuthMethod);
		
		IIdentity checkIdentity = identityModel.getIdentityByName(name);
		if(checkIdentity == null)
		{
			//no identity like this exists
			identityModel.store(myIdentity);
			this.identity = name;
			System.out.println("Identity stored.");
		} else
		{
			IAuthMethod checkAuthMethod = checkIdentity.getAuthMethod();
			//assert: we only have basic authentication (only type available for python)
			if(checkAuthMethod.getType() == IAuthMethod.AuthMethodType.AUTH_METHOD_RFC2617)
			{
				IAuthMethodRfc2617 checkAuthMethod_basic = (IAuthMethodRfc2617) checkAuthMethod;
				if(checkAuthMethod_basic.getUsername().equals(username) && 
						checkAuthMethod_basic.getPassword().equals(password))
				{
					//exact same identity is already stored and can be used.
					System.out.println("Using existing idenity.");
					this.identity = name;
				}else
				{
					//identity with same name but different credentials already exists
					System.out.println("A differing identity with this name already exists (needs to be unique)!");
					return false;
				}
			}
		}				
		
		return true;
	}

	public boolean isLogAllRequests() {
		return logAllRequests;
	}

	public void setLogAllRequests(boolean logAllRequests) {
		this.logAllRequests = logAllRequests;
	}

	public boolean isDisplayDebugOutput() {
		return displayDebugOutput;
	}

	public void setDisplayDebugOutput(boolean displayDebugOutput) {
		this.displayDebugOutput = displayDebugOutput;
	}

	public int getMaxRequestsPerSecond() {
		return maxRequestsPerSecond;
	}

	public void setMaxRequestsPerSecond(int maxRequestsPerSecond) {
		this.maxRequestsPerSecond = maxRequestsPerSecond;
	}

	public int getMaxScanDescendants() {
		return maxScanDescendants;
	}

	public void setMaxScanDescendants(int maxScanDescendants) {
		this.maxScanDescendants = maxScanDescendants;
	}

	public int getMaxScanChildren() {
		return maxScanChildren;
	}

	public void setMaxScanChildren(int maxScanChildren) {
		this.maxScanChildren = maxScanChildren;
	}

	public int getMaxScanDepth() {
		return maxScanDepth;
	}

	public void setMaxScanDepth(int maxScanDepth) {
		this.maxScanDepth = maxScanDepth;
	}

	public int getMaxScanDuplicatePaths() {
		return maxScanDuplicatePaths;
	}

	public void setMaxScanDuplicatePaths(int maxScanDuplicatePaths) {
		this.maxScanDuplicatePaths = maxScanDuplicatePaths;
	}

	public int getMaxResponseLength() {
		return maxResponseLength;
	}

	public void setMaxResponseLength(int maxResponseLength) {
		this.maxResponseLength = maxResponseLength;
	}

	public String runScan() {
		final IScanner scanner = Activator.getDefault().getScanner();
		final IScan scan = scanner.createScan();
		//final Collection<IIdentity> identities = Activator.getDefault().getModel().getCurrentWorkspace().getIdentityModel().getAllIdentities();
		String result = null;
		if(scanRunning) {
			scan.stopScan();
			System.out.println("Error. Tried starting a scan but there was already a scan running.");
		} else {
			result = maybeLaunchScanFromWizard(scanner, scan);
		}
		IScanInstance scanInstance = scan.getScanInstance();
		if(scanInstance.getScanStatus() == IScanInstance.SCAN_AUDITING || scanInstance.getScanStatus() == IScanInstance.SCAN_PROBING ||
				scanInstance.getScanStatus() == IScanInstance.SCAN_STARTING)
		{
			waitForScanToFinish(scanInstance);
		}
		
		return result;
	}
	
	private void waitForScanToFinish(IScanInstance s)
	{
		while(s.getScanStatus() == IScanInstance.SCAN_AUDITING || s.getScanStatus() == IScanInstance.SCAN_PROBING ||
					s.getScanStatus() == IScanInstance.SCAN_STARTING)
		{
			try{
				Thread.sleep(1000);
			}catch(InterruptedException e)
			{
				break;
			}
		}
	}
	
	private String maybeLaunchScanFromWizard(IScanner scanner, IScan scan) {
		
		/*---new code---*/

		//--------scan target--------------//

		//final ITargetScope scanTargetScope = wizard.getScanTargetScope();
		//if(scanTargetScope == null) {
		//	return null;
		//}

		ITargetScope scanTargetScope;
		scanTargetScope = Activator.getDefault().getModel().getCurrentWorkspace().getTargetScopeManager().createNewScope();
		scanTargetScope.clear();
		if(UriTools.isTextValidURI(target)) {
			scanTargetScope.addScopeURI(UriTools.getURIFromText(target));
		}
		System.out.println("Using target "+ target);

		final IScannerConfig config = scan.getConfig();
		config.setScanTargetScope(scanTargetScope);
		config.setUserAgent(userAgent);
		System.out.println("Using userAgent "+ userAgent);

		//--------cookies--------//

		//config.setCookieList(getCookieListForScope(wizard.getCookieStringList(), scanTargetScope));

		config.setCookieList(getCookieListForScope(cookieList, scanTargetScope));
		System.out.println("Using cookieList: ");
		for(int i = 0; i < cookieList.size(); i++)
		{
			System.out.print(cookieList.get(i) + ", ");
		}
		System.out.print("\n");

		//-------identity-------//

		//if no fitting identity has been found, the identity in the config will be null.
		// This is the same as it has been before.

		// config.setScanIdentity(wizard.getScanIdentity());


		final Collection<IIdentity> identities = Activator.getDefault().getModel().getCurrentWorkspace().getIdentityModel().getAllIdentities();
		IIdentity myid = null;
		for(IIdentity id : identities)
		{
			if(id.getName().equals(identity)){
				myid = id;
				break;
			}			
		}
		if(!(myid == null))
		{
			System.out.println("Using id "+ myid.getName());
		}
		
		config.setScanIdentity(myid);

		//-------excluded parameters-------//

		//config.setExcludedParameterNames(wizard.getExcludedParameterNames());	
		
		Set<String> excludedParametersSet = new HashSet<String>();
		for(int i = 0; i < excludedParameters.size(); i++)
		{
			excludedParametersSet.add(excludedParameters.get(i));
		}

		config.setExcludedParameterNames(excludedParametersSet);
		
		
		final IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		config.setLogAllRequests(logAllRequests);
		config.setDisplayDebugOutput(displayDebugOutput);
		config.setMaxRequestsPerSecond(maxRequestsPerSecond);
		config.setMaxDescendants(maxScanDescendants);
		config.setMaxChildren(maxScanChildren);
		config.setMaxDepth(maxScanDepth);
		config.setMaxDuplicatePaths(maxScanDuplicatePaths);
		config.setMaxResponseKilobytes(maxResponseLength);
		
		if(useAllModules)
		{
			scan.useAllModules();			
		}

		
		//end of changes

		final Thread probeThread = new Thread(new ScanProbeTask(scan));
		probeThread.start();
		synchronized (probeThread) {			
			try{
				probeThread.wait();
			}catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		return target;
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
