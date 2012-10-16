package com.subgraph.vega.ui.scanner.wizards;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.api.model.scope.ITargetScopeManager;
import com.subgraph.vega.api.util.UriTools;
import com.subgraph.vega.ui.scanner.scope.EditScopeDialog;

public class NewScanTargetPage extends WizardPage {
	private final static String TARGET_TITLE = "Select a Scan Target";
	private final static String TARGET_DESCRIPTION = 
			"Choose a target for new scan";
	
	private final IWorkspace workspace;
	private final String targetUriText;
	
	private Text scanTargetUriText;
	private ComboViewer scopeComboViewer;
	private ListViewer scopeURIViewer;
	private Button includeModelContent;
	private ITargetScope comboScope;
	private ITargetScope uriScope;
	
	public NewScanTargetPage(IWorkspace workspace, String targetValue) {
		super(TARGET_TITLE);
		setTitle(TARGET_TITLE);
		setDescription(TARGET_DESCRIPTION);
		this.workspace = workspace;
		this.targetUriText = targetValue;
	}
	
	public boolean isTargetValid() {
		return isValidScope(getScanTargetScope());
	}
	
	private boolean isValidScope(ITargetScope scope) {
		return scope != null && !scope.isEmpty();
	}
	
	public ITargetScope getScanTargetScope() {
		if(scanTargetUriText.isEnabled()) {
			return uriScope;
		} else {
			return comboScope;
		}
	}
	
	public String getUriTextIfValid() {
		final String text = scanTargetUriText.getText();
		if(UriTools.isTextValidURI(text)) {
			return text;
		} else {
			return null;
		}
	}

	@Override
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(1, false));
		createTargetGroup(container);
		createScopeURIGroup(container);
		if(targetUriText != null && !targetUriText.isEmpty()) {
			scanTargetUriText.setText(targetUriText);
			setUriScopeFromText(targetUriText);
		}

		refreshScopeCombo();
		updatePage();
		setControl(container);
	}
	
	private void createTargetGroup(Composite parent) {
		final Group group = createTargetGroupControl(parent);
		createBaseURIControls(group);
		createScopeControls(group);
	}
	
	private Group createTargetGroupControl(Composite parent) {
		final Group group = new Group(parent, SWT.NONE);
		group.setText("Scan Target");
		final GridLayout layout = new GridLayout();
		layout.verticalSpacing = 15;
		layout.marginBottom = 15;
		group.setLayout(layout);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		return group;
	}

	private void createBaseURIControls(Composite parent) {
		final Button radio = new Button(parent, SWT.RADIO);
		radio.setText("Enter a base URI for scan:");
		radio.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		
		scanTargetUriText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		final GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd.widthHint = 300;
		gd.horizontalIndent = 20;
		scanTargetUriText.setLayoutData(gd);
		scanTargetUriText.setMessage("Enter URI to scan");
		scanTargetUriText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setUriScopeFromText(scanTargetUriText.getText());
			}
		});
		
		radio.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				scanTargetUriText.setEnabled(radio.getSelection());
				updatePage();
			}
		});
		radio.setSelection(true);
	}
	
	private void setUriScopeFromText(String text) {
		if(uriScope == null) {
			uriScope = workspace.getTargetScopeManager().createNewScope();
		}
		uriScope.clear();
		if(UriTools.isTextValidURI(text)) {
			uriScope.addScopeURI(UriTools.getURIFromText(text));
		}
		updatePage();
	}

	
	private void createScopeControls(Composite parent) {
		final Button radio = new Button(parent, SWT.RADIO);
		radio.setText("Choose a target scope for scan");
		
		final Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new RowLayout());
		final GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalIndent = 20;
		c.setLayoutData(gd);
		
		scopeComboViewer = new ComboViewer(c, SWT.READ_ONLY);
		scopeComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		scopeComboViewer.getCombo().setEnabled(false);
		scopeComboViewer.getCombo().setLayoutData(new RowData(250, SWT.DEFAULT));
		scopeComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				final StructuredSelection ss = (StructuredSelection) event.getSelection();
				if(ss.getFirstElement() instanceof ITargetScope) {
					comboScope = (ITargetScope) ss.getFirstElement();
					updatePage();
				}
			}
			
		});
		
		final Button editButton = new Button(c, SWT.NONE);
		editButton.setText("Edit Scopes");
		editButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				handleEditScopes();
			}
		});
		editButton.setEnabled(false);
		
		radio.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				final boolean flag = radio.getSelection();
				scopeComboViewer.getCombo().setEnabled(flag);
				editButton.setEnabled(flag);
				updatePage();
			}
		});
	}
	
	private void handleEditScopes() {
		final EditScopeDialog dialog = new EditScopeDialog(getShell());
		dialog.create();
		dialog.open();
		refreshScopeCombo();
	}
	
	private void refreshScopeCombo() {
		final ITargetScopeManager scopeManager = workspace.getTargetScopeManager();
		scopeComboViewer.setInput(scopeManager.getAllScopes().toArray());
		scopeComboViewer.setSelection(new StructuredSelection(scopeManager.getActiveScope()));
	}
	
	private void createScopeURIGroup(Composite parent) {
		final Group group = new Group(parent, SWT.NONE);
		includeModelContent = new Button(group, SWT.CHECK);
		scopeURIViewer = new ListViewer(group, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		
		group.setText("Web Model");
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		
		includeModelContent.setText("Include previously discovered paths from Web model");
		includeModelContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		includeModelContent.setSelection(true);
		
		scopeURIViewer.setContentProvider(ArrayContentProvider.getInstance());
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);

		scopeURIViewer.getList().setLayoutData(gd);
		
		includeModelContent.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				scopeURIViewer.getList().setEnabled(includeModelContent.getSelection());
			}
		});
	}
	
	private void updatePage() {
		refreshScopeUriList(getScanTargetScope());
		enableButtonForIncludeModel();
		maybeDisplayError();
		setPageComplete(isTargetValid());
	}
	
	private void refreshScopeUriList(ITargetScope scope) {
		if(scope == null) {
			scopeURIViewer.setInput(new Object[0]);
			return;
		}
		
		final InScopeWebVisitor visitor = new InScopeWebVisitor(scope);
		workspace.getWebModel().accept(visitor);
		scopeURIViewer.setInput(visitor.getScopeURIs().toArray());
	}
	
	private void enableButtonForIncludeModel() {
		includeModelContent.setEnabled(scopeURIViewer.getList().getItems().length != 0);
	}
	
	private void maybeDisplayError() {
		setMessage(null);
		setErrorMessage(null);
		if(scanTargetUriText.isEnabled()) {
			maybeDisplayErrorForUriText();
		} else {
			maybeDisplayErrorForScopeCombo();
		}
	}
	
	private void maybeDisplayErrorForUriText() {
		final String text = scanTargetUriText.getText();
		if(text.isEmpty()) {
			setMessage("Enter a target URI");
			return;
		}
		if(!UriTools.isTextValidURI(scanTargetUriText.getText())) {
			setErrorMessage("Target URI is invalid.  Enter a properly formatted target URI.");
		}
	}
	
	private void maybeDisplayErrorForScopeCombo() {
		if(comboScope == null) {
			setMessage("Select a Target Scope");
		} else if(comboScope.isEmpty()) {
			setErrorMessage("Selected Target Scope is empty.  Edit scope to add target URIs");
		}
	}
}
