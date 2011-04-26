package com.subgraph.vega.ui.http.requesteditviewer;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.BasicHttpContext;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.analysis.IContentAnalyzerFactory;
import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.text.httpeditor.HttpRequestViewer;
import com.subgraph.vega.ui.text.httpeditor.RequestRenderer;

public class RequestEditView extends ViewPart {
	private String requestSchemes[] = {
		"http",
		"https",
	};

	public final static String VIEW_ID = "com.subgraph.vega.views.requestEdit";
	private IHttpRequestEngine requestEngine;
	private IHttpRequestBuilder requestBuilder;
	private IContentAnalyzer contentAnalyzer;
	private SashForm parentComposite;
	private ComboViewer requestScheme;
	private Text requestHost;
	private Text requestPort;
	private Text requestLine;
	private TableViewer tableViewerHeaders;
	private Button buttonCreate;
	private Button buttonRemove;
	private Button buttonMoveUp;
	private Button buttonMoveDown;
	private final RequestRenderer requestRenderer = new RequestRenderer();
	private HttpRequestViewer requestBodyViewer;
	private HttpRequestViewer responseViewer;

	@Override
	public void createPartControl(Composite parent) {
		parentComposite = new SashForm(parent, SWT.VERTICAL);

		createRequestEditor(parentComposite);
		createResponseViewer(parentComposite);
		
		parentComposite.setWeights(new int[] {50, 50});
		parentComposite.pack();

		final IContentAnalyzerFactory contentAnalyzerFactory = Activator.getDefault().getContentAnalyzerFactoryService();
		final IScannerModuleRegistry moduleRepository = Activator.getDefault().getScannerModuleRegistry();
		contentAnalyzer = contentAnalyzerFactory.createContentAnalyzer();
		contentAnalyzer.setResponseProcessingModules(moduleRepository.getResponseProcessingModules(true));
		contentAnalyzer.setDefaultAddToRequestLog(true);
		contentAnalyzer.setAddLinksToModel(true);
	}

	@Override
	public void setFocus() {
		if (parentComposite != null) {
			parentComposite.setFocus();
		}
	}

	public void setRequest(IRequestLogRecord record) throws URISyntaxException {
		if (record != null) {
			if (requestEngine == null) {
				IHttpRequestEngineFactory requestEngineFactory = Activator.getDefault().getHttpRequestEngineFactoryService();
				requestEngine = requestEngineFactory.createRequestEngine(requestEngineFactory.createConfig());
			}

			requestBuilder = requestEngine.createRequestBuilder();
			requestBuilder.setFromRequest(record);
			requestScheme.setSelection(new StructuredSelection(requestBuilder.getScheme()));
			requestHost.setText(requestBuilder.getHost());
			requestPort.setText(Integer.toString(requestBuilder.getHostPort()));
			requestLine.setText(requestBuilder.getRequestLine());
			
			if (requestBuilder.getEntity() != null) {
				requestBodyViewer.setContent(requestRenderer.renderEntity(requestBuilder.getEntity()));
			}
		} else {
			requestBuilder = null;
			requestScheme.setSelection(new StructuredSelection(requestSchemes[0]));
			requestHost.setText("");
			requestPort.setText("");
			requestLine.setText("");
		}

		tableViewerHeaders.setInput(requestBuilder);
	}

	public void displayError(String text) {
		MessageBox messageDialog = new MessageBox(parentComposite.getShell(), SWT.ICON_WARNING | SWT.OK);
		messageDialog.setText("Error");
		if (text == null) {
			// REVISIT this should always be set
			text = "An error occurred";
		}
		messageDialog.setMessage(text);
		messageDialog.open();
	}
	
	public void sendRequest() {
		final String scheme = (String)((IStructuredSelection)requestScheme.getSelection()).getFirstElement();
		requestBuilder.setScheme(scheme);
		
		final String host = requestHost.getText().trim();
		if (host.length() == 0) {
			displayError("Invalid host");
			return;
		}
		requestBuilder.setHost(host);

		int hostPort;
		try {
			hostPort = Integer.parseInt(requestPort.getText().trim());
		} catch (NumberFormatException e) {
			hostPort = 0;
		}
		if (hostPort == 0) {
			displayError("Invalid port");
			return;
		}
		requestBuilder.setHostPort(hostPort);

		String[] requestLineWords = requestLine.getText().split(" +");
		if (requestLineWords.length > 0) {
			requestBuilder.setMethod(requestLineWords[0]);
			if (requestLineWords.length > 1) {
				requestBuilder.setPath(requestLineWords[1]);
				if (requestLineWords.length > 2) {
					// REVISIT parse protocol version
				}
			} else {
				requestBuilder.setPath("/");
				// REVISIT: http/0.9?
			}
		} else {
			displayError("Invalid request line");
			return;
		}
		requestBuilder.setRawRequestLine(requestLine.getText());

		final String body = requestBodyViewer.getContent();
		if (body.length() != 0) {
			StringEntity entity;
			try {
				entity = new StringEntity(body);
			} catch (UnsupportedEncodingException e) {
				displayError(e.getMessage());
				return;
			}
			requestBuilder.setEntity(entity);
		} else {
			requestBuilder.setEntity(null);
		}
		
		HttpUriRequest uriRequest;
		try {
			uriRequest = requestBuilder.buildRequest();
		} catch (Exception e) {
			displayError(e.getMessage());
			return;
		}

		BasicHttpContext ctx = new BasicHttpContext();
		IHttpResponse response;
		try {
			response = requestEngine.sendRequest(uriRequest, ctx);
			responseViewer.setContent(requestRenderer.renderResponseText(response.getRawResponse()));
		} catch (Exception e) {
			if (e.getMessage() != null) {
				displayError(e.getMessage());
			} else {
				displayError(e.getCause().getMessage());
			}
			return;
		}

		contentAnalyzer.processResponse(response);
	}

	private Composite createRequestEditor(Composite parent) {
		final Group rootControl = new Group(parent, SWT.V_SCROLL);
		rootControl.setText("Request");
		rootControl.setLayout(new GridLayout(1, true));

		createRequestFieldsEditor(rootControl).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		createHeaderEditor(rootControl).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		createBodyEditor(rootControl).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		return rootControl;
	}
	
	private Composite createRequestFieldsEditor(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(2, false));

		createRequestFieldsAddress(rootControl).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		final Label label = new Label(rootControl, SWT.NONE);
		label.setText("Request:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		requestLine = new Text(rootControl, SWT.BORDER | SWT.SINGLE);
		requestLine.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		return rootControl;
	}

	private Composite createRequestFieldsAddress(Composite parent) {
		final GridLayout controlLayout = new GridLayout(3, false);
		controlLayout.marginWidth = 0;
		controlLayout.marginHeight = 0;
		controlLayout.marginLeft = 0;
		controlLayout.marginTop = 0;
		controlLayout.marginRight = 0;
		controlLayout.marginBottom = 0;
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(controlLayout);

		final Composite schemeControl = new Composite(rootControl, SWT.NONE);
		schemeControl.setLayout(controlLayout);
		schemeControl.setLayout(new GridLayout(2, false));
		schemeControl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		Label label = new Label(schemeControl, SWT.NONE);
		label.setText("Scheme:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		requestScheme = new ComboViewer(schemeControl, SWT.READ_ONLY);
		requestScheme.setContentProvider(new ArrayContentProvider());
		requestScheme.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return (String)element;
			}
		});
		requestScheme.setInput(requestSchemes);
		requestScheme.setSelection(new StructuredSelection(requestSchemes[0]));
		requestScheme.addSelectionChangedListener(createSelectionChangedListenerRequestScheme());

		final Composite hostControl = new Composite(rootControl, SWT.NONE);
		hostControl.setLayout(controlLayout);
		hostControl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		label = new Label(hostControl, SWT.NONE);
		label.setText("Host:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		requestHost = new Text(hostControl, SWT.BORDER | SWT.SINGLE);
		requestHost.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Composite portControl = new Composite(rootControl, SWT.NONE);
		portControl.setLayout(controlLayout);
		portControl.setLayout(new GridLayout(2, false));
		portControl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		label = new Label(portControl, SWT.NONE);
		label.setText("Port:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		requestPort = new Text(portControl, SWT.BORDER | SWT.SINGLE);
		requestPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		requestPort.addListener(SWT.Verify, new Listener() {
	      public void handleEvent(Event e) {
	          String string = e.text;
	          char[] chars = new char[string.length()];
	          string.getChars(0, chars.length, chars, 0);
	          for (int i = 0; i < chars.length; i++) {
	        	  if (!('0' <= chars[i] && chars[i] <= '9')) {
	        		  e.doit = false;
	        		  return;
	        	  }
	          }
	      }
		});

		return rootControl;
	}

	private ISelectionChangedListener createSelectionChangedListenerRequestScheme() {
		return new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent e) {
				final String scheme = (String)((IStructuredSelection)requestScheme.getSelection()).getFirstElement();
				if (scheme == "https") {
					requestPort.setText("443");
				} else {
					requestPort.setText("80");
				}
			}
		};
	}

	private Composite createHeaderEditor(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(2, false));

		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		final Composite compTable = createHeaderTable(rootControl, gd, 9);
		compTable.setLayoutData(gd);
		final Composite compTableButtons = createHeaderTableButtons(rootControl);
		compTableButtons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		return rootControl;
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
	
	private Composite createBodyEditor(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new FillLayout());

		requestBodyViewer = new HttpRequestViewer(rootControl);

		return rootControl;
	}

	private Composite createResponseViewer(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setText("Response");
		rootControl.setLayout(new FillLayout());

		responseViewer = new HttpRequestViewer(rootControl);

		return rootControl;
	}

}
