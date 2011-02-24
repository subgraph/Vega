package com.subgraph.vega.ui.http.requesteditviewer;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;

public class HeaderValueEditingSupport extends EditingSupport {
	private final TableViewer viewer;
	
	public HeaderValueEditingSupport(TableViewer viewer) {
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
		return ((IHttpHeaderBuilder) element).getValue();
	}

	@Override
	protected void setValue(Object element, Object value) {
		((IHttpHeaderBuilder) element).setValue((String) value);
		viewer.refresh();
	}

}
