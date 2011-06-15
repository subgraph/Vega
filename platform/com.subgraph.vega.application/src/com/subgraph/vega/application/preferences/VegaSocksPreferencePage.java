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
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.subgraph.vega.application.Activator;

public class VegaSocksPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage, IVegaSocksPreferenceConstants {

	private BooleanFieldEditor enableField;
	private StringFieldEditor addressField;
	private IntegerFieldEditor portField;
	
	public VegaSocksPreferencePage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Configure a SOCKS proxy for all Vega connections");
	}

	@Override
	protected void createFieldEditors() {
		enableField = new BooleanFieldEditor(P_SOCKS_ENABLED, "Enable SOCKS proxy", getFieldEditorParent());
		addField(enableField);
		
		addressField = new StringFieldEditor(P_SOCKS_ADDRESS, "SOCKS Proxy Address", getFieldEditorParent());
		addressField.setEmptyStringAllowed(false);
		addField(addressField);
		
		portField = new IntegerFieldEditor(P_SOCKS_PORT, "Socks Proxy Port", getFieldEditorParent());
		portField.setValidRange(1, 65535);
		portField.setTextLimit(5);
		addField(portField);
		
		setEnableState(getPreferenceStore().getBoolean(P_SOCKS_ENABLED));
	}
	
	@Override public void propertyChange(PropertyChangeEvent event) {
		if(event.getProperty().equals(FieldEditor.VALUE) && event.getSource() == enableField) {
			if(event.getNewValue() instanceof Boolean) {
				setEnableState((Boolean) event.getNewValue());
			}
		}
		super.propertyChange(event);
	}
	
	private void setEnableState(boolean value) {
		addressField.setEnabled(value, getFieldEditorParent());
		portField.setEnabled(value, getFieldEditorParent());
	}
}
