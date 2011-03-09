package com.subgraph.vega.ui.scanner.modules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.scanner.modules.IScannerModule;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;

public class ModuleRegistryContentProvider implements ITreeContentProvider {

	private final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	private  Map<String, List<IScannerModule>> modulesMap;
	Set<String> types = new HashSet<String>();
	
	public ModuleRegistryContentProvider() {
		modulesMap = new LinkedHashMap<String, List<IScannerModule>>();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		if (newInput instanceof IScannerModuleRegistry) {
			
			modulesMap.clear();
			
			List<IScannerModule> modules = ((IScannerModuleRegistry)newInput).getAllModules(false);
			
			for(IScannerModule m: modules) {
				getModuleListByCategory(m.getModuleCategoryName()).add(m);
			}
			
		}	
	}
	
	private List<IScannerModule> getModuleListByCategory(String categoryName) {
		if(!modulesMap.containsKey(categoryName)) 
			modulesMap.put(categoryName, new ArrayList<IScannerModule>());
		return modulesMap.get(categoryName);
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		final List<String> roots = new ArrayList<String>();
		for(String category: modulesMap.keySet()) {
			if(!modulesMap.get(category).isEmpty())
				roots.add(category);
		}
		return roots.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof String) {
			String category = (String) parentElement;
			List<IScannerModule> modules = modulesMap.get(category);
			return modules.toArray();
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
		if (element instanceof String) {
			final String category = (String) element;
			return (modulesMap.containsKey(category) && modulesMap.get(category).size() > 0);
		}
		return false;
	}

}
