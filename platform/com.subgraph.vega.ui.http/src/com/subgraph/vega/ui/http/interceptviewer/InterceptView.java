package com.subgraph.vega.ui.http.interceptviewer;

import java.net.URI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorEventHandler;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.proxy.IProxyTransactionEventHandler;
import com.subgraph.vega.api.http.proxy.ProxyTransactionDirection;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.httpeditor.HttpRequestViewer;
import com.subgraph.vega.ui.httpeditor.RequestRenderer;

public class InterceptView extends ViewPart {
	private static final Image IMAGE_FORWARD = Activator.getImageDescriptor("icons/start_16x16.png").createImage();
	private static final Image IMAGE_DROP = Activator.getImageDescriptor("icons/stop_16x16.png").createImage();
	private IHttpInterceptor interceptor;
	private IHttpInterceptorEventHandler interceptorEventHandler;
	private IProxyTransactionEventHandler transactionEventHandler;
	private IProxyTransaction transactionCurr; /**< Current intercepted transaction. */
	private final RequestRenderer requestRenderer = new RequestRenderer();
	private HttpRequestViewer requestViewer;
	private IProxyTransaction requestTransactionCurr; /**< Transaction currently displayed in request viewer. */
	private Label requestLabelStatus;
	private ToolItem requestButtonForward;
	private ToolItem requestButtonDrop;
	private HttpRequestViewer responseViewer;
	private IProxyTransaction responseTransactionCurr; /**< Transaction currently displayed in response viewer. */
	private Label responseLabelStatus;
	private ToolItem responseButtonForward;
	private ToolItem responseButtonDrop;

	public InterceptView() {
		interceptorEventHandler = new IHttpInterceptorEventHandler() {
			@Override
			public void notifyQueue(IProxyTransaction transaction) {
				if (transaction.hasResponse() == false) {
					handleTransactionRequest(transaction);
				} else {
					handleTransactionResponse(transaction);
				}
			}
		};
		transactionEventHandler = new IProxyTransactionEventHandler() {
			@Override
			public void notifyComplete() {
				handleTransactionComplete();
			}
		};

		interceptor = Activator.getDefault().getProxyService().getInterceptor();
		interceptor.setEventHandler(interceptorEventHandler);
	}

	@Override
	public void createPartControl(Composite parent) {
		final SashForm form = new SashForm(parent, SWT.VERTICAL);
		createTabFolderRequest(form);
		setRequestInactive();
		createTabFolderResponse(form);
		setResponseInactive();
		form.setWeights(new int[] { 50, 50, });
		form.pack();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	private void handleTransactionRequest(final IProxyTransaction transaction) {
		synchronized(this) {
			if (transactionCurr == null) {
				transactionCurr = transaction;
				Display display = requestViewer.getControl().getDisplay();
				display.asyncExec (new Runnable () {
					public void run () {
						if (requestViewer != null && responseViewer != null) {							
							setRequestPending();
						}
					}
				});	
			}
		}
	}

	private void handleTransactionResponse(final IProxyTransaction transaction) {
		synchronized(this) {
			if (transactionCurr == null || transactionCurr == transaction) {
				transactionCurr = transaction;
				transactionCurr.setEventHandler(null);
				Display display = responseViewer.getControl().getDisplay();
				display.asyncExec (new Runnable () {
					public void run () {
						if (requestViewer != null && responseViewer != null) {							
							setResponsePending();
						}
					}
				});
			}
		}
	}

	private void setTransactionComplete() {
		synchronized(this) {
			if (transactionCurr != null) {
				if (transactionCurr.hasResponse() == false) {
					setRequestPending();
					setResponseInactive();
				} else {
					setResponsePending();
				}
			} else {
				setRequestInactive();
				setResponseInactive();
			}
		}
	}

	private void handleTransactionComplete() {
		synchronized(this) {
			transactionCurr.setEventHandler(null);
			transactionCurr = interceptor.transactionQueuePop();
			Display display = responseViewer.getControl().getDisplay();
			display.asyncExec (new Runnable () {
				public void run () {
					if (requestViewer != null && responseViewer != null) {
						setTransactionComplete();
					}
				}
			});
		}
	}

	private Composite createTabFolderRequest(Composite parent) {
		final TabFolder rootControl = new TabFolder(parent, SWT.TOP);

		final TabItem requestItem = new TabItem(rootControl, SWT.NONE);
		requestItem.setText("Request");
		requestItem.setControl(createTabRequest(rootControl));

		final TabItem optionsItem = new TabItem(rootControl, SWT.NONE);
		optionsItem.setText("Options");
		OptionsViewer requestOptions = new OptionsViewer(ProxyTransactionDirection.DIRECTION_REQUEST);
		optionsItem.setControl(requestOptions.createViewer(rootControl));

		return rootControl;
	}

	private Composite createTabRequest(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		final GridLayout rootControlLayout = new GridLayout(2, false);
		rootControlLayout.marginWidth = 2;
		rootControlLayout.marginHeight = 0;
		rootControlLayout.marginLeft = 0;
		rootControlLayout.marginTop = 0;
		rootControlLayout.marginRight = 0;
		rootControlLayout.marginBottom = 0;
		rootControlLayout.horizontalSpacing = 2;
		rootControlLayout.verticalSpacing = 0;
		rootControl.setLayout(rootControlLayout);

		requestLabelStatus = new Label(rootControl, SWT.NONE);
		requestLabelStatus.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		
		ToolBar toolBar = new ToolBar(rootControl, SWT.FLAT);
		toolBar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		requestButtonForward = new ToolItem(toolBar, SWT.PUSH);
		requestButtonForward.setImage(IMAGE_FORWARD);
		requestButtonForward.addSelectionListener(createSelectionListenerRequestButtonForward());
		requestButtonForward.setToolTipText("Forward");
		requestButtonDrop = new ToolItem(toolBar, SWT.PUSH);
		requestButtonDrop.setImage(IMAGE_DROP);
		requestButtonDrop.addSelectionListener(createSelectionListenerRequestButtonDrop());
		requestButtonDrop.setToolTipText("Drop");

		final Group requestViewerGroup = new Group(rootControl, SWT.NONE);
		requestViewerGroup.setLayout(new FillLayout());
		requestViewerGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
		requestViewer = new HttpRequestViewer(requestViewerGroup);

		return rootControl;
	}

	private void doRequestForward() {
		synchronized(this) {
			transactionCurr.setEventHandler(transactionEventHandler);
			transactionCurr.doForward();
			setRequestSent();
		}
	}

	private void doRequestDrop() {
		synchronized(this) {
			transactionCurr.setEventHandler(null);
			transactionCurr.doDrop();
			transactionCurr = interceptor.transactionQueuePop();
			if (transactionCurr != null) {
				setRequestPending();
			} else {
				setRequestInactive();
			}
		}
	}

	private SelectionListener createSelectionListenerRequestButtonForward() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doRequestForward();
			}
		};
	}

	private SelectionListener createSelectionListenerRequestButtonDrop() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doRequestDrop();
			}
		};
	}

	private void setRequestInactive() {
		synchronized(this) {
			requestLabelStatus.setText("No request pending");
			requestButtonForward.setEnabled(false);
			requestButtonDrop.setEnabled(false);
			requestViewer.clearContent();
			requestTransactionCurr = null;
		}
	}

	private void setRequestSent() {
		synchronized(this) {
			requestLabelStatus.setText("Request sent, awaiting response");
			requestButtonForward.setEnabled(false);
			requestButtonDrop.setEnabled(false);
			requestViewer.setContent(requestRenderer.renderRequestText(transactionCurr.getRequest()));
			requestTransactionCurr = transactionCurr;			
		}
	}

	private String getTransactionHostPart(IProxyTransaction transaction) {
		final URI uri = transactionCurr.getUri();
		String httpHost = uri.getScheme() + "://" + uri.getHost();
		if (uri.getPort() != -1) {
			httpHost += ":" + uri.getPort();
		}
		return httpHost;
	}

	private void setRequestPending() {
		synchronized(this) {
			requestLabelStatus.setText("Request pending to " + getTransactionHostPart(transactionCurr));
			requestButtonForward.setEnabled(true);
			requestButtonDrop.setEnabled(true);
			requestViewer.setContent(requestRenderer.renderRequestText(transactionCurr.getRequest()));			
			requestTransactionCurr = transactionCurr;			
		}
	}

	private Composite createTabFolderResponse(Composite parent) {
		final TabFolder rootControl = new TabFolder(parent, SWT.TOP);

		final TabItem responseItem = new TabItem(rootControl, SWT.NONE);
		responseItem.setText("Response");
		responseItem.setControl(createTabResponse(rootControl));

		final TabItem optionsItem = new TabItem(rootControl, SWT.NONE);
		optionsItem.setText("Options");
		OptionsViewer requestOptions = new OptionsViewer(ProxyTransactionDirection.DIRECTION_RESPONSE);
		optionsItem.setControl(requestOptions.createViewer(rootControl));

		return rootControl;
	}

	private Composite createTabResponse(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		final GridLayout rootControlLayout = new GridLayout(2, false);
		rootControlLayout.marginWidth = 2;
		rootControlLayout.marginHeight = 0;
		rootControlLayout.marginLeft = 0;
		rootControlLayout.marginTop = 0;
		rootControlLayout.marginRight = 0;
		rootControlLayout.marginBottom = 0;
		rootControlLayout.horizontalSpacing = 2;
		rootControlLayout.verticalSpacing = 0;
		rootControl.setLayout(rootControlLayout);

		responseLabelStatus = new Label(rootControl, SWT.NONE);
		responseLabelStatus.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		
		ToolBar toolBar = new ToolBar(rootControl, SWT.FLAT);
		toolBar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		responseButtonForward = new ToolItem(toolBar, SWT.PUSH);
		responseButtonForward.setImage(IMAGE_FORWARD);
		responseButtonForward.addSelectionListener(createSelectionListenerResponseButtonForward());
		responseButtonDrop = new ToolItem(toolBar, SWT.PUSH);
		responseButtonDrop.setImage(IMAGE_DROP);
		responseButtonDrop.addSelectionListener(createSelectionListenerResponseButtonDrop());

		final Group responseViewerGroup = new Group(rootControl, SWT.NONE);
		responseViewerGroup.setLayout(new FillLayout());
		responseViewerGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
		responseViewer = new HttpRequestViewer(responseViewerGroup);

		return rootControl;
	}

	private void doResponseForward() {
		synchronized(this) {
			transactionCurr.doForward();
			transactionCurr = interceptor.transactionQueuePop();
			if (transactionCurr != null) {
				if (transactionCurr.hasRequest() == false) {
					setRequestPending();
					setResponseInactive();
				} else {
					setResponsePending();
				}
			} else {
				setRequestInactive();
				setResponseInactive();
			}
		}
	}

	private void doResponseDrop() {
		synchronized(this) {
			transactionCurr.doDrop();
			transactionCurr = interceptor.transactionQueuePop();
			if (transactionCurr != null) {
				if (transactionCurr.hasRequest() == false) {
					setRequestPending();
					setResponseInactive();
				} else {
					setResponsePending();
				}
			} else {
				setRequestInactive();
				setResponseInactive();
			}
		}
	}

	private SelectionListener createSelectionListenerResponseButtonForward() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doResponseForward();
			}
		};
	}

	private SelectionListener createSelectionListenerResponseButtonDrop() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doResponseDrop();
			}
		};
	}

	private void setResponseInactive() {
		synchronized(this) {
			responseLabelStatus.setText("No response pending");
			responseButtonForward.setEnabled(false);
			responseButtonDrop.setEnabled(false);
			responseViewer.clearContent();
			responseTransactionCurr = null;
		}
	}

	private void setResponsePending() {
		synchronized(this) {
			responseLabelStatus.setText("Response pending");
			responseButtonForward.setEnabled(true);
			responseButtonForward.setEnabled(true);
			responseViewer.setContent(requestRenderer.renderResponseText(transactionCurr.getResponse().getRawResponse()));
			responseTransactionCurr = transactionCurr;
			if (requestTransactionCurr != transactionCurr) {
				setRequestSent();
			}
		}
	}

}
