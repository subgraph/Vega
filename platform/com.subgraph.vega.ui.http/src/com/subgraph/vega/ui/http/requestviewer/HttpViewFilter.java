package com.subgraph.vega.ui.http.requestviewer;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.subgraph.vega.api.http.conditions.IHttpConditionSet;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.ui.http.Activator;

public class HttpViewFilter extends ViewerFilter {
	private TableViewer tableViewer;
	private IWebEntity filterEntity;
	private IHttpConditionSet conditionSet;
	
	public HttpViewFilter(TableViewer viewer) {
		tableViewer = viewer;
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		//conditionSet = new HttpConditionSet();
		//conditionSet.unserialize(preferenceStore.getString(com.subgraph.vega.ui.http.requestfilterpreferencepage.PreferenceConstants.P_FILTER));
		preferenceStore.addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				/*
				if (event.getProperty() == com.subgraph.vega.ui.http.requestfilterpreferencepage.PreferenceConstants.P_FILTER) {
					conditionSet.unserialize(event.getNewValue().toString());
					tableViewer.refresh();
				}
				*/
			}
		});

	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IRequestLogRecord) {
			return selectTransaction((IRequestLogRecord) element);
		} else {
			return false;
		}
	}
	
	void setFilterEntity(IWebEntity e) {
		filterEntity = e;
		tableViewer.refresh();
	}
	
	private boolean selectTransaction(IRequestLogRecord record) {
		if (filterEntity != null) {
			if (filterEntity instanceof IWebHost) {
				if (filterWithHost(record, (IWebHost) filterEntity) == false) {
					return false;
				}
			} else if (filterEntity instanceof IWebPath) {
				if (filterWithPath(record, (IWebPath) filterEntity) == false) {
					return false;
				}
			}
//			else if (filterEntity instanceof IWebGetTarget) {
//				if (filterWithGetTarget(record, (IWebGetTarget) filterEntity) == false) {
//					return false;
//				}
//			}
		}

		if (conditionSet.getConditionCnt() != 0) {
			return conditionSet.test(record.getRequest(), record.getResponse());
		}
		return true;
	}
	
	private boolean filterWithHost(IRequestLogRecord record, IWebHost host) {
		return record.getHttpHost().getHostName().equals(host.getHostname());
	}
	
	private boolean filterWithPath(IRequestLogRecord record, IWebPath path) { 
		try {
			final URI uri = new URI(record.getRequest().getRequestLine().getUri());
			final String hostname = record.getHttpHost().getHostName();
			return (hostname.equals(path.getMountPoint().getWebHost().getHostname()) &&
					uri.getPath().startsWith(path.getFullPath()));
		} catch (URISyntaxException e) {
			return false;
		}
	}

	/*
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
	*/
}
