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
package com.subgraph.vega.ui.http.preferencepage;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;

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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;

import com.subgraph.vega.api.http.proxy.IHttpProxyListenerConfig;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.util.preferences.VegaPreferencePage;

public class ProxyListenerPreferencePage extends VegaPreferencePage implements IPreferenceConstants {
	private static final String SEPARATOR_LISTENER = ";"; 
	private static final String SEPARATOR_ADDRESS_PORT = ":";
	private Composite parentComposite;
	private TableViewer listenersTableViewer;
	private Button buttonCreate;
	private Button buttonRemove;
	private ArrayList<IHttpProxyListenerConfig> listenerList = new ArrayList<IHttpProxyListenerConfig>();

	public ProxyListenerPreferencePage() {
		super("Proxy Listen Address");
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createPage(Composite parent) {
		parentComposite = new SashForm(parent, SWT.VERTICAL);
		createListenAddressGroup(parentComposite);
		parsePreferencesString(listenerList, getPreferenceStore().getString(IPreferenceConstants.P_PROXY_LISTENERS));
		listenersTableViewer.setInput(listenerList);
		return parentComposite;
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		parsePreferencesString(listenerList, getPreferenceStore().getDefaultString(IPreferenceConstants.P_PROXY_LISTENERS));
		listenersTableViewer.setInput(listenerList);
	}

	@Override
	public boolean performOk() {
	    boolean rv = super.performOk();
	    if (rv) {
	    	getPreferenceStore().setValue(IPreferenceConstants.P_PROXY_LISTENERS, getPreferencesString());
	    }
	    return rv;
	}

	static public void parsePreferencesString(ArrayList<IHttpProxyListenerConfig> listenerList, final String prefListeners) {
		listenerList.clear();
		if (prefListeners != null) {
			final String[] listeners = prefListeners.split(SEPARATOR_LISTENER);
			for (int listenerIdx = 0; listenerIdx < listeners.length; listenerIdx++) {
				final String[] listenerInfo = listeners[listenerIdx].split(SEPARATOR_ADDRESS_PORT);
				IHttpProxyListenerConfig listenerConfig;
				try {
					listenerConfig = Activator.getDefault().getProxyService().createListenerConfig();
					InetAddress inetAddress = InetAddress.getByName(listenerInfo[0].substring(1, listenerInfo[0].length() - 1));
					listenerConfig.setInetAddress(inetAddress);
					listenerConfig.setPort(Integer.parseInt(listenerInfo[1]));
					listenerList.add(listenerConfig);
				} catch (Exception e) {
					// REVISIT: should log this
					continue;
				}
			}
		}
	}

	private String getPreferencesString() {
		final StringBuilder sb = new StringBuilder();
		for (int idx = 0; idx < listenerList.size(); idx++) {
			IHttpProxyListenerConfig listenerConfig = listenerList.get(idx);
			if (idx != 0) {
				sb.append(SEPARATOR_LISTENER);
			}
			sb.append("[");
			sb.append(listenerConfig.getInetAddress().getHostAddress());
			sb.append("]");
			sb.append(SEPARATOR_ADDRESS_PORT);
			sb.append(Integer.toString(listenerConfig.getPort()));
		}
		return sb.toString();
	}
	
	private Composite createListenAddressGroup(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(2, false));
		rootControl.setText("Listen Addresses");

		final Composite compTable = createListenerTable(rootControl);
		compTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		final Composite compTableButtons = createListenerTableButtons(rootControl);
		compTableButtons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		
		return rootControl;
	}

	private Composite createListenerTable(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		final TableColumnLayout tcl = new TableColumnLayout();
		rootControl.setLayout(tcl);

		listenersTableViewer = new TableViewer(rootControl, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		listenersTableViewer.setContentProvider(new ListenerTableContentProvider());
		listenersTableViewer.addSelectionChangedListener(createSelectionChangedListener());
		createListenerTableColumns(listenersTableViewer, tcl);
		final Table table = listenersTableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		return rootControl;
	}

	private ISelectionChangedListener createSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				boolean sel = event.getSelection().isEmpty(); 
				buttonRemove.setGrayed(sel);
			}
		};
	}

	private void createListenerTableColumns(TableViewer viewer, TableColumnLayout layout) {
		final String[] titles = { "IP Address", "Port", };
		final ColumnLayoutData[] layoutData = {
			new ColumnWeightData(100, 100, true),
			new ColumnPixelData(80, true, true),
		};
		final ColumnLabelProvider providerList[] = {
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpProxyListenerConfig) element).getInetAddress().getHostAddress();
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return Integer.toString(((IHttpProxyListenerConfig) element).getPort());
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

	private Composite createListenerTableButtons(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, true));

		buttonCreate = new Button(rootControl, SWT.PUSH);
		buttonCreate.setText("create");
		buttonCreate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		buttonCreate.addSelectionListener(createSelectionListenerButtonCreate());

		buttonRemove = new Button(rootControl, SWT.PUSH);
		buttonRemove.setText("remove");
		buttonRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		buttonRemove.setGrayed(true);
		buttonRemove.addSelectionListener(createSelectionListenerButtonRemove());

		return rootControl;
	}

	private SelectionListener createSelectionListenerButtonCreate() {
		final ProxyListenerPreferencePage page = this;
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final ListenerAddressDialog dialog = new ListenerAddressDialog(parentComposite.getShell(), page); 
				dialog.create();
				if (dialog.open() == Window.OK) {
					listenerList.add(dialog.getConfig());
					listenersTableViewer.refresh();
				}
			}
		};
	}

	private SelectionListener createSelectionListenerButtonRemove() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) listenersTableViewer.getSelection();
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					listenerList.remove((IHttpProxyListenerConfig) i.next());
				}
				listenersTableViewer.refresh();
			}
		};
	}
	
	public boolean hasListener(InetAddress inetAddress, int port) {
		for (IHttpProxyListenerConfig listener: listenerList) {
			if (listener.getInetAddress().equals(inetAddress) && listener.getPort() == port) {
				return true;
			}
		}
		return false;
	}
	
}
