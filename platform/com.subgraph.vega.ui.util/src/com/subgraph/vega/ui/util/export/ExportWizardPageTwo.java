package com.subgraph.vega.ui.util.export;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.ui.util.Activator;

public class ExportWizardPageTwo extends WizardPage {

	protected Label label;
	private Composite container;
	private final IModel model;
	private IScanInstance scanInstance = null;
	private final Logger logger = Logger.getLogger("export-wizard");
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
    Tree alertsTree;
    ScrolledComposite treeContainer;
    
	private final static String SCAN_IMAGE = "/icons/scanner.png";
	private final static String PROXY_IMAGE = "/icons/proxy.png";

	protected ExportWizardPageTwo(IScanInstance s) {
		super("Alerts");
		setTitle("Scans to include in report.");

		scanInstance = s;
		
		model = Activator.getDefault().getModel();
		if (model == null) {
			logger.warning("Failed to obtain reference to model");
			return;
		}
		//model.getCurrentWorkspace().getScanAlertRepository().

	}

	@Override
	public void createControl(Composite parent) {
		
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		TreeItem scanInstanceItem = null;
		
		container.setLayout(layout);
		layout.numColumns = 2;
		setPageComplete(false);
	    
		Label selectAllLabel = new Label(container, SWT.NONE);
		selectAllLabel.setText("Select all:");
		Button selectAllButton = new Button(container, SWT.CHECK);
		
		selectAllButton.addSelectionListener(new SelectionListener() { 

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if (selectAllButton.getSelection() == true) {
					for (TreeItem t: alertsTree.getItems()) {
						t.setChecked(true);
						setPageComplete(true);
					}
				}
			
	  			treeContainer.setOrigin (0, 10);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}}); 
		
		treeContainer = new ScrolledComposite (container, SWT.VERTICAL);
		
		GridData treeContainerGridData = new GridData();
		treeContainerGridData.horizontalSpan = 2;
		treeContainerGridData.horizontalAlignment = GridData.FILL;
		treeContainer.setLayoutData(treeContainerGridData);
		treeContainer.setBounds (10, 10, 280, 200);
		
		alertsTree = new Tree(treeContainer, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL
	            | SWT.H_SCROLL | SWT.MULTI);
		

	    IScanInstance proxyScanInstance = model.getCurrentWorkspace().getScanAlertRepository().getProxyScanInstance(); 
		if (proxyScanInstance != null) {
			TreeItem item = new TreeItem(alertsTree, SWT.NONE);
			item.setText("Proxy alerts (" + model.getCurrentWorkspace().getScanAlertRepository().getProxyScanInstance().getAllAlerts().size() + ")");	
			// TODO: If proxy scan is enabled, we probably shouldn't be letting the user export its alerts, but maybe we don't care..
			item.setImage(ImageDescriptor.createFromURL(getClass().getResource(PROXY_IMAGE)).createImage());
			item.setData(proxyScanInstance.getAllAlerts());
		}

		for (IScanInstance s: model.getCurrentWorkspace().getScanAlertRepository().getAllScanInstances()) {
			String time = "";
			if (s.getStartTime() != null && 
					((s.getScanStatus() == IScanInstance.SCAN_CANCELLED) || (s.getScanStatus() == IScanInstance.SCAN_COMPLETED))) {
				TreeItem item = new TreeItem(alertsTree, SWT.NONE);
				time = dateFormat.format(s.getStartTime());
				item.setText(time + " [" + statusToString(s.getScanStatus()) + "] ("+s.getAllAlerts().size()+")");
				item.setImage(ImageDescriptor.createFromURL(getClass().getResource(SCAN_IMAGE)).createImage());
				item.setData(s.getAllAlerts());
				if (scanInstance != null) {
					if (s.getScanId() == scanInstance.getScanId()) {
						item.setChecked(true);
						setPageComplete(true);
						scanInstanceItem = item;
					}
				}
			}
		}
		
		treeContainer.setContent(alertsTree);

	    final int width = treeContainer.getClientArea ().width;

		int height = alertsTree.computeSize (SWT.DEFAULT, SWT.DEFAULT).y;
		alertsTree.setSize (width, height > 280 ? 280 : height);
		
		alertsTree.addTreeListener (new TreeListener () {
			@Override
			public void treeExpanded (TreeEvent e) {
				int height = alertsTree.computeSize (SWT.DEFAULT, SWT.DEFAULT).y;
				alertsTree.setSize (width, height);
			}
			@Override
			public void treeCollapsed (TreeEvent e) {
				int height = alertsTree.computeSize (SWT.DEFAULT, SWT.DEFAULT).y;
				alertsTree.setSize (width, height);
			}
			
			
		});
		
		alertsTree.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent e) {
		          TreeItem ti = (TreeItem) e.item;
		          if (ti.getChecked() == false) {
		        	  if (selectAllButton.getSelection() == true) {
		        		  selectAllButton.setSelection(false);
		        	  }
		        	
		          }
		          
		          boolean anyChecked = false;
		          for (TreeItem t : alertsTree.getItems()) {
		        	  if (t.getChecked() == true) {
		        		anyChecked = true;
		        	  }
		          }
		          setPageComplete(anyChecked);
		      }
			
		});
		

		setControl(container);
		
		if (scanInstanceItem != null) {
			for (TreeItem sItem : alertsTree.getItems()) {
				if (sItem == scanInstanceItem) {
					Rectangle itemRectangle = sItem.getBounds();
					Rectangle clientArea = treeContainer.getClientArea();
					Point origin = treeContainer.getOrigin();
					if (itemRectangle.x < origin.x || itemRectangle.y < origin.y
							|| itemRectangle.x + itemRectangle.width > origin.x + clientArea.width
							|| itemRectangle.y + itemRectangle.height > origin.y + clientArea.height) {
						treeContainer.setOrigin(0, itemRectangle.y);
						origin = treeContainer.getOrigin();
						treeContainer.setOrigin(new Point(sItem.getBounds().x, sItem.getBounds().y));
					} 
					else {
						System.out.println("nope");
					}
					
				}
			}

		}
		
	}
	
	
	protected List<IScanAlert> allAlertsFromTree() {
		
		List<IScanAlert> alerts = new ArrayList<IScanAlert>();
				
		for (TreeItem t : alertsTree.getItems()) {
			if (t.getChecked() == true) {
		//		IScanInstance s = (IScanInstance)(t.getData());
				List<IScanAlert> scanInstanceAlerts = (List<IScanAlert>) t.getData();
				//s.getAllAlerts();
				alerts.addAll(scanInstanceAlerts);
			}
		}
		
		return alerts;
		
	}
	
	private String statusToString(int status) {
		
		String s = "";
		
		switch(status) {
		
		case IScanInstance.SCAN_PROBING:
			s = "Probing Server";
			break;
		case IScanInstance.SCAN_STARTING:
			s =  "Starting";
			break;
		case IScanInstance.SCAN_AUDITING:
			s = "Auditing";
			break;
		case IScanInstance.SCAN_CANCELLED:
			s = "Cancelled";
			break;
		case IScanInstance.SCAN_COMPLETED:
			s = "Completed";
			break;
		}
		return s;
	
	}
	

}
