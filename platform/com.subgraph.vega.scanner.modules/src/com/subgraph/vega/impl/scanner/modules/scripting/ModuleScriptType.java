package com.subgraph.vega.impl.scanner.modules.scripting;

public enum ModuleScriptType {
	PER_SERVER("per-server"),
	PER_DIRECTORY("per-directory"),
	RESPONSE_PROCESSOR("response-processor");

	private final String name;
	private ModuleScriptType(String name) {
		this.name = name;
	}
	
	static ModuleScriptType lookup(String name) {
		for(ModuleScriptType type: ModuleScriptType.values()) {
			if(type.name.equals(name))
				return type;
		}
		return null;
	}
	
}
