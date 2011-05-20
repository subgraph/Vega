package com.subgraph.vega.ui.scanner.wizards;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
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

import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.util.ImageCache;

public class NewScanWizardPage2 extends WizardPage {
	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);
	private final static String VEGA_LOGO = "icons/vega_small.png";
	private Table cookiesTable;
	private Table exclusionsTable;
	
	public NewScanWizardPage2() {
		super("Create a New Scan");
		setTitle("Create a New Scan");
		setDescription("Cookie and Exclusions");
		setImageDescriptor(ImageDescriptor.createFromImage(imageCache.get(VEGA_LOGO)));
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);		
		container.setLayout(new GridLayout());
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 140;
		gd.heightHint = 60;
		createCookiesPart(container, gd);
		createExclusionsPart(container, gd);

		setControl(container);
		setPageComplete(true);
	}

	private void createCookiesPart(Composite parent, GridData exlusionTableLayoutData) {
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
	        				for (TableItem t: items) { 
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
	        				TableItem newCookie = new TableItem(cookiesTable, SWT.NONE);
	        				newCookie.setText(cookieText.getText());
		        	  }
		          }
	        }
		});

		cookiesTable = new Table(parent, SWT.BORDER | SWT.MULTI);
		cookiesTable.setLayoutData(exlusionTableLayoutData);

		final Button removeButton = new Button(parent, SWT.PUSH);
		removeButton.setText("Remove selected cookie(s)");		
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				cookiesTable.remove(cookiesTable.getSelectionIndices());
			}
		});
	}
	
	private void createExclusionsPart(Composite parent, GridData exlusionTableLayoutData) {
		final Label exclusionLabel = new Label(parent, SWT.NULL);
		exclusionLabel.setText("Set scan exclusion patterns:");

		final Text exclusionText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		exclusionText.setMessage("regular expression");
		exclusionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button addButton = new Button(parent, SWT.PUSH);
		addButton.setText("Add exclusion");
		addButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
	        	if (event.widget == addButton) {
	        		final String value = exclusionText.getText(); 
	        			if (value != null) {
	        				TableItem items[] = exclusionsTable.getItems();
	        				for (TableItem t: items) { 
	        					if (exclusionText.getText().equals(t.getText())) {
	        						return;
	        					}
	        				}	        				
	        				TableItem newExclusion = new TableItem(exclusionsTable, SWT.NONE);
	        				newExclusion.setText(exclusionText.getText());
		        	  }
		          }
	        }
		});

		exclusionsTable = new Table(parent, SWT.BORDER | SWT.MULTI);
		exclusionsTable.setLayoutData(exlusionTableLayoutData);

		final Button removeButton = new Button(parent, SWT.PUSH);
		removeButton.setText("Remove selected exclusion(s)");		
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				exclusionsTable.remove(exclusionsTable.getSelectionIndices());
			}
		});
	}

	public List<String> getCookieStringList() {
		return getTableItemsAsString(cookiesTable);
	}
	
	public List<String> getExclusions() {
		return getTableItemsAsString(exclusionsTable);
	}

	private List<String> getTableItemsAsString(Table table) {
		ArrayList<String> list = new ArrayList<String>();
		for (TableItem t: table.getItems()) {
			list.add(t.getText());
		}
		return list;
	}

}
