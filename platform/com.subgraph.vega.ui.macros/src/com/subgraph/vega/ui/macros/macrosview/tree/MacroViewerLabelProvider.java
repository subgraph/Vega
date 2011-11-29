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
package com.subgraph.vega.ui.macros.macrosview.tree;

import org.eclipse.jface.viewers.LabelProvider;

import com.subgraph.vega.ui.macros.Activator;
import com.subgraph.vega.ui.util.ImageCache;

public class MacroViewerLabelProvider extends LabelProvider {
	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);

	@Override
	public String getText(Object element) {
		return "";
	}

}
