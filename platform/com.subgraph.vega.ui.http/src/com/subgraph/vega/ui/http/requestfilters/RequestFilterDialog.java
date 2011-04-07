package com.subgraph.vega.ui.http.requestfilters;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.ui.http.Activator;

public class RequestFilterDialog extends TitleAreaDialog {
	
	private TreeViewer treeViewer;
	private IHttpConditionManager conditionManager;
	private IHttpConditionSet conditionSet;
	private boolean conditionSetDirty;

	public RequestFilterDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override 
	protected Point getInitialSize() {
		return new Point(600, 400);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Configure Request View Filters");
		setMessage("Create and remove filters which will control the displayed HTTP request and response records.");
	}
	
	@Override
	protected void okPressed() {
		if(conditionSetDirty)
			conditionManager.saveConditionSet("filter", conditionSet);
		super.okPressed();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);
		createFields(dialogAreaComposite);
		return dialogAreaComposite;
	}
	
	private void createFields(Composite parent) {

		final Composite fieldsComposite = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout();
		layout.marginHeight = 10;
		layout.verticalSpacing = 20;
		fieldsComposite.setLayout(layout);
		fieldsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label label = new Label(fieldsComposite, SWT.WRAP);
		label.setText("Filter out information displayed in the Requests table within the Proxy.");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Composite treePanel = new Composite(fieldsComposite, SWT.NONE);
		treePanel.setLayout(new GridLayout(2, false));
		treePanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createTreeViewer(treePanel);
		createTreeViewerButtons(treePanel);
		
		final IWorkspace workspace = Activator.getDefault().getModel().getCurrentWorkspace();
		if(workspace != null) {
			conditionManager = workspace.getHttpConditionMananger();
			conditionSet = conditionManager.getConditionSetCopy("filter");

		} else {
			conditionManager = null;
			conditionSet = null;
		}
		treeViewer.setInput(conditionSet);
	
	}
	
	private void createTreeViewer(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewer.setContentProvider(new ConditionTreeContentProvider());
		treeViewer.setLabelProvider(new ConditionTreeLabelProvider());
		treeViewer.setAutoExpandLevel(2);
		treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
	}

	private void createTreeViewerButtons(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("create");
		button.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		button.addSelectionListener(createSelectionListenerButtonCreate());
		button = new Button(parent, SWT.PUSH);
		button.setText("remove");
		button.setLayoutData(new GridData(SWT.TOP, SWT.CENTER, false, false));
		button.addSelectionListener(createSelectionListenerButtonRemove());
	}

	private SelectionListener createSelectionListenerButtonCreate() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				openConditionCreateDialog();
			}
		};
	}

	private SelectionListener createSelectionListenerButtonRemove() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
				for(Object element: selection.toList()) {
					if(element instanceof IHttpCondition) {
						conditionSet.removeCondition((IHttpCondition) element);
						conditionSetDirty = true;
					} else if (element instanceof IHttpConditionType) {
						IHttpConditionType type = (IHttpConditionType) element;
						for(IHttpCondition c: conditionSet.getAllConditions()) {
							if(c.getType() == type) {
								conditionSet.removeCondition(c);
								conditionSetDirty = true;
							}
						}
					}
						
				}
				treeViewer.refresh();
			}
		};
	}

	private void openConditionCreateDialog() {
		if(conditionSet == null || conditionManager == null)
			return;
		ConditionCreateDialog dialog = new ConditionCreateDialog(this.getShell(), conditionManager);
		dialog.setBlockOnOpen(true);
		if (dialog.open() == ConditionCreateDialog.OK) {
			final IHttpCondition condition = dialog.getNewCondition();
			if(condition != null) {
				conditionSet.appendCondition(condition);
				conditionSetDirty = true;
				treeViewer.refresh();
				treeViewer.expandToLevel(condition, TreeViewer.ALL_LEVELS);
			}
		}
	}
}
