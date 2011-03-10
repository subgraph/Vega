package com.subgraph.vega.ui.http.requesteditviewer;

import java.net.URISyntaxException;
import java.util.Iterator;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.BasicHttpContext;
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

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.httpeditor.HttpRequestViewer;
import com.subgraph.vega.ui.httpeditor.RequestRenderer;

public class RequestEditView extends ViewPart {
	public final static String VIEW_ID = "com.subgraph.vega.views.requestEdit";
	private IHttpRequestEngine requestEngine;
	private IHttpRequestBuilder requestBuilder;
	private SashForm parentComposite;
	private Text requestHost;
	private Text requestPort;
	private Text requestLine;
	private TableViewer tableViewerHeaders;
	private Button buttonCreate;
	private Button buttonRemove;
	private Button buttonMoveUp;
	private Button buttonMoveDown;
	private final RequestRenderer requestRenderer = new RequestRenderer();
	private HttpRequestViewer responseViewer;
	private IWorkspace currentWorkspace;

	@Override
	public void createPartControl(Composite parent) {
		parentComposite = new SashForm(parent, SWT.VERTICAL);
		createRequestEditor(parentComposite);
		createResponseViewer(parentComposite);
		parentComposite.setWeights(new int[] {40, 60});
		parentComposite.pack();

		// REVISIT
		currentWorkspace = Activator.getDefault().getModel().addWorkspaceListener(new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof WorkspaceOpenEvent) {
					handleWorkspaceOpen((WorkspaceOpenEvent) event);
				} else if(event instanceof WorkspaceCloseEvent) {
					handleWorkspaceClose((WorkspaceCloseEvent) event);
				} else if(event instanceof WorkspaceResetEvent) {
					handleWorkspaceReset((WorkspaceResetEvent) event);
				}
			}
		});
	}

	// REVISIT
	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		this.currentWorkspace = event.getWorkspace();
	}
	
	// REVISIT
	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		this.currentWorkspace = null;
	}
	
	// REVISIT
	private void handleWorkspaceReset(WorkspaceResetEvent event) {
		this.currentWorkspace = event.getWorkspace();
	}

	@Override
	public void setFocus() {
		if (tableViewerHeaders != null) {
			tableViewerHeaders.getControl().setFocus();
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
			requestHost.setText(requestBuilder.getHost());
			requestPort.setText(Integer.toString(requestBuilder.getHostPort()));
			requestLine.setText(requestBuilder.getRequestLine());
		} else {
			requestBuilder = null;
			requestHost.setText("");
			requestPort.setText("");
			requestLine.setText("");
		}

		tableViewerHeaders.setInput(requestBuilder);
	}

	public void displayError(final String text) {
		MessageBox messageDialog = new MessageBox(parentComposite.getShell(), SWT.ICON_WARNING | SWT.OK);
		messageDialog.setText("Error");
		messageDialog.setMessage(text);
		messageDialog.open();
	}
	
	public void sendRequest() {
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

		String[] requestLineWords = requestLine.getText().split(" +");
		if (requestLineWords.length < 2) {
			displayError("Invalid request line");
			return;
		}
		requestBuilder.setMethod(requestLineWords[0]);
		requestBuilder.setPath(requestLineWords[1]);

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
			displayError(e.getMessage());
			return;
		}

		// REVISIT
		currentWorkspace.getRequestLog().addRequestResponse(response.getOriginalRequest(), response.getRawResponse(), response.getHost());
	}

	private Composite createRequestEditor(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setText("Request");
		rootControl.setLayout(new GridLayout(1, true));
		createRequestFieldsEditor(rootControl).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		createHeaderEditor(rootControl).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
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
		final GridLayout controlLayout = new GridLayout(2, false);
		controlLayout.marginWidth = 0;
		controlLayout.marginHeight = 0;
		controlLayout.marginLeft = 0;
		controlLayout.marginTop = 0;
		controlLayout.marginRight = 0;
		controlLayout.marginBottom = 0;
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(controlLayout);

		final Composite hostControl = new Composite(rootControl, SWT.NONE);
		hostControl.setLayout(controlLayout);
		hostControl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		Label label = new Label(hostControl, SWT.NONE);
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

	private Composite createResponseViewer(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setText("Response");
		rootControl.setLayout(new FillLayout());

		responseViewer = new HttpRequestViewer(rootControl);

		return rootControl;
	}

}
