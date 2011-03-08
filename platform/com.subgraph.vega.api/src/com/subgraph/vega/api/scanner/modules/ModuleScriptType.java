package com.subgraph.vega.api.scanner.modules;

public enum ModuleScriptType {
	DISABLED("disabled"),
	PER_SERVER("per-server"),
	PER_DIRECTORY("per-directory"),
	PER_MOUNTPOINT("per-mountpoint"),
	PER_RESOURCE("per-resource"),
	RESPONSE_PROCESSOR("response-processor"),
	DOM_TEST("dom-test");

	private final String name;
	private ModuleScriptType(String name) {
		this.name = name;
	}
	
	public static ModuleScriptType lookup(String name) {
		for(ModuleScriptType type: ModuleScriptType.values()) {
			if(type.name.equals(name))
				return type;
		}
		return null;
	}
}
