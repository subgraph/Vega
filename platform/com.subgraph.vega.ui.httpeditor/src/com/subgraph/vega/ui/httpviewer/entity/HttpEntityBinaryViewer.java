package com.subgraph.vega.ui.httpviewer.entity;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.subgraph.vega.ui.hexeditor.HexEditControl;

public class HttpEntityBinaryViewer extends Composite {
	private final HexEditControl hexedit;
	
	public HttpEntityBinaryViewer(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		hexedit = new HexEditControl(this);
	}
		
	void setInput(byte[] data) {
		hexedit.setInput(data);
	}
	
	void clear() {
		hexedit.setInput(new byte[0]);
	}
	
	boolean isContentDirty() {
		return hexedit.isContentDirty();
	}
	
	HttpEntity getEntityContent() {
		final byte[] content = hexedit.getContent();
		if(content == null) {
			return null;
		} else {
			return new ByteArrayEntity(content);
		}
	}
}
