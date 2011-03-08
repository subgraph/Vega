package com.subgraph.vega.ui.scanner.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.util.ImageCache;

public class NewScanWizardPage3 extends WizardPage {

	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);
	private final static String VEGA_LOGO = "icons/vega_small.png";

	Composite container;
	Composite innerContainer;
	Composite innerContainer2;
	
	private Text basicUsername;
	private Text basicPassword;
	private Text basicRealm;
	private Text basicDomain;
	private Text ntlmUsername;
	private Text ntlmPassword;
	
	
	public NewScanWizardPage3() {
		super("Create a New Scan");
		setTitle("Create a New Scan");
		setDescription("Authentication");
		setImageDescriptor(ImageDescriptor.createFromImage(imageCache.get(VEGA_LOGO)));

	}



	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout innerLayout = new GridLayout();
		Label basicLabel;
		Label basicUsernameLabel;
		Label basicPasswordLabel;
		Label basicRealmLabel;
		Label basicDomainLabel;
		Label ntlmLabel;
		Label ntlmUsernameLabel;
		Label ntlmPasswordLabel;
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		GridData gd2 = new GridData();
		gd2.horizontalSpan = 2;
		
		innerLayout.numColumns = 2;
		container.setLayout(innerLayout);
		
		basicLabel = new Label(container, SWT.NONE);
		basicLabel.setText("Basic Authentication");
		basicLabel.setLayoutData(gd2);
	
		
		basicUsernameLabel = new Label(container, SWT.NONE);
		basicUsernameLabel.setText("Username:");
		basicUsername = new Text(container, SWT.SINGLE | SWT.BORDER);
		basicUsername.setLayoutData(gd);
				
		basicPasswordLabel = new Label(container, SWT.NONE);
		basicPasswordLabel.setText("Password:");
		basicPassword = new Text(container, SWT.BORDER);
		basicPassword.setLayoutData(gd);
		
		basicRealmLabel = new Label(container, SWT.NONE);
		basicRealmLabel.setText("Realm:");
		basicRealm = new Text(container, SWT.BORDER);
		basicRealm.setLayoutData(gd);
		
		basicDomainLabel = new Label(container, SWT.NONE);
		basicDomainLabel.setText("Domain");
		basicDomain = new Text(container, SWT.BORDER);
		basicDomain.setLayoutData(gd);
		
		ntlmLabel = new Label(container, SWT.NONE);
		ntlmLabel.setText("NTLM Authentication");
		ntlmLabel.setLayoutData(gd2);
		

		
		ntlmUsernameLabel = new Label(container, SWT.NONE);
		ntlmUsernameLabel.setText("Username:");
		ntlmUsername = new Text(container, SWT.BORDER);
		ntlmUsername.setLayoutData(gd);
		
		ntlmPasswordLabel = new Label(container, SWT.NONE);
		ntlmPasswordLabel.setText("Passowrd:");
		ntlmPassword = new Text(container, SWT.BORDER);
		ntlmPassword.setLayoutData(gd);
		
		setControl(container);
		setPageComplete(true);
	}

	
	public String getBasicUsername() {
		return basicUsername.getText();
	}
	
	public String getBasicPassword() {
		return basicPassword.getText();
	}
	
	public String getBasicRealm() {
		return basicRealm.getText();
	}
	
	public String getBasicDomain() {
		return basicDomain.getText();
	}
	
	public String getNtlmUsername() {
		return ntlmUsername.getText();
	}
	
	public String getNtlmPassword() {
		return ntlmPassword.getText();
	}
	
}
