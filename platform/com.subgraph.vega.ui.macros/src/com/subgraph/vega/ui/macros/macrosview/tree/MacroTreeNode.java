package com.subgraph.vega.ui.macros.macrosview.tree;

import com.subgraph.vega.api.model.macros.IHttpMacro;

public class MacroTreeNode implements IMacroTreeNode {
	private static final String IMAGE_PATH = "icons/macro.png";
	private final IHttpMacro macro;

	public MacroTreeNode(IHttpMacro macro) {
		this.macro = macro;
	}
	
	@Override
	public String getText() {
		return macro.getName();
	}

	@Override
	public String getImagePath() {
		return IMAGE_PATH;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public Object[] getChildren() {
		return null;
	}

}
