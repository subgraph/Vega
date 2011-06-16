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
package com.subgraph.vega.ui.hexeditor;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellNavigationStrategy;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class HexEditControl extends Composite {
	private final static int WORD_SIZE = 4;
	private final static int MINIMUM_DATA_COLUMNS = 4;
	private final HexEditFonts fonts;
	private TableViewer tableViewer;
	private HexEditModel model;
	private int currentDataColumnCount;
	private boolean editable = true;
	private volatile boolean enablePreserveSelection = true;
	public HexEditControl(Composite parent) {
		super(parent, SWT.NONE);
		this.fonts = new HexEditFonts(this);
		currentDataColumnCount = calculateDataColumnCount(getClientArea().width);
		tableViewer = createTableViewer();
		tableViewer.setContentProvider(new HexEditContentProvider(tableViewer));
		createColumns(currentDataColumnCount);
		
		addControlListener(createControlListener());
	}
	
	private TableViewer createTableViewer() {
		final TableViewer tv = new TableViewer(this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL) {
			@Override
			protected void preservingSelection(Runnable updateCode) { 
				if(enablePreserveSelection)
					super.preservingSelection(updateCode);
			}
		};
		
		tv.getTable().setHeaderVisible(true);	
		tv.setUseHashlookup(true);
		if(editable)
			addEditorSupport(tv);
		return tv;
	}
	/*
	 * Q: teh fuck is this?
	 * A: http://bingjava.appspot.com/snippet.jsp?id=2213
	 */
	private void addEditorSupport(TableViewer tv) {
		final CellNavigationStrategy cellNavigation = createCellNavigationStrategy(tv);
		final TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(tv, new FocusCellOwnerDrawHighlighter(tv), cellNavigation);
		final ColumnViewerEditorActivationStrategy activationStrategy = createEditorActivationStrategy(tv);
		TableViewerEditor.create(tv, focusCellManager, activationStrategy, 
				ColumnViewerEditor.TABBING_HORIZONTAL 
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
				| ColumnViewerEditor.TABBING_VERTICAL 
				| ColumnViewerEditor.KEYBOARD_ACTIVATION);
		tv.getColumnViewerEditor().addEditorActivationListener(createEditorActivationListener(tv));
	}
	private CellNavigationStrategy createCellNavigationStrategy(TableViewer tv) {
		final Table t = tv.getTable();
		return new CellNavigationStrategy() {
			@Override
			public ViewerCell findSelectedCell(ColumnViewer viewer, 
					ViewerCell currentSelectedCell, Event event) {
				final ViewerCell cell = super.findSelectedCell(viewer, currentSelectedCell, event);
				if(cell != null) {
					t.showColumn(t.getColumn(cell.getColumnIndex()));
				}
				return cell;
			}
		};
	}
	
	private ColumnViewerEditorActivationStrategy createEditorActivationStrategy(TableViewer tv) {
		return new ColumnViewerEditorActivationStrategy(tv) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return  event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL 
				||  event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
				|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR);
			}
		};
	}
	private ColumnViewerEditorActivationListener createEditorActivationListener(TableViewer tv) {
		final Table t = tv.getTable();
		return new ColumnViewerEditorActivationListener() {
			
			@Override
			public void beforeEditorDeactivated(
					ColumnViewerEditorDeactivationEvent event) {
			}
			
			@Override
			public void beforeEditorActivated(ColumnViewerEditorActivationEvent event) {		
				ViewerCell cell = (ViewerCell) event.getSource();
				t.showColumn(t.getColumn(cell.getColumnIndex()));
			}
			
			@Override
			public void afterEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {				
			}
			
			@Override
			public void afterEditorActivated(ColumnViewerEditorActivationEvent event) {
			}
		};
	}
	
	public void setInput(byte[] binaryData) {
		if(currentDataColumnCount == 0) {
			model = new HexEditModel(binaryData);
		} else {
			model = new HexEditModel(binaryData, currentDataColumnCount);
		}
		changeModel(model);
	}
	
	public boolean isContentDirty() {
		if(model != null) {
			return model.isDirty();
		} else {
			return false;
		}
	}
	
	public byte[] getContent() {
		return model.getContent();
	}
	
	private void changeModel(final HexEditModel model) {
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=200214
		enablePreserveSelection = false;
		tableViewer.setInput(model);
		enablePreserveSelection = true;		
	}

	private void createColumns(int dataColumnCount) {
		tableViewer.getTable().dispose();
		tableViewer = createTableViewer();
		tableViewer.setContentProvider(new HexEditContentProvider(tableViewer));
		final TableColumnLayout layout = new TableColumnLayout();
		createOffsetColumn(layout);
		for(int i = 0; i < dataColumnCount; i++)
			createDataColumn(layout, i);
		createAsciiColumn(layout);
		setLayout(layout);
		tableViewer.setLabelProvider(new HexEditLabelProvider(fonts));
	}
	
	private void createOffsetColumn(TableColumnLayout layout) {
		final TableColumn c = createColumn("Offset", SWT.CENTER);
		c.setAlignment(SWT.RIGHT);
		layout.setColumnData(c, new ColumnPixelData(fonts.getOffsetColumnWidth(), false, false));
	}
	
	private void createAsciiColumn(TableColumnLayout layout) {
		final TableColumn c = createColumn("", SWT.CENTER);
		layout.setColumnData(c, new ColumnWeightData(100));
	}
	
	private void createDataColumn(TableColumnLayout layout, int index) {
		final EditingSupport editor = (editable) ? (new HexEditTableEditor(tableViewer, index)) : (null);
		final TableColumn c = createColumn(String.format("%02X", index), SWT.CENTER, editor);
		layout.setColumnData(c, new ColumnPixelData(fonts.getDataColumnWidth(), false, false));
	}
	
	private TableColumn createColumn(String headerText, int align) {
		return createColumn(headerText, align, null);
	}

	private TableColumn createColumn(String headerText, int align, EditingSupport editor) {
		final TableViewerColumn tvc = new TableViewerColumn(tableViewer, align);
		final TableColumn tc = tvc.getColumn();
		tc.setMoveable(false);
		tc.setResizable(false);
		tc.setText(headerText);
		if(editor != null)
			tvc.setEditingSupport(editor);
		return tc;
	}
	
	private ControlListener createControlListener() {
		return new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				doResize();
			}
		};
	}

	private void doResize() {
		final int width = getClientArea().width;		
		final int cc = calculateDataColumnCount(width);
		if(cc != currentDataColumnCount) {
			changeModel(null);
			createColumns(cc);
			if(model != null) {
				// Remembering old offset does not work on Linux.  Maybe because of these bugs:
				//
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=74739
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=202392
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=295666
				final HexEditModel newModel = model.getModelForRowLength(cc);
				final int topIndex = tableViewer.getTable().getTopIndex();
				final int topOffset = model.getOffsetForLine(topIndex);
				final int newTopIndex = newModel.getLineForOffset(topOffset);
				changeModel(newModel);
				model = newModel;				
				if(newTopIndex != 0) {
					tableViewer.getTable().setTopIndex(newTopIndex);
				}		
			} else {
				changeModel(model);
			}
			currentDataColumnCount = cc;
		}
	}
	
	
	private int calculateDataColumnCount(int pixelWidth) {
		/*
		 * w = width
		 * o = offsetColumnWidth
		 * d = dataColumnWidth
		 * a = asciiColumnWordWidth
		 * n = words
		 * 
		 * w = o + 4nd + na
		 * w - o = n(4d + a)
		 * n = (w - o) / (4d + a)
		 */
		
		int space = (pixelWidth - fonts.getOffsetColumnWidth()) - 30;
		int perWord = WORD_SIZE * fonts.getDataColumnWidth() + fonts.getAsciiColumnWordWidth();
		int words = space / perWord;
		int columns = words * WORD_SIZE;
		return (columns < MINIMUM_DATA_COLUMNS) ? (MINIMUM_DATA_COLUMNS) : (columns);
	}
}
