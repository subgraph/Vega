package com.subgraph.vega.ui.http.intercept;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

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
import com.subgraph.vega.ui.http.ErrorDisplay;
import com.subgraph.vega.ui.http.builder.BuilderParseException;
import com.subgraph.vega.ui.http.builder.HeaderEditor;
import com.subgraph.vega.ui.http.builder.IHttpBuilderPart;
import com.subgraph.vega.ui.http.builder.RequestEditor;
import com.subgraph.vega.ui.http.builder.ResponseMessageEditor;
import com.subgraph.vega.ui.http.dialogs.ConfigDialogCreator;
import com.subgraph.vega.ui.http.intercept.config.ConfigureInterceptionContent;

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

	private Composite viewerControl;
	private StackLayout viewerLayout;
	private Menu viewerMenu;
	private boolean hasContent;
	private IHttpBuilderPart builderPartCurr;
	private Window configDialog;
	
	public TransactionViewer(Composite parent, IModel model, TransactionManager manager, TransactionDirection direction) {
		super(parent, SWT.NONE);
		this.model = model;
		this.direction = direction;
		this.manager = manager;
		setLayout(createLayout());
		createStatusLabel();
		createToolbar();
		final Group viewerGroup = new Group(this, SWT.NONE);
		viewerGroup.setLayout(new FillLayout());
		viewerGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		if(direction == TransactionDirection.DIRECTION_REQUEST) { 
			createViewersRequest(viewerGroup);
			manager.setRequestViewer(this);
		} else {
			createViewersResponse(viewerGroup);
			manager.setResponseViewer(this);
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
		final ToolBar toolBar = new ToolBar(this, SWT.FLAT);

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
				try {
					builderPartCurr.processContents();
				} catch (Exception ex) {
					ErrorDisplay.displayExceptionError(getShell(), ex);
					return;
				}
				if(direction == TransactionDirection.DIRECTION_REQUEST) {
					try {
						manager.forwardRequest();
					} catch (Exception ex) {
						ErrorDisplay.displayExceptionError(getShell(), ex);
						return;
					}
				} else {
					try {
						manager.forwardResponse();
					} catch (Exception ex) {
						ErrorDisplay.displayExceptionError(getShell(), ex);
						return;
					}
				}
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

	private void createViewersRequest(final Composite parent) {
		viewerControl = new Composite(parent, SWT.NONE);
		viewerLayout = new StackLayout();
		viewerControl.setLayout(viewerLayout);
		
		IHttpRequestBuilder requestBuilder = manager.getRequestBuilder();
		final SelectionListener listener = createSelectionListenerMenuItem();

		RequestEditor requestEditor = new RequestEditor(viewerControl, requestBuilder);
	    MenuItem menuItem = new MenuItem(viewerMenu, SWT.NONE);
	    menuItem.setText("Request");
	    menuItem.setData(requestEditor);
	    menuItem.addSelectionListener(listener);
	    viewerLayout.topControl = requestEditor;
		builderPartCurr = requestEditor;

	    HeaderEditor headerEditor = new HeaderEditor(viewerControl, requestBuilder, 0);
	    menuItem = new MenuItem(viewerMenu, SWT.NONE);
	    menuItem.setText("Headers");
	    menuItem.setData(headerEditor);
	    menuItem.addSelectionListener(listener);

	    builderPartCurr.setEditable(false);
	}

	private void createViewersResponse(final Composite parent) {
		viewerControl = new Composite(parent, SWT.NONE);
		viewerLayout = new StackLayout();
		viewerControl.setLayout(viewerLayout);

		IHttpResponseBuilder responseBuilder = manager.getResponseBuilder();
		final SelectionListener listener = createSelectionListenerMenuItem();

		ResponseMessageEditor responseEditor = new ResponseMessageEditor(viewerControl, responseBuilder);
		responseEditor.setEditable(false);
	    MenuItem menuItem = new MenuItem(viewerMenu, SWT.NONE);
	    menuItem.setText("Response");
	    menuItem.setData(responseEditor);
	    menuItem.addSelectionListener(listener);
	    viewerLayout.topControl = responseEditor;
		builderPartCurr = responseEditor;

	    HeaderEditor headerEditor = new HeaderEditor(viewerControl, responseBuilder, 0);
	    headerEditor.setEditable(false);
	    menuItem = new MenuItem(viewerMenu, SWT.NONE);
	    menuItem.setText("Headers");
	    menuItem.setData(headerEditor);
	    menuItem.addSelectionListener(listener);

	    builderPartCurr.setEditable(false);
	}
	
	private SelectionAdapter createSelectionListenerMenuItem() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final MenuItem menuItem = (MenuItem) event.widget;
				final IHttpBuilderPart builderPart = (IHttpBuilderPart) menuItem.getData();
				if (hasContent != false) {
					try {
						builderPartCurr.processContents();
					} catch (Exception e) {
						ErrorDisplay.displayExceptionError(getShell(), e);
						return;
					}
				}
				builderPartCurr = builderPart;
				builderPartCurr.setEditable(hasContent);
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

	public void notifyUpdate(final String statusMessage, final boolean hasContent) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				doUpdate(statusMessage, hasContent);
			}
		});
	}
	
	private void doUpdate(String statusMessage, boolean hasContent) {
		if (isDisposed()) {
			return;
		}

		this.hasContent = hasContent;
		statusLabel.setText(statusMessage);
		forwardButton.setEnabled(hasContent);
		dropButton.setEnabled(hasContent);
		if (builderPartCurr != null) {
			builderPartCurr.setEditable(hasContent);
			builderPartCurr.refresh();
		}
	}

}
