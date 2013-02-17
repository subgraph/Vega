/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.ui.util.modules;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;

import com.subgraph.vega.api.scanner.modules.IEnableableModule;

public class ModuleRegistryCheckStateProvider implements ICheckStateProvider, ICheckStateListener {

	private final CheckboxTreeViewer viewer;
	private ModuleTreeData treeData;
	
	public ModuleRegistryCheckStateProvider(CheckboxTreeViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public boolean isChecked(Object element) {
		if(element instanceof IEnableableModule) {
			final IEnableableModule m = (IEnableableModule) element;
			return m.isEnabled();
		} else if((element instanceof String) && (treeData != null)) {
			final String category = (String) element;
			return treeData.someEnabledInCategory(category);
		}
		return false;
	}

	@Override
	public boolean isGrayed(Object element) {
		if(!(element instanceof String))
			return false;
		if(treeData == null)
			return false;
		final String category = (String) element;
		return (treeData.someEnabledInCategory(category) && !treeData.allEnabledInCategory(category));
	}

	private void setEnableStateForCategory(String category, boolean state) {
		if(treeData == null)
			return;
		else
			treeData.setEnableStateForCategory(category, state);
	}

	void setTreeData(ModuleTreeData treeData) {
		this.treeData = treeData;
	}

	@Override
	public void checkStateChanged(CheckStateChangedEvent event) {
		if(event.getElement() instanceof IEnableableModule) {
			final IEnableableModule m = (IEnableableModule) event.getElement();
			m.setEnabled(event.getChecked());
			viewer.refresh();
		} else if(event.getElement() instanceof String) {
			final String category = (String) event.getElement();
			setEnableStateForCategory(category, event.getChecked());
			viewer.setSubtreeChecked(event.getElement(), event.getChecked());
			viewer.refresh();
		}
	}
}
