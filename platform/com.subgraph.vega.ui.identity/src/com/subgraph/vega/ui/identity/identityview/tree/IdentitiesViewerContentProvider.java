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
package com.subgraph.vega.ui.identity.identityview.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.identity.IIdentity;
import com.subgraph.vega.api.model.identity.IIdentityModel;
import com.subgraph.vega.api.model.identity.NewIdentityEvent;

public class IdentitiesViewerContentProvider implements ITreeContentProvider, IEventHandler {
	private IIdentityModel identityModel;
	private Viewer viewer;
	private List<IIdentityTreeNode> childrenList = new ArrayList<IIdentityTreeNode>();

	@Override
	public void dispose() {
		if (identityModel != null) {
			identityModel.removeChangeListener(this);
		}
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		if (identityModel != newInput) {
			if (identityModel != null) {
				identityModel.removeChangeListener(this);
				childrenList.clear();
			}

			identityModel = (IIdentityModel) newInput;
			childrenList.clear();
			if (identityModel != null) {
				identityModel.addChangeListener(this);
				for (IIdentity identity: identityModel.getAllIdentities()) {
					childrenList.add(new IdentityTreeNode(identity));
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
		return ((IIdentityTreeNode) parentElement).getChildren();
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return ((IIdentityTreeNode) element).hasChildren();
	}

	@Override
	public void handleEvent(IEvent event) {
		if (event instanceof NewIdentityEvent) {
			childrenList.add(new IdentityTreeNode(((NewIdentityEvent) event).getIdentity()));
			viewer.refresh();
		}
	}
	
}
