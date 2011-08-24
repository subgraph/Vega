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
package com.subgraph.vega.ui.util.preferences;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * A flexible replacement for FieldEditorPreferencePage. Requires the inheriting class to set up fields within
 * createContents(Composite).
 */
public abstract class VegaPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, IPropertyChangeListener {
	private ArrayList<FieldEditor> fieldList = new ArrayList<FieldEditor>();

    protected VegaPreferencePage() {
        this("");
    }

    protected VegaPreferencePage(String title) {
        super(title);
    }

    protected VegaPreferencePage(String title, ImageDescriptor image) {
        super(title, image);
    }

	@Override
	protected Control createContents(Composite parent) {
		Control control = createPage(parent);
	    for (Iterator<FieldEditor> iter = fieldList.iterator(); iter.hasNext();) {
	    	FieldEditor editor = iter.next();
			editor.setPage(this);
			editor.setPropertyChangeListener(this);
			editor.setPreferenceStore(getPreferenceStore());
	    	editor.load();
	    }
		checkState();
		return control;
	}

	/**
	 * Create a SWT control containing page contents for the preferences page.
	 * @param parent Parent control.
	 * @return Created control.
	 */
	abstract protected Control createPage(Composite parent);
	
	@Override
	protected void performDefaults() {
	    super.performDefaults();
	    for (Iterator<FieldEditor> iter = fieldList.iterator(); iter.hasNext();) {
	    	((FieldEditor) iter.next()).loadDefault();
	    }
		checkState();
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
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(FieldEditor.IS_VALID)) {
            boolean value = ((Boolean) event.getNewValue()).booleanValue();
            if (value) {
                checkState();
            } else {
                setValid(false);
            }
        }
	}

    /**
     * Add a field editor to the list of field editors managed on the page.
     * @param editor FieldEditor.
     */
	protected void addField(FieldEditor editor) {
		fieldList.add(editor);
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
