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
package com.subgraph.vega.ui.http.requestlogviewer;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.httpeditor.HttpMessageEditor;
import com.subgraph.vega.ui.util.images.ImageCache;

public class RequestResponseViewer extends Composite {
	private final static String VERTICAL_ICON = "icons/vertical.png";
	private final static String HORIZONTAL_ICON = "icons/horizontal.png";
	private final static String TABBED_ICON = "icons/tabbed.png";
	private final static String UP_ICON = "icons/up.png";
	private final static String DOWN_ICON = "icons/down.png";
	private final static String CONFIG_ICON = "icons/configure.png";
	
	private IEventHandler workspaceListener;
	private final SashForm parentForm;
	private final ImageCache imageCache;
	private final Menu optionsMenu;
	private final Composite toolbarComposite;
	private ToolItem hideItem;
	private SashForm sashForm;
	private TabFolder tabFolder;
	private Composite rootComposite;
	private HttpMessageEditor requestViewer;
	private HttpMessageEditor responseViewer;
	private IRequestLogRecord currentRecord;
	
	private boolean displayImages = true;
	private boolean displayImagesAsHex = false;
	private boolean urlDecodeState = false;
	private boolean hideState = false;
	private boolean wordWrapLines = false;
	
	public RequestResponseViewer(SashForm parentForm) {
		super(parentForm, SWT.NONE);
		setLayout(new FormLayout());
		this.parentForm = parentForm;
		imageCache = new ImageCache(Activator.PLUGIN_ID);
		optionsMenu = createOptionsMenu(parentForm.getShell());
		toolbarComposite = createToolbarComposite(this);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0);
		fd.right = new FormAttachment(100);
		fd.top = new FormAttachment(0);
		toolbarComposite.setLayoutData(fd);
		setTabbedMode();
		workspaceListener = new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if (event instanceof WorkspaceCloseEvent || event instanceof WorkspaceResetEvent) {
					handleWorkspaceCloseOrReset();
				}
			}
		};
		Activator.getDefault().getModel().addWorkspaceListener(workspaceListener);

		createDisposeListener();
	}

	private void createDisposeListener() {
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				Activator.getDefault().getModel().removeWorkspaceListener(workspaceListener);
				removeDisposeListener(this);
			}
		});
	}

	private void handleWorkspaceCloseOrReset() {
		if (!isDisposed()) {
			clearViewers();
		}
	}

	private Composite createToolbarComposite(Composite parent) {
		final Composite c = new Composite(this, SWT.NONE);
		final GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		c.setLayout(layout);
		Composite tb = createLayoutToolBar(c);
		tb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tb = createConfigureToolBar(c);
		tb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		return c;
	}
		
	private ToolBar createLayoutToolBar(Composite parent) {
		ToolBar tb = new ToolBar(parent, SWT.NONE);
		ToolItem tabbed = new ToolItem(tb, SWT.RADIO);
		tabbed.setImage(imageCache.get(TABBED_ICON));
		tabbed.setToolTipText("Tabbed Request/Response layout");
		tabbed.setSelection(true);
		tabbed.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setTabbedMode();
			}			
		});
		ToolItem horiz = new ToolItem(tb, SWT.RADIO);
		horiz.setImage(imageCache.get(HORIZONTAL_ICON));
		horiz.setToolTipText("Horizontal Request/Response layout");
		horiz.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setHorizontalSashMode();
			}			
		});
		ToolItem vert = new ToolItem(tb, SWT.RADIO);
		vert.setImage(imageCache.get(VERTICAL_ICON));
		vert.setToolTipText("Vertical Request/Response layout");
		vert.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setVerticalSashMode();
			}				
		});
		tb.pack();
		return tb;
	}
	
	private ToolBar createConfigureToolBar(Composite parent) {
		final ToolBar tb = new ToolBar(parent, SWT.NONE);

		final ToolItem config = new ToolItem(tb, SWT.DROP_DOWN);
		config.setImage(imageCache.get(CONFIG_ICON));
		config.setToolTipText("Options");
		config.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Rectangle r = config.getBounds();
				final Point p = new Point(r.x, r.y + r.height);
				optionsMenu.setLocation(tb.getDisplay().map(tb, null, p));
				optionsMenu.setVisible(true);
			}
		});

		hideItem = new ToolItem(tb, SWT.PUSH);
		hideItem.setImage(imageCache.get(UP_ICON));
		hideItem.setToolTipText("Hide Request Table");
		hideItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toggleHideState();
			}						
		});

		tb.pack();
		return tb;
	}
	
	private Menu createOptionsMenu(Shell shell) {
		final Menu menu = new Menu(shell, SWT.POP_UP);
		final MenuItem displayImagesItem = new MenuItem(menu, SWT.CHECK);
		displayImagesItem.setText("Display Images");
		displayImagesItem.setSelection(displayImages);
		
		final MenuItem imagesAsHexItem = new MenuItem(menu, SWT.CHECK);
		imagesAsHexItem.setText("Display Images with Hex Viewer");
		
		final MenuItem decodeItem = new MenuItem(menu, SWT.CHECK);
		decodeItem.setText("Remove URL encoding");
		
		final MenuItem wordwrapItem = new MenuItem(menu, SWT.CHECK);
		wordwrapItem.setText("Word wrap lines");
		
		displayImagesItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean value = displayImagesItem.getSelection();
				imagesAsHexItem.setEnabled(value);
				setDisplayImageState(value);
				displayImages = value;
			}
		});
		
		imagesAsHexItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean value = imagesAsHexItem.getSelection();
				setDisplayImagesAsHexState(value);
				displayImagesAsHex = value;
			}
		});
		
		decodeItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean value = decodeItem.getSelection();
				setUrlDecodeState(value);
				urlDecodeState  = value;
			}
		});
		
		wordwrapItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean value = wordwrapItem.getSelection();
				setWordwrapState(value);
				wordWrapLines = value;
			}
		});
		
		return menu;
	}
	
	private void setDisplayImageState(boolean value) {
		requestViewer.setDisplayImages(value);
		responseViewer.setDisplayImages(value);
	}
	
	private void setDisplayImagesAsHexState(boolean value) {
		requestViewer.setDisplayImagesAsHex(value);
		responseViewer.setDisplayImagesAsHex(value);
	}
	
	private void setUrlDecodeState(boolean value) {
		requestViewer.setDecodeUrlEncoding(value);
		responseViewer.setDecodeUrlEncoding(value);
	}

	private void setWordwrapState(boolean value) {
		requestViewer.setWordwrapLines(value);
		responseViewer.setWordwrapLines(value);
	}

	private void toggleHideState() {
		if(hideState) {
			hideState = false;
			hideItem.setImage(imageCache.get(UP_ICON));
			hideItem.setToolTipText("Hide Request Table");
			parentForm.setMaximizedControl(null);
		} else {
			hideState = true;
			hideItem.setToolTipText("Show Request Table");
			hideItem.setImage(imageCache.get(DOWN_ICON));
			parentForm.setMaximizedControl(this);
		}	
	}
	
	public void setTabbedMode() {
		sashForm = null;
		recreateRootComposite();
	
		tabFolder = new TabFolder(rootComposite, SWT.TOP);
		createMessageViewers(tabFolder);

		final TabItem requestItem = new TabItem(tabFolder, SWT.NONE);
		requestItem.setText("Request");
		requestItem.setControl(requestViewer);
		
		final TabItem responseItem = new TabItem(tabFolder, SWT.NONE);
		responseItem.setText("Response");
		responseItem.setControl(responseViewer);

		this.layout();
		if(currentRecord != null)
			processCurrentTransaction();
		setDisplayResponse();
	}
	
	private void createMessageViewers(Composite parent) {
		requestViewer = new HttpMessageEditor(parent);
		requestViewer.setEditable(false);
		responseViewer = new HttpMessageEditor(parent);
		responseViewer.setEditable(false);
		setDisplayImageState(displayImages);
		setDisplayImagesAsHexState(displayImagesAsHex);
		setUrlDecodeState(urlDecodeState);
		setWordwrapState(wordWrapLines);
	}

	public void setDisplayResponse() {
		if(tabFolder != null)
			tabFolder.setSelection(1);
		
	}
	private void recreateRootComposite() {
		if(rootComposite != null)
			rootComposite.dispose();
		rootComposite = new Composite(this, SWT.NONE);
		rootComposite.setLayout(new FillLayout());
		
		final FormData fd = new FormData();
		fd.left = new FormAttachment(0);
		fd.right = new FormAttachment(100);
		fd.top = new FormAttachment(toolbarComposite);
		fd.bottom = new FormAttachment(100);
		rootComposite.setLayoutData(fd);	
	}

	public void setVerticalSashMode() {
		configureSashMode(SWT.VERTICAL);
	}
	
	public void setHorizontalSashMode() {
		configureSashMode(SWT.HORIZONTAL);
	}

	private void configureSashMode(int mode) {
		tabFolder = null;
		if(sashForm != null) {
			if(sashForm.getOrientation() != mode)
				sashForm.setOrientation(mode);
			return;
		}
		recreateRootComposite();
		sashForm = new SashForm(rootComposite, mode);

		createMessageViewers(sashForm);

		sashForm.setWeights(new int[] {50, 50});
		this.layout();
		processCurrentTransaction();
	}
	
	public void setCurrentRecord(IRequestLogRecord record) {
		if (currentRecord != record) {
			currentRecord = record;
			processCurrentTransaction();
		}
	}
	
	private void addResponseHighlights(Collection<IScanAlert> alerts) {
		for(IScanAlert a: alerts) {
			responseViewer.addAlertHighlights(a.getHighlights());
		}
		responseViewer.displayAlertHighlights();
		
	}
	private void processCurrentTransaction() {
		if(currentRecord == null) {
			clearViewers();
			return;
		}
		requestViewer.displayHttpRequest(currentRecord.getRequest());
		responseViewer.displayHttpResponse(currentRecord.getResponse());
		IWorkspace workspace = Activator.getDefault().getModel().getCurrentWorkspace();
		if(workspace != null) {
			final Collection<IScanAlert> alerts = workspace.getScanAlertRepository().getAlertsByRequestId(currentRecord.getRequestId());
			if(!alerts.isEmpty()) {
				addResponseHighlights(alerts);
			}
		}
	}

	private void clearViewers() {
		currentRecord = null;
		requestViewer.clearContent();
		responseViewer.clearContent();
	}

}
