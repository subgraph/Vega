package com.subgraph.vega.ui.http.builder;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.http.requests.IHttpMessageBuilder;

public class HeaderTableContentProvider implements IStructuredContentProvider {
	private IHttpMessageBuilder builder;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		builder = (IHttpMessageBuilder) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return builder.getHeaders();
	}

}
