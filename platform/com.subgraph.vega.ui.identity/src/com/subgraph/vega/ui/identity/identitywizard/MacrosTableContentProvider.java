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
package com.subgraph.vega.ui.identity.identitywizard;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.model.macros.IHttpMacroModel;

public class MacrosTableContentProvider implements IStructuredContentProvider {
	private IHttpMacroModel macroModel;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		macroModel = (IHttpMacroModel) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return macroModel.getAllMacros().toArray();
	}

}
