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
package com.subgraph.vega.ui.http.requestviewer;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public class HttpViewLabelProvider extends LabelProvider implements ITableLabelProvider {

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
		URI uri;
		try {
			uri = new URI(record.getRequest().getRequestLine().getUri());
			
		} catch (URISyntaxException e) {
			return null;
		}
		switch(columnIndex) {
		case 0:
			return Long.toString(record.getRequestId());
		case 1:
			return record.getHttpHost().toURI();
		case 2:
			return record.getRequest().getRequestLine().getMethod();
		case 3:
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
}
