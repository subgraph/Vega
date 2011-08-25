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
package com.subgraph.vega.ui.http.intercept.config;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.http.proxy.HttpInterceptorLevel;
import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IProxyTransaction.TransactionDirection;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.conditions.ConditionSetChanged;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.http.conditions.ConditionInput;
import com.subgraph.vega.ui.util.dialogs.IConfigDialogContent;

public class ConfigureInterceptionContent implements IConfigDialogContent {
	private static final Image IMAGE_CHECKED = Activator.getImageDescriptor("icons/checked.png").createImage();
	private static final Image IMAGE_UNCHECKED = Activator.getImageDescriptor("icons/unchecked.png").createImage();
	private final IModel model;
	private final TransactionDirection direction;
	private final IEventHandler conditionSetEventHandler;
	private ComboViewer comboViewerInterceptorLevel;
	private TableViewer tableViewerBreakpoints;
	private ConditionInput conditionInput;
	private IHttpInterceptor interceptor;
	private IHttpConditionSet conditionSet;
	private Composite composite;
	
	public ConfigureInterceptionContent(IModel model, TransactionDirection direction) {
		this.model = model;
		final IWorkspace workspace = model.getCurrentWorkspace();
		final IHttpConditionManager conditionManager = (workspace == null) ? (null) : (workspace.getHttpConditionMananger());
		this.conditionInput = new ConditionInput(conditionManager);
		this.direction = direction;
		this.conditionSetEventHandler = createConditionSetEventHandler();
		this.conditionSet = model.addConditionSetTracker(getConditionSetName(), conditionSetEventHandler);
	}
	private IEventHandler createConditionSetEventHandler() {
		return new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof ConditionSetChanged) 
					onConditionSetChanged((ConditionSetChanged) event);
			}			
		};
	}
	@Override
	public Composite createContents(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		createInterceptorOptions(composite).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		createBreakpointsEditor(composite).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		interceptor = Activator.getDefault().getProxyService().getInterceptor();
		comboViewerInterceptorLevel.setSelection(new StructuredSelection(interceptor.getInterceptLevel(direction)));
		setConditionSetInput();
		return composite;
	}

	@Override
	public String getTitle() {
		return "Interceptor Options";
	}

	@Override
	public String getMessage() {
		return "Set up breakpoint for interceptor";
	}

	@Override
	public Control getFocusControl() {
		return composite;
	}

	@Override
	public void onClose() {
		model.removeConditionSetTracker(getConditionSetName(), conditionSetEventHandler);
	}

	@Override
	public void onOk() {	
	}
	
	private String getConditionSetName() {
		switch(direction) {
		case DIRECTION_REQUEST:
			return IHttpConditionManager.CONDITION_SET_BREAKPOINTS_REQUEST;
		case DIRECTION_RESPONSE:
			return IHttpConditionManager.CONDITION_SET_BREAKPOINTS_RESPONSE;
		}
		return null;
	}

	private void setConditionSetInput() {
		if(tableViewerBreakpoints == null || tableViewerBreakpoints.getContentProvider() == null)
			return;
		if(conditionSet == null)
			tableViewerBreakpoints.setInput(null);
		else
			tableViewerBreakpoints.setInput(conditionSet.getAllConditions());
	}
	
	private void onConditionSetChanged(ConditionSetChanged event) {
		conditionSet = event.getConditionSet();
		setConditionSetInput();
	}

	protected Control createDialogArea(Composite parent) {
		
		parent.setLayout(new GridLayout(1, true));
		createInterceptorOptions(parent).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		createBreakpointsEditor(parent).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		interceptor = Activator.getDefault().getProxyService().getInterceptor();
		comboViewerInterceptorLevel.setSelection(new StructuredSelection(interceptor.getInterceptLevel(direction)));
		setConditionSetInput();
		return parent;
	}

	void dispose() {
		model.removeConditionSetTracker(getConditionSetName(), conditionSetEventHandler);
	}

	public TransactionDirection getDirection() {
		return direction;
	}

	private Composite createInterceptorOptions(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setText("Interceptor Options");
		rootControl.setLayout(new GridLayout(2, false));

		final Label label = new Label(rootControl, SWT.NONE);
		label.setText("Intercept for:");

		comboViewerInterceptorLevel = new ComboViewer(rootControl, SWT.READ_ONLY);
		comboViewerInterceptorLevel.setContentProvider(new ArrayContentProvider());
		comboViewerInterceptorLevel.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((HttpInterceptorLevel) element).getName();
			}
		});
		comboViewerInterceptorLevel.setInput(HttpInterceptorLevel.values());
		comboViewerInterceptorLevel.addSelectionChangedListener(createSelectionChangedListenerComboViewerInterceptorLevel());
		
		return rootControl;
	}

	private ISelectionChangedListener createSelectionChangedListenerComboViewerInterceptorLevel() {
		return new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent e) {
				HttpInterceptorLevel level = (HttpInterceptorLevel) ((IStructuredSelection) comboViewerInterceptorLevel.getSelection()).getFirstElement();
				if (level != null) {
					interceptor.setInterceptLevel(direction, level);
				}
			}
		};
	}
	private Composite createBreakpointsEditor(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setText("Breakpoints");
		rootControl.setLayout(new GridLayout(2, false));

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		final Composite compTable = createTableBreakpoints(rootControl, gd, 7);
		compTable.setLayoutData(gd);
		final Composite compTableButtons = createTableBreakpointsButtons(rootControl);
		compTableButtons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		final Composite compCreate = createCreatorBreakpoints(rootControl);
		compCreate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		final Composite compCreateButtons = createCreatorBreakpointsButtons(rootControl);
		compCreateButtons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		return rootControl;
	}
	
	private Composite createTableBreakpoints(Composite parent, GridData gd, int heightInRows) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		final TableColumnLayout tcl = new TableColumnLayout();
		rootControl.setLayout(tcl);

		tableViewerBreakpoints = new TableViewer(rootControl, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createTableBreakpointsColumns(tableViewerBreakpoints, tcl);
		tableViewerBreakpoints.setContentProvider(new ArrayContentProvider());
		final Table table = tableViewerBreakpoints.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		gd.heightHint = table.getItemHeight() * heightInRows;

		return rootControl;
	}

	private void createTableBreakpointsColumns(TableViewer viewer, TableColumnLayout layout) {
		final String[] titles = { "", "Type", "Matches", "Pattern", };
		final ColumnLayoutData[] layoutData = {
			new ColumnPixelData(16, false, true),
			new ColumnPixelData(150, true, true),
			new ColumnPixelData(150, true, true),
			new ColumnWeightData(100, 100, true),
		};
		final EditingSupport editorList[] = {
				new BreakpointEnabledEditingSupport(viewer),
				new BreakpointTypeEditingSupport(viewer),
				new BreakpointMatchTypeEditingSupport(viewer),
				new BreakpointPatternEditingSupport(viewer),
		};
		final ColumnLabelProvider providerList[] = {
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return null;
				}

				@Override
				public Image getImage(Object element) {
					if(((IHttpCondition) element).isEnabled()) {
						return IMAGE_CHECKED;
					} else {
						return IMAGE_UNCHECKED;
					}
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpCondition) element).getType().getName();
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					IHttpCondition condition = (IHttpCondition) element;
					return condition.getMatchAction().getLabel();
				}
			},		
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					IHttpCondition condition = (IHttpCondition) element;
					return condition.getMatchAction().getArgumentAsString();
				}
			},
		};

		for (int i = 0; i < titles.length; i++) {
			final TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			final TableColumn c = column.getColumn();
			layout.setColumnData(c, layoutData[i]);			
			c.setText(titles[i]);
			c.setMoveable(true);
			column.setEditingSupport(editorList[i]);
			column.setLabelProvider(providerList[i]);
		}	
	}

	private Composite createTableBreakpointsButtons(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, true));

		Button button = new Button(rootControl, SWT.PUSH);
		button.setText("remove");
		button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		button.addSelectionListener(createSelectionListenerButtonRemove());

		return rootControl;
	}
	
	private SelectionListener createSelectionListenerButtonRemove() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewerBreakpoints.getSelection();
				for(Object ob: selection.toList()) {
					if((ob instanceof IHttpCondition) && (conditionSet != null)) 
						conditionSet.removeCondition((IHttpCondition) ob);			
				}
				tableViewerBreakpoints.refresh(true);
			}
		};
	}

	
	private Composite createCreatorBreakpoints(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(3, false));
		conditionInput.createConditionTypeCombo(rootControl);
		conditionInput.createConditionMatchCombo(rootControl);
		conditionInput.createInputPanel(rootControl);
		return rootControl;
	}

	private Composite createCreatorBreakpointsButtons(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, true));

		Button button = new Button(rootControl, SWT.PUSH);
		button.setText("create");
		button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		button.addSelectionListener(createSelectionListenerButtonCreateBreakpoint());

		return rootControl;
	}
	
	
	private SelectionListener createSelectionListenerButtonCreateBreakpoint() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final IHttpCondition breakpoint = conditionInput.createConditionFromData();
				if(breakpoint == null)
					return;
				if(conditionSet != null)
					conditionSet.appendCondition(breakpoint);
				conditionInput.reset();
				tableViewerBreakpoints.refresh(true);
			}
		};
	}

}
