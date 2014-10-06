package com.subgraph.vega.ui.scanner.scope;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Composite;

public class ExcludeWidget extends RemovableItemWidget {

	public ExcludeWidget(Composite parent, StructuredViewer scopeViewer) {
		super(parent, "Exclude (URL or pattern)", scopeViewer);
	}

	@Override
	Object[] getTableInput() {
		if(currentScope == null) {
			return new Object[0];
		} else {
			final List<Object> exclusions = new ArrayList<Object>(currentScope.getExclusionURIs());
			exclusions.addAll(currentScope.getExclusionPatterns());
			return exclusions.toArray();
		}
	}

	@Override
	boolean verifyText(String input) {
		try {
			Pattern.compile(input);
			return true;
		} catch (PatternSyntaxException e) {
			return false;
		}
	}

	@Override
	boolean handleInput(String input) {
		if(currentScope == null) {
			return false;
		}
		currentScope.addExclusionPattern(input);
		return true;
	}

	@Override
	boolean handleRemoveElement(Object element) {
		if(currentScope == null) {
			return false;
		} else if(element instanceof String) {
			currentScope.removeExclusionPattern((String) element);
			return true;
		} else if(element instanceof URI) {
			currentScope.removeExclusionURI((URI) element);
			return true;
		} else {
			return false;
		}
	}



}
