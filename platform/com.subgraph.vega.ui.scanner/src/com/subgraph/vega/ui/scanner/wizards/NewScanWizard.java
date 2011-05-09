package com.subgraph.vega.ui.scanner.wizards;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;

public class NewScanWizard extends Wizard {
	private NewScanWizardPage page1;
	private NewScanWizardPage2 page2;
	private NewScanWizardPage3 page3;
	private URI scanHostURI;
	private String cookieString;
	private String validTargetField;
	private boolean isDomTest;
	private String targetField;
	private List<String> exclusions;
	private String basicUsername;
	private String basicPassword;
	private String basicRealm;
	private String basicDomain;
	private String ntlmUsername;
	private String ntlmPassword;
	
	@Override
	public void addPages() {
		page1 = new NewScanWizardPage(targetField);
		addPage(page1);
		page2 = new NewScanWizardPage2();
		addPage(page2);
		page3 = new NewScanWizardPage3();
		addPage(page3);
	}
	
	@Override 
	public boolean canFinish() {
		final String scanHostText = page1.getScanTarget();
		if(scanHostText.isEmpty())
			return false;
		
		return (createTargetURI(scanHostText) != null);
	}
	
	@Override
	public boolean performFinish() {
		String target = page1.getScanTarget();
		if(target.equals("domtest")) {
			isDomTest = true;
			return true;
		}
		
		exclusions = page2.getExclusions();
		scanHostURI = createTargetURI(page1.getScanTarget());
		cookieString = page2.getCookieString();
		basicUsername = page3.getBasicUsername();
		basicPassword = page3.getBasicPassword();
		basicRealm = page3.getBasicRealm();
		basicDomain = page3.getBasicRealm();
		ntlmUsername = page3.getNtlmUsername();
		ntlmPassword = page3.getNtlmPassword();
		
		for(String s: exclusions) {
			System.out.print(s);
		}
		if(scanHostURI != null)
			validTargetField = target;
		return (scanHostURI != null);		
	}
	
	public void setTargetField(String value) {
		targetField = value;
	}
	
	public URI getScanHostURI() {
		return scanHostURI;
	}
	
	public String getTargetField() {
		return validTargetField;
	}
	
	public String getCookieString() {
		return cookieString;
	}
	
	public boolean isDomTest() {
		return isDomTest;	
	}
	
	public String getBasicUsername() {
		return basicUsername;
	}
	
	public String getBasicPassword() {
		return basicPassword;
	}
	
	public String getBasicRealm() {
		return basicRealm;
	}
	
	public String getBasicDomain() {
		return basicDomain;
	}
	
	public List<String> getExclusions() {
		return exclusions;
	}
	
	public String getNtlmUsername() {
		return ntlmUsername;
	}
	
	public String getNtlmPassword() {
		return ntlmPassword;
	}
	
	URI createTargetURI(String value) {
		if(!(value.startsWith("http://") || value.startsWith("https://"))) {
			value = "http://"+ value;
		}
		try {
			return new URI(value);
		} catch (URISyntaxException e) {
			return null;
		}
	}

}
