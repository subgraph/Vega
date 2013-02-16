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
package com.subgraph.vega.ui.http.requestlogviewer;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
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
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionIntegerMatchAction;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.internal.ui.http.requestlogviewer.HttpViewLabelProvider;
import com.subgraph.vega.internal.ui.http.requestlogviewer.RequestViewContentProvider;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.http.requesteditviewer.RequestEditView;
import com.subgraph.vega.ui.tags.taggableeditor.TaggableEditorDialog;
import com.subgraph.vega.ui.tagsl.taggablepopup.TaggablePopupDialog;
import com.subgraph.vega.ui.util.dialogs.ErrorDialog;

public class RequestLogViewer extends Composite {
	public final static String POPUP_REQUESTS_TABLE = "com.subgraph.vega.ui.http.requestlogviewer.popup";
	private static final RGB ACTIVE_FILTER_COLOR = new RGB(0xF0, 0xFF, 0xFF);
	private static final int MAX_OPEN_EDITORS = 3; // Maximum number of editors to allow to open at once before prompting the user
	private final String instanceId;
	private final int heightInRows;
	private TableViewer tableViewer;
	private Menu tableMenu;
	private RequestResponseViewer requestResponseViewer;
	private TaggablePopupDialog taggablePopupDialog;
	private RequestViewContentProvider contentProvider;

	/**
	 * @param parent
	 * @param instanceId A unique ID to differentiate between condition filter sets.
	 * @param heightInRows Desired height of table in text rows, or 0 to fill available space. 
	 */
	public RequestLogViewer(Composite parent, String instanceId, int heightInRows) {
		super(parent, SWT.NONE);
		this.instanceId = instanceId;
		this.heightInRows = heightInRows;

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginTop = 0;
		layout.marginRight = 0;
		layout.marginBottom = 0;
		setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		createTable(gd).setLayoutData(gd);

		tableViewer.setInput(Activator.getDefault().getModel());
	}

	public void setRequestResponseViewer(RequestResponseViewer requestResponseViewer) {
		this.requestResponseViewer = requestResponseViewer;
		tableMenu.setEnabled(this.requestResponseViewer != null && !tableViewer.getSelection().isEmpty());
	}

	public void focusOnRecord(long requestId) {
		final Object inputObj = tableViewer.getInput();
		if(!(inputObj instanceof IModel)) {
			return;
		}
		final IModel model = (IModel) inputObj;
		final IWorkspace workspace = model.getCurrentWorkspace();
		if(workspace == null) {
			return;
		}
		
		addRequestIdConditionRule(requestId);
		
		final IRequestLog requestLog = workspace.getRequestLog();
		final IRequestLogRecord record = requestLog.lookupRecord(requestId);
		if(record == null) {
			return;
		}
		
		final FocusOnRecordTask task = new FocusOnRecordTask(record, contentProvider, tableViewer);
		new Thread(task).start();
		
		if (requestResponseViewer != null) {
			requestResponseViewer.setDisplayResponse();
		}
	}	
	
	private IHttpCondition requestIdCondition;
	
	private void addRequestIdConditionRule(long requestId) {
		if(contentProvider == null || contentProvider.getConditionSet() == null) {
			return;
		}
		final IHttpConditionSet conditionSet = contentProvider.getConditionSet();
		final IHttpConditionType type = conditionSet.getConditionManager().getConditionTypeByName("request id");
		final IHttpConditionMatchAction matchAction = type.getMatchActionByName("equals");

		((IHttpConditionIntegerMatchAction) matchAction).setInteger((int) requestId);
		
		if(requestIdCondition != null) {
			conditionSet.removeTemporaryCondition(requestIdCondition, false);
		}
		requestIdCondition = type.createConditionInstance(matchAction);
		requestIdCondition.setSufficient(true);
		conditionSet.appendTemporaryCondition(requestIdCondition, true);
	}

	
	
	private Composite createTable(GridData gd) {
		final Composite rootControl = new Composite(this, SWT.NONE);
		final TableColumnLayout tcl = new TableColumnLayout();
		rootControl.setLayout(tcl);

		tableViewer = new TableViewer(rootControl, SWT.MULTI| SWT.VIRTUAL | SWT.FULL_SELECTION);
		createColumns(tableViewer, tcl);
		final Color activeFilterColor = new Color(tableViewer.getControl().getDisplay(), ACTIVE_FILTER_COLOR);
		contentProvider = new RequestViewContentProvider(instanceId, activeFilterColor);
		tableViewer.setContentProvider(contentProvider);
		tableViewer.setLabelProvider(new HttpViewLabelProvider());
		tableViewer.addSelectionChangedListener(createSelectionChangedListener());
		final Table table = tableViewer.getTable();
		table.setMenu(createTableMenu(table));
		table.addMouseTrackListener(createTableMouseTrackListener());
		table.addMouseMoveListener(createMouseMoveListener());
		if (heightInRows != 0) {
			gd.heightHint = table.getItemHeight() * heightInRows;
		}
		
		return rootControl;
	}
	
	private void createColumns(TableViewer viewer, TableColumnLayout layout) {
		final String[] titles = {"ID", "Host", "Method", "Request", "Status", "Length", "Time (ms)", "Tags" };
		final ColumnLayoutData[] layoutData = {
				new ColumnPixelData(60, true, true),
				new ColumnPixelData(120, true, true),
				new ColumnPixelData(60, true, true),
				new ColumnWeightData(100, 100, true),
				new ColumnPixelData(50, true, true),
				new ColumnPixelData(80, true, true),
				new ColumnPixelData(50, true, true),
				new ColumnPixelData(15, true, true)
		};

		for(int i = 0; i < titles.length; i++) {
			final TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			final TableColumn c = column.getColumn();
			layout.setColumnData(c, layoutData[i]);
			c.setText(titles[i]);
			c.setMoveable(true);
		}
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	private ISelectionChangedListener createSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				tableMenu.setEnabled(requestResponseViewer != null && !event.getSelection().isEmpty());
				if (requestResponseViewer != null) { // REVISIT gross
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					if (selection.getFirstElement() instanceof IRequestLogRecord) {
						requestResponseViewer.setCurrentRecord((IRequestLogRecord) selection.getFirstElement());
					} else {
						requestResponseViewer.setCurrentRecord(null);
					}
				}
			}
		};
	}

	private Menu createTableMenu(Table table) {
		tableMenu = new Menu(table);
		tableMenu.setEnabled(false);
		
	    MenuItem replayMenuItem = new MenuItem(tableMenu, SWT.CASCADE);
	    replayMenuItem.setText("Replay Request");
	    replayMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				replayRequestSelection();
			};
	    });

	    MenuItem tagEditMenuItem = new MenuItem(tableMenu, SWT.CASCADE);
	    tagEditMenuItem.setText("Edit Tag");
	    tagEditMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tagEditSelection();
			};
	    });

		return tableMenu;
	}

	private void replayRequestSelection() {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		if (selection.size() > MAX_OPEN_EDITORS) {
			MessageBox messageDialog = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
			messageDialog.setText("Warning");
			messageDialog.setMessage(selection.size() + " replay editors will be opened. Proceed?");
			if (messageDialog.open() == SWT.CANCEL) {
				return;
			}
		}

		int viewMode = IWorkbenchPage.VIEW_ACTIVATE;
		for (Iterator<?> iter = selection.iterator(); iter.hasNext(); ) {
			IRequestLogRecord record = (IRequestLogRecord) iter.next();
			String secondaryId = UUID.randomUUID().toString();
			RequestEditView view;
			try {
				view = (RequestEditView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(RequestEditView.VIEW_ID, secondaryId, viewMode);
			} catch (PartInitException e) {
				ErrorDialog.displayExceptionError(getShell(), e);
				return;
			}

			try {
				view.setRequest(record);
			} catch (URISyntaxException e) {
				ErrorDialog.displayExceptionError(getShell(), e);
				return;
			}
			viewMode = IWorkbenchPage.VIEW_VISIBLE;
		}
	}

	private void tagEditSelection() {
		final IRequestLogRecord record = (IRequestLogRecord)((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
		final Dialog dialog = TaggableEditorDialog.createDialog(getShell(), record);
		if (dialog.open() == Window.OK) {
			tableViewer.refresh();
		}
	}	
	
	@SuppressWarnings("unchecked")
	public List<IRequestLogRecord> getSelectionList() {
		return ((IStructuredSelection) tableViewer.getSelection()).toList();
	}
	
	private MouseMoveListener createMouseMoveListener() {
		return new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				if (taggablePopupDialog != null) {
					taggablePopupDialog.close();
					taggablePopupDialog = null;
				}
			}
		};
	}
	
	private MouseTrackListener createTableMouseTrackListener() {
		return new MouseTrackListener() {
			@Override
			public void mouseEnter(MouseEvent e) {
			}

			@Override
			public void mouseExit(MouseEvent e) {
				if (taggablePopupDialog != null) {
					taggablePopupDialog.close();
					taggablePopupDialog = null;
				}
			}

			@Override
			public void mouseHover(MouseEvent e) {
				if (taggablePopupDialog == null) {
					Point pt = new Point(e.x, e.y);
					Table table = tableViewer.getTable();
					TableItem tableItem = table.getItem(pt);
					if (tableItem != null) {
						IRequestLogRecord record = (IRequestLogRecord) tableItem.getData();
						if (record.getTagCount() > 0) {
							Point origin = tableViewer.getTable().getDisplay().map(table.getParent(), null, e.x, e.y);
							taggablePopupDialog = new TaggablePopupDialog(table.getShell(), record, origin);
							taggablePopupDialog.open();
						}
					}
				}
			}
		};
	}

}
