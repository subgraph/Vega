package com.subgraph.vega.ui.http.builder;

import java.util.Iterator;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;

/**
 * Manages visual components used to edit request headers. 
 */
public class RequestHeaderEditor implements IHttpBuilderPart {
	private IHttpRequestBuilder requestBuilder;
	private Composite parentComposite;
	private TableViewer tableViewerHeaders;
	private Button buttonCreate;
	private Button buttonRemove;
	private Button buttonMoveUp;
	private Button buttonMoveDown;

	public RequestHeaderEditor(final IHttpRequestBuilder requestBuilder) {
		this.requestBuilder = requestBuilder;
	}

	@Override
	public Composite createPartControl(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(2, false));

		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		final Composite compTable = createHeaderTable(parentComposite, gd, 9);
		compTable.setLayoutData(gd);
		final Composite compTableButtons = createHeaderTableButtons(parentComposite);
		compTableButtons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		tableViewerHeaders.setInput(requestBuilder);

		return parentComposite;
	}

	@Override
	public Control getControl() {
		return parentComposite;
	}

	@Override
	public void refresh() {
		tableViewerHeaders.refresh();
	}

	@Override
	public void processContents() {
		// nothing to do: headers are modified in table 
	}

	private Composite createHeaderTable(Composite parent, GridData gd, int heightInRows) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		final TableColumnLayout tcl = new TableColumnLayout();
		rootControl.setLayout(tcl);

		tableViewerHeaders = new TableViewer(rootControl, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewerHeaders.setContentProvider(new HeaderTableContentProvider());
		tableViewerHeaders.addSelectionChangedListener(createSelectionChangedListener());
		createHeaderTableColumns(tableViewerHeaders, tcl);
		final Table table = tableViewerHeaders.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		gd.heightHint = table.getItemHeight() * heightInRows;

		return rootControl;
	}

	private ISelectionChangedListener createSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				boolean sel = event.getSelection().isEmpty(); 
				buttonRemove.setGrayed(sel);
				buttonMoveUp.setGrayed(sel);
				buttonMoveDown.setGrayed(sel);
			}
		};
	}

	private void createHeaderTableColumns(TableViewer viewer, TableColumnLayout layout) {
		final String[] titles = { "Name", "Value", };
		final ColumnLayoutData[] layoutData = {
			new ColumnPixelData(120, true, true),
			new ColumnWeightData(100, 100, true),
		};
		final EditingSupport editorList[] = {
				new HeaderNameEditingSupport(viewer),
				new HeaderValueEditingSupport(viewer),
		};
		final ColumnLabelProvider providerList[] = {
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpHeaderBuilder) element).getName();
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpHeaderBuilder) element).getValue();
				}
			},
		};

		for (int i = 0; i < titles.length; i++) {
			final TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			final TableColumn c = column.getColumn();
			layout.setColumnData(c, layoutData[i]);
			c.setText(titles[i]);
			c.setMoveable(true);
			column.setEditingSupport(editorList[i]);
			column.setLabelProvider(providerList[i]);
		}	
	}

	private Composite createHeaderTableButtons(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, true));

		buttonCreate = new Button(rootControl, SWT.PUSH);
		buttonCreate.setText("create");
		buttonCreate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		buttonCreate.addSelectionListener(createSelectionListenerButtonCreate());
		buttonRemove = new Button(rootControl, SWT.PUSH);
		buttonRemove.setText("remove");
		buttonRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		buttonRemove.setGrayed(true);
		buttonRemove.addSelectionListener(createSelectionListenerButtonRemove());
		buttonMoveUp = new Button(rootControl, SWT.PUSH);
		buttonMoveUp.setText("move up");
		buttonMoveUp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		buttonMoveUp.setGrayed(true);
		buttonMoveUp.addSelectionListener(createSelectionListenerButtonMoveUp());
		buttonMoveDown = new Button(rootControl, SWT.PUSH);
		buttonMoveDown.setText("move down");
		buttonMoveDown.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		buttonMoveDown.setGrayed(true);
		buttonMoveDown.addSelectionListener(createSelectionListenerButtonMoveDown());

		return rootControl;
	}

	private SelectionListener createSelectionListenerButtonCreate() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				requestBuilder.addHeader("", "");
				tableViewerHeaders.refresh();
				tableViewerHeaders.editElement(tableViewerHeaders.getElementAt(tableViewerHeaders.getTable().getItemCount() - 1), 0);
			}
		};
	}

	private SelectionListener createSelectionListenerButtonRemove() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewerHeaders.getSelection();
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					requestBuilder.removeHeader((IHttpHeaderBuilder) i.next());
				}
				tableViewerHeaders.refresh();
			}
		};
	}
	
	private SelectionListener createSelectionListenerButtonMoveUp() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewerHeaders.getSelection();
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					int idx = requestBuilder.getHeaderIdxOf((IHttpHeaderBuilder) i.next());
					if (idx != 0) {
						requestBuilder.swapHeader(idx - 1, idx);
					} else {
						break;
					}
				}
				tableViewerHeaders.refresh();
			}
		};
	}

	private SelectionListener createSelectionListenerButtonMoveDown() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewerHeaders.getSelection();
				int idx[] = new int[selection.size()];
				int offset = 1;
				for (Iterator<?> i = selection.iterator(); i.hasNext(); offset++) {
					idx[idx.length - offset] = requestBuilder.getHeaderIdxOf((IHttpHeaderBuilder) i.next());
				}

				if (idx[0] + 1 != requestBuilder.getHeaderCnt()) {
					for (int i = 0; i < idx.length; i++) {
						requestBuilder.swapHeader(idx[i], idx[i] + 1);
					}
				}
				tableViewerHeaders.refresh();
			}
		};
	}
	
}
