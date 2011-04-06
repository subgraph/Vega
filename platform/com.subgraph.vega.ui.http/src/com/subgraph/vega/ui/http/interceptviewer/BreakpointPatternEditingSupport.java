package com.subgraph.vega.ui.http.interceptviewer;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.subgraph.vega.api.model.conditions.IHttpRangeCondition;
import com.subgraph.vega.api.model.conditions.IHttpRegexCondition;

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
		if(element instanceof IHttpRegexCondition)
			return ((IHttpRegexCondition) element).getPattern();
		else if(element instanceof IHttpRangeCondition) {
			IHttpRangeCondition range = (IHttpRangeCondition) element;
			return range.getRangeLow() + "-" + range.getRangeHigh();
		} else {
			return null;
		}
	}

	@Override
	protected void setValue(Object element, Object value) {
		if(!(value instanceof String))
			return;
		final String str = (String) value;
		if(element instanceof IHttpRegexCondition) {
			((IHttpRegexCondition) element).setPattern(str);
		} else if(element instanceof IHttpRangeCondition) {
			setRangeFromString((IHttpRangeCondition) element, str);
		}
		viewer.refresh();
	}
	
	private void setRangeFromString(IHttpRangeCondition range, String value) {
		String[] parts = value.split("-");
		if(parts.length != 2)
			return;
		
		try {
			int low = Integer.parseInt(parts[0].trim());
			int high = Integer.parseInt(parts[1].trim());
			if(low >= 0 && high >= low) {
				range.setRangeLow(low);
				range.setRangeHigh(high);
			}
			
		} catch (NumberFormatException e ) {
			return;
		}
		
	}
}
