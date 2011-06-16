/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.ui.tree.web;

import java.util.Collection;

import com.google.common.collect.Iterables;

import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.ui.tree.ITreeAdapter;

public class WebHostTreeStrategy implements ITreeAdapter<IWebHost> {
	private final WebPathTreeStrategy pathTreeStrategy = new WebPathTreeStrategy();
	
	@Override
	public Object[] getChildren(IWebHost item) {
		IWebPath rootPath = item.getRootPath();
		boolean noResponses = (rootPath.getGetResponses().size() == 0) && (rootPath.getPostResponses().size() == 0);

		Collection<IWebPath> childPaths = rootPath.getChildPaths();
		
		if(childPaths.isEmpty() && noResponses)
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
