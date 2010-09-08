package com.subgraph.vega.ui.tree.web;

import java.util.Collection;

import com.google.common.collect.Iterables;
import com.subgraph.vega.api.model.web.IWebGetTarget;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.ui.tree.ITreeAdapter;

public class WebHostTreeStrategy implements ITreeAdapter<IWebHost> {
	private final WebPathTreeStrategy pathTreeStrategy = new WebPathTreeStrategy();
	
	@Override
	public Object[] getChildren(IWebHost item) {
		IWebPath rootPath = item.getRootPath();
		Collection<IWebPath> childPaths = rootPath.getChildPaths();
		Collection<IWebGetTarget> targets = rootPath.getTargets();
		if(childPaths.isEmpty() && targets.isEmpty())
			return new Object[] { rootPath };
		else if(childPaths.isEmpty())
			return Iterables.toArray(childPaths, Object.class);
		else
			return pathTreeStrategy.getChildren(rootPath);
	}

	@Override
	public int getChildrenCount(IWebHost item) {
		return getChildren(item).length;
	}

	@Override
	public Object getParent(IWebHost item) {
		return null;
	}

	@Override
	public String getLabel(IWebHost item) {
		return item.getHostname();
	}

}
