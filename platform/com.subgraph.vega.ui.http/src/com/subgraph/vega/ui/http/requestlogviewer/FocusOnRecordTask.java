package com.subgraph.vega.ui.http.requestlogviewer;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.internal.ui.http.requestlogviewer.RequestViewContentProvider;

public class FocusOnRecordTask implements Runnable {
	private final IRequestLogRecord record;
	private final RequestViewContentProvider contentProvider;
	private final TableViewer tableViewer;

	
	FocusOnRecordTask(IRequestLogRecord record, RequestViewContentProvider contentProvider, TableViewer tableViewer) {
		this.record = record;
		this.contentProvider = contentProvider;
		this.tableViewer = tableViewer;
	}

	@Override
	public void run() {
		final int row = contentProvider.getRowForRecord(record);
		if(row == -1) {
			return;
		}
	
		final Table table = tableViewer.getTable();
		final Display display = table.getDisplay();
		
		if(!display.isDisposed()) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					if(table.isDisposed()) {
						return;
					}
					final int top = calculateTopRow(row);
					table.setTopIndex(top);
					tableViewer.setSelection(new StructuredSelection(record), false);
				}
			});
		}
	}
	
	private int calculateTopRow(int rowToDisplay) {
		final int rowsVisible = rowsVisible();
		final int rowsAbove = rowsVisible / 2;
		if(rowsAbove > rowToDisplay) {
			return 0;
		} else {
			return rowToDisplay - rowsAbove;
		}
	}

	private int rowsVisible() {
		final Table table = tableViewer.getTable();
		final int rowHeight = table.getItemHeight();
		final int clientHeight = (table.getClientArea().height - table.getHeaderHeight());
		return clientHeight / rowHeight;
	}
}
