package com.subgraph.vega.ui.scanner.modules;

import org.eclipse.jface.viewers.ICheckStateProvider;

import com.subgraph.vega.api.scanner.modules.IEnableableModule;

public class ModuleRegistryCheckStateProvider implements ICheckStateProvider{

	@Override
	public boolean isChecked(Object element) {
		if(element instanceof IEnableableModule) {
			final IEnableableModule m = (IEnableableModule) element;
			return m.isEnabled();
		}
		return false;
	}

	@Override
	public boolean isGrayed(Object element) {
		return false;
	}

}
