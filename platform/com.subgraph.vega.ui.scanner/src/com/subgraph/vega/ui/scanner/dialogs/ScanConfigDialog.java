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
package com.subgraph.vega.ui.scanner.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.util.modules.ModuleRegistryCheckStateProvider;
import com.subgraph.vega.ui.util.modules.ModuleRegistryContentProvider;
import com.subgraph.vega.ui.util.modules.ModuleRegistryLabelProvider;

public class ScanConfigDialog extends Dialog {

	private IScannerModuleRegistry registry;
	private CheckboxTreeViewer viewer;
	private Text cookieString;
	private Table exclusionsTable;

	public ScanConfigDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.RESIZE);
	}
	
	protected Control createDialogArea(Composite parent) {
		registry = Activator.getDefault().getIScannerModuleRegistry();

		Composite area = (Composite) super.createDialogArea(parent);
		area.setLayout(new FillLayout());

		TabFolder tabFolder = new TabFolder(area, SWT.BORDER);
		createTabItemModules(tabFolder);
		createTabItemAuthentication(tabFolder);
		createTabItemCookieString(tabFolder);
		createTabItemExclusions(tabFolder);
		
		return area;
	}
	
	private void createTabItemModules(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Modules");
		
		Composite tabItemArea = new Composite(tabFolder,SWT.NULL);
		GridLayout gridLayout = new GridLayout(1, false);
		tabItemArea.setLayout(gridLayout);
		tabItemArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label label = new Label(tabItemArea,SWT.NONE);
		label.setText("Select Modules");
		
		viewer = new CheckboxTreeViewer(tabItemArea,SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		final ModuleRegistryCheckStateProvider checkStateProvider = new ModuleRegistryCheckStateProvider(viewer);
		viewer.setContentProvider(new ModuleRegistryContentProvider(checkStateProvider));
		viewer.setLabelProvider(new ModuleRegistryLabelProvider());
		viewer.setCheckStateProvider(checkStateProvider);
		
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH)); 

	    viewer.setInput(registry);
        viewer.addCheckStateListener(checkStateProvider);
		
		tabItem.setControl(tabItemArea);
	}

	private void createTabItemAuthentication(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Authentication");

		GridLayout innerLayout = new GridLayout();
		Label basicLabel;
		Label basicUsernameLabel;
		Label basicPasswordLabel;
		Label basicRealmLabel;
		Label basicDomainLabel;
		Label ntlmLabel;
		Label ntlmUsernameLabel;
		Label ntlmPasswordLabel;
		
		Composite tabItemArea = new Composite(tabFolder, SWT.NULL);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		GridData gd2 = new GridData();
		gd2.horizontalSpan = 2;
		
		innerLayout.numColumns = 2;
		tabItemArea.setLayout(innerLayout);
		
		basicLabel = new Label(tabItemArea, SWT.NONE);
		basicLabel.setText("Basic Authentication");
		basicLabel.setLayoutData(gd2);
	
		basicUsernameLabel = new Label(tabItemArea, SWT.NONE);
		basicUsernameLabel.setText("Username:");
		Text basicUsername = new Text(tabItemArea, SWT.SINGLE | SWT.BORDER);
		basicUsername.setLayoutData(gd);
				
		basicPasswordLabel = new Label(tabItemArea, SWT.NONE);
		basicPasswordLabel.setText("Password:");
		Text basicPassword = new Text(tabItemArea, SWT.BORDER);
		basicPassword.setLayoutData(gd);
		
		basicRealmLabel = new Label(tabItemArea, SWT.NONE);
		basicRealmLabel.setText("Realm:");
		Text basicRealm = new Text(tabItemArea, SWT.BORDER);
		basicRealm.setLayoutData(gd);
		
		basicDomainLabel = new Label(tabItemArea, SWT.NONE);
		basicDomainLabel.setText("Domain");
		Text basicDomain = new Text(tabItemArea, SWT.BORDER);
		basicDomain.setLayoutData(gd);
		
		ntlmLabel = new Label(tabItemArea, SWT.NONE);
		ntlmLabel.setText("NTLM Authentication");
		ntlmLabel.setLayoutData(gd2);
		
		ntlmUsernameLabel = new Label(tabItemArea, SWT.NONE);
		ntlmUsernameLabel.setText("Username:");
		Text ntlmUsername = new Text(tabItemArea, SWT.BORDER);
		ntlmUsername.setLayoutData(gd);
		
		ntlmPasswordLabel = new Label(tabItemArea, SWT.NONE);
		ntlmPasswordLabel.setText("Password:");
		Text ntlmPassword = new Text(tabItemArea, SWT.BORDER);
		ntlmPassword.setLayoutData(gd);
		
		tabItem.setControl(tabItemArea);
	}

	private void createTabItemCookieString(TabFolder tabFolder) {		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Cookie String");

		Composite tabItemArea = new Composite(tabFolder,SWT.NULL);
		GridLayout gridLayout = new GridLayout();
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 140;
		gridData.heightHint = 60;
	
		tabItemArea.setLayout(gridLayout);
		
		Label cookieLabel = new Label(tabItemArea, SWT.BORDER);
		cookieLabel.setText("Cookie string:");
		cookieString = new Text(tabItemArea, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		cookieString.setLayoutData(gridData);
		
		tabItem.setControl(tabItemArea);			
	}
	
	private void createTabItemExclusions(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Exclusions");
		
		Composite tabItemArea = new Composite(tabFolder, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		tabItemArea.setLayout(gridLayout);
	
		Label exclusionLabel = new Label(tabItemArea, SWT.NULL);
		exclusionLabel.setText("Set scan exclusion patterns:");
		final Text exclusionText = new Text(tabItemArea, SWT.BORDER | SWT.SINGLE);
		exclusionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		exclusionText.setMessage("regular expression");
		final Button addButton = new Button(tabItemArea, SWT.PUSH);
		addButton.setText("Add exclusion");
		
		exclusionsTable = new Table(tabItemArea, SWT.BORDER | SWT.MULTI);
		exclusionsTable.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
	
		Button removeButton = new Button(tabItemArea, SWT.PUSH);
		removeButton.setText("Remove selected exclusion(s)");
		
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				exclusionsTable.remove(exclusionsTable.getSelectionIndices());
			}
		});
		
		addButton.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event event) {
		          if (event.widget == addButton) {
		        	  if (exclusionText.getText() != null) {
		        		  TableItem items[] = exclusionsTable.getItems();
		        		  for (TableItem t: items) { 
		        			  if (exclusionText.getText().equals(t.getText())) {
		        				  return;
		        			  }
		        		  }
		        		  TableItem newExclusion = new TableItem(exclusionsTable,SWT.NONE);
		        		  newExclusion.setText(exclusionText.getText());
		        	  }
		          }
	        }
		});
		tabItem.setControl(tabItemArea);
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createSaveButton(parent, true);
		//createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	protected Button createSaveButton(Composite parent,  boolean defaultButton) {
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Save");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				okPressed();

			}
		});
		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		setButtonLayoutData(button);
		return button;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Scan Configuration");
	}
	
}
