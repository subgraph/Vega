package com.subgraph.vega.ui.scanner.wizards;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.api.model.identity.IIdentity;

public class NewScanAuthPage extends WizardPage {
	private Table cookiesTable;
	static private final Object emptyIdentity = new Object();
	private final List<Object> identities;
	private ComboViewer scanIdentityViewer;


	protected NewScanAuthPage(Collection<IIdentity> identities) {
		super("Authentication");
		setTitle("Authentication Options");
		setDescription("Configure cookies and authentication identity to use during scan");
		this.identities = new ArrayList<Object>(identities.size() + 1);
		this.identities.add(emptyIdentity);
		this.identities.addAll(identities);

	}

	@Override
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		createIdentityPart(container);
		createCookiesPart(container);
		setControl(container);
	}

	private void createIdentityPart(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Identity to scan site as:");

		scanIdentityViewer = new ComboViewer(parent, SWT.READ_ONLY);
		scanIdentityViewer.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		scanIdentityViewer.setContentProvider(ArrayContentProvider.getInstance());
		scanIdentityViewer.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				if (element != emptyIdentity) {
					return ((IIdentity)element).getName();
				} else {
					return "";
				}
			}
		});
		Object[] identitiesCp = identities.toArray(new Object[0]);
		scanIdentityViewer.setInput(identitiesCp);
		scanIdentityViewer.setSelection(new StructuredSelection(identitiesCp[0]));
	}

	private void createCookiesPart(Composite parent) {
		
		final Label cookieLabel = new Label(parent, SWT.NULL);
		cookieLabel.setText("Set-Cookie or Set-Cookie2 value:");

		final Text cookieText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		cookieText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button addButton = new Button(parent, SWT.PUSH);
		addButton.setText("Add cookie");
		addButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.widget == addButton) {
					final String value = cookieText.getText();
					if (value != null) {
						TableItem items[] = cookiesTable.getItems();
						for (TableItem t : items) {
							if (cookieText.getText().equals(t.getText())) {
								return;
							}
						}
						try {
							HttpCookie.parse(cookieText.getText());
						} catch (Exception e) {
							setErrorMessage("Cookie error: " + e.getMessage());
							return;
						}
						TableItem newCookie = new TableItem(cookiesTable,
								SWT.NONE);
						newCookie.setText(cookieText.getText());
					}
				}
			}
		});

		cookiesTable = new Table(parent, SWT.BORDER | SWT.MULTI);
		final GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.widthHint = 140;
		gd.heightHint = 60;
		cookiesTable.setLayoutData(gd);
		
		final Button removeButton = new Button(parent, SWT.PUSH);
		removeButton.setText("Remove selected cookie(s)");
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				cookiesTable.remove(cookiesTable.getSelectionIndices());
			}
		});
	}
	
	public List<String> getCookieStringList() {
		return getTableItemsAsString(cookiesTable);
	}

	private List<String> getTableItemsAsString(Table table) {
		ArrayList<String> list = new ArrayList<String>();
		for (TableItem t: table.getItems()) {
			list.add(t.getText());
		}
		return list;
	}

	public IIdentity getScanIdentity() {
		Object selection = ((IStructuredSelection) scanIdentityViewer.getSelection()).getFirstElement();
		if (selection != emptyIdentity) {
			return  (IIdentity)selection;
		} else {
			return null;
		}
	}
}
