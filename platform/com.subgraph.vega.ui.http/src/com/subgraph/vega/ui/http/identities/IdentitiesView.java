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
package com.subgraph.vega.ui.http.identities;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.identity.IIdentity;
import com.subgraph.vega.api.model.identity.IIdentityModel;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.http.identities.wizard.IdentityWizard;
import com.subgraph.vega.ui.http.identities.wizard.IdentityWizardDialog;

public class IdentitiesView extends ViewPart {
	public static final String ID = "com.subgraph.vega.views.http.identities";
	private IIdentityModel identityModel;
	private Composite parentComposite;
	private TableViewer identityTableViewer; 
	private Button identityCreateButton;
	private Button identityRemoveButton;
	private TableViewer dictionaryTableViewer;
	private Button dictionaryCreateButton;
	private Button dictionaryRemoveButton;
	private TableViewer exclusionTableViewer;
	private Button exclusionCreateButton;
	private Button exclusionRemoveButton;

	public IdentitiesView() {
		IWorkspace currentWorkspace = Activator.getDefault().getModel().addWorkspaceListener(new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if (event instanceof WorkspaceOpenEvent) {
					handleWorkspaceOpen((WorkspaceOpenEvent) event);
				} else if (event instanceof WorkspaceCloseEvent) {
					handleWorkspaceClose((WorkspaceCloseEvent) event);
				} else if (event instanceof WorkspaceResetEvent) {
					handleWorkspaceReset((WorkspaceResetEvent) event);
				}
			}
		});
		identityModel = currentWorkspace.getIdentityModel();
	}

	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		identityModel = event.getWorkspace().getIdentityModel();
	}

	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		identityModel = null;
		// REVISIT clear all UI fields
	}

	private void handleWorkspaceReset(WorkspaceResetEvent event) {
		identityModel = event.getWorkspace().getIdentityModel();
		// REVISIT update all UI fields  
	}

	@Override
	public void createPartControl(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, false));
		createIdentityGroup(parentComposite).setLayoutData(new GridData(GridData.FILL_BOTH));
		createDictionaryGroup(parentComposite).setLayoutData(new GridData(GridData.FILL_BOTH));
		createExclusionGroup(parentComposite).setLayoutData(new GridData(GridData.FILL_BOTH));
		createAuthGroup(parentComposite).setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
	public void setFocus() {
		parentComposite.setFocus();
	}

	private Composite createIdentityGroup(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(2, false));
		rootControl.setText("Identities");

		createIdentityTable(rootControl).setLayoutData(new GridData(GridData.FILL_BOTH));
		createIdentityButtons(rootControl).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		return rootControl;
	}

	private Composite createIdentityTable(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		final TableColumnLayout tcl = new TableColumnLayout();
		rootControl.setLayout(tcl);

		identityTableViewer = new TableViewer(rootControl, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		identityTableViewer.setContentProvider(new IdentityTableContentProvider());
		identityTableViewer.addSelectionChangedListener(createIdentityTableSelectionChangedListener());
		createIdentityTableColumns(identityTableViewer, tcl);
		final Table table = identityTableViewer.getTable();
		table.setHeaderVisible(false);
		table.setLinesVisible(true);
		identityTableViewer.setInput(identityModel.getAllIdentities());
		
		return rootControl;
	}

	private void createIdentityTableColumns(TableViewer viewer, TableColumnLayout layout) {
		final ColumnLayoutData[] layoutData = {
			new ColumnWeightData(100, 100, true),
		};
		final ColumnLabelProvider providerList[] = {
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IIdentity) element).getName();
				}
			},
		};
		for (int i = 0; i < layoutData.length; i++) {
			final TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			final TableColumn c = column.getColumn();
			layout.setColumnData(c, layoutData[i]);
			c.setMoveable(false);
			column.setLabelProvider(providerList[i]);
		}	
	}

	private ISelectionChangedListener createIdentityTableSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty() == false) {
					final IIdentity scanIdentity = (IIdentity)((IStructuredSelection) event.getSelection()).getFirstElement();
					identityRemoveButton.setGrayed(false);
					setScanIdentitySelected(scanIdentity);
				} else {
					identityRemoveButton.setGrayed(true);
				}
			}
		};
	}
	
	private void setScanIdentitySelected(IIdentity scanIdentity) {
	}

	private Composite createIdentityButtons(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, true));

		identityCreateButton = new Button(rootControl, SWT.PUSH);
		identityCreateButton.setText("create");
		identityCreateButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		identityCreateButton.addSelectionListener(createSelectionListenerIdentityCreateButton());
		identityRemoveButton = new Button(rootControl, SWT.PUSH);
		identityRemoveButton.setText("remove");
		identityRemoveButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		identityRemoveButton.setGrayed(true);
		identityRemoveButton.addSelectionListener(createSelectionListenerIdentityRemoveButton());

		return rootControl;
	}

	private SelectionListener createSelectionListenerIdentityCreateButton() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IdentityWizard wizard = new IdentityWizard();
				IdentityWizardDialog dialog = new IdentityWizardDialog(parentComposite.getShell(), wizard);
				if (dialog.open() == IDialogConstants.OK_ID) {
					IIdentity identity = wizard.getIdentity();
					identityModel.store(identity);
					identityTableViewer.setInput(identityModel.getAllIdentities());
				}
			}
		};
	}

	private SelectionListener createSelectionListenerIdentityRemoveButton() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// drop identity - disable only
			}
		};
	}

	private Composite createDictionaryGroup(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(2, false));
		rootControl.setText("Dictionary");

		createDictionaryTable(rootControl).setLayoutData(new GridData(GridData.FILL_BOTH));
		createDictionaryButtons(rootControl).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		return rootControl;
	}

	private Composite createDictionaryTable(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		final TableColumnLayout tcl = new TableColumnLayout();
		rootControl.setLayout(tcl);

		dictionaryTableViewer = new TableViewer(rootControl, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
//		dictionaryTableViewer.setContentProvider(new DictionaryTableContentProvider());
		dictionaryTableViewer.addSelectionChangedListener(createDictionaryTableSelectionChangedListener());
		createDictionaryTableColumns(dictionaryTableViewer, tcl);
		final Table table = dictionaryTableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		return rootControl;
	}

	private void createDictionaryTableColumns(TableViewer viewer, TableColumnLayout layout) {
		final String[] titles = { "Name", "Value", };
		final ColumnLayoutData[] layoutData = {
			new ColumnPixelData(120, true, true),
			new ColumnWeightData(100, 100, true),
		};
		final ColumnLabelProvider providerList[] = {
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return "";//((IScanIdentity) element).getName();
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return "";//((IScanIdentity) element).getName();
				}
			},
		};
		for (int i = 0; i < titles.length; i++) {
			final TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			final TableColumn c = column.getColumn();
			layout.setColumnData(c, layoutData[i]);
			c.setText(titles[i]);
			c.setMoveable(true);
			column.setLabelProvider(providerList[i]);
		}	
	}

	private ISelectionChangedListener createDictionaryTableSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty() == false) {
				} else {
				}
			}
		};
	}

	private Composite createDictionaryButtons(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, true));

		dictionaryCreateButton = new Button(rootControl, SWT.PUSH);
		dictionaryCreateButton.setText("create");
		dictionaryCreateButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		dictionaryCreateButton.addSelectionListener(createSelectionListenerDictionaryCreateButton());
		dictionaryRemoveButton = new Button(rootControl, SWT.PUSH);
		dictionaryRemoveButton.setText("remove");
		dictionaryRemoveButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		dictionaryRemoveButton.setGrayed(true);
		dictionaryRemoveButton.addSelectionListener(createSelectionListenerDictionaryRemoveButton());
		
		return rootControl;
	}

	private SelectionListener createSelectionListenerDictionaryCreateButton() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		};
	}

	private SelectionListener createSelectionListenerDictionaryRemoveButton() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		};
	}

	private Composite createExclusionGroup(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(2, false));
		rootControl.setText("Scan Path Exclusions");

		createExclusionTable(rootControl).setLayoutData(new GridData(GridData.FILL_BOTH));
		createExclusionButtons(rootControl).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		return rootControl;
	}

	private Composite createExclusionTable(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		final TableColumnLayout tcl = new TableColumnLayout();
		rootControl.setLayout(tcl);

		exclusionTableViewer = new TableViewer(rootControl, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
//		exclusionTableViewer.setContentProvider(new ExclusionTableContentProvider());
		exclusionTableViewer.addSelectionChangedListener(createExclusionTableSelectionChangedListener());
		createExclusionTableColumns(exclusionTableViewer, tcl);
		final Table table = exclusionTableViewer.getTable();
		table.setHeaderVisible(false);
		table.setLinesVisible(true);

		return rootControl;
	}

	private void createExclusionTableColumns(TableViewer viewer, TableColumnLayout layout) {
		final ColumnLayoutData[] layoutData = {
			new ColumnWeightData(100, 100, true),
		};
		final ColumnLabelProvider providerList[] = {
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return "";//((IScanIdentity) element).getName();
				}
			},
		};
		for (int i = 0; i < layoutData.length; i++) {
			final TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			final TableColumn c = column.getColumn();
			layout.setColumnData(c, layoutData[i]);
			c.setMoveable(false);
			column.setLabelProvider(providerList[i]);
		}	
	}

	private ISelectionChangedListener createExclusionTableSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
			}
		};
	}

	private Composite createExclusionButtons(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, true));

		exclusionCreateButton = new Button(rootControl, SWT.PUSH);
		exclusionCreateButton.setText("create");
		exclusionCreateButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		exclusionCreateButton.addSelectionListener(createSelectionListenerExclusionCreateButton());
		exclusionRemoveButton = new Button(rootControl, SWT.PUSH);
		exclusionRemoveButton.setText("remove");
		exclusionRemoveButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		exclusionRemoveButton.setGrayed(true);
		exclusionRemoveButton.addSelectionListener(createSelectionListenerExclusionRemoveButton());
		
		return rootControl;
	}

	private SelectionListener createSelectionListenerExclusionCreateButton() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		};
	}

	private SelectionListener createSelectionListenerExclusionRemoveButton() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		};
	}
	
	private Composite createAuthGroup(Composite parent) {
		Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, false));
		rootControl.setText("Authentication");


		return rootControl;
	}

}
