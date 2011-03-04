package com.subgraph.vega.ui.http.interceptviewer;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import com.subgraph.vega.api.http.proxy.HttpInterceptorBreakpointMatchType;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorBreakpoint;

public class BreakpointMatchTypeEditingSupport extends EditingSupport {
	private final TableViewer viewer;

	public BreakpointMatchTypeEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		ComboBoxViewerCellEditor editor = new ComboBoxViewerCellEditor(viewer.getTable(), SWT.READ_ONLY);
		editor.setContenProvider(new ArrayContentProvider());
		editor.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((HttpInterceptorBreakpointMatchType) element).getName();
			}
		});
		editor.setInput(HttpInterceptorBreakpointMatchType.values());
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		return ((IHttpInterceptorBreakpoint) element).getMatchType();

	}

	@Override
	protected void setValue(Object element, Object value) {
		((IHttpInterceptorBreakpoint) element).setMatchType((HttpInterceptorBreakpointMatchType) value);
		viewer.refresh();
	}

}
