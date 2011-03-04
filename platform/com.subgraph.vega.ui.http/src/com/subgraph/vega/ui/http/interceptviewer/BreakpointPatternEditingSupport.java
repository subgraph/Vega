package com.subgraph.vega.ui.http.interceptviewer;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.subgraph.vega.api.http.proxy.IHttpInterceptorBreakpoint;

public class BreakpointPatternEditingSupport extends EditingSupport {
	private final TableViewer viewer;

	public BreakpointPatternEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
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
		return ((IHttpInterceptorBreakpoint) element).getPattern();
	}

	@Override
	protected void setValue(Object element, Object value) {
		((IHttpInterceptorBreakpoint) element).setPattern((String) value);
		viewer.refresh();
	}
}
