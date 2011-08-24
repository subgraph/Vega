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
package com.subgraph.vega.ui.http.intercept.queue;

import java.net.URI;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.http.intercept.InterceptView;
import com.subgraph.vega.ui.util.dialogs.ErrorDialog;

public class InterceptQueueView extends ViewPart {
	public final static String POPUP_TRANSACTIONS_TABLE = "com.subgraph.vega.ui.http.intercept.queue.InterceptQueueView.tableViewerTransactions";
	private IHttpInterceptor interceptor;
	private Composite parentComposite;
	private TableViewer tableViewerTransactions;

	public InterceptQueueView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		interceptor = Activator.getDefault().getProxyService().getInterceptor();
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new FillLayout());
		createTable(parentComposite);
		tableViewerTransactions.setInput(interceptor);
	}

	@Override
	public void setFocus() {
		tableViewerTransactions.getTable().setFocus();
	}

	private Composite createTable(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		final TableColumnLayout tcl = new TableColumnLayout();
		rootControl.setLayout(tcl);

		tableViewerTransactions = new TableViewer(rootControl, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewerTransactions.setContentProvider(new TransactionTableContentProvider(tableViewerTransactions));
		createColumns(tableViewerTransactions, tcl);
		final Table table = tableViewerTransactions.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewerTransactions.addDoubleClickListener(createDoubleClickListener());
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(tableViewerTransactions.getTable());
		tableViewerTransactions.getTable().setMenu(menu);
		getSite().registerContextMenu(POPUP_TRANSACTIONS_TABLE, menuManager, tableViewerTransactions);
		getSite().setSelectionProvider(tableViewerTransactions);
		
		return rootControl;
	}
	
	private void createColumns(TableViewer viewer, TableColumnLayout layout) {
		final String[] titles = { "Type", "Host", "Method", "Request", };
		final ColumnLayoutData[] layoutData = {
				new ColumnPixelData(60, true, true),
				new ColumnPixelData(120, true, true),
				new ColumnPixelData(60, true, true),
				new ColumnWeightData(100, 100, true),
		};

		final ColumnLabelProvider providerList[] = {
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IProxyTransaction) element).hasResponse() == false ? "Request" : "Response";
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					final URI uri = ((IProxyTransaction) element).getRequest().getURI();
					final StringBuilder buf = new StringBuilder();
					buf.append(uri.getScheme());
					buf.append("://");
					buf.append(uri.getHost());
					if (uri.getPort() != -1) {
						buf.append(':');
						buf.append(Integer.toString(uri.getPort()));
					}
					return buf.toString();
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IProxyTransaction) element).getRequest().getMethod();
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					final URI uri = ((IProxyTransaction) element).getRequest().getURI();
					if (uri.getRawQuery() != null) {
						return uri.getRawPath() + "?" + uri.getRawQuery();
					} else {
						return uri.getRawPath();
					}
				}
			}
		};
		for(int i = 0; i < titles.length; i++) {
			final TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			final TableColumn c = column.getColumn();
			layout.setColumnData(c, layoutData[i]);
			c.setText(titles[i]);
			c.setMoveable(true);
			column.setLabelProvider(providerList[i]);
		}
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	private IDoubleClickListener createDoubleClickListener() {
		return new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				final Object element = ((IStructuredSelection) event.getSelection()).getFirstElement();
				final InterceptView view;
				try {
					view = (InterceptView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(InterceptView.VIEW_ID);
				} catch (PartInitException e) {
					ErrorDialog.displayExceptionError(parentComposite.getShell(), e);
					return;
				}
				view.openTransaction((IProxyTransaction) element);
			}
		};
	}

}
