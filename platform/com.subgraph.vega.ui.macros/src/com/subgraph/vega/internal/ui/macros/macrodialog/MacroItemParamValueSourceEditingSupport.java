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
package com.subgraph.vega.internal.ui.macros.macrodialog;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import com.subgraph.vega.api.model.macros.IHttpMacroItemParam;

public class MacroItemParamValueSourceEditingSupport extends EditingSupport {
	private final TableViewer viewer;
	
	public MacroItemParamValueSourceEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		ComboBoxViewerCellEditor cellEditor = new ComboBoxViewerCellEditor(viewer.getTable(), SWT.READ_ONLY);
		cellEditor.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((IHttpMacroItemParam.ValueSource) element).getDescription();
			}
		});
		cellEditor.setContentProvider(new ArrayContentProvider());
		cellEditor.setInput(IHttpMacroItemParam.ValueSource.values());
		return cellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		if (element instanceof IHttpMacroItemParam) {
			return ((IHttpMacroItemParam) element).getValueSource().getDescription();
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element instanceof IHttpMacroItemParam && value instanceof IHttpMacroItemParam.ValueSource) {
			final IHttpMacroItemParam macroItemParam = (IHttpMacroItemParam) element;
			final IHttpMacroItemParam.ValueSource newValue = (IHttpMacroItemParam.ValueSource) value;
			if (macroItemParam.getValueSource().getDescription().equals(newValue.getDescription())) {
				return;
			}
			macroItemParam.setValueSource(newValue);
			viewer.refresh(true);
		}
	}

}
