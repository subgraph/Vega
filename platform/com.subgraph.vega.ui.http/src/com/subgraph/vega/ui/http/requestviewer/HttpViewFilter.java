package com.subgraph.vega.ui.http.requestviewer;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.api.model.web.IWebGetTarget;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.requestlog.IRequestLogRecord;

public class HttpViewFilter extends ViewerFilter {

	private IWebEntity filterEntity;
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof IRequestLogRecord)
			return selectTransaction((IRequestLogRecord) element);
		else
			return false;
	}
	
	void setFilterEntity(IWebEntity e) {
		filterEntity = e;
	}
	
	private boolean selectTransaction(IRequestLogRecord record) {
		if(filterEntity == null)
			return true;
		else if(filterEntity instanceof IWebHost)
			return filterWithHost(record, (IWebHost) filterEntity);
		else if(filterEntity instanceof IWebPath)
			return filterWithPath(record, (IWebPath) filterEntity);
		else if(filterEntity instanceof IWebGetTarget) 
			return filterWithGetTarget(record, (IWebGetTarget) filterEntity);
		else
			return false;
	}
	
	private boolean filterWithHost(IRequestLogRecord record, IWebHost host) {
		return record.getHttpHost().getHostName().equals(host.getHostname());
	}
	
	private boolean filterWithPath(IRequestLogRecord record, IWebPath path) { 
		try {
			final URI uri = new URI(record.getRequest().getRequestLine().getUri());
			final String hostname = record.getHttpHost().getHostName();
			return (hostname.equals(path.getHost().getHostname()) &&
					uri.getPath().startsWith(path.getFullPath()));
		} catch (URISyntaxException e) {
			return false;
		}
	}
	
	private boolean filterWithGetTarget(IRequestLogRecord record, IWebGetTarget get) {
		try {
			final URI uri = new URI(record.getRequest().getRequestLine().getUri());
			final String hostname = get.getPath().getHost().getHostname();
			final String fullPath = get.getPath().getFullPath();
			return(hostname.equals(record.getHttpHost().getHostName()) && uri.getPath().startsWith(fullPath));
		} catch (URISyntaxException e) {
			return false;
		}
	}
}
