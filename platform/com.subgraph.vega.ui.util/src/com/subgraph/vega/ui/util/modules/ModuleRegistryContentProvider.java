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

import java.util.Arrays;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.scanner.modules.IScannerModule;

public class ModuleRegistryContentProvider implements ITreeContentProvider {

	private final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	private final ModuleRegistryCheckStateProvider checkStateProvider;
	private ModuleTreeData treeData;
	
	public ModuleRegistryContentProvider(ModuleRegistryCheckStateProvider checkStateProvider) {
		this.checkStateProvider = checkStateProvider;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof IScannerModule[]) {
			IScannerModule[] modules = (IScannerModule[]) newInput;
			treeData = new ModuleTreeData(Arrays.asList(modules));
			checkStateProvider.setTreeData(treeData);	
		}	
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(treeData != null)
			return treeData.getAllCategories().toArray();
		else
			return EMPTY_OBJECT_ARRAY;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if ((parentElement instanceof String) && (treeData != null)) {
			return treeData.getModuleListForCategory((String) parentElement).toArray();
		}
		return EMPTY_OBJECT_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IScannerModule) 
			return ((IScannerModule) element).getModuleCategoryName();
		else
			return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if ((element instanceof String) && (treeData != null)) {
			return treeData.getModuleListForCategory((String) element).size() > 0;
		}
		return false;
	}
}
