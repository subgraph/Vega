package com.subgraph.vega.ui.tree;

public interface ITreeAdapter<E> {
	Object[] getChildren(E item);
	int getChildrenCount(E item);
	Object getParent(E item);
	String getLabel(E item);
}
