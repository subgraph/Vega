package com.subgraph.vega.application.preferences;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
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
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.subgraph.vega.application.Activator;

public class ProxyPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, IPropertyChangeListener, IPreferenceConstants {
	private Composite parentComposite;
	private ArrayList<FieldEditor> fieldList = new ArrayList<FieldEditor>();
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
	}

	@Override
	protected Control createContents(Composite parent) {
		parentComposite = new SashForm(parent, SWT.VERTICAL);
		createSocksGroup(parentComposite);
		createHttpProxyGroup(parentComposite);
		checkState();
		return parentComposite;
	}

	@Override
	public boolean performOk() {
	    boolean rv = super.performOk();
	    if (rv) {
		    for (Iterator<FieldEditor> iter = fieldList.iterator(); iter.hasNext();) {
		    	((FieldEditor) iter.next()).store();
		    }
	    }
	    return rv;
	}

	@Override
	protected void performDefaults() {
	    super.performDefaults();
	    for (Iterator<FieldEditor> iter = fieldList.iterator(); iter.hasNext();) {
	    	((FieldEditor) iter.next()).loadDefault();
	    }
	    final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setSocksEnableState(store.getBoolean(IPreferenceConstants.P_SOCKS_ENABLED));
		setHttpProxyEnableState(store.getBoolean(IPreferenceConstants.P_PROXY_ENABLED));
		checkState();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(FieldEditor.VALUE)) {
			if (event.getSource() == socksEnableField) {
				setSocksEnableState((Boolean) event.getNewValue());
			} else if (event.getSource() == httpProxyEnableField) {
				setHttpProxyEnableState((Boolean) event.getNewValue());
			}
		} else if (event.getProperty().equals(FieldEditor.IS_VALID)) {
            boolean value = ((Boolean) event.getNewValue()).booleanValue();
            if (value) {
                checkState();
            } else {
                setValid(false);
            }
        }
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
//		socksConfigControl.setLayout(new GridLayout(2, false));
		socksConfigControl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		socksAddressField = new StringFieldEditor(P_SOCKS_ADDRESS, "Proxy Address", socksConfigControl);
		socksAddressField.setEmptyStringAllowed(false);
		addField(socksAddressField);

		socksPortField = new IntegerFieldEditor(P_SOCKS_PORT, "Proxy Port", socksConfigControl);
		socksPortField.setValidRange(1, 65535);
		socksPortField.setTextLimit(5);
		addField(socksPortField);
		
		setSocksEnableState(Activator.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.P_SOCKS_ENABLED));
		
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
//		httpProxyConfigControl.setLayout(new GridLayout(2, false));
		httpProxyConfigControl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		httpProxyAddressField = new StringFieldEditor(P_PROXY_ADDRESS, "Proxy Address", httpProxyConfigControl);
		httpProxyAddressField.setEmptyStringAllowed(false);
		addField(httpProxyAddressField);

		httpProxyPortField = new IntegerFieldEditor(P_PROXY_PORT, "Proxy Port", httpProxyConfigControl);
		httpProxyPortField.setValidRange(1, 65535);
		httpProxyPortField.setTextLimit(5);
		addField(httpProxyPortField);

		setHttpProxyEnableState(Activator.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.P_PROXY_ENABLED));

		return rootControl;
	}

	private void addField(FieldEditor editor) {
		fieldList.add(editor);
		editor.setPage(this);
		editor.setPropertyChangeListener(this);
		editor.setPreferenceStore(Activator.getDefault().getPreferenceStore());
		editor.load();
	}

	private void setSocksEnableState(Boolean enable) {
		socksAddressField.setEnabled(enable, socksConfigControl);
		socksPortField.setEnabled(enable, socksConfigControl);
	}

	private void setHttpProxyEnableState(Boolean enable) {
		httpProxyAddressField.setEnabled(enable, httpProxyConfigControl);
		httpProxyPortField.setEnabled(enable, httpProxyConfigControl);
	}
	
	private void checkState() {
	    for (Iterator<FieldEditor> iter = fieldList.iterator(); iter.hasNext();) {
	    	if (!((FieldEditor) iter.next()).isValid()) {
	    		setValid(false);
	    		return;
	    	}
	    }
	    setValid(true);
	}

}
