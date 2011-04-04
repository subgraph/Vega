package com.subgraph.vega.ui.http.requestfilterpreferencepage;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.subgraph.vega.api.http.conditions.ConditionType;
import com.subgraph.vega.api.http.conditions.IHttpConditionSet;
import com.subgraph.vega.api.http.conditions.MatchType;
import com.subgraph.vega.http.conditions.HttpConditionSet;
import com.subgraph.vega.ui.http.Activator;

public class RequestFilterPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	private Composite parentComposite;
	private TreeViewer treeViewer;
	private IHttpConditionSet conditionSet;

	public RequestFilterPreferencePage() {
	}

	public RequestFilterPreferencePage(String title) {
		super(title);
	}

	public RequestFilterPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createContents(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, true));

		Label label = new Label(parentComposite, SWT.NONE);
		label.setText("Filter out information displayed in the Requests table within the Proxy.");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		createTreeField(parentComposite).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		conditionSet = new HttpConditionSet();
		conditionSet.unserialize(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_FILTER));
		treeViewer.setInput(conditionSet);

		return parentComposite;
	}

	@Override
	public boolean performCancel() {
		conditionSet.unserialize(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_FILTER));
		return true;
	}
	
	@Override
	public boolean performOk() {
		Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_FILTER, conditionSet.serialize());
		return true;
	}

	private Composite createTreeField(Composite parent) {
		Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(2, false));

		createTreeViewer(rootControl).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		createTreeViewerButtons(rootControl).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		return rootControl;
	}
	
	private Composite createTreeViewer(Composite parent) {
		Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, false));

		treeViewer = new TreeViewer(rootControl, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewer.setContentProvider(new ConditionTreeContentProvider());
		treeViewer.setLabelProvider(new ConditionTreeLabelProvider());
		treeViewer.setAutoExpandLevel(2);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

		return rootControl;
	}

	private Composite createTreeViewerButtons(Composite parent) {
		Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, true));

		Button button = new Button(rootControl, SWT.PUSH);
		button.setText("create");
		button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		button.addSelectionListener(createSelectionListenerButtonCreate());
		button = new Button(rootControl, SWT.PUSH);
		button.setText("remove");
		button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		return rootControl;
	}

	private SelectionListener createSelectionListenerButtonCreate() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				openConditionCreateDialog();
			}
		};
	}

	private void openConditionCreateDialog() {
		ConditionCreateDialog dialog = new ConditionCreateDialog(this.getShell());
		dialog.setBlockOnOpen(true);
		if (dialog.open() == ConditionCreateDialog.OK) {
			conditionSet.createCondition(dialog.getSelectionConditionType(), dialog.getSelectionComparisonType(), dialog.getTextPattern(), true);
			treeViewer.refresh();
		}
	}
}
