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

import com.subgraph.vega.api.model.web.IWebResponse;
import com.subgraph.vega.ui.tree.ITreeAdapter;

public class WebResponseTreeStrategy implements ITreeAdapter<IWebResponse>{

	@Override
	public Object[] getChildren(IWebResponse item) {
		return new Object[0];
	}

	@Override
	public int getChildrenCount(IWebResponse item) {
		return 0;
	}

	@Override
	public Object getParent(IWebResponse item) {
		return item.getParent();
	}

	@Override
	public String getLabel(IWebResponse item) {
		return getPathLabel(item) + getQueryLabel(item);
	}

	private String getPathLabel(IWebResponse item) {
		String p = item.getPathEntity().getPathComponent();
		if(p == null || p.isEmpty())
			return "/";
		else
			return p;
	}
	private String getQueryLabel(IWebResponse item) {
		if(item.getQueryString().isEmpty())
			return "";
		else
			return " ["+ item.getQueryString() +"]";
	}
}
