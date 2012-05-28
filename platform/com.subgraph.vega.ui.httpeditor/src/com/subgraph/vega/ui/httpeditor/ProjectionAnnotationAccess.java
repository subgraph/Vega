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

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationAccessExtension;
import org.eclipse.jface.text.source.IAnnotationPresentation;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

public class ProjectionAnnotationAccess implements IAnnotationAccess, IAnnotationAccessExtension {

	@Override
	public String getTypeLabel(Annotation annotation) {
		return null;
	}

	@Override
	public int getLayer(Annotation annotation) {
		return IAnnotationPresentation.DEFAULT_LAYER; 
	}

	@Override
	public void paint(Annotation annotation, GC gc, Canvas canvas,
			Rectangle bounds) {
		if(annotation instanceof IAnnotationPresentation) {
			((IAnnotationPresentation)annotation).paint(gc, canvas, bounds);
		}
	}

	@Override
	public boolean isPaintable(Annotation annotation) {
		return (annotation instanceof IAnnotationPresentation);
	}

	@Override
	public boolean isSubtype(Object annotationType, Object potentialSupertype) {
		if(annotationType == null || potentialSupertype == null) {
			return false;
		}
		return annotationType.toString().equals(potentialSupertype.toString());
	}

	@Override
	public Object[] getSupertypes(Object annotationType) {
		return new Object[0];
	}

	@Override
	public Object getType(Annotation annotation) {
		return annotation.getType();
	}

	@Override
	public boolean isMultiLine(Annotation annotation) {
		return true;
	}

	@Override
	public boolean isTemporary(Annotation annotation) {
		return !annotation.isPersistent();
	}
}
