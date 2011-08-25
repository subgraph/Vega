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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.subgraph.vega.api.scanner.modules.IEnableableModule;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;
import com.subgraph.vega.api.scanner.modules.IScannerModule;
import com.subgraph.vega.ui.util.dialogs.IConfigDialogContent;

public class ConfigureProxyModulesContent implements IConfigDialogContent, ICheckStateListener, ICheckStateProvider {
	private final List<IResponseProcessingModule> modules;
	private Composite composite;
	private CheckboxTableViewer viewer;
	
	public ConfigureProxyModulesContent(List<IResponseProcessingModule> modules) {
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
		
		viewer = CheckboxTableViewer.newCheckList(composite, SWT.NONE);
		viewer.setCheckStateProvider(this);
		viewer.addCheckStateListener(this);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				if(element instanceof IScannerModule) {
					return ((IScannerModule) element).getModuleName();
				} else {
					return super.getText(element);
				}
			}
		});
		viewer.setInput(modules);
		viewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.layout();
		return composite;
	}

	@Override
	public String getTitle() {
		return "Configure Response Processing Modules for Proxy";
	}

	@Override
	public String getMessage() {
		return "Choose which response processing modules will be enabled to process responses received by the HTTP proxy";
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

	@Override
	public boolean isChecked(Object element) {
		if(element instanceof IEnableableModule) {
			IEnableableModule module = (IEnableableModule) element;
			return module.isEnabled();
		}
		return false;
	}

	@Override
	public boolean isGrayed(Object element) {
		return false;
	}

	@Override
	public void checkStateChanged(CheckStateChangedEvent event) {
		if(event.getElement() instanceof IEnableableModule) {
			IEnableableModule module = (IEnableableModule) event.getElement();
			module.setEnabled(event.getChecked());
		}
	}
}
