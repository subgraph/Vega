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
import com.subgraph.vega.api.scanner.modules.ModuleScriptType;

import java.util.logging.Logger;

public class ModuleRegistryContentProvider implements ITreeContentProvider {

	private final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	private  Map<ModuleScriptType, List<IScannerModule>> modulesMap;
	Set<String> types = new HashSet<String>();

	private final Logger logger;

	
	public ModuleRegistryContentProvider() {
		
		modulesMap = new LinkedHashMap<ModuleScriptType, List<IScannerModule>>();
		logger = Logger.getLogger("scan-modules-view");
		
	}
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
		if (newInput instanceof IScannerModuleRegistry) {
			
			modulesMap.clear();
			
			List<IScannerModule> modules = ((IScannerModuleRegistry)newInput).getAllModules(false);
			
			for(ModuleScriptType s: ModuleScriptType.values()) 
				modulesMap.put(s, new ArrayList<IScannerModule>());

			for(IScannerModule m: modules) 
				modulesMap.get(m.getModuleType()).add(m);
			
		}


		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		
		final List<ModuleScriptType> roots = new ArrayList<ModuleScriptType>();
		
			for(ModuleScriptType t: modulesMap.keySet()) {
				if(!modulesMap.get(t).isEmpty())
					roots.add(t);
			}
		return roots.toArray();

	}

	@Override
	public Object[] getChildren(Object parentElement) {
		
		if (parentElement instanceof ModuleScriptType) {
			ModuleScriptType type = (ModuleScriptType) parentElement;
			List<IScannerModule> modules = modulesMap.get(type);
			return modules.toArray();
		}
		return EMPTY_OBJECT_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		logger.warning("getParent");

		if (element instanceof IScannerModule) {
			return ((IScannerModule) element).getModuleType();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub

		if (element instanceof ModuleScriptType) 
			return true;
		return false;

	}

}
