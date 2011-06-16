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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

public class HexEditTableEditor extends EditingSupport {

	private final TableViewer viewer;
	private final int index;
	
	HexEditTableEditor(TableViewer viewer, int index) {
		super(viewer);
		this.viewer = viewer;
		this.index = index;
	}
	
	@Override
	protected CellEditor getCellEditor(Object element) {
		return new TextCellEditor(viewer.getTable());
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		if(element instanceof HexEditModelItem) {
			HexEditModelItem item = (HexEditModelItem) element;
			int value = item.getByteAt(index);
			return String.format("%02X", value);
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if(!(element instanceof HexEditModelItem))
			return;
		final HexEditModelItem item = (HexEditModelItem) element;
		try {
			int n = Integer.parseInt(String.valueOf(value), 16);
			if(n >= 0 && n <= 0xFF) {
				item.setByteAt(index, n);
				viewer.refresh(element, true);
			}
		} catch (NumberFormatException e) {
			
		}
	}
}
