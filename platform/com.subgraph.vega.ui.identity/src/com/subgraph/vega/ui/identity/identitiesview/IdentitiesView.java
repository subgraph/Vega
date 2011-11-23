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
package com.subgraph.vega.ui.identity.identitiesview;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.identity.IIdentityModel;
import com.subgraph.vega.ui.identity.Activator;
import com.subgraph.vega.ui.identity.identityview.tree.IdentitiesViewerContentProvider;
import com.subgraph.vega.ui.identity.identityview.tree.IdentitiesViewerLabelProvider;

public class IdentitiesView extends ViewPart {
	public static final String ID = "com.subgraph.vega.views.identity.identities";
	private IIdentityModel identityModel;
	private Composite parentComposite;
	private TreeViewer identitiesViewer;

	public IdentitiesView() {
		IWorkspace currentWorkspace = Activator.getDefault().getModel().addWorkspaceListener(new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if (event instanceof WorkspaceOpenEvent) {
					handleWorkspaceOpen((WorkspaceOpenEvent) event);
				} else if (event instanceof WorkspaceCloseEvent) {
					handleWorkspaceClose((WorkspaceCloseEvent) event);
				} else if (event instanceof WorkspaceResetEvent) {
					handleWorkspaceReset((WorkspaceResetEvent) event);
				}
			}
		});
		identityModel = currentWorkspace.getIdentityModel();
	}

	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		identityModel = event.getWorkspace().getIdentityModel();
		identitiesViewer.setInput(identityModel);
	}

	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		identityModel = null;
		identitiesViewer.setInput(identityModel);
	}

	private void handleWorkspaceReset(WorkspaceResetEvent event) {
		identityModel = event.getWorkspace().getIdentityModel();
		identitiesViewer.setInput(identityModel);
	}

	@Override
	public void createPartControl(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new FillLayout());
		createTreeViewer(parentComposite);
		identitiesViewer.setInput(identityModel);
	}

	@Override
	public void setFocus() {
		parentComposite.setFocus();
	}

	private void createTreeViewer(Composite parent) {
		identitiesViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		identitiesViewer.setContentProvider(new IdentitiesViewerContentProvider());
		identitiesViewer.setLabelProvider(new IdentitiesViewerLabelProvider());
	}

}
