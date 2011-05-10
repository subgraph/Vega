package com.subgraph.vega.api.scanner.modules;

public enum ModuleScriptType {
	RESPONSE_PROCESSOR("response-processor", "Response Processing Modules"),
	BASIC_MODULE("basic", "Basic module"),
	DOM_TEST("dom-test", "DOM testing Modules");

	private final String name;
	private final String verboseName;
	
	private ModuleScriptType(String name, String verbose) {
		this.name = name;
		this.verboseName = verbose;
	}
	
	public static ModuleScriptType lookup(String name) {
		for(ModuleScriptType type: ModuleScriptType.values()) {
			if(type.name.equals(name))
				return type;
		}
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	public String getVerboseName() {
		return verboseName;
	}
}
