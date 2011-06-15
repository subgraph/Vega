/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.ui.httpviewer;


import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.http.requests.IHttpResponseBuilder;
import com.subgraph.vega.ui.httpviewer.entity.HttpEntityViewer;

public class HttpMessageViewer extends Composite {
	private final static String COLLAPSED_ICON = "icons/collapsed.png";
	private final static String EXPANDED_ICON = "icons/expanded.png";
	private final Colors colors;
	private final ImageCache imageCache = new ImageCache("com.subgraph.vega.ui.httpeditor");
	private final Composite rootComposite;
	private final Label collapseButton;
	private final SourceViewer viewer;
	private final HttpMessageDocumentFactory documentFactory;
	private final HttpEntityViewer entityViewer;
	private final EmbeddedControlPainter painter;

	private IDocument rawDocument;
	private boolean isRawDocumentDirty;
	private IDocument decodedDocument;
	private boolean isDecodedDocumentDirty;
	private boolean isDecodingEnabled;
	private boolean isCollapsed;
	private boolean isEmpty;
	private boolean isEditable;

	public HttpMessageViewer(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		rootComposite = createRootComposite();
		collapseButton = createCollapseHeaders(rootComposite);

		viewer = createSourceViewer(rootComposite);
		viewer.getTextWidget().setWrapIndent(20);
		entityViewer = new HttpEntityViewer(viewer.getTextWidget());
		painter = new EmbeddedControlPainter(viewer, entityViewer, 200);
		viewer.addPainter(painter);
		colors = new Colors(getDisplay());
		viewer.configure(new Configuration(colors));
		documentFactory = new HttpMessageDocumentFactory();
		layout(true);
		rootComposite.layout(true, true);
	}

	private Composite createRootComposite() {
		final Composite c = new Composite(this, SWT.NONE);
		final GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		c.setLayout(layout);
		c.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		return c;
	}

	private Label createCollapseHeaders(Composite parent) {
		final Label button = new Label(parent, SWT.NONE);
		final GridData gd = new GridData(SWT.FILL, SWT.TOP, false, false);
		gd.widthHint = 16;
		gd.heightHint = 16;
		button.setLayoutData(gd);
		button.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				toggleCollapseState();
			}
		});
		return button;
	}

	private void toggleCollapseState() {
		if(isEmpty) {
			return;
		}
		
		if(isCollapsed) {
			expandHeaders();
		} else {
			collapseHeaders();
		}
	}

	public void expandHeaders() {
		if(!isCollapsed || isEmpty) {
			return;
		}
		isCollapsed = false;
		collapseButton.setImage(imageCache.get(EXPANDED_ICON));
		viewer.setEditable(isEditable);
		displayDocumentForDecodeState();
	}
	
	public void collapseHeaders() {
		if(isCollapsed || isEmpty) {
			return;
		}
		isCollapsed = true;
		collapseButton.setImage(imageCache.get(COLLAPSED_ICON));
		viewer.setEditable(false);
		displayDocumentForDecodeState();
	}

	public void dispose() {
		colors.dispose();
		super.dispose();
	}

	public void setEditable(boolean flag) {
		isEditable = flag;
		viewer.setEditable(flag);
	}

	public void clearContent() {
		if (rawDocument != null) {
			rawDocument.set("");
			isRawDocumentDirty = false;
		}
		if (decodedDocument != null) {
			decodedDocument.set("");
			isDecodedDocumentDirty = false;
			
		}
		isDecodedDocumentDirty = false;
		isEmpty = true;
		entityViewer.clearContent();
		collapseButton.setImage(null);
		viewer.refresh();
	}

	public String getContent() {
		if(isDecodingEnabled) {
			return EmbeddedControlPainter.getDocumentContent(decodedDocument);
		} else {
			return EmbeddedControlPainter.getDocumentContent(rawDocument);
		}
	}

	public boolean isHeaderContentDirty() {
		if(isDecodingEnabled) {
			return isDecodedDocumentDirty;
		} else {
			return isRawDocumentDirty;
		}
	}

	public boolean isEntityContentDirty() {
		return entityViewer.isEntityContentDirty();
	}

	public HttpEntity getEntityContent() {
		return entityViewer.getEntityContent();
	}

	public void setDecodeUrlEncoding(boolean flag) {
		if(flag == isDecodingEnabled) {
			return;
		}
	
		isDecodingEnabled = flag;
		displayDocumentForDecodeState();
	}

	private void displayDocumentForDecodeState() {
		if(isCollapsed) {
			viewer.setDocument(createCollapsedDocument());
		} else if(isDecodingEnabled) {
			viewer.setDocument(decodedDocument);
		} else {
			viewer.setDocument(rawDocument);
		}
		viewer.refresh();
	}

	private IDocument createCollapsedDocument() {
		if(isDecodingEnabled) {
			return createCollapsedDocument(decodedDocument);
		} else {
			return createCollapsedDocument(rawDocument);
		}
	}

	private IDocument createCollapsedDocument(IDocument current) {
		final String content = current.get();
		final String[] lines = content.split("\n");
		if(lines.length == 0) {
			return documentFactory.createDocumentForText("");
		} else {
			return documentFactory.createDocumentForText(lines[0] + " ...");
		}
	}

	private boolean isDocumentEmpty() {
		return rawDocument.get().trim().isEmpty();
	}

	private void displayNewDocument() {
		isEmpty = isDocumentEmpty();
		viewer.setEditable(isEditable);
		if(isEmpty) {
			collapseButton.setImage(null);
		} else if (isCollapsed) {
			collapseButton.setImage(imageCache.get(COLLAPSED_ICON));
		} else {
			collapseButton.setImage(imageCache.get(EXPANDED_ICON));
		}
		
		displayDocumentForDecodeState();

		isRawDocumentDirty = false;
		isDecodedDocumentDirty = false;

		rawDocument.addDocumentListener(new IDocumentListener() {			
			@Override
			public void documentChanged(DocumentEvent event) {
			}
			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
				isRawDocumentDirty = true;
			}
		});

		decodedDocument.addDocumentListener(new IDocumentListener() {
			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
			}
			@Override
			public void documentChanged(DocumentEvent event) {
				isDecodedDocumentDirty = true;
			}			
		});	
	}

	public void setDisplayImages(boolean flag) {
		entityViewer.setDisplayImages(flag);
	}

	public void setDisplayImagesAsHex(boolean flag) {
		entityViewer.setDisplayImagesAsHex(flag);
	}

	public void displayHttpRequest(HttpRequest request) {
		rawDocument = documentFactory.createDocumentForRequest(request, false);
		decodedDocument = documentFactory.createDocumentForRequest(request, true);
		displayNewDocument();
		entityViewer.displayHttpEntity(maybeGetRequestEntity(request));
	}

	public void displayHttpRequest(IHttpRequestBuilder builder) {
		rawDocument = documentFactory.createDocumentForRequest(builder, false);
		decodedDocument = documentFactory.createDocumentForRequest(builder, true);
		displayNewDocument();
		entityViewer.displayHttpEntity(builder.getEntity());
	}

	private HttpEntity maybeGetRequestEntity(HttpRequest request) {
		if(request instanceof HttpEntityEnclosingRequest) {
			return ((HttpEntityEnclosingRequest) request).getEntity();
		} else {
			return null;
		}
	}

	public void displayHttpResponse(HttpResponse response) {
		rawDocument = documentFactory.createDocumentForResponse(response, false);
		decodedDocument = documentFactory.createDocumentForResponse(response, true);
		displayNewDocument();
		entityViewer.displayHttpEntity(response.getEntity());
	}

	public void displayHttpResponse(IHttpResponseBuilder builder) {
		rawDocument = documentFactory.createDocumentForResponse(builder, false);
		decodedDocument = documentFactory.createDocumentForResponse(builder, true);
		displayNewDocument();
		entityViewer.displayHttpEntity(builder.getEntity());
	}

	private SourceViewer createSourceViewer(Composite parent) {
		final SourceViewer sv = new SourceViewer(parent, null, SWT.MULTI | SWT.WRAP);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		sv.getControl().setLayoutData(gd);
		return sv;
	}	
}
