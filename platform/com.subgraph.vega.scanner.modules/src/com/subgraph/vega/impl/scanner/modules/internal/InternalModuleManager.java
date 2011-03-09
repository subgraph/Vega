package com.subgraph.vega.impl.scanner.modules.internal;

import java.util.ArrayList;
import java.util.List;

import com.subgraph.vega.api.scanner.modules.IScannerModule;

public class InternalModuleManager {
	
	private final List<InternalModule> modules = new ArrayList<InternalModule>();
	
	public InternalModuleManager() {
		final String cat = "Injection Checks";
		register(cat, "SQL Injection");
		register(cat, "XSS Injection");
		register(cat, "XML Injection");
		register(cat, "Shell Command Injection");
	}
	
	private void register(String category, String name) {
		final InternalModule m = new InternalModule(category, name);
		modules.add(m);
	}
	
	
	public List<IScannerModule> getModules(boolean enabledOnly) {
		final List<IScannerModule> result = new ArrayList<IScannerModule>();
		for(InternalModule m: modules) {
			if((enabledOnly && m.isEnabled()) || (!enabledOnly))
				result.add(m);
		}
		return result;
	}
}
