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
import java.net.UnknownHostException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.api.http.proxy.IHttpProxyListenerConfig;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.util.dialogs.ErrorDialog;

public class ListenerAddressDialog extends Dialog {
	private ProxyListenerPreferencePage page;
	private Composite parentComposite;
	private Text ipAddress;
	private Text port;
	private int portNum;
	private InetAddress inetAddress;

	public ListenerAddressDialog(Shell parent, ProxyListenerPreferencePage page) {
		super(parent);
		this.page = page;
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Add Listen Address");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parentComposite = (Composite) super.createDialogArea(parent);
		createFields(parentComposite);		
		return parentComposite;
	}
	
	@Override
	protected void okPressed() {
		if (parseInput() == true) {
			super.okPressed();
		}
	}

	private Composite createFields(Composite parent) {
		Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(2, true));

		Label label = new Label(rootControl, SWT.NONE);
		label.setText("IP Address:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		ipAddress = new Text(rootControl, SWT.BORDER);
//		ipAddress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		ipAddress.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9') && chars[i] != '.') {
						e.doit = false;
						return;
					}
				}
			}
		});

		label = new Label(rootControl, SWT.NONE);
		label.setText("Port:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		port = new Text(rootControl, SWT.BORDER);
		port.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		port.addListener(SWT.Verify, new Listener() { // REVISIT: should create a helper method for this
			public void handleEvent(Event e) {
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});
		
		return rootControl;
	}
	
	public IHttpProxyListenerConfig getConfig() {
		final IHttpProxyListenerConfig listenerConfig = Activator.getDefault().getProxyService().createListenerConfig();
		listenerConfig.setInetAddress(inetAddress);
		listenerConfig.setPort(portNum);
		return listenerConfig;
	}

	private boolean parseInput() {
		try {
			inetAddress = InetAddress.getByName(ipAddress.getText());
		} catch (UnknownHostException e) {
			ErrorDialog.displayError(getShell(), "Invalid IP address");
	        return false;
		}

		try {
			portNum = Integer.parseInt(port.getText());
		} catch (NumberFormatException e) {
			ErrorDialog.displayError(getShell(), "Invalid port: must be between 1 and 65535");
	        return false;
		}		
		if (portNum < 1 || portNum > 65535) {
			ErrorDialog.displayError(getShell(), "Invalid port: must be between 1 and 65535");
	        return false;
		}

		if (page.hasListener(inetAddress, portNum) == true) {
			ErrorDialog.displayError(getShell(), "IP address/port combination is already registered");
	        return false;
		}
		
		return true;
	}
	
}
