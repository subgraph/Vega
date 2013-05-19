package com.subgraph.vega.ui.scanner.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class NewScanParameterPage extends WizardPage {

	private final List<String> parameterNames;
	private Button enableExcludedParameters;
	private ListViewer excludedParameterList;
	private Text parameterNameText;
	private Button addParameterButton;
	private Button removeParameterButton;
	
	protected NewScanParameterPage(Collection<String> parameterNames) {
		super("Parameters");
		setTitle("Parameters");
		setDescription("Add names of parameters to avoid fuzzing during scan");
		this.parameterNames = new ArrayList<String>(parameterNames);
	}
	
	public Set<String> getExcludedParameterNames() {
		if(enableExcludedParameters.getSelection()) {
			return Collections.unmodifiableSet(new HashSet<String> (parameterNames));
		} else {
			return Collections.emptySet();
		}
	}

	@Override
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout());
		final Group group = createGroupControl(container);
		createEnableButton(group);
		createParameterExcludeList(group);
		createAddRemoveWidget(group);
		updatePage();
		setControl(container);
	}

	private Group createGroupControl(Composite parent) {
		final Group group = new Group(parent, SWT.NONE);
		group.setText("Exclude Parameters");
		final GridLayout layout = new GridLayout();
		layout.verticalSpacing = 15;
		group.setLayout(layout);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return group;
	}
	
	private void createEnableButton(Composite parent) {
		enableExcludedParameters = new Button(parent, SWT.CHECK);
		enableExcludedParameters.setText("Exclude listed parameters from scan");
		enableExcludedParameters.setSelection(true);
		enableExcludedParameters.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		enableExcludedParameters.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if(excludedParameterList != null) {
					updatePage();
				}
			}
		});
	}
	
	private void createParameterExcludeList(Composite parent) {
		excludedParameterList = new ListViewer(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		excludedParameterList.setContentProvider(ArrayContentProvider.getInstance());
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		
		excludedParameterList.getList().setLayoutData(gd);
		excludedParameterList.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updatePage();
			}
		});
		excludedParameterList.setInput(parameterNames.toArray());
	}
	
	
	private void createAddRemoveWidget(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new RowLayout());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		parameterNameText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		parameterNameText.setMessage("Enter name of parameter to exclude");
		
		addParameterButton = new Button(composite, SWT.PUSH);
		addParameterButton.setText("Add");
		removeParameterButton = new Button(composite, SWT.PUSH);
		removeParameterButton.setText("Remove");
		
		parameterNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updatePage();
			}
		});
		
		addParameterButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				handleAddParameter();
			}
		});
		
		removeParameterButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				handleRemoveParameter();
			}
		});
	}
	
	private void handleAddParameter() {
		final String name = parameterNameText.getText();
		if(!parameterNames.contains(name)) {
			parameterNames.add(name.toLowerCase());
			excludedParameterList.setInput(parameterNames.toArray());
		}
		parameterNameText.setText("");
	}
	
	private void handleRemoveParameter() {
		final IStructuredSelection selection = (IStructuredSelection) excludedParameterList.getSelection();
		for(Object elem: selection.toArray()) {
			if(elem instanceof String) {
				parameterNames.remove(elem);
			}
		}
		excludedParameterList.setInput(parameterNames.toArray());
		updatePage();
	}
	
	private void updatePage() {
		if(enableExcludedParameters.getSelection()) {
			excludedParameterList.getList().setEnabled(true);
			setAddRemoveWidgetEnabled();
		} else {
			excludedParameterList.getList().setEnabled(false);
			setAddRemoveWidgetDisabled();
		}
	}

	private void setAddRemoveWidgetDisabled() {
		parameterNameText.setEnabled(false);
		addParameterButton.setEnabled(false);
		removeParameterButton.setEnabled(false);
	}
	
	private void setAddRemoveWidgetEnabled() {
		parameterNameText.setEnabled(true);
		setAddRemoveButtonsEnabledForState();
	}
	private void setAddRemoveButtonsEnabledForState() {
		addParameterButton.setEnabled(!parameterNameText.getText().isEmpty());
		removeParameterButton.setEnabled(!excludedParameterList.getSelection().isEmpty());
	}
}
