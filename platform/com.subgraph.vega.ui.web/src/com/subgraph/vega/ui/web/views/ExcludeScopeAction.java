package com.subgraph.vega.ui.web.views;

import java.net.URI;
import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.scope.ITargetScope;

public class ExcludeScopeAction extends AbstractScopeAction {
	private final static String EXCLUDE_TEXT = "Exclude from current scope";
	ExcludeScopeAction(StructuredViewer viewer, IModel model) {
		super(viewer, model, EXCLUDE_TEXT);
	}

	@Override
	protected void handleSelectedURI(ITargetScope scope, URI uri) {
		if(scope.isInsideScope(uri)) {
			scope.addExclusionURI(uri);
		}
	}

	@Override
	protected boolean isEnabledForURIs(ITargetScope scope, List<URI> uris) {
		for(URI u: uris) {
			if(scope.isInsideScope(u)) {
				return true;
			}
		}
		return false;
	}
}
