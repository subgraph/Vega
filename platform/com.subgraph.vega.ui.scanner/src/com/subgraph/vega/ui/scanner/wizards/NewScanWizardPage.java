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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.api.model.identity.IIdentity;
import com.subgraph.vega.api.scanner.modules.IScannerModule;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.scanner.modules.ModuleRegistryCheckStateProvider;
import com.subgraph.vega.ui.scanner.modules.ModuleRegistryContentProvider;
import com.subgraph.vega.ui.scanner.modules.ModuleRegistryLabelProvider;
import com.subgraph.vega.ui.util.images.ImageCache;

public class NewScanWizardPage extends WizardPage {
	static private final String VEGA_LOGO = "icons/vega_small.png";
	static private final Object emptyIdentity = new Object();
	private final String targetValue;
	private final List<IScannerModule> modules;
	private final List<Object> identities;
	private Composite container;
	private CheckboxTreeViewer viewer;
	private Text scanTarget;
	private ComboViewer scanIdentityViewer;

	public NewScanWizardPage(ImageCache imageCache, String targetValue, Collection<IIdentity> identities, List<IScannerModule> modules) {
		super("Create a New Scan");
		setTitle("Create a New Scan");
		setDescription("New Scan Parameters");
		setImageDescriptor(ImageDescriptor.createFromImage(imageCache.get(VEGA_LOGO)));
		this.targetValue = targetValue;
		this.modules = modules;
		this.identities = new ArrayList<Object>(identities.size() + 1);
		this.identities.add(emptyIdentity);
		this.identities.addAll(identities);
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(1, false));

		createTargetPart(container);
		createIdentityPart(container);
		createModulesPart(container);

		setControl(container);
		setPageComplete(!scanTarget.getText().isEmpty());		
	}

	private void createTargetPart(Composite parent) {
		Label label = new Label(container, SWT.NULL);
		label.setText("Input the base URI:");

		scanTarget = new Text(container, SWT.BORDER | SWT.SINGLE);
		scanTarget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if(targetValue != null) {
			scanTarget.setText(targetValue);
		}
		scanTarget.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(!scanTarget.getText().trim().isEmpty());
			}
		});
	}
	
	private void createIdentityPart(Composite parent) {
		Label label = new Label(container, SWT.NULL);
		label.setText("Identity to scan site as:");

		scanIdentityViewer = new ComboViewer(parent, SWT.READ_ONLY);
		scanIdentityViewer.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		scanIdentityViewer.setContentProvider(new ArrayContentProvider());
		scanIdentityViewer.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				if (element != emptyIdentity) {
					return ((IIdentity)element).getName();
				} else {
					return "";
				}
			}
		});
		Object[] identitiesCp = identities.toArray(new Object[0]);
		scanIdentityViewer.setInput(identitiesCp);
		scanIdentityViewer.setSelection(new StructuredSelection(identitiesCp[0]));
	}

	private void createModulesPart(Composite parent) {
		Label modulesLabel = new Label(container, SWT.NULL);
		modulesLabel.setText("Select the modules to run:");
		
		viewer = new CheckboxTreeViewer(container, SWT.BORDER| SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		final ModuleRegistryCheckStateProvider checkStateProvider = new ModuleRegistryCheckStateProvider(viewer);
		viewer.setContentProvider(new ModuleRegistryContentProvider(checkStateProvider));
		viewer.setLabelProvider(new ModuleRegistryLabelProvider());
		viewer.setCheckStateProvider(checkStateProvider);		
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH)); 
	    viewer.setInput(modules.toArray(new IScannerModule[0]));
        viewer.addCheckStateListener(checkStateProvider);
	}

	public String getScanTarget() {
		return scanTarget.getText();
	}

	public IIdentity getScanIdentity() {
		Object selection = ((IStructuredSelection) scanIdentityViewer.getSelection()).getFirstElement();
		if (selection != emptyIdentity) {
			return  (IIdentity)selection;
		} else {
			return null;
		}
	}

}
