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
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;

import com.subgraph.vega.ui.httpeditor.Colors;

class SearchBar implements ControlListener, KeyListener {
	
	private static final int KEY_ESC = 27;
	static final int KEY_F = 102;

	private final Shell searchBarShell;
	private final SearchBarWidget searchBarWidget;
	private final ProjectionViewer viewer;
	
	SearchBar(ProjectionViewer viewer, Colors colors, IDocument document, AnnotationModel annotationModel) {
		searchBarShell = createShell(viewer.getTextWidget().getShell());
		this.searchBarWidget = new SearchBarWidget(this, viewer, colors, document, annotationModel);
		this.viewer = viewer;
		viewer.getTextWidget().addControlListener(this);
		viewer.getTextWidget().getShell().addControlListener(this);
		searchBarShell.pack();
		searchBarShell.open();
		moveShell();
	}

	Shell getShell() {
		return searchBarShell;
	}
	
	private Shell createShell(Shell parentShell) {
		final Shell shell = new Shell(parentShell, SWT.NO_TRIM);
		shell.setLayout(new FillLayout());
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				handleClose();
			}
		});
		return shell;
	}
	
	private void handleClose() {
		searchBarWidget.clearAnnotation();
		viewer.getTextWidget().removeControlListener(this);
		viewer.getTextWidget().getShell().removeControlListener(this);
	}
	
	@Override
	public void controlMoved(ControlEvent e) {
		moveShell();
	}

	@Override
	public void controlResized(ControlEvent e) {
		moveShell();
	}

	private void moveShell() {
		if(searchBarShell == null) {
			return;
		}
		final Rectangle textClientArea = viewer.getTextWidget().getClientArea();
		final Rectangle mappedArea = searchBarShell.getDisplay().map(viewer.getTextWidget(), null, textClientArea);
		searchBarShell.setLocation( calculateLocation(mappedArea) );
	}
	
	private Point calculateLocation(Rectangle area) {
		final int myWidth = searchBarShell.getSize().x;
		if(area.width < myWidth) {
			return new Point(area.x, area.y);
		} else {
			return new Point(area.x + (area.width - myWidth), area.y);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		handleKeyEvent(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
	
	private void handleKeyEvent(KeyEvent event) {
		if((event.stateMask & SWT.CONTROL) != 0 && event.keyCode == KEY_F) {
			searchBarShell.close();
		} else if(event.keyCode == KEY_ESC) {
			searchBarShell.close();
		}
	}
}
