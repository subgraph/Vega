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
package com.subgraph.vega.ui.model.taggableeditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.tags.ITag;
import com.subgraph.vega.api.model.tags.ITagModel;
import com.subgraph.vega.api.model.tags.ITaggable;
import com.subgraph.vega.internal.ui.model.taggableeditor.TagModifier;
import com.subgraph.vega.internal.ui.model.taggableeditor.TagTableCheckStateManager;
import com.subgraph.vega.internal.ui.model.taggableeditor.TagTableContentProvider;
import com.subgraph.vega.internal.ui.model.taggableeditor.TagTableLabelProvider;
import com.subgraph.vega.internal.ui.model.taggableeditor.TagTableSearchFilter;
import com.subgraph.vega.ui.model.Activator;

public class TaggableEditorDialog extends TitleAreaDialog {
	protected static final String IStructuredSelection = null;
	private ITagModel tagModel;
	private ITaggable taggable;
	private ArrayList<TagModifier> tagList = new ArrayList<TagModifier>();
	private TagModifier tagSelected;
	private Composite parentComposite;
	private Text tagFilterText;
	private CheckboxTableViewer tagTableViewer;
	private TagTableCheckStateManager checkStateManager;
	private TagTableSearchFilter tagTableSearchFilter;
	private Text tagNameText;
	private Text tagDescText;
	private ColorSelector nameColorSelector;
	private ColorSelector rowColorSelector;
	private Button tagButtonClear;
	private Button tagButtonRestore;
	private Button tagButtonSave;

	public TaggableEditorDialog(Shell parentShell, ITaggable taggable) {
		super(parentShell);
		this.taggable = taggable;
		IWorkspace currentWorkspace = Activator.getDefault().getModel().addWorkspaceListener(new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if (event instanceof WorkspaceOpenEvent) {
					handleWorkspaceOpen((WorkspaceOpenEvent) event);
				} else if (event instanceof WorkspaceCloseEvent) {
					handleWorkspaceClose((WorkspaceCloseEvent) event);
				} else if (event instanceof WorkspaceResetEvent) {
					handleWorkspaceReset((WorkspaceResetEvent) event);
				}
			}
		});
		tagModel = currentWorkspace.getTagModel();
		checkStateManager = new TagTableCheckStateManager(); 
		tagTableSearchFilter = new TagTableSearchFilter();
	}

	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		tagModel = event.getWorkspace().getTagModel();
	}

	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		// REVISIT this is really bad. pop up a warning and exit?
		tagModel = null;
	}

	private void handleWorkspaceReset(WorkspaceResetEvent event) {
		tagModel = event.getWorkspace().getTagModel();
	}

	@Override
	public void create() {
		super.create();
		setTitle("Select Tags");
		setMessage("Tags can be used to signify a result as noteworthy and to simplify searching for it. Select " +
			       "which tags apply to this result or create and edit tags below.");
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite dialogArea = (Composite) super.createDialogArea(parent);

		parentComposite = new Composite(dialogArea, SWT.NULL);
	    parentComposite.setLayout(new GridLayout(1, false));
	    parentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

	    createTagsArea(parentComposite).setLayoutData(new GridData(GridData.FILL_BOTH));
		createTagEditArea(parentComposite).setLayoutData(new GridData(GridData.FILL_BOTH));

		for (ITag tag: tagModel.getAllTags()) {
			TagModifier tagModifier = new TagModifier(tag, tagModel.createTag(tag));
			tagList.add(tagModifier);
			for (ITag tagged: taggable.getAllTags()) {
				if (tagModifier.getTagOrig() == tagged) {
					checkStateManager.addChecked(tagModifier);
					break;
				}
			}
		}
		tagTableViewer.setInput(tagList);

		setTagSelected(null);
		
		return dialogArea;
	}

	@Override
	protected void okPressed() {
		for (TagModifier tagModifier: tagList) {
			if (tagModifier.isModified()) {
				tagModifier.store(tagModel);
			}
		}

		List<TagModifier> checked = checkStateManager.getCheckedList(); 
		ArrayList<ITag> checkedList = new ArrayList<ITag>(checked.size());
		for (Object tagModifier: checked) {
			checkedList.add(((TagModifier) tagModifier).getTagOrig());
		}
		taggable.setTags(checkedList);

		super.okPressed();
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
	
	private Control createTagsArea(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, false));
		rootControl.setText("Available Tags");

		tagFilterText = new Text(rootControl, SWT.SEARCH);
		tagFilterText.setLayoutData(new GridData(GridData.FILL_BOTH));
		tagFilterText.setMessage("type filter text");
		tagFilterText.addModifyListener(createTagFilterModifyListener());

		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		final Control tagTableControl = createTagTable(rootControl, gd, 7);
		tagTableControl.setLayoutData(gd);
		
		return rootControl;
	}

	private ModifyListener createTagFilterModifyListener() {
		return new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				final String matchFilter = tagFilterText.getText();
				if (!matchFilter.isEmpty()) {
					tagTableSearchFilter.setMatchFilter(matchFilter);
				} else {					
					tagTableSearchFilter.setMatchFilter(null);
				}
				tagTableViewer.refresh();
			}
		};
	}
	
	private Control createTagTable(Composite parent, GridData gd, int heightInRows) {
		tagTableViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER);
		tagTableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		tagTableViewer.setLabelProvider(new TagTableLabelProvider());
		tagTableViewer.setContentProvider(new TagTableContentProvider());
		tagTableViewer.addSelectionChangedListener(createSelectionChangedListener());
		tagTableViewer.setCheckStateProvider(checkStateManager);
		tagTableViewer.addCheckStateListener(checkStateManager);
		tagTableViewer.addFilter(tagTableSearchFilter);
		gd.heightHint = tagTableViewer.getTable().getItemHeight() * heightInRows;
		return tagTableViewer.getTable();
	}

	private ISelectionChangedListener createSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty() == false) {
					final TagModifier tagModifier = (TagModifier)((IStructuredSelection) event.getSelection()).getFirstElement();
					setTagSelected(tagModifier);
				}
			}
		};
	}

	private Group createTagEditArea(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, false));
		rootControl.setText("Tag Information");

		createTagEditNameControl(rootControl).setLayoutData(new GridData(GridData.FILL_BOTH));
		createTagEditDescControl(rootControl).setLayoutData(new GridData(GridData.FILL_BOTH));
		createTagEditColorControl(rootControl).setLayoutData(new GridData(GridData.FILL_BOTH));
		createTagEditButtonControl(rootControl).setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));

		return rootControl;
	}
	
	private Composite createTagEditNameControl(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(createGaplessGridLayout(2, false));
		
		final Label label = new Label(rootControl, SWT.NONE);
		label.setText("Name:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		tagNameText = new Text(rootControl, SWT.BORDER | SWT.SINGLE);
		tagNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		return rootControl;
	}

	private Composite createTagEditDescControl(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(createGaplessGridLayout(1, false));
		
		final Label label = new Label(rootControl, SWT.NONE);
		label.setText("Description:");

		tagDescText = new Text(rootControl, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		final FontMetrics tagDescTextFm = new GC(tagDescText).getFontMetrics();
		GridData tagDescTextGd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		tagDescTextGd.heightHint = tagDescTextFm.getHeight() * 5;
		tagDescText.setLayoutData(tagDescTextGd);

		return rootControl;
	}
	
	private Composite createTagEditColorControl(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(createGaplessGridLayout(2, false));

		Label label = new Label(rootControl, SWT.NONE);
		label.setText("Name color:");
		nameColorSelector = new ColorSelector(rootControl);

		label = new Label(rootControl, SWT.NONE);
		label.setText("Row background color:");
		rowColorSelector = new ColorSelector(rootControl);
		
		return rootControl;
	}

	private RGB tagColorToRgb(int color) {
		return new RGB((color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff);
	}

	private int rgbToTagColor(RGB rgb) {
		return (rgb.red << 16) | (rgb.green << 8) | rgb.blue; 
	}

	private Composite createTagEditButtonControl(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(3, false));

		tagButtonClear = new Button(rootControl, SWT.PUSH);
		tagButtonClear.setText("Clear");
		tagButtonClear.addSelectionListener(createSelectionListenerButtonTagClear());

		tagButtonRestore = new Button(rootControl, SWT.PUSH);
		tagButtonRestore.setText("Restore");
		tagButtonRestore.addSelectionListener(createSelectionListenerButtonTagRestore());

		tagButtonSave = new Button(rootControl, SWT.PUSH);
		tagButtonSave.setText("Save");
		tagButtonSave.addSelectionListener(createSelectionListenerButtonTagSave());

		return rootControl;
	}

	private SelectionListener createSelectionListenerButtonTagClear() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setTagSelected(null);
			}
		};
	}
	
	private SelectionListener createSelectionListenerButtonTagRestore() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setTagSelected(tagSelected);
			}
		};
	}

	private SelectionListener createSelectionListenerButtonTagSave() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final String name = tagNameText.getText().trim();
				if (name.isEmpty()) {
					setErrorMessage("Tag name cannot be empty");
					return;
				}
				for (TagModifier tagModifier: tagList) {
					if (tagModifier != tagSelected && tagModifier.getTagMod().getName().equalsIgnoreCase(name)) {
						setErrorMessage("A tag of that name already exists");
						return;
					}
				}
				setErrorMessage(null);

				if (tagSelected == null) {
					tagSelected = new TagModifier(null, tagModel.createTag());
					tagList.add(tagSelected);
				}
				final ITag tagMod = tagSelected.getTagMod(); 
				tagMod.setName(name);
				tagMod.setDescription(tagDescText.getText().trim());
				tagMod.setNameColor(rgbToTagColor(nameColorSelector.getColorValue()));
				tagMod.setRowColor(rgbToTagColor(rowColorSelector.getColorValue()));
				tagSelected.setModified();
				
				tagTableViewer.refresh();
				tagTableViewer.setSelection(null, false);
				setTagSelected(null);
			}
		};
	}

	private void setTagSelected(TagModifier tagModifier) {
		this.tagSelected = tagModifier;
		if (tagModifier != null) {
			final ITag tag = tagModifier.getTagMod(); 
			tagNameText.setText(tag.getName());
			if (tag.getDescription() != null) {
				tagDescText.setText(tag.getDescription());
			} else {
				tagDescText.setText("");
			}
			nameColorSelector.setColorValue(tagColorToRgb(tag.getNameColor()));
			rowColorSelector.setColorValue(tagColorToRgb(tag.getRowColor()));
		} else {
			tagNameText.setText("");
			tagDescText.setText("");
			nameColorSelector.setColorValue(new RGB(0, 0, 0));
			rowColorSelector.setColorValue(new RGB(255, 255, 255));
		}
	}

}
