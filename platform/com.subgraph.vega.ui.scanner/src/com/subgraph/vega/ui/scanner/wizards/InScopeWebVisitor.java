package com.subgraph.vega.ui.scanner.wizards;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebModelVisitor;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.model.web.IWebResponse;

public class InScopeWebVisitor implements IWebModelVisitor {
	
	private final ITargetScope scope;
	private final Set<URI> scopeURIs;
	
	InScopeWebVisitor(ITargetScope scope) {
		this.scope = scope;
		this.scopeURIs = new TreeSet<URI>();
	}

	public Collection<URI> getScopeURIs() {
		return Collections.unmodifiableCollection(new ArrayList<URI>(scopeURIs));
	}

	@Override
	public void visit(IWebHost host) {
		final URI u = host.getUri();
		if(scope.filter(u)) {
			scopeURIs.add(u);
		}
	}

	@Override
	public void visit(IWebPath path) {
		final URI u = path.getUri();
		if(scope.filter(u)) {
			scopeURIs.add(u);
		}
	}

	@Override
	public void visit(IWebResponse response) {
	}

}
