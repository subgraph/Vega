package com.subgraph.vega.ui.hexeditor;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

public class HexEditControl  {
	
	private final static int OFFSET_COLUMN_WIDTH = 50;
	private final static int ELEMENT_COLUMN_WIDTH = 25;
	private final static int ASCII_COLUMN_WIDTH = 150;
	private TableViewer tableViewer;
	private HexEditContentProvider contentProvider;
	
	public void createTableViewer(Composite parent) {
		tableViewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL);
		tableViewer.getTable().setHeaderVisible(true);
		createTableColumns();
		contentProvider = new HexEditContentProvider(tableViewer);
		tableViewer.setContentProvider(contentProvider);
		tableViewer.setLabelProvider(new HexEditLabelProvider());	
	}
	
	public void setModel(HexEditModel model) {
		contentProvider.setModel(model);
		tableViewer.setItemCount(model.getLineCount());
	}
	
	private void createTableColumns() {
		
		for(int i = 0; i < 18; i++) {
			TableViewerColumn tvc = new TableViewerColumn(tableViewer, SWT.CENTER);
			TableColumn column = tvc.getColumn();
			column.setResizable(true);
			if(i == 0) {
				column.setText("Offset");
				column.setWidth(OFFSET_COLUMN_WIDTH);
			} else if(i == 17) {
				column.setText("Ascii");
				column.setWidth(ASCII_COLUMN_WIDTH);
			} else {
				column.setText(String.format("%02X", i - 1));
				column.setWidth(ELEMENT_COLUMN_WIDTH);
			}
		}
	}

}