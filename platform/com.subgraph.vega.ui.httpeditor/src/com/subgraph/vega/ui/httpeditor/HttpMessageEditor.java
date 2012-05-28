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
package com.subgraph.vega.ui.httpeditor;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.http.requests.IHttpResponseBuilder;

public class HttpMessageEditor extends Composite {
	
	
	private final Colors colors;
	private final Composite rootComposite;
	private final SashForm sashForm;
	private final ProjectionViewer viewer;
	private final AnnotationModel viewerAnnotationModel;
	private final ProjectionSupport projectionSupport;
	private final HttpMessageDocumentFactory messageDocumentFactory;

	private final BinaryEntityManager bem;
	private HttpMessageDocument activeDocument;
	private boolean urlDecode;
	
	public HttpMessageEditor(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		sashForm = new SashForm(this, SWT.VERTICAL);
		
		rootComposite = createRootComposite(sashForm);
		colors = new Colors(getDisplay());
		viewerAnnotationModel = new AnnotationModel();
		viewer = createSourceViewer(rootComposite, viewerAnnotationModel);
		viewer.getTextWidget().setWrapIndent(20);
		viewer.configure(new Configuration(colors));
		projectionSupport = new ProjectionSupport(viewer,new ProjectionAnnotationAccess(), colors);
		projectionSupport.install();
		messageDocumentFactory = new HttpMessageDocumentFactory();
		layout(true);
		rootComposite.layout(true, true);
				
		bem = new BinaryEntityManager(viewer, sashForm, rootComposite);

	}
	
	private Composite createRootComposite(Composite parent) {
		final Composite c = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		c.setLayout(layout);
		c.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		return c;
	}

	
	private ProjectionViewer createSourceViewer(Composite parent, AnnotationModel annotationModel) {
		final CompositeRuler ruler = new CompositeRuler();
		final ProjectionViewer sv = new ProjectionViewer(parent, ruler, null, false, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		sv.getControl().setLayoutData(gd);
		return sv;
	}

	public void dispose() {
		colors.dispose();
		bem.dispose();
		super.dispose();
	}
	
	public void clearContent() {
		
		bem.clear();
		activeDocument = null;
		viewer.unconfigure();
		viewer.configure(new Configuration(colors));
	}
	
	public String getContent() {
		if(activeDocument == null)
			return "";
		else 
			return activeDocument.getHeaderSectionText();
	}
	
	public HttpEntity getEntityContent() {
		
		if(activeDocument == null || activeDocument.getMessageEntity().isEmptyEntity())
			return null;
		
		if(!isEntityContentDirty()) {
			return activeDocument.getMessageEntity().getEntity();
		}
		
		if(activeDocument.getMessageEntity().isAsciiEntity()) {
			return getDirtyAsciiEntityContent();
		}
		
		return getDirtyBinaryEntityContent();
	}
	
	private HttpEntity getDirtyAsciiEntityContent() {
		try {
			final StringEntity entity = new StringEntity(activeDocument.getBodySectionText());
			entity.setContentType(activeDocument.getMessageEntity().getContentType());
			entity.setContentEncoding(activeDocument.getMessageEntity().getContentEncoding());
			return entity;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unsupported encoding creating new ascii entity", e);
		}
	}

	private HttpEntity getDirtyBinaryEntityContent() {
		final ByteArrayEntity entity = new ByteArrayEntity(bem.getContent());
		entity.setContentType(activeDocument.getMessageEntity().getContentType());
		entity.setContentEncoding(activeDocument.getMessageEntity().getContentType());
		return entity;
	}

	public void setDecodeUrlEncoding(boolean flag) {
		urlDecode = flag;
		if(activeDocument != null) {
			activeDocument.setHeaderDecodeState(flag);
		}
	}
	
	
	public void setEditable(boolean flag) {
		viewer.setEditable(flag);
	}
	
	public void setDisplayImages(boolean flag) {
		bem.displayImages(flag);
	}
	
	public void setDisplayImagesAsHex(boolean flag) {
		bem.displayImagesAsHex(flag);
	}
	
	public void displayHttpRequest(HttpRequest request) {
		clearContent();
		
		activeDocument = messageDocumentFactory.createForRequest(request);
		
		displayNewDocument();
	}
	
	public void displayHttpRequest(IHttpRequestBuilder builder) {
		clearContent();
		activeDocument = messageDocumentFactory.createForRequest(builder);
		
		displayNewDocument();
	}
	
	public void displayHttpResponse(HttpResponse response) {
		clearContent();
		activeDocument = messageDocumentFactory.createForResponse(response);
		
		displayNewDocument();
		
	}
	
	public void displayHttpResponse(IHttpResponseBuilder builder) {
		clearContent();
		activeDocument = messageDocumentFactory.createForResponse(builder);
		
		displayNewDocument();
	}
	
	public boolean isEntityContentDirty() {
		if(activeDocument == null || activeDocument.getMessageEntity().isEmptyEntity()) {
			return false;
		} else if(activeDocument.getMessageEntity().isAsciiEntity()) {
			return activeDocument.isBodyEntityDirty();
		} else {
			return bem.isContentDirty();
		}
	}
	
	private void displayNewDocument()  {
		if(activeDocument == null) {
			return;
		}
		viewer.disableProjection();
		viewer.setDocument(activeDocument.getDocument(), new AnnotationModel());
		viewer.enableProjection();
		activeDocument.addProjectionAnnotations(viewer.getProjectionAnnotationModel());
		bem.displayNewDocument(activeDocument);
		activeDocument.setHeaderDecodeState(urlDecode);
	}
	
}
