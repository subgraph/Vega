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
package com.subgraph.vega.ui.http.intercept.config;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

public class BreakpointTypeEditingSupport extends EditingSupport {
	//private final TableViewer viewer;

	public BreakpointTypeEditingSupport(TableViewer viewer) {
		super(viewer);
		//this.viewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return null;
	}

	@Override
	protected boolean canEdit(Object element) {
		return false;
	}

	@Override
	protected Object getValue(Object element) {
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
	}

}
