package com.subgraph.vega.ui.http.conditions;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.api.model.conditions.IHttpRangeCondition;
import com.subgraph.vega.api.model.conditions.IHttpRegexCondition;
import com.subgraph.vega.api.model.conditions.IHttpCondition.MatchOption;;

public class ConditionInput {
	private final IHttpConditionManager conditionManager;
	
	private ComboViewer conditionTypeViewer;
	private ComboViewer conditionMatchViewer;
	private StackLayout inputStackLayout;
	private Composite inputRootPanel;
	private Composite patternInputPanel;
	private Composite rangeInputPanel;
	private Text patternText;
	private Text rangeLowText;
	private Text rangeHighText;
	
	public ConditionInput(IHttpConditionManager conditionManager) {
		this.conditionManager = conditionManager;
	}
	
	public ComboViewer createConditionTypeCombo(Composite parent) {
		if(conditionTypeViewer != null) 
			throw new IllegalStateException("Condition type combo already created.");
		conditionTypeViewer = new ComboViewer(parent, SWT.READ_ONLY);
		conditionTypeViewer.setContentProvider(new ArrayContentProvider());
		conditionTypeViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof IHttpConditionType) 
					return ((IHttpConditionType) element).getName();
				else
					return null;
			}
		});
		
		final List<IHttpConditionType> types = conditionManager.getConditionTypes();
		conditionTypeViewer.setInput(types);
		if(types.size() != 0)
			conditionTypeViewer.setSelection(new StructuredSelection(types.get(0)));
		conditionTypeViewer.addSelectionChangedListener(createConditionTypeSelectionListener());
		return conditionTypeViewer;
	}
	
	public void reset() {
		patternText.setText("");
		rangeLowText.setText("");
		rangeHighText.setText("");
		conditionTypeViewer.setSelection(new StructuredSelection(conditionTypeViewer.getElementAt(0)));
		conditionMatchViewer.setSelection(new StructuredSelection(conditionMatchViewer.getElementAt(0)));
	}

	public IHttpCondition createConditionFromData() {
		final IHttpConditionType type = getSelectedConditionType();
		if(type == null)
			return null;
		final IHttpCondition condition = createConditionFromType(type);
		if(condition == null)
			return null;
		condition.setInverted(getSelectedMatchOption().getInverted());
		return condition;
	}
	
	private IHttpCondition createConditionFromType(IHttpConditionType type) {
		switch(type.getStyle()) {
		case CONDITION_REGEX:
			return createRegexCondition(type);
		case CONDITION_RANGE:
			return createRangeCondition(type);
		}
		return null;
	}
	
	private IHttpCondition createRegexCondition(IHttpConditionType type) {
		final String pattern = patternText.getText();
		if(pattern.isEmpty())
			return null;
		final IHttpRegexCondition condition = (IHttpRegexCondition) type.createConditionInstance();
		condition.setPattern(pattern);
		return condition;
	}
	
	private IHttpCondition createRangeCondition(IHttpConditionType type) {
		final String startRange = rangeLowText.getText();
		final String endRange = rangeHighText.getText();
		if(startRange.isEmpty() || endRange.isEmpty())
			return null;
		try {
			final int start = Integer.parseInt(startRange);
			final int end = Integer.parseInt(endRange);
			if(start < 0 || end < 0 || start > end)
				return null;
			final IHttpRangeCondition condition = (IHttpRangeCondition) type.createConditionInstance();
			condition.setRangeLow(start);
			condition.setRangeHigh(end);
			return condition;
		} catch (NumberFormatException e) {
			return null;
		}
		
	}
	private IHttpConditionType getSelectedConditionType() {
		final IStructuredSelection selection = (IStructuredSelection) conditionTypeViewer.getSelection();
		if(selection.isEmpty())
			return null;
		return (IHttpConditionType) selection.getFirstElement();
	}
	
	private MatchOption getSelectedMatchOption() {
		final IStructuredSelection selection = (IStructuredSelection) conditionMatchViewer.getSelection();
		if(selection.isEmpty())
			return null;
		return (MatchOption) selection.getFirstElement();
	}
	
	private ISelectionChangedListener createConditionTypeSelectionListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				displayPanelForSelection(event.getSelection());
			}
		};
	}
	
	private void displayPanelForSelection(ISelection selection) {
		if(selection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) selection;
			if(!(ss.getFirstElement() instanceof IHttpConditionType))
				return;
			final IHttpConditionType type = (IHttpConditionType) ss.getFirstElement();
			switch(type.getStyle()) {
			case CONDITION_REGEX:
				displayPatternInput();
				break;
			case CONDITION_RANGE:
				displayRangeInput();
				break;
			}
		}
	}

	private void displayPatternInput() {
		if(inputStackLayout != null) {
			inputStackLayout.topControl = patternInputPanel;
			inputRootPanel.layout();
		}
	}
	
	private void displayRangeInput() {
		if(inputStackLayout != null) {
			inputStackLayout.topControl = rangeInputPanel;
			inputRootPanel.layout();
		}
	}

	public ComboViewer createConditionMatchCombo(Composite parent) {
		if(conditionMatchViewer != null)
			throw new IllegalStateException("Condition match combo already created");
		conditionMatchViewer = new ComboViewer(parent, SWT.READ_ONLY);
		conditionMatchViewer.setContentProvider(new ArrayContentProvider());
		conditionMatchViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MatchOption) element).getName();
			}
		});
		final MatchOption[] options = MatchOption.values();
		conditionMatchViewer.setInput(options);
		conditionMatchViewer.setSelection(new StructuredSelection(options[0]));
		return conditionMatchViewer;
	}
	
	public Composite createInputPanel(Composite parent) {
		inputRootPanel = new Composite(parent, SWT.NONE);
		inputStackLayout = new StackLayout();
		inputRootPanel.setLayout(inputStackLayout);
		createPatternInputPanel(inputRootPanel);
		createRangeInputPanel(inputRootPanel);
		if(conditionTypeViewer != null)
			displayPanelForSelection(conditionTypeViewer.getSelection());
		return inputRootPanel;
	}
	
	private void createPatternInputPanel(Composite stackPanel) {
		patternInputPanel = new Composite(stackPanel, SWT.NONE);
		patternInputPanel.setLayout(new GridLayout(1, false));
		patternText = new Text(patternInputPanel, SWT.BORDER | SWT.SINGLE);
		patternText.setMessage("regular expression");
		patternText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	}
	
	private void createRangeInputPanel(Composite stackPanel) {
		rangeInputPanel = new Composite(stackPanel, SWT.NONE);
		rangeInputPanel.setLayout(new GridLayout(3, false));
		rangeLowText = new Text(rangeInputPanel, SWT.BORDER | SWT.SINGLE);
		rangeLowText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		rangeLowText.setMessage("from");
		final Label sep = new Label(rangeInputPanel, SWT.NONE);
		sep.setText(" - ");
		sep.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		rangeHighText = new Text(rangeInputPanel, SWT.BORDER | SWT.SINGLE);
		rangeHighText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		rangeHighText.setMessage("to");
	}
}
