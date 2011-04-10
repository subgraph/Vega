package com.subgraph.vega.ui.http.intercept;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.api.http.proxy.IProxyTransaction.TransactionDirection;

import com.subgraph.vega.ui.http.intercept.config.ConfigureInterceptionPanel;
import com.subgraph.vega.ui.httpeditor.HttpRequestViewer;

public class TransactionViewer extends Composite {
	private static final Image IMAGE_CONFIGURE = Activator.getImageDescriptor("icons/filter.gif").createImage();
	private static final Image IMAGE_FORWARD = Activator.getImageDescriptor("icons/start_16x16.png").createImage();
	private static final Image IMAGE_DROP = Activator.getImageDescriptor("icons/stop_16x16.png").createImage();
	
	private final IModel model;
	private final TransactionDirection direction;
	private final TransactionManager manager;
	private Label statusLabel;
	private ToolItem forwardButton;
	private ToolItem dropButton;
	private ToolItem configureButton;
	private HttpRequestViewer httpRequestViewer;
	
	public TransactionViewer(Composite parent, IModel model, TransactionManager manager, TransactionDirection direction) {
		super(parent, SWT.NONE);
		this.model = model;
		this.direction = direction;
		this.manager = manager;
		if(direction == TransactionDirection.DIRECTION_REQUEST) 
			manager.setRequestViewer(this);
		else
			manager.setResponseViewer(this);
		
		setLayout(createLayout());
		createStatusLabel();
		createToolbar();
		
		final Group viewerGroup = new Group(this, SWT.NONE);
		viewerGroup.setLayout(new FillLayout());
		viewerGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
		httpRequestViewer = new HttpRequestViewer(viewerGroup);
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
		final ToolBar toolBar = new ToolBar(this, SWT.FLAT);
		configureButton = createToolbarButton(toolBar, IMAGE_CONFIGURE, "Configure interception rules", createConfigureButtonListener());
		forwardButton = createToolbarButton(toolBar, IMAGE_FORWARD, "Forward", createForwordButtonListener());
		dropButton = createToolbarButton(toolBar, IMAGE_DROP, "Drop", createDropButtonListener());
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
	
	private SelectionListener createForwordButtonListener() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(direction == TransactionDirection.DIRECTION_REQUEST)
					manager.forwardRequest();
				else
					manager.forwardResponse();
			}
			
		};
	}
	
	private SelectionListener createDropButtonListener() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(direction == TransactionDirection.DIRECTION_REQUEST)
					manager.dropRequest();
				else
					manager.dropResponse();
			}
		};
	}
	
	
	private void doConfigure() {
		int x = configureButton.getBounds().x;
		int y = configureButton.getBounds().y + configureButton.getBounds().height;
		Point p = configureButton.getDisplay().map(configureButton.getParent(), null, x, y);
		final ConfigureInterceptionPanel configPanel = new ConfigureInterceptionPanel(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), p, model, direction);
		configPanel.open();
	}
	
	void setStatus(String statusMessage, boolean buttonState) {
		setStatus(statusMessage, buttonState, null);
	}

	void setStatus(final String statusMessage, final boolean buttonState, final String content) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				doSetStatus(statusMessage, buttonState, content);
			}
		});
	}
	
	private void doSetStatus(String statusMessage, boolean buttonState, String httpContent) {
		if(isDisposed())
			return;
		statusLabel.setText(statusMessage);
		forwardButton.setEnabled(buttonState);
		dropButton.setEnabled(buttonState);
		if(httpContent == null)
			httpRequestViewer.clearContent();
		else
			httpRequestViewer.setContent(httpContent);
	}
}
