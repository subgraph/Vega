package com.subgraph.vega.ui.http.requestfilterpreferencepage;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.api.http.conditions.ConditionType;
import com.subgraph.vega.api.http.conditions.MatchType;

public class ConditionCreateDialog extends TitleAreaDialog {
	private Composite parentComposite;
	private ComboViewer comboViewerConditionTypes;
	ConditionType conditionTypeSelected;
	private ComboViewer comboViewerMatchTypes;
	Enum<?> comparisonTypeSelected;
	private Text textPattern;
	String textPatternSelected;

	public ConditionCreateDialog(Shell parent) {
		super(parent);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Create a filter");
		setMessage("Create a new filter to filter out information displayed in the Requests table within the Proxy.");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parentComposite = (Composite) super.createDialogArea(parent);
		createFields(parentComposite);
		return parentComposite;
	}
	
	@Override
	protected void okPressed() {
		conditionTypeSelected = (ConditionType) ((IStructuredSelection) comboViewerConditionTypes.getSelection()).getFirstElement();
		comparisonTypeSelected = (Enum<?>) ((IStructuredSelection) comboViewerMatchTypes.getSelection()).getFirstElement();
		textPatternSelected = textPattern.getText();
		super.okPressed();
	}
	
	private Composite createFields(Composite parent) {
		Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(2, true));

		Label label = new Label(rootControl, SWT.NONE);
		label.setText("Condition type:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		comboViewerConditionTypes = new ComboViewer(rootControl, SWT.READ_ONLY);
		comboViewerConditionTypes.setContentProvider(new ArrayContentProvider());
		comboViewerConditionTypes.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((ConditionType) element).getName();
			}
		});
		final ConditionType[] typesList = ConditionType.values();
		comboViewerConditionTypes.setInput(ConditionType.values());
		comboViewerConditionTypes.setSelection(new StructuredSelection(typesList[0]));

		label = new Label(rootControl, SWT.NONE);
		label.setText("Match type:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		comboViewerMatchTypes = new ComboViewer(rootControl, SWT.READ_ONLY);
		comboViewerMatchTypes.setContentProvider(new ArrayContentProvider());
		comboViewerMatchTypes.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((MatchType) element).getName();
			}
		});
		MatchType[] matchTypesList = MatchType.values();
		comboViewerMatchTypes.setInput(matchTypesList);
		comboViewerMatchTypes.setSelection(new StructuredSelection(matchTypesList[0]));

		label = new Label(rootControl, SWT.NONE);
		label.setText("Pattern:");
		textPattern = new Text(rootControl, SWT.BORDER);
		textPattern.setMessage("regular expression");
		textPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		return rootControl;
	}
	
	public ConditionType getSelectionConditionType() {
		return conditionTypeSelected;
	}
	
	public Enum<?> getSelectionComparisonType() {
		return comparisonTypeSelected;
	}

	public String getTextPattern() {
		return textPatternSelected;
	}

}
