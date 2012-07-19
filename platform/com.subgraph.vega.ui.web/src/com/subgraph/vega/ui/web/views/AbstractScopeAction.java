package com.subgraph.vega.ui.web.views;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.model.web.IWebResponse;

public abstract class AbstractScopeAction extends Action {
	protected final IModel model;
	protected final StructuredViewer viewer;
	
	AbstractScopeAction(StructuredViewer viewer, IModel model, String text) {
		this.model = model;
		this.viewer = viewer;
		setText(text);
	}
	
	
	abstract protected void handleSelectedURI(ITargetScope scope, URI uri);
	abstract protected boolean isEnabledForURIs(ITargetScope scope, List<URI> uris);
	
	@Override
	public void run() {
		final ITargetScope scope = getCurrentActiveScope();
		if(scope == null) {
			return;
		}
		final IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
		for(Object ob: ss.toList()) {
			URI uri = elementToURI(ob);
			if(uri != null) {
				handleSelectedURI(scope, uri);
			}
		}
	}
	
	public boolean isEnabledForSelection() {
		final ITargetScope scope = getCurrentActiveScope();
		if(scope == null) {
			return false;
		}
		
		final IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
		final List<URI> uris = new ArrayList<URI>();
		for(Object ob : ss.toList()) {
			URI uri = elementToURI(ob);
			if(uri != null) {
				uris.add(uri);
			}
		}
		if(uris.isEmpty()) {
			return false;
		} else {
			return isEnabledForURIs(scope, uris);
		}
	}
	
	private ITargetScope getCurrentActiveScope() {
		final IWorkspace ws = model.getCurrentWorkspace();
		if(ws == null) {
			return null;
		}
		return ws.getTargetScopeManager().getActiveScope();
	}
	
	private URI elementToURI(Object element) {
		if(element instanceof IWebHost) {
			return ((IWebHost)element).getUri();
		} else if (element instanceof IWebPath) {
			return ((IWebPath)element).getUri();
		} else if (element instanceof IWebResponse) {
			return ((IWebResponse)element).getPathEntity().getUri();
		} else {
			return null;
		}
	}
}
