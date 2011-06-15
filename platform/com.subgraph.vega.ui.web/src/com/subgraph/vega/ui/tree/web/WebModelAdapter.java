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


import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.model.web.IWebResponse;
import com.subgraph.vega.ui.tree.ITreeAdapter;


public class WebModelAdapter {
	
	private final static Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	private final ITreeAdapter<IWebHost> webHostAdapter = new WebHostTreeStrategy();
	private final ITreeAdapter<IWebPath> webPathAdapter = new WebPathTreeStrategy();
	private final ITreeAdapter<IWebResponse> webResponseAdapter = new WebResponseTreeStrategy();

	
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IWebHost) 
			return webHostAdapter.getChildren((IWebHost) parentElement);
		else if(parentElement instanceof IWebPath)
			return webPathAdapter.getChildren((IWebPath) parentElement);
		else if(parentElement instanceof IWebResponse)
			return webResponseAdapter.getChildren((IWebResponse) parentElement);
		else
			return EMPTY_OBJECT_ARRAY;
	}
	
	public Object getParent(Object element) {
		if(element instanceof IWebHost)
			return webHostAdapter.getParent((IWebHost) element);
		else if(element instanceof IWebPath)
			return webPathAdapter.getParent((IWebPath) element);
		else if(element instanceof IWebResponse) 
			return webResponseAdapter.getParent((IWebResponse) element);
		else
			return null;
	}
	
	public boolean hasChildren(Object element) {
		return childCount(element) > 0;
	}
	
	private int childCount(Object element) {
		if(element instanceof IWebPath) 
			return webPathAdapter.getChildrenCount((IWebPath) element);
		else if(element instanceof IWebResponse)
			return webResponseAdapter.getChildrenCount((IWebResponse) element);
		else  if(element instanceof IWebHost)
			return webHostAdapter.getChildrenCount((IWebHost) element);
		else
			return 0;
	}
	
	public String getLabel(Object element) {
		if(element instanceof IWebPath)
			return webPathAdapter.getLabel((IWebPath) element);
		if(element instanceof IWebResponse)
			return webResponseAdapter.getLabel((IWebResponse) element);
		if(element instanceof IWebHost)
			return webHostAdapter.getLabel((IWebHost) element);
		else
			return null;
	}
}
