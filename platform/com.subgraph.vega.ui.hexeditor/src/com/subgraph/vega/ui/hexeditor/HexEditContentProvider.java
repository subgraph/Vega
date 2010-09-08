package com.subgraph.vega.ui.hexeditor;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

public class HexEditContentProvider implements ILazyContentProvider {

	private final TableViewer viewer;
	private HexEditModel model;
	
	HexEditContentProvider(TableViewer viewer) {
		this.viewer = viewer;
	}
	
	void setModel(HexEditModel model) {
		this.model = model;
	}
	
	@Override
	public void updateElement(int index) {
		if(model == null)
			return;
		final HexEditModelItem item = model.getItemForLine(index);
		if(item != null)
			viewer.replace(item, index);		
	}

	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {	
	}

}