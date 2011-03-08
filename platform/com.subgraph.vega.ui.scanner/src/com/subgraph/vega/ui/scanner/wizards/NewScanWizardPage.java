package com.subgraph.vega.ui.scanner.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.scanner.modules.ModuleRegistryContentProvider;
import com.subgraph.vega.ui.scanner.modules.ModuleRegistryLabelProvider;
import com.subgraph.vega.ui.util.ImageCache;

public class NewScanWizardPage extends WizardPage {

	private Composite container;
	private IScannerModuleRegistry registry;
	private CheckboxTreeViewer viewer;
	private Text scanTarget;
	private Text cookieString;
	private String targetValue;
	private final static String VEGA_LOGO = "icons/vega_small.png";
	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);

	
	public NewScanWizardPage(String targetValue) {
		super("Create a New Scan");
		setTitle("Create a New Scan");
		setDescription("New Scan Prameters");
		setImageDescriptor(ImageDescriptor.createFromImage(imageCache.get(VEGA_LOGO)));
		this.targetValue = targetValue;
		registry = Activator.getDefault().getIScannerModuleRegistry();
	}
	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		Label cookieLabel;
		Label modulesLabel;
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		Label label = new Label(container, SWT.NULL);
		label.setText("Input the base URI:");
		
		scanTarget = new Text(container, SWT.BORDER | SWT.SINGLE);
		if(targetValue != null)
			scanTarget.setText(targetValue);
		else
			scanTarget.setText("");
		scanTarget.addKeyListener(new KeyListener() {
			@Override 
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if (!scanTarget.getText().isEmpty()) {
					setPageComplete(true);
				}
			}		  
		  
		});
		
		modulesLabel = new Label(container, SWT.NULL);
		modulesLabel.setText("Select the modules to run:");
		
		viewer = new CheckboxTreeViewer(container,SWT.BORDER| SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ModuleRegistryContentProvider());
		viewer.setLabelProvider(new ModuleRegistryLabelProvider());
		
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH)); 

	    viewer.setInput(registry);
        viewer.addCheckStateListener(new ICheckStateListener(){
            public void checkStateChanged(CheckStateChangedEvent event){
                if(event.getChecked()){
                    viewer.setSubtreeChecked(event.getElement(), true);
                }
            }
        });  
		

		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		scanTarget.setLayoutData(gd);

		setControl(container);
		setPageComplete(false);
		
	}
		
	public String getScanTarget() {
		return scanTarget.getText();
	}			
	
	public String getCookieString() {
		return cookieString.getText();
	}
	
}