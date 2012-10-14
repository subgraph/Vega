package com.subgraph.vega.ui.scanner.scope;

import java.net.URI;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Composite;

import com.subgraph.vega.api.util.UriTools;

public class BasePathWidget extends RemovableItemWidget {

	public BasePathWidget(Composite parent, StructuredViewer scopeViewer) {
		super(parent, "Base Paths", scopeViewer);
	}

		@Override
	boolean verifyText(String input) {
		return UriTools.isTextValidURI(input);
	}

	@Override
	boolean handleInput(String input) {
		final URI uri = UriTools.getURIFromText(input);
		if(uri == null || currentScope == null) {
			return false;
		}
		currentScope.addScopeURI(uri);
		return true;
	}

	@Override
	boolean handleRemoveElement(Object element) {
		if(currentScope == null || !(element instanceof URI)) {
			return false;
		}
		currentScope.removeScopeURI((URI) element, false);
		return true;
	}

	@Override
	Object[] getTableInput() {
		if(currentScope == null) {
			return new Object[0];
		} else {
			return currentScope.getScopeURIs().toArray();
		}
	}
}
