package com.subgraph.vega.ui.scanner.scope;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Composite;

public class BasePathWidget extends RemovableItemWidget {
	public static String[] schemePrefixes = new String[] {"http://", "https://"};
	public BasePathWidget(Composite parent, StructuredViewer scopeViewer) {
		super(parent, "Base Paths", scopeViewer);
	}

		@Override
	boolean verifyText(String input) {
		return getURIFromText(input) != null;
	}

	@Override
	boolean handleInput(String input) {
		final URI uri = getURIFromText(input);
		if(uri == null || currentScope == null) {
			return false;
		}
		currentScope.addScopeURI(uri);
		return true;
	}

	private URI getURIFromText(String text) {
		try {
			final URI uri = new URI(textToAbsoluteURL(text));
			if(uri.getHost() == null) {
				return null;
			}
			return uri;
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	private String textToAbsoluteURL(String input) {
		for(String prefix: schemePrefixes) {
			if(input.startsWith(prefix) || prefix.startsWith(input)) {
				return input;
			}
		}
		return "http://"+input;
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
