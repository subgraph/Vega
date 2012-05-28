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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.macros.IHttpMacro;
import com.subgraph.vega.api.model.macros.IHttpMacroModel;
import com.subgraph.vega.api.model.macros.NewMacroEvent;

public class MacroViewerContentProvider implements ITreeContentProvider, IEventHandler {
	private IHttpMacroModel macroModel;
	private Viewer viewer;
	private final List<IMacroTreeNode> childrenList = new ArrayList<IMacroTreeNode>();

	@Override
	public void dispose() {
		if (macroModel != null) {
			macroModel.removeChangeListener(this);
		}
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		if (newInput != oldInput) {
			if (macroModel != null) {
				macroModel.removeChangeListener(this);
				childrenList.clear();
			}

			macroModel = (IHttpMacroModel) newInput;
			if (macroModel != null) {
				macroModel.addChangeListener(this);
				for (IHttpMacro macro: macroModel.getAllMacros()) {
					childrenList.add(new MacroTreeNode(macro));
				}
			}
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return childrenList.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return ((IMacroTreeNode) parentElement).getChildren();
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return ((IMacroTreeNode) element).hasChildren();
	}

	@Override
	public void handleEvent(IEvent event) {
		if (event instanceof NewMacroEvent) {
			childrenList.add(new MacroTreeNode(((NewMacroEvent) event).getMacro()));
			viewer.refresh();
		}
	}

}
