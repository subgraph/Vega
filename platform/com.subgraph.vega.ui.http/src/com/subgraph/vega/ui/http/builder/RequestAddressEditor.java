package com.subgraph.vega.ui.http.builder;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;

public class RequestAddressEditor implements IHttpBuilderPart {
	private String requestSchemes[] = {
		"http",
		"https",
	};

	private IHttpRequestBuilder requestBuilder;
	private Composite parentComposite;
	private ComboViewer requestScheme;
	private Text requestHost;
	private Text requestPort;

	public RequestAddressEditor(final IHttpRequestBuilder requestBuilder) {
		this.requestBuilder = requestBuilder;
	}
	
	@Override
	public Composite createPartControl(Composite parent) {
		final GridLayout controlLayout = new GridLayout(3, false);
		controlLayout.marginWidth = 0;
		controlLayout.marginHeight = 0;
		controlLayout.marginLeft = 0;
		controlLayout.marginTop = 0;
		controlLayout.marginRight = 0;
		controlLayout.marginBottom = 0;
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(controlLayout);

		final Composite schemeControl = new Composite(parentComposite, SWT.NONE);
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

		final Composite hostControl = new Composite(parentComposite, SWT.NONE);
		hostControl.setLayout(new GridLayout(2, false));
		hostControl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		label = new Label(hostControl, SWT.NONE);
		label.setText("Host:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		requestHost = new Text(hostControl, SWT.BORDER | SWT.SINGLE);
		requestHost.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Composite portControl = new Composite(parentComposite, SWT.NONE);
		portControl.setLayout(new GridLayout(2, false));
		portControl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		label = new Label(portControl, SWT.NONE);
		label.setText("Port:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		requestPort = new Text(portControl, SWT.BORDER | SWT.SINGLE);
		final FontMetrics requestPortFm = new GC(requestPort).getFontMetrics();
		GridData requestPortGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		requestPortGd.widthHint = requestPortFm.getAverageCharWidth() * 7;
		requestPort.setLayoutData(requestPortGd);
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
		
		refresh();

		return parentComposite;
	}

	@Override
	public Control getControl() {
		return parentComposite;
	}

	@Override
	public void refresh() {
		requestScheme.setSelection(new StructuredSelection(requestBuilder.getScheme()));
		requestHost.setText(requestBuilder.getHost());
		requestPort.setText(Integer.toString(requestBuilder.getHostPort()));
	}

	@Override
	public void processContents() {
		final String scheme = (String)((IStructuredSelection) requestScheme.getSelection()).getFirstElement();
		requestBuilder.setScheme(scheme);
		requestBuilder.setHost(requestHost.getText().trim());
		try {
			requestBuilder.setHostPort(Integer.parseInt(requestPort.getText().trim()));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid host port");
		}
	}

	private ISelectionChangedListener createSelectionChangedListenerRequestScheme() {
		return new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent e) {
				final String scheme = (String)((IStructuredSelection)requestScheme.getSelection()).getFirstElement();
				if (scheme.equals("https")) {
					requestPort.setText("443");
				} else {
					requestPort.setText("80");
				}
			}
		};
	}
	
}
