package com.subgraph.vega.ui.web.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.model.web.IWebResponse;

public class ScopeFilter extends ViewerFilter {

	private final IModel model;
	
	ScopeFilter(IModel model) {
		this.model = model;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		final ITargetScope activeScope = getActiveScope();
		if(activeScope == null) {
			return true;
		} else if(element instanceof IWebEntity) {
			return isFiltered(activeScope, ((IWebEntity)element));
		} else {
			return true;
		}
	}
	
	
	private boolean isFiltered(ITargetScope scope, IWebEntity entity) {
		if(entity instanceof IWebHost) {
			return isHostFiltered(scope, ((IWebHost)entity));
		} else if(entity instanceof IWebPath) {
			return isPathFiltered(scope, ((IWebPath)entity));
		} else if(entity instanceof IWebResponse) {
			return isPathFiltered(scope, ((IWebResponse)entity).getPathEntity());
		} else {
			return true;
		}
	}

	private boolean isHostFiltered(ITargetScope scope, IWebHost host) {
		if(scope.filter(host.getUri())) {
			return true;
		}
		return isPathFiltered(scope, host.getRootPath());
	}

	private boolean isPathFiltered(ITargetScope scope, IWebPath path) {
		if(scope.filter(path.getUri())) {
			return true;
		}
		for(IWebPath child: path.getChildPaths()) {
			if(isPathFiltered(scope, child)) {
				return true;
			}
		}
		return false;
	}
	
	private ITargetScope getActiveScope() {
		final IWorkspace ws = model.getCurrentWorkspace();
		if(ws == null) {
			return null;
		}
		return ws.getTargetScopeManager().getActiveScope();
	}
	

}
