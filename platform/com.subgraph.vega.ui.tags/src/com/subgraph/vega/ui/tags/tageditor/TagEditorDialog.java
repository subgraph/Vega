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
package com.subgraph.vega.ui.tags.tageditor;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.internal.ui.tags.taggableeditor.TagModifier;
import com.subgraph.vega.ui.tagsl.taggablepopup.ITagModifierValidator;

public class TagEditorDialog extends TitleAreaDialog {
	private final ITagModifierValidator validator;
	private final TagModifier tag;
	private Composite parentComposite;
	private boolean tagModified;
	private Text tagNameText;
	private Text tagDescText;
	private ColorSelector nameColorSelector;
	private ColorSelector rowColorSelector;

	static public TagEditorDialog createDialog(Shell parentShell, TagModifier tag, ITagModifierValidator validator) {
		final TagEditorDialog dialog = new TagEditorDialog(parentShell, tag, validator);
		dialog.create();
		dialog.getShell().addListener(SWT.Traverse, new Listener() {
        	public void handleEvent(Event e) {
        		if (e.detail == SWT.TRAVERSE_ESCAPE) {
        			e.doit = false;
        		}
        	}
        });
		return dialog;
	}

	private TagEditorDialog(Shell parentShell, TagModifier tag, ITagModifierValidator validator) {
		super(parentShell);
		this.tag = tag;
		this.validator = validator;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Edit Tag");
		setMessage("Edit the properties of the tag below.");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite dialogArea = (Composite) super.createDialogArea(parent);

		parentComposite = new Composite(dialogArea, SWT.NULL);
	    parentComposite.setLayout(new GridLayout(1, false));
	    parentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createArea(parentComposite).setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return dialogArea;
	}

	@Override
	protected void okPressed() {
		tag.setName(tagNameText.getText().trim());
		tag.setDescription(tagDescText.getText().trim());
		tag.setNameColor(rgbToTagColor(nameColorSelector.getColorValue()));
		tag.setRowColor(rgbToTagColor(rowColorSelector.getColorValue()));

		final String errorMsg = validator.validate(tag);
		setErrorMessage(errorMsg);
		if (errorMsg != null) {
			return;
		}

		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		if (tagModified) {
			if (confirmLoseTagModification() == false) {
				return;
			}
		}

		super.cancelPressed();
	}
	
	private GridLayout createGaplessGridLayout(int numColumns, boolean makeColumnsEqualWidth) {
		final GridLayout layout = new GridLayout(numColumns, makeColumnsEqualWidth);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginTop = 0;
		layout.marginRight = 0;
		layout.marginBottom = 0;
		return layout;
	}

	private Group createArea(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, false));
		rootControl.setText("Tag Information");

		createNameControl(rootControl).setLayoutData(new GridData(GridData.FILL_BOTH));
		createDescControl(rootControl).setLayoutData(new GridData(GridData.FILL_BOTH));
		createColorControl(rootControl).setLayoutData(new GridData(GridData.FILL_BOTH));
		setTagFields();

		return rootControl;
	}
	
	private Composite createNameControl(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(createGaplessGridLayout(2, false));
		
		final Label label = new Label(rootControl, SWT.NONE);
		label.setText("Name:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		tagNameText = new Text(rootControl, SWT.BORDER | SWT.SINGLE);
		tagNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		tagNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				tagModified = true;
			}
		});

		return rootControl;
	}

	private Composite createDescControl(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(createGaplessGridLayout(1, false));
		
		final Label label = new Label(rootControl, SWT.NONE);
		label.setText("Description:");

		tagDescText = new Text(rootControl, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		final FontMetrics tagDescTextFm = new GC(tagDescText).getFontMetrics();
		GridData tagDescTextGd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		tagDescTextGd.heightHint = tagDescTextFm.getHeight() * 5;
		tagDescText.setLayoutData(tagDescTextGd);
		tagDescText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				tagModified = true;
			}
		});

		return rootControl;
	}
	
	private Composite createColorControl(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(createGaplessGridLayout(2, false));

		Label label = new Label(rootControl, SWT.NONE);
		label.setText("Name color:");
		nameColorSelector = new ColorSelector(rootControl);
		nameColorSelector.addListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals("colorValue")) {
					tagModified = true;
				}
			}
		});

		label = new Label(rootControl, SWT.NONE);
		label.setText("Row background color:");
		rowColorSelector = new ColorSelector(rootControl);
		rowColorSelector.addListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals("colorValue")) {
					tagModified = true;
				}
			}
		});
		
		return rootControl;
	}

	private void setTagFields() {
		if (tag.getName() != null) {
			tagNameText.setText(tag.getName());
		} else {
			tagNameText.setText("");
		}
		if (tag.getDescription() != null) {
			tagDescText.setText(tag.getDescription());
		} else {
			tagDescText.setText("");
		}
		nameColorSelector.setColorValue(tagColorToRgb(tag.getNameColor()));
		rowColorSelector.setColorValue(tagColorToRgb(tag.getRowColor()));
		tagModified = false;
	}
	
	private RGB tagColorToRgb(int color) {
		return new RGB((color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff);
	}

	private int rgbToTagColor(RGB rgb) {
		return (rgb.red << 16) | (rgb.green << 8) | rgb.blue; 
	}

	private boolean confirmLoseTagModification() {
		MessageBox messageDialog = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
		messageDialog.setText("Warning");
		messageDialog.setMessage("Changes were made to the tag. Proceed without saving?");
		if (messageDialog.open() == SWT.CANCEL) {
			return false;
		} else {
			return true;
		}
	}
		
}
