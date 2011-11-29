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
package com.subgraph.vega.internal.ui.macros.macrodialog;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.subgraph.vega.api.http.requests.IHttpMessageBuilder;
import com.subgraph.vega.api.model.macros.IHttpMacroItem;
import com.subgraph.vega.api.model.macros.IHttpMacroItemParam;
import com.subgraph.vega.ui.http.builder.BuilderParseException;
import com.subgraph.vega.ui.http.builder.IHttpBuilderPart;

public class MacroItemEditor extends Composite implements IHttpBuilderPart {
	private IHttpMessageBuilder messageBuilder;
	private IHttpMacroItem macroItem;
	private Button useCookiesButton;
	private Button keepCookiesButton;
	private TableViewer paramsTableViewer;
	private boolean paramsTableHasSelection;
	private Button createButton;
	private Button removeButton;
	private Button moveUpButton;
	private Button moveDownButton;

	public MacroItemEditor(Composite parent, IHttpMessageBuilder messageBuilder) {
		super(parent, SWT.NONE);
		this.messageBuilder = messageBuilder;
		setLayout(new GridLayout(1, false));

		createConfigGroup(this).setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		createParamsGroup(this).setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public void setEditable(boolean editable) {
		useCookiesButton.setEnabled(editable);
		keepCookiesButton.setEnabled(editable);
		createButton.setEnabled(editable);
		removeButton.setEnabled(editable && paramsTableHasSelection);
		moveUpButton.setEnabled(editable && paramsTableHasSelection);
		moveDownButton.setEnabled(editable && paramsTableHasSelection);
	}

	@Override
	public void refresh() {
		if (macroItem != null) {
			useCookiesButton.setSelection(macroItem.getUseCookies());
			keepCookiesButton.setSelection(macroItem.getKeepCookies());
		} else {
			useCookiesButton.setSelection(false);
			keepCookiesButton.setSelection(false);
		}
		paramsTableViewer.setInput(macroItem);
	}

	@Override
	public void processContents() throws BuilderParseException {
	}

	public void setMacroItem(IHttpMacroItem macroItem) {
		this.macroItem = macroItem;
	}
	
	private Composite createConfigGroup(Composite parent) {
		Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(2, false));
		rootControl.setText("Configuration");

		useCookiesButton = new Button(rootControl, SWT.CHECK);
		Label label = new Label(rootControl, SWT.NONE);
		label.setText("Use cookies in the request that were already set");

		keepCookiesButton = new Button(rootControl, SWT.CHECK);
		label = new Label(rootControl, SWT.NONE);
		label.setText("Keep cookies from the response");
		
		return rootControl;
	}

	private Composite createParamsGroup(Composite parent) {
		Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(2, false));
		rootControl.setText("Request Parameters");

		GridData gd = new GridData(GridData.FILL_BOTH);
		createParamsTable(rootControl, gd, 8).setLayoutData(gd);
		createParamsButtons(rootControl).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		return rootControl;
	}

	private Composite createParamsTable(Composite parent, GridData gd, int heightInRows) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		final TableColumnLayout tcl = new TableColumnLayout();
		rootControl.setLayout(tcl);

		paramsTableViewer = new TableViewer(rootControl, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		paramsTableViewer.setContentProvider(new MacroItemParamsTableContentProvider());
		paramsTableViewer.addSelectionChangedListener(createParamsTableSelectionChangedListener());
		createParamsTableColumns(paramsTableViewer, tcl);
		final Table table = paramsTableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		gd.heightHint = table.getItemHeight() * heightInRows;

		return rootControl;
	}

	private ISelectionChangedListener createParamsTableSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				paramsTableHasSelection = !event.getSelection().isEmpty(); 
				removeButton.setEnabled(paramsTableHasSelection);
				moveUpButton.setEnabled(paramsTableHasSelection);
				moveDownButton.setEnabled(paramsTableHasSelection);
			}
		};
	}

	private void createParamsTableColumns(TableViewer viewer, TableColumnLayout layout) {
		final String[] titles = { "Name", "Source", "Value", };
		final ColumnLayoutData[] layoutData = {
			new ColumnPixelData(70, true, true),
			new ColumnPixelData(140, true, true),
			new ColumnWeightData(100, 100, true),
		};
		final EditingSupport editorList[] = {
			null,
			new MacroItemParamValueSourceEditingSupport(viewer),
			new MacroItemParamValueEditingSupport(viewer),
		};
		final ColumnLabelProvider providerList[] = {
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpMacroItemParam) element).getName();
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpMacroItemParam) element).getValueSource().getDescription();
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpMacroItemParam) element).getValue();
				}
			},
		};
		for (int i = 0; i < titles.length; i++) {
			final TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			final TableColumn c = column.getColumn();
			layout.setColumnData(c, layoutData[i]);
			c.setText(titles[i]);
			c.setMoveable(true);
			column.setEditingSupport(editorList[i]);
			column.setLabelProvider(providerList[i]);
		}	
	}
	
	private Composite createParamsButtons(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, true));

		createButton = new Button(rootControl, SWT.PUSH);
		createButton.setText("create");
		createButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
//		createButton.addSelectionListener(createSelectionListenerCreateButton());
		removeButton = new Button(rootControl, SWT.PUSH);
		removeButton.setText("remove");
		removeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
//		removeButton.addSelectionListener(createSelectionListenerRemoveButton());
		moveUpButton = new Button(rootControl, SWT.PUSH);
		moveUpButton.setText("move up");
		moveUpButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
//		moveUpButton.addSelectionListener(createSelectionListenerMoveUpButton());
		moveDownButton = new Button(rootControl, SWT.PUSH);
		moveDownButton.setText("move down");
		moveDownButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
//		moveDownButton.addSelectionListener(createSelectionListenerMoveDownButton());
		
		return rootControl;
	}

}
