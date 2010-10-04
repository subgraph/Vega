package com.subgraph.vega.ui.scanner.alerts;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.scanner.model.INewScanAlertEvent;
import com.subgraph.vega.api.scanner.model.IScanAlert;
import com.subgraph.vega.api.scanner.model.IScanAlert.Severity;
import com.subgraph.vega.api.scanner.model.IScanModel;

public class ScanAlertContentProvider implements ITreeContentProvider {
	private final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	private final Map<Severity, List<IScanAlert>> alertMap;
	private final IEventHandler alertListener = createAlertListener();
	private IScanModel scanModel;
	private Viewer viewer;
	
	ScanAlertContentProvider() {
		alertMap = new LinkedHashMap<Severity, List<IScanAlert>>();
		for(Severity s: Severity.values())
			alertMap.put(s, new ArrayList<IScanAlert>());
	}
	
	@Override
	public void dispose() {		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(newInput instanceof IScanModel)
			setNewModelAndViewer((IScanModel) newInput, viewer);
		else
			setNullModel();		
	}

	private void resetAlertMap() {
		for(Severity s: Severity.values())
			alertMap.get(s).clear();
	}
	
	private void setNullModel() {
		resetAlertMap();
		scanModel = null;
	}
	
	private void setNewModelAndViewer(IScanModel newModel, Viewer newViewer) {
		if(newModel != scanModel) {
			if(scanModel != null)
				scanModel.removeAlertListener(alertListener);
			scanModel = newModel;
			resetAlertMap();
			scanModel.addAlertListenerAndPopulate(alertListener);
			this.viewer = newViewer;
		}
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		final List<Severity> roots = new ArrayList<Severity>();
		
		synchronized(alertMap) {
			for(Severity s: alertMap.keySet()) {
				if(!alertMap.get(s).isEmpty())
					roots.add(s);
			}
		}
		return roots.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof Severity) {
			Severity severity = (Severity) parentElement;
			synchronized(alertMap) {
				List<IScanAlert> alerts = alertMap.get(severity);
				return alerts.toArray();
			}
		}
		return EMPTY_OBJECT_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof IScanAlert) {
			IScanAlert alert = (IScanAlert) element;
			return alert.getSeverity();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof Severity) {
			Severity severity = (Severity) element;
			return !alertMap.get(severity).isEmpty();
		}
		return false;
	}
	
	private IEventHandler createAlertListener() {
		return new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof INewScanAlertEvent) {
					handleNewScanAlert((INewScanAlertEvent) event);
				}				
			}
		};
	}
	
	private void handleNewScanAlert(INewScanAlertEvent event) {
		synchronized(alertMap) {
			final IScanAlert alert = event.getAlert();
			final Severity severity = alert.getSeverity();
			alertMap.get(severity).add(alert);
		}
		refreshViewer();
	}
	
	private void refreshViewer() {
		if(viewer != null && !viewer.getControl().isDisposed()) {
			synchronized(viewer) {
				viewer.getControl().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						viewer.refresh();						
					}
				});
			}
		}
	}

}
