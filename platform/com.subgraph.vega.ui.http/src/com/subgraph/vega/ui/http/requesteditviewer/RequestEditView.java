package com.subgraph.vega.ui.http.requesteditviewer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.httpeditor.HttpRequestViewer;
import com.subgraph.vega.ui.httpeditor.RequestRenderer;

public class RequestEditView extends ViewPart {
	public final static String VIEW_ID = "com.subgraph.vega.views.requestEdit";
	private IHttpRequestEngine requestEngine;
	private IHttpRequestBuilder requestBuilder;
	private Text requestLine;
	private TableViewer tableViewer;
	private Button buttonRemove;
	private Button buttonMoveUp;
	private Button buttonMoveDown;
	private final RequestRenderer requestRenderer = new RequestRenderer();
	private HttpRequestViewer responseViewer;
	
	public RequestEditView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		final SashForm form = new SashForm(parent, SWT.VERTICAL);

		Composite set1 = new Composite(form, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		set1.setLayout(layout);

		Label label;

		label = new Label(set1, SWT.NONE);
		label.setText("Request:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));

		requestLine = new Text(set1, SWT.BORDER | SWT.SINGLE);
		requestLine.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));

		label = new Label(set1, SWT.NONE);
		label.setText("Headers:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		
		Composite comp = createHeaderTable(set1);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		comp = new Composite(set1, SWT.NONE);
		comp.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
		layout = new GridLayout(1, true);
		comp.setLayout(layout);
		Button b = new Button(comp, SWT.PUSH);
		b.setText("create");
		b.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		buttonRemove = new Button(comp, SWT.PUSH);
		buttonRemove.setText("remove");
		buttonRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		buttonRemove.setGrayed(true);
		buttonRemove.addSelectionListener(createSelectionListenerButtonRemove());
		buttonMoveUp = new Button(comp, SWT.PUSH);
		buttonMoveUp.setText("move up");
		buttonMoveUp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		buttonMoveUp.setGrayed(true);
		buttonMoveUp.addSelectionListener(createSelectionListenerButtonMoveUp());
		buttonMoveDown = new Button(comp, SWT.PUSH);
		buttonMoveDown.setText("move down");
		buttonMoveDown.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		buttonMoveDown.setGrayed(true);
		buttonMoveDown.addSelectionListener(createSelectionListenerButtonMoveDown());

		b = new Button(set1, SWT.PUSH);
		b.setText("send");
		b.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		b.addSelectionListener(createSelectionListenerButtonSend());

		responseViewer = new HttpRequestViewer(form);

		form.setWeights(new int[] {40, 60});
		form.pack();
	}

	@Override
	public void setFocus() {
		if (tableViewer != null) {
			tableViewer.getControl().setFocus();
		}		
	}

	public void setRequest(IRequestLogRecord record) {
		if (record != null) {
			if (requestEngine == null) {
				IHttpRequestEngineFactory requestEngineFactory = Activator.getDefault().getHttpRequestEngineFactoryService();
				requestEngine = requestEngineFactory.createRequestEngine(requestEngineFactory.createConfig());
			}

			requestBuilder = requestEngine.createRequestBuilder();
			requestBuilder.setFromRequest(record);
			requestLine.setText(requestBuilder.getRequestLine());
		} else {
			requestBuilder = null;
		}

		tableViewer.setInput(requestBuilder);
	}

	private Composite createHeaderTable(Composite parent) {
		final Composite comp = new Composite(parent, SWT.NONE);
		tableViewer = new TableViewer(comp, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer.setContentProvider(new HeaderTableContentProvider());
		tableViewer.addSelectionChangedListener(createSelectionChangedListener());
		final TableColumnLayout tcl = new TableColumnLayout();
		comp.setLayout(tcl);
		createHeaderTableColumns(tableViewer, tcl);
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		return comp;
	}

	private void createHeaderTableColumns(TableViewer viewer, TableColumnLayout layout) {
		final String[] titles = { "Name", "Value", };
		final ColumnLayoutData[] layoutData = {
			new ColumnPixelData(120, true, true),
			new ColumnPixelData(1200, true, true),
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

	private SelectionListener createSelectionListenerButtonRemove() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					requestBuilder.removeHeader((IHttpHeaderBuilder) i.next());
				}
				tableViewer.refresh();
			}
		};
	}
	
	private SelectionListener createSelectionListenerButtonMoveUp() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					int idx = requestBuilder.getHeaderIdxOf((IHttpHeaderBuilder) i.next());
					if (idx != 0) {
						requestBuilder.swapHeader(idx - 1, idx);
					} else {
						break;
					}
				}
				tableViewer.refresh();
			}
		};
	}

	private SelectionListener createSelectionListenerButtonMoveDown() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
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
				tableViewer.refresh();
			}
		};
	}

	private SelectionListener createSelectionListenerButtonSend() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				HttpUriRequest uriRequest;
				try {
					uriRequest = requestBuilder.buildRequest();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}

				BasicHttpContext ctx = new BasicHttpContext();
				try {
					IHttpResponse response = requestEngine.sendRequest(uriRequest, ctx);
					responseViewer.setContent(requestRenderer.renderResponseText(response.getRawResponse()));
				} catch (ClientProtocolException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}			
			}
		};
	}

}
