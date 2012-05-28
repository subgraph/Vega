package com.subgraph.vega.ui.identity.identityview.tree;

public class StringTreeNode implements IIdentityTreeNode {
	private final String text;

	public StringTreeNode(String text) {
		this.text = text;
	}
	
	@Override
	public String getText() {
		return text;
	}

	@Override
	public String getImagePath() {
		return null;
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
