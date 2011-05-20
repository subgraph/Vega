package com.subgraph.vega.ui.http.builder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;

/**
 * Manages visual components to edit a HTTP request line.
 */
public class RequestLineEditor extends Composite implements IHttpBuilderPart {
	private IHttpRequestBuilder requestBuilder;
	private Text requestLine;

	public RequestLineEditor(Composite parent, final IHttpRequestBuilder requestBuilder) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(2, false));
		this.requestBuilder = requestBuilder;

		final Label label = new Label(this, SWT.NONE);
		label.setText("Request:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		requestLine = new Text(this, SWT.BORDER | SWT.SINGLE);
		requestLine.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		refresh();
	}
	
	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public void setEditable(boolean editable) {
		requestLine.setEditable(editable);
	}

	@Override
	public void refresh() {
		requestLine.setText(requestBuilder.getRequestLine());
	}

	@Override
	public void processContents() {
		String[] requestLineWords = requestLine.getText().split(" +");
		if (requestLineWords.length > 0) {
			requestBuilder.setMethod(requestLineWords[0]);
			if (requestLineWords.length > 1) {
				requestBuilder.setPath(requestLineWords[1]);
				if (requestLineWords.length > 2) {
					// REVISIT parse protocol version
				}
			} else {
				requestBuilder.setPath("/");
				// REVISIT: http/0.9?
			}
		} else {
			throw new IllegalArgumentException("Invalid request line");
		}
		requestBuilder.setRawRequestLine(requestLine.getText());
	}

}
