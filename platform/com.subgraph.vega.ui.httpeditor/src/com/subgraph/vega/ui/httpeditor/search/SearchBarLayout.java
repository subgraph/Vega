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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public class SearchBarLayout extends Layout {

	private final Composite textControl;
	
	public SearchBarLayout(StyledText textControl) {
		this.textControl = textControl;
	}

	@Override
	protected Point computeSize(Composite composite, int wHint, int hHint,
			boolean flushCache) {
		checkChildren(composite);
		final Point sz = getBackgroundControl(composite).computeSize(wHint, hHint, flushCache);
		int width = sz.x;
		int height = sz.y;
		if(wHint != SWT.DEFAULT) width = wHint;
		if(hHint != SWT.DEFAULT) height = hHint;
		return new Point(width, height);
	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {
		checkChildren(composite);
		final Rectangle area = composite.getClientArea();
		getBackgroundControl(composite).setBounds(area);
		final Control sb = getSearchBar(composite);
		sb.setLocation(calculateSearchBarPosition(composite, sb));
	}
	
	private Point calculateSearchBarPosition(Composite composite, Control searchBar) {
		final Rectangle area = textControl.getClientArea();
		final int widthSB = searchBar.getSize().x;
		final int x = getSearchBarX(area.width, widthSB);
		return searchBar.getDisplay().map(textControl, composite, x, 0);
	}
	
	private int getSearchBarX(int widthText, int widthSB) {
		if(widthText < widthSB) {
			return 0;
		} else {
			return widthText - widthSB;
		}
	}

	@Override
	protected boolean flushCache(Control control) {
		return true;
	}

	private Control getBackgroundControl(Composite composite) {
		return composite.getChildren()[1];
	}
	
	private Control getSearchBar(Composite composite) {
		return composite.getChildren()[0];
	}
	
	private void checkChildren(Composite composite) {
		final Control[] children = composite.getChildren();
		if(children.length != 2) {
			throw new RuntimeException();
		}
		if(children[1] instanceof SearchBar) {
			children[1].moveAbove(children[0]);
		}
	}
}
