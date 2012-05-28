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
package com.subgraph.vega.ui.macros.macrodialog;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.macros.IHttpMacro;
import com.subgraph.vega.api.model.macros.IHttpMacroItem;
import com.subgraph.vega.api.model.macros.IHttpMacroModel;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.internal.ui.macros.macrodialog.MacroItemEditor;
import com.subgraph.vega.internal.ui.macros.macrodialog.MacroItemSelectionDialog;
import com.subgraph.vega.internal.ui.macros.macrodialog.MacroItemTableContentProvider;
import com.subgraph.vega.ui.http.builder.HeaderEditor;
import com.subgraph.vega.ui.http.builder.IHttpBuilderPart;
import com.subgraph.vega.ui.http.builder.RequestEditor;
import com.subgraph.vega.ui.macros.Activator;
import com.subgraph.vega.ui.util.dialogs.ErrorDialog;

public class MacroDialog extends TitleAreaDialog {
	private IHttpMacroModel macroModel;
	private IHttpMacro macro;
	private IHttpRequestBuilder requestBuilder;
	private Composite parentComposite;
	private Text macroNameText;
	private TableViewer macroItemTableViewer;
	private Button addItemButton;
	private Button moveUpButton;
	private Button moveDownButton;
	private Button removeButton;
	private TabFolder macroItemTabFolder;
	private TabItem macroItemTabFolderItem;
	private MacroItemEditor macroItemEditor;
	private IHttpBuilderPart requestBuilderPartCurr;
	private boolean requestIsEditable;
	
	public static MacroDialog createDialog(Shell parentShell) {
		return createDialog(parentShell, null);
	}

	static public MacroDialog createDialog(Shell parentShell, IHttpMacro macro) {
		final MacroDialog dialog = new MacroDialog(parentShell, macro);
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
	
	private MacroDialog(Shell parentShell, IHttpMacro macro) {
		super(parentShell);
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
		macroModel = currentWorkspace.getHttpMacroModel();
		if (macro != null) {
			this.macro = macro;
		} else {
			this.macro = macroModel.createMacro();
		}
		requestBuilder = Activator.getDefault().getHttpRequestEngineFactoryService().createRequestBuilder();
	}
	
	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		macroModel = event.getWorkspace().getHttpMacroModel();
	}

	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		macroModel = null;
	}

	private void handleWorkspaceReset(WorkspaceResetEvent event) {
		macroModel = event.getWorkspace().getHttpMacroModel();
	}

	public IHttpMacro getMacro() {
		return macro;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		final String macroName = macroNameText.getText().trim();
		if (macroName.isEmpty()) {
			setErrorMessage("The macro name cannot be empty");
			return;
		}
		final IHttpMacro macroStored = macroModel.getMacroByName(macroName); 
		if (macroStored != null && macroStored != macro) {
			setErrorMessage("A macro by that name already exists");
			return;
		}

		setErrorMessage(null);		
		macro.setName(macroName);
		macroModel.store(macro);
		super.okPressed();
	}
	
	@Override
	public void create() {
		super.create();
		setTitle("Macro Editor");
		setMessage("Select items for the macro, then modify their characteristics");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite dialogArea = (Composite) super.createDialogArea(parent);
		parentComposite = new Composite(dialogArea, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, false));
		parentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createMacroArea(parentComposite).setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
	    createItemsArea(parentComposite).setLayoutData(new GridData(GridData.FILL_BOTH));
	    createItemEditor(parentComposite).setLayoutData(new GridData(GridData.FILL_BOTH));
	    macroItemTableViewer.setInput(macro);
	    
		return dialogArea;
	}

	private Composite createMacroArea(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(2, false));
		rootControl.setText("Macro");

		Label label = new Label(rootControl, SWT.NONE);
		label.setText("Macro Name:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		macroNameText = new Text(rootControl, SWT.BORDER | SWT.SINGLE);
		macroNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));;
		final FontMetrics macroNameTextFm = new GC(macroNameText).getFontMetrics();
		GridData requestPortGd = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		requestPortGd.widthHint = macroNameTextFm.getAverageCharWidth() * 50;
		macroNameText.setLayoutData(requestPortGd);

		return rootControl;
	}

	private Composite createItemsArea(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(2, false));
		rootControl.setText("Macro Items");

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		createItemsTable(rootControl, gd, 8).setLayoutData(gd);

		createItemsButtons(rootControl).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		return rootControl;
	}

	private Composite createItemsTable(Composite parent, GridData gd, int heightInRows) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		final TableColumnLayout tcl = new TableColumnLayout();
		rootControl.setLayout(tcl);

		macroItemTableViewer = new TableViewer(rootControl, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		macroItemTableViewer.setContentProvider(new MacroItemTableContentProvider());
		macroItemTableViewer.addSelectionChangedListener(createMacroItemTableSelectionChangedListener());
		createItemsTableColumns(macroItemTableViewer, tcl);
		final Table table = macroItemTableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		gd.heightHint = table.getItemHeight() * heightInRows;

		return rootControl;
	}

	private ISelectionChangedListener createMacroItemTableSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				boolean hasSelection = !event.getSelection().isEmpty();
				moveUpButton.setEnabled(hasSelection);
				moveDownButton.setEnabled(hasSelection);
				removeButton.setEnabled(hasSelection);
				if (hasSelection == true) {
					final IHttpMacroItem macroItem = (IHttpMacroItem)((IStructuredSelection) event.getSelection()).getFirstElement();
					setMacroItemSelected(macroItem);
				} else {
					setMacroItemSelected(null);
				}
			}
		};
	}
	
	private void createItemsTableColumns(TableViewer viewer, TableColumnLayout layout) {
		final String[] titles = { "Host", "Method", "Request", "Status", };
		final ColumnLayoutData[] layoutData = {
			new ColumnPixelData(120, true, true),
			new ColumnPixelData(60, true, true),
			new ColumnWeightData(100, 500, true),
			new ColumnPixelData(50, true, true),
		};
		final ColumnLabelProvider providerList[] = {
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpMacroItem) element).getRequestLogRecord().getHttpHost().toURI();
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpMacroItem) element).getRequestLogRecord().getRequest().getRequestLine().getMethod();
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					URI uri;
					try {
						uri = new URI(((IHttpMacroItem) element).getRequestLogRecord().getRequest().getRequestLine().getUri());
					} catch (URISyntaxException e) {
						return null;
					}
					if (uri.getRawQuery() != null) {
						return uri.getRawPath() + "?" + uri.getRawQuery();
					} else {
						return uri.getRawPath();
					}
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return Integer.valueOf(((IHttpMacroItem) element).getRequestLogRecord().getResponse().getStatusLine().getStatusCode()).toString();
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

	private Composite createItemsButtons(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, true));

		addItemButton = new Button(rootControl, SWT.PUSH);
		addItemButton.setText("add item");
		addItemButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		addItemButton.addSelectionListener(createAddItemButtonSelectionListener());

		moveUpButton = new Button(rootControl, SWT.PUSH);
		moveUpButton.setText("move up");
		moveUpButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		moveUpButton.setEnabled(false);
		moveUpButton.addSelectionListener(createMoveUpButtonSelectionListener());

		moveDownButton = new Button(rootControl, SWT.PUSH);
		moveDownButton.setText("move down");
		moveDownButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		moveDownButton.setEnabled(false);
		moveDownButton.addSelectionListener(createMoveDownButtonSelectionListener());

		removeButton = new Button(rootControl, SWT.PUSH);
		removeButton.setText("remove");
		removeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(createRemoveButtonSelectionListener());

		return rootControl;
	}

	private SelectionListener createAddItemButtonSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MacroItemSelectionDialog dialog = new MacroItemSelectionDialog(parentComposite.getShell());
				if (dialog.open() == IDialogConstants.OK_ID) {
					List<IRequestLogRecord> selectionList = dialog.getSelectionList();
					for (Iterator<IRequestLogRecord> iter = selectionList.iterator(); iter.hasNext();) {
						try {
							macro.createMacroItem(iter.next());
						} catch (URISyntaxException ex) {
							ErrorDialog.displayError(getShell(), "An unexpected error occurred while processing the URI");
							ex.printStackTrace();
							continue;
						} catch (IOException ex) {
							ErrorDialog.displayError(getShell(), null);
							ex.printStackTrace();
							continue;
						}
					}
					macroItemTableViewer.refresh();
				}
			}
		};
	}

	private SelectionListener createMoveUpButtonSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) macroItemTableViewer.getSelection();
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					int idx = macro.indexOfMacroItem((IHttpMacroItem) i.next());
					if (idx != 0) {
						macro.swapMacroItems(idx - 1, idx);
					} else {
						break;
					}
				}
				macroItemTableViewer.refresh();
			}
		};
	}

	private SelectionListener createMoveDownButtonSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) macroItemTableViewer.getSelection();
				int idx[] = new int[selection.size()];
				int offset = 1;
				for (Iterator<?> i = selection.iterator(); i.hasNext(); offset++) {
					idx[idx.length - offset] = macro.indexOfMacroItem((IHttpMacroItem) i.next());
				}

				if (idx[0] + 1 != macro.macroItemsSize()) {
					for (int i = 0; i < idx.length; i++) {
						macro.swapMacroItems(idx[i], idx[i] + 1);
					}
				}
				macroItemTableViewer.refresh();
			}
		};
	}

	private SelectionListener createRemoveButtonSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) macroItemTableViewer.getSelection();
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					macro.removeMacroItem((IHttpMacroItem) i.next());
				}
				macroItemTableViewer.refresh();
			}
		};
	}

	private Composite createItemEditor(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new FillLayout());
		
		macroItemTabFolder = new TabFolder(rootControl, SWT.TOP);

		TabItem macroParamsTabItem = new TabItem(macroItemTabFolder, SWT.NONE);
		macroParamsTabItem.setText("Params");
		macroItemEditor = new MacroItemEditor(macroItemTabFolder, requestBuilder);
		macroParamsTabItem.setControl(macroItemEditor);
		macroParamsTabItem.setData(macroItemEditor);

		TabItem requestTabItem = new TabItem(macroItemTabFolder, SWT.NONE);
		requestTabItem.setText("Request");
		RequestEditor requestEditor = new RequestEditor(macroItemTabFolder, requestBuilder);
		requestTabItem.setControl(requestEditor);
		requestTabItem.setData(requestEditor);

		TabItem requestHeaderTabItem = new TabItem(macroItemTabFolder, SWT.NONE);
		requestHeaderTabItem.setText("Headers");
		HeaderEditor requestHeaderEditor = new HeaderEditor(macroItemTabFolder, requestBuilder, 0);
		requestHeaderTabItem.setControl(requestHeaderEditor);
		requestHeaderTabItem.setData(requestHeaderEditor);
		
		macroItemTabFolder.addSelectionListener(createRequestTabFolderSelectionListener());
		macroItemTabFolderItem = macroItemTabFolder.getSelection()[0];
		requestBuilderPartCurr = (IHttpBuilderPart) macroItemTabFolderItem.getData();
		requestIsEditable = false;
		requestBuilderPartCurr.setEditable(requestIsEditable);

		return rootControl;
	}

	private SelectionListener createRequestTabFolderSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TabItem[] selection = macroItemTabFolder.getSelection();
				if (selection != null) {
					try {
						requestBuilderPartCurr.processContents();
					} catch (Exception ex) {
						macroItemTabFolder.setSelection(macroItemTabFolderItem);
						ErrorDialog.displayError(getShell(), "An unexpected error occurred processing the request");
						ex.printStackTrace();
						return;
					}
				}

				macroItemTabFolderItem = selection[0];
				requestBuilderPartCurr = (IHttpBuilderPart) macroItemTabFolderItem.getData();
				requestBuilderPartCurr.setEditable(requestIsEditable);
				requestBuilderPartCurr.refresh();
			}
		};
	}

	private void setMacroItemSelected(IHttpMacroItem macroItem) {
		if (macroItem != null) {
			try {
				macroItem.setRequestBuilder(requestBuilder, null);
			} catch (Exception e) {
				ErrorDialog.displayError(getShell(), "An unexpected error occurred while processing the request");
				e.printStackTrace();
				return;
			}
			requestIsEditable = true;
		} else {
			requestBuilder.clear();
			requestIsEditable = false;
		}
		macroItemEditor.setMacroItem(macroItem);
		requestBuilderPartCurr.setEditable(requestIsEditable);
		requestBuilderPartCurr.refresh();
	}

}
