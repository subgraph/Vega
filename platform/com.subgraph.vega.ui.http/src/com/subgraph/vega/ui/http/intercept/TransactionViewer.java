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
package com.subgraph.vega.ui.http.intercept;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.subgraph.vega.api.http.proxy.IProxyTransaction.TransactionDirection;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.http.requests.IHttpResponseBuilder;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.http.builder.HeaderEditor;
import com.subgraph.vega.ui.http.builder.IHttpBuilderPart;
import com.subgraph.vega.ui.http.builder.RequestEditor;
import com.subgraph.vega.ui.http.builder.ResponseMessageEditor;
import com.subgraph.vega.ui.http.intercept.config.ConfigureInterceptionContent;
import com.subgraph.vega.ui.util.dialogs.ConfigDialogCreator;
import com.subgraph.vega.ui.util.dialogs.ErrorDialog;

public class TransactionViewer extends Composite {
	private static final Image IMAGE_CONFIGURE = Activator.getImageDescriptor("icons/interception_rules.png").createImage();
	private final IModel model;
	private final TransactionDirection direction;
	private final TransactionInfo transactionInfo;
	private Label statusLabel;
	private ToolItem configureButton;
	private Composite viewerControl;
	private StackLayout viewerLayout;
	private Composite viewerEmpty;
	private Menu viewerMenu;
	private boolean isPending;
	private int lastTransactionSerial;
	private IHttpBuilderPart builderPartCurr;
	private Window configDialog;

	public TransactionViewer(Composite parent, IModel model, TransactionInfo transactionInfo, TransactionDirection direction) {
		super(parent, SWT.NONE);

		this.model = model;
		this.direction = direction;
		this.transactionInfo = transactionInfo;
		isPending = false;

		setLayout(createLayout());

		createStatusLabel();
		createToolbar();

		final Group viewerGroup = new Group(this, SWT.NONE);
		viewerGroup.setLayout(new FillLayout());
		viewerGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		if (direction == TransactionDirection.DIRECTION_REQUEST) { 
		    lastTransactionSerial = transactionInfo.getRequestTransactionSerial();
			createViewersRequest(viewerGroup);
			updateViewerRequest();
		} else {
		    lastTransactionSerial = transactionInfo.getResponseTransactionSerial();
			createViewersResponse(viewerGroup);
			updateViewerResponse();
		}
	}

	public void processChanges() {
		if (isPending) {
			try {
				builderPartCurr.processContents();
			} catch (Exception e) {
				ErrorDialog.displayExceptionError(getShell(), e);
			}
		}
	}

	private GridLayout createLayout() {
		final GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 2;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 2;
		return layout;
	}
	
	private Label createStatusLabel() {
		statusLabel = new Label(this, SWT.NONE);
		statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
		return statusLabel;
	}

	private ToolBar createToolbar() {
		final ToolBar toolBar = new ToolBar(this, SWT.RIGHT);

		viewerMenu = new Menu(getShell(), SWT.POP_UP);
	    final ToolItem item = new ToolItem(toolBar, SWT.DROP_DOWN);
	    item.setText("View");
	    item.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	        	Rectangle rect = item.getBounds();
	        	Point pt = new Point(rect.x, rect.y + rect.height);
	            pt = toolBar.toDisplay(pt);
	            viewerMenu.setLocation(pt.x, pt.y);
	            viewerMenu.setVisible(true);
	    	}
	    });
	    
		configureButton = createToolbarButton(toolBar, IMAGE_CONFIGURE, "Configure interception rules", createConfigureButtonListener());
		
		return toolBar;
	}
	
	private ToolItem createToolbarButton(ToolBar toolBar, Image image, String tooltip, SelectionListener listener) {
		final ToolItem buttonItem = new ToolItem(toolBar, SWT.PUSH);
		buttonItem.setImage(image);
		buttonItem.setToolTipText(tooltip);
		buttonItem.addSelectionListener(listener);
		return buttonItem;
	}
	
	private SelectionListener createConfigureButtonListener() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doConfigure();
			}
		};
	}

	private void createViewersRequest(final Composite parent) {
		viewerControl = new Composite(parent, SWT.NONE);
		viewerLayout = new StackLayout();
		viewerControl.setLayout(viewerLayout);
		
		IHttpRequestBuilder requestBuilder = transactionInfo.getRequestBuilder();
		final SelectionListener listener = createSelectionListenerMenuItem();

	    viewerEmpty = new Composite(viewerControl, SWT.NONE);
	    viewerEmpty.setLayout(new FillLayout());
		
		RequestEditor requestEditor = new RequestEditor(viewerControl, requestBuilder);
	    MenuItem menuItem = new MenuItem(viewerMenu, SWT.NONE);
	    menuItem.setText("Request");
	    menuItem.setData(requestEditor);
	    menuItem.addSelectionListener(listener);
		builderPartCurr = requestEditor;

	    HeaderEditor headerEditor = new HeaderEditor(viewerControl, requestBuilder, 0);
	    menuItem = new MenuItem(viewerMenu, SWT.NONE);
	    menuItem.setText("Headers");
	    menuItem.setData(headerEditor);
	    menuItem.addSelectionListener(listener);
	}

	private void createViewersResponse(final Composite parent) {
		viewerControl = new Composite(parent, SWT.NONE);
		viewerLayout = new StackLayout();
		viewerControl.setLayout(viewerLayout);

		IHttpResponseBuilder responseBuilder = transactionInfo.getResponseBuilder();
		final SelectionListener listener = createSelectionListenerMenuItem();

	    viewerEmpty = new Composite(viewerControl, SWT.NONE);
	    viewerEmpty.setLayout(new FillLayout());

		ResponseMessageEditor responseEditor = new ResponseMessageEditor(viewerControl, responseBuilder);
		responseEditor.setEditable(false);
	    MenuItem menuItem = new MenuItem(viewerMenu, SWT.NONE);
	    menuItem.setText("Response");
	    menuItem.setData(responseEditor);
	    menuItem.addSelectionListener(listener);
		builderPartCurr = responseEditor;

	    HeaderEditor headerEditor = new HeaderEditor(viewerControl, responseBuilder, 0);
	    headerEditor.setEditable(false);
	    menuItem = new MenuItem(viewerMenu, SWT.NONE);
	    menuItem.setText("Headers");
	    menuItem.setData(headerEditor);
	    menuItem.addSelectionListener(listener);
	}
	
	private SelectionAdapter createSelectionListenerMenuItem() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final MenuItem menuItem = (MenuItem) event.widget;
				final IHttpBuilderPart builderPart = (IHttpBuilderPart) menuItem.getData();
				processChanges();
				builderPartCurr = builderPart;
				builderPartCurr.setEditable(isPending);
				builderPartCurr.refresh();
				viewerLayout.topControl = builderPartCurr.getControl();
				viewerControl.layout();
			}
		};
	}

	private void doConfigure() {
		if(configDialog != null && configDialog.getShell() != null) {
			configDialog.close();
			configDialog = null;
			return;
		}
		configDialog = ConfigDialogCreator.createDialog(configureButton, new ConfigureInterceptionContent(model, direction));
		configDialog.open();
	}

	private void updateViewerRequest() {
		final TransactionManager.TransactionStatus status = transactionInfo.getRequestStatus();
		final Control topControl;
		String statusMessage = transactionInfo.getRequestStatusMessage();
		if (status == TransactionManager.TransactionStatus.STATUS_INACTIVE) {
			isPending = false;
			topControl = viewerEmpty;
		} else {
			isPending = (status == TransactionManager.TransactionStatus.STATUS_PENDING);
			topControl = builderPartCurr.getControl();
			builderPartCurr.setEditable(isPending);
			if (lastTransactionSerial != transactionInfo.getRequestTransactionSerial()) {
				lastTransactionSerial = transactionInfo.getRequestTransactionSerial();
				builderPartCurr.refresh();
			}
		}
		statusLabel.setText(statusMessage);
		if (viewerLayout.topControl != topControl) {
			viewerLayout.topControl = topControl;
			viewerControl.layout();
		}
		viewerMenu.setEnabled(isPending);
	}
	
	private void updateViewerResponse() {
		final TransactionManager.TransactionStatus status = transactionInfo.getResponseStatus();
		final Control topControl;
		String statusMessage = transactionInfo.getResponseStatusMessage();
		if (status == TransactionManager.TransactionStatus.STATUS_INACTIVE) {
			isPending = false;
			topControl = viewerEmpty;
		} else {
			isPending = (status == TransactionManager.TransactionStatus.STATUS_PENDING);
			topControl = builderPartCurr.getControl();
			builderPartCurr.setEditable(isPending);
			if (lastTransactionSerial != transactionInfo.getResponseTransactionSerial()) {
				lastTransactionSerial = transactionInfo.getResponseTransactionSerial();
				builderPartCurr.refresh();
			}
		}
		statusLabel.setText(statusMessage);
		if (viewerLayout.topControl != topControl) {
			viewerLayout.topControl = topControl;
			viewerControl.layout();
		}
		viewerMenu.setEnabled(isPending);
	}

	/**
	 * Notification from InterceptView when transactionInfo has been updated.
	 */
	public void notifyUpdate() {
		if (direction == TransactionDirection.DIRECTION_REQUEST) {
			updateViewerRequest();
		} else {
			updateViewerResponse();
		}
	}

}
