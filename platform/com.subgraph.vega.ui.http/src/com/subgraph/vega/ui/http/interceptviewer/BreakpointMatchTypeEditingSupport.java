package com.subgraph.vega.ui.http.interceptviewer;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import com.subgraph.vega.api.http.conditions.IHttpBooleanCondition;
import com.subgraph.vega.api.http.conditions.MatchType;

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
				return ((MatchType) element).getName();
			}
		});
		editor.setInput(MatchType.values());
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		return ((IHttpBooleanCondition) element).getComparisonType();

	}

	@Override
	protected void setValue(Object element, Object value) {
		((IHttpBooleanCondition) element).setComparisonType((MatchType) value);
		viewer.refresh();
	}

}
