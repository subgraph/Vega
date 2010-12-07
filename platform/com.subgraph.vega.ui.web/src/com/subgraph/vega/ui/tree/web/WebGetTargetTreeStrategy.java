package com.subgraph.vega.ui.tree.web;

import com.google.common.base.Strings;
import com.subgraph.vega.ui.tree.ITreeAdapter;

public class WebGetTargetTreeStrategy implements ITreeAdapter<Object> {
/*
	@Override
	public Object[] getChildren(IWebGetTarget item) {
		return new Object[0];
	}

	@Override
	public int getChildrenCount(IWebGetTarget item) {
		return 0;
	}

	@Override
	public Object getParent(IWebGetTarget item) {
		return item.getPath();
	}

	@Override
	public String getLabel(IWebGetTarget item) {
		return getPathLabel(item) + getQueryLabel(item);
	}
	
	private String getPathLabel(IWebGetTarget item) {
		String p = item.getPath().getPath();
		return (p == null || p.isEmpty()) ? ("/") : (p);
	}
	
	private String getQueryLabel(IWebGetTarget item) {
		return (Strings.isNullOrEmpty(item.getQuery())) ? ("") : (" ["+ item.getQuery() +"]");
	}
*/
	@Override
	public Object[] getChildren(Object item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getChildrenCount(Object item) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getParent(Object item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel(Object item) {
		// TODO Auto-generated method stub
		return null;
	}

}
