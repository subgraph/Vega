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
package com.subgraph.vega.ui.http.requestviewer;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.model.taggablepopup.TaggablePopupDialog;

public class HttpRequestView extends ViewPart {
	public final static String POPUP_REQUESTS_TABLE = "com.subgraph.vega.ui.http.requestviewer.HttpRequestView.requestView";
	private TableViewer tableViewer;
	private RequestResponseViewer requestResponseViewer;
	private TaggablePopupDialog taggablePopupDialog;

	public HttpRequestView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		final SashForm form = new SashForm(parent, SWT.VERTICAL);
		final Composite comp = new Composite(form, SWT.NONE);
		final TableColumnLayout tcl = new TableColumnLayout();
		comp.setLayout(tcl);

		tableViewer = new TableViewer(comp, SWT.VIRTUAL | SWT.FULL_SELECTION);
		createColumns(tableViewer, tcl);
		tableViewer.setContentProvider(new HttpViewContentProviderLazy());
		tableViewer.setLabelProvider(new HttpViewLabelProvider());
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(tableViewer.getTable());
		tableViewer.getTable().setMenu(menu);
		tableViewer.getTable().addMouseTrackListener(createTableMouseTrackListener());
		tableViewer.getTable().addMouseMoveListener(createMouseMoveListener());
		getSite().registerContextMenu(POPUP_REQUESTS_TABLE, menuManager, tableViewer);
		getSite().setSelectionProvider(tableViewer);

		tableViewer.setInput(Activator.getDefault().getModel());

		requestResponseViewer = new RequestResponseViewer(form);
		form.setWeights(new int[] {40, 60});
		parent.pack();

		tableViewer.addSelectionChangedListener(createSelectionChangedListener());
	}

	public void  focusOnRecord(long requestId) {
		final Object inputObj = tableViewer.getInput();
		if(!(inputObj instanceof IModel)) {
			return;
		}
		final IModel model = (IModel) inputObj;
		final IWorkspace workspace = model.getCurrentWorkspace();
		if(workspace == null) {
			return;
		}
		
		final IRequestLog requestLog = workspace.getRequestLog();
		final IRequestLogRecord record = requestLog.lookupRecord(requestId);
		if(record == null)
			return;

		tableViewer.setSelection(new StructuredSelection(record), true);
		requestResponseViewer.setDisplayResponse();
	}

	private void createColumns(TableViewer viewer, TableColumnLayout layout) {
		final String[] titles = {"ID", "Host", "Method", "Request", "Status", "Length", "Time (ms)", "Tags" };
		final ColumnLayoutData[] layoutData = {
				new ColumnPixelData(60, true, true),
				new ColumnPixelData(120, true, true),
				new ColumnPixelData(60, true, true),
				new ColumnWeightData(100, 100, true),
				new ColumnPixelData(50, true, true),
				new ColumnPixelData(80, true, true),
				new ColumnPixelData(50, true, true),
				new ColumnPixelData(15, true, true)
		};

		for(int i = 0; i < titles.length; i++) {
			final TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			final TableColumn c = column.getColumn();
			layout.setColumnData(c, layoutData[i]);
			c.setText(titles[i]);
			c.setMoveable(true);
		}
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	private ISelectionChangedListener createSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if(selection.getFirstElement() instanceof IRequestLogRecord)
					requestResponseViewer.setCurrentRecord((IRequestLogRecord) selection.getFirstElement());
				else
					requestResponseViewer.setCurrentRecord(null);
			}
		};
	}

	@Override
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}

	private MouseMoveListener createMouseMoveListener() {
		return new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				if (taggablePopupDialog != null) {
					taggablePopupDialog.close();
					taggablePopupDialog = null;
				}
			}

		};
	}
	
	private MouseTrackListener createTableMouseTrackListener() {
		return new MouseTrackListener() {
			@Override
			public void mouseEnter(MouseEvent e) {
			}

			@Override
			public void mouseExit(MouseEvent e) {
			}

			@Override
			public void mouseHover(MouseEvent e) {
				Point pt = new Point(e.x, e.y);
				TableItem tableItem = tableViewer.getTable().getItem(pt);
				if (tableItem != null) {
					IRequestLogRecord record = (IRequestLogRecord) tableItem.getData();
					if (record.getTagCount() > 0) {
						taggablePopupDialog = new TaggablePopupDialog(tableViewer.getTable().getShell(), record, pt);
						taggablePopupDialog.open();
					}
				}			
			}
		};
	}

}
