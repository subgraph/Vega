package com.subgraph.vega.ui.scanner.wizards;

import java.util.List;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.subgraph.vega.api.scanner.modules.IScannerModule;
import com.subgraph.vega.ui.util.modules.ModuleRegistryCheckStateProvider;
import com.subgraph.vega.ui.util.modules.ModuleRegistryContentProvider;
import com.subgraph.vega.ui.util.modules.ModuleRegistryLabelProvider;

public class NewScanModulesPage extends WizardPage {

	private final List<IScannerModule> modules;
	private CheckboxTreeViewer viewer;
	
	NewScanModulesPage(List<IScannerModule> modules) {
		super("Select Modules");
		setTitle("Select Modules");
		setDescription("Choose which scanner modules to enable for this scan");
		
		this.modules = modules;
	}

	@Override
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout());
		createModulesPart(container);
		setControl(container);
	}
	
	private void createModulesPart(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText("Select modules to run:");
		
		viewer = new CheckboxTreeViewer(parent, SWT.BORDER| SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		final ModuleRegistryCheckStateProvider checkStateProvider = new ModuleRegistryCheckStateProvider(viewer);
		viewer.setContentProvider(new ModuleRegistryContentProvider(checkStateProvider));
		viewer.setLabelProvider(new ModuleRegistryLabelProvider());
		viewer.setCheckStateProvider(checkStateProvider);		
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH)); 
	    viewer.setInput(modules.toArray(new IScannerModule[0]));
        viewer.addCheckStateListener(checkStateProvider);
	}
}
