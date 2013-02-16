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

import java.util.ArrayList;
import java.util.List;

import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.model.web.IWebResponse;
import com.subgraph.vega.ui.tree.ITreeAdapter;

public class WebPathTreeStrategy implements ITreeAdapter<IWebPath> {

	@Override
	public Object[] getChildren(IWebPath item) {
		List<Object> children = new ArrayList<Object>();
		for(IWebPath cp : item.getChildPaths()) {
			Object node = collapsedNode(item, cp);
			if(node != null)
				children.add(node);
			for(IWebResponse r: cp.getGetResponses()) 
				children.add(r);
			for(IWebResponse r: cp.getPostResponses())
				children.add(r);
		}
		
		return children.toArray();
	}
	
	private Object collapsedNode(IWebPath root, IWebPath p) {
		boolean noResponses = (p.getGetResponses().size() == 0) && (p.getPostResponses().size() == 0);
		if(p.getChildPaths().size() == 1 && noResponses)
			return collapsedNode(root, p.getChildPaths().iterator().next());
		else if(p.getChildPaths().isEmpty() && !noResponses) {
			IWebPath pp = p.getParentPath();
			if(pp == null || pp == root)
				return null;
			else
				return pp;
		} else {
			return p;
		}
	}
	
	@Override
	public int getChildrenCount(IWebPath item) {
		return getChildren(item).length;
	}

	@Override
	public Object getParent(IWebPath item) {
		if(item.getParentPath() == null)
			return item.getMountPoint().getWebHost();
		else if(item.getParentPath().getChildPaths().size() == 1)
			return getParent(item.getParentPath());
		else
			return item.getParentPath();
	}

	@Override
	public String getLabel(IWebPath item) {
		if(item.getParentPath() == null)
			return "/";
		else if(getChildrenCount(item) == 0 && (item.getPathComponent().contains(".") || item.getMimeType() != null)) {
			return item.getPathComponent();
			
		} else {
			return prevLabel(item) + "/" + item.getPathComponent();
		}
	}
	
	private String prevLabel(IWebPath item) {
		IWebPath pp = item.getParentPath();
		if(pp.getChildPaths().size() == 1 && pp.getParentPath() != null)
			return getLabel(pp);
		else
			return "";
	}

	
}
