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
package com.subgraph.vega.ui.httpeditor.search;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.VerifyEvent;

import com.subgraph.vega.ui.httpeditor.Colors;

public class SearchBarManager implements VerifyKeyListener {
	private final ProjectionViewer viewer;
	private final Colors colors;
	
	private SearchBar currentSearchBar;
	private IDocument currentDocument;
	private AnnotationModel annotationModel;
	
	public SearchBarManager(ProjectionViewer viewer, Colors colors) {
		this.viewer = viewer;
		this.colors = colors;
		viewer.prependVerifyKeyListener(this);
		attachHighlightPainter(viewer);
	}
	
	public void clearDocument() {
		if(currentSearchBar != null) {
			closeSearchBar();
		}
		
	}
	
	public void setDocument(IDocument document, AnnotationModel model) {
		currentDocument = document;
		annotationModel = model;
		if(currentSearchBar != null) {
			closeSearchBar();
		}
	}

	private void displaySearchBar() {
		if(currentSearchBar != null) {
			return;
		}

		if(currentDocument != null && annotationModel != null) {
			currentSearchBar = new SearchBar(viewer, colors, currentDocument, annotationModel);
			currentSearchBar.getShell().addShellListener(new ShellAdapter() {
				@Override
				public void shellClosed(ShellEvent e) {
					currentSearchBar = null;
				}
			});
		}
	}
	
	private void closeSearchBar() {
		if(currentSearchBar != null) {
			currentSearchBar.getShell().close();
			currentSearchBar = null;
		}
	}

	private void toggleSearchBar() {
		if(currentSearchBar == null) {
			displaySearchBar();
		} else {
			closeSearchBar();
		}
	}
	
	@Override
	public void verifyKey(VerifyEvent event) {
		if((event.stateMask & SWT.CONTROL) != 0 && 
				event.keyCode == SearchBar.KEY_F &&
				currentDocument != null) {
			toggleSearchBar();
		}
	}
	
	private void attachHighlightPainter(SourceViewer viewer) {
		final AnnotationPainter painter = new AnnotationPainter(viewer, createAnnotationAccess());
		painter.addHighlightAnnotationType(SearchBarWidget.ANNOTATION_MATCH);
		painter.setAnnotationTypeColor(SearchBarWidget.ANNOTATION_MATCH, colors.get(Colors.HIGHLIGHT_SEARCH_MATCH));
		viewer.addPainter(painter);
		viewer.addTextPresentationListener(painter);
	}
	
	private IAnnotationAccess createAnnotationAccess() {
		return new IAnnotationAccess() {
			@Override
			public boolean isTemporary(Annotation annotation) {
				return true;
			}
			
			@Override
			public boolean isMultiLine(Annotation annotation) {
				return true;
			}
			
			@Override
			public Object getType(Annotation annotation) {
				return annotation.getType();
			}
		};
	}
}
