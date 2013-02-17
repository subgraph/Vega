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
package com.subgraph.vega.ui.http.proxy;

import java.util.List;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.subgraph.vega.api.scanner.modules.IScannerModule;
import com.subgraph.vega.ui.util.dialogs.IConfigDialogContent;
import com.subgraph.vega.ui.util.modules.ModuleRegistryCheckStateProvider;
import com.subgraph.vega.ui.util.modules.ModuleRegistryContentProvider;

public class ConfigureProxyModulesContent implements IConfigDialogContent {
	private final List<IScannerModule> modules;
	private Composite composite;
	private CheckboxTreeViewer viewer;
	
	public ConfigureProxyModulesContent(List<IScannerModule> modules) {
		this.modules = modules;
	}

	@Override
	public Composite createContents(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout();
		layout.marginHeight = 10;
		layout.verticalSpacing = 20;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		viewer = new CheckboxTreeViewer(composite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		final ModuleRegistryCheckStateProvider checkStateProvider = new ModuleRegistryCheckStateProvider(viewer);
		viewer.setContentProvider(new ModuleRegistryContentProvider(checkStateProvider));
		viewer.setLabelProvider(new ConfigureProxyModulesLabelProvider());
		viewer.setCheckStateProvider(checkStateProvider);
		
		viewer.setInput(modules.toArray(new IScannerModule[0]));

		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 300;
		gd.widthHint = 500;
		viewer.getTree().setLayoutData(gd);
		
		viewer.addCheckStateListener(checkStateProvider);
		composite.layout();
		return composite;
	}

	@Override
	public String getTitle() {
		return "Configure enabled modules for proxy";
	}

	@Override
	public String getMessage() {
		return "Select which vulnerability modules to enable in the Proxy. Injection modules only run against in-scope targets when proxy scanning is enabled.";
	}

	@Override
	public Control getFocusControl() {
		return composite;
	}

	@Override
	public void onClose() {		
	}

	@Override
	public void onOk() {		
	}


}
