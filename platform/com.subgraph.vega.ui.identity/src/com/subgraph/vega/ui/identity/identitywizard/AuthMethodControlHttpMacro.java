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
package com.subgraph.vega.ui.identity.identitywizard;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.subgraph.vega.api.model.identity.IAuthMethod;
import com.subgraph.vega.api.model.identity.IAuthMethodHttpMacro;
import com.subgraph.vega.api.model.macros.IHttpMacro;
import com.subgraph.vega.ui.identity.Activator;
import com.subgraph.vega.ui.macros.macrodialog.MacroDialog;

public class AuthMethodControlHttpMacro extends Composite implements IAuthMethodControl {
	private final WizardPage page;
	private IAuthMethodHttpMacro authMethod;
	private TableViewer macrosTableViewer;

	AuthMethodControlHttpMacro(Composite parent, WizardPage page) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));
		this.page = page;
		authMethod = Activator.getDefault().getModel().getCurrentWorkspace().getIdentityModel().createAuthMethodHttpMacro();
		createControls();
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public IAuthMethod getAuthMethod() {
		IHttpMacro macro = (IHttpMacro)((IStructuredSelection) macrosTableViewer.getSelection()).getFirstElement();
		authMethod.setMacro(macro);
		return authMethod;
	}

	private void createControls() {
		Label label = new Label(this, SWT.NONE);
		label.setText("Select a macro below, or create one");
		createMacrosTable(this).setLayoutData(new GridData(GridData.FILL_BOTH));
		createMacrosTableButtons(this).setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		macrosTableViewer.setInput(Activator.getDefault().getModel().getCurrentWorkspace().getHttpMacroModel());
	}

	private Composite createMacrosTable(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		final TableColumnLayout tcl = new TableColumnLayout();
		rootControl.setLayout(tcl);

		macrosTableViewer = new TableViewer(rootControl, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		macrosTableViewer.setContentProvider(new MacrosTableContentProvider());
		macrosTableViewer.addSelectionChangedListener(createMacrosTableSelectionChangedListener());
		createMacrosTableColumns(macrosTableViewer, tcl);
		final Table table = macrosTableViewer.getTable();
		table.setHeaderVisible(false);
		table.setLinesVisible(true);

		return rootControl;
	}

	private ISelectionChangedListener createMacrosTableSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				page.setPageComplete(!event.getSelection().isEmpty());
			}
		};
	}

	private void createMacrosTableColumns(TableViewer viewer, TableColumnLayout layout) {
		final ColumnLayoutData[] layoutData = {
			new ColumnWeightData(1),
		};
		final ColumnLabelProvider providerList[] = {
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpMacro) element).getName();
				}
			},
		};
		for (int i = 0; i < layoutData.length; i++) {
			final TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			final TableColumn c = column.getColumn();
			layout.setColumnData(c, layoutData[i]);
			column.setLabelProvider(providerList[i]);
		}	
	}

	private Control createMacrosTableButtons(Composite parent) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Create macro");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MacroDialog dialog = MacroDialog.createDialog(getShell());
				if (dialog.open() == Window.OK) {
					macrosTableViewer.refresh();
					macrosTableViewer.setSelection(new StructuredSelection(dialog.getMacro()), true);
				}
			}
		});		
		return button;
	}

}
