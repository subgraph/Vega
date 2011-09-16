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
package com.subgraph.vega.internal.ui.model.taggableeditor;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class TagTableSearchFilter extends ViewerFilter {
	private String matchFilter;

	public void setMatchFilter(String matchFilter) {
		this.matchFilter = matchFilter;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (matchFilter != null) {
			return ((TagModifier) element).getTagMod().getName().contains(matchFilter);
		}
		return true;
	}

}
