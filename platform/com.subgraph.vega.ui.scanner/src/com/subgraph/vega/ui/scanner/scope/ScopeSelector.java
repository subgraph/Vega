package com.subgraph.vega.ui.scanner.scope;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.api.model.scope.ITargetScopeManager;

public class ScopeSelector extends Composite {

	private final ITargetScopeManager targetScopeManager;
	private final StackLayout stack;
	private final Composite stackPanel;
	private final Text scopeText;
	private final ComboViewer scopeComboViewer;
	
	private final Button addButton;
	private final Button editButton;
	private final Button removeButton;
	private boolean isEditable;
	
	public ScopeSelector(Composite parent, ITargetScopeManager scopeManager) {
		super(parent, SWT.NONE);
		this.targetScopeManager = scopeManager;
		stack = new StackLayout();
		stackPanel = new Composite(this, SWT.NONE);
		stackPanel.setLayout(stack);
		
		this.scopeText = createText(stackPanel);
		this.scopeComboViewer = createComboViewer(stackPanel);
		
		final Composite buttonPanel = new Composite(this, SWT.NONE);
		buttonPanel.setLayout(new RowLayout(SWT.HORIZONTAL));
		this.addButton = createAddButton(buttonPanel);
		this.editButton = createEditButton(buttonPanel);
		this.removeButton = createRemoveButton(buttonPanel);
		refreshScopeCombo();
		setReadOnly();
		setEnabledButtonsForState();
		setLayout(new FillLayout());
	}
	
	public ComboViewer getViewer() {
		return scopeComboViewer;
	}

	private Text createText(Composite parent) {
		final Text text = new Text(parent, SWT.BORDER);
		text.addSelectionListener(createTextSelectionListener());
		text.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if(e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
				}
			}
			
		});
		return text;
	}
	
	private SelectionAdapter createTextSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				final String text = scopeText.getText();
				if(!text.isEmpty()) {
					getScopeFromCombo().setName(text);
					scopeComboViewer.refresh(true);
					setReadOnly();
				}
			}
		};
	}
	private ComboViewer createComboViewer(Composite parent) {
		final ComboViewer viewer = new ComboViewer(parent, SWT.READ_ONLY);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				final StructuredSelection ss = (StructuredSelection) event.getSelection();
				if(ss.getFirstElement() instanceof ITargetScope) {
					targetScopeManager.setActiveScope((ITargetScope) ss.getFirstElement());
					setEnabledButtonsForState();
				}
			}
		});
		return viewer;
	}
	
	private Button createAddButton(Composite parent) {
		return createButton(parent, "Add", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleAdd();
			}
		});
	}
	
	private Button createEditButton(Composite parent) {
		return createButton(parent, "Edit", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleEdit();
			}
		});
	}
	
	private Button createRemoveButton(Composite parent) {
		return createButton(parent, "Remove", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleRemove();
			}
		});
	}

	private void refreshScopeCombo() {
		scopeComboViewer.setInput(targetScopeManager.getAllScopes().toArray());
		scopeComboViewer.setSelection(new StructuredSelection(targetScopeManager.getActiveScope()));
		setEnabledButtonsForState();
	}

	private Button createButton(Composite parent, String text, SelectionListener listener) {
		final Button b = new Button(parent, SWT.NONE);
		b.setText(text);
		b.addSelectionListener(listener);
		return b;
	}

	private void handleEdit() {
		setEditable();
	}
	
	private void handleRemove() {
		if(targetScopeManager.getAllScopes().size() <= 1) {
			return;
		}
		targetScopeManager.removeScope(getScopeFromCombo());
		refreshScopeCombo();
	}
	
	private void handleAdd() {
		final ITargetScope newScope = targetScopeManager.createNewScope();
		targetScopeManager.saveScope(newScope);
		targetScopeManager.setActiveScope(newScope);
		refreshScopeCombo();
		setEditable();
	}
	
	private void setEditable() {
		final String name = getScopeFromCombo().getName();
		if(name != null) {
			isEditable = true;
			stack.topControl = scopeText;
			stackPanel.layout();
			setEnabledButtonsForState();

			scopeText.setText(name);
			scopeText.setSelection(0, name.length());
			scopeText.setFocus();
		}
	}

	private void setReadOnly() {
		isEditable = false;
		stack.topControl = scopeComboViewer.getCombo();
		stackPanel.layout();
		setEnabledButtonsForState();
	}

	private ITargetScope getScopeFromCombo() {
		final ISelection selection = scopeComboViewer.getSelection();
		if(!(selection instanceof StructuredSelection)) {
			return null;
		}
		final Object ob = ((StructuredSelection) selection).getFirstElement();
		if(!(ob instanceof ITargetScope)) {
			return null;
		}
		return (ITargetScope) ob;
	}
	
	private void setEnabledButtonsForState() {
		if(isEditable) {
			addButton.setEnabled(false);
			editButton.setEnabled(false);
			removeButton.setEnabled(false);
			return;
		} 
		if(getScopeFromCombo().isDefaultScope()) {
			addButton.setEnabled(true);
			editButton.setEnabled(false);
			removeButton.setEnabled(false);
			return;
		}
		
		addButton.setEnabled(true);
		editButton.setEnabled(true);
		removeButton.setEnabled(true);
	}
}
