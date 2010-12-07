package com.subgraph.vega.ui.http.requestviewer;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.ui.httpeditor.HttpImage;
import com.subgraph.vega.ui.httpeditor.HttpRequestViewer;
import com.subgraph.vega.ui.httpeditor.RequestRenderer;

public class RequestResponseViewer {
	
	private final Composite parentComposite;
	private final RequestRenderer requestRenderer;
	private final CoolBar coolBar;
	private SashForm sashForm;
	private Composite rootComposite;
	private HttpRequestViewer requestViewer;
	private HttpRequestViewer responseViewer;
	private IRequestLogRecord currentRecord;
	
	public RequestResponseViewer(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		FormLayout layout = new FormLayout();
		parentComposite.setLayout(layout);
		requestRenderer = new RequestRenderer();
		coolBar = new CoolBar(parentComposite, SWT.NONE);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0);
		fd.right = new FormAttachment(100);
		fd.top = new FormAttachment(0);
		coolBar.setLayoutData(fd);
		
		final Combo combo = new Combo(coolBar, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo.add("Tabbed");
		combo.add("Horizontal");
		combo.add("Vertical");
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switch(combo.getSelectionIndex()) {
				case 0:
					setTabbedMode();
					break;
				case 1:
					setHorizontalSashMode();
					break;
				case 2:
					setVerticalSashMode();
					break;
				}
			}
		});
		addControlToCoolBar(combo, SWT.NONE);
		combo.select(0);
		setTabbedMode();
	}
	
	private CoolItem addControlToCoolBar(Control control, int coolItemStyle) {
		final CoolItem item = new CoolItem(coolBar, coolItemStyle);
		Point size = control.getSize();
		if(size.x == 0 && size.y == 0) {
			control.pack();
			size = control.getSize();
		}
		item.setControl(control);
		item.setSize(item.computeSize(100, size.y));
		return item;
	}
	
	public void setTabbedMode() {
		sashForm = null;
		recreateRootComposite();
	
		final TabFolder tabFolder = new TabFolder(rootComposite, SWT.TOP);
		
		final TabItem requestItem = new TabItem(tabFolder, SWT.NONE);
		requestItem.setText("Request");
		requestViewer = new HttpRequestViewer(tabFolder);
		requestItem.setControl(requestViewer.getControl());
		
		final TabItem responseItem = new TabItem(tabFolder, SWT.NONE);
		responseItem.setText("Response");
		responseViewer = new HttpRequestViewer(tabFolder);
		responseItem.setControl(responseViewer.getControl());
		parentComposite.layout();
		if(currentRecord != null)
			processCurrentTransaction();
		
	}
	
	private void recreateRootComposite() {
		if(rootComposite != null)
			rootComposite.dispose();
		rootComposite = new Composite(parentComposite, SWT.NONE);
		rootComposite.setLayout(new FillLayout());
		final FormData fd = new FormData();
		fd.left = new FormAttachment(0);
		fd.right = new FormAttachment(100);
		fd.top = new FormAttachment(coolBar);
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
		if(sashForm != null) {
			if(sashForm.getOrientation() != mode)
				sashForm.setOrientation(mode);
			return;
		}
		recreateRootComposite();
		sashForm = new SashForm(rootComposite, mode);
		requestViewer = new HttpRequestViewer(sashForm);
		responseViewer = new HttpRequestViewer(sashForm);
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
		requestViewer.setContent(requestRenderer.renderRequestText(currentRecord));
		if(currentRecord.getResponse().getEntity() != null) {
			HttpEntity e = currentRecord.getResponse().getEntity();
			HttpImage img = createPossibleResponseImage(e, getRequestFilename(currentRecord));
			responseViewer.setContent(requestRenderer.renderResponseText(currentRecord), img);
		} else {
			responseViewer.setContent(requestRenderer.renderResponseText(currentRecord));
		}
	}
	
	private HttpImage createPossibleResponseImage(HttpEntity entity, String filename) {
		final String mime = getMimeType(entity);
		if(mime == null)
			return null;
		if(!isImageMimeType(mime))
			return null;
		
		try {
			final ImageData id = new ImageData(entity.getContent());
			return new HttpImage(Display.getDefault(), id, filename, (int) entity.getContentLength());
		} catch(SWTException e) {
			System.out.println("Image is invalid for type "+ mime +" : "+ e.getMessage());
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private String getMimeType(HttpEntity entity) {
		if(entity.getContentType() != null)
			return entity.getContentType().getValue();
		else
			return null;		
	}
	
	private boolean isImageMimeType(String type) {
		final String[] imageTypes = new String[] {"image/jpeg", "image/gif", "image/png "};
		for(String t: imageTypes)
			if(type.equals(t))
				return true;
		return false;	
	}
	
	private String getRequestFilename(IRequestLogRecord record) {
		final String uri = record.getRequest().getRequestLine().getUri();
		final String[] parts = uri.split("\\/");
		if(parts.length == 0)
			return "";
		else {
			final String end = parts[parts.length - 1];
			final String[] parts2 = end.split("\\?");
			if(parts2.length == 0)
				return "";
			else 
				return parts2[0];
		}
	}
	
	private void clearViewers() {
		requestViewer.clearContent();
		responseViewer.clearContent();
	}

}