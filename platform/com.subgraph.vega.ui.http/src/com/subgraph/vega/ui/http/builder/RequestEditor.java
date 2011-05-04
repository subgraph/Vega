package com.subgraph.vega.ui.http.builder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;

/**
 * Manages RequestAddressEditor and RequestMessageEditor widgets to edit a request.
 */
public class RequestEditor implements IHttpBuilderPart {
	private Composite parentComposite;
	private RequestAddressEditor requestAddressEditor;
	private RequestMessageEditor messageEditor;

	public RequestEditor(final IHttpRequestBuilder requestBuilder) {
		requestAddressEditor = new RequestAddressEditor(requestBuilder);
		messageEditor = new RequestMessageEditor(requestBuilder);
	}
	
	@Override
	public Composite createPartControl(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, false));

		requestAddressEditor.createPartControl(parentComposite).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		messageEditor.createPartControl(parentComposite).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		return parentComposite;
	}

	@Override
	public Control getControl() {
		return parentComposite;
	}
	
	@Override
	public void refresh() {
		requestAddressEditor.refresh();
		messageEditor.refresh();
	}

	@Override
	public void processContents() {
		requestAddressEditor.processContents();
		messageEditor.processContents();
	}

}
