package com.subgraph.vega.ui.http.requestviewer;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.subgraph.vega.api.requestlog.IRequestLogRecord;

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
			return record.getHttpHost().getHostName();
		case 1:
			return record.getRequest().getRequestLine().getMethod();
		case 2:
			return uri.getPath();
		case 3:
			return new Integer(record.getResponse().getStatusLine().getStatusCode()).toString();
		case 4:
			Header lengthHeader = record.getResponse().getFirstHeader("Content-Length");
			return (lengthHeader == null)?"":lengthHeader.getValue();
		}
		
		
		return null;
	}
	
	

}