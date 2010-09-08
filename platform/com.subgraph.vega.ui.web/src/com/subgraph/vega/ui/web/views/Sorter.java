package com.subgraph.vega.ui.web.views;

import org.eclipse.jface.viewers.ViewerSorter;

import com.subgraph.vega.api.model.web.IWebEntity;

public class Sorter extends ViewerSorter {
	public int category(Object element) {
		if(!(element instanceof IWebEntity))
			return 3;
		final IWebEntity we = (IWebEntity) element;
		return (we.isVisited()) ? (1) : (2);
	}

}
