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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.macros.IHttpMacroModel;

public class MacroViewerContentProvider implements ITreeContentProvider, IEventHandler {
	private IHttpMacroModel macroModel;
	private Viewer viewer;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		if (newInput != oldInput) {
			if (macroModel != null) {
				macroModel.removeChangeListener(this);
			}

			macroModel = (IHttpMacroModel) newInput;
			if (macroModel != null) {
				macroModel.addChangeListener(this);
			}
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return new Object[0];
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}

	@Override
	public void handleEvent(IEvent event) {
	}

}
