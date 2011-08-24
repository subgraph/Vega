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
package com.subgraph.vega.application.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;

import com.subgraph.vega.application.Activator;
import com.subgraph.vega.ui.util.preferences.VegaPreferencePage;

public class ProxyPreferencePage extends VegaPreferencePage implements IPropertyChangeListener, IPreferenceConstants {
	private Composite parentComposite;
	private Composite socksConfigControl;
	private BooleanFieldEditor socksEnableField;
	private StringFieldEditor socksAddressField;
	private IntegerFieldEditor socksPortField;
	private Composite httpProxyConfigControl;
	private BooleanFieldEditor httpProxyEnableField;
	private StringFieldEditor httpProxyAddressField;
	private IntegerFieldEditor httpProxyPortField;

	public ProxyPreferencePage() {
		super("External Proxy Options");
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createPage(Composite parent) {
		parentComposite = new SashForm(parent, SWT.VERTICAL);
		createSocksGroup(parentComposite);
		createHttpProxyGroup(parentComposite);
		updateEnableState();
		return parentComposite;
	}


	@Override
	protected void performDefaults() {
		super.performDefaults();
		updateEnableState();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(FieldEditor.VALUE)) {
			if (event.getSource() == socksEnableField) {
				setSocksEnableState((Boolean) event.getNewValue());
			} else if (event.getSource() == httpProxyEnableField) {
				setHttpProxyEnableState((Boolean) event.getNewValue());
			}
		}
		super.propertyChange(event);
	}
	
	private Composite createSocksGroup(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, false));
		rootControl.setText("SOCKS proxy");

		Label label = new Label(rootControl, SWT.NONE);
		label.setText("Configure Vega to use a SOCKS proxy for all connections");

		socksEnableField = new BooleanFieldEditor(P_SOCKS_ENABLED, "Enable SOCKS proxy", rootControl);
		addField(socksEnableField);
		
		socksConfigControl = new Composite(rootControl, SWT.NONE);
		socksConfigControl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		socksAddressField = new StringFieldEditor(P_SOCKS_ADDRESS, "Proxy Address", socksConfigControl);
		socksAddressField.setEmptyStringAllowed(false);
		addField(socksAddressField);

		socksPortField = new IntegerFieldEditor(P_SOCKS_PORT, "Proxy Port", socksConfigControl);
		socksPortField.setValidRange(1, 65535);
		socksPortField.setTextLimit(5);
		addField(socksPortField);
		
		return rootControl;
	}

	private Composite createHttpProxyGroup(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, false));
		rootControl.setText("External HTTP proxy");

		Label label = new Label(rootControl, SWT.NONE);
		label.setText("Configure Vega to send all requests through an external HTTP proxy");

		httpProxyEnableField = new BooleanFieldEditor(P_PROXY_ENABLED, "Enable HTTP proxy", rootControl);
		addField(httpProxyEnableField);
		
		httpProxyConfigControl = new Composite(rootControl, SWT.NONE);
		httpProxyConfigControl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		httpProxyAddressField = new StringFieldEditor(P_PROXY_ADDRESS, "Proxy Address", httpProxyConfigControl);
		httpProxyAddressField.setEmptyStringAllowed(false);
		addField(httpProxyAddressField);

		httpProxyPortField = new IntegerFieldEditor(P_PROXY_PORT, "Proxy Port", httpProxyConfigControl);
		httpProxyPortField.setValidRange(1, 65535);
		httpProxyPortField.setTextLimit(5);
		addField(httpProxyPortField);

		return rootControl;
	}

	private void updateEnableState() {
	    final IPreferenceStore store = getPreferenceStore();
		setSocksEnableState(store.getBoolean(IPreferenceConstants.P_SOCKS_ENABLED));
		setHttpProxyEnableState(store.getBoolean(IPreferenceConstants.P_PROXY_ENABLED));
	}

	private void setSocksEnableState(Boolean enable) {
		socksAddressField.setEnabled(enable, socksConfigControl);
		socksPortField.setEnabled(enable, socksConfigControl);
	}

	private void setHttpProxyEnableState(Boolean enable) {
		httpProxyAddressField.setEnabled(enable, httpProxyConfigControl);
		httpProxyPortField.setEnabled(enable, httpProxyConfigControl);
	}

}
