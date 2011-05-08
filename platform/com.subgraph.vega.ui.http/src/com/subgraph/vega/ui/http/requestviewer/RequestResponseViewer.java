package com.subgraph.vega.ui.http.requestviewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
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

import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.httpviewer.HttpMessageViewer;

public class RequestResponseViewer {
	private final static String VERTICAL_ICON = "icons/vertical.png";
	private final static String HORIZONTAL_ICON = "icons/horizontal.png";
	private final static String TABBED_ICON = "icons/tabbed.png";
	private final static String UP_ICON = "icons/up.png";
	private final static String DOWN_ICON = "icons/down.png";
	private final static String CONFIG_ICON = "icons/configure.png";
	
	private final ImageCache imageCache;
	private final SashForm parentForm;
	private final Composite parentComposite;
	private final Menu optionsMenu;
	private final Composite toolbarComposite;
	private ToolItem hideItem;
	private SashForm sashForm;
	private TabFolder tabFolder;
	private Composite rootComposite;
	private HttpMessageViewer requestViewer;
	private HttpMessageViewer responseViewer;
	private IRequestLogRecord currentRecord;
	
	private boolean hideState = false;
	
	public RequestResponseViewer(SashForm parentForm) {
		imageCache = new ImageCache(Activator.PLUGIN_ID);
		this.parentForm = parentForm;
		parentComposite = new Composite(parentForm, SWT.NONE);
		parentComposite.setLayout(new FormLayout());
		optionsMenu = createOptionsMenu(parentForm.getShell());
		toolbarComposite = createToolbarComposite(parentComposite);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0);
		fd.right = new FormAttachment(100);
		fd.top = new FormAttachment(0);
		toolbarComposite.setLayoutData(fd);
		setTabbedMode();
	}
	
	private Composite createToolbarComposite(Composite parent) {
		final Composite c = new Composite(parentComposite, SWT.NONE);
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
		final MenuItem displayImages = new MenuItem(menu, SWT.CHECK);
		displayImages.setText("Display Images");
		displayImages.setSelection(true);
		
		final MenuItem imagesAsHex = new MenuItem(menu, SWT.CHECK);
		imagesAsHex.setText("Display Images with Hex Viewer");
		
		final MenuItem decode = new MenuItem(menu, SWT.CHECK);
		decode.setText("Remove URL encoding");
		
		displayImages.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean value = displayImages.getSelection();
				imagesAsHex.setEnabled(value);
				setDisplayImageState(value);
			}
		});
		
		imagesAsHex.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDisplayImagesAsHexState(imagesAsHex.getSelection());
			}
		});
		
		decode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setUrlDecodeState(decode.getSelection());
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
			parentForm.setMaximizedControl(parentComposite);
		}	
	}
	
	public void setTabbedMode() {
		sashForm = null;
		recreateRootComposite();
	
		tabFolder = new TabFolder(rootComposite, SWT.TOP);
		
		final TabItem requestItem = new TabItem(tabFolder, SWT.NONE);
		requestItem.setText("Request");
		requestViewer = new HttpMessageViewer(tabFolder);
		requestViewer.setEditable(false);
		requestItem.setControl(requestViewer);
		
		final TabItem responseItem = new TabItem(tabFolder, SWT.NONE);
		responseItem.setText("Response");
		responseViewer = new HttpMessageViewer(tabFolder);
		responseViewer.setEditable(false);
		responseItem.setControl(responseViewer);
		parentComposite.layout();
		if(currentRecord != null)
			processCurrentTransaction();
		setDisplayResponse();
	}
	
	public void setDisplayResponse() {
		if(tabFolder != null)
			tabFolder.setSelection(1);
		
	}
	private void recreateRootComposite() {
		if(rootComposite != null)
			rootComposite.dispose();
		rootComposite = new Composite(parentComposite, SWT.NONE);
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
		requestViewer = new HttpMessageViewer(sashForm);
		requestViewer.setEditable(false);
		responseViewer = new HttpMessageViewer(sashForm);
		responseViewer.setEditable(false);
		sashForm.setWeights(new int[] {50, 50});
		parentComposite.layout();
		processCurrentTransaction();
	}
	
	public void setCurrentRecord(IRequestLogRecord record) {
		currentRecord = record;
		processCurrentTransaction();
	}
	
	private void processCurrentTransaction() {
		if(currentRecord == null) {
			clearViewers();
			return;
		}
		requestViewer.displayHttpRequest(currentRecord.getRequest());
		responseViewer.displayHttpResponse(currentRecord.getResponse());
	}

	private void clearViewers() {
		requestViewer.clearContent();
		responseViewer.clearContent();
	}

}