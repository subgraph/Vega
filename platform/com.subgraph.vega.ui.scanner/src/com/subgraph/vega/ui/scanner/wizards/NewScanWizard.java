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
package com.subgraph.vega.ui.scanner.wizards;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import com.subgraph.vega.api.model.identity.IIdentity;
import com.subgraph.vega.api.scanner.modules.IScannerModule;

public class NewScanWizard extends Wizard {
	private NewScanWizardPage page1;
	private NewScanWizardPage2 page2;
	private URI scanHostURI;
	private String validTargetField;
	private boolean isDomTest;
	private IIdentity scanIdentity;
	private List<String> cookieStringList;
	private List<String> exclusions;

	public NewScanWizard(String target, Collection<IIdentity> identities, List<IScannerModule> modules) {
		page1 = new NewScanWizardPage(target, identities, modules);
		page2 = new NewScanWizardPage2();
	}
	
	@Override
	public void addPages() {
		addPage(page1);
		addPage(page2);
	}
	
	@Override 
	public boolean canFinish() {
		final String scanHostText = page1.getScanTarget().trim();
		if(scanHostText.isEmpty()) {
			page1.setErrorMessage(null);
			return false;
		}

		final URI target = createTargetURI(scanHostText);
		if(target == null) {
			page1.setErrorMessage("Target entered is not a valid host or URL");
			return false;
		} else {
			page1.setErrorMessage(null);
			return true;
		}
	}
	
	@Override
	public boolean performFinish() {
		String target = page1.getScanTarget();
		if (target.equals("domtest")) {
			isDomTest = true;
			return true;
		}
		
		scanIdentity = page1.getScanIdentity();
		cookieStringList = page2.getCookieStringList();
		exclusions = page2.getExclusions();

		scanHostURI = createTargetURI(target);
		if (scanHostURI != null) {
			validTargetField = target;
		}

		return (scanHostURI != null);		
	}
	
	public URI getScanHostURI() {
		return scanHostURI;
	}

	public String getTargetField() {
		return validTargetField;
	}
	
	public List<String> getCookieStringList() {
		return cookieStringList;
	}

	public boolean isDomTest() {
		return isDomTest;	
	}

	public IIdentity getScanIdentity() { 
		return scanIdentity;
	}
	
	public List<String> getExclusions() {
		return exclusions;
	}
	
	URI createTargetURI(String value) {
		if (!(value.startsWith("http://") || value.startsWith("https://"))) {
			value = "http://" + value;
		}
		try {
			return new URI(value);
		} catch (URISyntaxException e) {
			return null;
		}
	}

}
