package com.subgraph.vega.ui.http.proxy;

import org.eclipse.jface.viewers.LabelProvider;

import com.subgraph.vega.api.scanner.modules.IScannerModule;

public class ConfigureProxyModulesLabelProvider extends LabelProvider {
	
	public String getText(Object element) {
		if (element instanceof IScannerModule) {
			return ((IScannerModule) element).getModuleName();
		} else if (element instanceof String) {
			return getCategoryText((String) element);
		} else {
			return null;
		}
	}
	
	private String getCategoryText(String text) {
		if(text.toLowerCase().contains("injection")) {
			return text + " (When Proxy Scanning is active)";
		} else {
			return text;
		}
	}

}
