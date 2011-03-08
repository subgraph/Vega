package com.subgraph.vega.ui.scanner.wizards;

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
	
	private Text cookieString;
	private Table exclusionsTable;
	private final static String VEGA_LOGO = "icons/vega_small.png";
	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);


	
	public NewScanWizardPage2() {
		super("Create a New Scan");
		setTitle("Create a New Scan");
		setDescription("Cookie and Exclusions");
		setImageDescriptor(ImageDescriptor.createFromImage(imageCache.get(VEGA_LOGO)));

	}
	
	
	@Override
	public void createControl(Composite parent) {
		Composite container;
		final Text exclusionText;
		final Button addButton;
		final Button removeButton;
		Label cookieLabel;
		Label exclusionLabel;
		
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		
		cookieLabel = new Label(container, SWT.NULL);
		cookieLabel.setText("Input cookie string:");
		cookieString = new Text(container, SWT.BORDER | SWT.SINGLE);
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 140;
		gd.heightHint = 60;
		
		cookieString.setLayoutData(gd);
		
		exclusionLabel = new Label(container, SWT.NULL);
		exclusionLabel.setText("Set scan exclusion patterns:");
		exclusionText = new Text(container, SWT.BORDER | SWT.SINGLE);
		exclusionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton = new Button(container, SWT.PUSH);
		addButton.setText("Add exclusion");
		
		exclusionsTable = new Table(container, SWT.BORDER | SWT.MULTI);
		exclusionsTable.setLayoutData(gd);		
	
		removeButton = new Button(container, SWT.PUSH);
		removeButton.setText("Remove selected exclusion(s)");
		
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				exclusionsTable.remove(exclusionsTable.getSelectionIndices());
			}
		});
		
		addButton.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event event) {
		          if (event.widget == addButton) {
		        	  if (exclusionText.getText() != null) {
		        		  boolean found = false;
		        		  TableItem items[] = exclusionsTable.getItems();
		        		  for (TableItem t: items) 
		        			  if (exclusionText.getText().equals(t.getText()))
		        				  found = true;
		        		  if (!found)
		        		  {
		        			  TableItem newExclusion = new TableItem(exclusionsTable,SWT.NONE);
		        			  newExclusion.setText(exclusionText.getText());
		        		  }
		        	  }
		          }
		        }
		      });
		
		
		setControl(container);
		setPageComplete(true);
		
	}
	
	public String getCookieString() {
		return cookieString.getText();
	}
	
	public List<String> getExclusions() {
		ArrayList<String> exclusions = new ArrayList<String>();
		TableItem items[] = exclusionsTable.getItems();
		
		for (TableItem t: items) {
			exclusions.add(t.getText());
		}
		return exclusions;
	}

}
