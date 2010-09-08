package com.subgraph.vega.ui.web.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.model.web.IWebModelChangeEvent;
import com.subgraph.vega.ui.tree.web.WebModelAdapter;

public class WebsiteContentProvider implements ITreeContentProvider {
	private final Object[] NULL_OB = new Object[0];
	private IWebModel model;
	private Viewer viewer;
	private final IEventHandler modelListener = createModelListener();
	private final List<IWebHost> webHosts = new ArrayList<IWebHost>();
	private final WebModelAdapter treeAdapter = new WebModelAdapter();
	
	public Object[] getChildren(Object parentElement) {
		return treeAdapter.getChildren(parentElement);
	}
	
	public Object getParent(Object element) {
		return treeAdapter.getParent(element);
	}
	
	public boolean hasChildren(Object element) {
		return treeAdapter.hasChildren(element);
	}
	
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof IWebModel) {
			return webHosts.toArray(NULL_OB);
		} else {
			return treeAdapter.getChildren(inputElement);
		}
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(newInput == null)
			setNullModel();
		else if(newInput instanceof IWebModel)
			setNewModelAndViewer((IWebModel) newInput, viewer);
	}
	
	private void setNullModel() {
		webHosts.clear();
		model = null;
	}
	
	private void setNewModelAndViewer(IWebModel newModel, Viewer newViewer) {
		if(newModel != model) {
			if(model != null) 
				model.removeChangeListener(modelListener);
			model = newModel;
			webHosts.clear();
			model.addChangeListenerAndPopulate(modelListener);
			this.viewer = newViewer;
		}
	}
	
	public void dispose() { 
	}
	
	private IEventHandler createModelListener() {
		return new IEventHandler() {
			public void handleEvent(IEvent event) {
				if(event instanceof IWebModelChangeEvent)
					handleModelChange((IWebModelChangeEvent) event);
			}
		};
	}
	
	private void handleModelChange(IWebModelChangeEvent event) {
		if(event.isEntityAddEvent()) {
			IWebEntity e = event.getEntity();
			if(e instanceof IWebHost)
				addWebHostEntity((IWebHost) e);
			refreshViewer();
		}
	}
	
	private void addWebHostEntity(IWebHost host) {
		webHosts.add(host);
		refreshViewer();
	}
	
	private void refreshViewer() {
		if(viewer != null && !viewer.getControl().isDisposed()) {
			synchronized (viewer) {
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
