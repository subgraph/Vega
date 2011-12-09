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
package com.subgraph.vega.internal.ui.http.requestlogviewer;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.api.model.tags.ITag;

public class HttpViewLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(!(element instanceof IRequestLogRecord))
			return null;
		final IRequestLogRecord record = (IRequestLogRecord) element;
		switch(columnIndex) {
		case 0:
			return Long.toString(record.getRequestId());
		case 1:
			return record.getHttpHost().toURI();
		case 2:
			return record.getRequest().getRequestLine().getMethod();
		case 3:
			URI uri;
			try {
				uri = new URI(record.getRequest().getRequestLine().getUri());
			} catch (URISyntaxException e) {
				return null;
			}
			if(uri.getRawQuery() != null)
				return uri.getRawPath() + "?" + uri.getRawQuery();
			else
				return uri.getRawPath();
		case 4:
			return Integer.valueOf(record.getResponse().getStatusLine().getStatusCode()).toString();
		case 5:
			return getResponseLength(record.getResponse());
		case 6:
			return Long.toString(record.getRequestMilliseconds());
		case 7:
			return Integer.toString(record.getTagCount());
		}
		return null;
	}
	
	public String getResponseLength(HttpResponse response) {
		final Header lengthHeader = response.getFirstHeader("Content-Length");
		if(lengthHeader != null)
			return lengthHeader.getValue();
		
		if(response.getEntity() == null)
			return "";
		
		return Long.toString(response.getEntity().getContentLength());
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		IRequestLogRecord record = (IRequestLogRecord) element;
		if (record.getTagCount() != 0) {
			int color = record.getTag(0).getNameColor();
			return new Color(Display.getCurrent(), (color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff);
		}
		return null;
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		IRequestLogRecord record = (IRequestLogRecord) element;
		if (record.getTagCount() != 0) {
			int color = record.getTag(0).getRowColor();
			return new Color(Display.getCurrent(), (color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff);
		}
		return null;
	}
	
}
