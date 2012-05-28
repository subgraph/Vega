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
package com.subgraph.vega.ui.http.statusview;

import java.net.URI;
import java.util.Iterator;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.requests.IHttpRequestTask;
import com.subgraph.vega.api.model.requests.IRequestOrigin;
import com.subgraph.vega.api.model.requests.IRequestOriginProxy;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.http.intercept.InterceptView;
import com.subgraph.vega.ui.util.dialogs.ErrorDialog;

public class StatusView extends ViewPart {
	public final static String ID = "com.subgraph.vega.views.proxystatus";
	private SashForm parentComposite;
	private TableViewer interceptQueueTableViewer;
	private Menu interceptQueueTableMenu;
	private TableViewer requestStatusTableViewer;
	private Menu requestStatusTableMenu;

	public StatusView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parentComposite = new SashForm(parent, SWT.VERTICAL);
		parentComposite.setLayout(new FillLayout());
		createInterceptQueueArea(parentComposite);
		createRequestStatusArea(parentComposite);
		parentComposite.setWeights(new int[] {75, 25});
		interceptQueueTableViewer.setInput(Activator.getDefault().getProxyService().getInterceptor());
		requestStatusTableViewer.setInput(Activator.getDefault().getProxyService());
	}

	@Override
	public void setFocus() {
	}

	private Composite createInterceptQueueArea(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, false));
		rootControl.setText("Interceptor Queue");

		createInterceptQueueTable(rootControl).setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return rootControl;
	}
	
	private Composite createInterceptQueueTable(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		final TableColumnLayout tcl = new TableColumnLayout();
		rootControl.setLayout(tcl);

		interceptQueueTableViewer = new TableViewer(rootControl, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		interceptQueueTableViewer.setContentProvider(new InterceptQueueTableContentProvider());
		createInterceptQueueTableColumns(interceptQueueTableViewer, tcl);
		final Table table = interceptQueueTableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		interceptQueueTableViewer.addSelectionChangedListener(createInterceptQueueTableSelectionChangedListener());
		interceptQueueTableViewer.addDoubleClickListener(createInterceptQueueTableDoubleClickListener());
		table.setMenu(createInterceptQueueTableMenu(table));

		return rootControl;
	}
	
	private void createInterceptQueueTableColumns(TableViewer viewer, TableColumnLayout layout) {
		final String[] titles = { "Type", "Host", "Method", "Request", "Listener", };
		final ColumnLayoutData[] layoutData = {
				new ColumnPixelData(60, true, true),
				new ColumnPixelData(120, true, true),
				new ColumnPixelData(60, true, true),
				new ColumnWeightData(100, 100, true),
				new ColumnPixelData(100, true, true),
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
					return uriToHostString(((IProxyTransaction) element).getRequest().getURI());
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
					return uriToPathString(((IProxyTransaction) element).getRequest().getURI());
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					final IRequestOrigin origin = ((IProxyTransaction) element).getRequestEngine().getRequestOrigin();
					if (origin instanceof IRequestOriginProxy) {
						return origin.toString();
					} else {
						return null;
					}
				}
			},
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

	private ISelectionChangedListener createInterceptQueueTableSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				interceptQueueTableMenu.setEnabled(!event.getSelection().isEmpty());
			}
		};
	}
	
	private IDoubleClickListener createInterceptQueueTableDoubleClickListener() {
		return new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				final Object element = ((IStructuredSelection) event.getSelection()).getFirstElement();
				final InterceptView view;
				try {
					view = (InterceptView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(InterceptView.ID);
				} catch (PartInitException e) {
					ErrorDialog.displayExceptionError(parentComposite.getShell(), e);
					return;
				}
				view.openTransaction((IProxyTransaction) element);
			}
		};
	}

	private Menu createInterceptQueueTableMenu(Table table) {
		interceptQueueTableMenu = new Menu(table);
		interceptQueueTableMenu.setEnabled(false);
		
	    MenuItem forwardMenuItem = new MenuItem(interceptQueueTableMenu, SWT.CASCADE);
	    forwardMenuItem.setText("Forward Transaction");
	    forwardMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				forwardInterceptQueueSelection();
			};
	    });

	    MenuItem dropMenuItem = new MenuItem(interceptQueueTableMenu, SWT.CASCADE);
	    dropMenuItem.setText("Drop Transaction");
	    dropMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dropInterceptQueueSelection();
			};
	    });

		return interceptQueueTableMenu;
	}

	private void forwardInterceptQueueSelection() {
		IStructuredSelection selection = (IStructuredSelection) interceptQueueTableViewer.getSelection();
		for (Iterator<?> iter = selection.iterator(); iter.hasNext(); ) {
			IProxyTransaction transaction = (IProxyTransaction) iter.next();
			transaction.doForward();
		}
	}

	private void dropInterceptQueueSelection() {
		IStructuredSelection selection = (IStructuredSelection) interceptQueueTableViewer.getSelection();
		for (Iterator<?> iter = selection.iterator(); iter.hasNext(); ) {
			IProxyTransaction transaction = (IProxyTransaction) iter.next();
			transaction.doDrop();
		}
	}
	
	private Composite createRequestStatusArea(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, false));
		rootControl.setText("Request Status");

		createRequestStatusTable(rootControl).setLayoutData(new GridData(GridData.FILL_BOTH));

		return rootControl;
	}

	private Composite createRequestStatusTable(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		final TableColumnLayout tcl = new TableColumnLayout();
		rootControl.setLayout(tcl);

		requestStatusTableViewer = new TableViewer(rootControl, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		requestStatusTableViewer.setContentProvider(new RequestStatusTableContentProvider());
		createRequestStatusTableColumns(requestStatusTableViewer, tcl);
		final Table table = requestStatusTableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		requestStatusTableViewer.addSelectionChangedListener(createRequestStatusTableSelectionChangedListener());
		table.setMenu(createRequestStatusTableMenu(table));

		return rootControl;
	}
	
	private String uriToHostString(URI uri) {
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

	private String uriToPathString(URI uri) {
		if (uri.getRawQuery() != null) {
			return uri.getRawPath() + "?" + uri.getRawQuery();
		} else {
			return uri.getRawPath();
		}
	}

	private void createRequestStatusTableColumns(TableViewer viewer, TableColumnLayout layout) {
		final String[] titles = { "Status", "Host", "Method", "Request", "Listener", };
		final ColumnLayoutData[] layoutData = {
			new ColumnPixelData(45, true, true),
			new ColumnPixelData(120, true, true),
			new ColumnPixelData(60, true, true),
			new ColumnWeightData(100, 100, true),
			new ColumnPixelData(100, true, true),
		};
		final ColumnLabelProvider providerList[] = {
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return (((IHttpRequestTask) element).getTimeCompleted() == null) ? "in progress" : "complete"; 
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return uriToHostString(((IHttpRequestTask) element).getRequest().getURI());
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpRequestTask) element).getRequest().getMethod();
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return uriToPathString(((IHttpRequestTask) element).getRequest().getURI());
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					final IRequestOrigin origin = ((IHttpRequestTask) element).getRequestEngine().getRequestOrigin();
					if (origin instanceof IRequestOriginProxy) {
						return origin.toString();
					} else {
						return null;
					}
				}
			},
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

	private ISelectionChangedListener createRequestStatusTableSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				requestStatusTableMenu.setEnabled(!event.getSelection().isEmpty());
			}
		};
	}

	private Menu createRequestStatusTableMenu(Table table) {
		requestStatusTableMenu = new Menu(table);
		requestStatusTableMenu.setEnabled(false);
		
	    MenuItem abortMenuItem = new MenuItem(requestStatusTableMenu, SWT.CASCADE);
	    abortMenuItem.setText("Abort Request");
	    abortMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				abortRequestStatusSelection();
			};
	    });

		return requestStatusTableMenu;
	}

	private void abortRequestStatusSelection() {
		IStructuredSelection selection = (IStructuredSelection) requestStatusTableViewer.getSelection();
		for (Iterator<?> iter = selection.iterator(); iter.hasNext(); ) {
			IHttpRequestTask requestTask = (IHttpRequestTask) iter.next();
			requestTask.abort();
		}
	}
}
