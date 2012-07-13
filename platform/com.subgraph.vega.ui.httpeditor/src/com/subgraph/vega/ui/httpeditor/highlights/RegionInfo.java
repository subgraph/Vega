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
package com.subgraph.vega.ui.httpeditor.highlights;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionViewer;

public class RegionInfo {
	private final IRegion region;
	private final Position position;
	private final Annotation annotation;
	
	private boolean isAnnotationDisplayed;
	
	RegionInfo(IRegion region, String annotationType) {
		this.region = region;
		this.position = new Position(region.getOffset(), region.getLength());
		this.annotation = new Annotation(annotationType, false, "");
	}
	
	IRegion getRegion() {
		return region;
	}
	
	void removeHighlight(SourceViewer viewer) {
		if(!isAnnotationDisplayed) {
			return;
		}
		final IAnnotationModel model = viewer.getAnnotationModel();
		if(model != null) {
			model.removeAnnotation(annotation);
			isAnnotationDisplayed = false;
		}
	}
	
	void displayHighlight(SourceViewer viewer) {
		if(isAnnotationDisplayed) {
			return;
		}
		final IAnnotationModel model = viewer.getAnnotationModel();
		if(model != null) {
			if(viewer instanceof ProjectionViewer) {
				((ProjectionViewer)viewer).getProjectionAnnotationModel().expandAll(region.getOffset(), region.getLength());
			}
			model.addAnnotation(annotation, position);
			isAnnotationDisplayed = true;
		}		
	}
}
