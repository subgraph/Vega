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
package com.subgraph.vega.ui.tags.taggableeditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
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
import com.subgraph.vega.internal.ui.tags.taggableeditor.TagModifier;
import com.subgraph.vega.internal.ui.tags.taggableeditor.TagTableCheckStateManager;
import com.subgraph.vega.internal.ui.tags.taggableeditor.TagTableContentProvider;
import com.subgraph.vega.internal.ui.tags.taggableeditor.TagTableLabelProvider;
import com.subgraph.vega.internal.ui.tags.taggableeditor.TagTableSearchFilter;
import com.subgraph.vega.ui.tags.Activator;
import com.subgraph.vega.ui.tags.tageditor.TagEditorDialog;
import com.subgraph.vega.ui.tagsl.taggablepopup.ITagModifierValidator;

public class TaggableEditorDialog extends TitleAreaDialog implements ITagModifierValidator {
	protected static final String IStructuredSelection = null;
	private final ITaggable taggable;
	private ITagModel tagModel;
	private IEventHandler workspaceListener;
	private ArrayList<TagModifier> tagList = new ArrayList<TagModifier>();
	private TagModifier tagSelected;
	private Composite parentComposite;
	private Text tagFilterText;
	private CheckboxTableViewer tagTableViewer;
	private TagTableCheckStateManager checkStateManager;
	private TagTableSearchFilter tagTableSearchFilter;
	private Button createButton;
	private Button editButton;
	private Text tagNameText;
	private Text tagDescText;
	private ColorSelector nameColorSelector;
	private ColorSelector rowColorSelector;

	static public TaggableEditorDialog createDialog(Shell parentShell, ITaggable taggable) {
		final TaggableEditorDialog dialog = new TaggableEditorDialog(parentShell, taggable);
		dialog.initialize();
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
	
	private TaggableEditorDialog(Shell parentShell, ITaggable taggable) {
		super(parentShell);
		this.taggable = taggable;
		workspaceListener = new IEventHandler() {
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
		};
		checkStateManager = new TagTableCheckStateManager(); 
		tagTableSearchFilter = new TagTableSearchFilter();
	}

	private void initialize() {
		IWorkspace currentWorkspace = Activator.getDefault().getModel().addWorkspaceListener(workspaceListener);
		tagModel = currentWorkspace.getTagModel();
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
			       "which tags apply to this result.");
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite dialogArea = (Composite) super.createDialogArea(parent);

		parentComposite = new Composite(dialogArea, SWT.NULL);
	    parentComposite.setLayout(new GridLayout(1, false));
	    parentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

	    createTagsArea(parentComposite).setLayoutData(new GridData(GridData.FILL_BOTH));
	    createTagInfoArea(parentComposite).setLayoutData(new GridData(GridData.FILL_BOTH));

		for (ITag tag: tagModel.getAllTags()) {
			TagModifier tagModifier = new TagModifier(tag);
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

	@Override
	protected void cancelPressed() {
		int tagModifiedCnt = 0;
		for (TagModifier tagModifier: tagList) {
			if (tagModifier.isModified()) {
				tagModifiedCnt++;
			}
		}
		if (tagModifiedCnt != 0) {
			if (confirmLoseTagModifications(tagModifiedCnt) == false) {
				return;
			}
		}
		
		super.cancelPressed();
	}
	
	@Override
	public boolean close() {
		if (workspaceListener != null) {
			Activator.getDefault().getModel().removeWorkspaceListener(workspaceListener);
			workspaceListener = null;
		}
		return super.close();
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

		createTagAreaButtonsControl(rootControl).setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));

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
				boolean isEmpty = event.getSelection().isEmpty(); 
				editButton.setEnabled(!isEmpty);
				if (isEmpty == false) {
					final TagModifier tagModifier = (TagModifier)((IStructuredSelection) event.getSelection()).getFirstElement();
					setTagSelected(tagModifier);
				}
			}
		};
	}

	private Composite createTagAreaButtonsControl(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(2, false));

		createButton = new Button(rootControl, SWT.PUSH);
		createButton.setText("Create");
		createButton.addSelectionListener(createSelectionListenerCreateButton());

		editButton = new Button(rootControl, SWT.PUSH);
		editButton.setText("Edit");
		editButton.addSelectionListener(createSelectionListenerEditButton());
		editButton.setEnabled(false);

		return rootControl;
	}

	private SelectionListener createSelectionListenerCreateButton() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TagModifier tag = new TagModifier(tagModel.createTag());
				TagEditorDialog dialog = TagEditorDialog.createDialog(getShell(), tag, TaggableEditorDialog.this);
				if (dialog.open() == IDialogConstants.OK_ID) {
					tagList.add(tag);
					tagTableViewer.refresh();
					tagTableViewer.setSelection(new StructuredSelection(tag));
					setTagSelected(tag);
				}
			}
		};
	}

	private SelectionListener createSelectionListenerEditButton() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final TagModifier tag = (TagModifier)((IStructuredSelection) tagTableViewer.getSelection()).getFirstElement();
				if (tag != null) {
					TagEditorDialog dialog = TagEditorDialog.createDialog(getShell(), tag, TaggableEditorDialog.this);
					if (dialog.open() == IDialogConstants.OK_ID) {
						tagTableViewer.refresh();
						setTagSelected(tag);
					}
				}
			}
		};
	}
	
	private Group createTagInfoArea(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, false));
		rootControl.setText("Tag Information");

		createTagInfoNameControl(rootControl).setLayoutData(new GridData(GridData.FILL_BOTH));
		createTagInfoDescControl(rootControl).setLayoutData(new GridData(GridData.FILL_BOTH));
		createTagInfoColorControl(rootControl).setLayoutData(new GridData(GridData.FILL_BOTH));

		return rootControl;
	}

	private Composite createTagInfoNameControl(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(createGaplessGridLayout(2, false));
		
		final Label label = new Label(rootControl, SWT.NONE);
		label.setText("Name:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		tagNameText = new Text(rootControl, SWT.BORDER | SWT.SINGLE);
		tagNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		tagNameText.setEnabled(false);

		return rootControl;
	}

	private Composite createTagInfoDescControl(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(createGaplessGridLayout(1, false));
		
		final Label label = new Label(rootControl, SWT.NONE);
		label.setText("Description:");

		tagDescText = new Text(rootControl, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		final FontMetrics tagDescTextFm = new GC(tagDescText).getFontMetrics();
		GridData tagDescTextGd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		tagDescTextGd.heightHint = tagDescTextFm.getHeight() * 5;
		tagDescText.setLayoutData(tagDescTextGd);
		tagDescText.setEditable(false);

		return rootControl;
	}
	
	private Composite createTagInfoColorControl(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(createGaplessGridLayout(2, false));

		Label label = new Label(rootControl, SWT.NONE);
		label.setText("Name color:");
		nameColorSelector = new ColorSelector(rootControl);
		nameColorSelector.setColorValue(new RGB(0, 0, 0));
		nameColorSelector.setEnabled(false);

		label = new Label(rootControl, SWT.NONE);
		label.setText("Row background color:");
		rowColorSelector = new ColorSelector(rootControl);
		rowColorSelector.setColorValue(new RGB(255, 255, 255));
		rowColorSelector.setEnabled(false);
		
		return rootControl;
	}

	private RGB tagColorToRgb(int color) {
		return new RGB((color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff);
	}

	private void setTagSelected(TagModifier tag) {
		this.tagSelected = tag;
		if (tag != null) {
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

	private boolean confirmLoseTagModifications(int cnt) {
		MessageBox messageDialog = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		messageDialog.setText("Warning");
		messageDialog.setMessage(cnt + " tags were modified. Proceed without saving?");
		if (messageDialog.open() == SWT.CANCEL) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public String validate(TagModifier modifier) {
		final String name = modifier.getName();
		if (name.isEmpty()) {
			return "Tag name cannot be empty";
		}
		for (TagModifier tagModifier: tagList) {
			if (tagModifier != modifier && tagModifier.getName().equalsIgnoreCase(name)) {
				return "A tag of that name already exists";
			}
		}
		return null;
	}

}
