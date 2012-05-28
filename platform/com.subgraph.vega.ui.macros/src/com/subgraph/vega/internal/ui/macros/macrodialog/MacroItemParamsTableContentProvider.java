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
package com.subgraph.vega.internal.ui.macros.macrodialog;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.model.macros.IHttpMacroItem;

public class MacroItemParamsTableContentProvider implements IStructuredContentProvider {
	private IHttpMacroItem macroItem;
	
	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		macroItem = (IHttpMacroItem) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return macroItem.getParams().toArray();
	}

}
