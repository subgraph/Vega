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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public class CornerLayout extends Layout {

	private final Composite textControl;
	
	public CornerLayout(StyledText textControl) {
		this.textControl = textControl;
	}

	@Override
	protected Point computeSize(Composite composite, int wHint, int hHint,
			boolean flushCache) {
		final Control bgControl = getBackgroundControl(composite);
		final Point sz = bgControl.computeSize(wHint, hHint, flushCache);
		final int width = getSize(sz.x, wHint);
		final int height = getSize(sz.y, hHint);
		return new Point(width, height);
	}
	
	private int getSize(int sz, int szHint) {
		return (szHint == SWT.DEFAULT) ? sz : szHint;
	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {
		final Control bgControl = getBackgroundControl(composite);
		final Rectangle area = composite.getClientArea();
		bgControl.setBounds(area);
		for(Control c: composite.getChildren()) {
			if(c != bgControl) {
				placeControl(composite, c);
			}
		}
	}

	private void placeControl(Composite composite, Control c) {
		final Object ob = c.getLayoutData();
		if(!(ob instanceof CornerLayoutData)) {
			return;
		}
		final Point pos = calculateControlPosition(composite, c, (CornerLayoutData) ob);
		c.setLocation(pos);
	}
	
	private Point calculateControlPosition(Composite composite, Control c, CornerLayoutData data) {
		final Rectangle area = textControl.getClientArea();
		final int x = getPosition(data.isLeft(), area.width, c.getSize().x);
		final int y = getPosition(data.isTop(), area.height, c.getSize().y);
		return c.getDisplay().map(textControl, composite, x, y);
	}
	
	private int getPosition(boolean flag, int dimText, int dimControl) {
		if(flag || (dimText < dimControl)) {
			return 0;
		} else {
			return dimText - dimControl;
		}
	}
	
	@Override
	protected boolean flushCache(Control control) {
		return true;
	}

	private Control getBackgroundControl(Composite composite) {
		checkZOrder(composite);
		final Control[] children = composite.getChildren();
		final int idx = findBackgroundControlIdx(children);
		return children[idx];
	}

	private void checkZOrder(Composite composite) {
		final Control[] children = composite.getChildren();
		final int backgroundIdx = findBackgroundControlIdx(children);
		if(backgroundIdx != children.length - 1) {
			children[backgroundIdx].moveBelow(null);
		}
	}
	
	private int findBackgroundControlIdx(Control[] children) {
		int backgroundIdx = -1;
		for(int i = 0; i < children.length; i++) {
			if(!(children[i].getLayoutData() instanceof CornerLayoutData)) {
				if(backgroundIdx >= 0) {
					throw new IllegalStateException("All children of composite managed by CornerLayout must have CornerLayoutData except background control");
				}
				backgroundIdx = i;
			}
		}
		if(backgroundIdx < 0) {
			throw new IllegalStateException("Could not find background control");
		}
		return backgroundIdx;
	}
}
