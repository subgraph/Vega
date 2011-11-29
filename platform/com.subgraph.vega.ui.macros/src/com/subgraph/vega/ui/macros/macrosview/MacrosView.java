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
package com.subgraph.vega.ui.macros.macrosview;

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
import com.subgraph.vega.api.model.macros.IHttpMacroModel;
import com.subgraph.vega.ui.macros.Activator;
import com.subgraph.vega.ui.macros.macrosview.tree.MacroViewerContentProvider;
import com.subgraph.vega.ui.macros.macrosview.tree.MacroViewerLabelProvider;

public class MacrosView extends ViewPart {
	public static final String ID = "com.subgraph.vega.views.macros.macros";
	private IHttpMacroModel macroModel;
	private Composite parentComposite;
	private TreeViewer macroViewer;

	public MacrosView() {
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
		macroModel = currentWorkspace.getHttpMacroModel();
	}

	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		macroModel = event.getWorkspace().getHttpMacroModel();
		macroViewer.setInput(macroModel);
	}

	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		macroModel = null;
		macroViewer.setInput(macroModel);
	}

	private void handleWorkspaceReset(WorkspaceResetEvent event) {
		macroModel = event.getWorkspace().getHttpMacroModel();
		macroViewer.setInput(macroModel);
	}

	@Override
	public void createPartControl(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new FillLayout());
		createTreeViewer(parentComposite);
		macroViewer.setInput(macroModel);
	}

	@Override
	public void setFocus() {
		parentComposite.setFocus();
	}

	private void createTreeViewer(Composite parent) {
		macroViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		macroViewer.setContentProvider(new MacroViewerContentProvider());
		macroViewer.setLabelProvider(new MacroViewerLabelProvider());
	}

}
