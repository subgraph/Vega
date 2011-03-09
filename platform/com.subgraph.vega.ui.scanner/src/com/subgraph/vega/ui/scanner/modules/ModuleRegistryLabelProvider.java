package com.subgraph.vega.ui.scanner.modules;

import org.eclipse.jface.viewers.LabelProvider;

import com.subgraph.vega.api.scanner.modules.IScannerModule;

public class ModuleRegistryLabelProvider extends LabelProvider {

	public String getText(Object element) {
		if (element instanceof IScannerModule)
			return ((IScannerModule) element).getModuleName();
		else if (element instanceof String)
			return (String) element;
		else
			return null;
	}
}
