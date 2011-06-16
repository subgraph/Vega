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
package com.subgraph.vega.ui.hexeditor;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

public class HexEditContentProvider implements ILazyContentProvider {

	private final TableViewer tableViewer;
	private HexEditModel model;
	
	HexEditContentProvider(TableViewer viewer) {
		this.tableViewer = viewer;
	}
	
	@Override
	public void updateElement(int index) {
		if(model == null)
			return;
		final HexEditModelItem item = model.getItemForLine(index);
		if(item != null)
			tableViewer.replace(item, index);		
	}

	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(newInput instanceof HexEditModel) {
			model = (HexEditModel) newInput;
			tableViewer.setItemCount(model.getLineCount());
		} else {
			model = null;
		}
	}
}
