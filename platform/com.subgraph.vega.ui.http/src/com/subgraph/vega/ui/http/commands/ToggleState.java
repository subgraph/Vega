package com.subgraph.vega.ui.http.commands;

import org.eclipse.core.commands.State;

public class ToggleState extends State {
	
	public ToggleState() {
		setValue(Boolean.FALSE);
	}

	public void setStateValue(boolean value) {
		if(value) {
			setValue(Boolean.TRUE);
		} else {
			setValue(Boolean.FALSE);
		}
	}
}
