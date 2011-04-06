package com.subgraph.vega.impl.scanner.forms;

public class FormParameter {
	static FormParameter create(String name, String value) {
		return null;
	}

	private final String name;
	private final String value;
	private final boolean isPasswordField;
	private final boolean needsGuess;

	FormParameter(String name, String value, boolean isPassword, boolean needsGuess) {
		this.name = name;
		this.value = value;
		this.isPasswordField = isPassword;
		this.needsGuess = needsGuess;
	}

}
