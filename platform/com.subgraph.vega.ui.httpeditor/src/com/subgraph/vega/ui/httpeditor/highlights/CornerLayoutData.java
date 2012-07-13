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

public class CornerLayoutData {
	
	public static CornerLayoutData createTopLeft() {
		return new CornerLayoutData(true, true);
	}
	
	public static CornerLayoutData createTopRight() {
		return new CornerLayoutData(true, false);
	}
	
	public static CornerLayoutData createBottomLeft() {
		return new CornerLayoutData(false, true);
	}
	
	public static CornerLayoutData createBottomRight() {
		return new CornerLayoutData(false, false);
	}
	
	private final boolean isTop;
	private final boolean isLeft;
	
	private CornerLayoutData(boolean isTop, boolean isLeft) {
		this.isTop = isTop;
		this.isLeft = isLeft;
	}
	
	public boolean isTop() {
		return isTop;
	}
	
	public boolean isLeft() {
		return isLeft;
	}
}
