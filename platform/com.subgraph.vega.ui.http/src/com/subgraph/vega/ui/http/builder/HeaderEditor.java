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
package com.subgraph.vega.ui.http.builder;

import java.util.Iterator;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;
import com.subgraph.vega.api.http.requests.IHttpMessageBuilder;

/**
 * Manages visual components used to edit HTTP message headers. 
 */
public class HeaderEditor extends Composite implements IHttpBuilderPart {
	private IHttpMessageBuilder messageBuilder;
	private TableViewer tableViewerHeaders;
	private boolean headersTableHasSelection;
	private Button buttonCreate;
	private Button buttonRemove;
	private Button buttonMoveUp;
	private Button buttonMoveDown;
	private int heightInRows;
	
	/**
	 * @param heightInRows Height of header table in text rows, or 0 to fill available space. 
	 */
	public HeaderEditor(Composite parent, final IHttpMessageBuilder messageBuilder, int heightInRows) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(2, false));
		this.messageBuilder = messageBuilder;
		this.heightInRows = heightInRows;

		GridData gd;
		if (heightInRows != 0) {
			gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		} else {
			gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		}
		final Composite compTable = createHeaderTable(this, gd, this.heightInRows);
		compTable.setLayoutData(gd);
		final Composite compTableButtons = createHeaderTableButtons(this);
		compTableButtons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		tableViewerHeaders.setInput(messageBuilder);
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public void setEditable(boolean editable) {
		buttonCreate.setEnabled(editable);
		buttonRemove.setEnabled(editable && headersTableHasSelection);
		buttonMoveUp.setEnabled(editable && headersTableHasSelection);
		buttonMoveDown.setEnabled(editable && headersTableHasSelection);
	}

	@Override
	public void refresh() {
		tableViewerHeaders.refresh();
	}

	@Override
	public void processContents() {
		// nothing to do: headers are modified in table 
	}

	private Composite createHeaderTable(Composite parent, GridData gd, int heightInRows) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		final TableColumnLayout tcl = new TableColumnLayout();
		rootControl.setLayout(tcl);

		tableViewerHeaders = new TableViewer(rootControl, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewerHeaders.setContentProvider(new HeaderTableContentProvider());
		tableViewerHeaders.addSelectionChangedListener(createSelectionChangedListener());
		createHeaderTableColumns(tableViewerHeaders, tcl);
		final Table table = tableViewerHeaders.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		if (heightInRows != 0) {
			gd.heightHint = table.getItemHeight() * heightInRows;
		}

		return rootControl;
	}

	private ISelectionChangedListener createSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				headersTableHasSelection = !event.getSelection().isEmpty(); 
				buttonRemove.setEnabled(headersTableHasSelection);
				buttonMoveUp.setEnabled(headersTableHasSelection);
				buttonMoveDown.setEnabled(headersTableHasSelection);
			}
		};
	}

	private void createHeaderTableColumns(TableViewer viewer, TableColumnLayout layout) {
		final String[] titles = { "Name", "Value", };
		final ColumnLayoutData[] layoutData = {
			new ColumnPixelData(120, true, true),
			new ColumnWeightData(100, 100, true),
		};
		final EditingSupport editorList[] = {
				new HeaderNameEditingSupport(viewer),
				new HeaderValueEditingSupport(viewer),
		};
		final ColumnLabelProvider providerList[] = {
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpHeaderBuilder) element).getName();
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpHeaderBuilder) element).getValue();
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

	private Composite createHeaderTableButtons(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, true));

		buttonCreate = new Button(rootControl, SWT.PUSH);
		buttonCreate.setText("create");
		buttonCreate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		buttonCreate.addSelectionListener(createSelectionListenerButtonCreate());
		buttonRemove = new Button(rootControl, SWT.PUSH);
		buttonRemove.setText("remove");
		buttonRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		buttonRemove.setEnabled(false);
		buttonRemove.addSelectionListener(createSelectionListenerButtonRemove());
		buttonMoveUp = new Button(rootControl, SWT.PUSH);
		buttonMoveUp.setText("move up");
		buttonMoveUp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		buttonMoveUp.setEnabled(false);
		buttonMoveUp.addSelectionListener(createSelectionListenerButtonMoveUp());
		buttonMoveDown = new Button(rootControl, SWT.PUSH);
		buttonMoveDown.setText("move down");
		buttonMoveDown.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		buttonMoveDown.setEnabled(false);
		buttonMoveDown.addSelectionListener(createSelectionListenerButtonMoveDown());

		return rootControl;
	}

	private SelectionListener createSelectionListenerButtonCreate() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				messageBuilder.addHeader("", "");
				tableViewerHeaders.refresh();
				tableViewerHeaders.editElement(tableViewerHeaders.getElementAt(tableViewerHeaders.getTable().getItemCount() - 1), 0);
			}
		};
	}

	private SelectionListener createSelectionListenerButtonRemove() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewerHeaders.getSelection();
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					messageBuilder.removeHeader((IHttpHeaderBuilder) i.next());
				}
				tableViewerHeaders.refresh();
			}
		};
	}
	
	private SelectionListener createSelectionListenerButtonMoveUp() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewerHeaders.getSelection();
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					int idx = messageBuilder.getHeaderIdxOf((IHttpHeaderBuilder) i.next());
					if (idx != 0) {
						messageBuilder.swapHeader(idx - 1, idx);
					} else {
						break;
					}
				}
				tableViewerHeaders.refresh();
			}
		};
	}

	private SelectionListener createSelectionListenerButtonMoveDown() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewerHeaders.getSelection();
				int idx[] = new int[selection.size()];
				int offset = 1;
				for (Iterator<?> i = selection.iterator(); i.hasNext(); offset++) {
					idx[idx.length - offset] = messageBuilder.getHeaderIdxOf((IHttpHeaderBuilder) i.next());
				}

				if (idx[0] + 1 != messageBuilder.getHeaderCnt()) {
					for (int i = 0; i < idx.length; i++) {
						messageBuilder.swapHeader(idx[i], idx[i] + 1);
					}
				}
				tableViewerHeaders.refresh();
			}
		};
	}
	
}
