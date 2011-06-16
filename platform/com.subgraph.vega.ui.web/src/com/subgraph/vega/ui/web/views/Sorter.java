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
