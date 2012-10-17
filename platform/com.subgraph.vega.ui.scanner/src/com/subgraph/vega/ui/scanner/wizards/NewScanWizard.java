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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.identity.IIdentity;
import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.api.scanner.modules.IScannerModule;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.util.images.ImageCache;

public class NewScanWizard extends Wizard {
	private final static String VEGA_LOGO = "icons/vega_small.png";
	
	private final ImageCache imageCache;
	private NewScanTargetPage targetPage;
	private NewScanModulesPage modulesPage;
	private NewScanAuthPage authPage;
	private NewScanParameterPage parameterPage;
	private ITargetScope scanTargetScope;
	private IIdentity scanIdentity;
	private List<String> cookieStringList;
	private Set<String> excludedParameterNames;
	private String targetFieldString;

	public NewScanWizard(String target, Collection<IIdentity> identities, List<IScannerModule> modules, Set<String> defaultExcludeParameters) {
		final IModel model = Activator.getDefault().getModel();
		imageCache = new ImageCache(Activator.PLUGIN_ID);
		final ImageDescriptor logo = ImageDescriptor.createFromImage(imageCache.get(VEGA_LOGO));
		
		targetPage = new NewScanTargetPage(model.getCurrentWorkspace(), target);
		targetPage.setImageDescriptor(logo);
		
		modulesPage = new NewScanModulesPage(modules);
		modulesPage.setImageDescriptor(logo);
		
		authPage = new NewScanAuthPage(identities);
		authPage.setImageDescriptor(logo);
		
		parameterPage = new NewScanParameterPage(defaultExcludeParameters);
		parameterPage.setImageDescriptor(logo);
	}
	
	@Override
	public void addPages() {
		addPage(targetPage);
		addPage(modulesPage);
		addPage(authPage);
		addPage(parameterPage);
	}
	
	@Override 
	public boolean canFinish() {
		return targetPage.isTargetValid();
	}
	
	@Override
	public boolean performFinish() {
		scanIdentity = authPage.getScanIdentity();
		cookieStringList = authPage.getCookieStringList();
		scanTargetScope = targetPage.getScanTargetScope();
		targetFieldString = targetPage.getUriTextIfValid();
		excludedParameterNames = parameterPage.getExcludedParameterNames();
		return scanTargetScope != null;
	}

	@Override
	public void dispose() {
		imageCache.dispose();
		super.dispose();
	}
	
	public ITargetScope getScanTargetScope() {
		return scanTargetScope;
	}

	public String getTargetField() {
		return targetFieldString;
	}
	
	public List<String> getCookieStringList() {
		return cookieStringList;
	}

	public IIdentity getScanIdentity() { 
		return scanIdentity;
	}
	
	public Set<String> getExcludedParameterNames() {
		return excludedParameterNames;
	}
}
