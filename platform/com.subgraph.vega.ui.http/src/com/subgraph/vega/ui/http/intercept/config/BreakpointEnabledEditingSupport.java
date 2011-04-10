package com.subgraph.vega.ui.http.intercept.config;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import com.subgraph.vega.api.model.conditions.IHttpCondition;

public class BreakpointEnabledEditingSupport extends EditingSupport {
	private final TableViewer viewer;

	public BreakpointEnabledEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new CheckboxCellEditor(null, SWT.CHECK);// | SWT.READ_ONLY);
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		return ((IHttpCondition)element).isEnabled();
	}

	@Override
	protected void setValue(Object element, Object value) {
		((IHttpCondition) element).setEnabled((Boolean) value);
		viewer.refresh();
	}

}
