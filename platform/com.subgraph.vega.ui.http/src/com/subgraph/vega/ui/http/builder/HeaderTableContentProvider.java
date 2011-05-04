package com.subgraph.vega.ui.http.builder;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;

public class HeaderTableContentProvider implements IStructuredContentProvider {
	private IHttpRequestBuilder request;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		request = (IHttpRequestBuilder) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return request.getHeaders();
	}

}
